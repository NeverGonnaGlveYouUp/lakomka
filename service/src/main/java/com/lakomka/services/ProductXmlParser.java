package com.lakomka.services;

import com.lakomka.models.product.Product;
import com.lakomka.models.product.ProductGroup;
import com.lakomka.repository.product.ProductGroupRepository;
import com.lakomka.repository.product.ProductRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductXmlParser {

    private final ProductRepository productRepository;
    private final ProductGroupRepository productGroupRepository;

    public boolean parse(byte[] fileContent) {
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int recordsRead = 0;
        int recordsSaved = 0;

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();

            // Security configurations for XML parser
            configureParserSecurity(factory);

            SAXParser saxParser = factory.newSAXParser();
            ProductHandler handler = new ProductHandler();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
            saxParser.parse(inputStream, handler);

            recordsRead = handler.getRecordsRead();
            List<Product> validProducts = handler.getValidProducts();

            // Save to database
            if (!validProducts.isEmpty()) {
                List<Product> savedProducts = productRepository.saveAll(validProducts);
                recordsSaved = savedProducts.size();
            }

            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memoryUsed = endMemory - startMemory;

            log.info("XML processing completed: {} records read, {} objects saved to database",
                    recordsRead, recordsSaved);
            log.info("Estimated RAM usage for XML processing: {} bytes ({} MB)",
                    memoryUsed, memoryUsed / (1024 * 1024));

            return true;

        } catch (ParserConfigurationException e) {
            log.error("XML parser configuration error: {}", e.getMessage());
            return false;
        } catch (SAXException e) {
            log.error("XML parsing error: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during XML processing: {}", e.getMessage());
            return false;
        }
    }

    private void configureParserSecurity(SAXParserFactory factory)
            throws ParserConfigurationException, SAXNotSupportedException, SAXNotRecognizedException {
        // Disable external entity processing to prevent XXE attacks
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        // Additional security features
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        factory.setXIncludeAware(false);
        factory.setNamespaceAware(true);
    }

    private class ProductHandler extends DefaultHandler {
        @Getter
        private final List<Product> validProducts = new ArrayList<>();
        private final List<String> fieldNames = new ArrayList<>();
        private final List<String> currentRecord = new ArrayList<>();
        private boolean inSchema = false;
        private boolean inData = false;
        private boolean inRecord = false;
        private int fieldIndex = -1;
        @Getter
        private int recordsRead = 0;

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            switch (qName) {
                case "schema":
                    inSchema = true;
                    fieldNames.clear();
                    break;
                case "data":
                    inData = true;
                    break;
                case "record":
                    if (inData) {
                        inRecord = true;
                        currentRecord.clear();
                        fieldIndex = -1;
                    }
                    break;
                case "field":
                    if (inSchema) {
                        String fieldName = attributes.getValue("name");
                        if (fieldName != null) {
                            fieldNames.add(fieldName);
                        }
                    }
                    break;
                case "f":
                    if (inRecord) {
                        fieldIndex++;
                    }
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (qName) {
                case "schema":
                    inSchema = false;
                    break;
                case "data":
                    inData = false;
                    break;
                case "record":
                    if (inData) {
                        inRecord = false;
                        processRecord();
                        recordsRead++;
                    }
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (inRecord && fieldIndex >= 0 && fieldIndex < fieldNames.size()) {
                String value = new String(ch, start, length);
                // Ensure we have enough elements in currentRecord
                while (currentRecord.size() <= fieldIndex) {
                    currentRecord.add("");
                }
                // Append to existing value instead of replacing
                String currentValue = currentRecord.get(fieldIndex);
                currentRecord.set(fieldIndex, (currentValue + value).trim());
            }
        }

        private void processRecord() {
            if (fieldNames.isEmpty() || currentRecord.isEmpty()) {
                return;
            }

            try {
                // Check if group field equals "0" (skip if not)
                String groupValue = getFieldValue("group");
                if (groupValue != null && !"0".equals(groupValue.trim())) {
                    log.info("Skip: Group{{}, {}}", getFieldValue("code"), getFieldValue("name"));
                    return; // Skip this record
                }

                Product product = new Product();

                // Map fields based on XML schema order
                product.setArticle(getFieldValue("code"));
                product.setName(getFieldValue("name"));
                product.setUnit(getFieldValue("unit"));
                product.setUnitVid(getFieldValue("measure_unit"));

                // Parse numeric fields
                String packaging = getFieldValue("packaging");
                if (packaging != null && !packaging.trim().isEmpty()) {
                    try {
                        product.setPackag(Integer.parseInt(packaging.trim()));
                    } catch (NumberFormatException e) {
                        log.debug("Invalid packaging value: {}", packaging);
                    }
                }

                // Parse price fields
                product.setPriceOpt1(parseBigDecimal(getFieldValue("price_Опт1")));
                product.setPriceOpt2(parseBigDecimal(getFieldValue("price_Опт2")));
                product.setPriceNal(parseBigDecimal(getFieldValue("price_Нал")));
                product.setPriceKons(parseBigDecimal(getFieldValue("price_Конс")));

                // todo
                // Set default group (you might need to adjust this based on your business logic)
                ProductGroup defaultGroup = productGroupRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Default product group not found"));
                product.setGroup(defaultGroup);

                log.info("Loaded: {}", productToString(product));

                validProducts.add(product);

            } catch (Exception e) {
                log.warn("Error processing record: {}", e.getMessage());
            }
        }

        private String getFieldValue(String fieldName) {
            int index = fieldNames.indexOf(fieldName);
            if (index >= 0 && index < currentRecord.size()) {
                String value = currentRecord.get(index);
                return value.isEmpty() ? null : value;
            }
            return null;
        }

        private BigDecimal parseBigDecimal(String value) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(value.trim());
            } catch (NumberFormatException e) {
                log.debug("Invalid decimal value: {}", value);
                return null;
            }
        }

        private String productToString(Product product) {

            if (product == null) {
                return "Product: null";
            }

            return String.format(
                    "Product {id=%d, name=%s, article=%s, unit=%s, unitVid=%s, packag=%s, " +
                            "priceOpt1=%s, priceOpt2=%s, priceNal=%s, priceKons=%s, group=%s}",
                    product.getId(),
                    quote(product.getName()),
                    quote(product.getArticle()),
                    quote(product.getUnit()),
                    quote(product.getUnitVid()),
                    product.getPackag(),
                    formatBigDecimal(product.getPriceOpt1()),
                    formatBigDecimal(product.getPriceOpt2()),
                    formatBigDecimal(product.getPriceNal()),
                    formatBigDecimal(product.getPriceKons()),
                    product.getGroup() != null ? "ProductGroup(id=" + product.getGroup().getId() + ")" : "null"
            ).replace("\n", "");
        }

        private static String quote(String value) {
            return value != null ? "\"" + value + "\"" : "null";
        }

        private static String formatBigDecimal(BigDecimal value) {
            return value != null ? value.stripTrailingZeros().toPlainString() : "null";
        }
    }

}


package com.lakomka.services.xml.imports;

import com.lakomka.models.product.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lakomka.services.xml.imports.ProductXmlParser.XmlFieldName.*;

@Slf4j
@Service(value = "ProductXmlParser")
@RequiredArgsConstructor
public class ProductXmlParser implements XmlParser {

    private final ProductXmlUpsert productXmlUpsert;

    /**
     * Cache of loaded Groups
     * key = CODE of Group from XML
     * val = Name of Group. If parent group present - name concatenated with Parent. Example "ЛК/Повидло ТУ"
     */
    private final Map<String, String> groups = new HashMap<>();

    @Override
    public boolean parse(byte[] fileContent) {
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int recordsRead;
        Stat stat = new Stat(0, 0, 0, 0, 0);

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
                stat = productXmlUpsert.upsert(validProducts);
            }

            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memoryUsed = endMemory - startMemory;

            log.info("XML processing completed: {} records read from file, {} groups loaded, "
                            + "{} verified objects for save to database, "
                            + "{} updated objects, {} new objects, {} not changed objects.",
                    recordsRead, groups.size(), stat.total(), stat.updated(), stat.newRecords(), stat.notTouched());
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
            log.error("Unexpected error during XML processing: {}", e.getMessage(), e);
            return false;
        }
    }

    private class ProductHandler extends DefaultHandler {
        public static final String SCHEMA_ELEMENT = "schema";
        public static final String DATA_ELEMENT = "data";
        public static final String RECORD_ELEMENT = "record";
        public static final String FIELD_ELEMENT = "field";
        public static final String F_ELEMENT = "f";
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
                                 Attributes attributes) {
            switch (qName) {
                case SCHEMA_ELEMENT:
                    inSchema = true;
                    fieldNames.clear();
                    break;
                case DATA_ELEMENT:
                    inData = true;
                    break;
                case RECORD_ELEMENT:
                    if (inData) {
                        inRecord = true;
                        currentRecord.clear();
                        fieldIndex = -1;
                    }
                    break;
                case FIELD_ELEMENT:
                    if (inSchema) {
                        String fieldName = attributes.getValue("name");
                        if (fieldName != null) {
                            fieldNames.add(fieldName);
                        }
                    }
                    break;
                case F_ELEMENT:
                    if (inRecord) {
                        fieldIndex++;
                    }
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            switch (qName) {
                case SCHEMA_ELEMENT:
                    inSchema = false;
                    break;
                case DATA_ELEMENT:
                    inData = false;
                    break;
                case RECORD_ELEMENT:
                    if (inData) {
                        inRecord = false;
                        processRecord();
                        recordsRead++;
                    }
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
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
                // Check if group field equals not "0" - that Group record
                String group = getFieldValue(GROUP);
                if (group != null && !"0".equals(group.trim())) {
                    String code = getFieldValue(CODE);
                    String name = getFieldValue(NAME);
                    String concatenatedName = name;
                    String parent = getFieldValue(PARENT);
                    if (parent != null) {
                        String parentName = groups.get(parent);
                        if (parentName != null) {
                            concatenatedName = parentName + " / " + name;
                        }
                    }
                    groups.put(code, concatenatedName);
                    log.info("Group: {}, {} -> {}", code, name, concatenatedName);
                    return; // Skip processing as product for this record
                }

                Product product = new Product();

                // Map fields based on XML schema order
                product.setArticle(getFieldValue(CODE));
                product.setName(getFieldValue(NAME));
                product.setUnit(getFieldValue(UNIT));
                product.setUnitVid(getFieldValue(M_UNIT));

                // Parse numeric fields
                String packaging = getFieldValue(PACKAGING);
                if (packaging != null && !packaging.trim().isEmpty()) {
                    try {
                        product.setPackag(Double.parseDouble(packaging.trim()));
                    } catch (NumberFormatException e) {
                        log.debug("Invalid packaging value: {}", packaging);
                    }
                }

                // Parse price fields
                product.setPriceOpt1(parseBigDecimal(getFieldValue(PR_OPT1)));
                product.setPriceOpt2(parseBigDecimal(getFieldValue(PR_OPT2)));
                product.setPriceNal(parseBigDecimal(getFieldValue(PR_NAL)));
                product.setPriceKons(parseBigDecimal(getFieldValue(PR_KONS)));

                product.setProductGroup(groups.get(getFieldValue(PARENT)));

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
                BigDecimal decimal = new BigDecimal(value.trim());
                return decimal.setScale(2, RoundingMode.HALF_UP);
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
                    product.getProductGroup() != null ? quote(product.getProductGroup()) : "null"
            ).replace("\n", "");
        }

        private static String quote(String value) {
            return value != null ? "\"" + value + "\"" : "null";
        }

        private static String formatBigDecimal(BigDecimal value) {
            return value != null ? value.stripTrailingZeros().toPlainString() : "null";
        }
    }

    public static class XmlFieldName {
        public static final String PR_OPT1 = "price_Опт1";
        public static final String PR_OPT2 = "price_Опт2";
        public static final String PR_NAL = "price_Нал";
        public static final String PR_KONS = "price_Конс";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String UNIT = "unit";
        public static final String M_UNIT = "measure_unit";
        public static final String PACKAGING = "packaging";
        public static final String GROUP = "group";
        public static final String PARENT = "parent";
    }
}


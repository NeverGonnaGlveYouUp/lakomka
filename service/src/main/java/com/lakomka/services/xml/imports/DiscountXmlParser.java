package com.lakomka.services.xml.imports;

import com.lakomka.dto.DiscountXmlDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.lakomka.services.xml.imports.DiscountXmlParser.XmlFieldName.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscountXmlParser implements XmlParser {

    private final DiscountXmlUpsert discountXmlUpsert;

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
            DiscountHandler handler = new DiscountHandler();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
            saxParser.parse(inputStream, handler);

            recordsRead = handler.getRecordsRead();
            List<DiscountXmlDto> discountList = handler.getValidDiscount();

            // Save to database
            if (!discountList.isEmpty()) {
                stat = discountXmlUpsert.upsert(discountList);
            }

            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memoryUsed = endMemory - startMemory;

            log.info("XML processing completed: "
                            + "{} records read from file, "
                            + "{} verified objects for save to database, "
                            + "{} updated objects, "
                            + "{} new objects, "
                            + "{} not changed objects, "
                            + "{} deleted objects.",
                    recordsRead, stat.total(), stat.updated(), stat.newRecords(), stat.notTouched(), stat.deleted());
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

    private static class DiscountHandler extends DefaultHandler {

        @Getter
        private final List<DiscountXmlDto> validDiscount = new ArrayList<>();
        private DiscountXmlDto currentDiscount;
        private final StringBuilder currentValue = new StringBuilder();
        @Getter
        private int recordsRead = 0;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            currentValue.setLength(0); // Clear the StringBuilder
            if (DISCOUNT_ITEM.equals(qName)) {
                currentDiscount = new DiscountXmlDto();
                validDiscount.add(currentDiscount);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            currentValue.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (currentDiscount == null) {
                return;
            }

            String value = currentValue.toString().trim();

            switch (qName) {
                case ID:
                    currentDiscount.setId(Long.parseLong(value));
                    break;
                case DISCOUNT:
                    currentDiscount.setDiscount(BigDecimal.valueOf(Double.parseDouble(value)));
                    break;
                case BASE_PRICE:
                    currentDiscount.setBasePrice(value);
                    break;
                case BIT_DISCOUNT:
                    currentDiscount.setBitDiscount(Boolean.parseBoolean(value));
                    break;
                case BIT_STOP:
                    currentDiscount.setBitStop(Boolean.parseBoolean(value));
                    break;
                case BIT_DELETE:
                    currentDiscount.setBitDelete(Boolean.parseBoolean(value));
                    break;
                case DATE_END:
                    currentDiscount.setDateEnd(value);
                    break;
                case DATE_START:
                    currentDiscount.setDateStart(value);
                    break;
                case JPERSON_OFFICE_ID:
                    currentDiscount.setJPersonOfficeId(Long.parseLong(value));
                    break;
                case JPERSON_SHOP_ID:
                    currentDiscount.setJPersonShopId(Long.parseLong(value));
                    break;
                case PRODUCT_ID:
                    currentDiscount.setProductId(Long.parseLong(value));
                    break;
                case DISCOUNT_ITEM:
                    recordsRead++;
                    break;
            }
        }
    }

    public static class XmlFieldName {
        public static final String BASE_PRICE = "basePrice";
        public static final String ID = "id";
        public static final String JPERSON_SHOP_ID = "JPersonShopId";
        public static final String JPERSON_OFFICE_ID = "JPersonOfficeId";
        public static final String PRODUCT_ID = "productId";
        public static final String BIT_DISCOUNT = "bitDiscount";
        public static final String DISCOUNT = "discount";
        public static final String BIT_STOP = "bitStop";
        public static final String DATE_START = "dateStart";
        public static final String DATE_END = "dateEnd";
        public static final String BIT_DELETE = "bitDelete";
        public static final String DISCOUNT_ITEM = "discount_item";
    }

}

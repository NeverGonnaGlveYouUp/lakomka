package com.lakomka.services.xml.imports;

import com.lakomka.dto.JpersonXmlDto;
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

import static com.lakomka.services.xml.imports.JPersonXmlParser.XmlFieldName.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JPersonXmlParser implements XmlParser {

    private final JPersonXmlUpsert jPersonXmlUpsert;

    @Override
    public boolean parse(byte[] fileContent) {

        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int recordsRead;
        Stat stat = new Stat(0, 0, 0, 0);

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();

            // Security configurations for XML parser
            configureParserSecurity(factory);

            SAXParser saxParser = factory.newSAXParser();
            JPersonHandler handler = new JPersonHandler();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
            saxParser.parse(inputStream, handler);

            recordsRead = handler.getRecordsRead();
            List<JpersonXmlDto> jPersonList = handler.getValidJPersons();

            // Save to database
            if (!jPersonList.isEmpty()) {
                stat = jPersonXmlUpsert.upsert(jPersonList);
            }

            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long memoryUsed = endMemory - startMemory;

            log.info("XML processing completed: "
                            + "{} records read from file, "
                            + "{} verified objects for save to database, "
                            + "{} updated objects, "
                            + "{} new objects, "
                            + "{} not changed objects.",
                    recordsRead, stat.total(), stat.updated(), stat.newRecords(), stat.notTouched());
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

    private static class JPersonHandler extends DefaultHandler {

        public static final String JPERSON_ITEM = "jperson";
        @Getter
        private final List<JpersonXmlDto> validJPersons = new ArrayList<>();
        private JpersonXmlDto currentJPerson;
        private final StringBuilder currentValue = new StringBuilder();
        @Getter
        private int recordsRead = 0;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            currentValue.setLength(0); // Clear the StringBuilder
            if (JPERSON_ITEM.equals(qName)) {
                currentJPerson = new JpersonXmlDto();
                validJPersons.add(currentJPerson);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            currentValue.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (currentJPerson == null) {
                return;
            }

            String value = currentValue.toString().trim();

            switch (qName) {
                case ACC_PRINT:
                    currentJPerson.setAccPrint(Boolean.parseBoolean(value));
                    break;
                case ADDRESS:
                    currentJPerson.setAddress(value);
                    break;
                case ADDRESS_DELIVERY:
                    currentJPerson.setAddressDelivery(value);
                    break;
                case BASE_PRICE:
                    currentJPerson.setBasePrice(value);
                    break;
                case CONTACT:
                    currentJPerson.setContact(value);
                    break;
                case DISCOUNTS:
                    currentJPerson.setDiscounts(value);
                    break;
                case DOGOVOR:
                    currentJPerson.setDogovor(Boolean.parseBoolean(value));
                    break;
                case DP_AGREEMENT:
                    currentJPerson.setDpAgreement(Boolean.parseBoolean(value));
                    break;
                case EDO:
                    currentJPerson.setEdo(Boolean.parseBoolean(value));
                    break;
                case EDO_DATE:
                    currentJPerson.setEdoDate(value);
                    break;
                case EMAIL:
                    currentJPerson.setEmail(value);
                    break;
                case GLOBAL_DISCOUNT:
                    currentJPerson.setGlobalDiscount(Integer.parseInt(value));
                    break;
                case INN:
                    currentJPerson.setINN(value);
                    break;
                case KPP:
                    currentJPerson.setKPP(value);
                    break;
                case MAP_DELIVERY:
                    currentJPerson.setMapDelivery(value);
                    break;
                case NAME:
                    currentJPerson.setName(value);
                    break;
                case NAME_FULL:
                    currentJPerson.setNameFull(value);
                    break;
                case OGRN:
                    currentJPerson.setOGRN(value);
                    break;
                case PAY_VID:
                    currentJPerson.setPayVid(Boolean.parseBoolean(value));
                    break;
                case PHONE:
                    currentJPerson.setPhone(value);
                    break;
                case POST:
                    currentJPerson.setPost(value);
                    break;
                case PRIM:
                    currentJPerson.setPrim(value);
                    break;
                case REST:
                    currentJPerson.setRest(BigDecimal.valueOf(Double.parseDouble(value)));
                    break;
                case REST_TIME:
                    currentJPerson.setRestTime(BigDecimal.valueOf(Double.parseDouble(value)));
                    break;
                case ROUTE_DAYS:
                    currentJPerson.setRouteDays(value);
                    break;
                case SERTIF_PRINT:
                    currentJPerson.setSertifPrint(Boolean.parseBoolean(value));
                    break;
                case SHIPPING_DELAY_DAYS:
                    currentJPerson.setShippingDelayDays(Integer.parseInt(value));
                    break;
                case SHOP_ID:
                    currentJPerson.setShopId(Long.parseLong(value));
                    break;
                case VZR_DOC:
                    currentJPerson.setVzrDoc(Boolean.parseBoolean(value));
                    break;
                case JPERSON_ITEM:
                    recordsRead++;
                    break;

            }
        }

    }

    public static class XmlFieldName {
        public static final String ACC_PRINT = "accPrint";
        public static final String ADDRESS = "address";
        public static final String ADDRESS_DELIVERY = "addressDelivery";
        public static final String BASE_PRICE = "basePrice";
        public static final String CONTACT = "contact";
        public static final String DISCOUNTS = "discounts";
        public static final String DOGOVOR = "dogovor";
        public static final String DP_AGREEMENT = "dpAgreement";
        public static final String EDO = "edo";
        public static final String EDO_DATE = "edoDate";
        public static final String EMAIL = "email";
        public static final String GLOBAL_DISCOUNT = "globalDiscount";
        public static final String INN = "INN";
        public static final String KPP = "KPP";
        public static final String MAP_DELIVERY = "mapDelivery";
        public static final String NAME = "name";
        public static final String NAME_FULL = "nameFull";
        public static final String OGRN = "OGRN";
        public static final String PAY_VID = "payVid";
        public static final String PHONE = "phone";
        public static final String POST = "post";
        public static final String PRIM = "prim";
        public static final String REST = "rest";
        public static final String REST_TIME = "restTime";
        public static final String ROUTE_DAYS = "routeDays";
        public static final String SERTIF_PRINT = "sertifPrint";
        public static final String SHIPPING_DELAY_DAYS = "shippingDelayDays";
        public static final String SHOP_ID = "shopId";
        public static final String VZR_DOC = "vzrDoc";
    }

}

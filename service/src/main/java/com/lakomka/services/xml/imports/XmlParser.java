package com.lakomka.services.xml.imports;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public interface XmlParser {
    boolean parse(byte[] fileContent);

    default void configureParserSecurity(SAXParserFactory factory)
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

}

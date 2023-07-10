package com.example.xml.service;

import com.example.xml.config.InvalidXMLException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

@Service
public class XMLValidationServiceImpl implements XMLValidationService {

    @Override
    public void validateXMLSchema(String xsdPath, String xmlPath) {

        try (InputStream in = new FileInputStream(xmlPath)) {
            validateXMLSchema(xsdPath, in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void validateXMLSchema(String xsdPath, InputStream xml) {

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
        } catch (IOException | SAXException e) {
            throw new InvalidXMLException();
        }
    }
}

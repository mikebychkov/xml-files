package com.example.xml.service;

import java.io.InputStream;

public interface XMLValidationService {

    void validateXMLSchema(String xsdPath, String xmlPath);
    void validateXMLSchema(String xsdPath, InputStream xml);
}

package com.example.xml.config;

public class InvalidXMLException extends RuntimeException {

    public InvalidXMLException() {
        super();
    }

    public InvalidXMLException(String message) {
        super(message);
    }
}
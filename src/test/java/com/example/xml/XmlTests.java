package com.example.xml;

import com.example.xml.config.InvalidXMLException;
import com.example.xml.dto.XmlData;
import com.example.xml.service.XMLValidationService;
import com.example.xml.service.XMLValidationServiceImpl;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Log4j2
public class XmlTests {

    XMLValidationService xmlValidationService = new XMLValidationServiceImpl();

    private XmlMapper mapper = new XmlMapper();

    @Test
    public void deserialize() throws IOException {

        File file = new File("./files/data/request.xml");

        XmlData xml = mapper.readValue(file, XmlData.class);

        log.info("XML: {}", xml);

        var screenInfo = xml.getDeviceInfo().getScreenInfo();
        log.info("WIDTH: {}; HEIGHT: {}; DPI: {}", screenInfo.getWidth(), screenInfo.getHeight(), screenInfo.getDpi());

        log.info("NEWSPAPER NAME: {}", xml.getDeviceInfo().getAppInfo().getNewspaperName());
    }

    @Test
    public void shouldValidate() {

        xmlValidationService.validateXMLSchema("./files/schema/request.xsd", "./files/data/request.xml");
    }

    @Test
    public void shouldNotValidate() {

        assertThrows(InvalidXMLException.class, () -> {
            xmlValidationService.validateXMLSchema("./files/schema/request.xsd", "./files/data/request2.xml");
        });
    }
}


package org.thingsboard.server.system;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;


@Slf4j
public class RestTemplateConvertersTest {

    @Test
    public void testJacksonXmlConverter() {
        ClassLoader classLoader = RestTemplate.class.getClassLoader();
        boolean jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", classLoader);
        Assertions.assertFalse(jackson2XmlPresent, "XmlMapper must not be present in classpath, please, exclude \"jackson-dataformat-xml\" dependency!");
        //If this xml mapper will be present in classpath then we will get "Unsupported Media Type" in RestTemplate
    }

}

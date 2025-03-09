package org.thingsboard.server.service.mail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class DefaultTbMailConfigTemplateService implements TbMailConfigTemplateService {

    private JsonNode mailConfigTemplates;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void postConstruct() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/mail_config_templates.json");
        try (InputStream inputStream = resource.getInputStream()) {
            mailConfigTemplates = objectMapper.readTree(inputStream);
        } catch (IOException e) {
            log.error("Failed to load mail configuration templates", e);
            throw e;
        }
    }

    @Override
    public JsonNode findAllMailConfigTemplates() {
        return mailConfigTemplates;
    }
}

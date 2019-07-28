package cz.muni.csirt.aida.idea.kafka;

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cz.muni.csirt.aida.idea.Idea;

public class IdeaDeserializer implements Deserializer<Idea> {

    private static final Logger logger = LoggerFactory.getLogger(IdeaDeserializer.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public IdeaDeserializer() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Date.class, new DateDeserializer());
        objectMapper.registerModule(module);
    }

    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public Idea deserialize(String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        try {
            return objectMapper.readValue(bytes, Idea.class);
        } catch (Exception e) {
            logger.error("Cannot deserialize Idea message", e);
            return null;
        }
    }

    @Override
    public void close() {
    }


    private static class DateDeserializer extends JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText();

            // To be able to parse RFC3999 dates where space is used as delimiter
            dateString = dateString.replace(' ', 'T');

            Instant instant = ZonedDateTime.parse(dateString, ISO_ZONED_DATE_TIME).toInstant();
            return Date.from(instant);
        }
    }

}

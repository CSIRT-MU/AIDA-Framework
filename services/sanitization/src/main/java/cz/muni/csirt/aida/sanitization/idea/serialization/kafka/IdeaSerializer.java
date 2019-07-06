package cz.muni.csirt.aida.sanitization.idea.serialization.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import cz.muni.csirt.aida.sanitization.idea.Idea;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class IdeaSerializer implements Serializer<Idea> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public IdeaSerializer() {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Idea data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void close() {
    }

}

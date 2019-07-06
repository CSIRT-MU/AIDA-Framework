package cz.muni.csirt.aida.sanitization.transformations.mappers;

import cz.muni.csirt.aida.sanitization.idea.Idea;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.List;
import java.util.stream.Collectors;

public class RemoveCategory implements ValueMapper<Idea, Idea> {

    private final String categoryPrefix;

    public RemoveCategory(String categoryPrefix) {
        this.categoryPrefix = categoryPrefix;
    }

    @Override
    public Idea apply(Idea value) {
        if (value.getCategory() != null) {
            List<String> filteredCategories = value.getCategory().stream()
                    .filter(s -> !s.startsWith(categoryPrefix))
                    .collect(Collectors.toList());
            value.setCategory(filteredCategories);
        }
        return value;
    }
}


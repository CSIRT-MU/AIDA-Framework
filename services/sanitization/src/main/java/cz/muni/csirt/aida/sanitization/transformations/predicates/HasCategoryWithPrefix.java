package cz.muni.csirt.aida.sanitization.transformations.predicates;


import org.apache.kafka.streams.kstream.Predicate;

import cz.muni.csirt.aida.idea.Idea;

public class HasCategoryWithPrefix implements Predicate<Object, Idea> {

    private final String prefix;

    public HasCategoryWithPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean test(Object key, Idea value) {
        return value.getCategory() != null &&
            value.getCategory().stream().anyMatch(val -> val.startsWith(prefix));
    }
}

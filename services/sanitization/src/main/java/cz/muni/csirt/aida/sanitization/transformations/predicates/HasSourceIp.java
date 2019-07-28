package cz.muni.csirt.aida.sanitization.transformations.predicates;


import org.apache.kafka.streams.kstream.Predicate;

import cz.muni.csirt.aida.idea.Idea;

public class HasSourceIp implements Predicate<Object, Idea> {

    @Override
    public boolean test(Object key, Idea value) {
        return value.getSource() != null &&
                value.getSource().stream()
                .anyMatch(source ->
                        source != null &&
                        source.getIP4() != null &&
                        !source.getIP4().isEmpty());
    }
}

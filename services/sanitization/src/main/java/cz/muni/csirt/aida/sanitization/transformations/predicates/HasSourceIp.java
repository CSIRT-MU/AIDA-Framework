package cz.muni.csirt.aida.sanitization.transformations.predicates;

import cz.muni.csirt.aida.sanitization.idea.Idea;

import org.apache.kafka.streams.kstream.Predicate;

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

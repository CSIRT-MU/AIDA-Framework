package cz.muni.csirt.aida.sanitization.transformations.predicates;

import cz.muni.csirt.aida.sanitization.idea.Idea;
import org.apache.kafka.streams.kstream.Predicate;

public class HasTargetPort implements Predicate<Object, Idea> {

    @Override
    public boolean test(Object key, Idea value) {
        return value.getTarget() != null &&
                value.getTarget().stream()
                .anyMatch(target ->
                    target != null &&
                    target.getPort() != null &&
                    !target.getPort().isEmpty());
    }
}

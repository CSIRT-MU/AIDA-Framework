package cz.muni.csirt.aida.sanitization.transformations.predicates;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.streams.kstream.Predicate;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.sanitization.jmx.Metrics;
import cz.muni.csirt.aida.sanitization.jmx.MetricsMBean;

public class HasTargetPort implements Predicate<Object, Idea> {

    private final MetricsMBean metrics = Metrics.getInstance();

    @Override
    public boolean test(Object key, Idea value) {
        boolean result =  value.getTarget() != null &&
                value.getTarget().stream()
                .anyMatch(target ->
                    target != null &&
                    target.getPort() != null &&
                    !target.getPort().isEmpty());


        if (result) {
            metrics.getMeasures()
                    .computeIfAbsent("Has target port", x -> new AtomicInteger())
                    .incrementAndGet();
        }

        return result;
    }
}

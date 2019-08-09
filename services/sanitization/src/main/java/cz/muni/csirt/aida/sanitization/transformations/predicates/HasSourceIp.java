package cz.muni.csirt.aida.sanitization.transformations.predicates;


import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.streams.kstream.Predicate;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.sanitization.jmx.Metrics;
import cz.muni.csirt.aida.sanitization.jmx.MetricsMBean;

public class HasSourceIp implements Predicate<Object, Idea> {

    private final MetricsMBean metrics = Metrics.getInstance();

    @Override
    public boolean test(Object key, Idea value) {
        boolean result = value.getSource() != null &&
                value.getSource().stream()
                .anyMatch(source ->
                        source != null &&
                        source.getIP4() != null &&
                        !source.getIP4().isEmpty());

        if (result) {
            metrics.getMeasures()
                    .computeIfAbsent("Has source IP", x -> new AtomicInteger())
                    .incrementAndGet();
        }

        return result;
    }
}

package cz.muni.csirt.aida.sanitization.transformations.predicates;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.streams.kstream.Predicate;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.sanitization.jmx.Metrics;
import cz.muni.csirt.aida.sanitization.jmx.MetricsMBean;

public class HasCategoryWithPrefix implements Predicate<Object, Idea> {

    private final MetricsMBean metrics = Metrics.getInstance();
    private final String prefix;

    public HasCategoryWithPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean test(Object key, Idea value) {
        boolean result = value.getCategory() != null &&
                value.getCategory().stream().anyMatch(val -> val.startsWith(prefix));

        if (result) {
            metrics.getMeasures()
                    .computeIfAbsent("has category with prefix " + prefix, x -> new AtomicInteger())
                    .incrementAndGet();
        }

        return result;
    }
}

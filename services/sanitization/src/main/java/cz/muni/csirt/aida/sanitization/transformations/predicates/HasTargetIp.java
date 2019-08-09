package cz.muni.csirt.aida.sanitization.transformations.predicates;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.streams.kstream.Predicate;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.sanitization.jmx.Metrics;
import cz.muni.csirt.aida.sanitization.jmx.MetricsMBean;

public class HasTargetIp implements Predicate<Object, Idea> {

	private final MetricsMBean metrics = Metrics.getInstance();

	@Override
	public boolean test(Object key, Idea value) {
		boolean result =  value.getTarget() != null &&
				value.getTarget().stream()
				.anyMatch(target ->
						target != null &&
						target.getIP4() != null &&
						!target.getIP4().isEmpty());

		if (result) {
			metrics.getMeasures()
					.computeIfAbsent("Has target IP", x -> new AtomicInteger())
					.incrementAndGet();
		}

		return result;
	}
}

package cz.muni.csirt.aida.sanitization.transformations.mappers;


import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.streams.kstream.ValueMapper;

import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.sanitization.jmx.Metrics;

public class MeasuringMapper implements ValueMapper<Idea, Idea> {

	private final Metrics metrics = Metrics.getInstance();
	private final String measureName;

	public MeasuringMapper(String measureName) {
		this.measureName = measureName;
	}

	@Override
	public Idea apply(Idea value) {
		AtomicInteger i = metrics.getMeasures().computeIfAbsent(measureName, x -> new AtomicInteger());
		i.incrementAndGet();
		return value;
	}
}

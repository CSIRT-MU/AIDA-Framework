package cz.muni.csirt.aida.feedback.jmx;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cz.muni.csirt.aida.mining.model.Rule;

public interface MetricsMBean {
	double getMaxMemoryInMb();
	Map<String, AtomicInteger> getMeasures();
	Map<Rule, RuleMeasures> getRulesMeasures();
}

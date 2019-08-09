package cz.muni.csirt.aida.sanitization.jmx;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface MetricsMBean {
	double getMaxMemoryInMb();
	Map<String, AtomicInteger> getMeasures();
}

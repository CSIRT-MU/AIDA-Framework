package cz.muni.csirt.aida.feedback.jmx;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.muni.csirt.aida.mining.model.Rule;
import cz.muni.csirt.aida.mining.model.Rules;

public class Metrics implements MetricsMBean {

	private static final int LOGGING_INTERVAL_SEC = 60;

	private static final Logger logger = LoggerFactory.getLogger(Metrics.class);
	private static final Metrics INSTANCE = new Metrics();

	static {
		try {
			ObjectName objectName = new ObjectName("cz.muni.csirt.aida:type=basic,name=feedback");
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			server.registerMBean(Metrics.getInstance(), objectName);
		} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			throw new RuntimeException(e);
		}
	}

	private double maxMemoryInMb = Double.MIN_VALUE;
	private Map<String, AtomicInteger> measures = new ConcurrentHashMap<>();
	private Map<Rule, RuleMeasures> rulesMeasures = new ConcurrentHashMap<>();

	private Metrics() {
		Timer timer = new Timer();

		TimerTask measuringTask = new TimerTask() {
			@Override
			public void run() {
				double currentMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
						/ 1024d / 1024d;
				if (currentMemory > maxMemoryInMb) {
					maxMemoryInMb = currentMemory;
				}
			}
		};
		timer.scheduleAtFixedRate(measuringTask, 0, 1000);

		TimerTask loggingTask = new TimerTask() {
			@Override
			public void run() {
				logger.info("Metrics: max memory usage in last {} seconds was {} MB", LOGGING_INTERVAL_SEC,
						getMaxMemoryInMb());
				maxMemoryInMb = 0;

				StringBuilder stringBuilder = new StringBuilder();
				for (Map.Entry<String, AtomicInteger> entry : measures.entrySet()) {
					stringBuilder
							.append(entry.getKey())
							.append(" : ")
							.append(entry.getValue())
							.append("\n");
				}
				logger.info("Metrics : \n{}", stringBuilder);

				stringBuilder = new StringBuilder();
				for (Map.Entry<Rule, RuleMeasures> entry : rulesMeasures.entrySet()) {
					stringBuilder
							.append(Rules.toSpmf(entry.getKey()))
							.append(" : ")
							.append(entry.getValue().toString())
							.append("\n");
				}
				logger.info("Rules : \n{}", stringBuilder);
			}
		};
		timer.scheduleAtFixedRate(loggingTask, 0, LOGGING_INTERVAL_SEC * 1000L);

	}

	public static Metrics getInstance() {
		return INSTANCE;
	}

	@Override
	public double getMaxMemoryInMb() {
		return maxMemoryInMb;
	}

	@Override
	public Map<String, AtomicInteger> getMeasures() {
		return measures;
	}

	@Override
	public Map<Rule, RuleMeasures> getRulesMeasures() {
		return rulesMeasures;
	}
}

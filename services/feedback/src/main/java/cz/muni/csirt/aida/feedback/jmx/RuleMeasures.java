package cz.muni.csirt.aida.feedback.jmx;

import java.util.concurrent.atomic.AtomicInteger;

public class RuleMeasures {

	private AtomicInteger predictions = new AtomicInteger();
	private AtomicInteger observations = new AtomicInteger();
	private int maxMitigationTime = Integer.MIN_VALUE;
	private int minMitigationTime = Integer.MAX_VALUE;
	private int numberOfPositiveMitigationTimes;
	private int numberOfNegativeMitigationTimes;
	private int avgOfPositiveMitigationTime;
	private int avgOfNegativeMitigationTime;

	public AtomicInteger getPredictions() {
		return predictions;
	}

	public void setPredictions(AtomicInteger predictions) {
		this.predictions = predictions;
	}

	public AtomicInteger getObservations() {
		return observations;
	}

	public void setObservations(AtomicInteger observations) {
		this.observations = observations;
	}

	public int getAvgOfPositiveMitigationTime() {
		return avgOfPositiveMitigationTime;
	}

	public void setAvgOfPositiveMitigationTime(int avgOfPositiveMitigationTime) {
		this.avgOfPositiveMitigationTime = avgOfPositiveMitigationTime;
	}

	public int getAvgOfNegativeMitigationTime() {
		return avgOfNegativeMitigationTime;
	}

	public void setAvgOfNegativeMitigationTime(int avgOfNegativeMitigationTime) {
		this.avgOfNegativeMitigationTime = avgOfNegativeMitigationTime;
	}

	public synchronized void updateMitigationExtremes(int mitigationTime) {
		if (mitigationTime > maxMitigationTime) {
			maxMitigationTime = mitigationTime;
		}
		if (mitigationTime < minMitigationTime && mitigationTime > 0) {
			minMitigationTime = mitigationTime;
		}
	}

	public synchronized void updateMitigationAverages(int mitigationTime) {
		if (mitigationTime < 0) {

			avgOfNegativeMitigationTime =
					((numberOfNegativeMitigationTimes * avgOfNegativeMitigationTime) + mitigationTime)
							/ (numberOfNegativeMitigationTimes+1);

			numberOfNegativeMitigationTimes++;
		}

		if (mitigationTime > 0) {
			avgOfPositiveMitigationTime =
					((numberOfPositiveMitigationTimes * avgOfPositiveMitigationTime) + mitigationTime)
							/ (numberOfPositiveMitigationTimes+1);

			numberOfPositiveMitigationTimes++;
		}
	}

	@Override
	public String toString() {
		return "RuleMeasures{" +
				"predictions=" + predictions +
				", observations=" + observations +
				", maxMitigationTime=" + maxMitigationTime +
				", minMitigationTime=" + minMitigationTime +
				", numberOfPositiveMitigationTimes=" + numberOfPositiveMitigationTimes +
				", numberOfNegativeMitigationTimes=" + numberOfNegativeMitigationTimes +
				", avgOfPositiveMitigationTime=" + avgOfPositiveMitigationTime +
				", avgOfNegativeMitigationTime=" + avgOfNegativeMitigationTime +
				'}';
	}
}

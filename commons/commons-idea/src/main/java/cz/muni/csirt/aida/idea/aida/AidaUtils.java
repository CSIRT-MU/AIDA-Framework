package cz.muni.csirt.aida.idea.aida;

import java.util.Collections;
import java.util.Map;

import cz.muni.csirt.aida.idea.Idea;

public class AidaUtils {

	private static final String AIDA_ATTRIBUTE = "_aida";
	private static final String DUPLICATE = "Duplicate";
	private static final String CONTINUING = "Continuing";
	private static final String OVERLAPPING = "Overlapping";
	private static final String NON_OVERLAPPING = "NonOverlapping";

	private static final String MITIGATION_TIME = "mitigationTime";

	private AidaUtils() {
	}

	private static Map<?, ?> getAidaAttribute(Idea idea) {
		Object aida = idea.getAdditionalProperties().get(AIDA_ATTRIBUTE);

		if (aida instanceof Map) {
			return (Map<?, ?>) aida;
		}
		return Collections.emptyMap();
	}

	public static boolean isDuplicate(Idea idea) {
		return getAidaAttribute(idea).containsKey(DUPLICATE);
	}

	public static boolean isContinuing(Idea idea) {
		return getAidaAttribute(idea).containsKey(CONTINUING);
	}

	public static boolean isOverlapping(Idea idea) {
		return getAidaAttribute(idea).containsKey(OVERLAPPING);
	}

	public static boolean isNonOverlapping(Idea idea) {
		return getAidaAttribute(idea).containsKey(NON_OVERLAPPING);
	}

	public static int getMitigationTime(Idea idea) {
		Object aida = idea.getAdditionalProperties().get(MITIGATION_TIME);

		if (aida == null) {
			throw new IllegalStateException("Idea event doesn't have '" + MITIGATION_TIME + "' attribute");
		}

		if (aida instanceof Integer) {
			return (Integer) aida;
		}
		throw new IllegalStateException("Idea event has '" + MITIGATION_TIME + "' attribute with invalid value '" +
				aida + "'");
	}

	public static String getSourceIP4(Idea idea) {
		return idea.getSource().get(0).getIP4().get(0);
	}

	public static Integer getTargetPort(Idea idea) {
		try {
			return idea.getTarget().get(0).getPort().get(0);
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			return null;
		}
	}
}

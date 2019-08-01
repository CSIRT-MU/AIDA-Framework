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
}

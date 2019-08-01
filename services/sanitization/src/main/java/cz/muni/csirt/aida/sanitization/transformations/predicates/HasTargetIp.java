package cz.muni.csirt.aida.sanitization.transformations.predicates;

import org.apache.kafka.streams.kstream.Predicate;

import cz.muni.csirt.aida.idea.Idea;

public class HasTargetIp implements Predicate<Object, Idea> {

	@Override
	public boolean test(Object key, Idea value) {
		return value.getTarget() != null &&
				value.getTarget().stream()
				.anyMatch(target ->
						target != null &&
						target.getIP4() != null &&
						!target.getIP4().isEmpty());
	}
}

package cz.muni.csirt.aida.mining.spmf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import ca.pfv.spmf.input.sequence_database_array_integers.Sequence;
import ca.pfv.spmf.input.sequence_database_array_integers.SequenceDatabase;
import cz.muni.csirt.aida.idea.Idea;
import cz.muni.csirt.aida.mining.model.Item;
import cz.muni.csirt.aida.mining.model.KeyType;

class SequenceDatabaseBuilder implements IdeaDatabaseBuilder<IdeaSequenceDatabase> {

	private Map<Object, Sequence> sequences = new HashMap<>();
	private KeyType keyType;
	private Map<Item, Integer> itemMapping = new HashMap<>();

	SequenceDatabaseBuilder(KeyType keyType) {
		this.keyType = keyType;
	}

	@Override
	public void addEvent(Idea idea) {
		Integer item = itemMapping.computeIfAbsent(new Item(idea), k -> itemMapping.size());
		Sequence sequence = sequences.computeIfAbsent(keyType.getKey(idea), k -> new Sequence());
		sequence.addItemset(new Integer[] { item });
	}

	@Override
	public IdeaSequenceDatabase build() {
		SequenceDatabase database = new SequenceDatabase();
		sequences.values().forEach(database::addSequence);

		if (!itemMapping.isEmpty()) {
			database.minItem = Collections.min(itemMapping.values());
			database.maxItem = Collections.max(itemMapping.values());
		}

		Map<Integer, Item> reverseItemMapping = itemMapping.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		return new IdeaSequenceDatabase(database, reverseItemMapping, keyType);
	}
}

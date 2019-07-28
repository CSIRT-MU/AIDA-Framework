package cz.muni.csirt.aida.mining.spmf;

import java.util.Map;

import ca.pfv.spmf.input.sequence_database_array_integers.SequenceDatabase;
import cz.muni.csirt.aida.mining.model.Item;
import cz.muni.csirt.aida.mining.model.KeyType;

public class IdeaSequenceDatabase {

	private SequenceDatabase database;
	private Map<Integer, Item> itemMapping;
	private KeyType keyType;

	public IdeaSequenceDatabase(SequenceDatabase database, Map<Integer, Item> itemMapping, KeyType keyType) {
		this.database = database;
		this.itemMapping = itemMapping;
		this.keyType = keyType;
	}

	public SequenceDatabase getDatabase() {
		return database;
	}

	public Map<Integer, Item> getItemMapping() {
		return itemMapping;
	}

	public KeyType getKeyType() {
		return keyType;
	}
}

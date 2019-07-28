package ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/*
 * This file is copyright (c) Ted Gueniche 
 * <ted.gueniche@gmail.com>
 *
 * This file is part of the IPredict project
 * (https://github.com/tedgueniche/IPredict).
 *
 * IPredict is distributed under The MIT License (MIT).
 * You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT 
 */

public class Sequence {

	private List<Item> items;
	private int id; // id de la sequence
	
	
	public Sequence(int id){
		this.id = id;
		items = new ArrayList<Item>();
	}
	

	/**
	 * Make a hard copy of the given sequence
	 * @param aSequence  sequence to copy
	 */
	public Sequence(Sequence aSequence) {
		this.id = aSequence.id;
		this.items = new ArrayList<Item>();
		for(Item item : aSequence.getItems()) {
			this.items.add(new Item(item.val));
		}
	}
	
	public Sequence(int id, List<Item> items) {
		this.id = id;
		this.items = (items != null) ? items : new ArrayList<Item>();
	}
	
	public int getId() {
		return id;
	}

	public List<Item> getItems() {
		return items;
	}
	
	private void setItems(List<Item> newItems) {
		items = newItems;
	}
	
	public Item get(int index) {
		return items.get(index);
	}
	
	public int size(){
		return items.size();
	}

	public void addItem(Item item) {
		items.add(item);
	}
	
	/**
	 * return the last [length] items from this sequence as a sequence
	 * @return [length] or less (if not enough items) items as a sequence or NULL on error
	 */
	public Sequence getLastItems(int length, int offset) {
		
		Sequence truncatedSequence = new Sequence(0);
		int size = size() - offset;
		
		//If there is not enough items then returns all available items
		if(items.isEmpty()) {
			return null; //ERROR
		}
		else if(length > size) {
			//creating new sequence with truncated list
			// PHIL08: HERE I MODIFIED TO MAKE A COPY OF THE LIST RETURNED BY SUBLIST 
			// BECAUSE BY DEFAULT SUBLIST MAKE POINTERS TO THE ORIGINAL LIST AND IF 
			// WE MODIFY THE LIST WE MAY GET A CONCURRENT ACCESS EXCEPTION (I was getting one!)
//		    //  new ArrayList(...)
			List<Item> truncatedList = new ArrayList<Item>(items.subList( 0, size ));
			truncatedSequence.setItems(truncatedList);
		}
		else {
			//splitting list
			// PHIL08: HERE I MODIFIED TO MAKE A COPY OF THE LIST RETURNED BY SUBLIST 
			// BECAUSE BY DEFAULT SUBLIST MAKE POINTERS TO THE ORIGINAL LIST AND IF 
			// WE MODIFY THE LIST WE MAY GET A CONCURRENT ACCESS EXCEPTION (I was getting one!)
			//  new ArrayList(...)
			List<Item> truncatedList = new ArrayList<Item>(items.subList( (size - length), (size) ));
			truncatedSequence.setItems(truncatedList);
		}
		
		return truncatedSequence;
		/*

		int size = size() - offset;
		if(length <= size) {
			//splitting list
			List<Item> truncatedList = items.subList( (size - length), (size) );
			
			//creating new sequence with truncated list
			Sequence truncatedSequence = new Sequence(0);
			truncatedSequence.setItems(truncatedList);
			
			return truncatedSequence;
		}
		else {
			return null; //should never happen! it would cause the algo to not work properly

		}
		*/
	}
	
	//adjust sequence to keep only the "length" items at the end of the sequence
	/*
	public void keepOnlyLastItems(int length) {
		if(length < size()) {
			//Sequence 
			//items = items.subList(size() - length, size()); //sketchy
		}
	}
	*/
	
	public void print() {
		System.out.print(toString());
	}
	
	public String toString() {
		StringBuffer r = new StringBuffer("");
		for(Item it : items){
			r.append('(');
			String string = it.toString();
			r.append(string);
			r.append(") ");
		}

		return r.append("    ").toString();
	}
	
	public void setID(int newid) {
		id = newid;
	}
	
	@Override
	public Sequence clone() {
		
		Sequence copy = new Sequence(id);
		
		for(Item item : items) {
			copy.items.add(item.clone());
		}
		
		return copy;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		Sequence other = (Sequence) obj;
		return equals(other);
	}
	
	public boolean equals(Sequence other) {
		
		if(id != other.id || items.size() != other.items.size()) {
			return false;
		}
		
		for(int i = 0; i < items.size(); i++) {
			
			if(items.get(i).equals(other.items.get(i)) == false) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + id;
		result = prime * result + items.hashCode();
		
		return result;
	}
	
	public static void main(String...args) {
		
		
		
		Sequence a = new Sequence(-1);
		a.addItem(new Item(1));
		a.addItem(new Item(2));
		a.addItem(new Item(3));
		
		Sequence b = new Sequence(-1);
		b.addItem(new Item(1));
		b.addItem(new Item(2));
		b.addItem(new Item(3));
		
		Sequence c = b.clone();
		
		System.out.println(a.hashCode());
		System.out.println(b.hashCode());
		System.out.println(c.hashCode());
		
		
		HashSet<Sequence> seen = new HashSet<Sequence>();
		seen.add(b);
		
		if(seen.contains(a)) {
			System.out.println("Seen a");
		}
		
		if(seen.contains(b)) {
			System.out.println("Seen b (obviously)");
		}
		
		if(seen.contains(c)) {
			System.out.println("Seen c");
		}
		
		if(b.equals(a)) {
			System.out.println("a == b");
		}
		
		if(b.equals(c)) {
			System.out.println("b == c");
		}
	}
}

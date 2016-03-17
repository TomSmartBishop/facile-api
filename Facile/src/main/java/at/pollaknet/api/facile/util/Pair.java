package at.pollaknet.api.facile.util;

/**
 * A generic Key&Value pair where the key implements
 * the generic interface {@code comparable <T>}.
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 *
 * @param <K> The type (CLASS) of the key implementing {@code comparable <CLASS>}.
 * @param <V> The type (CLASS) of the value.
 */
public class Pair<K extends Comparable<K>, V> implements Comparable<K>{

	//make these fields public because getters and setters
	//for both items are a bit useless.
	public K key;
	public V value;

	/**
	 * Default constructor for an instance of a key and a value.
	 */
	public Pair() {
	}
	
	/**
	 * Constructor for a pair containing a key and a value.
	 * @param key The key for this pair.
	 * @param value The value for this pair.
	 */
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public int compareTo(K compareObject) {
		return compareObject.compareTo(key);
	}
	
}

package at.pollaknet.api.facile.symtab.symbols;

public interface ClassLayout {

	/**
	 * Returns the alignment of the fields (which is
	 * a number of the power of 2 between 2<sup>0</sup>
	 * and 2<sup>7</sup> - default is 1).
	 * @return The packing size, which represents the
	 * byte alignment of the fields inside a class.
	 */
	public abstract int getPackingSize();
	
	/**
	 * Returns the total specified size of the class in bytes.
	 * @return The via a layout defined size of the class.
	 */
	public abstract long getClassSize();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	int compareTo(ClassLayout c);
}

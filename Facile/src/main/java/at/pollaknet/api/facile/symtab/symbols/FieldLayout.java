package at.pollaknet.api.facile.symtab.symbols;

public interface FieldLayout {

	/**
	 * Returns the offset of the field relative to the beginning of the class.
	 * @return The explicit specified offset of the field.
	 */
	public abstract long getOffset();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
	
}

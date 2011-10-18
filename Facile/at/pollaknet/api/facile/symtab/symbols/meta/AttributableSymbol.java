package at.pollaknet.api.facile.symtab.symbols.meta;


public interface AttributableSymbol {
	
	/**
	 * Returns an array of {@link at.pollaknet.api.facile.symtab.symbols.meta.CustomAttribute},
	 * which are assigned to the implementing object.
	 * @return The defined custom attributes.
	 */
	public abstract CustomAttribute [] getCustomAttributes();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

package at.pollaknet.api.facile.symtab.symbols;


public interface PropertySignature {
	
	/**
	 * Returns all parameters of a property as {@link at.pollaknet.api.facile.symtab.symbols.Parameter}.
	 * @return An array of parameters.
	 */
	public abstract Parameter[] getParameters();
	
	/**
	 * Non static property methods have an implicit first argument referencing the class instance.
	 * @return {@code true} if the property has an implicit first "this" argument, otherwise {@code false}
	 */
	public abstract boolean hasThis();

	/**
	 * Returns the base type of the property (this is
	 * the type which has been resolved via a binary signature and is possibly
	 * not the same as the owner - even if they are equal).
	 * @return The base type of the property.
	 */
	public abstract TypeRef getTypeRef();
	
	/**
	 * Returns the number of used parameters inside the property.
	 * @return The number of parameters.
	 */
	public abstract int getParameterCount();
	
	/**
	 * Get the raw representation of the property.
	 * @return The binary property signature as {@code byte []}.
	 */
	public abstract byte [] getBinarySignature();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

}
package at.pollaknet.api.facile.symtab.symbols;


public interface GenericParameter extends Parameter {

	/**
	 * Returns a method as constraint to a generic parameter, which
	 * was possible with Metadata stream v1.1 before .Net 2.0.
	 * @return A {at.pollaknet.api.facile.symtab.symbols.Method}
	 * or {@code null}.
	 */
	public abstract Method getDeprecatedMethodConstraint();

	/**
	 * Returns a type as constraint to a generic parameter, which
	 * was possible with Metadata stream v1.1 before .Net 2.0.
	 * @return A {at.pollaknet.api.facile.symtab.symbols.Type}
	 * or {@code null}.
	 */
	public abstract Type getDeprecatedTypeConstraint();

	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}
package at.pollaknet.api.facile.symtab.symbols;

/**
 * <p/>A qualifiable symbol is almost the same like
 * {@link at.pollaknet.api.facile.symtab.symbols.FullQualifiableSymbol},
 * with the difference that it does not support utility methods for the
 * name space and the short system name.
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public interface QualifiableSymbol {

	/**
	 * Returns the full qualified name of the implementing object.
	 * @return The full qualified name.
	 */
	public abstract String getFullQualifiedName();

	/**
	 * The name of the object (without the name space).
	 * @return The object's name.
	 */
	public abstract String getName();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

}
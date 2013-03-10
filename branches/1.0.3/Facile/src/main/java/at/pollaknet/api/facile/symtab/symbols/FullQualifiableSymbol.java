package at.pollaknet.api.facile.symtab.symbols;

/**
 * <p/>A full qualifiable symbol has a full qualified name. As result it
 * consists of two parts: The name space and the symbol's name.
 * 
 * <p/>Some .Net embedded types even contain a second name which can
 * be used to identify the symbol - the short system name.
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public interface FullQualifiableSymbol extends QualifiableSymbol {

	/**
	 * The object's name space.
	 * @return The name space where the implementing object lives.
	 */
	public abstract String getNamespace();
	
	/**
	 * Get the short representation of the object, which is used in the CLI.
	 * @return The short system name or {@code null} if there is no short system name.
	 */
	public abstract String getShortSystemName();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

}

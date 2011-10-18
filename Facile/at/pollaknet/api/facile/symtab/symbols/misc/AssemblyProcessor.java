package at.pollaknet.api.facile.symtab.symbols.misc;

public interface AssemblyProcessor {

	/**
	 * This feature is not used in assemblies.
	 */
	public abstract long getProcessor();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

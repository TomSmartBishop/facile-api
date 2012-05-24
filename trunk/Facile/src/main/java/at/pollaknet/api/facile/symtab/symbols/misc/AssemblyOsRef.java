package at.pollaknet.api.facile.symtab.symbols.misc;

import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;

public interface AssemblyOsRef extends AssemblyOs {

	/**
	 * This feature is not used in assemblies.
	 */
	public abstract AssemblyRef getAssemblyRef();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

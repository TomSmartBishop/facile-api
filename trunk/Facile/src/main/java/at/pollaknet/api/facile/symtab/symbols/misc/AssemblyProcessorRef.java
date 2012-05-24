package at.pollaknet.api.facile.symtab.symbols.misc;

import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;

public interface AssemblyProcessorRef extends AssemblyProcessor {

	/**
	 * This feature is not used in assemblies.
	 */
	public abstract AssemblyRef getAssemblyRef();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
	
}

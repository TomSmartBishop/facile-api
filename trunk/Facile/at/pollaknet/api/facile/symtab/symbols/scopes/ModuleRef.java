package at.pollaknet.api.facile.symtab.symbols.scopes;

import at.pollaknet.api.facile.symtab.symbols.FullQualifiableSymbol;
import at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace;
import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;

public interface ModuleRef extends AttributableSymbol, FullQualifiableSymbol {

	/**
	 * Returns a array of all defined name spaces as flat hierarchy of
	 * {@link at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace} objects.
	 * @return The name spaces of the module.
	 */
	public abstract Namespace[] getNamespaces();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	int compareTo(ModuleRef o);
}

package at.pollaknet.api.facile.symtab.symbols.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.symtab.symbols.FullQualifiableSymbol;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;

public interface Namespace extends FullQualifiableSymbol, Comparable<Namespace>, RenderableCilElement {

	/**
	 * Returns all defined or referenced types (classes) in this name space.
	 * @return An array of {@link at.pollaknet.api.facile.symtab.symbols.TypeRef}.
	 */
	public abstract TypeRef[] getTypeRefs();

	/**
	 * Check weather the current name space is sub name space of the specified name space or not
	 * (e.g. {@code at.pollaknet.facile} is a sub name space of {@code at.pollaknet}).
	 * @param namespace The super {@link at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace} for the proof.
	 * @return {@code true} if the current name space is a sub name space of the specified, otherwise {@code false}.
	 */
	public abstract boolean isSubNamespace(Namespace namespace);
	
	/**
	 * Check weather the current name space is sub name space of the specified name space or not
	 * (e.g. {@code at.pollaknet.facile} is a sub name space of {@code at.pollaknet}).
	 * @param namespace The super {@code String} for the proof.
	 * @return {@code true} if the current name space is a sub name space of the specified, otherwise {@code false}.
	 */
	public abstract boolean isSubNamespace(String namespace);
	
	/**
	 * Check weather the current name space is super name space of the specified name space or not
	 * (e.g. {@code at.pollaknet} is a super name space of {@code at.pollaknet.facile}).
	 * @param namespace The super {@link at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace} for the proof.
	 * @return {@code true} if the current name space is a sub name space of the specified, otherwise {@code false}.
	 */
	public abstract boolean isSuperNamespace(Namespace namespace);
	
	/**
	 * Check weather the current name space is super name space of the specified name space or not
	 * (e.g. {@code at.pollaknet} is a super name space of {@code at.pollaknet.facile}).
	 * @param namespace The super {@code String} for the proof.
	 * @return {@code true} if the current name space is a sub name space of the specified, otherwise {@code false}.
	 */
	public abstract boolean isSuperNamespace(String namespace);

	/**
	 * Returns the address of the name space. The address is a tokenized version
	 * of the full name space with the dot as separator.
	 * @return The address of the namespace as {@code String []}.
	 */
	public String[] getAddress();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

}
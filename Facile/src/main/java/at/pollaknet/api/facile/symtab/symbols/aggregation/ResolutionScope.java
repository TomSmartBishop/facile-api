package at.pollaknet.api.facile.symtab.symbols.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.symtab.symbols.FullQualifiableSymbol;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.Module;
import at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef;

public interface ResolutionScope extends RenderableCilElement, FullQualifiableSymbol {
	
	/**
	 * Returns a module if the underlying scope is a module.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.scopes.Module} object or {@code null}.
	 */
	public abstract Module getModule();
	
	/**
	 * Returns a module reference if the underlying scope is a module reference.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef} object or {@code null}.
	 */
	public abstract ModuleRef getModuleRef();
	
	/**
	 * Returns an assembly reference if the underlying scope is an assembly reference.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef} object or {@code null}.
	 */
	public abstract AssemblyRef getAssemblyRef();
	
	/**
	 * Returns a type reference if the underlying scope is a type reference.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.TypeRef} object or {@code null}.
	 */
	public abstract TypeRef getTypeRef();

	/**
	 * Check if this resolution scope object is in the current assembly.
	 * @return {@code true} if the object is in the current assembly.
	 */
	public abstract boolean isInAssembly();

	//this is a draft interface method
	//public abstract void setNamespaces(Namespace[] namespaces);
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

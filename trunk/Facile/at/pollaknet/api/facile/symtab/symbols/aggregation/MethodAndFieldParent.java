package at.pollaknet.api.facile.symtab.symbols.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef;

public interface MethodAndFieldParent extends RenderableCilElement {

	/**
	 * The name of the parent object.
	 * @return The name of the parent (owner) object.
	 */
	public abstract String getName();
	
	/**
	 * Returns a type if the underlying object is a type.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.Type} object or {@code null}.
	 */
	public abstract Type getType();
	
	/**
	 * Returns a type reference if the object implementation is a type reference.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.TypeRef} object or {@code null}.
	 */
	public abstract TypeRef getTypeRef();
	
	/**
	 * Returns a type specification if the object implementation is a type specification.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.TypeSpec} object or {@code null}.
	 */
	public abstract TypeSpec getTypeSpec();
	
	/**
	 * Returns a module reference if the underlying object is a module reference.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef} object or {@code null}.
	 */
	public abstract ModuleRef getModuleRef();
	
	/**
	 * Returns a method if the underlying object is a method.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef} object or {@code null}.
	 */
	public abstract Method getMethod();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
	

}

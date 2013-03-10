package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;

/**
 * A {@link at.pollaknet.api.facile.symtab.symbols.TypeRef} object references a type, which has
 * been defined in this or another module. If the type reference has been defined in this module
 * you can obtain the type with the {@link at.pollaknet.api.facile.symtab.symbols.TypeRef#getType()}
 * method or if it is a type specification (e.g. arrays) you can use
 * {@link at.pollaknet.api.facile.symtab.symbols.TypeRef#getTypeSpec()}.
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public interface TypeRef extends AttributableSymbol,
		FullQualifiableSymbol, Comparable<TypeRef> {

	/**
	 * Check if the type ref is a class or not.
	 * @return {@code true} if the type represents a class, otherwise {@code false}.
	 */
	public abstract boolean isClass();

	/**
	 * Check if the type ref is a value type.
	 * @return {@code true} if the type represents a class, otherwise {@code false}.
	 */
	public abstract boolean isValueType();
	
	/**
	 * Returns the ELEMENT_TYPE of the stored value as number.
	 * @return Type kind value.
	 */
	public abstract int getElementTypeKind();
	
	/**
	 * Returns the resolution scope, where the type is specified as
	 * {@link at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope}.
	 * @return The resolution scope.
	 */
	public abstract ResolutionScope getResolutionScope();

	/**
	 * Returns the method signature of the type as
	 * {@link at.pollaknet.api.facile.symtab.symbols.MethodSignature}.
	 * @return The signature of the referenced method.
	 */
	public abstract MethodSignature getMethodRefSignature();
	
	/**
	 * Returns a {@link at.pollaknet.api.facile.symtab.symbols.Type}
	 * if this type reference is even a type definition.
	 * @return A type definition or {@code null} if this is not a
	 * type definition.
	 */
	public abstract Type getType();
	
	/**
	 * Returns a {@link at.pollaknet.api.facile.symtab.symbols.TypeSpec}
	 * if this type reference is even a type specification (e.g. arrays are
	 * type specifications).
	 * @return A type specification or {@code null} if this is not a type
	 * specification.
	 */	
	public abstract TypeSpec getTypeSpec();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

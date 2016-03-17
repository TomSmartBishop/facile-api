package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.symtab.signature.ArrayShape;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;


public interface TypeSpec extends TypeRef, ResolutionScope {

	/**
	 * Build an array of all required modifier types for this type.
	 * @return An array of type specifications, which is probably empty.
	 */
	public abstract TypeSpec[] buildRequiredModifierArray();

	/**
	 * Build an array of all optional modifier types for this type.
	 * @return An array of type specifications, which is probably empty.
	 */
	public abstract TypeSpec[] buildOptionalModifierArray();
	
	/**
	 * Returns the enclosed, inner type of this type specification
	 * (If the type is an array the type of the array elements is hold there).
	 * @return A type reference or {@code null}.
	 */
	public abstract TypeRef getEnclosedTypeRef();
	
	/**
	 * Returns the most inner enclosed type reference of this type specification
	 * (If there is no enclosed type reference it will return this).
	 * @return The enclosed type reference or {@code this}.
	 */
	public abstract TypeRef getMostInnerEnclosedTypeRef();
	
	/**
	 * Returns the most inner enclosed type specification of this type specification
	 * (If there is no enclosed type specification it will return this).
	 * @return The enclosed type specification or {@code this}.
	 */
	public abstract TypeSpec getMostInnerEnclosedTypeSpec();
	
	/**
	 * ByRef means that the type is a managed pointer (this method is not 
	 * recursive - it does not check the enclosed type).
	 * @return {@code true} if the type is a managed pointer, otherwise {@code false}.
	 */
	public abstract boolean isTypeByRef();

	/**
	 * Tells you if the specified type is a single dimensional array or not.
	 * @return {@code true} if it is a single dimensional array, otherwise {@code false}.
	 */
	public abstract boolean isSingleDimensionalZeroBasedArray();

	/**
	 * Tells you if the specified type is a general array or not.
	 * @return {@code true} if it is a general array, otherwise {@code false}.
	 */
	public abstract boolean isGeneralArray();
	
	/**
	 * Tells you if the specified type is an array (general array or 
	 * single dimensional) or not.
	 * @return {@code true} if it is an array, otherwise {@code false}.
	 */
	public abstract boolean isArray();

	/**
	 * Tells you if the specified type is a class or not.
	 * @return {@code true} if it is a class, otherwise {@code false}.
	 */
	public abstract boolean isClass();

	/**
	 * Tells you if the specified type is a function pointer or not.
	 * @return {@code true} if it is a function pointer, otherwise {@code false}.
	 */
	public abstract boolean isFunctionPointer();

	/**
	 * Tells you if the specified type is a value type or not.
	 * @return {@code true} if it is a value type, otherwise {@code false}.
	 */
	public abstract boolean isValueType();


	/**
	 * Informs you, if the specified type has the 'generic' flag set.
	 * @see TypeSpec#isGenericInstance()
	 * @return {@code true} if the specified type is a generic instance, otherwise {@code false}.
	 */
	public abstract boolean isGeneric();
	
	/**
	 * Informs you, if the specified type looks like a generic instance (generic number &gt;=0): &lt;T1&gt;
	 * @return {@code true} if the specified type is a generic instance, otherwise {@code false}.
	 */
	public abstract boolean isGenericInstance();

	/**
	 * Tells you if the specified type is a pointer or not.
	 * @return {@code true} if it is a pointer, otherwise {@code false}.
	 */
	public abstract boolean isPointer();
	
	/**
	 * Pointers can be pinned the current memory location.
	 * @return {@code true} if the specified pointer is pinned, otherwise {@code false}.
	 */
	public abstract boolean isPinned();

	/**
	 * In this context basic type means that the type is a ELEMENT_TYPE.
	 * @return {@code true} if the type is a ELEMT_TYPE, otherwise {@code false}.
	 */
	public abstract boolean isBasicType();
	
	/**
	 * Boxed types always made of the type 'object' and containing
	 * another type, which you can retrieve by the method
	 * {@link TypeRef at.pollaknet.api.facile.symtab.symbols.TypeSpec#getEnclosedTypeRef()}.
	 * @return {@code true} if the specified type is 'object'
	 * and boxes (contains) another type, otherwise {@code false}.
	 */
	public abstract boolean isBoxed();
	
	/**
	 * Returns a description of the specified array if the type is a general array. 
	 * The type of the array's elements can be retrieved by calling the method 
	 * {@link TypeRef at.pollaknet.api.facile.symtab.symbols.TypeSpec#getEnclosedTypeRef()}.
	 * @return The array shape description.
	 */
	public abstract ArrayShape getArrayShape();
	
	/**
	 * Returns a MethodSignature in case this type specification
	 * contains a function pointer.
	 * @return The signature of the function pointer or {@code null}.
	 */
	public abstract MethodSignature getFunctionPointer();

	/**
	 * Returns the number of present generic parameters.
	 * @return The length of the generic parameter array.
	 */
	public abstract int getGenericParameterNumber();

    /**
     * Test if this generic parameter belongs to a method.
     * @return returns true, if this is a generic parameter belonging to a method.
     */
    public abstract boolean isGenericMethodParameter();

	/**
	 * Get the generic parameters of this type.
	 * @return An array of the generic parameters as TypeRef's.
	 */
	public abstract TypeRef[] getGenericArguments();
	
	/**
	 * The extended name function looks into the enclosed type an involves
	 * an array and generic attribute representation within the name.
	 * It will use short system name for generic encapsulated types if possible.
	 * @return The extended name without namespace.
	 */
	public abstract String getExtName();
	
	/**
	 * Involves handling of generics and array representation as getExtName, but uses
	 * everywhere full qualified names (e.g. generic parameters).
	 * @return The extended representation of the full qualified type name.
	 */
	public abstract String getExtFullQualifiedName();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

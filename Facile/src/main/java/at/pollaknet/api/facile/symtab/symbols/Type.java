package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity;

/**
 * A {@link at.pollaknet.api.facile.symtab.symbols.Type} object represents a type, which has
 * been defined in this .Net module. {@link at.pollaknet.api.facile.symtab.symbols.Type} is a
 * extension to the {@link at.pollaknet.api.facile.symtab.symbols.TypeRef} interface.
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public interface Type extends TypeRef {

	//Flags (ECMA 335 Part.II 13.1.25)
	public static final int FLAGS_VISIBILITY_BIT_MASK 					= 0x000007;
	public static final int FLAGS_LAYOUT_BIT_MASK 						= 0x000018;
	public static final int FLAGS_SEMANTICS_BIT_MASK 					= 0x0005a0;
	public static final int FLAGS_STRING_FORMAT_BIT_MASK 				= 0x030000;
	public static final int FLAGS_CUSTOM_STRING_BIT_MASK 				= 0xc00000;
	public static final int FLAGS_IMPLEMENTATION_BIT_MASK 				= 0x103000;
	
	public static final int FLAGS_VISIBILITY_PRIVATE 					= 0x00;
	public static final int FLAGS_VISIBILITY_PUBLIC 					= 0x01;
	public static final int FLAGS_VISIBILITY_NESTED_PUBLIC 				= 0x02;
	public static final int FLAGS_VISIBILITY_NESTED_PRIVATE 			= 0x03;
	public static final int FLAGS_VISIBILITY_NESTED_FAMILY 				= 0x04;
	public static final int FLAGS_VISIBILITY_NESTED_ASSEMBLY 			= 0x05;
	public static final int FLAGS_VISIBILITY_NESTED_FAMILY_AND_ASSEMBLY = 0x06;
	public static final int FLAGS_VISIBILITY_NESTED_FAMILY_OR_ASSEMBLY 	= 0x07;
	
	public static final int FLAGS_LAYOUT_AUTO_FIELDS 					= 0x00;
	public static final int FLAGS_LAYOUT_SEQUENTIAL_FIELDS				= 0x08;
	public static final int FLAGS_LAYOUT_EXPLICIT 						= 0x10;
	
	public static final int FLAGS_SEMANTICS_IS_A_CLASS 					= 0x0000;
	public static final int FLAGS_SEMANTICS_IS_AN_INTERFACE 			= 0x0020;
	public static final int FLAGS_SEMANTICS_IS_ABSTRACT 				= 0x0080;
	public static final int FLAGS_SEMANTICS_IS_SEALED 					= 0x0100;
	public static final int FLAGS_SEMANTICS_HAS_SPECIAL_NAME 			= 0x0400;
	
	public static final int FLAGS_IMPLEMENTATION_IS_IMPORTED 			= 0x001000;
	public static final int FLAGS_IMPLEMENTATION_IS_SERIALIZABLE 		= 0x002000;
	public static final int FLAGS_IMPLEMENTATION_BEFORE_FIELD_INIT 		= 0x100000;
	
	public static final int FLAGS_STRING_FORMAT_ANSI 					= 0x00000000;
	public static final int FLAGS_STRING_FORMAT_UNICODE 				= 0x00010000;
	public static final int FLAGS_STRING_FORMAT_AUTO 					= 0x00020000;
	public static final int FLAGS_STRING_FORMAT_CUSTOM 					= 0x00030000;
	
	public static final int FLAGS_RT_SPECIAL_NAME 						= 0x00000800;
	public static final int FLAGS_HAS_SECURITY 							= 0x00040000;
	
	/**
	 * Check if the type is an interface or not.
	 * @return {@code true} if the type represents an interface, otherwise {@code false}.
	 */
	public abstract boolean isInterface();

	/**
	 * Get the type's flags.
	 * @return The flags of the type as {@code long}.
	 */
	public abstract long getFlags();

	/**
	 * Returns the super class (base class) of the current type.
	 * @return The super class.
	 */
	public abstract TypeRef getExtends();
	
	/**
	 * Returns all defined fields.
	 * @return An array containing the fields.
	 */
	public abstract Field[] getFields();
	
	/**
	 * Returns all defined properties.
	 * @return An array containing the properties.
	 */
	public abstract Property[] getProperties();
	
	/**
	 * Returns all defined events.
	 * @return An array containing the events.
	 */
	public abstract Event[] getEvents();

	/**
	 * Returns all defined methods.
	 * @return An array containing the methods.
	 */
	public abstract Method[] getMethods();
	
	/**
	 * Returns information about the physical layout of the type as
	 * {@link at.pollaknet.api.facile.symtab.symbols.ClassLayout}.
	 * @return The layout of the class.
	 */
	public abstract ClassLayout getClassLayout();
	
	/**
	 * Returns an array containing all defined constants.
	 * @return The constants of the type.
	 */
	public abstract Constant[] getConstants();

	/**
	 * Informs weather the type is exported for other modules or not.
	 * @return {@code true} if the type is exported, otherwise {@code false}.
	 */
	public abstract boolean isExported();
	
	/**
	 * Informs weather the current type is already enclosed (nested) or not.
	 * @return {@code true} if the current type is already nested in another, otherwise {@code false}.
	 */
	public abstract boolean isNested();
	
	/**
	 * Returns all implemented interfaces of the type.
	 * @return An array of implementing interfaces.
	 */
	public abstract TypeRef [] getInterfaces();
	
	/**
	 * Returns enclosing (nested) classes of the current type.
	 * @return An array of all enclosing classes if present.
	 */
	public abstract Type [] getEnclosingClasses();
	
	/**
	 * Returns all generic parameters of the type.
	 * @return An array containing the generic parameters.
	 */
	public abstract Parameter[] getGenericParameters();

	/**
	 * Returns the declarative security entry for the assembly as
	 * {@link at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity} object.
	 * @return The security settings for the assembly.
	 */
	public abstract DeclarativeSecurity getDeclarativeSecurity();
	
	/**
	 * Test if the type is equal or inherited from the specified type.
	 * @param type The base type as {@link at.pollaknet.api.facile.symtab.symbols.TypeRef}.
	 * @return {@code true} if the current type is equal or inherited
	 * from the specified type, otherwise {@code false}.
	 */
	public abstract boolean isInheritedFrom(TypeRef type);
	
	/**
	 * Test if the type is equal or inherited from the specified type
	 * @param fullQualifiedName The base type as full qualified name.
	 * @return {@code true} if the current type is equal or inherited
	 * from the specified type, otherwise {@code false}.
	 */
	public abstract boolean isInheritedFrom(String fullQualifiedName);
	
	//public abstract String toExtendedString();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

}
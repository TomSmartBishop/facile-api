package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;

public interface Field extends AttributableSymbol, Comparable<Field> {

	public static final int FLAGS_VISIBILITY_BIT_MASK = 0x07;
	public static final int FLAGS_VTABLE_BIT_MASK = 0x0100;
	
	public static final int FLAGS_VISIBILITY_COMPILER_CONTROLLED = 0x00;
	public static final int FLAGS_VISIBILITY_PRIVATE = 0x01;
	public static final int FLAGS_VISIBILITY_FAMILY_AND_ASSEMBLY = 0x02;
	public static final int FLAGS_VISIBILITY_ASSEMBLY = 0x03;
	public static final int FLAGS_VISIBILITY_FAMILY = 0x04;
	public static final int FLAGS_VISIBILITY_FAMILY_OR_ASSEMBLY = 0x05;
	public static final int FLAGS_VISIBILITY_PUBLIC = 0x06;
	
	public static final int FLAGS_STATIC = 0x0010;
	public static final int FLAGS_INIT_ONLY = 0x0020;
	public static final int FLAGS_IS_LITERAL = 0x0040;
	public static final int FLAGS_REMOTING_NOT_SERIALIZED = 0x0080;
	public static final int FLAGS_SPECIAL_NAME = 0x0200;
	
	public static final int FLAGS_INTEROP_PINVOKE = 0x2000;

	public static final int FLAGS_ADDITIONAL_RT_SPECIAL_NAME = 0x0400;
	public static final int FLAGS_ADDITIONAL_HAS_FIELD_MARSHAL= 0x1000;
	public static final int FLAGS_ADDITIONAL_HAS_DEFAULT = 0x8000;
	public static final int FLAGS_ADDITIONAL_HAS_FIELD_RVA = 0x0100;
	
	/**
	 * Returns the flags of the field.
	 * @return The flags as {@code int}.
	 */
	public abstract int getFlags();

	/**
	 * Returns the name of the field.
	 * @return The name as {@code String}.
	 */
	public abstract String getName();
	
	/**
	 * Returns the type of the field.
	 * @return A type reference of the field's type.
	 */
	public abstract TypeRef getTypeRef(); 
	
	/**
	 * Returns the {@link at.pollaknet.api.facile.symtab.symbols.Constant}
	 * object of the field if the field has an assigned constant value
	 * @return A {@link at.pollaknet.api.facile.symtab.symbols.Constant}
	 * object or {@code null}.
	 */
	public abstract Constant getConstant();
	
	/**
	 * Returns the field layout if set.
	 * @return A {@link at.pollaknet.api.facile.symtab.symbols.FieldLayout}
	 * or {@code null}.
	 */
	public abstract FieldLayout getFieldLayout();

	/**
	 * Returns the relative virtual address (RVA) of the field.
	 * @return The RVA as {@code long}.
	 */
	public abstract long getRelativeVirtualAddress();
	
	/**
	 * Retruns a the native implementation object if the field has
	 * a native implementation scope.
	 * @return A {@link at.pollaknet.api.facile.symtab.symbols.NativeImplementation}
	 * or {@code null}.
	 */
	public abstract NativeImplementation getNativeImplementation();
	
	/**
	 * Returns the marshaling signature if the field marshals a native type.
	 * @return A {@link at.pollaknet.api.facile.symtab.symbols.MarshalSignature}
	 * or {@code null}.
	 */
	public abstract MarshalSignature getMarshalSignature();

	/**
	 * Get the marshal signature as binary {@code byte} buffer.
	 * @return The raw {@code byte} buffer.
	 */
	public abstract byte [] getBinaryMarshalTypeSignature();
	
	/**
	 * Get the field signature as binary {@code byte} buffer.
	 * @return The raw {@code byte} buffer.
	 */
	public abstract byte[] getBinarySignature();
	
	//public abstract String toExtendedString();
	
	/**
	 * Get the owner type definition of this field.
	 * Exceptional method since it's a backward-reference (to the parent).
	 * @return The parent as {@link at.pollaknet.api.facile.symtab.symbols.Type}.
	 */
	public abstract Type getParent();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	public abstract String getFullQualifiedName();
}
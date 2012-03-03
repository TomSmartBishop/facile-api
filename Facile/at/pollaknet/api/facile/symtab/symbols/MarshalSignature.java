package at.pollaknet.api.facile.symtab.symbols;

public interface MarshalSignature {

	public static final int NATIVE_TYPE_VOID = 0x01;
	public static final int NATIVE_TYPE_BOOLEAN = 0x02;
	public static final int NATIVE_TYPE_I1 = 0x03;
	public static final int NATIVE_TYPE_U1 = 0x04;
	public static final int NATIVE_TYPE_I2 = 0x05;
	public static final int NATIVE_TYPE_U2 = 0x06;
	public static final int NATIVE_TYPE_I4 = 0x07;
	public static final int NATIVE_TYPE_U4 = 0x08;
	public static final int NATIVE_TYPE_I8 = 0x09;
	public static final int NATIVE_TYPE_U8 = 0x0a;
	public static final int NATIVE_TYPE_R4 = 0x0b;
	public static final int NATIVE_TYPE_R8 = 0x0c;
	public static final int NATIVE_TYPE_SYSCHAR = 0x0d;
	public static final int NATIVE_TYPE_VARIANT = 0x0e;
	public static final int NATIVE_TYPE_CURRENCY = 0x0f;
	public static final int NATIVE_TYPE_PTR = 0x10;
	public static final int NATIVE_TYPE_DECIMAL = 0x11;
	public static final int NATIVE_TYPE_DATE = 0x12;
	public static final int NATIVE_TYPE_BSTR = 0x13;
	public static final int NATIVE_TYPE_LPSTR = 0x14;
	public static final int NATIVE_TYPE_LPWSTR = 0x15;
	public static final int NATIVE_TYPE_LPTSTR = 0x16;
	public static final int NATIVE_TYPE_FIXED_SYSSTRING = 0x17;
	public static final int NATIVE_TYPE_OBJECT_REF = 0x18;
	public static final int NATIVE_TYPE_IUNKNOWN = 0x19;
	public static final int NATIVE_TYPE_IDISPATCH = 0x1a;
	public static final int NATIVE_TYPE_STRUCT = 0x1b;
	public static final int NATIVE_TYPE_INTERFACE = 0x1c;
	public static final int NATIVE_TYPE_SAFE_ARRAY = 0x1d;
	public static final int NATIVE_TYPE_FIXED_ARRAY = 0x1e;
	public static final int NATIVE_TYPE_INT = 0x1f;
	public static final int NATIVE_TYPE_UINT = 0x20;
	public static final int NATIVE_TYPE_NESTED_STRUCT = 0x21;
	public static final int NATIVE_TYPE_BYVAL_STRING = 0x22;
	public static final int NATIVE_TYPE_ANSI_BSTR = 0x23;
	public static final int NATIVE_TYPE_TBSTR = 0x24;
	public static final int NATIVE_TYPE_VARIANT_BOOL = 0x25;
	public static final int NATIVE_TYPE_FUNC = 0x26;
	public static final int NATIVE_TYPE_AS_ANY = 0x28;
	public static final int NATIVE_TYPE_ARRAY = 0x2a;
	public static final int NATIVE_TYPE_LPSTRUCT = 0x2b;
	public static final int NATIVE_TYPE_CUSTOM_MARSHALER = 0x2c;
	public static final int NATIVE_TYPE_ERROR = 0x2d;
	
	public static final int NATIVE_UNKNOWN_0x2E_NI_V404 = 0x2e; //just detected in native images (NI) so far
	public static final int NATIVE_UNKNOWN_0x2F_V404 = 0x2f;
	
	/**
	 * Returns the native type number (NATIVE_TYPE_*).
	 * @return The numeric representation of the native type.
	 */
	public abstract int getNativeType();
	
	/**
	 * Returns the native type number (NATIVE_TYPE_*) of the array
	 * elements, if {@code getNativeType()} returned an array type.
	 * @return The numeric representation of the native type
	 * (of the array elements).
	 */
	public abstract int getArrayElementsNativeType();
	
	/**
	 * Checks weather it is an array or not.
	 * @return {@code true} if it is an array, otherwise {@code false}.
	 */
	public abstract boolean isArray();
	
	/**
	 * Returns the number of array elements or -1 if specified
	 * by an extra parameter called size parameter.
	 * @return The number of array elements.
	 */
	public abstract int getNumberOfArrayElements();

	/**
	 * <p/>Returns the name of a custom marshaler if used.
	 * <p/>Visit <a href="http://msdn.microsoft.com/en-us/library/d3cxf9f0.aspx">
	 * Microsoft MSDN: Custom Marshaling</a> for further information.
	 * @return The name of the custom marshaler or {@code null}.
	 */
	public abstract String getMarshalerClassName();

	/**
	 * <p/>The marshaler cookie provides additional information to the marshaler
	 * (e.g. the same marshaler could be used as wrapper, where the cookie
	 * identifies the specific wrapper type).
	 * <p/>The cookie is passed to the {@code GetInstance} method of the marshaler.
	 * <p/>Visit <a href="http://msdn.microsoft.com/en-us/library/d3cxf9f0.aspx">
	 * Microsoft MSDN: Custom Marshaling</a> for further information.
	 * @return The content of the cookie or {@code null}.
	 */
	public abstract String getMarshalerCookie();

	/**
	 * Returns the number of the parameter, which holds the
	 * size information of an array or -1 unused.
	 * @return The number of the parameter holding the size of an array.
	 */
	public abstract int getSizeParameterNumber();
	
	/**
	 * Represent a variant type as {@code String}.
	 * @param variant The numeric representation of the variant
	 * to be converted.
	 * @return A {@code String} describing the variant type.
	 */
	public abstract String variantToString(int variant);
	
	/**
	 * Represent a variant type as {@code String}.
	 * @param variant The numeric multi-byte representation
	 * of the variant to be converted.
	 * @return A {@code String} describing the variant type.
	 */
	public abstract String variantToString(int [] variant);
	
	//public abstract String toString();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

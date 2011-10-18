package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef;


public interface NativeImplementation {

	public final static int FLAGS_NO_MANGLE					= 0x0001;

	public final static int FLAGS_CHAR_SET_BITMASK			= 0x0006;

	public final static int FLAGS_CHAR_SET_NOT_SPECIFIED	= 0x0000;
	public final static int FLAGS_CHAR_SET_ANSI				= 0x0002;
	public final static int FLAGS_CHAR_SET_UNICODE			= 0x0004;
	public final static int FLAGS_CHAR_SET_AUTO				= 0x0006;
	
	public final static int FLAGS_BEST_FIT_STRINGS_ON		= 0x0010;
	public final static int FLAGS_BEST_FIT_STRINGS_OFF		= 0x0020;
	public final static int FLAGS_SUPPORTS_LAST_ERROR		= 0x0040;
	
	public final static int FLAGS_CALL_CONV_BITMASK			= 0x0700;
	
	public final static int FLAGS_CALL_CONV_WINAPI 			= 0x0100;
	public final static int FLAGS_CALL_CONV_C_DECL			= 0x0200;
	public final static int FLAGS_CALL_CONV_STDCALL	 		= 0x0300;
	public final static int FLAGS_CALL_CONV_THISCALL 		= 0x0400;
	public final static int FLAGS_CALL_CONV_FASTCALL 		= 0x0500;
	
	public final static int FLAGS_CHAR_MAP_ERROR_ON 		= 0x1000;
	public final static int FLAGS_CHAR_MAP_ERROR_OFF 		= 0x2000;
	
	public final static int FLAGS_BIT_MASK = 0x3777;
	
	/**
	 * Returns the mapping flags of the native implementation.
	 * @return The flags as {@code int}.
	 */
	public abstract int getMappingFlags();
	
	/**
	 * Returns the name of the imported symbol.
	 * @return The name of the native implementation.
	 */
	public abstract String getImportName();
	
	/**
	 * Returns the scope (a {@link at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef}
	 * ) of the implementation.
	 * @return A module reference where the implementation is defined.
	 */
	public ModuleRef getImportScope();
	
	/**
	 * Returns a field if the underlying implementation is a field.
	 * @return A {@link at.pollaknet.api.facile.symtab.symbols.Field} 
	 * object or {@code null}.
	 */
	public abstract Field getField();
	
	/**
	 * Returns a method if the underlying implementation is a method.
	 * @return A {@link at.pollaknet.api.facile.symtab.symbols.Method} 
	 * object or {@code null}.
	 */
	public abstract Method getMethod();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;

public interface Property extends AttributableSymbol, Comparable<Property> {

	public final static int FLAGS_SPECIAL_NAME 			= 0x0200;
	public final static int FLAGS_RT_SPECIAL_NAME 		= 0x0400;
	public final static int FLAGS_HAS_CONSTANT_VALUE 	= 0x1000;
	public final static int FLAGS_UNUSED 				= 0xe9ff;
	
	/**
	 * Returns the property's flags.
	 * @return Bit flags as {@code int}
	 */
	public abstract int getFlags();
	
	/**
	 * Returns the name of the property
	 * @return The full name.
	 */
	public abstract String getName();
	
	/**
	 * Returns a defined constant value for the property if defined.
	 * @return A constant value for the property.
	 */
	public abstract Constant getConstant();
	
	/**
	 * Returns the attached methods.
	 * @return The methods of the property.
	 */
	public abstract Method [] getMethods();
	
	/**
	 * Returns the property's signature as
	 * {@link at.pollaknet.api.facile.symtab.symbols.PropertySignature}.
	 * @return The property signature.
	 */
	public abstract PropertySignature getPropertySignature();
	
	/**
	 * Get the owner type definition of this property.
	 * Exceptional method since it's a backward-reference (to the parent).
	 * @return The parent as {@link at.pollaknet.api.facile.symtab.symbols.Type}.
	 */
	public Type getParent();
		
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	public abstract String getFullQualifiedName();
}

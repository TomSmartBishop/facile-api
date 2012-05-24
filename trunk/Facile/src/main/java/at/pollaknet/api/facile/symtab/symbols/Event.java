package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;


public interface Event extends AttributableSymbol, Comparable<Event> {

	public final static int FLAGS_SPECIAL_NAME = 0x0200;
	public final static int FLAGS_RT_SPECIAL_NAME = 0x0400;
	
	/**
	 * Returns the flags of the event.
	 * @return Bit flags as {@code int}
	 */
	public abstract int getFlags();
	
	/**
	 * Returns the name of the event
	 * @return The full name.
	 */
	public abstract String getName();
	
	/**
	 * Returns the type reference of the event.
	 * @return The type of the event.
	 */
	public abstract TypeRef getTypeRef();
	
	/**
	 * Returns the attached methods.
	 * @return The methods of the event.
	 */
	public abstract Method [] getMethods();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

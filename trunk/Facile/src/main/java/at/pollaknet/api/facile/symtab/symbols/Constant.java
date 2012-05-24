package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.symtab.TypeKind;


public abstract class Constant extends TypeKind {

	/**
	 * Returns the ELEMENT_TYPE of the stored value as number.
	 * @return Type kind value.
	 */
	public abstract int getElementTypeKind();
	
	/**
	 * Returns the binary stored value.
	 * @return The value as {@code byte []}.
	 */
	public abstract byte[] getValue();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	public abstract int compareTo(Constant c);
	
}

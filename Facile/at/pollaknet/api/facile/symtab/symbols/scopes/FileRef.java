package at.pollaknet.api.facile.symtab.symbols.scopes;

import at.pollaknet.api.facile.symtab.symbols.FullQualifiableSymbol;
import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;

public interface FileRef extends AttributableSymbol, FullQualifiableSymbol {

	public final static int FLAGS_CONTAINS_META_DATA = 0x00;
	public final static int FLAGS_CONTAINS_NO_META_DATA = 0x01;
	
	/**
	 * The flags for the referenced file.
	 * @return The flags as {@code long}.
	 */
	public abstract long getFlags();
	
	/**
	 * Returns the hash value of the referenced file.
	 * @return The hash value as {@code byte []}.
	 */
	public abstract byte[] getHashValue();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	int compareTo(FileRef r);
}

package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;
import at.pollaknet.api.facile.symtab.symbols.meta.Implementation;


public interface ExportedType extends FullQualifiableSymbol, AttributableSymbol {

	public static final int FLAGS_VISIBILITY_BIT_MASK = 0x03;
	
	public static final int FLAGS_VISIBILITY_PUBLIC = 0x01;
	public static final int FLAGS_VISIBILITY_NESTED_PUBLIC = 0x02;

	public static final int FLAGS_FORWARDER_TYPE = 0x00200000;
	
	/**
	 * Returns the flags of the exported type.
	 * @return The flags as {@code long}.
	 */
	public abstract long getFlags();
	
	/**
	 * Hint id (foreign Metadata Token) of the exported type
	 * in the defining module.
	 * @return The metadata token of the exported type inside the defining assembly.
	 */
	public abstract int getTypeDefId();
	
	/**
	 * Returns the object where the exported type is located (e.g. another module).
	 * @return The {@code at.pollaknet.api.facile.symtab.symbols.meta.Implementation}
	 * object, which defines the exported type.
	 */
	public abstract Implementation getImplementation();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
	
}

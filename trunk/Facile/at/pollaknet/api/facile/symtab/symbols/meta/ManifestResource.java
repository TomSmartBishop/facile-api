package at.pollaknet.api.facile.symtab.symbols.meta;



public interface ManifestResource extends AttributableSymbol {

	public static final int FLAGS_VISIBILITY_BIT_MASK = 0x03;
	
	public static final int FLAGS_VISIBILITY_PUBLIC = 0x01;
	public static final int FLAGS_VISIBILITY_PRIVATE = 0x02;
	
	/**
	 * Returns the resource as {@code byte []}. If the resource is stored
	 * in a different module is this array is {@code null}.
	 * @return The binary representation of the resource.
	 */
	public abstract byte[] getResource();
	
	/**
	 * Offset of the binary resource to the CLI resource directory.
	 * @return The offset to the resource directory.
	 */
	public abstract long getOffset();
	
	/**
	 * The flags are indicating the visibility of the resource.
	 * @return The flags as {@code long}
	 */
	public abstract long getFlags();
	
	/**
	 * Returns the name of the resource (e.g. "facile.bmp").
	 * @return The name of the resource as {@code string}.
	 */
	public abstract String getName();
	
	/**
	 * Returns the object where the resource is stored. If the resource
	 * is present at the current module this method return {@code null}.
	 * @return The {@code at.pollaknet.api.facile.symtab.symbols.meta.Implementation}
	 * object of the resource.
	 */
	public abstract Implementation getImplementation();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	int compareTo(ManifestResource r);
	
}

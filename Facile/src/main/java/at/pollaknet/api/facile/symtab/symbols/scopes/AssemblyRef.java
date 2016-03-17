package at.pollaknet.api.facile.symtab.symbols.scopes;

import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;

public interface AssemblyRef extends AttributableSymbol, ResolutionScope {
	
	public final static long FLAGS_ASSEMBLY_HOLDS_FULL_PUBLIC_KEY			= 0x0001;
	public final static long FLAGS_ASSEMBLY_IS_SIDE_BY_SIDE_COMPARABLE		= 0x0000;
	public final static long FLAGS_RESERVED									= 0x0030;
	public final static long FLAGS_ASSEMBLY_IS_RETARGETABLE					= 0x0100;
	public final static long FLAGS_JIT_COMPILE_TRACKING_ENABLE				= 0x8000;
	public final static long FLAGS_JIT_COMPILE_TRACKING_DISABLE				= 0x4000;
	
	/**
	 * Returns the major version number of the assembly reference.
	 * @return The major version number as {@code int}.
	 */
	public abstract int getMajorVersion();

	/**
	 * Returns the minor version number of the assembly reference.
	 * @return The minor version number as {@code int}.
	 */
	public abstract int getMinorVersion();

	/**
	 * Returns the build number of the assembly reference.
	 * @return The build number as {@code int}.
	 */
	public abstract int getBuildNumber();

	/**
	 * Returns the revision number of the assembly reference.
	 * @return The revision number as {@code int}.
	 */
	public abstract int getRevisionNumber();
	
	/**
	 * Returns the flags assigned to the assembly reference.
	 * @return The flags as {@code long} (only 32 bits are valid).
	 */
	public abstract long getFlags();
	
	/**
	 * Returns the public key of the assembly reference.
	 * @return The key as {@code byte []}.
	 */
	public abstract byte[] getPublicKey();
	
	/**
	 * Returns the culture string of the assembly reference. Possible values are defined in
	 * ECMA 335 revision 4 - Partition II, 23.1.3 "Values for Culture", which you can find here:
	 * <a href="http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=269&view=FitH">
	 * http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf
	 * </a>
	 * @return The culture string (e.g. "de-AT").
	 */
	public abstract String getCulture();

	/**
	 * Returns the hash value of the aessmbly's prime module.
	 * @return The hash value as {@code byte []}.
	 */
	public abstract byte [] getHashValue();
	
	/**
	 * Returns the module reference which is defined by the assembly reference.
	 * @return A module reference as {@link at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef}
	 * object.
	 */
	public abstract ModuleRef getModuleRef();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}
package at.pollaknet.api.facile.symtab.symbols.scopes;

import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;
import at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity;
import at.pollaknet.api.facile.symtab.symbols.meta.ManifestResource;
import at.pollaknet.api.facile.symtab.symbols.misc.AssemblyOs;
import at.pollaknet.api.facile.symtab.symbols.misc.AssemblyOsRef;
import at.pollaknet.api.facile.symtab.symbols.misc.AssemblyProcessor;
import at.pollaknet.api.facile.symtab.symbols.misc.AssemblyProcessorRef;

public interface Assembly extends AttributableSymbol {

	public final static long HASH_ALGORITHM_ID_NONE 						= 0x0000;
	public final static long HASH_ALGORITHM_ID_MD5 							= 0x8003;
	public final static long HASH_ALGORITHM_ID_SHA1 						= 0x8004;
	
	public final static long FLAGS_ASSEMBLY_HOLDS_FULL_PUBLIC_KEY			= 0x0001;
	public final static long FLAGS_ASSEMBLY_IS_SIDE_BY_SIDE_COMPARABLE 		= 0x0000;
	public final static long FLAGS_RESERVED									= 0x0030;
	public final static long FLAGS_ASSEMBLY_IS_RETARGETABLE					= 0x0100;
	public final static long FLAGS_JIT_COMPILE_TRACKING_ENABLE				= 0x8000;
	public final static long FLAGS_JIT_COMPILE_TRACKING_DISABLE				= 0x4000;
	
	
	/**
	 * Returns the identifier number of the hash algorithm.
	 * @return The ID of the hash algorithm as {@code long}.
	 */
	public abstract long getHasAlgorithmId();

	/**
	 * Returns the major version number of the assembly.
	 * @return The major version number as {@code int}.
	 */
	public abstract int getMajorVersion();

	/**
	 * Returns the minor version number of the assembly.
	 * @return The minor version number as {@code int}.
	 */
	public abstract int getMinorVersion();

	/**
	 * Returns the build number of the assembly.
	 * @return The build number as {@code int}.
	 */
	public abstract int getBuildNumber();

	/**
	 * Returns the revision number of the assembly.
	 * @return The revision number as {@code int}.
	 */
	public abstract int getRevisionNumber();

	/**
	 * Returns the flags assigned to the assembly.
	 * @return The flags as {@code long} (only 32 bits are valid).
	 */
	public abstract long getFlags();

	/**
	 * Returns the public key of the assembly.
	 * @return The key as {@code byte []}.
	 */
	public abstract byte[] getPublicKey();

	/**
	 * Returns the name of the assembly.
	 * @return The name of the assembly.
	 */
	public abstract String getName();

	/**
	 * Returns the culture string of the assembly. Possible values are defined in
	 * ECMA 335 revision 4 - Partition II, 23.1.3 "Values for Culture", which you can find here:
	 * <a href="http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=269&view=FitH">
	 * http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf
	 * </a>
	 * @return The culture string (e.g. "de-AT").
	 */
	public abstract String getCulture();

	/**
	 * Returns a short informal string representation of the assembly.
	 * @return A {@code string} describing the assembly.
	 */
	public abstract String toString();

	/**
	 * Returns a extended informal string representation of the assembly.
	 * @return A large {@code string} describing the whole assembly.
	 */
	public abstract String toExtendedString();
	
	/**
	 * Returns the file name of the assembly.
	 * @return The file name as {@code string}.
	 */
	public abstract String getFileName();

	/**
	 * Returns an array of all defines types inside the assembly as
	 * array of {@link at.pollaknet.api.facile.symtab.symbols.Type} objects.
	 * @return A array of all defined types.
	 */
	public abstract Type[] getAllTypes();

	/**
	 * Returns an array of all defines type references inside the assembly as
	 * array of {@link at.pollaknet.api.facile.symtab.symbols.TypeRef} objects. This array also
	 * contains {@link at.pollaknet.api.facile.symtab.symbols.TypeSpec} objects (which are derived
	 * from {@link at.pollaknet.api.facile.symtab.symbols.TypeRef})
	 * @return A array of all defined type references (including type specifications).
	 */
	public abstract TypeRef[] getAllTypeRefs();
	
	/**
	 * Returns an array of all explicit type specifications (compound types) inside the assembly as
	 * array of {@link at.pollaknet.api.facile.symtab.symbols.TypeSpec} objects (e.g. Arrays).
	 * Implicit type specifications are found inside function-, field-, ... signatures. Accessible via
	 *  the getEmbeddedTypeSpecs method.
	 *  @see Assembly#getEmbeddedTypeSpecs()
	 * @return A array of all explicit defined types.
	 */
	public abstract TypeSpec[] getAllTypeSpecs();
	
	/**
	 * Returns an array of all implicitly used type specifications found inside the signatures of the assembly as
	 * array of {@link at.pollaknet.api.facile.symtab.symbols.TypeSpec} objects (e.g. Arrays).
	 * @return A array of implicit defined types.
	 */
	public abstract TypeSpec[] getEmbeddedTypeSpecs();

	/**
	 * Returns the module which is defined in the assembly. The module is the owner
	 * of all Types and other metadata objects. There is always one module per assembly.
	 * @return A module as {@link at.pollaknet.api.facile.symtab.symbols.scopes.Module} object.
	 */
	public abstract Module getModule();

	/**
	 * Returns a list of all referenced modules inside the assembly as
	 * array of {@link at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef} objects.
	 * In case of an Assembly a module reference is a reference to a native dll component! 
	 * @return A list of all referenced Modules.
	 */
	public abstract ModuleRef[] getModuleRefs();

	/**
	 * Returns all defined resources inside the assembly as array of
	 * {@link at.pollaknet.api.facile.symtab.symbols.meta.ManifestResource}.
	 * @return The embedded resources of the assembly.
	 */
	public abstract ManifestResource[] getManifestResources();

	/**
	 * A conforming assembly never defines OSs.
	 * @return The defined Operating Systems for the assembly
	 */
	public abstract AssemblyOs[] getAssemblyOS();
	
	/**
	 * A conforming assembly never defines referenced OSs.
	 * @return Referenced Operating Systems for the assembly
	 */
	public abstract AssemblyOsRef[] getAssemblyOSRefs();

	/**
	 * A conforming assembly never defines Processors.
	 * @return The defined processors for this assembly.
	 */
	public abstract AssemblyProcessor[] getAssemblyProcessors();

	/**
	 * A conforming assembly never defines referenced Processors.
	 * @return The defined processor references for this assembly.
	 */
	public abstract AssemblyProcessorRef[] getAssemblyProcessorRefs();
	
	/**
	 * Returns an array of all referenced assemblies as
	 * {@link at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef} objects.
	 * @return The referenced assemblies.
	 */
	public abstract AssemblyRef[] getAssemblyRefs();

	/**
	 * Returns all file resources referenced by this assembly
	 * (e.g. ctype.nlp). 
	 * @return An array of referenced files as
	 * {@link at.pollaknet.api.facile.symtab.symbols.scopes.FileRef}.
	 */
	public abstract FileRef[] getFileRefs();

	/**
	 * Returns the declarative security entry for the assembly as
	 * {@link at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity} object.
	 * @return The security settings for the assembly.
	 */
	public abstract DeclarativeSecurity getDeclarativeSecurity();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
	
}

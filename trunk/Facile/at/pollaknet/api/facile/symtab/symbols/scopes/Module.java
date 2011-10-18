package at.pollaknet.api.facile.symtab.symbols.scopes;


import at.pollaknet.api.facile.symtab.symbols.ExportedType;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;

public interface Module extends ModuleRef, ResolutionScope {

	/**
	 * This value is only used in the runtime edit-and-continue mode.
	 * @return The generation value.
	 */
	public abstract int getGeneration();

	/**
	 * Returns the globally unique identifier for this module.
	 * @return The ID as array of {@code byte}.
	 */
	public abstract byte[] getMvId();

	/**
	 * The module ID for the runtime edit-and-continue mode.
	 * @return The ID as array of {@code byte}.
	 */
	public abstract byte[] getEncId();

	/**
	 * The module base ID for the runtime edit-and-continue mode.
	 * @return The base ID as array of {@code byte}.
	 */
	public abstract byte[] getEncBaseId();
	
	/**
	 * Returns all exported types as array of
	 * {@link at.pollaknet.api.facile.symtab.symbols.ExportedType}.
	 * @return A array of all exported types.
	 */
	public abstract ExportedType[] getExportedTypes();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

}
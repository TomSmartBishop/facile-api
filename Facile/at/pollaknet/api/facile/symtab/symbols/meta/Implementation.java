package at.pollaknet.api.facile.symtab.symbols.meta;

import at.pollaknet.api.facile.symtab.symbols.ExportedType;
import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.FileRef;

public interface Implementation {
	
	/**
	 * Returns the name of the implementing object.
	 * @return The name as {@code string}.
	 */
	public abstract String getName();
	
	/**
	 * Returns a file reference if the underlying implementation is a file reference.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.scopes.FileRef} object or {@code null}.
	 */
	public abstract FileRef getFileRef();
	
	/**
	 * Returns an assembly reference if the underlying implementation is an assembly reference.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef} object or {@code null}.
	 */
	public abstract AssemblyRef getAssemblyRef();
	
	/**
	 * Returns an exported type if the underlying implementation is an exported type.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.ExportedType} object or {@code null}.
	 */
	public abstract ExportedType getExportedType(); 
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

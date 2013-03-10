package at.pollaknet.api.facile.symtab.symbols.misc;

public interface AssemblyOs {
	
	/**
	 * This feature is not used in assemblies.
	 */
	public abstract long getOsPlatformId();
	
	/**
	 * This feature is not used in assemblies.
	 */
	public abstract long getOsMajorVersion();
	
	/**
	 * This feature is not used in assemblies.
	 */
	public abstract long getOsMinorVersion();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

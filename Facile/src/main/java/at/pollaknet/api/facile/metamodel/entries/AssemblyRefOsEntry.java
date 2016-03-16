package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.misc.AssemblyOsRef;

public class AssemblyRefOsEntry implements RenderableCilElement, AssemblyOsRef {

	private long osPlatformId;
	private long osMajorVersion;
	private long osMinorVersion;
	private AssemblyRefEntry assemblyRef;
	
	public long getOsPlatformId() {
		return osPlatformId;
	}
	public void setOsPlatformId(long osPlatformId) {
		this.osPlatformId = osPlatformId;
	}
	public long getOsMajorVersion() {
		return osMajorVersion;
	}
	public void setOsMajorVersion(long osMajorVersion) {
		this.osMajorVersion = osMajorVersion;
	}
	public long getOsMinorVersion() {
		return osMinorVersion;
	}
	public void setOsMinorVersion(long osMinorVersion) {
		this.osMinorVersion = osMinorVersion;
	}
	public AssemblyRefEntry getAssemblyRef() {
		return assemblyRef;
	}
	public void setAssemblyRef(AssemblyRefEntry assemblyRef) {
		this.assemblyRef = assemblyRef;
	}
	
	@Override
	public String toString() {
		return String.format("AssemblyRefOs: %s Version: %d.%d AssemblyRef: %s",
				osMajorVersion, osMinorVersion, osPlatformId, assemblyRef.getName());
	}
	@Override
	public String render(LanguageRenderer renderer) {
		return null;
	}
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + (int) (osMajorVersion ^ (osMajorVersion >>> 32));
		result = prime * result	+ (int) (osMinorVersion ^ (osMinorVersion >>> 32));
		result = prime * result + (int) (osPlatformId ^ (osPlatformId >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssemblyRefOsEntry other = (AssemblyRefOsEntry) obj;
		if (assemblyRef != other.assemblyRef)
			return false;
		if (osMajorVersion != other.osMajorVersion)
			return false;
		if (osMinorVersion != other.osMinorVersion)
			return false;
		return osPlatformId == other.osPlatformId;
	}
	
	
}

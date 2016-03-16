package at.pollaknet.api.facile.metamodel.entries;


import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.INamespaceOwner;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.NamespaceContainer;
import at.pollaknet.api.facile.symtab.symbols.ExportedType;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.symtab.symbols.meta.Implementation;
import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.FileRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.Module;
import at.pollaknet.api.facile.util.ArrayUtils;

public class AssemblyRefEntry extends AbstractAttributable implements IHasCustomAttribute,
		Implementation, ResolutionScope, AssemblyRef, INamespaceOwner {

	private int majorVersion;
	private int minorVersion;
	private int buildNumber;
	private int revisionNumber;
	private long flags;
	private byte [] publicKey;
	private String name;
	private String culture;
	private byte [] hashValue;
	
	private ModuleRefEntry moduleRefEntry;
	
	public int getMajorVersion() {
		return majorVersion;
	}
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}
	public int getMinorVersion() {
		return minorVersion;
	}
	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}
	public int getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}
	public int getRevisionNumber() {
		return revisionNumber;
	}
	public void setRevisionNumber(int revisionNumber) {
		this.revisionNumber = revisionNumber;
	}
	public long getFlags() {
		return flags;
	}
	public void setFlags(long flags) {
		this.flags = flags;
	}
	public byte[] getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getCulture() {
		return culture;
	}
	public void setCulture(String culture) {
		this.culture = culture;
	}
	public byte[] getHashValue() {
		return hashValue;
	}
	public void setHashValue(byte[] hashValue) {
		this.hashValue = hashValue;
	}
	
	@Override
	public String toString() {
		return String.format("AssemblyRef: %s %s Version: %d.%d Build: %d Revision: %d (Flags: 0x%08x) HashValue: %s PublicKey: %s",
				name, culture==null?"[neutral]":culture, majorVersion, minorVersion, buildNumber,
				revisionNumber, flags, hashValue==null?"[not set]":ArrayUtils.formatByteArray(hashValue),
				publicKey==null?"[not set]":ArrayUtils.formatByteArray(publicKey));
	}
	
	public String toExtendedString() {
		StringBuffer buffer = new StringBuffer(2048);
	
		if(getCustomAttributes()!=null) {
			for(CustomAttributeEntry c: getCustomAttributes()) {
				buffer.append(c.toExtendedString());
				buffer.append("\n");
			}
		}
		
		getModuleRef();
		
		buffer.append(toString());
		
		buffer.append("\n");
		buffer.append(moduleRefEntry.toExtendedString());

		
		return buffer.toString();
	}
	
	@Override
	public AssemblyRef getAssemblyRef() {
		return this;
	}
	@Override
	public Module getModule() {
		return null;
	}
	@Override
	public ModuleRefEntry getModuleRef() {
		if(moduleRefEntry==null) {
			moduleRefEntry = new ModuleRefEntry();
			if(name!=null && name.equals("mscorlib")) {
				moduleRefEntry.setName("CommonLanguageRuntimeLibrary");
			} else {
				moduleRefEntry.setName("Stub [Module]");
			}
		}
		return moduleRefEntry;
	}
	@Override
	public TypeRef getTypeRef() {
		return null;
	}
	@Override
	public FileRef getFileRef() {
		return null;
	}
	
	@Override
	public void setNamespaces(NamespaceContainer[] namespaces) {
		getModuleRef().setNamespaces(namespaces);
	}
	
	@Override
	public boolean isInAssembly() {
		return false;
	}
	
	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}
	@Override
	public String getFullQualifiedName() {
		return name;
	}
	@Override
	public String getNamespace() {
		return null;
	}
	@Override
	public String getShortSystemName() {
		return null;
	}
	@Override
	public ExportedType getExportedType() {
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (flags ^ (flags >>> 32));
		result = prime * result + majorVersion;
		result = prime * result + minorVersion;
		result = prime * result + revisionNumber;
		result = prime * result + buildNumber;
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
		AssemblyRefEntry other = (AssemblyRefEntry) obj;
		if (buildNumber != other.buildNumber)
			return false;
		if (culture == null) {
			if (other.culture != null)
				return false;
		} else if (!culture.equals(other.culture))
			return false;
		if (flags != other.flags)
			return false;
		if (!Arrays.equals(hashValue, other.hashValue))
			return false;
		if (majorVersion != other.majorVersion)
			return false;
		if (minorVersion != other.minorVersion)
			return false;
		//avoid back-tracking
		if (moduleRefEntry != other.moduleRefEntry)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(publicKey, other.publicKey))
			return false;
		return revisionNumber == other.revisionNumber;
	}

}

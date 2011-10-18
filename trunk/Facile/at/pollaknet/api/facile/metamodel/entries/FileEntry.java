package at.pollaknet.api.facile.metamodel.entries;


import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.ExportedType;
import at.pollaknet.api.facile.symtab.symbols.meta.Implementation;
import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.FileRef;
import at.pollaknet.api.facile.util.ArrayUtils;

public class FileEntry extends AbstractAttributable implements IHasCustomAttribute,
		Implementation, FileRef {

	private long flags;
	private String name;
	private byte [] hashValue;
	
	public long getFlags() {
		return flags;
	}
	public void setFlags(long flags) {
		this.flags = flags;
	}
	public String getName() {
		return name;
	}
	@Override
	public String getFullQualifiedName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getHashValue() {
		return hashValue;
	}
	public void setHashValue(byte[] hashValue) {
		this.hashValue = hashValue;
	}
	
	@Override
	public String toString() {
		return String.format("File: %s (Flags: 0x%08x) HashValue: %s",
				name, flags, hashValue==null?"[not set]":ArrayUtils.formatByteArray(hashValue));
	}

	@Override
	public AssemblyRef getAssemblyRef() {
		return null;
	}
	@Override
	public FileRef getFileRef() {
		return this;
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
		int result = super.hashCode();
		result = prime * result + (int) (flags ^ (flags >>> 32));
		result = prime * result + Arrays.hashCode(hashValue);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileEntry other = (FileEntry) obj;
		if (flags != other.flags)
			return false;
		if (!Arrays.equals(hashValue, other.hashValue))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(FileRef r) {
		return ArrayUtils.compareStrings(r.getName(), getName());
	}
}

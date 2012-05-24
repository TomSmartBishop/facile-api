package at.pollaknet.api.facile.metamodel.entries;


import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.meta.Implementation;
import at.pollaknet.api.facile.symtab.symbols.meta.ManifestResource;
import at.pollaknet.api.facile.util.ArrayUtils;

public class ManifestResourceEntry extends AbstractAttributable implements 
		IHasCustomAttribute, ManifestResource {
	
	private long offset;
	private long flags;
	private String name;
	private Implementation implementation;
	private byte [] resource = null;

	public byte[] getResource() {
		return resource;
	}
	
	public void setResource(byte[] resource) {
		this.resource = resource;
	}
	
	public long getOffset() {
		return offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
	public long getFlags() {
		return flags;
	}
	public void setFlags(long flags) {
		this.flags = flags;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Implementation getImplementation() {
		return implementation;
	}
	public void setImplementation(Implementation implementation) {
		this.implementation = implementation;
	}
	
	@Override
	public String toString() {
		return String.format("ManifestResource: %s (Flags: 0x%08x Offset: 0x%x) Implementation: %s",
				name, flags, offset,
				implementation==null?"[not set]":implementation.getName());
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (flags ^ (flags >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(resource);
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
		ManifestResourceEntry other = (ManifestResourceEntry) obj;
		if (flags != other.flags)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		//ignore the offset
		//if (offset != other.offset)
		//	return false;
		
		if (!Arrays.equals(resource, other.resource))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(ManifestResource r) {
		return ArrayUtils.compareStrings(r.getName(), getName());
	}
}

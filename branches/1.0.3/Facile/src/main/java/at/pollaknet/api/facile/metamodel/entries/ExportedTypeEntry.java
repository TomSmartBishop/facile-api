package at.pollaknet.api.facile.metamodel.entries;


import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.ExportedType;
import at.pollaknet.api.facile.symtab.symbols.meta.Implementation;
import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.FileRef;
import at.pollaknet.api.facile.util.ArrayUtils;

public class ExportedTypeEntry extends AbstractAttributable
		implements ExportedType, IHasCustomAttribute, Implementation, Comparable<ExportedType> {

	private long flags;
	private int typeDefId;
	private String typeName;
	private String typeNamespace;
	private Implementation implementation;
	
	public long getFlags() {
		return flags;
	}
	public void setFlags(long flags) {
		this.flags = flags;
	}
	
	public int getTypeDefId() {
		return typeDefId;
	}
	
	public void setTypeDefId(int typeDefId) {
		this.typeDefId = typeDefId;
	}
	
	public String getName() {
		return typeName;
	}
	public void setName(String typeName) {
		this.typeName = typeName;
	}
	
	public String getNamespace() {
		return typeNamespace;
	}
	public void setNamespace(String typeNamespace) {
		this.typeNamespace = typeNamespace;
	}
	
	public Implementation getImplementation() {
		return implementation;
	}
	public void setImplementation(Implementation implementation) {
		this.implementation = implementation;
	}
	
	@Override
	public String toString() {
		return String.format("ExportedType: %s BackupHint (TypeDefId): 0x%04x Implementation: %s (Flags: 0x%08x)",
				getFullQualifiedName(), typeDefId, implementation.getName(), flags);
	}
	
	public String getFullQualifiedName() {
		if(typeNamespace==null)
			return typeName;
		
		return typeNamespace + "." + typeName;
	}
	
	@Override
	public AssemblyRef getAssemblyRef() {
		return null;
	}
	@Override
	public FileRef getFileRef() {
		return null;
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
	public String getShortSystemName() {
		return null;
	}

	@Override
	public ExportedType getExportedType() {
		return this;
	}
	@Override
	public int compareTo(ExportedType o) {
		return ArrayUtils.compareStrings(o.getFullQualifiedName(),  getFullQualifiedName());
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (flags ^ (flags >>> 32));
		result = prime * result	+ ((typeName == null) ? 0 : typeName.hashCode());
		return prime * result	+ ((typeNamespace == null) ? 0 : typeNamespace.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportedTypeEntry other = (ExportedTypeEntry) obj;
		if (flags != other.flags)
			return false;
		
		//ignore the typedef id - it is only a hint
		//		if (typeDefId != other.typeDefId)
		//			return false;
		
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		if (typeNamespace == null) {
			if (other.typeNamespace != null)
				return false;
		} else if (!typeNamespace.equals(other.typeNamespace))
			return false;
		return true;
	}
	
	
}

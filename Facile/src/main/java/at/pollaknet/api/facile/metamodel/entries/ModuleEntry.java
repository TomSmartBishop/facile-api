package at.pollaknet.api.facile.metamodel.entries;


import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.INamespaceOwner;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.NamespaceContainer;
import at.pollaknet.api.facile.symtab.symbols.ExportedType;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.Module;
import at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef;
import at.pollaknet.api.facile.util.ArrayUtils;

public class ModuleEntry extends AbstractAttributable
		implements IHasCustomAttribute, ResolutionScope, INamespaceOwner, Module {

	private String name;
	private int generation;
	
	private byte [] mvId;
	private byte [] encId;
	private byte [] encBaseId;
	private NamespaceContainer[] namespaces;
	
	private ExportedTypeEntry[] exportedTypes;
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Module#getName()
	 */
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

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Module#getGeneration()
	 */
	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Module#getMvId()
	 */
	public byte [] getMvId() {
		return mvId;
	}

	public void setMvId(byte [] mvId) {
		this.mvId = mvId;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Module#getEncId()
	 */
	public byte [] getEncId() {
		return encId;
	}

	public void setEncId(byte [] encId) {
		this.encId = encId;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Module#getEncBaseId()
	 */
	public byte [] getEncBaseId() {
		return encBaseId;
	}

	public void setEncBaseId(byte [] encBaseId) {
		this.encBaseId = encBaseId;
	}

	@Override
	public String toString() {
		return String.format("Module: %s Generation: %d MvId: %s EncId: %s EncBaseId: %s",
				name, generation, ArrayUtils.formatByteArray(mvId),
				encId==null?"[not set]":ArrayUtils.formatByteArray(encId),
				encBaseId==null?"[not set]":ArrayUtils.formatByteArray(encBaseId)	);
	}

	public String toExtendedString() {
		StringBuffer buffer = new StringBuffer();
		
		if(getCustomAttributes()!=null) {
			for(CustomAttributeEntry c: getCustomAttributes()) {
				buffer.append(c.toExtendedString());
				buffer.append("\n");
			}
		}
		
		buffer.append("Module: ");
		buffer.append(name);
		buffer.append(" Generation: ");
		buffer.append(generation);
		buffer.append(" MvId: ");
		buffer.append(mvId==null?"[not set]":ArrayUtils.formatByteArray(mvId));
		buffer.append(" EncId: ");
		buffer.append(encId==null?"[not set]":ArrayUtils.formatByteArray(encId));
		buffer.append(" EncBaseId: ");
		buffer.append(encBaseId==null?"[not set]":ArrayUtils.formatByteArray(encBaseId));
		
		buffer.append("\n\tExportedTypes:");
		for(ExportedTypeEntry t: exportedTypes) {
			if(t.getCustomAttributes()!=null) {
				for(CustomAttributeEntry c: t.getCustomAttributes()) {
					buffer.append(c.toExtendedString());
					buffer.append("\n\t\t");
				}
			}
			buffer.append("\n\t\t");
			buffer.append(t.toString());
		}
		
		return buffer.toString();
	}


	
	@Override
	public AssemblyRef getAssemblyRef() {
		return null;
	}

	@Override
	public Module getModule() {
		return this;
	}

	@Override
	public ModuleRef getModuleRef() {
		return this;
	}

	@Override
	public TypeRef getTypeRef() {
		return null;
	}

	@Override
	public void setNamespaces(NamespaceContainer[] namespaces) {
		this.namespaces = namespaces;
	}

	public Namespace[] getNamespaces() {
		return namespaces;
	}

	@Override
	public boolean isInAssembly() {
		return true;
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


	public void setExportedTypes(ExportedTypeEntry[] exportedTypes) {
		this.exportedTypes = exportedTypes;
	}
	
	@Override
	public ExportedType [] getExportedTypes() {
		if(exportedTypes==null) return new ExportedType[0];
		return exportedTypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(mvId);
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
		ModuleEntry other = (ModuleEntry) obj;
		if (!Arrays.equals(encBaseId, other.encBaseId))
			return false;
		if (!Arrays.equals(encId, other.encId))
			return false;
		if (!Arrays.equals(exportedTypes, other.exportedTypes))
			return false;
		if (generation != other.generation)
			return false;
		if (!Arrays.equals(mvId, other.mvId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return Arrays.equals(namespaces, other.namespaces);
	}

	@Override
	public int compareTo(ModuleRef m) {
		if(m == null || m.getClass()!=getClass()) {
			return Integer.MAX_VALUE;
		}
	
		return ArrayUtils.compareStrings(m.getFullQualifiedName(), getFullQualifiedName());
	}


}

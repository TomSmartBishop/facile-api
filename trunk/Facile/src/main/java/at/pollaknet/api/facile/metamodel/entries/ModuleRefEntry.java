package at.pollaknet.api.facile.metamodel.entries;

import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.AbstractMethodRefSignature;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.INamespaceOwner;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.NamespaceContainer;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.Module;
import at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef;
import at.pollaknet.api.facile.util.ArrayUtils;

public class ModuleRefEntry extends AbstractMethodRefSignature implements IHasCustomAttribute,
		MethodAndFieldParent, ResolutionScope, INamespaceOwner, ModuleRef {

	private String name;
	private NamespaceContainer[] namespaces;

	public String getName() {
		if(name==null) return "Stub [this]";
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "ModuleRef: " + name;
	}
	
	public String toExtendedString() {
		StringBuffer buffer = new StringBuffer(64);
		buffer.append("ModuleRef: ");
		buffer.append(name);
		
		if(signature!=null) {
			buffer.append("\n\t");
			buffer.append(signature.toString());
		}
	
		if(namespaces!=null) {
			for(NamespaceContainer n: namespaces) {
				buffer.append("\n\t");
				buffer.append(n.toString());
			}
		}
		
		return buffer.toString();
	}

	@Override
	public String getFullQualifiedName() {
		return getName();
	}

	@Override
	public AssemblyRef getAssemblyRef() {
		return null;
	}

	@Override
	public Module getModule() {
		return null;
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
	public Method getMethod() {
		return null;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public TypeSpec getTypeSpec() {
		return null;
	}
	
	@Override
	public void setNamespaces(NamespaceContainer[] namespaces) {
		this.namespaces = namespaces;
	}
	
	public NamespaceContainer[] getNamespaces() {
		if(namespaces==null) return new NamespaceContainer[0];
		return namespaces;
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
	public String getNamespace() {
		return null;
	}

	@Override
	public String getShortSystemName() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		ModuleRefEntry other = (ModuleRefEntry) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(namespaces, other.namespaces))
			return false;
		return true;
	}

	@Override
	public int compareTo(ModuleRef o) {
		return ArrayUtils.compareStrings(o.getFullQualifiedName(), getFullQualifiedName());
	}
}

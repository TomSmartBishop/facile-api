package at.pollaknet.api.facile.metamodel.entries;

import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.AbstractMethodRefSignature;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ICustomAttributeType;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.INamespaceOwner;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeDefOrRef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.NamespaceContainer;
import at.pollaknet.api.facile.symtab.TypeKind;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;
import at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;
import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.Module;
import at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef;
import at.pollaknet.api.facile.util.ArrayUtils;

public class TypeRefEntry extends AbstractMethodRefSignature
		implements ResolutionScope, INamespaceOwner, ITypeDefOrRef,
			IHasCustomAttribute, MethodAndFieldParent, TypeRef, AttributableSymbol, ICustomAttributeType {

	protected ResolutionScope resolutionScope;
	
	protected String name = null;
	protected String namespace = null;
	protected String shortName = null;

	private int kind;
	protected char namespaceSeperator = '.';

	private NamespaceContainer[] namespaces = new NamespaceContainer [0];

	public TypeRefEntry() {
	}
	
	public TypeRefEntry(ResolutionScope systemScope, String name) {
		setName(name);
		setNamespace("System");
		setResolutionScope(systemScope);
	}

	public TypeRefEntry(ResolutionScope systemScope, String name, String shortName) {
		setName(name);
		setNamespace("System");
		setResolutionScope(systemScope);
		setShortSystemName(shortName);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		assert(this.name==null);
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String nameSpace) {
		this.namespace = nameSpace;
	}
	
	public void setResolutionScope(ResolutionScope resolutionScope) {
		this.resolutionScope = resolutionScope;
	}

	public ResolutionScope getResolutionScope() {
		return resolutionScope;
	}

	public String getFullQualifiedName() {
		if(namespace==null || namespace.equals("")) return name;
		
		return namespace + namespaceSeperator + name;
	}

	@Override
	public String toString() {
		return String.format("TypeRef: %s ResolutionScope: %s",
			getFullQualifiedName(), getResolutionScope()!=null?getResolutionScope().getName():"[not set]");
	}

	@Override
	public TypeDefEntry getType() {
		return null;
	}

	@Override
	public TypeRefEntry getTypeRef() {
		return this;
	}

	@Override
	public AssemblyRef getAssemblyRef() {
		ResolutionScope resolutionScope = getResolutionScope();
		
		//some obfuscated assemblies have loops
		return (resolutionScope==null||resolutionScope==this)?null:resolutionScope.getAssemblyRef();
	}

	@Override
	public Module getModule() {
		ResolutionScope resolutionScope = getResolutionScope();
		
		//some obfuscated assemblies have loops
		return (resolutionScope==null||resolutionScope==this)?null:resolutionScope.getModule();
	}

	@Override
	public ModuleRef getModuleRef() {
		ResolutionScope resolutionScope = getResolutionScope();
		
		//some obfuscated assemblies have loops
		return (resolutionScope==null||resolutionScope==this)?null:resolutionScope.getModuleRef();
	}

	@Override
	public MethodDefEntry getMethod() {
		return null;
	}
	
	@Override
	public MemberRefEntry getMemberRef() {
		return null;
	}

	@Override
	public TypeSpecEntry getTypeSpec() {
		return null;
	}

	@Override
	public int getElementTypeKind() {
		return kind;
	}
	
	public void setElementKind(int kind) {
		this.kind = kind;
	}
	
	public void setNamespaces(NamespaceContainer[] namespaces) {
		this.namespaces = namespaces;
	}
	
	public NamespaceContainer[] getNamespaces() {
		return namespaces;
	}

	@Override
	public boolean isInAssembly() {
		if(resolutionScope==null)
			return true;
		//TODO: Check if the resolution scope returns an appropriate value.
		return resolutionScope.isInAssembly();
	}

	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}

	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return renderer.renderAsReference(this);
	}

	public void setShortSystemName(String shortName) {
		this.shortName = shortName;
	}
	
	@Override 
	public String getShortSystemName() {
		return shortName;
	}

	@Override
	public int compareTo(TypeRef other) {	
		return ArrayUtils.compareStrings(other.getFullQualifiedName(), getFullQualifiedName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime + kind;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result //do not use resolutionScope.hashCode since this could lead to a recursion
				+ ((resolutionScope == null) ? 0 : resolutionScope.getName() == null ? 0 : resolutionScope.getName().hashCode());
		
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
		TypeRefEntry other = (TypeRefEntry) obj;
		if (kind != other.kind)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		if (!Arrays.equals(namespaces, other.namespaces))
			return false;
		if (resolutionScope == null) {
			return other.resolutionScope == null;
		} else if(other.resolutionScope == null) {
			return false;
		}
		if(resolutionScope.getName()!=null)
			return resolutionScope.getName().equals(other.resolutionScope.getName());
		
		return true;
	}

	@Override
	public boolean isClass() {
		return !ArrayUtils.contains(TypeKind.NUMERIC_TYPES, kind) && !isValueType();
	}

	@Override
	public boolean isValueType() {
		return kind==TypeKind.ELEMENT_TYPE_VALUETYPE || (getType()!=null && getType().isInheritedFrom("System.ValueType"));
	}
	
	public void adjustNamespace(ModuleRef moduleRef) {
		adjustNamespace(this, moduleRef);
	}
	
	protected void adjustNamespace(TypeRefEntry typeRef, ModuleRef moduleRef) {
			
		if(	typeRef.getResolutionScope()==null)
			return;
		
		ModuleRef parentModule = typeRef.getResolutionScope().getModule();
		
		if(parentModule==null || parentModule.getFullQualifiedName()!=moduleRef.getFullQualifiedName() )
			return;
		
		//check all name spaces to determinate where to replace '.' with '/'
		String enclosedFullQualifiedName = typeRef.getFullQualifiedName();
		String enclosedNamespace = typeRef.getNamespace();

		Namespace [] definedNamespaces = parentModule.getNamespaces();
		if(enclosedNamespace!=null && definedNamespaces!=null && definedNamespaces.length>0) {
			int longestMatch = -1;
			for(int i=0;i<definedNamespaces.length;i++) {
				//perfect match
				if( definedNamespaces[i].getNamespace()==enclosedNamespace) {
					return;
				} else if( definedNamespaces[i].isSuperNamespace(enclosedNamespace) &&
					( longestMatch<0 || definedNamespaces[longestMatch].getAddress().length<definedNamespaces[i].getAddress().length) ) {
						longestMatch = i;
				}
			}
			
			if(longestMatch>0) {
				
				int lengthWithDot = definedNamespaces[longestMatch].getNamespace().length()+1;
				if(enclosedFullQualifiedName.length()>lengthWithDot) {
					String [] nameParts = enclosedFullQualifiedName.split("\\.|\\/");
					
					String [] currentAddress = definedNamespaces[longestMatch].getAddress();
					
					String modifiedNamespace = definedNamespaces[longestMatch].getNamespace();
					
					for(int i=currentAddress.length;i<nameParts.length-1;i++)
						if(i==currentAddress.length)
							modifiedNamespace += '.' + nameParts[i];
						else
							modifiedNamespace += '/' + nameParts[i];
					
					typeRef.setNamespace(modifiedNamespace);
				}
				namespaceSeperator = '/';
				
			}
		}
	}

}
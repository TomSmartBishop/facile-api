package at.pollaknet.api.facile.symtab;

import java.util.Arrays;

import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace;
import at.pollaknet.api.facile.util.ArrayUtils;

/**
 * A name space container holds all types which are defined in the
 * name space. Use the {@link at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace}
 * interface where it is possible.
 */
public class NamespaceContainer implements Namespace {

	//the name of the name space
	private String name = null;
	
	//the full qualified name
	private String fullQualifiedName = null;
	
	//all types which are contained in this name space
	private TypeRef [] typeRefs = null;
	
	//the address of the name space (e.g. ["System","Windows","Forms"] )
	private String[] address = null;
	
	/**
	 * Create a new name space container with the given full qualified name.
	 * @param fullQualifiedName The full qualified name of the name space.
	 * @param typeRefs All types of the name space.
	 */
	NamespaceContainer(String fullQualifiedName, TypeRef[] typeRefs) {
		assert(fullQualifiedName!=null);
		int dotPosition = fullQualifiedName.lastIndexOf('.');
		this.fullQualifiedName = fullQualifiedName;
		
		this.name = dotPosition<0?fullQualifiedName:fullQualifiedName.substring(dotPosition+1);
		
		address = fullQualifiedName.split("\\.");
		
		this.typeRefs = typeRefs;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.QualifiableSymbol#getFullQualifiedName()
	 */
	@Override
	public String getFullQualifiedName() {
		return fullQualifiedName;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.QualifiableSymbol#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace#getAddress()
	 */
	public String[] getAddress() {
		return address;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace#getTypeRefs()
	 */
	public TypeRef[] getTypeRefs() {
		return typeRefs;
	}	
	
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace#isSubNamespace(at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace)
	 */
	public boolean isSubNamespace(Namespace namespace) {
		int supLength = namespace.getAddress().length;
		int subLength = address.length;
		
		if(subLength <= supLength) return false;
		
		String [] superAddress = namespace.getAddress();
		for(int level=0;level<supLength;level++) {
			if(!address[level].equals(superAddress[level])) {
				return false;
			}
		}
		
		return true;
	}
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace#isSubNamespace(java.lang.String)
	 */
	@Override
	public boolean isSubNamespace(String namespace) {
		String [] superAddress = namespace.split("\\.");
		
		if(superAddress==null||superAddress.length==0)
			return false;
		
		int supLength = superAddress.length;
		int subLength = address.length;
		
		if(subLength <= supLength) return false;
		
		for(int level=0;level<supLength;level++) {
			if(!address[level].equals(superAddress[level])) {
				return false;
			}
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace#isSuperNamespace(at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace)
	 */
	@Override
	public boolean isSuperNamespace(Namespace namespace) {
		return namespace.isSubNamespace(this);
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace#isSuperNamespace(java.lang.String)
	 */
	@Override
	public boolean isSuperNamespace(String namespace) {
		String [] subAddress = namespace.split("\\.");
		
		if(subAddress==null||subAddress.length==0)
			return false;
		
		int subLength = subAddress.length;
		int supLength = address.length;
		
		if(supLength >= subLength) return false;
		
		for(int level=0;level<supLength;level++) {
			if(!address[level].equals(subAddress[level])) {
				return false;
			}
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.QualifiableSymbol#getNamespace()
	 */
	@Override
	public String getNamespace() {
		return fullQualifiedName;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.QualifiableSymbol#getShortSystemName()
	 */
	@Override
	public String getShortSystemName() {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.metamodel.RenderableCilElement#render(at.pollaknet.api.facile.renderer.LanguageRenderer)
	 */
	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.metamodel.RenderableCilElement#renderAsReference(at.pollaknet.api.facile.renderer.LanguageRenderer)
	 */
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		//not renderable as reference
		return null;
	}
	
	@Override
	public String toString() {
		return "Namespace: " + (fullQualifiedName!=null?fullQualifiedName:"[no name set]");
	}


	@Override
	public int compareTo(Namespace namespace) {
		return ArrayUtils.compareStrings(namespace.getFullQualifiedName(),  fullQualifiedName);
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result =  prime + ((fullQualifiedName == null) ? 0 : fullQualifiedName.hashCode());
		result = prime * result + Arrays.hashCode(typeRefs);
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
		NamespaceContainer other = (NamespaceContainer) obj;
		if (fullQualifiedName == null) {
			if (other.fullQualifiedName != null)
				return false;
		} else if (!fullQualifiedName.equals(other.fullQualifiedName))
			return false;		
		
		if (!Arrays.equals(typeRefs, other.typeRefs)){
			//use this code for debugging
//			for(int i=0;i<typeRefs.length;i++) {
//				if(!typeRefs[i].equals(other.typeRefs[i])) {
//					typeRefs[i].equals(other.typeRefs[i]); //insert break point here
//				}
//			}
			return false;
		}
		return true;
	}

}

package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.AbstractGenericInstanceContainer;
import at.pollaknet.api.facile.metamodel.ITypeSpecInstances;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ICustomAttributeType;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IMethodDefOrRef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.MemberRef;
import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;
import at.pollaknet.api.facile.util.ArrayUtils;

public class MemberRefEntry extends AbstractGenericInstanceContainer
		implements IHasCustomAttribute, IMethodDefOrRef, ICustomAttributeType,
			MemberRef, ITypeSpecInstances {

	private MethodAndFieldParent ownerClass;
	private String name;
	private byte [] signature;
	private TypeRefEntry typeRef;
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.MemberRef#getOwnerClass()
	 */
	public MethodAndFieldParent getOwner() {
		return ownerClass;
	}
	public void setOwnerClass(MethodAndFieldParent ownerClass) {
		this.ownerClass = ownerClass;
	}
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.MemberRef#getName()
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.MemberRef#getBinarySignature()
	 */
	public byte[] getBinarySignature() {
		return signature;
	}
	public void setBinarySignature(byte[] signature) {
		this.signature = signature;
	}
	
	@Override
	public String toString() {
		return String.format("MemberRef: %s Owner: %s Signature: %s",
				name, ownerClass==null?"[not set]":ownerClass.getName(),
				signature==null?"[not set]":ArrayUtils.formatByteArray(signature));
	}
	@Override
	public MemberRefEntry getMemberRef() {
		return this;
	}
	@Override
	public MethodDefEntry getMethod() {
		return null;
	}
	@Override
	public void setEnclosedTypeRef(TypeRefEntry typeRef) {
		this.typeRef = typeRef;
	}
	
	public TypeRefEntry getTypeRef() {
		return typeRef;
	}
	
	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}
	
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return renderer.renderAsReference(this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((typeRef == null) ? 0 : typeRef.hashCode());
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
		MemberRefEntry other = (MemberRefEntry) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (typeRef == null) {
			if (other.typeRef != null)
				return false;
		} else if (!typeRef.equals(other.typeRef))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(MemberRef o) {
		return ArrayUtils.compareStrings(o.getName(), getName());
	}

}

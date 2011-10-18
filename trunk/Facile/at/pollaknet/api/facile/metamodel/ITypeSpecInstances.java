package at.pollaknet.api.facile.metamodel;

import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;

public interface ITypeSpecInstances {

	public abstract byte [] getBinarySignature();
		
	public abstract void setEnclosedTypeRef(TypeRefEntry typeRef);
	
	public abstract TypeRefEntry getTypeRef();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

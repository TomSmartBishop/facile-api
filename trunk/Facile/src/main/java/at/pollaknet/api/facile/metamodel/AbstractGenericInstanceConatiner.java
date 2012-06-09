package at.pollaknet.api.facile.metamodel;

import java.util.ArrayList;

import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;


public abstract class AbstractGenericInstanceConatiner extends AbstractMethodRefSignature {

	private ArrayList<TypeSpecEntry> typeSpecInstances = new ArrayList<TypeSpecEntry>(1);

	public void addGenericInstance(TypeSpecEntry typeSpec) {
		assert(typeSpec!=null);
		typeSpecInstances.add(typeSpec);
	}

	public TypeSpec[] getGenericInstances() {
		if(typeSpecInstances==null || typeSpecInstances.size()==0) return new TypeSpec[0];
		
		//do not care about the sort order of the entries!
		return typeSpecInstances.toArray(new TypeSpec[0]);

	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractGenericInstanceConatiner other = (AbstractGenericInstanceConatiner) obj;
		if (typeSpecInstances == null) {
			if (other.typeSpecInstances != null)
				return false;
		} else if (!typeSpecInstances.equals(other.typeSpecInstances))
			return false;
		return true;
	}

}

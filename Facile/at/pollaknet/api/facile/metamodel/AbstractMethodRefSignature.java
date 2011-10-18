package at.pollaknet.api.facile.metamodel;

import at.pollaknet.api.facile.symtab.symbols.MethodSignature;

public abstract class AbstractMethodRefSignature extends AbstractAttributable {

	protected MethodSignature signature=null;
	
	public MethodSignature getMethodRefSignature() {
		return signature;
	}

	public void setMethodRefSignature(MethodSignature signature) {
		assert(this.signature==null) : "Method signature set twice!";
		this.signature = signature;
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
		AbstractMethodRefSignature other = (AbstractMethodRefSignature) obj;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		return true;
	}
}

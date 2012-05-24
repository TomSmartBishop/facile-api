package at.pollaknet.api.facile.metamodel.entries;


import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.ITypeSpecInstances;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.MethodSignature;
import at.pollaknet.api.facile.util.ArrayUtils;

public class StandAloneSigEntry extends AbstractAttributable
		implements IHasCustomAttribute, ITypeSpecInstances {

	private byte [] signature;
	private MethodSignature methodSignature = null;
	private TypeRefEntry typeRef;

	public byte[] getBinarySignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
	@Override
	public String toString() {
		return "StandAloneSignature: " + getName();
	}

	@Override
	public String getName() {
		if(signature==null)
			return "[SIG]: null";
		return "[SIG]: " + ArrayUtils.formatByteArray(signature);
	}

	public void setMethodSignature(MethodSignature methodSignature) {
		this.methodSignature = methodSignature;
	}

	public MethodSignature getMethodSignature() {
		return methodSignature;
	}

	public void setEnclosedTypeRef(TypeRefEntry typeRef) {
		this.typeRef = typeRef;
	}

	public TypeRefEntry getTypeRef() {
		return typeRef;
	}

	@Override
	public String render(LanguageRenderer renderer) {
		return null;
	}

	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return renderer.renderAsReference(this);
	}
}

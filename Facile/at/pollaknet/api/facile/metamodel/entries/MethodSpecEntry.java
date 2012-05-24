package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IMethodDefOrRef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ArrayUtils;

public class MethodSpecEntry extends AbstractAttributable
		implements RenderableCilElement, IHasCustomAttribute {

	private IMethodDefOrRef method;
	private byte [] instantiation;
	
	public IMethodDefOrRef getMethod() {
		return method;
	}
	public void setMethod(IMethodDefOrRef method) {
		this.method = method;
	}
	public byte[] getInstantiation() {
		return instantiation;
	}
	public void setInstantiation(byte[] instantiation) {
		this.instantiation = instantiation;
	}
	
	@Override
	public String getName() {
		return method.getName();
	}
	
	@Override
	public String toString() {
		return "MethodSpec: " + method.getName() + " Instantiation: " +
			(instantiation==null?"[not set]": ArrayUtils.formatByteArray(instantiation));
	}
	@Override
	public String render(LanguageRenderer renderer) {
		return null;
	}
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		//TODO: Implement method spec rendering!
		//return renderer.renderAsReference(this);
		return null;
	}

}

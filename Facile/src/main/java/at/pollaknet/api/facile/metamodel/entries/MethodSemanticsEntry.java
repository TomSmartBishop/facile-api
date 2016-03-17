package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasSemantics;
import at.pollaknet.api.facile.renderer.LanguageRenderer;

public class MethodSemanticsEntry implements RenderableCilElement {

	private int semantics;
	private MethodDefEntry method;
	private IHasSemantics association;
	
	public int getSemantics() {
		return semantics;
	}
	public void setSemantics(int semantics) {
		this.semantics = semantics;
	}
	public MethodDefEntry getMethod() {
		return method;
	}
	public void setMethod(MethodDefEntry method) {
		this.method = method;
	}
	public IHasSemantics getAssociation() {
		return association;
	}
	public void setAssociation(IHasSemantics association) {
		this.association = association;
	}
	
	
	@Override
	public String toString() {
		return String.format("MethodSemantics: %s (Semantics: 0x%x) Association: %s",
				method.getName(), semantics,
				association==null?"[not set]":association.getName());
	}
	@Override
	public String render(LanguageRenderer renderer) {
		return null;
	}
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}	
}

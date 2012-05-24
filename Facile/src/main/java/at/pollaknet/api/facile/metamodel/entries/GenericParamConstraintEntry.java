package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeDefOrRef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;

public class GenericParamConstraintEntry extends AbstractAttributable
		implements RenderableCilElement, IHasCustomAttribute {

	private GenericParamEntry owner;
	private ITypeDefOrRef constraint;
	
	public GenericParamEntry getOwner() {
		return owner;
	}
	public void setOwner(GenericParamEntry owner) {
		this.owner = owner;
	}
	public ITypeDefOrRef getConstraint() {
		return constraint;
	}
	public void setConstraint(ITypeDefOrRef constraint) {
		this.constraint = constraint;
	}
	
	@Override
	public String getName() {
		return constraint.getName();
	}
	
	@Override
	public String toString() {
		return String.format("GenericParamConstraint: %s Constraint: %s",
				owner==null?"[DELETED]":owner.getName(), constraint==null?"[not set]":constraint.getFullQualifiedName());
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

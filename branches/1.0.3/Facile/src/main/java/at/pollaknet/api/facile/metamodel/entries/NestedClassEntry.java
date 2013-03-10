package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.renderer.LanguageRenderer;

public class NestedClassEntry implements RenderableCilElement {

	private TypeDefEntry nestedClass;
	private TypeDefEntry enclosingClass;
	
	public TypeDefEntry getNestedClass() {
		return nestedClass;
	}
	public void setNestedClass(TypeDefEntry nestedClass) {
		this.nestedClass = nestedClass;
	}
	public TypeDefEntry getEnclosingClass() {
		return enclosingClass;
	}
	public void setEnclosingClass(TypeDefEntry enclosingClass) {
		this.enclosingClass = enclosingClass;
	}

	@Override
	public String toString() {
		return String.format("Nested Class: %s Enclosing: %s",
				nestedClass.getFullQualifiedName(),
				nestedClass.getFullQualifiedName());
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

package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.renderer.LanguageRenderer;

public class PropertyMapEntry implements RenderableCilElement {

	private TypeDefEntry parent;
	private PropertyEntry [] properties;
	
	public TypeDefEntry getParent() {
		return parent;
	}
	public void setParent(TypeDefEntry parent) {
		this.parent = parent;
	}
	public PropertyEntry[] getProperties() {
		return properties;
	}
	public void setProperties(PropertyEntry[] properties) {
		this.properties = properties;
	}
	
	@Override
	public String toString() {
		return String.format("PropertyMap: %s NumOfProperties: %d",
				parent.getFullQualifiedName(), properties==null?0:properties.length);
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

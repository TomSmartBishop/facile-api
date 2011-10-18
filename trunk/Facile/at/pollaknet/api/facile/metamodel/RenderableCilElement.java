package at.pollaknet.api.facile.metamodel;

import at.pollaknet.api.facile.renderer.LanguageRenderer;

public interface RenderableCilElement {

	public abstract String render(LanguageRenderer renderer);
	public abstract String renderAsReference(LanguageRenderer renderer);
}

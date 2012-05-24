package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.renderer.LanguageRenderer;


public class EventMapEntry implements RenderableCilElement {

	private TypeDefEntry parent;
	private EventEntry [] events;
	
	public TypeDefEntry getParent() {
		return parent;
	}
	public void setParent(TypeDefEntry parent) {
		this.parent = parent;
	}
	public EventEntry[] getEvents() {
		return events;
	}
	public void setEvents(EventEntry[] events) {
		this.events = events;
	}
	
	@Override
	public String toString() {
		return String.format("EventMap: %s Events: %s",
				parent.getFullQualifiedName(), events==null?"[not set]":""+events.length);
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

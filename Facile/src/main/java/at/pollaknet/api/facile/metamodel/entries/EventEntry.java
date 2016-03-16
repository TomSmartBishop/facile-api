package at.pollaknet.api.facile.metamodel.entries;


import java.util.ArrayList;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasSemantics;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeDefOrRef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.Event;
import at.pollaknet.api.facile.symtab.symbols.Property;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.util.ArrayUtils;


public class EventEntry extends AbstractAttributable
		implements IHasCustomAttribute, IHasSemantics, Event{

	private int eventFlags;
	private String name;
	private ITypeDefOrRef eventType;
	private ArrayList<MethodDefEntry> methods;
	
	public int getFlags() {
		return eventFlags;
	}
	public void setEventFlags(int eventFlags) {
		this.eventFlags = eventFlags;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setEventType(ITypeDefOrRef eventType) {
		this.eventType = eventType;
	}
	
	@Override
	public String toString() {
		return String.format("Event: %s (Flags: 0x%04x Type: %s)",
				name, eventFlags, eventType.getFullQualifiedName());
	}


	@Override
	public TypeRef getTypeRef() {
		if(eventType==null) return null;
		return eventType.getTypeRef();
	}
	
	@Override
	public Event getEvent() {
		return this;
	}
	@Override
	public Property getProperty() {
		return null;
	}
	
	public boolean addMethod(MethodDefEntry method) {
		if(methods==null) methods = new ArrayList<>(4);
		return methods.add(method);
	}

	public MethodDefEntry[] getMethods() {
		if(methods==null || methods.size()==0) {
			return new MethodDefEntry[0]; 
		}
		
		return methods.toArray(new MethodDefEntry[methods.size()]);
	}

	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}
	@Override
	public int compareTo(Event o) {
		return ArrayUtils.compareStrings(o.getName(), name);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + eventFlags;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventEntry other = (EventEntry) obj;
		if (eventFlags != other.eventFlags)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} else if (!eventType.equals(other.eventType))
			return false;
//		if (methods == null) {
//			if (other.methods != null)
//				return false;
//		} else if (!methods.equals(other.methods))
//			return false;
		
		return true;
	}
	
	
}

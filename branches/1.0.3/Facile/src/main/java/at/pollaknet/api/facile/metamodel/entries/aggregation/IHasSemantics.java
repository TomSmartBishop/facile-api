package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.MethodDefEntry;
import at.pollaknet.api.facile.symtab.symbols.Event;
import at.pollaknet.api.facile.symtab.symbols.Property;

public interface IHasSemantics extends RenderableCilElement {
	public abstract String getName();
	
	public abstract Event getEvent();
	
	public abstract Property getProperty();

	public abstract boolean addMethod(MethodDefEntry methodDefEntry);
}

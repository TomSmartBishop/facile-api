package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.CustomAttributeEntry;
import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;

public interface IHasCustomAttribute extends RenderableCilElement, AttributableSymbol {
	public abstract String getName();
	
	public abstract boolean addCustomAttribute(CustomAttributeEntry customAttribute);
}

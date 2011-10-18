package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.ConstantEntry;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Property;

public interface IHasConstant extends RenderableCilElement {
	public abstract String getName();
	public abstract void setConstant(ConstantEntry constantEntry);
	
	public abstract Parameter getParameter();
	public abstract Field getField();
	public abstract Property getProperty();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

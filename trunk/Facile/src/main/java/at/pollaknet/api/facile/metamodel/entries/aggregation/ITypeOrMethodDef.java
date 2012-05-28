package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.GenericParamEntry;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.Type;

public interface ITypeOrMethodDef extends RenderableCilElement {
	
	public abstract boolean addGenericParam(GenericParamEntry p);
	public abstract String getName();
	
	public abstract Type getType();
	public abstract Method getMethod();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
	
	public abstract void setName(String name);
}

package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.DeclSecurityEntry;
import at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity;

public interface IHasDeclSecurity extends RenderableCilElement {
	public abstract String getName();
	
	public void setDeclarativeSecurity(DeclSecurityEntry declSecurityEntry);
	
	public DeclarativeSecurity getDeclarativeSecurity();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
	
}

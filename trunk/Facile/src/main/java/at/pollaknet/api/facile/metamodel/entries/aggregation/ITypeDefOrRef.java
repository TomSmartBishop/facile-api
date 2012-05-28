package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.TypeDefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.symbols.QualifiableSymbol;

/**
 * This interface is for internal use. Please avoid using it.
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public interface ITypeDefOrRef extends RenderableCilElement, QualifiableSymbol {
	
	public abstract TypeRefEntry getTypeRef();
	
	public abstract TypeDefEntry getType();
	
	public abstract TypeSpecEntry getTypeSpec();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	public abstract void setName(String name);
	
}

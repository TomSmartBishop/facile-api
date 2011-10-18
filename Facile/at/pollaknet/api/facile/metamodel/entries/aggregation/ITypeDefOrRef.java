package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.symtab.symbols.QualifiableSymbol;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;

/**
 * This interface is for internal use. Please avoid using it.
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public interface ITypeDefOrRef extends RenderableCilElement, QualifiableSymbol {
	
	public abstract TypeRef getTypeRef();
	
	public abstract Type getType();
	
	public abstract TypeSpec getTypeSpec();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
	
}

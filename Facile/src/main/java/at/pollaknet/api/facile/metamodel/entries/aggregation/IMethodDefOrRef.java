package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.symbols.MemberRef;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;

public interface IMethodDefOrRef extends RenderableCilElement {

	public abstract String getName();
	
	public abstract byte [] getBinarySignature();
	
	public abstract Method getMethod();
	
	public abstract MemberRef getMemberRef();

	public abstract void addGenericInstance(TypeSpecEntry typeSpec);
	
	public abstract TypeSpec []  getGenericInstances();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}

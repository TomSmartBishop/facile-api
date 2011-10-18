package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.MemberRefEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodDefEntry;

public interface ICustomAttributeType extends RenderableCilElement {
	public abstract String getName();
	
	public abstract MethodDefEntry getMethod();
	public abstract MemberRefEntry getMemberRef(); 
}

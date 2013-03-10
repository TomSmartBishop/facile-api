package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IMethodDefOrRef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;

public class MethodImplEntry implements RenderableCilElement {

	private TypeDefEntry ownerClass;
	private IMethodDefOrRef methodImplementationBody;
	private IMethodDefOrRef methodDeclaration;
	
	public TypeDefEntry getOwnerClass() {
		return ownerClass;
	}
	public void setOwnerClass(TypeDefEntry ownerClass) {
		this.ownerClass = ownerClass;
	}
	public IMethodDefOrRef getImplementationBody() {
		return methodImplementationBody;
	}
	public void setImplementationBody(IMethodDefOrRef methodBody) {
		this.methodImplementationBody = methodBody;
	}
	public IMethodDefOrRef getMethodDeclaration() {
		return methodDeclaration;
	}
	public void setMethodDeclaration(IMethodDefOrRef methodDeclaration) {
		this.methodDeclaration = methodDeclaration;
	}
	
	@Override
	public String toString() {
		return String.format("MethodImpl: %s Body: %s Decleration: %s",
				ownerClass==null?"[DELETED]":ownerClass.getFullQualifiedName(),
				methodImplementationBody==null?"[not set]":methodImplementationBody.getName(),
				methodDeclaration==null?"[not set]":methodDeclaration.getName());
	}
	@Override
	public String render(LanguageRenderer renderer) {
		return null;
	}
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}

}

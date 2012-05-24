package at.pollaknet.api.facile.code.instruction;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;

public abstract class ReferenceCilInstruction extends CilInstruction {

	protected int token;
	protected RenderableCilElement reference;

	public RenderableCilElement getReferencedElement() {
		return reference;
	}

	public int getMetadataToken() {
		return token;
	}
}

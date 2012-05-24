package at.pollaknet.api.facile.code.instruction;


public abstract class TargetCilInstruction extends CilInstruction {

	protected int target;
	
	public int getJumpTarget() {
		return target;
	}

}

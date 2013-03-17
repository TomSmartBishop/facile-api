package at.pollaknet.api.facile.pdb;

public class UnexpectedPdbContent extends Exception {
	
	private static final long serialVersionUID = -8026316368868720187L;

	public UnexpectedPdbContent() {
		super();
	}
	
	public UnexpectedPdbContent(String msg) {
		super(msg);
	}

}

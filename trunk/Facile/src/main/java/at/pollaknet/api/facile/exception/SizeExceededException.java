package at.pollaknet.api.facile.exception;

public class SizeExceededException extends Exception {

	private static final long serialVersionUID = 8754371828709725700L;

	public SizeExceededException(String msg) {
		super(msg);
	}
}

package at.pollaknet.api.facile.exception;

public class InvalidSignatureException extends RuntimeException {
	
	private static final long serialVersionUID = -2874486703280287456L;

	public InvalidSignatureException(int currentToken) {
		super(String.format("Unexpected Token 0x%x", currentToken));
	}
	
	public InvalidSignatureException(String msg) {
		super(msg);
	}

	public InvalidSignatureException(int currentToken, int expectedToken) {
		super(String.format("Invalid Token 0x%x, expected 0x%x", currentToken, expectedToken));
	}
}

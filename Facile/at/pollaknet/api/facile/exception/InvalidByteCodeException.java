package at.pollaknet.api.facile.exception;

public class InvalidByteCodeException extends InvalidMethodBodyException {

	private static final long serialVersionUID = -7931946184376688372L;

	public InvalidByteCodeException() {
		super();
	}
	
	public InvalidByteCodeException(String msg) {
		super(msg);
	}
	
	public InvalidByteCodeException(byte currentToken) {
		super(String.format("Unexpected Token 0x%02x", currentToken));
	}

	public InvalidByteCodeException(byte currentToken, byte expectedToken) {
		super(String.format("Invalid Token 0x%02x, expected 0x%02x", currentToken, expectedToken));
	}
}
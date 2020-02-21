package at.pollaknet.api.facile.exception;

import at.pollaknet.api.facile.util.ArrayUtils;

public class InvalidSignatureException extends RuntimeException {
	
	private static final long serialVersionUID = -2874486703280287456L;

	private static String createPointer(int currentIndex) {
		StringBuffer pointer = new StringBuffer(currentIndex*3+10);
		
		for(int i=0;i<currentIndex;i++) {
			if(i%10==0) {
				pointer.append(String.format("%-3d", i%1000));
			} else {
				pointer.append("   ");
			}
		}
		pointer.append("^^-- index: ");
		pointer.append(currentIndex);
		return pointer.toString();
	}
	
	public InvalidSignatureException(String msg) {
		super(msg);
	}

	public InvalidSignatureException(byte[] binarySignature, int currentIndex, int currentToken,
			boolean malformedSignature) {
		this(binarySignature, currentIndex, currentToken, malformedSignature, null);
	}
	
	public InvalidSignatureException(byte[] binarySignature, int currentIndex, int currentToken,
			boolean malformedSignature, String expected) {
		super(String.format("Invalid Token 0x%x @ index %d (%s)%s:\n   %s\n  [%s]\n   %s",
					currentToken, currentIndex,
					malformedSignature ? "malformed" : "valid",
					expected==null ? "" : " [expected: " + expected + "]",
					ArrayUtils.formatByteArrayAsAscii(binarySignature),
					ArrayUtils.formatByteArray(binarySignature),
					createPointer(currentIndex))
				);
	}
}

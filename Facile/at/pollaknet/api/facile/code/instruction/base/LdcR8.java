package at.pollaknet.api.facile.code.instruction.base;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class LdcR8 extends CilInstruction {

	public static final byte FIRST_TOKEN = 0x23;
	public static final byte BYTE_SIZE = 0x09;
	
	private double constant;
	
	
	@Override
	public String render(LanguageRenderer renderer) {
		return toString();
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer, index, FIRST_TOKEN);
		constant = ByteReader.getDouble(buffer, index+1);
		return BYTE_SIZE;
	}
	
	public double getConstant() {
		return constant;
	}
	
	public String toString() {
		return "ldc.r8 " + constant;
	}
	
	@Override
	public byte getByteSize() {
		return BYTE_SIZE;
	}
	
	@Override
	public byte getFirstToken() {
		return FIRST_TOKEN;
	}

	@Override
	public byte getSecondToken() {
		return EXTENDED_INSTRUCTION_TOKEN;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		if(((LdcR8)obj).constant!=constant)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		long raw = Double.doubleToLongBits(constant);
		return 631 + (int)(raw ^ (raw >>> 32));
	}

}

package at.pollaknet.api.facile.code.instruction.base;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class LdcI4 extends CilInstruction {

	public static final byte FIRST_TOKEN = 0x20;
	public static final byte BYTE_SIZE = 0x05;
	
	private int constant;

	
	@Override
	public String render(LanguageRenderer renderer) {
		return toString();
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer, index, FIRST_TOKEN);
		constant = ByteReader.getInt32(buffer, index+1);
		return BYTE_SIZE;
	}
	
	public int getConstant() {
		return constant;
	}
	
	public String toString() {
		return "ldc.i4 " + constant;
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
		if(((LdcI4)obj).constant!=constant)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return 547 + constant;
	}
}

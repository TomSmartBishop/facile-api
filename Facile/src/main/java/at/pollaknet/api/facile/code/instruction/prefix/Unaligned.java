package at.pollaknet.api.facile.code.instruction.prefix;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class Unaligned extends CilInstruction {

	public static final byte SECOND_TOKEN = 0x12;
	public static final byte BYTE_SIZE = 0x03;
	
	private short alignment;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return toString();
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer,index, EXTENDED_INSTRUCTION_TOKEN);
		ensure(buffer,index+1, SECOND_TOKEN);
		
		alignment = ByteReader.getUInt8(buffer, index+2);
		
		return BYTE_SIZE;
	}

	public short getAlignment() {
		return alignment;
	}

	public String toString() {
		return "unaligned." + alignment;
	}
	
	@Override
	public byte getByteSize() {
		return BYTE_SIZE;
	}
	
	@Override
	public byte getFirstToken() {
		return EXTENDED_INSTRUCTION_TOKEN;
	}

	@Override
	public byte getSecondToken() {
		return SECOND_TOKEN;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		return ((Unaligned) obj).alignment == alignment;
	}

	@Override
	public int hashCode() {
		return 1447 + alignment;
	}
}

package at.pollaknet.api.facile.code.instruction.prefix;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class No extends CilInstruction {

	public static final byte SECOND_TOKEN = 0x19;
	public static final byte BYTE_SIZE = 0x03;
	
	private byte checkType;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return toString();
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer,index, EXTENDED_INSTRUCTION_TOKEN);
		ensure(buffer,index+1, SECOND_TOKEN);
		
		checkType = ByteReader.getInt8(buffer, index+2);
		return BYTE_SIZE;
	}

	public byte getCheckType() {
		return checkType;
	}
	
	public String toString() {
		switch(checkType) {
			case 1:		return "no.typecheck";
			case 2:		return "no.rangecheck";
			case 4:		return "no.nullcheck";
			default:	assert(false); return "";
		}
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
		return ((No) obj).checkType == checkType;
	}

	@Override
	public int hashCode() {
		return 1433 + checkType;
	}
}

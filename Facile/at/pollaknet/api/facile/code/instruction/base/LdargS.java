package at.pollaknet.api.facile.code.instruction.base;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class LdargS extends CilInstruction {

	public static final byte FIRST_TOKEN = 0x0e;
	public static final byte BYTE_SIZE = 0x02;
	
	private short argumentNumber;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return toString();
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer, index, FIRST_TOKEN);
		argumentNumber = ByteReader.getUInt8(buffer, index+1);
		return BYTE_SIZE;
	}
	
	public short getArgumentNumber() {
		return argumentNumber;
	}
	
	public String toString() {
		return "ldarg.s " + argumentNumber;
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
		if(((LdargS)obj).argumentNumber!=argumentNumber)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return 541 + argumentNumber;
	}

}

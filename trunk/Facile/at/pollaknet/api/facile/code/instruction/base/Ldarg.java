package at.pollaknet.api.facile.code.instruction.base;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class Ldarg extends CilInstruction {

	public static final byte SECOND_TOKEN = 0x09;
	public static final byte BYTE_SIZE = 0x03;
	
	private int argumentNumber;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return toString();
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer,index, EXTENDED_INSTRUCTION_TOKEN);
		ensure(buffer,index+1, SECOND_TOKEN);
		
		this.argumentNumber = ByteReader.getUInt16(buffer, index+2);
		return BYTE_SIZE;
	}
	
	public int getArgumentNumber() {
		return argumentNumber;
	}
	
	public String toString() {
		return "ldarg " + argumentNumber;
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
		if(((Ldarg)obj).argumentNumber!=argumentNumber)
			return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		return 487 + argumentNumber;
	}

}

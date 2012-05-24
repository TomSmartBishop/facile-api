package at.pollaknet.api.facile.code.instruction.base;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class Ldloca extends CilInstruction {

	public static final byte SECOND_TOKEN = 0x0d;
	public static final byte BYTE_SIZE = 0x03;

	private int address;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return toString();
	}
	
	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer,index, EXTENDED_INSTRUCTION_TOKEN);
		ensure(buffer,index+1, SECOND_TOKEN);
		
		address = ByteReader.getUInt16(buffer, index+2);
		return BYTE_SIZE;
	}
	
	public int getAddress() {
		return address;
	}

	public String toString() {
		return "ldloca " + address;
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
		if(((Ldloca)obj).address!=address)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return 751 + address;
	}
}

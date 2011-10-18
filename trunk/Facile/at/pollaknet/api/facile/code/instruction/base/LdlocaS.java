package at.pollaknet.api.facile.code.instruction.base;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class LdlocaS extends CilInstruction {

	public static final byte FIRST_TOKEN = (byte)0x12;
	public static final byte BYTE_SIZE = 0x02;
	
	private short address;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return toString();
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer,index, FIRST_TOKEN);
		
		address = ByteReader.getUInt8(buffer, index+1);
		return BYTE_SIZE;
	}
	
	public short getAddress() {
		return address;
	}

	public String toString() {
		return "ldloca.s " + address;
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
		if(((LdlocaS)obj).address!=address)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return 761 + address;
	}
}

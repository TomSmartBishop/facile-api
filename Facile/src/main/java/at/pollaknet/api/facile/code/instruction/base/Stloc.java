package at.pollaknet.api.facile.code.instruction.base;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class Stloc extends CilInstruction {

	public static final byte SECOND_TOKEN = 0x0e;
	public static final byte BYTE_SIZE = 0x03;
	
	private int index;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return toString();
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer,index, EXTENDED_INSTRUCTION_TOKEN);
		ensure(buffer,index+1, SECOND_TOKEN);
		
		this.index = ByteReader.getUInt16(buffer, index+2);
		return BYTE_SIZE;
	}
	
	public int getArgumentIndex(){
		return index;
	}
	
	public String toString() {
		return "stloc " + index;
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
		return ((Stloc) obj).index == index;
	}

	@Override
	public int hashCode() {
		return 977 + index;
	}

}

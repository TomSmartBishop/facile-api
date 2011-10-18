package at.pollaknet.api.facile.code.instruction.object;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;

public class StelemI1 extends CilInstruction {

	public static final byte FIRST_TOKEN = (byte) 0x9c;
	public static final byte BYTE_SIZE = 0x01;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return toString();
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer,index, FIRST_TOKEN);
		return BYTE_SIZE;
	}

	
	public String toString() {
		return "stelem.i1";
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
		return true;
	}

	@Override
	public int hashCode() {
		return 1303;
	}
}

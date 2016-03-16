package at.pollaknet.api.facile.code.instruction.base;

import at.pollaknet.api.facile.code.instruction.ReferenceCilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class Jmp extends ReferenceCilInstruction {

	public static final byte FIRST_TOKEN = 0x27;
	public static final byte BYTE_SIZE = 0x05;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return "jmp " + reference.renderAsReference(renderer);
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer, index, FIRST_TOKEN);
		token = ByteReader.getInt32(buffer, index+1);
		reference = metaModel.getEntryByToken(token);
		return BYTE_SIZE;
	}
	
	
	
	public String toString() {
		return "jmp " + token;
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
		return ((Jmp) obj).token == token;
	}

	@Override
	public int hashCode() {
		return 479 + token;
	}

}

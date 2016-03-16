package at.pollaknet.api.facile.code.instruction.object;

import at.pollaknet.api.facile.code.instruction.ReferenceCilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class Initobj extends ReferenceCilInstruction {

	public static final byte SECOND_TOKEN = 0x15;
	public static final byte BYTE_SIZE = 0x06;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return "initobj " + reference.renderAsReference(renderer);
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer,index, EXTENDED_INSTRUCTION_TOKEN);
		ensure(buffer,index+1, SECOND_TOKEN);
		reference = metaModel.getEntryByToken(ByteReader.getInt32(buffer, index+2));
		return BYTE_SIZE;
	}	
	
	public String toString() {
		return "initobj " + token;
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
		return ((Initobj) obj).token == token;
	}

	@Override
	public int hashCode() {
		return 1069 + token;
	}

}

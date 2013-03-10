package at.pollaknet.api.facile.code.instruction.base;

import at.pollaknet.api.facile.code.instruction.ReferenceCilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class Ldftn extends ReferenceCilInstruction {

	public static final byte SECOND_TOKEN = 06;
	public static final byte BYTE_SIZE = 0x06;
	
	@Override
	public String render(LanguageRenderer renderer) {
		return "ldftn " + reference.renderAsReference(renderer);
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer,index, EXTENDED_INSTRUCTION_TOKEN);
		ensure(buffer,index+1, SECOND_TOKEN);

		token = ByteReader.getInt32(buffer, index+2);
		reference = metaModel.getEntryByToken(token);
		
		return BYTE_SIZE;
	}
	
	public String toString() {
		return "ldftn " + token;
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
		if(((Ldftn)obj).token!=token)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return 641 + token;
	}
	

}

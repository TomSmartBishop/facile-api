package at.pollaknet.api.facile.code.instruction.object;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class Ldstr extends CilInstruction {

	public static final byte FIRST_TOKEN = 0x72;
	public static final byte BYTE_SIZE = 0x05;
	
	private int token;
	private String str;
	
	@Override
	public String render(LanguageRenderer renderer) {
		if(str!=null)
			return "ldstr \"" + str + "\"";
		
		return "ldstr null";
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer, index, FIRST_TOKEN);
		token = ByteReader.getInt32(buffer, index+1);
		str = metaModel.getUserStringbyToken(token);
		return BYTE_SIZE;
	}
	
	public int getMetadataToken(){
		return token;
	}
	
	public String toString() {
		return "ldstr " + token;
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
		
		String other = ((Ldstr)obj).str;
		if(str==null) {
			if(other!=null)
				return false;
			//valid!!!
			return true;
		} else if(other==null) {
			return false;
		}
	
		return other.equals(str);
	}

	@Override
	public int hashCode() {
		return 1229 + token;
	}

}

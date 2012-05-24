package at.pollaknet.api.facile.code.instruction.base;

import at.pollaknet.api.facile.code.instruction.TargetCilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.ILAsmRenderer;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class BleUnS extends TargetCilInstruction {

	public static final byte FIRST_TOKEN = 0x36;
	public static final byte BYTE_SIZE = 0x02;

	@Override
	public String render(LanguageRenderer renderer) {
		assert(renderer instanceof ILAsmRenderer);
		return "ble.un.s " + ((ILAsmRenderer)renderer).renderRelativeAsLabel(target);
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer, index, FIRST_TOKEN);
		target = ByteReader.getInt8(buffer, index+1);
		return BYTE_SIZE;
	}
	
	
	public String toString() {
		return "ble.un.s " + target;
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
		if(((BleUnS)obj).target!=target)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return 113 + target;
	}

}

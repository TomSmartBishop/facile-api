package at.pollaknet.api.facile.code.instruction.base;

import java.util.Arrays;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.renderer.ILAsmRenderer;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ByteReader;

public class Switch extends CilInstruction {

	public static final byte FIRST_TOKEN = 0x45;
	private int byteSize;
	private int numCases;
	private int [] jumpTargets;
	
	@Override
	public String render(LanguageRenderer renderer) {
		assert(renderer instanceof ILAsmRenderer);
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("switch (");
		for(int i=0;i<numCases;i++) {
			if(i!=0) buffer.append(", ");
			buffer.append(((ILAsmRenderer)renderer).renderRelativeAsLabel(jumpTargets[i]));
		}
		buffer.append(")");
		
		return buffer.toString();
	}

	@Override
	public int parseInstruction(byte[] buffer, int index, MetadataModel metaModel) throws InvalidByteCodeException {
		ensure(buffer, index, FIRST_TOKEN);
		long cases = ByteReader.getUInt32(buffer, index+1);
		assert(cases<=ByteReader.INT32_MAX_VAL);
		numCases = (int) cases;
	
		assert(numCases>0);
		
		jumpTargets = new int [numCases];
		
		for(int i=0;i<numCases;i++) {
			jumpTargets[i] = ByteReader.getInt32(buffer, index+5+i*4);
		}
		
		byteSize = 5 + numCases*4;
		
		return byteSize;
	}
	
	public int getNumberOfCases(){
		return numCases;
	}
	
	public int [] getTargetAddresses() {
		return jumpTargets;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("switch (");
		for(int i=0;i<numCases;i++) {
			if(i!=0) buffer.append(", ");
			buffer.append(jumpTargets[i]);
		}
		buffer.append(")");
		
		return buffer.toString();
	}
	
	@Override
	public byte getByteSize() {
		if(byteSize>ByteReader.INT8_MAX_VAL)
			return -1;
		return (byte) byteSize;
	}
	
	public int getExtendedByteSize() {
		return byteSize;
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
		Switch other = (Switch) obj;
		if (numCases != other.numCases)
			return false;
		return Arrays.equals(jumpTargets, other.jumpTargets);
	}
	
	@Override
	public int hashCode() {
		return 1033 + numCases;
	}

}

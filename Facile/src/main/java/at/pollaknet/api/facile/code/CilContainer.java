package at.pollaknet.api.facile.code;

import at.pollaknet.api.facile.header.coffpe.PESectionHeader;
import at.pollaknet.api.facile.util.ByteReader;


public class CilContainer {

	private byte [] codeBuffer;
	private PESectionHeader[] sections;

	public void setCodeBuffer(byte[] codeBuffer) {
		this.codeBuffer = codeBuffer;
	}

	public byte[] getCodeBuffer() {
		return codeBuffer;
	}

	public void setPeFileSections(PESectionHeader[] sections) {
		this.sections = sections;
	}

	public int getPhysicalAddressOf(long relativeVirtualAddress) {

		long prevRVA = 0;
		long prevPA = 0;
		for(PESectionHeader header: sections) {
		
			if(header.getRelativeVirtualAddress()>relativeVirtualAddress) {
				assert((prevPA + relativeVirtualAddress - prevRVA)<=ByteReader.INT32_MAX_VAL);
				return (int) (prevPA + relativeVirtualAddress - prevRVA);
			}
			
			prevRVA = header.getRelativeVirtualAddress();
			prevPA = header.getPointerToRawData();
		}
		
		assert((prevPA + relativeVirtualAddress - prevRVA)<=ByteReader.INT32_MAX_VAL);
		return (int) (prevPA + relativeVirtualAddress - prevRVA);
	}

}

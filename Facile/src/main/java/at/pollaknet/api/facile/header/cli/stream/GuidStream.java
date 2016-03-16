package at.pollaknet.api.facile.header.cli.stream;

import java.util.ArrayList;

import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.ByteReader;



public class GuidStream implements IDataHeader {

	private int byteSize;
	private ArrayList <byte []> guidHeap;
	
	public final static int GUID_BYTE_SIZE = 16;
	
	public GuidStream(int size) {
		byteSize=size;
		guidHeap = new ArrayList<>();
	}
	
	@Override
	public int getSize() {
		return byteSize;
	}

	@Override
	public int read(byte[] data, int offset)
			throws UnexpectedHeaderDataException {

		int index = offset;
		
		//See ECMA 335 revision 4 - Partition II, 24.2.5: #GUID heap
		//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=295&view=FitH
		while(index<offset+byteSize) {
			guidHeap.add(ByteReader.getBytes(data, index, GUID_BYTE_SIZE));
			
			index += GUID_BYTE_SIZE;
		}

		byteSize = index-offset; 
		return byteSize;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("#GUID Stream (Globally Unique Identifier Heap):");
		
		int index = 0;
		
		for(byte [] guid : guidHeap) {
			buffer.append("\n ");
			buffer.append(index);
			buffer.append(":\t");
			buffer.append(ArrayUtils.formatByteArray(guid));
			index++;
		}
		
		return buffer.toString();
	}

	public byte [] getGuid(int index) {
		index--;
		if(index<0 || index>=guidHeap.size())
			return null;
		
		return guidHeap.get(index);
	}
	
	public String getName(){
		return "#GUID";
	}
}

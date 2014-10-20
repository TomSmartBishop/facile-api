package at.pollaknet.api.facile.header.cli.stream;

import java.util.Arrays;
import java.util.HashMap;

import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.ByteReader;


public class BlobStream implements IDataHeader {

	private int byteSize;
	private HashMap<Integer, byte []> blobHeap;
	
	public BlobStream(int size) {
		byteSize=size;
		blobHeap = new HashMap<Integer, byte []>();
	}
	
	@Override
	public int getSize() {
		return byteSize;
	}

	@Override
	public int read(byte[] data, int offset)
			throws UnexpectedHeaderDataException {

		int maxIndex = offset+byteSize;
		
		//the blob heap starts with the empty blob, so start with the next byte
		int index = offset + 1;
		int length = 0;
		int lengthInfo = 0;
		
		while(index<maxIndex) {

			//See ECMA 335 revision 4 - Partition II, 24.2.4: #US and #Blob heaps
			//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=294&view=FitH
			length = ByteReader.readHeapObjectSize(data, index);
			lengthInfo = ByteReader.getSizeOfHeapLength(length);
			
			if(length>=0 && index+length+lengthInfo<=maxIndex) {		
				byte [] binarySignature = ByteReader.getBytes(data, index+lengthInfo, length);
				blobHeap.put(index-offset, binarySignature);
			}			
		
			index += length + lengthInfo;
		}

		byteSize = index-offset; 
		return byteSize;
	}


	public String toString() {
		StringBuffer buffer = new StringBuffer("#Blob Stream (Blob Heap):");
				
		if(blobHeap.size()>0) {
			Integer [] keySet = blobHeap.keySet().toArray(new Integer[0]);
			Arrays.sort(keySet);
			
			for(Integer key: keySet) {
				String value = ArrayUtils.formatAsHexTable(blobHeap.get(key), false);
				buffer.append(String.format("\n%10d:\t%s", key, value));
			}
		}
		
		return buffer.toString();
	}
	
	public byte [] getBlob(int byteIndex) {
		return blobHeap.get(byteIndex);
	}
	
	public String getName(){
		return "#Blob";
	}

}

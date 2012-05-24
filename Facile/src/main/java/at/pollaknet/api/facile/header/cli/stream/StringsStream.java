package at.pollaknet.api.facile.header.cli.stream;

import java.util.Arrays;
import java.util.HashMap;

import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.ByteReader;

public class StringsStream implements IDataHeader {

	private int byteSize;
	private HashMap<Integer, String> stringHeap;
	
	Integer [] sortedKeySet = null;
	
	public StringsStream(int size) {
		byteSize=size;
		stringHeap = new HashMap<Integer, String>();
	}
	
	@Override
	public int getSize() {
		return byteSize;
	}

	@Override
	public int read(byte[] data, int offset)
			throws UnexpectedHeaderDataException {

		//the string heap starts with a leading 0, so start with the next byte
		int index = offset + 1;
		int length;
		
		while(index<offset+byteSize) {
			
			//See ECMA 335 revision 4 - Partition II, 24.2.3: #Strings heap
			//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=294&view=FitH
			
			length = ArrayUtils.findInByteArray(data, index, (byte)0);
			
			if(length<0) return index-offset;
			
			if(length>0) {
				stringHeap.put(index-offset, new String(ByteReader.getBytes(data, index, length)));
			}
			
			index += length+1;				
		}

		byteSize = index-offset; 
		return byteSize;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer("#Strings Stream (String Heap):");
		
		if(stringHeap.size()>0) {
			ensureSortedKeySet();
			
			for(Integer key: sortedKeySet) {
				String value = stringHeap.get(key);
				value = (value==null) ? "null" : value.replaceAll("\\p{Cntrl}",".");
			
				buffer.append(String.format("\n%10d:\t%s", key, value));
			}
		}
		return buffer.toString();
	}
	
	public String getName(){
		return "#Strings";
	}
	
	public String getString(int byteIndex) {
		String searchString = stringHeap.get(byteIndex);
		
		if(searchString==null && byteIndex!=0) {
			//recover an invalid string
			
			ensureSortedKeySet();
			
			//ensure that the index is not out of the valid range
			int lastIndex = sortedKeySet[sortedKeySet.length-1].intValue();
			String lastElement = stringHeap.get(lastIndex);
			
			if(byteIndex>=lastIndex+lastElement.length()) {
				return null;
			}
				
			//search for the previous entry
			Integer prevValue = sortedKeySet[0];
			
			for(Integer key: sortedKeySet) {
				if(byteIndex<key.intValue()) {
					break;
				}
				prevValue = key;
			}

			//create a valid string representation
			searchString = stringHeap.get(prevValue.intValue()).substring(byteIndex-prevValue.intValue());
		}
		
		return searchString;
	}

	private void ensureSortedKeySet() {
		//generate the sorted key set if necessary
		if(sortedKeySet==null || sortedKeySet.length != stringHeap.size()) {
		
			sortedKeySet = stringHeap.keySet().toArray(new Integer[0]);
			Arrays.sort(sortedKeySet);
		}
	}

//	public String getStringByApproximatedIndex(int byteIndex) {
//		String searchString = stringHeap.get(byteIndex);
//		
//		if(searchString==null && byteIndex!=0) {
//			//recover an invalid string
//			
//			ensureSortedKeySet();
//				
//			//search for the previous entry
//			Integer prevValue = sortedKeySet[0];
//			
//			for(Integer key: sortedKeySet) {
//				if(byteIndex<key.intValue()) {
//					break;
//				}
//				prevValue = key;
//			}
//
//			//create a valid string representation
//			searchString = stringHeap.get(prevValue.intValue());
//		}
//		
//		return searchString;
//	}

	public String getStringByDecodedToken(int decodedToken) {
		ensureSortedKeySet();
		
		if(decodedToken>=0 && decodedToken<sortedKeySet.length) {
			return stringHeap.get(sortedKeySet[decodedToken].intValue());
		}
		
		return null;
	}
}

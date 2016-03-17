package at.pollaknet.api.facile.header.cli.stream;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ByteReader;

public class UserStringStream implements IDataHeader {
	
	private int byteSize;
	private HashMap<Integer, String> userStringHeap;
	
	Integer [] sortedKeySet = null;
	
	private String USER_STRING_DECODING_STANDARD = "UTF16";
	
	public UserStringStream(int size) {
		byteSize=size;
		userStringHeap = new HashMap<>();
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
		int index = offset+1;
		int length = 0;
		int lengthInfo;
		byte utf16String [] = null;
		
		while(index<maxIndex) {
				
			//See ECMA 335 revision 4 - Partition II, 24.2.4: #US and #Blob heaps
			//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=294&view=FitH
			length = ByteReader.readHeapObjectSize(data, index);
			lengthInfo = ByteReader.getSizeOfHeapLength(length);
			
			if(length>0 && index+length+lengthInfo<=maxIndex) {

				try {
					utf16String = ByteReader.getUTF16PreparedBytes(data, index+lengthInfo, length-1);
					if(utf16String!=null)
						userStringHeap.put(index-offset,new String(utf16String, USER_STRING_DECODING_STANDARD));
					else
						userStringHeap.put(index-offset,null);
				} catch (UnsupportedEncodingException e) {
					Logger.getLogger(FacileReflector.LOGGER_NAME).logp(Level.WARNING,
							"UserStringStream", "read", 
							"Unable to decode the user string at position " + index + " (length: " + length + ") using " + USER_STRING_DECODING_STANDARD);
				}			
			}
			
			index += length + lengthInfo;
		}

		byteSize = index-offset; 
		return byteSize;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("#US Stream (UserString Heap):");

		if(userStringHeap.size()>0) {
			ensureSortedKeySet();
			
			for(Integer key: sortedKeySet) {
				String value = userStringHeap.get(key);
				value = (value==null) ? "null" : value.replaceAll("\\p{Cntrl}",".");
			
				buffer.append(String.format("\n%10d:\t%s", key, value));
			}
		}
		
		return buffer.toString();
	}
	
	public String getName(){
		return "#US";
	}
	
	private void ensureSortedKeySet() {
		//generate the sorted key set if necessary
		if(sortedKeySet==null || sortedKeySet.length != userStringHeap.size()) {

			java.util.Set<Integer> var = userStringHeap.keySet();
			sortedKeySet = var.toArray(new Integer[var.size()]);
			Arrays.sort(sortedKeySet);
		}
	}
	
	public String getUserString(int byteIndex) {
		return userStringHeap.get(byteIndex);
	}
	
	public String getUserStringByDecodedToken(int decodedToken) {
		ensureSortedKeySet();
		
		if(decodedToken>=0 && decodedToken<sortedKeySet.length) {
			return userStringHeap.get(sortedKeySet[decodedToken].intValue());
		}
		
		return null;
	}

}

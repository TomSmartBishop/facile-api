package at.pollaknet.api.facile.util;


//TODO: Implement generic methods
public class ArrayUtils {

	/**
	 * Checks weather the specified array contains the given element.
	 * @param array The valid (not null) array to check.
	 * @param element The search element.
	 * @return True, if the array contains the element.
	 */
	public static boolean contains(int[] array, int element) {
		for(int value: array) {
			if(value==element)
				return true;
		}
		return false;
	}

	/**
	 * Format the specified byte array in whitespace separated pairs of hex digits.
	 * @param array The array to format (even null).
	 * @return The proper string representation of the buffer.
	 */
	public static String formatByteArray(byte[] array) {
		if(array==null) return "null";
		StringBuffer buffer = new StringBuffer(array.length*3);
		
		buffer.append(String.format("%02x", array[0]));
		
		for(int i=1;i<array.length;i++) {
			buffer.append(String.format(" %02x", array[i]));
		}
		
		return buffer.toString();
	}
	
	public static String formatByteArrayAsAscii(byte[] array) {
		if(array==null) return "null";
			
		return new String(array).replaceAll("\\p{Cntrl}",".").replaceAll("\\B|\\b", "  ").substring(2);
	}

	/**
	 * Check if two specified arrays are equal.
	 * @param a1 The first byte array.
	 * @param a2 The second byte array.
	 * @return True if the specified arrays are equal an not null.
	 */
	public static boolean arraysAreEqual(byte[] a1, byte[] a2) {
		if(a1.length!=a2.length)
			return false;
		
		for(int i=0;i<a1.length;i++){
			if(a1[i]!=a2[i]) return false;
		}
		
		return true;
	}
	
	public static boolean arraysAreEqualN(byte[] a1, byte[] a2) {
		if(a1==null) {
			return a2 == null;
		} else if(a2==null) {
			return false;
		}
		
		if(a1.length!=a2.length) {
			return false;
		}
		
		for(int i=0;i<a1.length;i++){
			if(a1[i]!=a2[i]) return false;
		}
		
		return true;
	}
	
	public static boolean stringsAreEqualN(String s1, String s2) {
		if(s1==null) {
			return s2 == null;
		}
		
		return s1.equals(s2);
	}
	
	public static int compareStrings(String s1, String s2) {
		if(s1==null) {
			if(s2==null) return 0;
			return Integer.MAX_VALUE;
		} else if(s2==null){
			return Integer.MIN_VALUE;
		}
			
		return s2.compareTo(s1);
	}
	
	public static int findDifference(byte[] a1, byte[] a2) {
		int limit = a1.length;
		if(limit>a2.length) {
			limit = a2.length;
		}
		
		for(int i=0;i<limit;i++){
			if(a1[i]!=a2[i]) return i;
		}
		
		return a1.length==a2.length ? 0 : limit;
	}
	
	public static String formatAsHexTable(long codeRVA, byte[] byteBuffer, int length, boolean addHeader) {
		
		if(byteBuffer==null)
			return null;
		
		if(length> byteBuffer.length)
			length = byteBuffer.length;
			
		StringBuffer stringBuffer = new StringBuffer();
		
		if(addHeader) {
			stringBuffer.append("Address: ");
			
			stringBuffer.append(String.format("-%1x--%1x--%1x--%1x--%1x--%1x--%1x--%1x--",
					codeRVA%16, (codeRVA+1)%16, (codeRVA+2)%16, (codeRVA+3)%16,
					(codeRVA+4)%16, (codeRVA+5)%16, (codeRVA+6)%16, (codeRVA+7)%16));
			
			stringBuffer.append(String.format("%1x--%1x--%1x--%1x--%1x--%1x--%1x--%1x\n",
					(codeRVA+8)%16, (codeRVA+9)%16, (codeRVA+10)%16, (codeRVA+11)%16,
					(codeRVA+12)%16, (codeRVA+13)%16, (codeRVA+14)%16, (codeRVA+15)%16));
				
			stringBuffer.append(String.format("%08X ", codeRVA));
		}
		
		int i;
		
		for(i=0;i<length;i++) {
			stringBuffer.append(String.format("%02x ", byteBuffer[i]));
			if((i&0x0f)==0x0f){
				byte [] buffer = new byte[16];
				System.arraycopy(byteBuffer,i-15,buffer,0,16);
				codeRVA += 16;
				if(addHeader)
					stringBuffer.append(String.format(" %s\n%08X ", new String(buffer).replaceAll("\\p{Cntrl}","."), codeRVA));
				else
					stringBuffer.append(String.format(" %s\n ", new String(buffer).replaceAll("\\p{Cntrl}",".")));
			}
		}
		
		i--;
		
		//extra handling for the last line (because the line is maybe not completely filled)
		for(int space=15;space>(i&0x0f);space--)
			stringBuffer.append("   ");
		
		if((i&0x0f)!=0x0f){
			byte [] buffer = new byte[(i&0x0f)+1];
			System.arraycopy(byteBuffer,length-(i&0x0f)-1,buffer,0,(i&0x0f)+1);
			stringBuffer.append(" ").append(new String(buffer).replaceAll("\\p{Cntrl}", "."));
		}
				
		return stringBuffer.toString();
	}

	public static String formatAsHexTable(byte[] byteBuffer, boolean addHeader) {
		return formatAsHexTable(0, byteBuffer, byteBuffer==null ? 0 : byteBuffer.length, addHeader);
	}

	public static String formatAsHexTable(byte[] byteBuffer, int length, boolean addHeader) {
		return formatAsHexTable(0, byteBuffer, length, addHeader);
	}
	
	public static String formatAsHexTable(long codeRVA, byte[] byteBuffer, boolean addHeader) {
		return formatAsHexTable(codeRVA, byteBuffer, byteBuffer==null ? 0 : byteBuffer.length, addHeader);
	}
	
	/**
	 * Searches for a specified byte value inside an array of bytes, 
	 * starting at a given offset.
	 * @param data The byte array where the search occurs.
	 * @param offset The offset where to start.
	 * @param sign The byte value which is getting detected inside the array.
	 * @return The relative position (to the {@code offset}) of the first 
	 * occurrence of {@code sign} or {@code -1} if {@code sign} was not found.
	 */
	public static int findInByteArray(byte[] data, int offset, byte sign) {
		int index = offset;
		
		while(data[index]!=sign){
			index++;
			
			if(index>=data.length) return -1;
		}
		
		return index-offset;
	}

}

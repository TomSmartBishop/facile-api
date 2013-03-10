package at.pollaknet.api.facile.util;


public class ByteReader {
	
	public final static int UINT8_MAX_VAL = 0xff;
	public final static int UINT8_MIN_VAL = 0;
	public final static int INT8_MAX_VAL = 127;
	public final static int INT8_MIN_VAL = -128;

	public final static int UINT16_MAX_VAL = 0xffff;
	public final static int UINT16_MIN_VAL = 0;
	public final static int INT16_MAX_VAL = 32767;
	public final static int INT16_MIN_VAL = -32768;

	public final static long UINT32_MAX_VAL = 0xffffffffL;
	public final static long UINT32_MIN_VAL = 0;
	public final static long INT32_MAX_VAL = Integer.MAX_VALUE;
	public final static long INT32_MIN_VAL = Integer.MIN_VALUE;
	
	/**
	 * Read a 8 bit signed integer from a byte buffer at a specified offset.
	 * @param buffer The byte buffer containing the integer value.
	 * @param offset The offset, where to start to read inside the byte buffer.
	 * @return The 8 bit signed integer value.
	 */
	public static byte getInt8(byte [] buffer, int offset) {
		short value = buffer[offset]; 
		
		assert(value >= INT8_MIN_VAL && value <= INT8_MAX_VAL);
		
		return (byte) value;
	}
	
	/**
	 * Read a 8 bit unsigned integer from a byte buffer at a specified offset.
	 * @param buffer The byte buffer containing the integer value.
	 * @param offset The offset, where to start to read inside the byte buffer.
	 * @return The 8 bit unsigned integer value.
	 */
	public static short getUInt8(byte [] buffer, int offset) {
		short value = (short) (buffer[offset]&0x7f);
		if(buffer[offset]<0)	value |= 0x0080;
		
		assert(value >= UINT8_MIN_VAL && value <= UINT8_MAX_VAL);

		return value;
	}
	
	/**
	 * Read a 16 bit (little endian) signed integer from a byte
	 * buffer at a specified offset.
	 * @param buffer The byte buffer containing the integer value.
	 * @param offset The offset, where to start to read inside the byte buffer.
	 * @return The 16 bit signed integer value.
	 */
	public static short getInt16(byte [] buffer, int offset) {
		int word = (((buffer[offset+1]&0x7f) <<8) | (buffer[offset]&0x7f));
		if(buffer[offset]<0)	word |= 0x0080;
		if(buffer[offset+1]<0)	word = INT16_MIN_VAL+word;

		assert(word >= INT16_MIN_VAL && word <= INT16_MAX_VAL);
		
		return (short) word;
	}

	/**
	 * Read a 16 bit (little endian) unsigned integer
	 * from a byte buffer at a specified offset.
	 * @param buffer The byte buffer containing the integer value.
	 * @param offset The offset, where to start to read inside the byte buffer.
	 * @return The 16 bit unsigned integer value.
	 */
	public static int getUInt16(byte [] buffer, int offset) {
		int word = (((buffer[offset+1]&0x7f) <<8) | (buffer[offset]&0x7f));
		if(buffer[offset+1]<0)	word |= 0x8000;
		if(buffer[offset]<0)	word |= 0x0080;

		assert(word >= UINT16_MIN_VAL && word <= UINT16_MAX_VAL);
		
		return word;
	}
	
	/**
	 * Read a 32 bit (little endian) signed integer
	 * from a byte buffer at a specified offset.
	 * @param buffer The byte buffer containing the integer value.
	 * @param offset The offset, where to start to read inside the byte buffer.
	 * @return The 32 bit signed integer value.
	 */
	public static int getInt32(byte [] buffer, int offset) {
		long doubleWord = (((buffer[offset+3]&0x7f) <<24) | ((buffer[offset+2]&0x7f) <<16) | ((buffer[offset+1]&0x7f) <<8) | (buffer[offset]&0x7f));
		if(buffer[offset+2]<0)	doubleWord |= 0x00800000;
		if(buffer[offset+1]<0)	doubleWord |= 0x00008000;
		if(buffer[offset]<0)	doubleWord |= 0x00000080;
		if(buffer[offset+3]<0)	doubleWord = INT32_MIN_VAL+doubleWord;
		
		assert(doubleWord >= INT32_MIN_VAL && doubleWord <= INT32_MAX_VAL);

		return (int) doubleWord;
	}
	
	/**
	 * Read a 32 bit (little endian) unsigned integer
	 * from a byte buffer at a specified offset.
	 * @param buffer The byte buffer containing the integer value.
	 * @param offset The offset, where to start to read inside the byte buffer.
	 * @return The 32 bit unsigned integer value.
	 */
	public static long getUInt32(byte [] buffer, int offset) {
		long doubleWord = (((buffer[offset+3]&0x7f) <<24) | ((buffer[offset+2]&0x7f) <<16) | ((buffer[offset+1]&0x7f) <<8) | (buffer[offset]&0x7f));
		if(buffer[offset+3]<0)	doubleWord |= 0x80000000L;
		if(buffer[offset+2]<0)	doubleWord |= 0x00800000L;
		if(buffer[offset+1]<0)	doubleWord |= 0x00008000L;
		if(buffer[offset]<0)	doubleWord |= 0x00000080L;
		
		assert(doubleWord >= UINT32_MIN_VAL && doubleWord <= UINT32_MAX_VAL);

		return doubleWord;
	}
	
	/**
	 * Read a 64 bit (little endian) signed (long) integer
	 * from a byte buffer at a specified offset.
	 * @param buffer The byte buffer containing the integer value.
	 * @param offset The offset, where to start to read inside the byte buffer.
	 * @return The 64 bit signed (long) integer value.
	 */
	public static long getInt64(byte [] buffer, int offset) {
		long qWord = (((long)(buffer[offset+7]&0x7f) <<56) | ((long)(buffer[offset+6]&0x7f) <<48) | ((long)(buffer[offset+5]&0x7f) <<40) | ((long)(buffer[offset+4]&0x7f) <<32) | 
				((buffer[offset+3]&0x7f) <<24) | ((buffer[offset+2]&0x7f) <<16) | ((buffer[offset+1]&0x7f) <<8) | (buffer[offset]&0x7f));

		if(buffer[offset+7]<0)	qWord |= 0x8000000000000000L;
		if(buffer[offset+6]<0)	qWord |= 0x0080000000000000L;
		if(buffer[offset+5]<0)	qWord |= 0x0000800000000000L;
		if(buffer[offset+4]<0)	qWord |= 0x0000008000000000L;
		if(buffer[offset+3]<0)	qWord |= 0x0000000080000000L;
		if(buffer[offset+2]<0)	qWord |= 0x0000000000800000L;
		if(buffer[offset+1]<0)	qWord |= 0x0000000000008000L;
		if(buffer[offset]<0)	qWord |= 0x0000000000000080L;
		
		//no range test because long can only handle long values
		
		return qWord;
	}
	
	/**
	 * Read a 64 bit (little endian) unsigned (long) integer
	 * from a byte buffer at a specified offset.
	 * @param buffer The byte buffer containing the integer value.
	 * @param offset The offset, where to start to read inside the byte buffer.
	 * @return The 64 bit unsigned (long) integer value.
	 */
	public static long getUInt64(byte [] buffer, int offset) {
		long qWord = (((long)(buffer[offset+7]&0x7f) <<56) | ((long)(buffer[offset+6]&0x7f) <<48) | ((long)(buffer[offset+5]&0x7f) <<40) | ((long)(buffer[offset+4]&0x7f) <<32) | 
				((buffer[offset+3]&0x7f) <<24) | ((buffer[offset+2]&0x7f) <<16) | ((buffer[offset+1]&0x7f) <<8) | (buffer[offset]&0x7f));

		if(buffer[offset+6]<0)	qWord |= 0x0080000000000000L;
		if(buffer[offset+5]<0)	qWord |= 0x0000800000000000L;
		if(buffer[offset+4]<0)	qWord |= 0x0000008000000000L;
		if(buffer[offset+3]<0)	qWord |= 0x0000000080000000L;
		if(buffer[offset+2]<0)	qWord |= 0x0000000000800000L;
		if(buffer[offset+1]<0)	qWord |= 0x0000000000008000L;
		if(buffer[offset]<0)	qWord |= 0x0000000000000080L;

		if(buffer[offset+7]<0)	qWord = Long.MIN_VALUE+qWord;
		
		//no range test because long can only handle long values
		
		return qWord;
	}

	/**
	 * Copy an part of the length {@code length} from the byte
	 * array {@code data} at the offset {@code offset}.
	 * @param data The source byte array.
	 * @param offset The offset where to start the array copy.
	 * @param length The length of the resulting array.
	 * @return A partially copy of the source buffer with the specifies length.
	 */
	public static byte[] getBytes(byte[] data, int offset, int length) {
		if(length==0) return null;
		
		byte [] buffer = new byte [length];
		
		System.arraycopy(data,offset,buffer,0,length);
		
		return buffer;
	}
	
	/**
	 * Calculate an offset which is align to the next starting
	 * DWORD (1 up to 4 bytes more than the previous offset).
	 * @param offset The current offset value, which has to be aligned.
	 * @return The DWORD aligned offset.
	 */
	public static int alingToNextDWord(int offset) {
		return offset + (4-offset%4);
	}
	
	/**
	 * Calculate an offset which is DWORD aligned (0 up to 3 bytes
	 * more than the previous offset).
	 * @param offset The current offset value, which has to be aligned.
	 * @return The DWORD aligned offset.
	 */
	public static int alingToDWord(int offset) {
		int correction = (4-offset%4);
		return offset + (correction!=4 ? correction : 0);
	}

	/**
	 * Calculate an offset which is align to the next starting
	 * WORD (1 up to 2 bytes more than the previous offset).
	 * @param offset The current offset value, which has to be aligned.
	 * @return The WORD aligned offset.
	 */
	public static int alingToNextWord(int offset) {
		return offset + (2-offset%2);
	}
	
	/**
	 * Calculate an offset which is WORD aligned (0 up to 1 bytes
	 * more than the previous offset).
	 * @param offset The current offset value, which has to be aligned.
	 * @return The WORD aligned offset.
	 */
	public static int alingToWord(int offset) {
		int correction = (2-offset%2);
		return offset + (correction!=2 ? correction : 0);
	}
	
	public static byte[] getUTF16PrepairedBytes(byte[] data, int offset, int length) {
		if(data==null)
			return null;
		
		byte [] buffer = ByteReader.getBytes(data, offset, length);
		byte swapElement;

		if(buffer==null)
			return null;
		
		assert((buffer.length&1)==0);
		
		for(int i=0; i<buffer.length; i+=2) {
			swapElement = buffer[i];
			buffer[i] = buffer[i+1];
			buffer[i+1] = swapElement;
		}
		
		return buffer;
	}
	
	public static int getSizeOfHeapLength(int heapObjectSize) {
		
		assert(heapObjectSize >= 0);
		
		if(heapObjectSize>0x3fff)	return 4;
		if(heapObjectSize>0x7f)		return 2;
		
		return 1;
	}

	
	public static int readHeapObjectSize(byte[] data, int offset) {
		
		int prefix = ByteReader.getUInt8(data, offset);
		
		if((prefix&0x80)==0x00)	return prefix;	
		if((prefix&0xc0)==0x80)	return ((prefix&0x3f)<<8) + ByteReader.getUInt8(data, offset+1); 
		
		assert((prefix&0xc0)==0xc0);
		
		return 	((prefix&0x1f)<<24) +
				(ByteReader.getUInt8(data, offset+1)<<16) +
				(ByteReader.getUInt8(data, offset+2)<<8) +
				ByteReader.getUInt8(data, offset+3);
	}
	
	public static int getSizeOfSignatureElement(int signatureObjectSize) {
		
		if(signatureObjectSize>0x3fff)	return 4;
		if(signatureObjectSize>0x7f)	return 2;
		
		return 1;
	}
	
	public static int decodeSignatureElement(byte[] data, int offset) {
		
		int prefix = ByteReader.getUInt8(data, offset);

		if((prefix&0x80)==0x00) return prefix;
		if((prefix&0xff)==0xff)	return -1; //reserved for null entries (negative value also works with getSizeOfSignatureElement(long))
		if((prefix&0xc0)==0x80)	return ((prefix&0x3f)<<8) + ByteReader.getUInt8(data, offset+1); 
		
		assert((prefix&0xe0)==0xc0);
		
		return 	((prefix&0x1f)<<24) +
				(ByteReader.getUInt8(data, offset+1)<<16) +
				(ByteReader.getUInt8(data, offset+2)<<8) +
				ByteReader.getUInt8(data, offset+3);
	}
	
	/**
	 * Decode a 32 bit float value from a byte buffer at a specified offset.
	 * @param data The byte buffer which contains the float value.
	 * @param offset The offset where to start inside the byte buffer.
	 * @return A {@code float} value.
	 */
	public static float getFloat(byte [] data, int offset) {
		Float value = Float.intBitsToFloat(ByteReader.getInt32(data, offset));
		
		return value;
	}

	/**
	 * Decode a 64 bit double value from a byte buffer at a specified offset.
	 * @param data The byte buffer which contains the double value.
	 * @param offset The offset where to start inside the byte buffer.
	 * @return A {@code double} value.
	 */
	public static double getDouble(byte [] data, int offset) {
		Double value = Double.longBitsToDouble(ByteReader.getInt64(data, offset));
		
		return value;
	}
	
	/**
	 * Test if all of the given {@code testBits} are set in {@code flags}, which is masked with {@code mask}.
	 * @param flags The flags to check.
	 * @param mask The bit mask which marks the flags, which are relevant for the test.
	 * @param testBits The bits which have to be set in order to fulfill a positive test.
	 * @return {@code true} if all required bits are set, otherwise {@code false}.
	 */
	public static boolean testFlags(long flags, long mask, long testBits){
		return (mask&flags)==testBits;
	}
	
	/**
	 * Test if all of the given {@code testBits} are set in {@code flags}, which is masked with {@code mask}.
	 * @param flags The flags to check.
	 * @param mask The bit mask which marks the flags, which are relevant for the test.
	 * @param testBits The bits which have to be set in order to fulfill a positive test.
	 * @return {@code true} if all required bits are set, otherwise {@code false}.
	 */
	public static boolean testFlags(int flags, int mask, int testBits){
		return (mask&flags)==testBits;
	}
	
	public static boolean testFlags(byte flags, byte mask, byte testBits){
		return (mask&flags)==testBits;
	}
	
	/**
	 * Test if all of the given {@code testBits} are set in {@code flags}.
	 * @param flags The flags to check.
	 * @param testBits The bits which have to be set in order to fulfill a positive test.
	 * @return {@code true} if all required bits are set, otherwise {@code false}.
	 */
	public static  boolean testFlags(long flags, long testBits){
		return (flags&testBits)==testBits;
	}
	
	/**
	 * Test if all of the given {@code testBits} are set in {@code flags}.
	 * @param flags The flags to check.
	 * @param testBits The bits which have to be set in order to fulfill a positive test.
	 * @return {@code true} if all required bits are set, otherwise {@code false}.
	 */
	public static  boolean testFlags(int flags, int testBits){
		return (flags&testBits)==testBits;
	}
	
	public static  boolean testFlags(byte flags, byte testBits){
		return (flags&testBits)==testBits;
	}
	
	/**
	 * Test if any of the given {@code testBits} is set in {@code flags}.
	 * @param flags The flags to check.
	 * @param testBits The bits, where at least one has to be set in order to fulfill a positive test.
	 * @return {@code true} if any of the required bits is set, otherwise {@code false}.
	 */
	public static  boolean testAny(long flags, long testBits){
		return (flags&testBits)!=0;
	}
	
	/**
	 * Test if any of the given {@code testBits} is set in {@code flags}.
	 * @param flags The flags to check.
	 * @param testBits The bits, where at least one has to be set in order to fulfill a positive test.
	 * @return {@code true} if any of the required bits is set, otherwise {@code false}.
	 */
	public static  boolean testAny(int flags, int testBits){
		return (flags&testBits)!=0;
	}
	
	public static  boolean testAny(byte flags, byte testBits){
		return (flags&testBits)!=0;
	}
	
	/**
	 * Set {@code flagsToSet} in {@code flags} to a boolean value. 
	 * @param flags The base of flags, which is getting manipulated. 
	 * @param flagsToSet The flags which are getting set or cleared in the result.
	 * @param set {@code true} sets all {@code flagsToSet}, {@code false} clears all {@code flagsToSet}.
	 * @return The result flags of the operation.
	 */
	public static long setFlags(long flags, long flagsToSet, boolean set) {
		if(set) return flags |= flagsToSet;
		
		return flags &= ~flagsToSet;
	}
	
	/**
	 * Set {@code flagsToSet} in {@code flags} to a boolean value. 
	 * @param flags The base of flags, which is getting manipulated. 
	 * @param flagsToSet The flags which are getting set or cleared in the result.
	 * @param set {@code true} sets all {@code flagsToSet}, {@code false} clears all {@code flagsToSet}.
	 * @return The result flags of the operation.
	 */
	public static int setFlags(int flags, int flagsToSet, boolean set) {
		if(set) return flags |= flagsToSet;
		
		return flags &= ~flagsToSet;
	}
	
	public static byte setFlags(byte flags, byte flagsToSet, boolean set) {
		if(set) return flags |= flagsToSet;
		
		return flags &= ~flagsToSet;
	}

}

package at.pollaknet.api.facile.tests.utils;

import java.io.UnsupportedEncodingException;

import at.pollaknet.api.facile.util.ByteReader;
import junit.framework.TestCase;

public class ByteReaderTests extends TestCase {

	/*
	 * Test method for 'facile.util.ByteReader.getInt8(byte[], int)'
	 */
	public void testGetInt8() {
		
		byte [] numbers = { (byte) 0xaa, 0x55, 0x00, (byte) 0xff, 0x7f, (byte) 0x80}; 
		
		assertTrue(ByteReader.getInt8(numbers,0) == -86);
		assertTrue(ByteReader.getInt8(numbers,1) == 85);
		assertTrue(ByteReader.getInt8(numbers,2) == 0);
		assertTrue(ByteReader.getInt8(numbers,3) == -1);
		assertTrue(ByteReader.getInt8(numbers,4) == 127);
		assertTrue(ByteReader.getInt8(numbers,5) == -128);
	}

	/*
	 * Test method for 'facile.util.ByteReader.getUInt8(byte[], int)'
	 */
	public void testGetUInt8() {
		byte [] numbers = { (byte) 0xaa, 0x55, 0x00, (byte) 0xff, 0x7f, (byte) 0x80}; 
		
		assertTrue(ByteReader.getUInt8(numbers,0) == 170);
		assertTrue(ByteReader.getUInt8(numbers,1) == 85);
		assertTrue(ByteReader.getUInt8(numbers,2) == 0);
		assertTrue(ByteReader.getUInt8(numbers,3) == 255);
		assertTrue(ByteReader.getUInt8(numbers,4) == 127);
		assertTrue(ByteReader.getUInt8(numbers,5) == 128);
	}

	/*
	 * Test method for 'facile.util.ByteReader.getInt16(byte[], int)'
	 */
	public void testGetInt16() {
		byte [] numbers = { (byte) 0xaa, (byte) 0xaa, 0x55, 0x55, 0x00, 0x00,
							(byte) 0xff, (byte) 0xff, (byte) 0xff,  0x7f, 0x00, (byte) 0x80}; 
		
		assertTrue(ByteReader.getInt16(numbers,0) == -21846);
		assertTrue(ByteReader.getInt16(numbers,2) == 21845);
		assertTrue(ByteReader.getInt16(numbers,4) == 0);
		assertTrue(ByteReader.getInt16(numbers,6) == -1);
		assertTrue(ByteReader.getInt16(numbers,8) == 32767);
		assertTrue(ByteReader.getInt16(numbers,10) == -32768);
	}

	/*
	 * Test method for 'facile.util.ByteReader.getUInt16(byte[], int)'
	 */
	public void testGetUInt16() {
		byte [] numbers = { (byte) 0xaa, (byte) 0xaa, 0x55, 0x55, 0x00, 0x00,
							(byte) 0xff, (byte) 0xff, (byte) 0xff,  0x7f, 0x00, (byte) 0x80}; 
		
		assertTrue(ByteReader.getUInt16(numbers,0) == 43690);
		assertTrue(ByteReader.getUInt16(numbers,2) == 21845);
		assertTrue(ByteReader.getUInt16(numbers,4) == 0);
		assertTrue(ByteReader.getUInt16(numbers,6) == 65535);
		assertTrue(ByteReader.getUInt16(numbers,8) == 32767);
		assertTrue(ByteReader.getUInt16(numbers,10) == 32768);
	}

	/*
	 * Test method for 'facile.util.ByteReader.getInt32(byte[], int)'
	 */
	public void testGetInt32() {
		byte [] numbers = { (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
							0x55, 0x55, 0x55, 0x55,
							0x00, 0x00, 0x00, 0x00,
							(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
							(byte) 0xff, (byte) 0xff, (byte) 0xff,  0x7f,
							0x00, 0x00 , 0x00, (byte) 0x80,
							(byte) 0xff, 0x00, (byte) 0xff,  0x7f,
							0x00, (byte) 0xff , 0x00, (byte) 0x80 	}; 
		
		assertTrue(ByteReader.getInt32(numbers,0) == -1431655766);
		assertTrue(ByteReader.getInt32(numbers,4) == 1431655765);
		assertTrue(ByteReader.getInt32(numbers,8) == 0);
		assertTrue(ByteReader.getInt32(numbers,12) == -1);
		assertTrue(ByteReader.getInt32(numbers,16) == 2147483647);
		assertTrue(ByteReader.getInt32(numbers,20) == -2147483648);
		assertTrue(ByteReader.getInt32(numbers,24) == 2147418367);
		assertTrue(ByteReader.getInt32(numbers,28) == -2147418368);
	}

	/*
	 * Test method for 'facile.util.ByteReader.getUInt32(byte[], int)'
	 */
	public void testGetUInt32() {
		byte [] numbers = { (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
				0x55, 0x55, 0x55, 0x55,
				0x00, 0x00, 0x00, 0x00,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0xff,  0x7f,
				0x00, 0x00 , 0x00, (byte) 0x80,
				(byte) 0xff, 0x00, (byte) 0xff,  0x7f,
				0x00, (byte) 0xff , 0x00, (byte) 0x80 	}; 

		assertTrue(ByteReader.getUInt32(numbers,0) == 2863311530L);
		assertTrue(ByteReader.getUInt32(numbers,4) == 1431655765);
		assertTrue(ByteReader.getUInt32(numbers,8) == 0);
		assertTrue(ByteReader.getUInt32(numbers,12) == 4294967295L);
		assertTrue(ByteReader.getUInt32(numbers,16) == 2147483647);
		assertTrue(ByteReader.getUInt32(numbers,20) == 2147483648L);
		assertTrue(ByteReader.getUInt32(numbers,24) == 2147418367);
		assertTrue(ByteReader.getUInt32(numbers,28) == 2147548928L);
	}

	/*
	 * Test method for 'facile.util.ByteReader.getInt64(byte[], int)'
	 */
	public void testGetInt64() {
		byte [] numbers = { (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
				0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,  0x7f,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00 , 0x00, (byte) 0x80,
				0x00, (byte) 0xff, 0x00, (byte)  0xff, 0x00, 0x00, (byte) 0xff,  0x7f,
				(byte) 0xff, 0x00, (byte) 0xff,  0x00, (byte) 0xff, (byte) 0xff, 0x00,  (byte) 0x80,
				}; 

		assertTrue(ByteReader.getInt64(numbers,0) == -6148914691236517206L);
		assertTrue(ByteReader.getInt64(numbers,8) == 6148914691236517205L);
		assertTrue(ByteReader.getInt64(numbers,16) == 0);
		assertTrue(ByteReader.getInt64(numbers,24) == -1);
		assertTrue(ByteReader.getInt64(numbers,32) == 9223372036854775807L);
		assertTrue(ByteReader.getInt64(numbers,40) == -9223372036854775808L);
		assertTrue(ByteReader.getInt64(numbers,48) == 9223090566156320512L);
		assertTrue(ByteReader.getInt64(numbers,56) == -9223090566156320513L);
	}

	/*
	 * Test method for 'facile.util.ByteReader.getBytes(byte[], int, int)'
	 */
	public void testGetBytes() {
		byte [] byteArray = {	(byte) 0xaa, 0x55, 0x0a, 0x2a, 0x42, 0x53, 0x4a, 0x42, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00,
				 				0x00, 0x0c, 0x00, 0x00, 0x00, 0x76, 0x32, 0x2e, 0x30, 0x2e, 0x35, 0x30, 0x37, 0x32, 0x37, 0x00 };

		//0-length
		assertTrue(ByteReader.getBytes(byteArray, 0, 0).length==0);
		assertTrue(ByteReader.getBytes(byteArray, 10, 0).length==0);
		
		//read the first two test bytes
		int index = 0;
		for(byte b : ByteReader.getBytes(byteArray, 0, 2)) {
			assertTrue(b == byteArray[index]);
			index++;
		}
		
		//read first part of the signature
		for(byte b : ByteReader.getBytes(byteArray, 2, 3)) {
			assertTrue(b == byteArray[index]);
			index++;
		}
		
		//continue with some signature bytes
		for(byte b : ByteReader.getBytes(byteArray, 5, 4)) {
			assertTrue(b == byteArray[index]);
			index++;
		}
		
		//read a few bytes from the version string
		index=19;
		for(byte b : ByteReader.getBytes(byteArray, 19, 5)) {
			assertTrue(b == byteArray[index]);
			index++;
		}
		
		//usage as string (default encoding is utf8)
		assertEquals("*BSJB", new String(ByteReader.getBytes(byteArray, 3, 5)));
		assertEquals("v2.0.50727", new String(ByteReader.getBytes(byteArray, 20, 10)));
		
	}
	
	/*
	 * Test method for 'facile.util.ByteReader.alingToNextDWord(int)'
	 */
	public void testAlignToNextDWord() {
		
		//test if the return value matches the next DWord bounds
		assertEquals(ByteReader.alingToNextDWord(0),4);
		assertEquals(ByteReader.alingToNextDWord(1),4);
		assertEquals(ByteReader.alingToNextDWord(2),4);
		assertEquals(ByteReader.alingToNextDWord(3),4);
		assertEquals(ByteReader.alingToNextDWord(4),8);
		assertEquals(ByteReader.alingToNextDWord(7),8);
		assertEquals(ByteReader.alingToNextDWord(8),12);
		assertEquals(ByteReader.alingToNextDWord(129),132);
		assertEquals(ByteReader.alingToNextDWord(1021),1024);
		assertEquals(ByteReader.alingToNextDWord(8096),8100);
	}
	
	/*
	 * Test method for 'facile.util.ByteReader.getUTF16PreparedBytes(byte[], int, int)'
	 */
	public void testGetUTF16PreparedBytes() {
		byte [] byteArray = {	(byte) 0xaa, 0x55, 0x0a, 0x2a, 0x00, 0x42, 0x00, 0x53, 0x00, 0x4a, 0x00, 0x42, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00,
 				0x00, 0x0c, 0x00, 0x00, 0x00, 0x76, 0x00, 0x32, 0x00, 0x2e, 0x00, 0x30, 0x00, 0x2e, 0x00, 0x35, 0x00, 0x30, 0x00, 0x37, 0x00, 0x32, 0x00, 0x37, 0x00, 0x00 };

		try {
			//build a string out of a byte array, which has an UTF16 encoding with the order low byte before high byte
			assertEquals(new String(ByteReader.getUTF16PreparedBytes(byteArray, 3, 10),"UTF16"),"*BSJB");
			assertEquals(new String(ByteReader.getUTF16PreparedBytes(byteArray, 25, 20),"UTF16"),"v2.0.50727");
			
			//test an invalid UTF16 length of 9 bytes (will succeed here since the missing byte is filled up with a zero)
			assertEquals(new String(ByteReader.getUTF16PreparedBytes(byteArray, 3, 9),"UTF16"),"*BSJB");
		} catch (UnsupportedEncodingException e) {
			fail(e.getMessage());
		}
		
	}
	
	/*
	 * Test method for 'facile.util.ByteReader.getSizeOfHeapLength(long)'
	 */
	public void testGetSizeOfHeapLength() {
	
		assertEquals(ByteReader.getSizeOfHeapLength(0),1);
		assertEquals(ByteReader.getSizeOfHeapLength(3),1);
		assertEquals(ByteReader.getSizeOfHeapLength(17),1);
		assertEquals(ByteReader.getSizeOfHeapLength(32),1);
		assertEquals(ByteReader.getSizeOfHeapLength(127),1);
		assertEquals(ByteReader.getSizeOfHeapLength(128),2);
		assertEquals(ByteReader.getSizeOfHeapLength(512),2);
		assertEquals(ByteReader.getSizeOfHeapLength(16383),2);
		assertEquals(ByteReader.getSizeOfHeapLength(16384),4);
		assertEquals(ByteReader.getSizeOfHeapLength(65536),4);
		assertEquals(ByteReader.getSizeOfHeapLength(72392302),4);
		assertEquals(ByteReader.getSizeOfHeapLength(2147483647),4);
		
	}

	/*
	 * Test method for 'facile.util.ByteReader.readHeapObjectSize(byte[], int)'
	 */
	public void testReadHeapObjectSize() {
		byte [] byteArray = { 0x00, 0x01, 0x12, 0x7f, (byte) 0x80, (byte) 0x80, (byte) 0x82, 0x03, (byte) 0xbf, (byte) 0xff,
				(byte) 0xc0, 0x00, 0x40, 0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
				
		assertEquals(ByteReader.readHeapObjectSize(byteArray, 0),0); //heap entry with 0 data bytes
		assertEquals(ByteReader.readHeapObjectSize(byteArray, 1),1); //heap entry with 1 data byte
		assertEquals(ByteReader.readHeapObjectSize(byteArray, 2),18); //heap entry with 18 data bytes
		assertEquals(ByteReader.readHeapObjectSize(byteArray, 3),127); //max with 0 followed byte
		assertEquals(ByteReader.readHeapObjectSize(byteArray, 4),128); //min with 1 followed byte
		assertEquals(ByteReader.readHeapObjectSize(byteArray, 6),515); //heap entry with 515 data bytes (1 followed byte)
		assertEquals(ByteReader.readHeapObjectSize(byteArray, 8),16383); //max with 1 followed byte
		assertEquals(ByteReader.readHeapObjectSize(byteArray, 10),16384); //min with 3 followed bytes
		assertEquals(ByteReader.readHeapObjectSize(byteArray, 14),536870911); //reachable max
	}
}

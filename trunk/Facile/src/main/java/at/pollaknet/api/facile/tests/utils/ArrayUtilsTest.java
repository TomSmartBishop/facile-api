package at.pollaknet.api.facile.tests.utils;

import at.pollaknet.api.facile.util.ArrayUtils;
import junit.framework.TestCase;

public class ArrayUtilsTest extends TestCase {

	public void testContains() {
		int intArray [] = { 0, 2, 0 ,4, 112, 1345, 5968, 9, 0, 40, 892374, 337, 1 };
		
		assertTrue(ArrayUtils.contains(intArray, 0));
		assertTrue(ArrayUtils.contains(intArray, 1345));
		assertTrue(ArrayUtils.contains(intArray, 9));
		assertTrue(ArrayUtils.contains(intArray, 892374));
		assertTrue(ArrayUtils.contains(intArray, 1));
		
		assertFalse(ArrayUtils.contains(intArray, -1));
		assertFalse(ArrayUtils.contains(intArray, 3));
		assertFalse(ArrayUtils.contains(intArray, 113));
		assertFalse(ArrayUtils.contains(intArray, 892375));
		assertFalse(ArrayUtils.contains(intArray, -40));
	}

	public void testFormatByteArray() {
		byte [] array1 = { (byte) 0xaa, (byte) 0xaa, 0x55, 0x55, 0x00 };
		byte [] array2 = { (byte) 0xff, (byte) 0xff, (byte) 0xff,  0x7f, 0x00, (byte) 0x80};
		
		assertEquals("aa aa 55 55 00", ArrayUtils.formatByteArray(array1));
		assertEquals("ff ff ff 7f 00 80", ArrayUtils.formatByteArray(array2));
	}

	public void testByteArraysAreEqual() {
		byte [] array0 = { (byte) 0xaa, (byte) 0xaa, 0x54, 0x55, 0x00 };
		byte [] array1 = { (byte) 0xaa, (byte) 0xaa, 0x55, 0x55, 0x00 };
		byte [] array2 = { (byte) 0xaa, (byte) 0xaa, 0x55, 0x55, 0x00 };
		byte [] array3 = { (byte) 0xaa, (byte) 0xaa, 0x55, 0x55, 0x00, 0x12 };
		byte [] array4 = { (byte) 0xff, (byte) 0xff, (byte) 0xff,  0x7f, 0x00, (byte) 0x80};
		
		assertTrue(ArrayUtils.arraysAreEqual(array1, array1));
		assertTrue(ArrayUtils.arraysAreEqual(array4, array4));
		
		assertTrue(ArrayUtils.arraysAreEqual(array1, array2));
		assertTrue(ArrayUtils.arraysAreEqual(array2, array1));
		
		assertFalse(ArrayUtils.arraysAreEqual(array1, array3));
		assertFalse(ArrayUtils.arraysAreEqual(array3, array1));
		
		assertFalse(ArrayUtils.arraysAreEqual(array0, array4));
		assertFalse(ArrayUtils.arraysAreEqual(array4, array0));
		
		assertFalse(ArrayUtils.arraysAreEqual(array0, array1));
		assertFalse(ArrayUtils.arraysAreEqual(array1, array0));
	}
	
	/*
	 * Test method for 'facile.util.ArrayUtils.findInByteArray(byte[], int, byte)'
	 */
	public void testFindInByteArray()  {
		byte [] byteArray = {	(byte) 0xaa, 0x55, 0x0a, 0x2a, 0x42, 0x53, 0x4a, 0x42, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00,
 				0x00, 0x0c, 0x00, 0x00, 0x00, 0x76, 0x32, 0x2e, 0x30, 0x2e, 0x35, 0x30, 0x37, 0x32, 0x37, 0x00 };

		//search for a byte inside the byte array
		assertEquals(ArrayUtils.findInByteArray(byteArray, 0, (byte) 0x4a), 6);
		assertEquals(ArrayUtils.findInByteArray(byteArray, 0, (byte) 0x00), 9);
		assertEquals(ArrayUtils.findInByteArray(byteArray, 9, (byte) 0x00), 0);
		assertEquals(ArrayUtils.findInByteArray(byteArray, 20, (byte) 0x00), 10);
		assertEquals(ArrayUtils.findInByteArray(byteArray, 3, (byte) 0x37), 24);
		
		assertEquals(ArrayUtils.findInByteArray(byteArray, 3, (byte) 0xfe), -1);
	}

}

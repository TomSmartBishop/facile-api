package at.pollaknet.api.facile.tests.assemblies;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.util.ArrayUtils;
import junit.framework.TestCase;

public class FacileMscorlibV4_0_30319Tests extends TestCase {

	private final static String PATH_MSCORLIBV4_0_30319_32 = "assemblies/v4.0.30319_32/mscorlib.dll";
	private final static String PATH_MSCORLIBV4_0_30319_64 = "assemblies/v4.0.30319_64/mscorlib.dll";
	private final static int NUM_OF_TEST_CASES = 5;
	private static FacileReflector reflector32 = null;
	private static FacileReflector reflector64 = null;
	private static int testCounter = 0;
	
	public FacileMscorlibV4_0_30319Tests(String name) {
		super(name);
	}
	
	/**
    * Sets up of basic fields. 
    * (Called before every test case method.) 
    */ 
   protected void setUp() { 
	   if(testCounter==0) {
			try {
				reflector32 = Facile.load(PATH_MSCORLIBV4_0_30319_32);
			} catch (Exception e) {
				fail();
			}
			try {
				reflector64 = Facile.load(PATH_MSCORLIBV4_0_30319_64);
			} catch (Exception e) {
				fail();
			}
  		}
	   testCounter++;
	}
   
   protected void tearDown() {
	   if(testCounter==NUM_OF_TEST_CASES) {
		   reflector32 = null;
	   }
   }

	public void testCliHeader() {
					
		assertEquals(reflector32.getCliHeader().getClrHeaderSize(), 72);
		assertEquals(reflector32.getCliHeader().getMajorRuntimeVersion(), 2);
		assertEquals(reflector32.getCliHeader().getMinorRuntimeVersion(), 5);
		assertEquals(reflector32.getCliHeader().getAddrOfMetadataDirectory(), 2361488);
		assertEquals(reflector32.getCliHeader().getSizeOfMetadataDirectory(), 2102204);
		assertEquals(reflector32.getCliHeader().getFlags(), 11);
		
		assertEquals(reflector64.getCliHeader().getClrHeaderSize(), 72);
		assertEquals(reflector64.getCliHeader().getMajorRuntimeVersion(), 2);
		assertEquals(reflector64.getCliHeader().getMinorRuntimeVersion(), 5);
		assertEquals(reflector64.getCliHeader().getAddrOfMetadataDirectory(), 0x002408d8);
		assertEquals(reflector64.getCliHeader().getSizeOfMetadataDirectory(), 2102620);
		assertEquals(reflector64.getCliHeader().getFlags(), 9);
	}
	
	public void testMetadataRootHeader() {
		assertEquals(reflector32.getCliMetadataRootHeader().getSize(), 105);		
		assertEquals(reflector32.getCliMetadataRootHeader().getMajorVersion(), 1);
		assertEquals(reflector32.getCliMetadataRootHeader().getMinorVersion(), 1);		
		assertEquals(reflector32.getCliMetadataRootHeader().getExtraDataOffset(), 0);
		assertEquals(reflector32.getCliMetadataRootHeader().getVersionString(), "v4.0.30319\0\0");
		assertEquals(reflector32.getCliMetadataRootHeader().getFlags(), 0);		
		assertEquals(reflector32.getCliMetadataRootHeader().getNumberOfStreams(), 5);
		
		assertEquals(reflector64.getCliMetadataRootHeader().getSize(), 105);		
		assertEquals(reflector64.getCliMetadataRootHeader().getMajorVersion(), 1);
		assertEquals(reflector64.getCliMetadataRootHeader().getMinorVersion(), 1);		
		assertEquals(reflector64.getCliMetadataRootHeader().getExtraDataOffset(), 0);
		assertEquals(reflector64.getCliMetadataRootHeader().getVersionString(), "v4.0.30319\0\0");
		assertEquals(reflector64.getCliMetadataRootHeader().getFlags(), 0);		
		assertEquals(reflector64.getCliMetadataRootHeader().getNumberOfStreams(), 5);
	}
	
	public void testStreamSizes() {
		assertEquals(reflector32.getSizeOfMetadataStream(), 1250864);		
		assertEquals(reflector32.getSizeOfStringsStream(), 423536);
		assertEquals(reflector32.getSizeOfUserStringStream(), 214784);		
		assertEquals(reflector32.getSizeOfGuidStream(), 16);
		assertEquals(reflector32.getSizeOfBlobStream(), 212896);
		
		assertEquals(reflector64.getSizeOfMetadataStream(), 0x001317c0);		
		assertEquals(reflector64.getSizeOfStringsStream(), 0x0006769c);
		assertEquals(reflector64.getSizeOfUserStringStream(), 0x000346c8);		
		assertEquals(reflector64.getSizeOfGuidStream(), 0x00000010);
		assertEquals(reflector64.getSizeOfBlobStream(), 0x00033fbc);	
	}
	
	public void testStringsStream() {
		assertEquals("<Module>", reflector32.getStringsStream().getString(1));
		assertEquals("CommonLanguageRuntimeLibrary", reflector32.getStringsStream().getString(10));
		assertEquals("Object", reflector32.getStringsStream().getString(39));
		assertEquals("System", reflector32.getStringsStream().getString(46));
		assertEquals("System.Runtime.Serialization", reflector32.getStringsStream().getString(103));
		assertEquals("DBNull", reflector32.getStringsStream().getString(3400));
		assertEquals("MinValue", reflector32.getStringsStream().getString(61623));
		assertEquals("sLocalizedCountry", reflector32.getStringsStream().getString(219351));
		assertEquals("get_SerializationFormatter", reflector32.getStringsStream().getString(285866));
		assertEquals("System.Runtime.InteropServices._TypeBuilder.GetTypeInfoCount", reflector32.getStringsStream().getString(349606));
		assertEquals("System.Collections.Generic.IEnumerator<System.Globalization.CultureInfo>.Current", reflector32.getStringsStream().getString(421845));
		
		assertEquals("<Module>", reflector64.getStringsStream().getString(1));
		assertEquals("CommonLanguageRuntimeLibrary", reflector64.getStringsStream().getString(10));
		assertEquals("Object", reflector64.getStringsStream().getString(39));
		assertEquals("System", reflector64.getStringsStream().getString(46));
		assertEquals("System.Runtime.Serialization", reflector64.getStringsStream().getString(103));
		assertEquals("DBNull", reflector64.getStringsStream().getString(3429));
		assertEquals("MinValue", reflector64.getStringsStream().getString(61653));
		assertEquals("Write7BitEncodedInt", reflector64.getStringsStream().getString(233539));
		assertEquals("AllocateUninitializedObject", reflector64.getStringsStream().getString(316174));
		assertEquals("System.Runtime.InteropServices._TypeBuilder.GetTypeInfoCount", reflector64.getStringsStream().getString(349724));
		assertEquals("__StaticArrayInitTypeSize=4096", reflector64.getStringsStream().getString(423269));
	}
	
	public void testBlobStream() {
		byte [] blob1 = { 0x15, 0x12, (byte) 0x80, (byte) 0xc8, 0x01, 0x13, 0x00 };
		byte [] blob2 = { 0x15, 0x12, (byte) 0x8e, 0x34, 0x02, 0x13, 0x00, 0x15, 0x12, (byte) 0x8d, (byte) 0xa4, 0x01, 0x13, 0x00 };
		byte [] blob3 = { 0x00, 0x05, 0x02, 0x12, (byte) 0x84, (byte) 0xc0, 0x11, (byte) 0x90, 0x14, 0x0e, 0x02, 0x0e };
		byte [] blob4 = { 0x20, 0x0c, 0x01, 0x0e, 0x11, (byte) 0x9b, (byte) 0xdc, 0x11, (byte) 0x9b, (byte) 0xd0, 0x08, 0x02, 0x11, (byte) 0x9b, (byte) 0xe8, 0x08, 0x11, (byte) 0x9b, (byte) 0xe4, 0x12, (byte) 0x95, (byte) 0xa0, 0x0e, 0x02, 0x02 };
		byte [] blob5 = { 0x07, 0x0a, 0x11, (byte) 0xa9, (byte) 0xdc, 0x12, (byte) 0xaa, 0x0c, 0x12, (byte) 0xaa, 0x0c, 0x12,(byte)  0xa9, 0x1c, 0x12, (byte) 0xa9, 0x1c, 0x05, 0x08, 0x08, 0x08, 0x08 };
		
		assertTrue(ArrayUtils.arraysAreEqual(blob1, reflector32.getBlobStream().getBlob(1)));
		assertTrue(ArrayUtils.arraysAreEqual(blob2, reflector32.getBlobStream().getBlob(667)));
		assertTrue(ArrayUtils.arraysAreEqual(blob3, reflector32.getBlobStream().getBlob(32638)));
		assertTrue(ArrayUtils.arraysAreEqual(blob4, reflector32.getBlobStream().getBlob(84592)));
		assertTrue(ArrayUtils.arraysAreEqual(blob5, reflector32.getBlobStream().getBlob(211579)));
		
		byte [] blob6 = { 0x15, 0x12, (byte) 0x80, (byte) 0xc8, 0x01, 0x13, 0x00 };
		byte [] blob7 = { 0x15, 0x12, (byte) 0x8e, 0x38, 0x02, 0x13, 0x00, 0x15, 0x12, (byte) 0x8d, (byte) 0xa8, 0x01, 0x13, 0x00 };
		byte [] blob8 = { 0x00, 0x05, 0x02, 0x12, (byte) 0x84, (byte) 0xc8, 0x11, (byte) 0x90, 0x18, 0x0e, 0x02, 0x0e };
		byte [] blob9 = { 0x06, 0x15, 0x12, (byte) 0x8d, 0x54, 0x02, 0x12, (byte) 0x84, (byte) 0xc8, 0x12, (byte) 0xa5, (byte) 0x90 };
		byte [] blob10 = { 0x07, 0x09, 0x11, (byte) 0x96, (byte) 0x98, 0x12, (byte) 0x97, 0x14, 0x12, (byte) 0x9f, 0x48, 0x11, (byte) 0x9f, (byte) 0xdc, 0x12, (byte) 0x97, 0x14, 0x12, (byte) 0x9f, 0x48, 0x1d, 0x11, (byte) 0x96, (byte) 0x98, 0x12, (byte) 0x80, (byte) 0xd8, 0x12, (byte) 0x80, (byte) 0xe8 };
		
		assertTrue(ArrayUtils.arraysAreEqual(blob6, reflector64.getBlobStream().getBlob(1)));
		assertTrue(ArrayUtils.arraysAreEqual(blob7, reflector64.getBlobStream().getBlob(667)));
		assertTrue(ArrayUtils.arraysAreEqual(blob8, reflector64.getBlobStream().getBlob(32638)));
		assertTrue(ArrayUtils.arraysAreEqual(blob9, reflector64.getBlobStream().getBlob(99988)));
		assertTrue(ArrayUtils.arraysAreEqual(blob10, reflector64.getBlobStream().getBlob(193276)));
	}

}

package at.pollaknet.api.facile.tests.assemblies;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.util.ArrayUtils;
import junit.framework.TestCase;

public class FacileMscorlibV4_0_30319Tests extends TestCase {

	private final static String PATH_MSCORLIBV4_0_30319 = "assemblies/v4.0.30319/mscorlib.dll";
	private final static int NUM_OF_TEST_CASES = 5;
	private static FacileReflector reflector = null;
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
				reflector = Facile.load(PATH_MSCORLIBV4_0_30319);
			} catch (Exception e) {
				fail();
			}
  		}
	   testCounter++;
	}
   
   protected void tearDown() {
	   if(testCounter==NUM_OF_TEST_CASES) {
		   reflector = null;
	   }
   }

	public void testCliHeader() {
					
		assertEquals(reflector.getCliHeader().getClrHeaderSize(), 72);
		assertEquals(reflector.getCliHeader().getMajorRuntimeVersion(), 2);
		assertEquals(reflector.getCliHeader().getMinorRuntimeVersion(), 5);
		assertEquals(reflector.getCliHeader().getAddrOfMetadataDirectory(), 2361488);
		assertEquals(reflector.getCliHeader().getSizeOfMetadataDirectory(), 2102204);
		assertEquals(reflector.getCliHeader().getFlags(), 11);
	}
	
	public void testMetadataRootHeader() {
		assertEquals(reflector.getCliMetadataRootHeader().getSize(), 105);		
		assertEquals(reflector.getCliMetadataRootHeader().getMajorVersion(), 1);
		assertEquals(reflector.getCliMetadataRootHeader().getMinorVersion(), 1);		
		assertEquals(reflector.getCliMetadataRootHeader().getExtraDataOffset(), 0);
		assertEquals(reflector.getCliMetadataRootHeader().getVersionString(), "v4.0.30319\0\0");
		assertEquals(reflector.getCliMetadataRootHeader().getFlags(), 0);		
		assertEquals(reflector.getCliMetadataRootHeader().getNumberOfStreams(), 5);
	}
	
	public void testStreamSizes() {
		assertEquals(reflector.getSizeOfMetadataStream(), 1250864);		
		assertEquals(reflector.getSizeOfStringsStream(), 423536);
		assertEquals(reflector.getSizeOfUserStringStream(), 214784);		
		assertEquals(reflector.getSizeOfGuidStream(), 16);
		assertEquals(reflector.getSizeOfBlobStream(), 212896);				
	}
	
	public void testStringsStream() {
		assertEquals("<Module>", reflector.getStringsStream().getString(1));
		assertEquals("CommonLanguageRuntimeLibrary", reflector.getStringsStream().getString(10));
		assertEquals("Object", reflector.getStringsStream().getString(39));
		assertEquals("System", reflector.getStringsStream().getString(46));
		assertEquals("System.Runtime.Serialization", reflector.getStringsStream().getString(103));
		assertEquals("DBNull", reflector.getStringsStream().getString(3400));
		assertEquals("MinValue", reflector.getStringsStream().getString(61623));
		assertEquals("sLocalizedCountry", reflector.getStringsStream().getString(219351));
		assertEquals("get_SerializationFormatter", reflector.getStringsStream().getString(285866));
		assertEquals("System.Runtime.InteropServices._TypeBuilder.GetTypeInfoCount", reflector.getStringsStream().getString(349606));
		assertEquals("System.Collections.Generic.IEnumerator<System.Globalization.CultureInfo>.Current", reflector.getStringsStream().getString(421845));
	}
	
	public void testBlobStream() {
		byte [] blob1 = { 0x15, 0x12, (byte) 0x80, (byte) 0xc8, 0x01, 0x13, 0x00 };
		byte [] blob2 = { 0x15, 0x12, (byte) 0x8e, 0x34, 0x02, 0x13, 0x00, 0x15, 0x12, (byte) 0x8d, (byte) 0xa4, 0x01, 0x13, 0x00 };
		byte [] blob3 = { 0x00, 0x05, 0x02, 0x12, (byte) 0x84, (byte) 0xc0, 0x11, (byte) 0x90, 0x14, 0x0e, 0x02, 0x0e };
		byte [] blob4 = { 0x20, 0x0c, 0x01, 0x0e, 0x11, (byte) 0x9b, (byte) 0xdc, 0x11, (byte) 0x9b, (byte) 0xd0, 0x08, 0x02, 0x11, (byte) 0x9b, (byte) 0xe8, 0x08, 0x11, (byte) 0x9b, (byte) 0xe4, 0x12, (byte) 0x95, (byte) 0xa0, 0x0e, 0x02, 0x02 };
		byte [] blob5 = { 0x07, 0x0a, 0x11, (byte) 0xa9, (byte) 0xdc, 0x12, (byte) 0xaa, 0x0c, 0x12, (byte) 0xaa, 0x0c, 0x12,(byte)  0xa9, 0x1c, 0x12, (byte) 0xa9, 0x1c, 0x05, 0x08, 0x08, 0x08, 0x08 };
		
		assertTrue(ArrayUtils.arraysAreEqual(blob1, reflector.getBlobStream().getBlob(1)));
		assertTrue(ArrayUtils.arraysAreEqual(blob2, reflector.getBlobStream().getBlob(667)));
		assertTrue(ArrayUtils.arraysAreEqual(blob3, reflector.getBlobStream().getBlob(32638)));
		assertTrue(ArrayUtils.arraysAreEqual(blob4, reflector.getBlobStream().getBlob(84592)));
		assertTrue(ArrayUtils.arraysAreEqual(blob5, reflector.getBlobStream().getBlob(211579)));
	}

}

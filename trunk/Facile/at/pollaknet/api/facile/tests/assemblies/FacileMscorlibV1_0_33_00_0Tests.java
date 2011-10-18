package at.pollaknet.api.facile.tests.assemblies;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.util.ArrayUtils;
import junit.framework.TestCase;

public class FacileMscorlibV1_0_33_00_0Tests extends TestCase {

	private final static String PATH_MSCORLIBV1_0_33_00_0 = "assemblies/v1.0.3300.0/mscorlib.dll";
	private final static int NUM_OF_TEST_CASES = 5;
	private static FacileReflector reflector = null;
	private static int testCounter = 0;
	
	public FacileMscorlibV1_0_33_00_0Tests(String name) {
		super(name);
	}
	
	/**
	    * Sets up of basic fields. 
	    * (Called before every test case method.) 
	    */ 
	   protected void setUp() { 
		   if(testCounter==0) {
				try {
					reflector = Facile.load(PATH_MSCORLIBV1_0_33_00_0);
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
		assertEquals(reflector.getCliHeader().getMinorRuntimeVersion(), 0);
		assertEquals(reflector.getCliHeader().getAddrOfMetadataDirectory(), 964580);
		assertEquals(reflector.getCliHeader().getSizeOfMetadataDirectory(), 1028788);
		assertEquals(reflector.getCliHeader().getFlags(), 9);
	}
	
	public void testMetadataRootHeader() {
		assertEquals(reflector.getCliMetadataRootHeader().getSize(), 105);		
		assertEquals(reflector.getCliMetadataRootHeader().getMajorVersion(), 1);
		assertEquals(reflector.getCliMetadataRootHeader().getMinorVersion(), 1);		
		assertEquals(reflector.getCliMetadataRootHeader().getExtraDataOffset(), 0);
		assertEquals(reflector.getCliMetadataRootHeader().getVersionString(), "v1.x86ret\0\0\0");
		assertEquals(reflector.getCliMetadataRootHeader().getFlags(), 0);		
		assertEquals(reflector.getCliMetadataRootHeader().getNumberOfStreams(), 5);
	}
	
	public void testStreamSizes() {
		assertEquals(reflector.getSizeOfMetadataStream(), 600132);		
		assertEquals(reflector.getSizeOfStringsStream(), 217096);
		assertEquals(reflector.getSizeOfUserStringStream(), 121524);		
		assertEquals(reflector.getSizeOfGuidStream(), 16);
		assertEquals(reflector.getSizeOfBlobStream(), 89912);				
	}
	
	public void testStringsStream() {
		assertEquals("System.IO", reflector.getStringsStream().getString(1));
		assertEquals("FileStream", reflector.getStringsStream().getString(11));
		assertEquals("SuppressUnmanagedCodeSecurityAttribute", reflector.getStringsStream().getString(22));
		assertEquals("Array", reflector.getStringsStream().getString(95));
		assertEquals("CodeAccessSecurityEngine", reflector.getStringsStream().getString(118));
		assertEquals("ObjWait", reflector.getStringsStream().getString(35276));
		assertEquals("bitCount", reflector.getStringsStream().getString(105162));
		assertEquals("Internet", reflector.getStringsStream().getString(140870));
		assertEquals("THRESHOLD_FOR_VALUETYPE_IDS", reflector.getStringsStream().getString(176068));
		assertEquals("System.Runtime.InteropServices._MethodRental.Invoke", reflector.getStringsStream().getString(186154));
		assertEquals("clsToken", reflector.getStringsStream().getString(216465));
	}
	
	public void testBlobStream() {
		byte [] blob1 = {0x06, 0x11, (byte) 0x95, (byte) 0xb8};
		byte [] blob2 = {0x00, 0x01, 0x0e, 0x0e};
		byte [] blob3 = {0x06, 0x1d, 0x12, (byte) 0x90, 0x44 };
		byte [] blob4 = {0x07, 0x01, 0x12, (byte) 0x8f, (byte) 0xcc };
		byte [] blob5 = {0x07, 0x07, 0x1d, 0x12, (byte) 0x83, 0x08, 0x08, 0x1d, 0x12, (byte) 0x85, (byte) 0xe8, 0x12,
				(byte) 0x8c, (byte) 0xf8, 0x12, (byte) 0x8c, (byte) 0xac, 0x12, (byte) 0x83, 0x08, 0x12, (byte) 0x83, 0x08 };
		
		assertTrue(ArrayUtils.arraysAreEqual(blob1, reflector.getBlobStream().getBlob(1)));
		assertTrue(ArrayUtils.arraysAreEqual(blob2, reflector.getBlobStream().getBlob(2117)));
		assertTrue(ArrayUtils.arraysAreEqual(blob3, reflector.getBlobStream().getBlob(7871)));
		assertTrue(ArrayUtils.arraysAreEqual(blob4, reflector.getBlobStream().getBlob(78116)));
		assertTrue(ArrayUtils.arraysAreEqual(blob5, reflector.getBlobStream().getBlob(88082)));
	}
	
}

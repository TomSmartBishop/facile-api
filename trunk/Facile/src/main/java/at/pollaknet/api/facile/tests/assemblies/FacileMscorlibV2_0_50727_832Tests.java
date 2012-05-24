package at.pollaknet.api.facile.tests.assemblies;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.util.ArrayUtils;
import junit.framework.TestCase;

public class FacileMscorlibV2_0_50727_832Tests extends TestCase {

	private final static String PATH_MSCORLIBV2_0_50727_832 = "assemblies/v2.0.50727.832/mscorlib.dll";
	private final static int NUM_OF_TEST_CASES = 5;
	private static FacileReflector reflector = null;
	private static int testCounter = 0;
	
	public FacileMscorlibV2_0_50727_832Tests(String name) {
		super(name);
	}
	
	/**
    * Sets up of basic fields. 
    * (Called before every test case method.) 
    */ 
   protected void setUp() { 
	   if(testCounter==0) {
			try {
				reflector = Facile.load(PATH_MSCORLIBV2_0_50727_832);
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
		assertEquals(reflector.getCliHeader().getAddrOfMetadataDirectory(), 2313328);
		assertEquals(reflector.getCliHeader().getSizeOfMetadataDirectory(), 1635212);
		assertEquals(reflector.getCliHeader().getFlags(), 11);
	}
	
	public void testMetadataRootHeader() {
		assertEquals(reflector.getCliMetadataRootHeader().getSize(), 105);		
		assertEquals(reflector.getCliMetadataRootHeader().getMajorVersion(), 1);
		assertEquals(reflector.getCliMetadataRootHeader().getMinorVersion(), 1);		
		assertEquals(reflector.getCliMetadataRootHeader().getExtraDataOffset(), 0);
		assertEquals(reflector.getCliMetadataRootHeader().getVersionString(), "v2.0.50727\0\0");
		assertEquals(reflector.getCliMetadataRootHeader().getFlags(), 0);		
		assertEquals(reflector.getCliMetadataRootHeader().getNumberOfStreams(), 5);
	}
	
	public void testStreamSizes() {
		assertEquals(reflector.getSizeOfMetadataStream(), 951100);		
		assertEquals(reflector.getSizeOfStringsStream(), 357088);
		assertEquals(reflector.getSizeOfUserStringStream(), 171652);		
		assertEquals(reflector.getSizeOfGuidStream(), 16);
		assertEquals(reflector.getSizeOfBlobStream(), 155248);				
	}
	
	public void testStringsStream() {
		assertEquals("<Module>", reflector.getStringsStream().getString(1));
		assertEquals("CommonLanguageRuntimeLibrary", reflector.getStringsStream().getString(10));
		assertEquals("Object", reflector.getStringsStream().getString(39));
		assertEquals("System", reflector.getStringsStream().getString(46));
		assertEquals("ICloneable", reflector.getStringsStream().getString(97));
		assertEquals("IFormatProvider", reflector.getStringsStream().getString(3304));
		assertEquals("TypeEntry", reflector.getStringsStream().getString(34183));
		assertEquals("File_ImportPath", reflector.getStringsStream().getString(104064));
		assertEquals("LOCALE_SNATIVEDIGITS", reflector.getStringsStream().getString(172608));
		assertEquals("CreateCompressedState_HG", reflector.getStringsStream().getString(238887));
		assertEquals("System.Reflection.ICustomAttributeProvider.GetCustomAttributes", reflector.getStringsStream().getString(284076));
	}
	
	public void testBlobStream() {
		byte [] blob1 = { 0x15, 0x12, 0x30, 0x01, 0x13, 0x00 };
		byte [] blob2 = { 0x15, 0x12, 0x58, 0x01, 0x13, 0x00 };
		byte [] blob3 = { 0x15, 0x12, 0x6c, 0x01, 0x02 };
		byte [] blob4 = { 0x20, 0x01, 0x08, 0x11, (byte) 0x82, (byte) 0xe0 };
		byte [] blob5 = { 0x07, 0x05, 0x12, (byte) 0x83, (byte) 0xb8, 0x12, (byte) 0x9c, 0x38, 0x12, (byte) 0x9b, 0x38, 0x12, (byte) 0x9c, 0x04, 0x0e };
		
		assertTrue(ArrayUtils.arraysAreEqual(blob1, reflector.getBlobStream().getBlob(1)));
		assertTrue(ArrayUtils.arraysAreEqual(blob2, reflector.getBlobStream().getBlob(8)));
		assertTrue(ArrayUtils.arraysAreEqual(blob3, reflector.getBlobStream().getBlob(69)));
		assertTrue(ArrayUtils.arraysAreEqual(blob4, reflector.getBlobStream().getBlob(19840)));
		assertTrue(ArrayUtils.arraysAreEqual(blob5, reflector.getBlobStream().getBlob(143169)));
	}

}

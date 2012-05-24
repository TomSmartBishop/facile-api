package at.pollaknet.api.facile.tests.assemblies;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.util.ArrayUtils;
import junit.framework.TestCase;

public class FacileMscorlibV2_0_31005_0Tests extends TestCase {

	private final static String PATH_MSCORLIBV2_0_31005_0 = "assemblies/v2.0.31005.0/mscorlib.dll";
	private final static int NUM_OF_TEST_CASES = 5;
	private static FacileReflector reflector = null;
	private static int testCounter = 0;
	
	public FacileMscorlibV2_0_31005_0Tests(String name) {
		super(name);
	}
	
	/**
    * Sets up of basic fields. 
    * (Called before every test case method.) 
    */ 
   protected void setUp() { 
	   if(testCounter==0) {
			try {
				reflector = Facile.load(PATH_MSCORLIBV2_0_31005_0);
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
		assertEquals(reflector.getCliHeader().getAddrOfMetadataDirectory(), 615340);
		assertEquals(reflector.getCliHeader().getSizeOfMetadataDirectory(), 832272);
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
		assertEquals(reflector.getSizeOfMetadataStream(), 505724);		
		assertEquals(reflector.getSizeOfStringsStream(), 176520);
		assertEquals(reflector.getSizeOfUserStringStream(), 63808);		
		assertEquals(reflector.getSizeOfGuidStream(), 16);
		assertEquals(reflector.getSizeOfBlobStream(), 86096);				
	}
	
	public void testStringsStream() {
		assertEquals("<Module>", reflector.getStringsStream().getString(1));
		assertEquals("CommonLanguageRuntimeLibrary", reflector.getStringsStream().getString(10));
		assertEquals("Object", reflector.getStringsStream().getString(39));
		assertEquals("System", reflector.getStringsStream().getString(46));
		assertEquals("IComparable", reflector.getStringsStream().getString(99));
		assertEquals("IEnumerator", reflector.getStringsStream().getString(1723));
		assertEquals("Mscorlib_DictionaryValueCollectionDebugView`2", reflector.getStringsStream().getString(5801));
		assertEquals("MethodBuilderInstantiation", reflector.getStringsStream().getString(12356));
		assertEquals("SystemRuntimeSerializationFormattersSoap", reflector.getStringsStream().getString(20250));
		assertEquals("System.Collections.IEnumerator.Current", reflector.getStringsStream().getString(36379));
		assertEquals("CreateAdjustmentRuleFromTimeZoneInformation", reflector.getStringsStream().getString(60861));
	}
	
	public void testBlobStream() {
		byte [] blob1 = { 0x15, 0x12, (byte) 0x81, 0x70, 0x01, 0x13, 0x00 };
		byte [] blob2 = { 0x15, 0x12, (byte) 0x81, (byte) 0x90, 0x01, 0x13, 0x00 };
		byte [] blob3 = { 0x15, 0x12, (byte) 0x84, 0x20, 0x01, 0x0e };
		byte [] blob4 = { 0x10, 0x01, 0x00, 0x1e, 0x00 };
		byte [] blob5 = { 0x6c, 0x00, 0x73, 0x00, 0x74, 0x00, 0x72, 0x00, 0x63, 0x00, 0x70, 0x00, 0x79, 0x00 };
		
		assertTrue(ArrayUtils.arraysAreEqual(blob1, reflector.getBlobStream().getBlob(1)));
		assertTrue(ArrayUtils.arraysAreEqual(blob2, reflector.getBlobStream().getBlob(9)));
		assertTrue(ArrayUtils.arraysAreEqual(blob3, reflector.getBlobStream().getBlob(308)));
		assertTrue(ArrayUtils.arraysAreEqual(blob4, reflector.getBlobStream().getBlob(9908)));
		assertTrue(ArrayUtils.arraysAreEqual(blob5, reflector.getBlobStream().getBlob(10555)));
	}

}

package at.pollaknet.api.facile.tests.assemblies;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.util.ArrayUtils;
import junit.framework.TestCase;

public class FacileMscorlibV2_0_50727_1433Tests extends TestCase {

	private final static String PATH_MSCORLIBV2_0_50727_1433 = "assemblies/v2.0.50727.1433/mscorlib.dll";
	private final static int NUM_OF_TEST_CASES = 5;
	private static FacileReflector reflector = null;
	private static int testCounter = 0;
	
	public FacileMscorlibV2_0_50727_1433Tests(String name) {
		super(name);
	}
	
	/**
    * Sets up of basic fields. 
    * (Called before every test case method.) 
    */ 
   protected void setUp() { 
	   if(testCounter==0) {
			try {
				reflector = Facile.load(PATH_MSCORLIBV2_0_50727_1433);
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
		assertEquals(reflector.getCliHeader().getAddrOfMetadataDirectory(), 2329808);
		assertEquals(reflector.getCliHeader().getSizeOfMetadataDirectory(), 1656440);
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
		assertEquals(reflector.getSizeOfMetadataStream(), 963712);		
		assertEquals(reflector.getSizeOfStringsStream(), 362872);
		assertEquals(reflector.getSizeOfUserStringStream(), 172564);		
		assertEquals(reflector.getSizeOfGuidStream(), 16);
		assertEquals(reflector.getSizeOfBlobStream(), 157164);				
	}
	
	public void testStringsStream() {
		assertEquals("<Module>", reflector.getStringsStream().getString(1));
		assertEquals("CommonLanguageRuntimeLibrary", reflector.getStringsStream().getString(10));
		assertEquals("Object", reflector.getStringsStream().getString(39));
		assertEquals("System", reflector.getStringsStream().getString(46));
		assertEquals("ValueType", reflector.getStringsStream().getString(220));
		assertEquals("Boolean", reflector.getStringsStream().getString(2168));
		assertEquals("CategoryMembershipDataEntryFieldId", reflector.getStringsStream().getString(7455));
		assertEquals("ArrayListEnumeratorSimple", reflector.getStringsStream().getString(11151));
		assertEquals("CryptographicUnexpectedOperationException", reflector.getStringsStream().getString(38885));
		assertEquals("INVOCATION_FLAGS_CONTAINS_STACK_POINTERS", reflector.getStringsStream().getString(80440));
		assertEquals("SXS_INSTALL_REFERENCE_SCHEME_SXS_STRONGNAME_SIGNED_PRIVATE_ASSEMBLY", reflector.getStringsStream().getString(114132));
	}
	
	public void testBlobStream() {
		byte [] blob1 = { 0x15, 0x12, 0x30, 0x01, 0x13, 0x00 };
		byte [] blob2 = { 0x15, 0x12, 0x58, 0x01, 0x13, 0x00 };
		byte [] blob3 = { 0x15, 0x12, 0x68, 0x01, 0x02 };
		byte [] blob4 = { 0x20, 0x02, 0x12, (byte) 0x8c, (byte) 0xb8, 0x0e, 0x0e };
		byte [] blob5 = { 0x00, 0x03, 0x1c, 0x12, (byte) 0x83, (byte) 0xc4, 0x1d, 0x1c, 0x1d, 0x1c };

		assertTrue(ArrayUtils.arraysAreEqual(blob1, reflector.getBlobStream().getBlob(1)));
		assertTrue(ArrayUtils.arraysAreEqual(blob2, reflector.getBlobStream().getBlob(8)));
		assertTrue(ArrayUtils.arraysAreEqual(blob3, reflector.getBlobStream().getBlob(79)));
		assertTrue(ArrayUtils.arraysAreEqual(blob4, reflector.getBlobStream().getBlob(568)));
		assertTrue(ArrayUtils.arraysAreEqual(blob5, reflector.getBlobStream().getBlob(12733)));
	}

}

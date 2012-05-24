package at.pollaknet.api.facile.tests.assemblies;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.util.ArrayUtils;
import junit.framework.TestCase;

public class FacileMscorlibV4_0_4_0_0_0Tests extends TestCase {

	private final static String PATH_MSCORLIBV4_0_4000_32 = "assemblies/v4.0.4.0.0.0_32/mscorlib.dll";
	private final static String PATH_MSCORLIBV4_0_4000_64 = "assemblies/v4.0.4.0.0.0_64/mscorlib.dll";
	private final static int NUM_OF_TEST_CASES = 5;
	private static FacileReflector reflector32 = null;
	private static FacileReflector reflector64 = null;
	private static int testCounter = 0;
	
	public FacileMscorlibV4_0_4_0_0_0Tests(String name) {
		super(name);
	}
	
	/**
    * Sets up of basic fields. 
    * (Called before every test case method.) 
    */ 
   protected void setUp() { 
	   if(testCounter==0) {
			try {
				reflector32 = Facile.load(PATH_MSCORLIBV4_0_4000_32);
			} catch (Exception e) {
				fail();
			}
			try {
				reflector64 = Facile.load(PATH_MSCORLIBV4_0_4000_64);
			} catch (Exception e) {
				fail();
			}
  		}
	   testCounter++;
	}
   
   protected void tearDown() {
	   if(testCounter==NUM_OF_TEST_CASES) {
		   reflector32 = null;
		   reflector64 = null;
	   }
   }

	public void testCliHeader() {
					
		assertEquals(reflector32.getCliHeader().getClrHeaderSize(), 72);
		assertEquals(reflector32.getCliHeader().getMajorRuntimeVersion(), 2);
		assertEquals(reflector32.getCliHeader().getMinorRuntimeVersion(), 5);
		assertEquals(reflector32.getCliHeader().getAddrOfMetadataDirectory(), 0x001681c8);
		assertEquals(reflector32.getCliHeader().getSizeOfMetadataDirectory(), 2254704);
		assertEquals(reflector32.getCliHeader().getFlags(), 0xb);
		
		assertEquals(reflector64.getCliHeader().getClrHeaderSize(), 72);
		assertEquals(reflector64.getCliHeader().getMajorRuntimeVersion(), 2);
		assertEquals(reflector64.getCliHeader().getMinorRuntimeVersion(), 5);
		assertEquals(reflector64.getCliHeader().getAddrOfMetadataDirectory(), 0x00168538);
		assertEquals(reflector64.getCliHeader().getSizeOfMetadataDirectory(), 2254152);
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
		int alignmentGap = 4;
		assertEquals(reflector32.getSizeOfMetadataStream(), 0x001534f8 - alignmentGap);
		assertEquals(reflector32.getSizeOfStringsStream(), 0x00060a7c);
		assertEquals(reflector32.getSizeOfUserStringStream(), 0x00036614);		
		assertEquals(reflector32.getSizeOfGuidStream(), 0x00000010);
		assertEquals(reflector32.getSizeOfBlobStream(), 0x0003c16c);		
		
		assertEquals(reflector64.getSizeOfMetadataStream(), 0x001533bc - alignmentGap);		
		assertEquals(reflector64.getSizeOfStringsStream(), 0x000609bc);
		assertEquals(reflector64.getSizeOfUserStringStream(), 0x000365dc);		
		assertEquals(reflector64.getSizeOfGuidStream(), 0x00000010);
		assertEquals(reflector64.getSizeOfBlobStream(), 0x0003c178);				
	}
	
	public void testStringsStream() {
		assertEquals("DaysTo10000", reflector32.getStringsStream().getString(1));
		assertEquals("Digit100", reflector32.getStringsStream().getString(26));
		assertEquals("SystemFunction040", reflector32.getStringsStream().getString(445));
		assertEquals("WAIT_OBJECT_0", reflector32.getStringsStream().getString(755));
		assertEquals("SystemCollectionsConcurrent_ProducerConsumerCollectionDebugView`1", reflector32.getStringsStream().getString(5395));
		assertEquals("GetNewLogicalCallID", reflector32.getStringsStream().getString(23517));
		assertEquals("System.Runtime.InteropServices.ICustomQueryInterface.GetInterface", reflector32.getStringsStream().getString(88346));
		assertEquals("StandAloneSig", reflector32.getStringsStream().getString(170184));
		assertEquals("System.Collections.Generic.IEnumerable<System.Collections.Generic.KeyValuePair<TKey,TValue>>.GetEnumerator", reflector32.getStringsStream().getString(280694));
		assertEquals("EncodeHexStringFromInt", reflector32.getStringsStream().getString(344085));
		assertEquals("GetServerContextAndDomainIdForProxy", reflector32.getStringsStream().getString(395515));
		
		assertEquals("DaysTo10000", reflector64.getStringsStream().getString(1));
		assertEquals("Digit100", reflector64.getStringsStream().getString(26));
		assertEquals("SystemFunction040", reflector64.getStringsStream().getString(445));
		assertEquals("WAIT_OBJECT_0", reflector64.getStringsStream().getString(755));
		assertEquals("SystemCollectionsConcurrent_ProducerConsumerCollectionDebugView`1", reflector64.getStringsStream().getString(5395));
		assertEquals("GetNewLogicalCallID", reflector64.getStringsStream().getString(23517));
		assertEquals("System.Runtime.InteropServices.ICustomQueryInterface.GetInterface", reflector64.getStringsStream().getString(88310));
		assertEquals("StandAloneSig", reflector64.getStringsStream().getString(170119));
		assertEquals("GetRuntimeMethodHandleFromMetadataToken", reflector64.getStringsStream().getString(216708));
		assertEquals("System.Runtime.Remoting.Contexts", reflector64.getStringsStream().getString(329363));
		assertEquals("System.Collections.IDictionaryEnumerator.Entry", reflector64.getStringsStream().getString(388806));
	}
	
	public void testBlobStream() {
		byte [] blob1 = { 0x15, 0x12, (byte) 0x83, 0x58, 0x01, 0x12, (byte) 0x92, 0x40 };
		byte [] blob2 = { 0x20, 0x00, 0x15, 0x12, (byte)0x80, (byte)0xd0, 0x01, 0x13, 0x00 };
		byte [] blob3 = { 0x07, 0x05, 0x12, 0x48, 0x12, (byte)0x84, (byte)0x98, 0x12, (byte)0x84, (byte)0x98, 0x1c, 0x12, 0x14 };
		byte [] blob4 = { 0x00, 0x03, 0x1d, 0x12, (byte) 0x82, 0x04, 0x12, (byte) 0x84, (byte) 0x80, 0x12, (byte) 0x84, (byte) 0x8c, 0x02 };
		byte [] blob5 = { 0x01, 0x00, 0x08, 0x00, 0x00, 0x00, 0x01, 0x00, 0x54, 0x02, 0x09, 0x49, 0x6e, 0x68, 0x65, 0x72, 0x69, 0x74, 0x65, 0x64, 0x01 };
		
		assertTrue(ArrayUtils.arraysAreEqual(blob1, reflector32.getBlobStream().getBlob(1)));
		assertTrue(ArrayUtils.arraysAreEqual(blob2, reflector32.getBlobStream().getBlob(110)));
		assertTrue(ArrayUtils.arraysAreEqual(blob3, reflector32.getBlobStream().getBlob(18063)));
		assertTrue(ArrayUtils.arraysAreEqual(blob4, reflector32.getBlobStream().getBlob(128463)));
		assertTrue(ArrayUtils.arraysAreEqual(blob5, reflector32.getBlobStream().getBlob(241326)));
		
		byte [] blob6 = { 0x15, 0x12, (byte) 0x83, 0x58, 0x01, 0x12, (byte) 0x92, 0x38 };
		byte [] blob7 = { 0x20, 0x00, 0x15, 0x12, (byte)0x80, (byte)0xd0, 0x01, 0x13, 0x00 };
		byte [] blob8 = { 0x07, 0x05, 0x12, 0x48, 0x12, (byte)0x84, (byte)0x98, 0x12, (byte)0x84, (byte)0x98, 0x1c, 0x12, 0x14 };
		byte [] blob9 = { 0x00, 0x03, 0x1d, 0x12, (byte) 0x82, 0x04, 0x12, (byte) 0x84, (byte) 0x80, 0x12, (byte) 0x84, (byte) 0x8c, 0x02 };
		byte [] blob10 = { 0x20, 0x0a, 0x12, (byte) 0x83, (byte) 0x80, 0x08, 0x08, 0x11, (byte) 0x82, (byte) 0x84, 0x11, (byte) 0x82, (byte) 0x80, 0x08, 0x0e, 0x08, 0x08, 0x12, (byte) 0x82, 0x50, 0x1c };
		
		assertTrue(ArrayUtils.arraysAreEqual(blob6, reflector64.getBlobStream().getBlob(1)));
		assertTrue(ArrayUtils.arraysAreEqual(blob7, reflector64.getBlobStream().getBlob(110)));
		assertTrue(ArrayUtils.arraysAreEqual(blob8, reflector64.getBlobStream().getBlob(18063)));
		assertTrue(ArrayUtils.arraysAreEqual(blob9, reflector64.getBlobStream().getBlob(128491)));
		assertTrue(ArrayUtils.arraysAreEqual(blob10, reflector64.getBlobStream().getBlob(198206)));
	}

}

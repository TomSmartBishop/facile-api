package at.pollaknet.api.facile.tests.assemblies;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.util.ArrayUtils;
import junit.framework.TestCase;

public class FacileMscorlibV1_0_50_00_0Tests extends TestCase {

	private final static String PATH_MSCORLIBV1_0_50_00_0 = "assemblies/v1.0.5000.0/mscorlib.dll";
	private final static int NUM_OF_TEST_CASES = 5;
	private static FacileReflector reflector = null;
	private static int testCounter = 0;
	
	public FacileMscorlibV1_0_50_00_0Tests(String name) {
		super(name);
	}
	
	/**
	    * Sets up of basic fields. 
	    * (Called before every test case method.) 
	    */ 
	   protected void setUp() { 
		   if(testCounter==0) {
				try {
					reflector = Facile.load(PATH_MSCORLIBV1_0_50_00_0);
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
		assertEquals(reflector.getCliHeader().getAddrOfMetadataDirectory(), 997360);
		assertEquals(reflector.getCliHeader().getSizeOfMetadataDirectory(), 1053160);
		assertEquals(reflector.getCliHeader().getFlags(), 9);
	}
	
	public void testMetadataRootHeader() {
		assertEquals(reflector.getCliMetadataRootHeader().getSize(), 105);		
		assertEquals(reflector.getCliMetadataRootHeader().getMajorVersion(), 1);
		assertEquals(reflector.getCliMetadataRootHeader().getMinorVersion(), 1);		
		assertEquals(reflector.getCliMetadataRootHeader().getExtraDataOffset(), 0);
		assertEquals(reflector.getCliMetadataRootHeader().getVersionString(), "v1.1.4322\0\0\0");
		assertEquals(reflector.getCliMetadataRootHeader().getFlags(), 0);		
		assertEquals(reflector.getCliMetadataRootHeader().getNumberOfStreams(), 5);
	}
	
	public void testStreamSizes() {
		assertEquals(reflector.getSizeOfMetadataStream(), 612072);		
		assertEquals(reflector.getSizeOfStringsStream(), 222680);
		assertEquals(reflector.getSizeOfUserStringStream(), 125432);		
		assertEquals(reflector.getSizeOfGuidStream(), 16);
		assertEquals(reflector.getSizeOfBlobStream(), 92848);			
	}
	
	public void testStringsStream() {
		assertEquals("InternalBlockCopy", reflector.getStringsStream().getString(1));
		assertEquals("SynchronizedClientContextSink", reflector.getStringsStream().getString(4315));
		assertEquals("<PrivateImplementationDetails>", reflector.getStringsStream().getString(1572));
		assertEquals("System.Configuration.Assemblies", reflector.getStringsStream().getString(1717));
		assertEquals("IOUtil", reflector.getStringsStream().getString(3270));
		assertEquals("Context", reflector.getStringsStream().getString(8138));
		assertEquals("File", reflector.getStringsStream().getString(12394));
		assertEquals("SetWin32ContextInIDispatchAttribute", reflector.getStringsStream().getString(14156));
		assertEquals("DESCryptoServiceProvider", reflector.getStringsStream().getString(16838));
		assertEquals("Boolean", reflector.getStringsStream().getString(24783));
		assertEquals("System.Runtime.InteropServices._Activator.Invoke", reflector.getStringsStream().getString(62754));
	}
	
	public void testBlobStream() {
		byte [] blob1 = { 0x06, 0x11, (byte) 0x97, 0x34 };
		byte [] blob2 = { 0x06, 0x11, (byte) 0x96, 0x08 };
		byte [] blob3 = { 0x00, 0x02, 0x0e, 0x0e, 0x12, (byte) 0x82, 0x5c };
		byte [] blob4 = { 0x00, 0x02, 0x08, 0x1d, 0x03, 0x08 };
		byte [] blob5 = { 0x07, 0x07, 0x08, 0x08, 0x08, 0x08, 0x12, (byte) 0x8f, 0x18, 0x12, (byte) 0x8e, 0x2c, 0x08 };
		
		assertTrue(ArrayUtils.arraysAreEqual(blob1, reflector.getBlobStream().getBlob(1)));
		assertTrue(ArrayUtils.arraysAreEqual(blob2, reflector.getBlobStream().getBlob(6)));
		assertTrue(ArrayUtils.arraysAreEqual(blob3, reflector.getBlobStream().getBlob(5207)));
		assertTrue(ArrayUtils.arraysAreEqual(blob4, reflector.getBlobStream().getBlob(40201)));
		assertTrue(ArrayUtils.arraysAreEqual(blob5, reflector.getBlobStream().getBlob(78882)));
	}
	
}

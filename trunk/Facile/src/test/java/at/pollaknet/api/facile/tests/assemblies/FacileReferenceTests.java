package at.pollaknet.api.facile.tests.assemblies;

import junit.framework.TestCase;
import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.exception.DotNetContentNotFoundException;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;

public class FacileReferenceTests extends TestCase {

	private final static String PATH_MSCORLIBV4_0_4000_32 = "assemblies/v4.0.4.0.0.0_32/mscorlib.dll";
	private final static String PATH_MSCORLIBV4_0_4000_64 = "assemblies/v4.0.4.0.0.0_64/mscorlib.dll";
	private final static int NUM_OF_TEST_CASES = 1;
	private static FacileReflector reflector32 = null;
	private static FacileReflector reflector64 = null;
	private static int testCounter = 0;
	
	public FacileReferenceTests(String name) {
		super(name);
	}
	
	/**
    * Sets up of basic fields. 
    * (Called before every test case method.) 
    */ 
   protected void setUp() { 
	   if(testCounter==0) {
			try {
				reflector32 = Facile.reflect(PATH_MSCORLIBV4_0_4000_32);
				
				//clear the default references (which works for mscorlib)
				reflector32.getReferneceEnums().clear();
			} catch (Exception e) {
				fail();
			}
			try {
				reflector64 = Facile.reflect(PATH_MSCORLIBV4_0_4000_64);
				
				//clear the default references (which works for mscorlib)
				reflector64.getReferneceEnums().clear();
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

	public void testLoadAssembyWithoutEnumReferences() {
		//this is possible because the required enums are define din mscorlib
		Assembly mscorlib32 = null;
		try {
			mscorlib32 = reflector32.loadAssembly();
		} catch (DotNetContentNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(mscorlib32);
		
		//this is possible because the required enums are define din mscorlib
		Assembly mscorlib64 = null;
		try {
			mscorlib64 = reflector64.loadAssembly();
		} catch (DotNetContentNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(mscorlib64);
	}

}

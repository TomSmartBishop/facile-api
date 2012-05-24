package at.pollaknet.api.facile.tests.assemblies;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.exception.DotNetContentNotFoundException;
import junit.framework.TestCase;

public class FacileNativeTests extends TestCase {

	private final static String PATH_NATIVE_EXE = "assemblies/native/md5.exe";
	
	public FacileNativeTests(String name) {
		super(name);
	}

	public void testCreateFacileReader() {
				
		try {
			Facile.load(PATH_NATIVE_EXE);
			
			fail();
		} catch (DotNetContentNotFoundException e) {
			assertTrue(true);
		} catch (Exception e) {
			fail();
		}
	}
}

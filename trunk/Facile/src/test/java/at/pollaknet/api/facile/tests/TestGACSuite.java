package at.pollaknet.api.facile.tests;

import at.pollaknet.api.facile.tests.gac.TestGAC;
import at.pollaknet.api.facile.tests.gac.TestGACFusion;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestGACSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests with files from the global assembly cache (GAC)");
		
		//$JUnit-BEGIN$

		suite.addTestSuite(TestGAC.class);
		suite.addTestSuite(TestGACFusion.class);
		
		//$JUnit-END$
		return suite;
	}

}

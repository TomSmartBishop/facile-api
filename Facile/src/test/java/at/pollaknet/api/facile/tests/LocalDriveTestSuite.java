package at.pollaknet.api.facile.tests;

import at.pollaknet.api.facile.tests.local.TestDrives;
import junit.framework.Test;
import junit.framework.TestSuite;

public class LocalDriveTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests all files detected on drive A to Z and '/' on POSIX");
		
		//$JUnit-BEGIN$
		
		suite.addTestSuite(TestDrives.class);

		//$JUnit-END$
		return suite;
	}

}

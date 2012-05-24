package at.pollaknet.api.facile.tests;

import at.pollaknet.api.facile.tests.assemblies.FacileMscorlibGeneralAPITests;
import at.pollaknet.api.facile.tests.assemblies.FacileMscorlibV1_0_33_00_0Tests;
import at.pollaknet.api.facile.tests.assemblies.FacileMscorlibV1_0_50_00_0Tests;
import at.pollaknet.api.facile.tests.assemblies.FacileMscorlibV2_0_31005_0Tests;
import at.pollaknet.api.facile.tests.assemblies.FacileMscorlibV2_0_50727_1433Tests;
import at.pollaknet.api.facile.tests.assemblies.FacileMscorlibV2_0_50727_832Tests;
import at.pollaknet.api.facile.tests.assemblies.FacileMscorlibV4_0_30319Tests;
import at.pollaknet.api.facile.tests.assemblies.FacileMscorlibV4_0_4_0_0_0Tests;
import at.pollaknet.api.facile.tests.assemblies.FacileNativeTests;
import at.pollaknet.api.facile.tests.utils.ArrayUtilsTest;
import at.pollaknet.api.facile.tests.utils.ByteReaderTests;
import at.pollaknet.api.facile.tests.utils.IndexDecoderTests;
import junit.framework.Test;
import junit.framework.TestSuite;

public class FacileBasicTestSuite {
	
	public static Test suite() {
		
		TestSuite suite = new TestSuite("Basic Facile Tests");
		
		//$JUnit-BEGIN$
		suite.addTestSuite(ArrayUtilsTest.class);
		suite.addTestSuite(ByteReaderTests.class);
		suite.addTestSuite(IndexDecoderTests.class);
		
		suite.addTestSuite(FacileMscorlibV1_0_33_00_0Tests.class);
		suite.addTestSuite(FacileMscorlibV1_0_50_00_0Tests.class);
		suite.addTestSuite(FacileMscorlibV2_0_31005_0Tests.class);
		suite.addTestSuite(FacileMscorlibV2_0_50727_832Tests.class);
		suite.addTestSuite(FacileMscorlibV2_0_50727_1433Tests.class);
		suite.addTestSuite(FacileMscorlibV4_0_4_0_0_0Tests.class);
		suite.addTestSuite(FacileMscorlibV4_0_30319Tests.class);
		
		suite.addTestSuite(FacileMscorlibGeneralAPITests.class);
		suite.addTestSuite(FacileNativeTests.class);
		
		//$JUnit-END$
		return suite;
	}

}

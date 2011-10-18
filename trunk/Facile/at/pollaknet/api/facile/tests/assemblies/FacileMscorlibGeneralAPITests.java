package at.pollaknet.api.facile.tests.assemblies;

import junit.framework.TestCase;
import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;

public class FacileMscorlibGeneralAPITests extends TestCase {

	private final static String PATH_MSCORLIBV1_0_33_00_0 = "assemblies/v1.0.3300.0/mscorlib.dll";
	private final static String PATH_MSCORLIBV1_0_50_00_0 = "assemblies/v1.0.5000.0/mscorlib.dll";
	private final static String PATH_MSCORLIBV2_0_31005_0 = "assemblies/v2.0.31005.0/mscorlib.dll";
	private final static String PATH_MSCORLIBV2_0_50727_1433 = "assemblies/v2.0.50727.1433/mscorlib.dll";
	private final static String PATH_MSCORLIBV2_0_50727_832 = "assemblies/v2.0.50727.832/mscorlib.dll";
	private final static String PATH_MSCORLIBV4_0_30319 = "assemblies/v4.0.30319/mscorlib.dll";
	
	private static Assembly mscrolibV1_0_33_A = null;
	private static Assembly mscrolibV1_0_33_B = null;
	private static Assembly mscrolibV1_0_50_A = null;
	private static Assembly mscrolibV1_0_50_B = null;
	private static Assembly mscrolibV2_0_31005_A = null;
	private static Assembly mscrolibV2_0_31005_B = null;
	private static Assembly mscrolibV2_0_50727_1433_A = null;
	private static Assembly mscrolibV2_0_50727_1433_B = null;
	private static Assembly mscrolibV2_0_50727_832_A = null;
	private static Assembly mscrolibV2_0_50727_832_B = null;
	private static Assembly mscrolibV4_0_30319_A = null;
	private static Assembly mscrolibV4_0_30319_B = null;
	
	private static boolean inited = false;
	
	public FacileMscorlibGeneralAPITests(String name) {
		super(name);
	}
	
    protected void setUp() { 
	    if(!inited) {
			try {
				mscrolibV1_0_33_A = Facile.loadAssembly(PATH_MSCORLIBV1_0_33_00_0);
				mscrolibV1_0_33_B = Facile.loadAssembly(PATH_MSCORLIBV1_0_33_00_0);
				mscrolibV1_0_50_A = Facile.loadAssembly(PATH_MSCORLIBV1_0_50_00_0);
				mscrolibV1_0_50_B = Facile.loadAssembly(PATH_MSCORLIBV1_0_50_00_0);
				mscrolibV2_0_31005_A = Facile.loadAssembly(PATH_MSCORLIBV2_0_31005_0);
				mscrolibV2_0_31005_B = Facile.loadAssembly(PATH_MSCORLIBV2_0_31005_0);
				mscrolibV2_0_50727_1433_A = Facile.loadAssembly(PATH_MSCORLIBV2_0_50727_1433);
				mscrolibV2_0_50727_1433_B = Facile.loadAssembly(PATH_MSCORLIBV2_0_50727_1433);
				mscrolibV2_0_50727_832_A = Facile.loadAssembly(PATH_MSCORLIBV2_0_50727_832);
				mscrolibV2_0_50727_832_B = Facile.loadAssembly(PATH_MSCORLIBV2_0_50727_832);
				mscrolibV4_0_30319_A = Facile.loadAssembly(PATH_MSCORLIBV4_0_30319);
				mscrolibV4_0_30319_B = Facile.loadAssembly(PATH_MSCORLIBV4_0_30319);
				inited = true;
			} catch (Exception e) {
				fail();
			}
  		 }
	 }

	public void testAssemblyEquals() {
	
		//test equality of the different instances
		assertTrue(mscrolibV1_0_33_A.equals(mscrolibV1_0_33_B));
		assertTrue(mscrolibV1_0_33_B.equals(mscrolibV1_0_33_A));
		
		assertTrue(mscrolibV1_0_50_A.equals(mscrolibV1_0_50_B));
		assertTrue(mscrolibV1_0_50_B.equals(mscrolibV1_0_50_A));
		
		assertTrue(mscrolibV2_0_31005_A.equals(mscrolibV2_0_31005_B));
		assertTrue(mscrolibV2_0_31005_B.equals(mscrolibV2_0_31005_A));
		
		assertTrue(mscrolibV2_0_50727_1433_A.equals(mscrolibV2_0_50727_1433_B));
		assertTrue(mscrolibV2_0_50727_1433_B.equals(mscrolibV2_0_50727_1433_A));
		
		assertTrue(mscrolibV2_0_50727_832_A.equals(mscrolibV2_0_50727_832_B));
		assertTrue(mscrolibV2_0_50727_832_B.equals(mscrolibV2_0_50727_832_A));
		
		assertTrue(mscrolibV4_0_30319_A.equals(mscrolibV4_0_30319_B));
		assertTrue(mscrolibV4_0_30319_B.equals(mscrolibV4_0_30319_A));
		
		//perform equals-test between the different mscorlib versions
		assertFalse(mscrolibV1_0_33_A.equals(mscrolibV1_0_50_A));
		assertFalse(mscrolibV1_0_33_A.equals(mscrolibV2_0_31005_A));
		assertFalse(mscrolibV1_0_33_A.equals(mscrolibV2_0_50727_1433_A));
		assertFalse(mscrolibV1_0_33_A.equals(mscrolibV2_0_50727_832_A));
		assertFalse(mscrolibV1_0_33_A.equals(mscrolibV4_0_30319_A));
		
		assertFalse(mscrolibV1_0_50_A.equals(mscrolibV2_0_31005_A));
		assertFalse(mscrolibV1_0_50_A.equals(mscrolibV2_0_50727_1433_A));
		assertFalse(mscrolibV1_0_50_A.equals(mscrolibV2_0_50727_832_A));
		assertFalse(mscrolibV1_0_50_A.equals(mscrolibV4_0_30319_A));
		
		assertFalse(mscrolibV2_0_31005_A.equals(mscrolibV2_0_50727_1433_A));
		assertFalse(mscrolibV2_0_31005_A.equals(mscrolibV2_0_50727_832_A));
		assertFalse(mscrolibV2_0_31005_A.equals(mscrolibV4_0_30319_A));

		assertFalse(mscrolibV2_0_50727_1433_A.equals(mscrolibV2_0_50727_832_A));
		assertFalse(mscrolibV2_0_50727_1433_A.equals(mscrolibV4_0_30319_A));
		
		assertFalse(mscrolibV2_0_50727_832_A.equals(mscrolibV4_0_30319_A));

	}
	
}

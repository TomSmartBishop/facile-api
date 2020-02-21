package at.pollaknet.api.facile.tests.local;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.logging.Logger;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileLogHandler;
import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.exception.CoffPeDataNotFoundException;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;
import junit.framework.TestCase;

public class TestDrives extends TestCase {

	//private static Hashtable<String,Hashtable<String,StatCounter>> stats = new Hashtable<String,Hashtable<String,StatCounter>>();

	private static FileFilter filter;

	private int fileCount = 0;
	private int loadedAssemblyCount = 0;
	private int failedCount = 0;

	private static boolean performExtendedTests = false;
	
//	private long startTime;
//	
//	private static final double ME_BI_1024x1024 = 1048576;
	
	private static Logger logger;
	private static FacileLogHandler facileLogHandler;
	
	static {
		facileLogHandler = new FacileLogHandler();
	    logger = Logger.getLogger("at.pollaknet.api.facile");
	    logger.addHandler(facileLogHandler);
	    logger.setUseParentHandlers(false);
	}
	
	public TestDrives() {
	}
	
	public TestDrives(String name) {
		super(name);
	}
	
	public static void main(String [] args) {
		new TestDrives().testAllAssembliesOnDriveLocalDrives();
	}
	
	public void testAllAssembliesOnDriveLocalDrives() {

		filter = new FileFilter() {
			public boolean accept(File file) {
				String name = file.getName();
				if(name.endsWith(".exe")) 		return true;
				if(name.endsWith(".dll")) 		return true;
				if(name.endsWith(".netmodule")) return true;
				if(name.endsWith(".mcl")) 		return true;
				return file.isDirectory();

			}
		};
		
		//System.out.println("PDB;Size (MiBi);Time (sec);Used Heap (MiBi);Used NonHeap (MiBi); Total Mem (MiBi);Nr. of Classes;Types;Name;;Nr;");
		
		String operatingSystem = System.getProperty("os.name").toLowerCase();
		
		if (operatingSystem.startsWith("win")) {
			for(char letter='A';letter<='Z';letter++) {
				addAndInvokeFiles(letter + ":\\");
			}
		} else {
			addAndInvokeFiles("/");
		}
		

		
		System.out.println("\nProcessed "+ fileCount + " files.");
		System.out.println("(" + failedCount + " out of " + (failedCount+loadedAssemblyCount) + " assemblies failed)");

		//System.out.println(buildStatsString());
		
		if(failedCount>0) fail();
	}


	private void addAndInvokeFiles(String directoryPath) {
		
		File currentDirectory = new File(directoryPath);
		
		File [] filesInDirectory = currentDirectory.listFiles(filter);

		if(filesInDirectory!=null) {
			for(File file:filesInDirectory) {
				if(file.isDirectory()) {
					addAndInvokeFiles(file.getAbsolutePath());
				} else {
					tryAssembly(file);
					fileCount++;

					if(fileCount%100==0 && fileCount!=0) {
						System.out.println(String.format("Passed %5d  files... (%5d Assemblies)", fileCount, loadedAssemblyCount));
						System.gc();
					}
				}
			}
		}
	}

	
	private void tryAssembly(File file) {
	    String path = file.getAbsolutePath();
	    
		try {
//			System.runFinalization();	// see also: http://java.sun.com/developer/technicalArticles/javase/finalization/
//			System.gc();
//	 
//			Thread.sleep(50);
			
//			startTime = System.nanoTime();
			
			FacileReflector reflector = Facile.load(path);
			
//			double usedTime = (System.nanoTime() - startTime)*0.000000001;
//			
//			double heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()/ME_BI_1024x1024;
//			double nonHeap = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed()/ME_BI_1024x1024;	
//			double total = heap + nonHeap;
//			int numClasses = ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
//			
			
			Assembly assembly = reflector.loadAssembly();
			
			if(performExtendedTests) {
				Assembly comparisonAssembly = Facile.loadAssembly(path);
				if(!assembly.equals(comparisonAssembly))
					throw new Exception("Equals test failed!");
				if(!comparisonAssembly.equals(assembly))
					throw new Exception("Equals test failed!");
			}
//			
//			System.out.print(String.format("%d;%3.3f;",
//					reflector.isDebugDataAvailable()?1:0, file.length()/ME_BI_1024x1024));
//			
//		    System.out.print(String.format("%3.3f;%3.3f;%3.3f;%3.3f;",
//		    		usedTime, heap, nonHeap, total));
//		    
//		    System.out.print(String.format("%d;%d;%s;;%d;\n",
//		    		numClasses, assembly.getAllTypes().length, assembly.getName(), loadedAssemblyCount));
//		    
			loadedAssemblyCount++;
			
//			PESectionHeader fileSection = new PESectionHeader();
//			//the collection is sorted by the virtual address (ascending)
//			for(PESectionHeader header: reflector.getPeSectionHeaders()) {
//				if(header.getRelativeVirtualAddress()<=reflector.getPeDataDirectories().getClrHeaderRVA()) {
//					fileSection = header;
//				}
//				//if(header.getSectionName().startsWith(".reloc")) {
//				//	relocSection = header;
//				//}
//			}
//	
//			String name = fileSection.getSectionName();
//			addToStats("FileSection", name==null?"unknown":name);
			
//			addToStats("PE_DATA_DIRECTORY;Export Directory Size", String.valueOf(reflector.getPeDataDirectories().getExceptionDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;Resource Directory Size", String.valueOf(reflector.getPeDataDirectories().getResourceDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;Exception Directory Size", String.valueOf(reflector.getPeDataDirectories().getExceptionDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;Certificate Directory Size", String.valueOf(reflector.getPeDataDirectories().getCertificateDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;Debug Directory Size", String.valueOf(reflector.getPeDataDirectories().getDebugDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;Architecture Directory Size", String.valueOf(reflector.getPeDataDirectories().getCopyrightDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;Global Pointer Directory Size", String.valueOf(reflector.getPeDataDirectories().getPointerDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;TLS Directory Size", String.valueOf(reflector.getPeDataDirectories().getThreadLocalStorageDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;Load Config Directory Size", String.valueOf(reflector.getPeDataDirectories().getLoadConfigDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;Bound Import Directory Size", String.valueOf(reflector.getPeDataDirectories().getBoundImportDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;Delay Load Directory Size", String.valueOf(reflector.getPeDataDirectories().getDelayLoadDirectorySize()));
//			addToStats("PE_DATA_DIRECTORY;Reserved Directory Size", String.valueOf(reflector.getPeDataDirectories().getReservedDirectorySize()));
//			
//			addToStats("CLI_HEADER;Runtime Version", reflector.getCliHeader().getMajorRuntimeVersion() + "." + reflector.getCliHeader().getMinorRuntimeVersion());
//
//			addToStats("CLI_HEADER;Size of Code Manager Table", String.valueOf(reflector.getCliHeader().getSizeOfCodeManagerTable()));
//			addToStats("CLI_HEADER;Size of Export Address Table", String.valueOf(reflector.getCliHeader().getSizeOfExportAddressTable()));
//			//addToStats("CLI_HEADER;Size of Metadata Directory", String.valueOf(reflector.getCliHeader().getSizeOfMetadataDirectory()));
//			addToStats("CLI_HEADER;Size of Precompiled Header", String.valueOf(reflector.getCliHeader().getSizeOfPrecompiledHeader()));
//			//addToStats("CLI_HEADER;Size of Resource Directory", String.valueOf(reflector.getCliHeader().getSizeOfResourcesDirectory()));
//			addToStats("CLI_HEADER;Size of Strong Name Signature", String.valueOf(reflector.getCliHeader().getSizeOfStrongNameSignature()));
//			addToStats("CLI_HEADER;Size of VTable Fixup Directory", String.valueOf(reflector.getCliHeader().getSizeOfVTableFixupDirectory()));
//			
//			addToStats("CLI_METADATA_ROOT_HEADER;Header Version", reflector.getCliMetadataRootHeader().getMajorVersion() + "." + reflector.getCliMetadataRootHeader().getMinorVersion());
//			addToStats("CLI_METADATA_ROOT_HEADER;Number of Streams", String.valueOf(reflector.getCliMetadataRootHeader().getNumberOfStreams()));
//			addToStats("CLI_METADATA_ROOT_HEADER;Version String", String.valueOf(reflector.getCliMetadataRootHeader().getVersionString()));
//			
//			addToStats("CLI_METADATA;Unoptimized", String.valueOf(reflector.getMetadataStream().isUnoptimized()));
//			
//			addToStats("CLI_METADATA_GUID_STREAM;Size", String.valueOf(reflector.getGuidStream().getSize()));
//			
//			if(reflector.getMetadataStream().isUnoptimized()) {
//				System.out.println("This assembly is unoptimized:" + path);
//			}
//			
//			if(reflector.getGuidStream().getSize()>16) {
//				System.out.println("Detected multiple entries in #GUID stream:" + path);
//			}
//			
//			addToStats("CLI_METADATA_STREAM_HEADER;Header Version", "rtm: " + reflector.getCliHeader().getMajorRuntimeVersion() + "." + reflector.getCliHeader().getMinorRuntimeVersion() + "#~"
//					+ reflector.getMetadataStream().getMajorVersion() + "." +  reflector.getMetadataStream().getMajorVersion() + "/ root " + reflector.getCliMetadataRootHeader().getMajorVersion() + "." + reflector.getCliMetadataRootHeader().getMinorVersion() +
//					"-" + (reflector.getMetaModel().genericParam!=null && reflector.getMetaModel().genericParam.length>0));

			facileLogHandler.flush();
		} catch (Error e) {
			System.out.println("Unable to open: " + path + ":");
			e.printStackTrace();
		} catch (CoffPeDataNotFoundException e) {
			//it is ok, if there is no .net or coff/pe content
		} catch (IOException e) {
			System.out.println("Unable to read: " + path + " (" + e.getMessage() +")");
		} catch (RuntimeException e) {
			System.out.println("Exception in: " + path);
			e.printStackTrace();
			System.out.println("Log:\n" + facileLogHandler.toString());
			failedCount++;
		} catch (Exception e) {
			System.out.println("Exception in: " + path);
			e.printStackTrace();
			System.out.println("Log:\n" + facileLogHandler.toString());
			failedCount++;
		}
	}

//	
//	private static void addToStats(String statsKey, String statsValue) {
//		Hashtable<String, StatCounter> table = stats.get(statsKey);
//		
//		if(table==null) {
//			table =  new Hashtable<String, StatCounter>();
//			stats.put(statsKey, table);
//		}
//		StatCounter counter = table.get(statsValue);
//		
//		if(counter==null) {
//			table.put(statsValue, new StatCounter() );
//		} else {
//			counter.inc();
//		}
//		
//	}
//
//
//	@SuppressWarnings("unchecked")
//	private static String buildStatsString() {
//		StringBuffer buffer = new StringBuffer(1024);
//
//		Pair entries []  = new Pair[stats.size()];
//		
//		int i=0;
//		for(String key : stats.keySet()) {
//
//			Hashtable<String, StatCounter> table = stats.get(key);
//			
//			int j=0;
//			Pair values [] = new Pair[table.size()];
//			for(String collectedKey: table.keySet()) {
//				values[j] = new Pair<String, Integer>(collectedKey,table.get(collectedKey).getValue());
//				j++;
//			}
//			Arrays.sort(values);
//			
//			entries[i] = new Pair<String, Pair []>(key, values);
//			
//			i++;
//		}
//		
//		Arrays.sort(entries);
//		
//		for(Pair<String, Pair []> entry : entries) {
//			buffer.append(";;;\n");
//			buffer.append(entry.key);
//			buffer.append(";;\n");
//			
//			for(Pair<String, Integer> count : entry.value) {
//				buffer.append(String.format("\t;%s; %d;\n", count.key.replaceAll("\\p{Cntrl}"," "), count.value.intValue()));
//			}
//		}
//
//		return buffer.toString();
//	}
	
}

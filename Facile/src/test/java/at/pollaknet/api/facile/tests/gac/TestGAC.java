package at.pollaknet.api.facile.tests.gac;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileLogHandler;
import at.pollaknet.api.facile.exception.DotNetContentNotFoundException;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;
import junit.framework.TestCase;

public class TestGAC extends TestCase {

	private final static String GAC_WIN_ABSOLUTE_PATH 	= "C:/WINDOWS/Microsoft.NET";
	private final static String GAC_OSX_PATH_PREFIX 	= "/Library/Frameworks/Mono.framework/Versions";
	private final static String GAC_OSX_PATH_POSTFIX 	= "/lib/mono/gac";
	
	private static ArrayList<String> assemblies = null;

	private static FileFilter filter;
	private static Logger logger;
	private static FacileLogHandler facileLogHandler;
	
	static {
		facileLogHandler = new FacileLogHandler();
	    logger = Logger.getLogger("at.pollaknet.api.facile");
	    logger.addHandler(facileLogHandler);
	    logger.setUseParentHandlers(false);
	}
	
	private static void initGACFileList() {
		if(assemblies==null) {
			assemblies = new ArrayList<>(1024);
			
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
			
			String operatingSystem = System.getProperty("os.name").toLowerCase();
			
			if (operatingSystem.startsWith("win")) {
				addFiles(GAC_WIN_ABSOLUTE_PATH);
			} else if (operatingSystem.startsWith("mac")) {
				
				File currentDirectory = new File(GAC_OSX_PATH_PREFIX);
				File [] directoryFiles = currentDirectory.listFiles();

				if(directoryFiles!=null) { //check all 'version' folders (eg. 3.10.0)
					for (File file : directoryFiles) {
						if (file.isDirectory()) {
							addFiles(file.getAbsolutePath() + GAC_OSX_PATH_POSTFIX);
						}
					}
				}
			} else {
				assertTrue("Please define operating system first!", false);
			}
		}
	}

	private static void addFiles(String directoryPath) {
		
		File currentDirectory = new File(directoryPath);
		
		for(File file:currentDirectory.listFiles(filter)) {
			if(file.isDirectory()) {
				addFiles(file.getAbsolutePath());
			} else {
				assemblies.add(file.getAbsolutePath());
			}
		}
	}
	
	public TestGAC(String name) {
		super(name);
	}
	
	public void testAllAssembliesInGAC() throws RuntimeException {
		String currentFile = null;
		int fileCount = 0;
		int missingCount = 0;
		
		initGACFileList();
		
		for(String path: assemblies) {	
			try {
				Assembly assembly = Facile.loadAssembly(path);
				fileCount++;
				facileLogHandler.flush();
			} catch (Error e) {
				System.out.println("Error occurred: " + path);
				missingCount++;
			} catch (DotNetContentNotFoundException e) {
				System.out.println("No .net content found: " + path);
				missingCount++;
			} catch (IOException e) {
				System.out.println("Unable to read: " + path);
				missingCount++;
				System.out.println("Log:\n" + facileLogHandler.toString());
			} catch (RuntimeException e) {
				currentFile = path;
				System.out.println("Exception in: " + path);
				e.printStackTrace();
				System.out.println("Log:\n" + facileLogHandler.toString());
			} catch (Exception e) {
				currentFile = path;	
				System.out.println("Exception in: " + path);
				e.printStackTrace();
				System.out.println("Log:\n" + facileLogHandler.toString());
			}
		}
		
		System.out.println("\nProcessed "+ fileCount + " .net assemblies.");
		System.out.println("("+ missingCount + " files are missing; " + (assemblies.size()-fileCount-missingCount) + " failed to reflect/load)\n");
		
		if(fileCount+missingCount!=assemblies.size()) {
			fail(currentFile);
		}
	}

}

package at.pollaknet.api.facile.tests.gac;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.FacileLogHandler;
import at.pollaknet.api.facile.exception.CoffPeDataNotFoundException;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;
import at.pollaknet.api.facile.util.ByteReader;
import junit.framework.TestCase;

public class TestGACFusion extends TestCase {

	private final static String GAC_WIN_ABSOLUTE_PATH 	="C:/WINDOWS/assembly";
	private final static String GAC_OSX_PATH_PREFIX 	="/Library/Frameworks/Mono.framework/Versions";
	private final static String ALL_OSX_PATH_POSTFIX 	="/lib/mono";
	
	private static ArrayList<String> assemblies = null;

	private static FileFilter filter;
	private static Logger logger;
	private static FacileLogHandler facileLogHandler;
	
	static {
		filter = new FileFilter() {
			public boolean accept(File file) {
				String name = file.getName();
				if(name.endsWith(".exe")) 		return true;
				if(name.endsWith(".dll")) 		return true;
				if(name.endsWith(".netmodule")) return true;
				if(name.endsWith(".mcl")) 		return true;
				if(file.isDirectory())			return true;
				
				return name.equals("__AssemblyInfo__.ini");
			}
		};
		
		facileLogHandler = new FacileLogHandler();
	    logger = Logger.getLogger("at.pollaknet.api.facile");
	    logger.addHandler(facileLogHandler);
	    logger.setUseParentHandlers(false);
	}
	
	private static void initGACFileList() {
		if(assemblies==null) {
			assemblies = new ArrayList<>(1024);
			
			String operatingSystem = System.getProperty("os.name").toLowerCase();
			
			if (operatingSystem.startsWith("win")) {
				addFiles(GAC_WIN_ABSOLUTE_PATH);
			} else if (operatingSystem.startsWith("mac")) {

				File currentDirectory = new File(GAC_OSX_PATH_PREFIX);
				File [] directoryFiles = currentDirectory.listFiles();

				if(directoryFiles!=null) { //check all 'version' folders (eg. 3.10.0)
					for (File file : directoryFiles) {
						if (file.isDirectory()) {
							addFiles(file.getAbsolutePath() + GAC_OSX_PATH_PREFIX);
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
				if(file.getName().equals("__AssemblyInfo__.ini")) {
					try {
						FileInputStream stream = new FileInputStream(file);
						assertTrue(file.length()<=ByteReader.INT32_MAX_VAL);
						
						byte [] buffer = new byte[(int) file.length()];
						int bytesRead = stream.read(buffer);

						assert(bytesRead==(int)file.length());
						assert(stream.read()==-1); //we assume that we reached the end
						stream.close();
						
						String text = new String(buffer);
						
						int pos = text.indexOf("URL=file:///");
						
						while(pos>=0) {
							text = text.substring(pos+12);
						
							String pathToAssembly = text.substring(0, text.indexOf('\r'));
							if(!assemblies.contains(pathToAssembly)) {
								assemblies.add(pathToAssembly);
							}
							pos = text.indexOf("URL=file:///");
						}
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					assemblies.add(file.getAbsolutePath());
				}
				
			}
		}
	}
	
	public TestGACFusion(String name) {
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
			} catch (CoffPeDataNotFoundException e) {
				System.out.println("No .NET content found: " + path);
				missingCount++;
			} catch (IOException e) {
				System.out.println("Unable to read: " + path);
				missingCount++;
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

package at.pollaknet.api.facile.dia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.exception.NativeImplementationException;


public class PdbReader {

	private final static boolean ENABLE_DEBUG = false;
	
	private final static String CLASS_NAME = "PdbReader";
	
	private static boolean nativeLibraryLoaded = false;
	
	private native int openPdb(String pathToPdbFile);
	public native DebugInformation getLineNumbersByRVA(long relativeVirtualAddress);
	private native void closePdb();
	
	//return codes
	private final static int SUCCESS						=	0;
	private final static int ERROR_COM_INIT_FAILED			=	-1;
	private final static int ERROR_DIA_INIT_FAILED			=	-2;
	private final static int ERROR_OUT_OF_MEMORY			=	-3;
	private final static int ERROR_PDB_NOT_FOUND			=	-4;
	private final static int ERROR_DIA_SESSION_FAILED		=	-5;
	private final static int ERROR_NO_GLOBAL_SCOPE_FOUND	=	-6;
	private final static int ERROR_FIELD_NOT_FOUND			=	-7;
	
	//this field is used in the native implementation!
	private long nativeHandle = 0;
	
	public PdbReader(String pathToPdbFile) throws NativeImplementationException, FileNotFoundException, UnexpectedPdbContent {
		if(!nativeLibraryLoaded)
			throw new NativeImplementationException("Unable to locate " + CLASS_NAME + ".dll in the bin folder of the dia package!");
		
		int returnCode = openPdb(pathToPdbFile);
		
		if(returnCode!=SUCCESS) {
			close();
			
			switch(returnCode) {
				case ERROR_COM_INIT_FAILED:
					throw new NativeImplementationException("Initialization of the COM interface failed.");
				case ERROR_DIA_INIT_FAILED:
					throw new NativeImplementationException("Initialization of the msdia interface failed.");
				case ERROR_OUT_OF_MEMORY:
					throw new OutOfMemoryError();
				case ERROR_PDB_NOT_FOUND:
					throw new FileNotFoundException(pathToPdbFile);
				case ERROR_DIA_SESSION_FAILED:
					throw new UnexpectedPdbContent("Unable to open a valid dia session.");
				case ERROR_NO_GLOBAL_SCOPE_FOUND:
					throw new UnexpectedPdbContent("No global scope found.");
				case ERROR_FIELD_NOT_FOUND:
					throw new NativeImplementationException("The field \"long nativeHandle\" inside the PdbReader class is missing.");
				default:
					throw new NativeImplementationException("An unknown error occured!");
			}
		}
	}
	
	public boolean close() {
		if(nativeHandle!=0) {
			closePdb(); //here nativeHandle should be set to zero
			assert(nativeHandle==0);
			return true;
		}
		return false;
	}
	
	protected void finalize() {
		close();
	}
	
	/**
	 * The static constructor tries to extract the embedded dll file
	 * in order to access the native methods in this class.
	 */
    static {
    	
    	if(ENABLE_DEBUG) System.out.println("PdbReader.java: Performing setup inside the static constructor.");

    	//locate the current path of the binary file...
    	//find out the architecture of the JVM
    	String archDataModel = System.getProperty("sun.arch.data.model");
    	
    	if(archDataModel!="32" || archDataModel!="64")
    		archDataModel = System.getProperty("os.arch").endsWith("64") ? "64" : "32";
    	
    	String binaryName =
    			"bin" + archDataModel + System.getProperty("file.separator")
    			+ CLASS_NAME + archDataModel + ".dll";
    	
    	URL dllUrl = PdbReader.class.getResource(binaryName);

    	if(dllUrl==null) {
    		Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
    		logger.log(Level.SEVERE, "Unable to locate " + binaryName);
    	} else {
    	
	    	//continue with a string representation
	    	String dllPath = dllUrl.toString();
	
	    	//extra handling for eclipse bundle resources
	    	if(dllPath.startsWith("bundleresource:")) {
	    	
	    		if(ENABLE_DEBUG) System.out.println("PdbReader.java: Detected eclipse environment.");
	    		
		    	try {
		    		//get the converter class (which should be present inside an eclipse environment)
					@SuppressWarnings("rawtypes")
					Class converter = Class.forName("org.eclipse.core.runtime.internal.adaptor.URLConverterImpl");
					
					//create a new instance of the converter
					Object instance = converter.newInstance();
	
					//search for the "bundleToFileURL" converter method
					for(Method m :converter.getMethods()) {
						if(m.getName().equals("toFileURL")) {
							//invoke the method and save the resulting url
							dllUrl = (URL) m.invoke(instance, dllUrl);
							dllPath = dllUrl.toString();
							break;
						}
					}
					
					if(ENABLE_DEBUG) System.out.println("PdbReader.java: Extracted DLL to \"" + dllPath + "\".");
				} catch (Exception e) {
					Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
		    		logger.log(Level.SEVERE, e.getMessage());
				}
	    	} else {
	    		if(dllPath.startsWith("jar:file:")) {
	    			
	    			if(ENABLE_DEBUG) System.out.println("PdbReader.java: DLL is inside a java archive.");
	    			
					//trim trailing "jar:file:/"
					dllPath = dllPath.substring(4);
					//trim internal file description from !/package/...
					dllPath = dllPath.substring(0, dllPath.lastIndexOf('!'));
					
					try {
						//extract the file to temporary path
						dllPath = extractFromJar(binaryName, dllPath);
						
						if(ENABLE_DEBUG) System.out.println("PdbReader.java: Extracted DLL to \"" + dllPath + "\".");
					} catch (SecurityException e) {
			    		Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
			    		logger.log(Level.SEVERE, "SecurityException: unable to access \""
			    			+ dllPath + "\": " + e.getMessage());
					} catch (Exception e) {
			    		Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
			    		logger.log(Level.SEVERE, e.getMessage());
			    	}
				}
	    	}
	    	
			try {
				
				//There are a lot of other protocol types,
				//but only files are accepted.
				if(!dllPath.startsWith("file:")) {
					throw new Exception("Unknown resource format: " + dllPath);
				}
				
				//On top of the sorrowful experiences with bundle resources (and jars) 
				//using the facile api inside eclipse the received url's cannot be 
				//converted to uri because blanks are not represented as %20.
				if(dllPath.contains(" ")) {
					//trim the trailing "file:/"
					dllPath = dllPath.substring(6);
				} else {
					//the url seems to be valid - try to convert it into an uri
					//and trim only the trailing slash
					dllPath = dllUrl.toURI().getPath().substring(1);
				}

				if(ENABLE_DEBUG) System.out.println("PdbReader.java: Loading DLL from \"" + dllPath + "\".");
				
				//load the library
		    	System.load(dllPath);
		    	nativeLibraryLoaded  = true;
	
			} catch (UnsatisfiedLinkError e) {
	    		Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
	    		logger.log(Level.SEVERE, "UnsatisfiedLinkError: Unable to locate " + dllPath +
	    					": " + e.getMessage());
	    	} catch (URISyntaxException e) {
	    		Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
	    		logger.log(Level.SEVERE, "URISyntaxException: Unable to convert the " + CLASS_NAME + archDataModel +
	    					".dll location (\"" + dllPath + "\") to a valid URI: " + e.getMessage());
			} catch (Exception e) {
	    		Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
	    		logger.log(Level.SEVERE, e.getMessage());
			}
    	}
    }

    /**
     * Extract a file from a jar archive to a temporary path inside the user's home directory.
     * @param fileNameInArchive The file which should be detected inside the archive.
     * @param archivePath The path and name of the jar archive.
     * @return The path to the extracted file (as file descriptor with trailing "file:/").
     * @throws SecurityException if the are no permissions to create the
     * temporary directory and the file.
     * @throws FileNotFoundException if the specified file is not in the archive.
     * @throws IOException if an unexpected occurs during handling the archive or the
     * temporary data.
     */
	private static String extractFromJar(String fileNameInArchive, String archivePath)
			throws SecurityException, FileNotFoundException, IOException {

		//convert the path to an url
		URL url = new URL(archivePath);
		
		//open the archive		
		InputStream inputStream = url.openStream();
		ZipInputStream zipStream = new ZipInputStream(inputStream);
		
		//search for the required entry
		ZipEntry entry = null;
		do {
			if(entry!=null) zipStream.closeEntry();
			entry = zipStream.getNextEntry();
		} while(!entry.getName().endsWith(fileNameInArchive) && entry!=null);
		
		//check if the requested entry is available
		if(entry==null) {
			zipStream.closeEntry();
		    zipStream.close();
		    inputStream.close();
		    
			throw new FileNotFoundException(
				"Unable to locate " + fileNameInArchive + " in " + archivePath);
		}
		
		//create a temporary directory in the user's home directory
		String directory = System.getProperty("user.home") +
		System.getProperty("file.separator") + ".facileTemp";
		
		new File(directory).mkdirs();
		
		//create a new dll file in the temporary directory
		archivePath = directory + System.getProperty("file.separator") + CLASS_NAME + ".dll";
		FileOutputStream outputStream = new FileOutputStream(archivePath);
		
		//write the content of the dll file
        for (int c = zipStream.read(); c != -1; c = zipStream.read()) {
        	outputStream.write(c);
        }

        //close all streams
        zipStream.closeEntry();
        zipStream.close();
        inputStream.close();
        
		outputStream.close();

		//return the path of the created file
		return "file:/" + archivePath;
	} 
}

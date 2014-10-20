package at.pollaknet.api.facile;

import java.io.IOException;

import at.pollaknet.api.facile.exception.CoffPeDataNotFoundException;
import at.pollaknet.api.facile.exception.DotNetContentNotFoundException;
import at.pollaknet.api.facile.exception.SizeMismatchException;
import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.renderer.ILAsmRenderer;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;


/**
 * The {@link at.pollaknet.api.facile.Facile} class is a Factory to instantiate
 * a {@link at.pollaknet.api.facile.FacileReflector} object or a
 * {@link at.pollaknet.api.facile.symtab.symbols.scopes.Assembly}
 * interface implementation, which contains the reflected and loaded
 * data of a .NET assembly up to version 3.5. In the default case of
 * loading an assembly using the static {@link Facile#loadAssembly(String)}
 * method, the {@link at.pollaknet.api.facile.Facile}Factory internally uses a
 * {@link at.pollaknet.api.facile.FacileReflector} object to create an instance for you
 * (which implements the {@link at.pollaknet.api.facile.symtab.symbols.scopes.Assembly}
 * interface).
 * 
 * <p/>These .NET assemblies are binary programs, program libraries
 * or program modules containing <i>Common Intermediate Language (CIL)</i>
 * and are based on the <i>Common Language Infrastructure (CLI)</i>,
 * which is specified in the
 * <a href="//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf">
 * ECMA 335 Standard</a> (also known as .NET version 4.5). The current
 * implementation and all referenced are based on <b>revision 6!</b>
 *  
 * <p/>Common file extensions for .NET assemblies are: {@code .exe .dll .netmodule}
 * 
 * <p/>In Addition it is possible to extract the debug information of an assembly,
 * via a <a href="http://msdn2.microsoft.com/en-us/netframework/cc378097.aspx">
 * Program Debug Database (pdb)</a> file. The location (path) of this compiler
 * generated (e.g. Microsoft C# - Compiler
 * <a href="http://msdn2.microsoft.com/en-us/library/78f4aasd.aspx">csc</a>, which
 * can be obtained <a href="http://msdn2.microsoft.com/en-us/netframework/cc378097.aspx">
 * here</a>) file, is embedded inside the assembly. You can specify an alternative
 * path to the pdb file or use the embedded path.The current implementation supports pdb
 * files used by Microsoft Visual Studio version 2013.
 * 
 * <p/><b>Sample Code:</b>
 * <blockquote><pre>
 *try {
 *    //specify a path, where to find the assembly
 *    String assemblyName = "mscorlib.dll";
 *    String assemblyLocation = "../../" + assemblyName;
 *
 *    //reflect and load the assembly using the Facile factory
 *    Assembly assembly = Facile.loadAssembly(assemblyLocation);
 *			
 *    //perform you custom operations on the assembly...
 *    System.out.println("All defined types in " + assemblyName + ":\n");
 *    for(Type type : assembly.getAllTypes()) {
 *        System.out.println(type);
 *    }
 *} catch (Exception e) {
 *    e.printStackTrace();
 *}
 * </pre></blockquote>
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public class Facile {

	/*
	 * simple program to perform some practical tests
	 */
	public static void main(String [] args) {

		if(args.length<1) {
			printUsageString();
			return;
		}
		
		boolean createIlOutput = false;
		boolean binaryCustomAttributes = false;
		boolean outputMetadataStream = false;
		boolean outputStringsStream = false;
		boolean outputBlobStream = false;
		boolean outputGuidStream = false;
		boolean outputUserStringStream = false;
		boolean verbose = false;
		boolean log = false;
		boolean assemblyOverview = false;
		
		
		String assemblyPath = null;
		String pdbPath = null;
		
		//parameter parsing
		for(int i=0;i<args.length;i++) {
			
			if(args[i].equals("--il")) {
				createIlOutput = true;
			} else if(args[i].equals("--bca")) {
				binaryCustomAttributes = true;
			} else if(args[i].equals("--info")) {
				assemblyOverview = true;
			} else if(args[i].equals("--verbose")) {
				verbose = true;
			} else if(args[i].equals("--log")) {
				log = true;
			} else if(args[i].equals("--#~")) {
				outputStringsStream = true;
			} else if(args[i].equals("--#Strings")) {
				outputStringsStream = true;
			} else if(args[i].equals("--#Blob")) {
				outputBlobStream = true;
			} else if(args[i].equals("--#GUID")) {
				outputGuidStream = true;
			} else if(args[i].equals("--#US")) {
				outputUserStringStream = true;
			}
			else if(args[i].endsWith(".exe") ||
				    args[i].endsWith(".dll") ||
					args[i].endsWith(".netmodule") ||
					args[i].endsWith(".mcl") ) {
				if(assemblyPath==null) {
					assemblyPath = args[i];
				} else {
					pdbPath = args[i];
				}
			} else if( args[i].endsWith(".pdb") ) {
				pdbPath = args[i];
			}
		}
		
		//we need at least the assembly
		if(assemblyPath==null) {
			System.out.println("Error: No assembly specified!");
			printUsageString();
			return;
		}
		
		System.out.println("Specified Assembly: "+assemblyPath);
		if(pdbPath!=null)				System.out.println("Specified Program Debug Database: "+pdbPath);
		
		System.out.println();
		
		//output parameter setup
		System.out.println("Running Facile with the followin parameter setup:");
		System.out.println("=================================================");
		if(verbose)						System.out.println("--verbose       : Verbose output");
		if(log)							System.out.println("--log           : Print the log file at the end (all verbose messages)");
		if(assemblyOverview)			System.out.println("--info          : Print assembly info (overview)");
		if(createIlOutput)				System.out.println("--il            : Generating IL code in current working directory");
		if(binaryCustomAttributes)		System.out.println("--bca           : Generating binary custom attributes in IL code");
		if(outputMetadataStream)		System.out.println("--#~            : Output #~ (Metadata) Stream");
		if(outputStringsStream) 		System.out.println("--#Strings      : Output #Strings Stream");
		if(outputBlobStream)			System.out.println("--#Blob         : Output #Blob Stream");
		if(outputGuidStream)			System.out.println("--#GUID         : Output #GUID Stream");
		if(outputUserStringStream)		System.out.println("--#US           : Output #US (UserString) Stream");
		
		System.out.println();
			
		
		if(verbose) FacileReflector.getFacileLogHandler().SetIntermediate(true);
		
		//create a new FacileReflector :)
		FacileReflector facileReflector = null;
		try {
			if(pdbPath==null)
				facileReflector = Facile.load(assemblyPath);
			else
				facileReflector = Facile.load(assemblyPath, pdbPath);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}		

		if(outputMetadataStream) 		System.out.println(facileReflector.getMetadataStream());
		if(outputStringsStream) 		System.out.println(facileReflector.getStringsStream());
		if(outputBlobStream) 			System.out.println(facileReflector.getBlobStream());
		if(outputGuidStream) 			System.out.println(facileReflector.getGuidStream());
		if(outputUserStringStream) 		System.out.println(facileReflector.getUserStringStream());
		
		Assembly assembly = null;
		try {
			assembly = facileReflector.loadAssembly();
			
			if(assembly!=null) {
				
				if(assemblyOverview) {
					System.out.println(assembly.toExtendedString());
				}
				
				if(createIlOutput) {
				  ILAsmRenderer renderer = new ILAsmRenderer(facileReflector, binaryCustomAttributes);
				  renderer.renderSourceFilesToDirectory(assembly, System.getProperty("user.dir"));
				  System.out.println("Generated decompiled files in: " + System.getProperty("user.dir"));
				}
				
			} else {
				if(assemblyOverview) {
					System.out.println("File contains only resources.");
				}

			}
		} catch (DotNetContentNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		if(log) {
			System.out.println("Facile Log:");
			System.out.println(FacileReflector.getFacileLogHandler());
		}
		
		System.out.println("Done.");
	}

	private static void printUsageString() {
		System.out.println("usage:  java Facile ASSEMBLY [OPTIONAL_PDB] [OPTIONS]");
		System.out.println("=====================================================");
		System.out.println("The first specified file will be treated as the assembly,");
		System.out.println("the second as the PDB (program debug database).");
		System.out.println();
		System.out.println("ASSEMBLY:");
		System.out.println("  The .Net assembly to analyze.");
		System.out.println();
		System.out.println("OPTIONAL_PDB:");
		System.out.println("  The program debug database, or the assembly it self");
		System.out.println("  again, if it has embedded debug information.");
		System.out.println();
		System.out.println("OPTIONS:");
		System.out.println("  --verbose   : More intermediate output");
		System.out.println("  --log       : Print the wohle log file at the end");
		System.out.println("                (all messages from the verbose mode)");
		System.out.println("  --info      : Print assembly info (summary)");
		System.out.println("  --il        : Generate IL code in current working directory");
		System.out.println("  --bca       : Generate binary custom attributes in IL code");
		//System.out.println("  --il=FILE   : Generating IL code and dump to FILE");
		System.out.println("  --#~        : Output #~ (Metadata) Stream");
		System.out.println("  --#Strings  : Output #Strings Stream");
		System.out.println("  --#Blob     : Output #Blob Stream");
		System.out.println("  --#GUID     : Output #GUID Stream");
		System.out.println("  --#US       : Output #US (UserString) Stream");
	}
	
	/**
	 * Reflects all streams inside the .NET assembly and loads its content.
	 * 
	 * <p/>This also causes an access to the program debug database (pdb) specified
	 * inside the assembly (by the compiler). The debug information will be
	 * available if the pdb file is valid and the code is executed on Windows (this
	 * limitation is caused by the Microsoft Debug Interface Access DIA which exists
     * as dll only). Byte code will be loaded.
	 * 
	 * @param pathToAssembly A {@link String}, containing the path to the .NET
	 * assembly.
	 *
     * @param loadByteCode   Set to {@code true} to load the byte code, otherwise
	 * to {@code false}.
     *
	 * @return A loaded assembly accessible by the
	 * {@link at.pollaknet.api.facile.symtab.symbols.scopes.Assembly} interface.
	 * 
	 * @throws CoffPeDataNotFoundException if the specified file
	 * contains no COFF/PE data structure which contains the .NET assembly.
	 * 
	 * @throws DotNetContentNotFoundException if the specified file
	 * contains no .NET assembly.
	 *
	 * @throws UnexpectedHeaderDataException if the expected data inside a
	 * data header have not been found.
	 * 
	 * @throws SizeMismatchException if the calculated size of internal
	 * data does not match the real size inside the assembly.
	 *
	 * @throws IOException if the reading process of the specified file throws
	 * an {@link IOException}.
	 */
	public static Assembly loadAssembly(String pathToAssembly, boolean loadByteCode)
			throws CoffPeDataNotFoundException, DotNetContentNotFoundException, UnexpectedHeaderDataException,
				SizeMismatchException, IOException {
		FacileReflector reflector = new FacileReflector(pathToAssembly);
		Assembly assembly = reflector.loadAssembly(loadByteCode);
		return assembly;
	}

    /**
     * Reflects all streams inside the .NET assembly and loads its content.
     *
     * <p/>This also causes an access to the program debug database (pdb) specified
     * inside the assembly (by the compiler). The debug information will be
     * available if the pdb file is valid and the code is executed on Windows (this
	 * limitation is caused by the Microsoft Debug Interface Access DIA which exists
     * as dll only). Byte code will be loaded.
     *
     * @param pathToAssembly A {@link String}, containing the path to the .NET
     * assembly.
     *
     * @return A loaded assembly accessible by the
     * {@link at.pollaknet.api.facile.symtab.symbols.scopes.Assembly} interface.
     *
     * @throws CoffPeDataNotFoundException if the specified file
     * contains no COFF/PE data structure which contains the .NET assembly.
     *
     * @throws DotNetContentNotFoundException if the specified file
     * contains no .NET assembly.
     *
     * @throws UnexpectedHeaderDataException if the expected data inside a
     * data header have not been found.
     *
     * @throws SizeMismatchException if the calculated size of internal
     * data does not match the real size inside the assembly.
     *
     * @throws IOException if the reading process of the specified file throws
     * an {@link IOException}.
     */
    public static Assembly loadAssembly(String pathToAssembly)
            throws CoffPeDataNotFoundException, DotNetContentNotFoundException, UnexpectedHeaderDataException,
                SizeMismatchException, IOException {
        return loadAssembly(pathToAssembly, true);
    }

	/**
	 * Reflects all streams inside the .NET assembly and loads its content.
	 *
 	 * <p/>This also causes an access to the program debug database (pdb) specified
	 * by the {@code pathToPdb} parameter. The debug information will be available
	 * if the pdb file is valid and the code is executed on Windows (this limitation
	 * is caused by the Microsoft Debug Interface Access DIA which exists as dll only).
	 * Byte code will be loaded.
     *
	 * <p/>There is an automatic fall-back to the path specified inside the .NET
	 * assembly (by the compiler) if {@code pathToPdb} does not work.
	 *
	 * @param pathToAssembly A {@link String}, containing the path to the .NET assembly.
	 *
	 * @param pathToPdb A {@link String}, containing the path to the program debug
	 * database (pdb).
	 *
	 * @return A loaded assembly accessible by the
	 * {@link at.pollaknet.api.facile.symtab.symbols.scopes.Assembly} interface.
	 *
	 * @throws CoffPeDataNotFoundException if the specified file
	 * contains no COFF/PE data structure which contains the .NET assembly.
	 *
	 * @throws DotNetContentNotFoundException if the specified file
	 * contains no .NET assembly.
	 *
	 * @throws UnexpectedHeaderDataException if the expected data inside a
	 * data header have not been found.
	 *
	 * @throws SizeMismatchException if the calculated size of internal
	 * data does not match the real size in the assembly.
	 *
	 * @throws IOException if the reading process of the specified file throws
	 */
	public static Assembly loadAssembly(String pathToAssembly, String pathToPdb)
			throws CoffPeDataNotFoundException, DotNetContentNotFoundException,
				UnexpectedHeaderDataException, SizeMismatchException, IOException {
		FacileReflector reflector = new FacileReflector(pathToAssembly, pathToPdb);
		Assembly assembly = reflector.loadAssembly();
		return assembly;
	}
	
	/**
	 * Reflects all streams inside the specified buffer and loads its content
	 * (The {@code buffer} will be treated as binary representation of a .NET
	 * assembly).
	 * 
	 * <p/>This also causes an access to the program debug database (pdb) specified
	 * inside the {@code buffer} (compiler generated). The debug information will
	 * be available if the pdb file is valid and the code is executed on Windows (this
	 * limitation is caused by the Microsoft Debug Interface Access DIA which exists
     * as dll only). Byte code will be loaded.
	 * 
	 * @param buffer A {@code byte[]} containing the binary representation
	 * of a .NET assembly.
	 * 
	 * @return A loaded assembly accessible by the
	 * {@link at.pollaknet.api.facile.symtab.symbols.scopes.Assembly} interface.
	 *  
	 * @throws CoffPeDataNotFoundException if the specified file
	 * contains no COFF/PE data structure which contains the .NET assembly. 
	 * 
	 * @throws DotNetContentNotFoundException if the specified {@code buffer}
	 * contains no .NET assembly.
	 *
	 * @throws UnexpectedHeaderDataException if the expected data inside a
	 * data header have not been found.
	 *
	 * @throws SizeMismatchException if the calculated size of internal
	 * data does not match the real size in the {@code buffer}.
	 */
	public static Assembly loadAssembly(byte[] buffer)
			throws CoffPeDataNotFoundException, DotNetContentNotFoundException, 
				UnexpectedHeaderDataException, SizeMismatchException {
		FacileReflector reflector = new FacileReflector(buffer);
		Assembly assembly = reflector.loadAssembly();
		return assembly;
	}

	/**
	 * Reflects all streams inside the .NET assembly and loads its content.
	 * 
	 * <p/>This also causes an access to the program debug database (pdb) specified
	 * inside the assembly (by the compiler). The debug information will be
	 * available if the pdb file is valid and the code is executed on Windows (this
	 * limitation is caused by the Microsoft Debug Interface Access DIA which exists
     * as dll only). Byte code will be loaded.
	 * 
	 * @param pathToAssembly A {@link String}, containing the path to the .NET
	 * assembly.
	 * 
	 * @return A {@link at.pollaknet.api.facile.FacileReflector} object, which gives access to the
	 * loaded assembly.
	 * 
	 * @throws CoffPeDataNotFoundException if the specified file
	 * contains no COFF/PE data structure which contains the .NET assembly. 
	 * 
	 * @throws DotNetContentNotFoundException if the specified file
	 * contains no .NET assembly.
	 * 
	 * @throws UnexpectedHeaderDataException if the expected data inside a
	 * data header have not been found.
	 * 
	 * @throws SizeMismatchException if the calculated size of internal
	 * data does not match the real size in the assembly.
	 * 
	 * @throws IOException if the reading process of the specified file throws
	 * an {@link IOException}.
	 */
	public static FacileReflector load(String pathToAssembly)
			throws CoffPeDataNotFoundException, DotNetContentNotFoundException,
				UnexpectedHeaderDataException, SizeMismatchException, IOException {
		FacileReflector reflector = new FacileReflector(pathToAssembly);
		reflector.loadAssembly();
		return reflector;
	}
	
	/**
	 * Reflects all streams inside the .NET assembly and loads its content.
	 * 
	 * <p/>This also causes an access to the program debug database (pdb) specified
	 * by the {@code pathToPdb} parameter. The debug information will be available
	 * if the pdb file is valid and the code is executed on Windows (this limitation
	 * is caused by the Microsoft Debug Interface Access DIA which exists as dll
     * only). Byte code will be loaded.
	 *
	 * <p/>There is an automatic fall-back to the path specified inside the .NET
	 * assembly (by the compiler) if {@code pathToPdb} does not work.
	 * 
	 * @param pathToAssembly A {@link String}, containing the path to the .NET
	 * assembly.
	 * 
	 * @param pathToPdb A {@link String}, containing the path to the program
	 * debug database (pdb).
	 * 
	 * @return A {@link at.pollaknet.api.facile.FacileReflector} object, which 
	 * gives access to the loaded assembly.
	 * 
	 * @throws CoffPeDataNotFoundException if the specified file
	 * contains no COFF/PE data structure which contains the .NET assembly. 
	 * 
	 * @throws DotNetContentNotFoundException if the specified file
	 * contains no .NET assembly.
	 * 
	 * @throws UnexpectedHeaderDataException if the expected data inside a
	 * data header have not been found.
	 * 
	 * @throws SizeMismatchException if the calculated size of internal
	 * data does not match the real size in the assembly.
	 * 
	 * @throws IOException if the reading process of the specified file throws
	 * an {@link IOException}.
	 */
	public static FacileReflector load(String pathToAssembly, String pathToPdb)
			throws CoffPeDataNotFoundException, DotNetContentNotFoundException, 
			UnexpectedHeaderDataException, SizeMismatchException, IOException {
		FacileReflector reflector = new FacileReflector(pathToAssembly, pathToPdb);
		reflector.loadAssembly();
		return reflector;
	}

	/**
	 * Reflects all streams inside the specified buffer and loads its content.
	 * 
	 * <p/>This also causes an access to the program debug database (pdb) specified
	 * inside the {@code buffer} (compiler generated). The debug information will
	 * be available if the pdb file is valid and the code is executed on Windows (this
	 * limitation is caused by the Microsoft Debug Interface Access DIA which exists
     * as dll only). Byte code will be loaded.
	 * 
	 * @param buffer A {@code byte[]} containing the binary representation
	 * of a .NET assembly.
	 * 
	 * @return A {@link at.pollaknet.api.facile.FacileReflector} object, which gives
	 * access to the loaded assembly.
	 * 
	 * @throws CoffPeDataNotFoundException if the specified file
	 * contains no COFF/PE data structure which contains the .NET assembly. 
	 * 
	 * @throws DotNetContentNotFoundException if the specified {@code buffer}
	 * contains no .NET assembly.
	 * 
	 * @throws UnexpectedHeaderDataException if the expected data inside a
	 * data header have not been found.
	 * 
	 * @throws SizeMismatchException if the calculated size of internal
	 * data does not match the real size in the {@code buffer}.
	 */
	public static FacileReflector load(byte [] buffer)
			throws CoffPeDataNotFoundException, DotNetContentNotFoundException,
				UnexpectedHeaderDataException, SizeMismatchException {
		FacileReflector reflector = new FacileReflector(buffer);
		reflector.loadAssembly();
		return reflector;
	}

	/**
	 * Reflects the MetadataStream, StringsStream, UserStringStream, GuidStream
	 * and BlobStream of a valid .NET assembly specified by the
	 * {@code pathToAssembbly} parameter. 
	 * 
	 * <p/>If you would like to get a representation of the whole assembly please
	 * call the {@link at.pollaknet.api.facile.FacileReflector#loadAssembly()} method afterwards
	 * or use {@link at.pollaknet.api.facile.Facile#load(String)} instead of
	 * {@link at.pollaknet.api.facile.Facile#reflect(String)}.
	 * 
	 * <p><b>Note:</b> Calling the {@link at.pollaknet.api.facile.FacileReflector#loadAssembly()}
	 * method of the {@link at.pollaknet.api.facile.FacileReflector} class will cause an access
	 * to the program debug database (pdb) specified inside the assembly (by the compiler).
	 * The debug information will be available if the pdb file is valid and the code is executed 
	 * on Windows (this limitation is caused by the Microsoft Debug Interface Access DIA which 
     * exists as dll only). Byte code will be loaded.
	 * 
	 * @param pathToAssembly A {@link String}, containing the path to the .NET
	 * assembly.
	 * 
	 * @return A {@link at.pollaknet.api.facile.FacileReflector} object, capable of performing
	 * further actions on the assembly.
	 * 
	 * @throws CoffPeDataNotFoundException if the specified file
	 * contains no COFF/PE data structure which contains the .NET assembly. 
	 * 
	 * @throws DotNetContentNotFoundException if the specified file
	 * contains no .NET assembly.
	 * 
	 * @throws UnexpectedHeaderDataException if the expected data inside a
	 * data header have not been found.
	 * 
	 * @throws SizeMismatchException if the calculated size of internal
	 * data does not match the real size in the assembly.
	 * 
	 * @throws IOException if the reading process of the specified file throws
	 * an {@link IOException}.
	 */
	public static FacileReflector reflect(String pathToAssembly)
			throws CoffPeDataNotFoundException, DotNetContentNotFoundException,
				UnexpectedHeaderDataException, SizeMismatchException, IOException {
		return new FacileReflector(pathToAssembly);
	}
	
	/**
	 * Reflects the MetadataStream, StringsStream, UserStringStream, GuidStream
	 * and BlobStream of a valid .NET assembly specified by the
	 * {@code pathToAssembbly} parameter. 
	 * 
	 * <p/>If you would like to get a representation of the whole assembly please
	 * call the {@link at.pollaknet.api.facile.FacileReflector#loadAssembly()} method afterwards
	 * or use {@link at.pollaknet.api.facile.Facile#load(String, String)}
	 * instead of
	 * {@link at.pollaknet.api.facile.Facile#reflect(String, String)}.
	 * 
	 * <p/><b>Note:</b> Calling the {@link at.pollaknet.api.facile.FacileReflector#loadAssembly()}
	 * method of the {@link at.pollaknet.api.facile.FacileReflector} class will cause an access
	 * to the program debug database (pdb) specified by the {@code pathToPdb} parameter.
	 * The debug information will be available if the pdb file is valid and the code is executed 
	 * on Windows (this limitation is caused by the Microsoft Debug Interface Access DIA which 
     * exists as dll only). Byte code will be loaded.
	 * 
	 * <p/>There is an automatic fall-back to the path specified inside the .NET
	 * assembly (by the compiler) if {@code pathToPdb} does not work.
	 *  
	 * @param pathToAssembly A {@link String}, containing the path to the .NET
	 * assembly.
	 * 
	 * @param pathToPdb A {@link String}, containing the path to the program
	 * debug database (pdb).
	 * 
	 * @return A {@link at.pollaknet.api.facile.FacileReflector} object, capable of performing
	 * further actions on the assembly.
	 * 
	 * @throws CoffPeDataNotFoundException if the specified file
	 * contains no COFF/PE data structure which contains the .NET assembly. 
	 * 
	 * @throws DotNetContentNotFoundException if the specified file
	 * contains no .NET assembly.
	 * 
	 * @throws UnexpectedHeaderDataException if the expected data inside a
	 * data header have not been found.
	 * 
	 * @throws SizeMismatchException if the calculated size of internal
	 * data does not match the real size in the assembly.
	 * 
	 * @throws  IOException if the reading process of the specified file throws
	 * an {@link IOException}.
	 */	
	public static FacileReflector reflect(String pathToAssembly, String pathToPdb)
			throws CoffPeDataNotFoundException, DotNetContentNotFoundException,
				UnexpectedHeaderDataException, SizeMismatchException, IOException {
		return new FacileReflector(pathToAssembly, pathToPdb);
	}
	
	/**
	 * Treats the specified buffer as binary representation of a .NET assembly and
	 * Reflects the MetadataStream, StringsStream, UserStringStream, GuidStream and
	 * BlobStream.
	 * 
	 * <p/>If you would like to get a representation of the whole assembly
	 * please call the {@link at.pollaknet.api.facile.FacileReflector#loadAssembly()} method
	 * afterwards or use {@link at.pollaknet.api.facile.Facile#load(byte[])}
	 * instead of {@link at.pollaknet.api.facile.Facile#reflect(byte[])}.
	 * 
	 * <p><b>Note:</b> Calling the {@link at.pollaknet.api.facile.FacileReflector#loadAssembly()}
	 * method of the {@link at.pollaknet.api.facile.FacileReflector} class will cause an
	 * access to the program debug database (pdb) specified inside the
	 * {@code buffer} (compiler generated). The debug information will be
	 * available if the pdb file is valid and the code is executed on Windows (this
	 * limitation is caused by the Microsoft Debug Interface Access DIA which exists
     * as dll only). Byte code will be loaded.
	 * 
	 * @param buffer A {@code byte[]} containing the binary representation
	 * of a .NET assembly.
	 * 
	 * @throws CoffPeDataNotFoundException if the specified file
	 * contains no COFF/PE data structure which contains the .NET assembly. 
	 * 
	 * @return A FacileReflector object, capable of performing further actions on
	 * the assembly.
	 * 
	 * @throws DotNetContentNotFoundException if the specified
	 * {@code buffer} contains no .NET assembly.
	 * 
	 * @throws UnexpectedHeaderDataException if the expected data inside a
	 * data header have not been found.
	 * 
	 * @throws SizeMismatchException if the calculated size of internal
	 * data does not match the real size in the {@code buffer}.
	 */
	public static FacileReflector reflect(byte[] buffer)
			throws CoffPeDataNotFoundException, DotNetContentNotFoundException,
				UnexpectedHeaderDataException, SizeMismatchException {
		return new FacileReflector(buffer);
	}
}

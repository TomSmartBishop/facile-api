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
 * ECMA 335 Standard</a> (also known as .NET version 3.5). The current
 * implementation and all referenced are based on <b>revision 4!</b>
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
 * path to the pdb file or use the embedded path. The current implementation supports pdb
 * files up to version {@code "MSF 7.0 DS"}, which is equivalent to output produced by
 * compilers for .NET version 3.5 and Microsoft Visual Studio version 2008.
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


			 
		//ensure the correct file type by extension
		switch(args.length) {
			case 2:
				if(	!(	args[1].endsWith(".exe") ||
						args[1].endsWith(".dll") ||
						args[1].endsWith(".netmodule") ||
						args[1].endsWith(".mcl") ||
						args[1].endsWith(".pdb")
					 )		) {
					System.out.println("Please specify a valid program debug database!");
					return;
				}
				//fall through
			case 1:
				if(	!(	args[0].endsWith(".exe") ||
						args[0].endsWith(".dll") ||
						args[0].endsWith(".netmodule") ||
						args[0].endsWith(".mcl")
					 )		) {
					System.out.println("Please specify a exe, netmodule or dll file as assembly!");
					return;
				}
				break;
				
			default:
				System.out.println("usage:\n\tjava Facile ASSEMBLY_PATH [PDB_PATH]");
				return;
			
		}
		
		//create a new FacileReflector :)
		FacileReflector facileReflector = null;
		try {
			if(args.length==1)
				facileReflector = Facile.load(args[0]);
			else
				facileReflector = Facile.load(args[0], args[1]);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}		

		//System.out.println(facileReflector.getStringsStream());
		//System.out.println(facileReflector.getBlobStream());
		//System.out.println(facileReflector.getGuidStream());
		//System.out.println(facileReflector.getUserStringStream());
		
		//System.out.println(facileReflector.getMetaModel());
		//System.out.println();
		System.out.println(FacileReflector.getFacileLogHandler());
		
		
		Assembly assembly = null;
		try {
			assembly = facileReflector.loadAssembly();
			
			if(assembly!=null) {
				System.out.println(assembly.toExtendedString());
				ILAsmRenderer renderer = new ILAsmRenderer(facileReflector);
				renderer.renderSourceFilesToDirectory(assembly, System.getProperty("user.dir"));
				System.out.println("Generated decompiled files in: " + System.getProperty("user.dir"));
			} else {
				System.out.println("File contains only resources:");
				if(facileReflector.getStringsStream()!=null)
					System.out.println(facileReflector.getStringsStream());
				if(facileReflector.getBlobStream()!=null)
					System.out.println(facileReflector.getBlobStream());
				if(facileReflector.getGuidStream()!=null)
					System.out.println(facileReflector.getGuidStream());
				if(facileReflector.getUserStringStream()!=null)
					System.out.println(facileReflector.getUserStringStream());
			}
		} catch (DotNetContentNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	/**
	 * Reflects all streams inside the .NET assembly and loads its content.
	 * 
	 * <p/>This also causes an access to the program debug database (pdb) specified
	 * inside the assembly (by the compiler). The debug information will be
	 * available if the pdb file is valid and at the specified location.
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
		FacileReflector reflector = new FacileReflector(pathToAssembly);
		Assembly assembly = reflector.loadAssembly();
		return assembly;
	}
	
	/**
	 * Reflects all streams inside the .NET assembly and loads its content.
	 * 
	 * <p/>This also causes an access to the program debug database (pdb) specified
	 * by the {@code pathToPdb} parameter. The debug information will be available
	 * if the pdb file is valid and at the specified location.
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
	 * be available if the pdb file is valid and at the specified location.
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
	 * available if the pdb file is valid and at the specified location.
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
	 * if the pdb file is valid and at the specified location.
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
	 * be available if the pdb file is valid and at the specified location.
	 * 
	 * @param buffer A {@code byte[]} containing the binary representation
	 * of a .NET assembly.
	 * 
	 * @return A {@link at.pollaknet.api.facile.FacileReflector} object, which gives access to the
	 * loaded assembly.
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
	 * The debug information will be available if the pdb file is valid and at the
	 * specified location.
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
	 * The debug information will be available if the pdb file can be found at the
	 * specified location and if the content is valid.
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
	 * available if the pdb file is valid and at the specified location.
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

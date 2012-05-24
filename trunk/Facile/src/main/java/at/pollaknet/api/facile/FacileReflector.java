package at.pollaknet.api.facile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.code.CilContainer;
import at.pollaknet.api.facile.dia.PdbReader;
import at.pollaknet.api.facile.dia.UnexpectedPdbContent;
import at.pollaknet.api.facile.exception.CoffPeDataNotFoundException;
import at.pollaknet.api.facile.exception.DotNetContentNotFoundException;
import at.pollaknet.api.facile.exception.NativeImplementationException;
import at.pollaknet.api.facile.exception.SizeMismatchException;
import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.cli.CliHeader;
import at.pollaknet.api.facile.header.cli.CliMetadataRootHeader;
import at.pollaknet.api.facile.header.cli.StreamHeader;
import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UnknownStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.coffpe.COFFPEHeader;
import at.pollaknet.api.facile.header.coffpe.DOSHeader;
import at.pollaknet.api.facile.header.coffpe.PEDataDirectories;
import at.pollaknet.api.facile.header.coffpe.PEOptionalHeader;
import at.pollaknet.api.facile.header.coffpe.PESectionHeader;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.entries.AssemblyEntry;
import at.pollaknet.api.facile.symtab.SymbolTable;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;
import at.pollaknet.api.facile.util.ByteReader;


/**
 * The {@link at.pollaknet.api.facile.FacileReflector} is capable of reflecting and loading .NET
 * assemblies.
 * 
 * <p/>These .NET assemblies are binary programs, program libraries
 * or program modules containing <i>Common Intermediate Language (CIL)</i>
 * and are based on the <i>Common Language Infrastructure (CLI)</i>,
 * which is specified in the
 * <a href="//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf">
 * ECMA 335 Standard</a> (also known as .NET version 3.5). The current
 * implementation and all referenced are based on <b>revision 4!</b>
 * (revision 5 is currently in development)
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
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public class FacileReflector {

	/**
	 * The name of the java logger
	 */
	public final static String LOGGER_NAME = "at.pollaknet.api.facile";
	
	/**
	 * Debug parameter. True enables bug tracking. Set it to false
	 * in order to continue reflection even if an error occurs.
	 */
	public static boolean DEBUG_AND_HALT_ON_ERRORS = true;
	
	private static final int PARTIAL_LOADING_SIZE = 8388608; // 8 MiBi
	
	//the names of the required streams
	private final static String STREAM_SIGNATURE_METADATA 	= "#~";
	private final static String STREAM_SIGNATURE_STRINGS 	= "#Strings";
	private final static String STREAM_SIGNATURE_US 		= "#US";
	private final static String STREAM_SIGNATURE_GUID 		= "#GUID";
	private final static String STREAM_SIGNATURE_BLOB 		= "#Blob";
	
	private final static String STREAM_SIGNATURE_METADATA_ALTERNATIVE 	= "#-";
	
	//portable executable required headers
	private PEDataDirectories peDataDirectories;
	PriorityQueue<PESectionHeader> peSectionHeaders = new PriorityQueue<PESectionHeader>();

	//number of section inside the PE files
	private int numberOfSections;

	//the CLI header is the first header with the address of the root header
	private CliHeader cliHeader;
	
	//the metadata root header contains the information about the stored streams
	private CliMetadataRootHeader cliMetadataRootHeader;

	//streams
	private MetadataStream metadataStream;
	private StringsStream stringsStream;
	private UserStringStream userStringStream;
	private GuidStream guidStream;
	private BlobStream blobStream;
	
	//remember unspecified streams
	private List<UnknownStream> additionalStreams = new ArrayList<UnknownStream>();
	private Map<String, Integer> additionalStreamsSetup = new HashMap<String, Integer>();

	//stream sizes
	private int sizeOfMetadataStream;
	private int sizeOfStringsStream;
	private int sizeOfUserStringStream;
	private int sizeOfGuidStream;
	private int sizeOfBlobStream;
	
	//indices of the streams inside the array of the metadata root header
	private int indexOfMetadataStream = -1;
	private int indexOfStringsStream = -1;
	private int indexOfUserStringStream = -1;
	private int indexOfGuidStream = -1;
	private int indexOfBlobStream = -1;

	//physical addresses
	private int cliMetadataRootHeaderPA;
	private int cliHeaderPA;
	
	private MetadataModel metaModel;
	private AssemblyEntry assembly = null;
	private CilContainer codeContainer;
	
	private String pathToAssembly = null;
	private String pathToPdb = null;

	private DOSHeader dosHeader;

	private COFFPEHeader coffPeHeader;

	private PEOptionalHeader peOptionalHeader;

	//detect if the assembly contains a .il section with native implementations
	private boolean assemblyHasIlSection = false;

	//use partialLoading to avoid loading large binary, which do not contain CIL data
	private boolean partialLoaded = false;

	//the default logger and utility vars
	private static Logger logger;
	private static FacileLogHandler facileLogHandler;
	private boolean debugDataAvailable = false;

	static {
		facileLogHandler = new FacileLogHandler();
	    logger = Logger.getLogger(LOGGER_NAME);
	    logger.addHandler(facileLogHandler);
	    logger.setUseParentHandlers(false);
	}
	
	private FacileReflector () {
	    logger.info(String.format("Created Instance 0x%x", this.hashCode()));
	}
	
	FacileReflector(String pathToAssembly)
			throws	CoffPeDataNotFoundException,
					DotNetContentNotFoundException,
					UnexpectedHeaderDataException,
					SizeMismatchException, IOException {
		this();
		
		this.pathToAssembly = pathToAssembly;
		this.pathToPdb = pathToAssembly;
		
		byte[] buffer = getFileBuffer(pathToAssembly);
		
		processAssemblyTables(buffer);
	}

	FacileReflector(String pathToAssembly, String pathToPdb)
			throws	CoffPeDataNotFoundException,
					DotNetContentNotFoundException,
					UnexpectedHeaderDataException,
					SizeMismatchException, IOException {
		this();
		
		this.pathToAssembly = pathToAssembly;
		this.pathToPdb = pathToPdb;
		
		byte[] buffer = getFileBuffer(pathToAssembly);

		processAssemblyTables(buffer);
	}

	FacileReflector(byte[] buffer)
			throws	CoffPeDataNotFoundException,
					DotNetContentNotFoundException,
					UnexpectedHeaderDataException,
					SizeMismatchException {
		this();
		try {
			processAssemblyTables(buffer);
		} catch (IOException e) {
			logger.info(e.getMessage());
		}
	}
	
	private void processAssemblyTables(byte [] buffer)
			throws 	CoffPeDataNotFoundException,
					DotNetContentNotFoundException,
					UnexpectedHeaderDataException,
					SizeMismatchException, IOException {
		 
	    if(pathToAssembly!=null && pathToPdb!=null) {
		    logger.log(Level.INFO, "Assembly: " + pathToAssembly);
		    logger.log(Level.INFO, "Pdb: " + pathToPdb);
	    } else {
	    	 logger.log(Level.INFO, "No file specified. Using a byte buffer as source!");
	    	 
	    	//ensure an assembly name
			pathToAssembly = "<byte[]:" + buffer.hashCode() + ">";
	    }
	    logger.log(Level.INFO, "Starting COFF/PE processing.");
	    
	    assert(buffer!=null);
		processCoffPeData(buffer);
		
		logger.log(Level.INFO, "Finished COFF/PE processing.");
		
		logger.log(Level.INFO, "Starting CIL processing.");
		
		codeContainer = new CilContainer();

		processCilData(buffer);

		logger.log(Level.INFO, "Finished CIL processing.");
		logger.log(Level.INFO, "Deleting file buffer.");
		
		buffer = null;	
	}

	private void processCilData(byte[] buffer)
			throws UnexpectedHeaderDataException, SizeMismatchException,
				DotNetContentNotFoundException, IOException {
		int byteOffset;
		
		//start processing the CLR data
		cliHeader = new CliHeader();
		
		//search for the correct file section
		PESectionHeader fileSection = null;
		PESectionHeader relocSection = null;		
		long clrHeaderRVA = peDataDirectories.getClrHeaderRVA();
		
		//the collection is sorted by the virtual address (ascending)
		for(PESectionHeader header: peSectionHeaders) {
			if(header.getRelativeVirtualAddress()<=clrHeaderRVA) {
				fileSection = header;
			}
			//if(header.getSectionName().startsWith(".reloc")) {
			//	relocSection = header;
			//}
		}
		
		codeContainer.setPeFileSections(peSectionHeaders.toArray(new PESectionHeader [0]));
		
		//do not continue if there is no CLR data
		if(fileSection == null)
			throw new DotNetContentNotFoundException("No CLR Header found.");
		
		assert(fileSection.getPointerToRawData() < ByteReader.INT32_MAX_VAL);
		
		//store virtual and physical address for further offset calculations
		int fileSectionPA = (int) fileSection.getPointerToRawData();
		long fileSectionRVA = fileSection.getRelativeVirtualAddress();
		
		long offsetToData =  peDataDirectories.getClrHeaderRVA() - fileSectionRVA;
		assert(fileSectionPA + offsetToData < ByteReader.INT32_MAX_VAL);
		cliHeaderPA += fileSectionPA + offsetToData;
		
		if(partialLoaded && (cliHeaderPA+CliHeader.STATIC_HEADER_SIZE)>PARTIAL_LOADING_SIZE) {
			buffer = getFileBuffer(pathToAssembly);
		}
		
		//the address is mal-formed
		if(cliHeaderPA>buffer.length-CliHeader.STATIC_HEADER_SIZE) {
			throw new DotNetContentNotFoundException("No CLR Header found.");
		}
		
		//read the CLR header information 
		cliHeader.read(buffer, cliHeaderPA);
		
		//read the meta data header
		cliMetadataRootHeader = new CliMetadataRootHeader();
		
		if(cliHeader.getSizeOfMetadataDirectory()<=0) {
			for(PESectionHeader sectionHeader : peSectionHeaders ) {
				if(sectionHeader.getSectionName().startsWith(".il")) {
					fileSectionRVA=0;
					assert(sectionHeader.getPointerToRawData()<=ByteReader.INT32_MAX_VAL);
					fileSectionPA = (int)sectionHeader.getPointerToRawData();
					assemblyHasIlSection = true;
				}
			}
		} 
		
		offsetToData = cliHeader.getAddrOfMetadataDirectory() - fileSectionRVA;
		assert(fileSectionPA + offsetToData < ByteReader.INT32_MAX_VAL);
		cliMetadataRootHeaderPA = (int) (fileSectionPA + offsetToData);
		
		if(cliMetadataRootHeaderPA<0) {
			throw new DotNetContentNotFoundException("No CLI metadata header found!");
		}
			
		//perform a full load (at this point it is sure, that it is a .net assembly)
		if(partialLoaded) {
			buffer = getFileBuffer(pathToAssembly);
		}
		
		cliMetadataRootHeader.read(buffer, cliMetadataRootHeaderPA);
		
		//pass file buffer reference to the code buffer
		codeContainer.setCodeBuffer(buffer);
		
		//get the indices of the 5 required streams
		getStreamIndices();
		
		//helper for the handling of the stream header
		StreamHeader streamHeader;
				
		if(indexOfMetadataStream>=0) {
			
			//start with metadata stream reading
			metadataStream = new MetadataStream(cliMetadataRootHeader.isUnoptimized());
			
			streamHeader = cliMetadataRootHeader.getStreamHeaders()[indexOfMetadataStream];
			offsetToData = streamHeader.getStreamOffset();
			
			assert(cliMetadataRootHeaderPA + offsetToData < ByteReader.INT32_MAX_VAL);
			byteOffset = (int) (cliMetadataRootHeaderPA + offsetToData);
			
			sizeOfMetadataStream = metadataStream.read(buffer, byteOffset);
		} else {
			logger.info("File contains no metadata.");
		}
		
		if(indexOfStringsStream>=0) {
			//start with string stream reading
			streamHeader = cliMetadataRootHeader.getStreamHeaders()[indexOfStringsStream];
			assert(streamHeader.getStreamSize() < ByteReader.INT32_MAX_VAL);
			stringsStream = new StringsStream((int) streamHeader.getStreamSize());
			
			offsetToData = streamHeader.getStreamOffset();
			
			assert(cliMetadataRootHeaderPA + offsetToData < ByteReader.INT32_MAX_VAL);
			byteOffset = (int) (cliMetadataRootHeaderPA + offsetToData);
			
			sizeOfStringsStream = stringsStream.read(buffer, byteOffset);
			
			if(sizeOfStringsStream != streamHeader.getStreamSize()) {
				StringBuffer msg = new StringBuffer("\nThe size of the #Strings stream content ");
				msg.append("is not equal to the header information.\n(");
				msg.append(sizeOfStringsStream);
				msg.append(" instead of ");
				msg.append(streamHeader.getStreamSize());
				msg.append(")");
				throw new SizeMismatchException(msg.toString());
			}
		}		
		
		if(indexOfUserStringStream>=0) {
			//start with string stream reading
			streamHeader = cliMetadataRootHeader.getStreamHeaders()[indexOfUserStringStream];
			assert(streamHeader.getStreamSize() < ByteReader.INT32_MAX_VAL);
			userStringStream = new UserStringStream((int) streamHeader.getStreamSize());
			
			offsetToData = streamHeader.getStreamOffset();
			
			assert(cliMetadataRootHeaderPA + offsetToData < ByteReader.INT32_MAX_VAL);
			byteOffset = (int) (cliMetadataRootHeaderPA + offsetToData);
			
			sizeOfUserStringStream = userStringStream.read(buffer, byteOffset);
			if(sizeOfUserStringStream != streamHeader.getStreamSize()) {	
				StringBuffer msg = new StringBuffer("\nThe size of the #US stream content ");
				msg.append("is not equal to the header information.\n(");
				msg.append(sizeOfUserStringStream);
				msg.append(" instead of ");
				msg.append(streamHeader.getStreamSize());
				msg.append(")");
				throw new SizeMismatchException(msg.toString());
			}
		}
		
		if(indexOfGuidStream>=0) {
			//start with string stream reading
			streamHeader = cliMetadataRootHeader.getStreamHeaders()[indexOfGuidStream];
			assert(streamHeader.getStreamSize() < ByteReader.INT32_MAX_VAL);
			guidStream = new GuidStream((int) streamHeader.getStreamSize());
			
			offsetToData = streamHeader.getStreamOffset();
			
			assert(cliMetadataRootHeaderPA + offsetToData < ByteReader.INT32_MAX_VAL);
			byteOffset = (int) (cliMetadataRootHeaderPA + offsetToData);
			
			sizeOfGuidStream = guidStream.read(buffer, byteOffset);
			if(sizeOfGuidStream != streamHeader.getStreamSize()) {
				StringBuffer msg = new StringBuffer("\nThe size of the #GUID stream content ");
				msg.append("is not equal to the header information.\n(");
				msg.append(sizeOfGuidStream);
				msg.append(" instead of ");
				msg.append(streamHeader.getStreamSize());
				msg.append(")");
				throw new SizeMismatchException(msg.toString());
			}
		}
		
		if(indexOfBlobStream>=0) {
			//start with string stream reading
			streamHeader = cliMetadataRootHeader.getStreamHeaders()[indexOfBlobStream];
			assert(streamHeader.getStreamSize() < ByteReader.INT32_MAX_VAL);
			blobStream = new BlobStream((int) streamHeader.getStreamSize());
			
			offsetToData = streamHeader.getStreamOffset();
			
			assert(cliMetadataRootHeaderPA + offsetToData < ByteReader.INT32_MAX_VAL);
			byteOffset = (int) (cliMetadataRootHeaderPA + offsetToData);
			
			sizeOfBlobStream = blobStream.read(buffer, byteOffset);
			if(sizeOfBlobStream != streamHeader.getStreamSize()) {
				StringBuffer msg = new StringBuffer("\nThe size of the #Blob stream content ");
				msg.append("is not equal to the header information.\n(");
				msg.append(sizeOfBlobStream);
				msg.append(" instead of ");
				msg.append(streamHeader.getStreamSize());
				msg.append(")");
				throw new SizeMismatchException(msg.toString());
			}
			
		}

		//check additional streams
		if(!additionalStreamsSetup.isEmpty()) {
			for(String name : additionalStreamsSetup.keySet()) {
				
				int index = additionalStreamsSetup.get(name);
				
				//read an add the unknown stream
				streamHeader = cliMetadataRootHeader.getStreamHeaders()[index];
				
				//start with string stream reading
				assert(streamHeader.getStreamSize() < ByteReader.INT32_MAX_VAL);
				UnknownStream unknownStream = new UnknownStream(streamHeader.getName(), (int)streamHeader.getStreamSize());
				
				offsetToData = streamHeader.getStreamOffset();
				
				//calculate the offset
				assert(cliMetadataRootHeaderPA + offsetToData < ByteReader.INT32_MAX_VAL);
				byteOffset = (int) (cliMetadataRootHeaderPA + offsetToData);
				
				//ignore return size of unknown streams and add to array list
				unknownStream.read(buffer, byteOffset);
				additionalStreams.add(unknownStream);
			}
		}
		//the relocations can be ignored
		//relocations = extractRelocations(buffer, relocSection);
	}

	//relocations are not needed (kept implementation)
//	private PriorityQueue<Pair<Integer, Integer []>> extractRelocations(byte[] buffer, PESectionHeader relocSection ) {		
//		if(relocSection==null) {
//			return new PriorityQueue<Pair<Integer, Integer []>>(0);
//		}
//	
//		PriorityQueue<Pair<Integer, Integer []>> relocations;relocations = new PriorityQueue<Pair< Integer, Integer []>>();
//		assert(relocSection.getPointerToRawData()<=ByteReader.INT32_MAX_VAL);
//		int dataPointer = (int) relocSection.getPointerToRawData();
//		int relocOffset = dataPointer;
//		long pageAddress;
//		long sizeOfBlock;
//		
//		while(relocOffset-dataPointer<relocSection.getVirtualSize()) {
//			pageAddress = ByteReader.getInt32(buffer, relocOffset);	relocOffset +=4;
//			sizeOfBlock = ByteReader.getInt32(buffer, relocOffset);	relocOffset +=4;
//
//			if(pageAddress==0&&sizeOfBlock==0) break;
//			
//			//subtract the size of the page address and the block size
//			long numberOfEntries =  (sizeOfBlock - 8) >> 1;
//			
//			assert(numberOfEntries<=ByteReader.INT32_MAX_VAL);
//			
//			ArrayList<Integer> offsets = new ArrayList<Integer>();
//			for(int i=0;i<numberOfEntries;i++) {
//				int offset = ByteReader.getInt16(buffer, relocOffset);
//				relocOffset +=2;
//				
//				if(offset!=0) offsets.add(offset);
//			}
//			
//			relocations.add(new Pair<Integer, Integer []>((int)pageAddress, offsets.toArray(new Integer[0])));
//		}
//		
//		return relocations;
//	}
	

	/**
	 * Utility function which assigns the correct indices of the metadata streams.
	 */
	private void getStreamIndices() throws DotNetContentNotFoundException {
		for(int index=0;index<cliMetadataRootHeader.getStreamHeaders().length;index++) {
			String name = cliMetadataRootHeader.getStreamHeaders()[index].getName();
			
			if(name.equals(STREAM_SIGNATURE_METADATA) || name.equals(STREAM_SIGNATURE_METADATA_ALTERNATIVE)) {
				indexOfMetadataStream = index;
			} else if(name.equals(STREAM_SIGNATURE_STRINGS)) {
				indexOfStringsStream = index;
			} else if(name.equals(STREAM_SIGNATURE_US)) {
				indexOfUserStringStream = index;
			} else if(name.equals(STREAM_SIGNATURE_GUID)) {
				indexOfGuidStream = index;
			} else if(name.equals(STREAM_SIGNATURE_BLOB)) {
				indexOfBlobStream = index;
			} else {
				//remember the unknown streams
				additionalStreamsSetup.put(name, index);
				logger.severe("Unknown stream (" + name + ") detected!");
			}
		}
		
		//Note: The user string stream and blob stream are not required!
		if(indexOfMetadataStream==-1 && indexOfStringsStream==-1 &&
				indexOfUserStringStream==-1 && indexOfGuidStream==-1 &&
				indexOfBlobStream==-1) {
			throw new DotNetContentNotFoundException("All .net streams are missing.");
		}
	}

	private void processCoffPeData(byte[] buffer)
			throws UnexpectedHeaderDataException, CoffPeDataNotFoundException, DotNetContentNotFoundException {
		
		 int byteOffset = 0;
		 
		//read the standard DOS header for EXE files
		dosHeader = new DOSHeader();
		dosHeader.read(buffer, byteOffset);
				
		if(dosHeader.getFileAddrOfCOFFHeader()>buffer.length-COFFPEHeader.STATIC_HEADER_SIZE) {
			throw new CoffPeDataNotFoundException("No COFF header found.");
		}
		
		assert(dosHeader.getFileAddrOfCOFFHeader() < ByteReader.INT32_MAX_VAL);
		byteOffset = (int) dosHeader.getFileAddrOfCOFFHeader();
		
		//read the common object file format header
		coffPeHeader = new COFFPEHeader();
		byteOffset += coffPeHeader.read(buffer, byteOffset);
		
		//read the portable executable header
		peOptionalHeader = new PEOptionalHeader();
		byteOffset += peOptionalHeader.read(buffer, byteOffset);
		
		//NOTE: Image base is not involved. 
		
		//read all entries of the data directory (16 entries)
		peDataDirectories = new PEDataDirectories();
		byteOffset += peDataDirectories.read(buffer, byteOffset);
		
		//NOTE: Import Table, Relocation Table, IAT, could be checked at this location
		
		//read the headers of all available sections in the file
		//default sections: .text .rsrc .reloc (others possible)
		numberOfSections = coffPeHeader.getNumberOfSections();
		
		for(int i=0;i<numberOfSections; i++) {
			PESectionHeader header = new PESectionHeader();			
			byteOffset += header.read(buffer, byteOffset);
			
			peSectionHeaders.add(header);
		}
	}

	/**
	 * Utility function which loads a specified file from the hard drive. The first
	 * call to this method loads only {@code PARTIAL_LOADING_SIZE} Bytes from the file
	 * and sets the member {@code partialLoaded} to {@code true}. The second call
	 * loads the whole file and resets {@code partialLoaded} to {@code false}. If the
	 * file is smaller than {@code PARTIAL_LOADING_SIZE} the first call results to a
	 * full load!
	 * @param pathToFile The path specification of the file to load.
	 * @return The {@code byte} buffer containing the full or a part of the file.
	 * @throws FileNotFoundException If the specified file cannot be found.
	 * @throws IOException If the specified file is not accessible (e.g. access restrictions).
	 * @throws SizeMismatchException If the file is large than 2GB ({@link Integer#MAX_VALUE}).
	 */
	private byte[] getFileBuffer(String pathToFile)
			throws FileNotFoundException, IOException, SizeMismatchException {
		//open a new file stream
		RandomAccessFile file = new RandomAccessFile(pathToFile, "r");
		long length = file.length();
		
		//only accept files within the range [0,MAX_INT],
		//because the array dimension is limited to that range
		if(length<0 || length>ByteReader.INT32_MAX_VAL) {
			throw new SizeMismatchException("Size limit of " + ByteReader.INT32_MAX_VAL + "bytes exceeded");
		}

		//check the buffer length and perform a partial loading if possible
		int bufferLength = (int) length;		
		if(!partialLoaded && bufferLength>PARTIAL_LOADING_SIZE) {
			bufferLength = PARTIAL_LOADING_SIZE;
			partialLoaded = true;
		}
		
		//read the file
		byte [] buffer = new byte[bufferLength];
		file.read(buffer, 0, bufferLength);
		file.close();
		
		//just for debug purpose...
		//System.out.println(ArrayUtils.formatAsHexTable(buffer));
		
		//return the created buffer
		return buffer;
	}
	
	/**
	 * Loads a reflected assembly. If {@link at.pollaknet.api.facile.FacileReflector#loadAssembly()}
	 * has already been called, the method returns a reference to the already
	 * loaded assembly instead of processing the data once more.
	 * 
	 * @return A loaded assembly accessible by the {@link at.pollaknet.api.facile.symtab.symbols.scopes.Assembly}
	 * interface.
	 * 
	 * @throws DotNetContentNotFoundException 
	 */
	public Assembly loadAssembly() throws DotNetContentNotFoundException {	
		
		if(assembly==null) {
			//the metadata stream is required
			if(metadataStream==null) {
				return null;
			}
			
			//perform log
			logger.log(Level.INFO, "Starting meta object processing.");
			
			//set an alternative name (required if there is no string stream)
			String alternativeModuleName;
			//get the systems separator
			String seperator = System.getProperty("file.separator");
			if(seperator==null) seperator = "/";
			
			int slashPos = pathToAssembly.lastIndexOf(seperator)+1;
			if(slashPos>1) {
				alternativeModuleName = pathToAssembly.substring(slashPos);
			} else {
				alternativeModuleName = pathToAssembly;
			}
		
			
			//create the metamodel (out of the given streams)
			metaModel = new MetadataModel(assemblyHasIlSection, alternativeModuleName,
					metadataStream, stringsStream, userStringStream, guidStream, blobStream);
			logger.log(Level.INFO, "Finished meta object processing.");	  
			
			//verify data
			if(metaModel.assembly.length<1) {
				if(metaModel.module.length<1) {
					throw new DotNetContentNotFoundException("No assembly entry and no module entry has been found!");
				}
				assert(metaModel.module[0]!=null);
				
				//create a stub entry for the module (a assembly entry is not mandatory)
				assembly = new AssemblyEntry();
				assembly.setName("Stub [" + metaModel.module[0].getName() + "]");
				metaModel.assembly = new AssemblyEntry[1];
				metaModel.assembly[0] = assembly;
			} else {
				assembly = metaModel.assembly[0];
			}
			
			//perform log
			logger.log(Level.INFO, "Try to access debug information.");
			
			//try to open the assigned pdb file
			PdbReader pdbReader = openPdb();
			logger.log(Level.INFO, "Starting symbol table processing.");
			
			//initialize the symbol table
			SymbolTable symbolTable = new SymbolTable(cliHeader,
					metaModel, blobStream, codeContainer, pdbReader, pathToAssembly);
			
			//build the symbol table (link metadata items)
			symbolTable.build();
			
			//close the pdb if opened
			if(pdbReader != null) {
				pdbReader.close();
				pdbReader=null;
				logger.log(Level.INFO, "PDB has been closed.");
				debugDataAvailable  = true;
			}
			
			//completed - perfom log
			logger.log(Level.INFO, "Finished symbol table processing.");
		}
		
		//assembly fully loaded
		return assembly;
	}

	/**
	 * Open the pdb specified in {@code pathToPdb}.
	 * @return A {@link at.pollaknet.api.facile.dia.PdbReader} object instance
	 * or {@code null} if no debug information is accessible.
	 */
	private PdbReader openPdb() {
		
		//do nothing if the path is explicitly null
		if(pathToPdb==null) {
			logger.log(Level.INFO, "No PDB specified.");
			return null;
		}
		
		PdbReader pdbReader = null;
		
		try {
			//first use the specified file (could be the .exe too)
			pdbReader = new PdbReader(pathToPdb);
			logger.log(Level.INFO, "Successfully opened PDB.");
		} catch (FileNotFoundException e) {
			
			//replace the .exe extension with .pdb if possible
			if(pathToAssembly.equals(pathToPdb)) {
				
				int pos = pathToAssembly.lastIndexOf('.');
				assert(pos>0);
				String alternativePath = pathToAssembly.substring(0,pos) + ".pdb";
				
				logger.log(Level.INFO, "PDB not found. Using " + alternativePath + " as alternative.");
				
				//try the alternative file name
				try {
					pdbReader = new PdbReader(alternativePath);
					logger.log(Level.INFO, "Successfully opened alternative PDB.");
				} catch (FileNotFoundException ex) {
					logger.log(Level.WARNING, "PDB not opened: " + ex.getMessage());
				} catch (NativeImplementationException ex) {
					logger.log(Level.WARNING, "PDB not opened: " + ex.getMessage());
				} catch (UnexpectedPdbContent ex) {
					logger.log(Level.WARNING, "PDB not opened: " + ex.getMessage());
				}
				
			} else {
				logger.log(Level.WARNING, "PDB not opened: " + e.getMessage());
			}
			
		} catch (NativeImplementationException e) {
			logger.log(Level.WARNING, "PDB not opened: " + e.getMessage());
		} catch (UnexpectedPdbContent e) {
			logger.log(Level.WARNING, "PDB not opened: " + e.getMessage());
		}
		
		//return the (maybe) loaded pdb file via the pdb reader
		return pdbReader;
	}
	
	/**
	 * The path to the assembly on the local system - if set.
	 * @return The path to the assembly.
	 */
	public String getPathToAssemby() {
		return pathToAssembly;
	}

	/**
	 * The path to the program database (debug information)
	 * on the local system, which can be equal to the path
	 * of the assembly.
	 * @return The path to the pdb.
	 */
	public String getPathToPdb() {
		return pathToPdb;
	}
	
	/**
	 * Returns the CLI Header (even called CLR Header) which points
	 * to the metadata root header .
	 * @return The {@link at.pollaknet.api.facile.header.cli.CliHeader}
	 * of the assembly.
	 */
	public CliHeader getCliHeader() {
		return cliHeader;
	}

	/**
	 * Returns the metadata root header.
	 * @return The root header information as
	 * {@link at.pollaknet.api.facile.header.cli.CliMetadataRootHeader}
	 * instance.
	 */
	public CliMetadataRootHeader getCliMetadataRootHeader() {
		return cliMetadataRootHeader;
	}

	/**
	 * Returns the Metadata stream of the assembly.
	 * @return The Metadata stream as
	 * {@link at.pollaknet.api.facile.header.cli.stream.MetadataStream}
	 * instance.
	 */
	public MetadataStream getMetadataStream() {
		return metadataStream;
	}

	/**
	 * Returns the raw Strings stream (heap) of the assembly.
	 * @return The Strings stream as
	 * {@link at.pollaknet.api.facile.header.cli.stream.StringsStream}
	 * instance.
	 */
	public StringsStream getStringsStream() {
		return stringsStream;
	}

	/**
	 * Returns the raw US stream (heap) of the assembly.
	 * @return The US stream as
	 * {@link at.pollaknet.api.facile.header.cli.stream.UserStringStream}
	 * instance.
	 */
	public UserStringStream getUserStringStream() {
		return userStringStream;
	}

	/**
	 * Returns the raw GUID stream of the assembly.
	 * @return The GUID stream as
	 * {@link at.pollaknet.api.facile.header.cli.stream.GuidStream}
	 * instance.
	 */
	public GuidStream getGuidStream() {
		return guidStream;
	}

	/**
	 * Returns the raw BLOB stream (heap) of the assembly.
	 * @return The BLOB stream as
	 * {@link at.pollaknet.api.facile.header.cli.stream.BlobStream}
	 * instance.
	 */
	public BlobStream getBlobStream() {
		return blobStream;
	}

	/**
	 * Returns the additional detected streams.
	 * @return A List of detected, but unknown streams.
	 */
	public List<UnknownStream> getAdditionalStreams() {
		return additionalStreams;
	}
	
	/**
	 * Returns the size of the Metadata stream.
	 * @return The size in bytes.
	 */
	public int getSizeOfMetadataStream() {
		return sizeOfMetadataStream;
	}

	/**
	 * Returns the size of the Strings stream (heap).
	 * @return The size in bytes.
	 */
	public int getSizeOfStringsStream() {
		return sizeOfStringsStream;
	}

	/**
	 * Returns the size of the US stream (heap).
	 * @return The size in bytes.
	 */
	public int getSizeOfUserStringStream() {
		return sizeOfUserStringStream;
	}

	/**
	 * Returns the size of the GUID stream.
	 * @return The size in bytes.
	 */
	public int getSizeOfGuidStream() {
		return sizeOfGuidStream;
	}

	/**
	 * Returns the size of the BLOB stream (heap).
	 * @return The size in bytes.
	 */
	public int getSizeOfBlobStream() {
		return sizeOfBlobStream;
	}

	/**
	 * Returns the metadata model, which is a high-level
	 * representation of the metadata stream.
	 * @return The {@link at.pollaknet.api.facile.metamodel.MetadataModel}
	 * object of the assembly.
	 */
	public MetadataModel getMetaModel() {
		return metaModel;
	}

	/**
	 * Returns the reference to the log handler of the API.
	 * @return The facile log handler {@link at.pollaknet.api.facile.FacileLogHandler}.
	 */
	public static FacileLogHandler getFacileLogHandler() {
		return facileLogHandler;
	}
	
	/**
	 * Return the assembly if it has been loaded already.
	 * @return An {@link at.pollaknet.api.facile.symtab.symbols.scopes.Assembly}
	 * instance or {@code null}.
	 */
	public Assembly getAssembly() {
		return assembly;
	}
	
	/**
	 * The DOS header of the file.
	 * @return The DOS header as
	 * {@link at.pollaknet.api.facile.header.coffpe.DOSHeader}
	 * object instance.
	 */
	public DOSHeader getDosHeader() {
		return dosHeader;
	}

	/**
	 * The COFF/PE Header of the file.
	 * @return The COFF/PE header as
	 * {@link at.pollaknet.api.facile.header.coffpe.COFFPEHeader}
	 * object instance.
	 */
	public COFFPEHeader getCoffPeHeader() {
		return coffPeHeader;
	}

	/**
	 * Returns the optional PE header.
	 * @return The optional PE header 
	 * {@link at.pollaknet.api.facile.header.coffpe.PEOptionalHeader}.
	 */
	public PEOptionalHeader getPeOptionalHeader() {
		return peOptionalHeader;
	}

	/**
	 * Returns the PE data directories.
	 * @return All directories inside the PE as
	 * {@link at.pollaknet.api.facile.header.coffpe.PEDataDirectories}.
	 */
	public PEDataDirectories getPeDataDirectories() {
		return peDataDirectories;
	}

	/**
	 * Returns the section headers of the PE file sections.
	 * @return An array of {@link at.pollaknet.api.facile.header.coffpe.PESectionHeader}.
	 */
	public PESectionHeader [] getPeSectionHeaders() {
		if(peSectionHeaders==null || peSectionHeaders.size()==0) return new PESectionHeader [0];
		return peSectionHeaders.toArray(new PESectionHeader [0]);
	}
	
	/**
	 * Returns true if line number information is present
	 * @return {@code true} if the debug data (line numbers) is available.
	 */
	public boolean isDebugDataAvailable() {
		return debugDataAvailable;
	}

}

package at.pollaknet.api.facile.header.coffpe;

import at.pollaknet.api.facile.exception.CoffPeDataNotFoundException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ByteReader;


public class COFFPEHeader implements IDataHeader  {

	public final static int PE_SIGNATURE = 0x4550;
	public final static int STATIC_HEADER_SIZE = 24;

	private long signature;
	private int targetMachineType;
	private int numberOfSections;
	private long timeDateStamp;
	private long pointerToSymbolTable;
	private long numberOfSymbols;
	private int sizeOfOptionalHeader;
	private int characteristics;

	private int headerSize;

	
	public final static int TARGET_MACHINE_UNKNOWN		= 0x0; 		//The contents of this field are assumed to be applicable to any machine type
	public final static int TARGET_MACHINE_AM33 		= 0x1d3;	//Matsushita AM33
	public final static int TARGET_MACHINE_AMD64		= 0x8664;	//x64
	public final static int TARGET_MACHINE_ARM			= 0x1c0;	//ARM little endian
	public final static int TARGET_MACHINE_EBC			= 0xebc;	//EFI byte code
	public final static int TARGET_MACHINE_I386			= 0x14c;	//Intel 386 or later processors and compatible processors
	public final static int TARGET_MACHINE_IA64			= 0x200;	//Intel Itanium processor family
	public final static int TARGET_MACHINE_M32R			= 0x9041;	//Mitsubishi M32R little endian
	public final static int TARGET_MACHINE_MIPS16		= 0x266;	//MIPS16
	public final static int TARGET_MACHINE_MIPSFPU		= 0x366;	//MIPS with FPU
	public final static int TARGET_MACHINE_MIPSFPU16	= 0x466;	//MIPS16 with FPU
	public final static int TARGET_MACHINE_POWERPC		= 0x1f0;	//Power PC little endian
	public final static int TARGET_MACHINE_POWERPCFP	= 0x1f1;	//Power PC with floating point support
	public final static int TARGET_MACHINE_R4000		= 0x166;	//MIPS little endian
	public final static int TARGET_MACHINE_SH3			= 0x1a2;	//Hitachi SH3
	public final static int TARGET_MACHINE_SH3DSP		= 0x1a3;	//Hitachi SH3 DSP
	public final static int TARGET_MACHINE_SH4			= 0x1a6;	//Hitachi SH4
	public final static int TARGET_MACHINE_SH5			= 0x1a8;	//Hitachi SH5
	public final static int TARGET_MACHINE_THUMB		= 0x1c2;	//Thumb
	public final static int TARGET_MACHINE_WCEMIPSV2	= 0x169;	//MIPS little-endian WCE v2

	
	public final static int CHARACTERISTICS_FLAG_RELOCS_STRIPPED		= 0x0001;	//Image only, Windows CE, and Microsoft Windows NT and later.
	public final static int CHARACTERISTICS_FLAG_EXECUTABLE_IMAGE		= 0x0002;	//Image only. This indicates that the image file is valid and can be run.
	public final static int CHARACTERISTICS_FLAG_LINE_NUMS_STRIPPED		= 0x0004;	//COFF line numbers have been removed. This flag is deprecated and should be zero.
	public final static int CHARACTERISTICS_FLAG_LOCAL_SYMS_STRIPPED	= 0x0008;	//COFF symbol table entries for local symbols have been removed (deprecated).
	public final static int CHARACTERISTICS_FLAG_AGGRESSIVE_WS_TRIM		= 0x0010;	//Obsolete. Aggressively trim working set (deprecated).
	public final static int CHARACTERISTICS_FLAG_LARGE_ADDRESS_AWARE	= 0x0020;	//Application can handle > 2 GB addresses.
	public final static int CHARACTERISTICS_FLAG_RESERVED				= 0x0040;	//This flag is reserved for future use.
	public final static int CHARACTERISTICS_FLAG_BYTES_REVERSED_LO		= 0x0080;	//Little endian (deprecated).
	public final static int CHARACTERISTICS_FLAG_32BIT_MACHINE			= 0x0100;	//Machine is based on a 32-bit-word architecture.
	public final static int CHARACTERISTICS_FLAG_DEBUG_STRIPPED			= 0x0200;	//Debugging information is removed from the image file.
	public final static int CHARACTERISTICS_FLAG_REMOVABLE_RUN_FROM_SWAP= 0x0400;	//If the image is on removable media, fully load it and copy it to the swap file.
	public final static int CHARACTERISTICS_FLAG_NET_RUN_FROM_SWAP		= 0x0800;	//If the image is on network media, fully load it and copy it to the swap file.
	public final static int CHARACTERISTICS_FLAG_SYSTEM					= 0x1000;	//The image file is a system file, not a user program.
	public final static int CHARACTERISTICS_FLAG_DLL					= 0x2000;	//The image file is a dynamic-link library (DLL).
	public final static int CHARACTERISTICS_FLAG_UP_SYSTEM_ONLY			= 0x4000;	//The file should be run only on a uniprocessor machine.
	public final static int CHARACTERISTICS_FLAG_BYTES_REVERSED_HI		= 0x8000;	//Big endian (deprecated).

	
	public int read (byte [] data, int offset) throws CoffPeDataNotFoundException {
		
		headerSize = offset;
		
		signature = ByteReader.getUInt32(data, offset);
		
		if(signature!=PE_SIGNATURE)
			throw new CoffPeDataNotFoundException("Invalid \"PE\" signature."); //$NON-NLS-1$
		
		offset += 4;
		
		targetMachineType = ByteReader.getUInt16(data, offset);		offset +=2;
		numberOfSections = ByteReader.getUInt16(data, offset);		offset +=2;
		timeDateStamp = ByteReader.getUInt32(data, offset);			offset +=4;
		
		pointerToSymbolTable = ByteReader.getUInt32(data, offset);	offset +=4;
		numberOfSymbols = ByteReader.getUInt32(data, offset);		offset +=4;
		
		sizeOfOptionalHeader = ByteReader.getUInt16(data, offset);	offset +=2;
		characteristics = ByteReader.getUInt16(data, offset);		offset +=2;
		
		headerSize = offset - headerSize;
		
		assert(headerSize == STATIC_HEADER_SIZE);
		
		return headerSize;
	}

	/**
	 * Get the static size of the COFF/PE header in bytes.
	 * @return Size in bytes.
	 */
	public int getSize() {
		return STATIC_HEADER_SIZE;
	}

	/**
	 * Get the number of available file sections.
	 * @return Number of file sections.
	 */
	public int getNumberOfSections() {
		return numberOfSections;
	}
	
	/**
	 * Checks if the file is a DLL or not.
	 * @return {@code true} if the file is a DLL. 
	 */
	public boolean isADll() {
		return ByteReader.testFlags(CHARACTERISTICS_FLAG_DLL, characteristics);
	}

	/**
	 * Get the time stamp, when the file was created. 
	 * @return Seconds since 01.01.1970 as {code long}.
	 */
	public long getTimeDateStamp() {
		return timeDateStamp;
	}

	/**
	 * Get the size of the {@link at.pollaknet.api.facile.header.coffpe.PEOptionalHeader}.
	 * @return Size in bytes.
	 */
	public int getSizeOfOptionalHeader() {
		return sizeOfOptionalHeader;
	}


	/**
	 * Get the PE Header signature (only 0x4550 is valid).
	 * @return The signature as number.
	 */
	public long getSignature() {
		return signature;
	}


	/**
	 * Get the target machine of the PE file.
	 * @return The machine type (one of the defined TARGET_MACHINE_* constants).
	 */
	public int getTargetMachineType() {
		return targetMachineType;
	}

	/**
	 * Get the address of the symbol table inside the file (deprecated PE feature).
	 * @return An address, pointing to the symbol table.
	 */
	public long getPointerToSymbolTable() {
		return pointerToSymbolTable;
	}


	/**
	 * Get the number of defined symbols in the symbol table (deprecated PE feature).
	 * @return The number of defined debug symbols.
	 */
	public long getNumberOfSymbols() {
		return numberOfSymbols;
	}

	/**
	 * Get the define characteristics flags of the file.
	 * @return Characteristics flags (flags are defined as CHARACTERISTICS_FLAG_*).
	 */
	public int getCharacteristicsFlags() {
		return characteristics;
	}

	public String toString() {

		return "COFF/PE Header" +
				String.format("\n  Signature: ..................0x%08x", signature) +
				String.format("\n  Target Machine Type: ........%010d", targetMachineType) +
				String.format("\n  Number of File Sections: ....%010d", numberOfSections) +
				String.format("\n  Time Date Stamp: ............0x%08x", timeDateStamp) +
				String.format("\n  Pointer to Symbol Table: ....0x%08x", pointerToSymbolTable) +
				String.format("\n  Number of Symbols: ..........%010d", numberOfSymbols) +
				String.format("\n  Size of Optional Header: ....%010d", sizeOfOptionalHeader) +
				String.format("\n  Characteristics: ............0x%08x", characteristics);
	}
}

package at.pollaknet.api.facile.header.coffpe;

import at.pollaknet.api.facile.exception.SizeExceededException;
import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ByteReader;


public class PEOptionalHeader implements IDataHeader  {

	//private static final int MIN_HEADER_SIZE;
	
	public static final int MAGIC_NUMBER_PE32_PLUS 		= 0x020b;
	public static final int MAGIC_NUMBER_PE32 			= 0x010b;
	
	public static final int SUBSYSTEM_UNKNOWN	  				= 0; //An unknown subsystem
	public static final int SUBSYSTEM_NATIVE	  				= 1; //Device drivers and native Windows processes
	public static final int SUBSYSTEM_WINDOWS_GUI	 			= 2; //The Windows graphical user interface (GUI) subsystem
	public static final int SUBSYSTEM_WINDOWS_CUI	  			= 3; //The Windows character subsystem
	public static final int SUBSYSTEM_POSIX_CUI	  				= 7; //The Posix character subsystem
	public static final int SUBSYSTEM_WINDOWS_CE_GUI	 		= 9; //Windows CE
	public static final int SUBSYSTEM_EFI_APPLICATION			= 10; //An Extensible Firmware Interface (EFI) application
	public static final int SUBSYSTEM_EFI_BOOT_SERVICE_DRIVER 	= 11; //An EFI driver with boot services
	public static final int SUBSYSTEM_EFI_RUNTIME_DRIVER		= 12; //An EFI driver with run-time services
	public static final int SUBSYSTEM_EFI_ROM					= 13; //An EFI ROM image
	public static final int SUBSYSTEM_XBOX						= 14; //XBOX

	public static final int DLL_CHARACTERISTICS_FLAGS_DYNAMIC_BASE			= 0x0040; //DLL can be relocated at load time.
	public static final int DLL_CHARACTERISTICS_FLAGS_FORCE_INTEGRITY		= 0x0080; //Code Integrity checks are enforced.
	public static final int DLL_CHARACTERISTICS_FLAGS_NX_COMPATIBLE			= 0x0100; //Image is NX compatible.
	public static final int DLL_CHARACTERISTICS_FLAGS_NO_ISOLATION 			= 0x0200; //Isolation aware, but do not isolate the image.
	public static final int DLL_CHARACTERISTICS_FLAGS_NO_SEH				= 0x0400; //Does not use structured exception (SE) handling.
	public static final int DLL_CHARACTERISTICS_FLAGS_NO_BIND				= 0x0800; //Do not bind the image.
	public static final int DLL_CHARACTERISTICS_FLAGS_WDM_DRIVER			= 0x2000; //A WDM driver.
	public static final int DLL_CHARACTERISTICS_FLAGS_TERMINAL_SERVER_AWARE	= 0x8000; //Terminal Server aware.

	
	//common header fields (32 and 64 bit)
	private int magicVersionNumber;
	private int majorLinkerVersion;
	private int minorLinkerVersion;
	private long sizeOfCode;
	private long sizeOfInitializedData;
	private long sizeOfUninitializedData;
	private long addrOfEntryPoint;
	private long baseOfCode;
	
	private long baseOfData; //image base low for 64 bit
	private long imageBase; //image base high for 64 bit
	
	private long sectionAlignment;
	private long fileAlignment;
	private int majorOSVersion;
	private int minorOSVersion;
	private int majorImageVersion;
	private int minorImageVersion;
	private int majorSubsystemVersion;
	private int minorSubsystemVersion;
	private long win32VersionValue;
	private long sizeOfImage;
	private long sizeOfHeaders;
	private long checksum;
	private int subsystem;
	private int DLLCharacteristics;
		
	//32bit specific
	private long sizeOfStackReserve;
	private long sizeOfStackCommit;
	private long sizeOfHeapReserve;
	private long sizeOfHeapCommit;
	
	//64bit specific
	private long sizeOfStackReserveHigh;
	private long sizeOfStackCommitHigh;
	private long sizeOfHeapReserveHigh;
	private long sizeOfHeapCommitHigh;
	
	//common
	private long loaderFlags;
	private long numberOfDataDirectoryEntries;
	
	private int headerSize = 0;

	public int read (byte [] data, int offset) throws UnexpectedHeaderDataException {
		
		headerSize = offset;
		
		magicVersionNumber = ByteReader.getUInt16(data, offset);
		
		if(magicVersionNumber!=MAGIC_NUMBER_PE32 && magicVersionNumber!=MAGIC_NUMBER_PE32_PLUS)
			throw new UnexpectedHeaderDataException("Magic number check failed: " +  //$NON-NLS-1$ 
					magicVersionNumber + "(expected " + MAGIC_NUMBER_PE32 + " or " + //$NON-NLS-1$ //$NON-NLS-2$
					MAGIC_NUMBER_PE32_PLUS + ")"); //$NON-NLS-1$
		
		offset += 2;
		
		majorLinkerVersion = ByteReader.getUInt8(data, offset);				offset++;
		minorLinkerVersion = ByteReader.getUInt8(data, offset);				offset++;
		sizeOfCode = ByteReader.getUInt32(data, offset);					offset +=4;
		sizeOfInitializedData = ByteReader.getUInt32(data, offset);			offset +=4;
		sizeOfUninitializedData = ByteReader.getUInt32(data, offset);		offset +=4;
		addrOfEntryPoint = ByteReader.getUInt32(data, offset);				offset +=4;
		baseOfCode = ByteReader.getUInt32(data, offset);					offset +=4;

		baseOfData = ByteReader.getUInt32(data, offset);					offset +=4;
		imageBase = ByteReader.getUInt32(data, offset);						offset +=4;

		sectionAlignment = ByteReader.getUInt32(data, offset);				offset +=4;
		fileAlignment = ByteReader.getUInt32(data, offset);					offset +=4;
		majorOSVersion = ByteReader.getUInt16(data, offset);				offset +=2;
		minorOSVersion = ByteReader.getUInt16(data, offset);				offset +=2;
		majorImageVersion = ByteReader.getUInt16(data, offset);				offset +=2;
		minorImageVersion = ByteReader.getUInt16(data, offset);				offset +=2;
		majorSubsystemVersion = ByteReader.getUInt16(data, offset);			offset +=2;
		minorSubsystemVersion = ByteReader.getUInt16(data, offset);			offset +=2;		
		win32VersionValue = ByteReader.getUInt32(data, offset);				offset +=4;
		sizeOfImage = ByteReader.getUInt32(data, offset);					offset +=4;
		sizeOfHeaders = ByteReader.getUInt32(data, offset);					offset +=4;
		checksum = ByteReader.getUInt32(data, offset);						offset +=4;
		subsystem = ByteReader.getUInt16(data, offset);						offset +=2;
		DLLCharacteristics = ByteReader.getUInt16(data, offset);			offset +=2;

		if(magicVersionNumber==MAGIC_NUMBER_PE32) {
			//32bit
			sizeOfStackReserve = ByteReader.getUInt32(data, offset);		offset +=4;
			sizeOfStackCommit = ByteReader.getUInt32(data, offset);			offset +=4;
			sizeOfHeapReserve = ByteReader.getUInt32(data, offset);			offset +=4;
			sizeOfHeapCommit = ByteReader.getUInt32(data, offset);			offset +=4;
		} else {
			//64bit
			sizeOfStackReserve = ByteReader.getUInt32(data, offset);		offset +=4;
			sizeOfStackReserveHigh = ByteReader.getUInt32(data, offset);	offset +=4;
			sizeOfStackCommit = ByteReader.getUInt32(data, offset);			offset +=4;
			sizeOfStackCommitHigh = ByteReader.getUInt32(data, offset);		offset +=4;
			sizeOfStackReserve = ByteReader.getUInt32(data, offset);		offset +=4;
			sizeOfHeapReserveHigh = ByteReader.getUInt32(data, offset);		offset +=4;
			sizeOfHeapCommit = ByteReader.getUInt32(data, offset);			offset +=4;
			sizeOfHeapCommitHigh = ByteReader.getUInt32(data, offset);		offset +=4;
		}
		
		loaderFlags = ByteReader.getUInt32(data, offset);					offset +=4;
		numberOfDataDirectoryEntries = ByteReader.getUInt32(data, offset);	offset +=4;
		
		headerSize = offset - headerSize;
		
		return headerSize;
	}

	/**
	 * Get the size of the header.
	 * @return Size in bytes.
	 */
	public int getSize() {
		return headerSize;
	}

	/**
	 * Get the image base for virtual addresses. In case of a PE32+ 
	 * file (MagicVersionNumber is equal to the MAGIC_NUMBER_PE32_PLUS
	 * constant) this is only the 4 byte high part of the whole 8 byte image
	 * base. The 4 byte low part of the image base is accessible via the
	 * {@link at.pollaknet.api.facile.header.coffpe.PEOptionalHeader#getBaseOfData()}
	 * method.
	 * @return The 4 byte image base as {@code long}.
	 */
	public long getImageBase() {
		return imageBase;
	}

	/**
	 * Get the address of the data file section. In case of a PE32+ 
	 * file (MagicVersionNumber is equal to the MAGIC_NUMBER_PE32_PLUS
	 * constant) this is the 4 byte low part of the whole 8 byte image
	 * base. The 4 byte high part of the image base is accessible via the
	 * {@link at.pollaknet.api.facile.header.coffpe.PEOptionalHeader#getImageBase()}
	 * method.
	 * @return The 4 byte base of data as {@code long}.
	 */
	public long getBaseOfData() {
		return baseOfData;
	}
	
	/**
	 * Get the address of the executable file section.
	 * @return The 4 byte address as {@code long}.
	 */
	public long getBaseOfCode() {
		return baseOfCode;
	}


	/**
	 * Get the magic version number of the header, which has to be
	 * MAGIC_NUMBER_PE32_PLUS or MAGIC_NUMBER_PE32.
	 * @return The magic version number of the header.
	 */
	public int getMagicVersionNumber() {
		return magicVersionNumber;
	}

	/**
	 * Get the major version number of the used linker.
	 * @return Major linker version number.
	 */
	public int getMajorLinkerVersion() {
		return majorLinkerVersion;
	}

	/**
	 * Get the minor version number of the used linker.
	 * @return Minor linker version number.
	 */
	public int getMinorLinkerVersion() {
		return minorLinkerVersion;
	}

	/**
	 * Get the size of the executable file section.
	 * @return Size in bytes.
	 */
	public long getSizeOfCode() {
		return sizeOfCode;
	}

	/**
	 * Get the size of all initialized data, ready to be loaded into memory
	 * (sum over all file sections).
	 * @return Size in bytes.
	 */
	public long getSizeOfInitializedData() {
		return sizeOfInitializedData;
	}

	/**
	 * Get the size of all uninitialized data (sum over all file sections).
	 * @return Size in bytes
	 */
	public long getSizeOfUninitializedData() {
		return sizeOfUninitializedData;
	}

	/**
	 * Get the file address of the native program entry point.
	 * @return The (unsigned 4 byte) physical address as {@code long}. 
	 */
	public long getAddrOfEntryPoint() {
		return addrOfEntryPoint;
	}

	/**
	 * Get the alignment of the file sections (the virtual size
	 * of each file section is a multiple of that alignment). 
	 * @return The alignment as number of bytes.
	 */
	public long getSectionAlignment() {
		return sectionAlignment;
	}

	/**
	 * The alignment of the PE file.
	 * @return The alignment as number of bytes.
	 */
	public long getFileAlignment() {
		return fileAlignment;
	}

	/**
	 * Get the major version number of target OS.
	 * @return The (unsigned 2 byte) major version number as {@code int}.
	 */
	public int getMajorOSVersion() {
		return majorOSVersion;
	}

	/**
	 * Get the minor version number of target OS.
	 * @return The (unsigned 2 byte) minor version number as {@code int}.
	 */
	public int getMinorOSVersion() {
		return minorOSVersion;
	}

	/**
	 * Get the major version number of the PE image.
	 * @return The (unsigned 2 byte) major version number as {@code int}.
	 */
	public int getMajorImageVersion() {
		return majorImageVersion;
	}

	/**
	 * Get the minor version number of the PE image.
	 * @return The (unsigned 2 byte) minor version number as {@code int}.
	 */
	public int getMinorImageVersion() {
		return minorImageVersion;
	}

	/**
	 * Get the major version number of the runtime environment.
	 * @return The (unsigned 2 byte) major version number as {@code int}.
	 */
	public int getMajorSubsystemVersion() {
		return majorSubsystemVersion;
	}

	/**
	 * Get the minor version number of the runtime environment.
	 * @return The (unsigned 2 byte) minor version number as {@code int}.
	 */
	public int getMinorSubsystemVersion() {
		return minorSubsystemVersion;
	}


	/**
	 * Get the version value of the target windows platform (reserved, never used).
	 * @return The win32 version value, which should be {@code 0}.
	 */
	public long getWin32VersionValue() {
		return win32VersionValue;
	}

	/**
	 * Get the size of the whole PE image.
	 * @return Size in bytes.
	 */
	public long getSizeOfImage() {
		return sizeOfImage;
	}

	/**
	 * Get the size of all headers inside the PE file.
	 * @return Size in bytes.
	 */
	public long getSizeOfHeaders() {
		return sizeOfHeaders;
	}

	/**
	 * Get the CRC16 or CRC32 checksum (unused in .Net assemblies).
	 * @return The (unsigned 4 byte) checksum as {@code long}.
	 */
	public long getChecksum() {
		return checksum;
	}

	/**
	 * Get characteristics of the subsystem (only SUBSYSTEM_WINDOWS_GUI
	 * and SUBSYSTEM_WINDOWS_CUI permitted for .Net assemblies)
	 * @return Information about the subsystem (matches a SUBSYSTEM_* constant).
	 */
	public int getSubsystem() {
		return subsystem;
	}

	/**
	 * Get characteristics flags for DLL images.
	 * @return The flags as {@code int} (defined in this class: DLL_CHARACTERISTICS_FLAGS_*).
	 */
	public int getDLLCharacteristics() {
		return DLLCharacteristics;
	}


	public long getSizeOfStackReserve() throws SizeExceededException{
		if(magicVersionNumber==MAGIC_NUMBER_PE32)
			return sizeOfStackReserve;
		
		if(sizeOfStackReserveHigh>ByteReader.INT32_MAX_VAL)
			throw new SizeExceededException("Range of long exceeded.");
		
		return (sizeOfStackReserveHigh<<32) | sizeOfStackReserve;
	}


	public long getSizeOfStackCommit() throws SizeExceededException{
		if(magicVersionNumber==MAGIC_NUMBER_PE32)
			return sizeOfStackCommit;
		
		if(sizeOfStackCommitHigh>ByteReader.INT32_MAX_VAL)
			throw new SizeExceededException("Range of long exceeded.");
		
		return (sizeOfStackCommitHigh<<32) | sizeOfStackCommit;
	}


	public long getSizeOfHeapReserve() throws SizeExceededException{
		if(magicVersionNumber==MAGIC_NUMBER_PE32)
			return sizeOfHeapReserve;
		
		if(sizeOfHeapReserveHigh>ByteReader.INT32_MAX_VAL)
			throw new SizeExceededException("Range of long exceeded.");
		
		return (sizeOfHeapReserveHigh<<32) | sizeOfHeapReserve;
	}


	public long getSizeOfHeapCommit() throws SizeExceededException{
		if(magicVersionNumber==MAGIC_NUMBER_PE32)
			return sizeOfHeapCommit;
		
		if(sizeOfHeapCommitHigh>ByteReader.INT32_MAX_VAL)
			throw new SizeExceededException("Range of long exceeded.");
		
		return (sizeOfHeapCommitHigh<<32) | sizeOfHeapCommit;
	}


	/**
	 * Get the loader flags (reserved, never used).
	 * @return The loader flags (default is 0).
	 */
	public long getLoaderFlags() {
		return loaderFlags;
	}


	/**
	 * Get the number of available entries in the data directory (default is 16).
	 * @return The number of data directory entries.
	 */
	public long getNumberOfDataDirectoryEntries() {
		return numberOfDataDirectoryEntries;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(128);
		buffer.append("PE Optional Header");
		if(magicVersionNumber==MAGIC_NUMBER_PE32) {
			buffer.append(" 32 bit");
		} else {
			buffer.append(" 64 bit");
		}
		buffer.append(String.format("\n  Magic Version Number: .............0x%08x", magicVersionNumber));
		buffer.append(String.format("\n  Major Linker Version: .............%010d", majorLinkerVersion));
		buffer.append(String.format("\n  Minor Linker Version: .............%010d", minorLinkerVersion));
		buffer.append(String.format("\n  Size of Code: .....................%010d", sizeOfCode));
		buffer.append(String.format("\n  Size of Initialized Data: .........%010d", sizeOfInitializedData));
		buffer.append(String.format("\n  Size of Uninitialized Data: .......%010d", sizeOfUninitializedData));
		buffer.append(String.format("\n  Address of Entry Point: ...........0x%08x", addrOfEntryPoint));
		buffer.append(String.format("\n  Code Base: ........................0x%08x", baseOfCode));

		if(magicVersionNumber==MAGIC_NUMBER_PE32) {
			buffer.append(String.format("\n  Data Base: ........................0x%08x", baseOfData));
			buffer.append(String.format("\n  Image Base: .......................0x%08x", imageBase));
		} else {
			buffer.append(String.format("\n  Image Base: .......................0x%04x%04x", imageBase, baseOfData));
		}
		
		buffer.append(String.format("\n  Section Alignment: ................0x%08x", sectionAlignment));
		buffer.append(String.format("\n  File Alignment: ...................0x%08x", fileAlignment));
		buffer.append(String.format("\n  Major OS Version: .................%05d", majorOSVersion));
		buffer.append(String.format("\n  Minor OS Version: .................%05d", minorOSVersion));
		buffer.append(String.format("\n  Major Image Version: ..............%05d", majorImageVersion));
		buffer.append(String.format("\n  Minor Image Version: ..............%05d", minorImageVersion));
		buffer.append(String.format("\n  Major Subsystem Version: ..........%05d", majorSubsystemVersion));
		buffer.append(String.format("\n  Minor Subsystem Version: ..........%05d", minorSubsystemVersion));
		buffer.append(String.format("\n  Win32 Version: ....................%05d", win32VersionValue));
		buffer.append(String.format("\n  Size of Image: ....................%010d", sizeOfImage));
		buffer.append(String.format("\n  Size of Header: ...................%010d", sizeOfHeaders));
	
		buffer.append(String.format("\n  Checksum: .........................0x%08x", checksum));
		buffer.append(String.format("\n  Subsystem: ........................0x%04x", subsystem));
		buffer.append(String.format("\n  DLL Characteristics: ..............0x%04x", DLLCharacteristics));

		if(magicVersionNumber==MAGIC_NUMBER_PE32) {
			buffer.append(String.format("\n  Size Of Stack Reserve: ............0x%08x", sizeOfStackReserve));
			buffer.append(String.format("\n  Size Of Stack Commit: .............0x%08x", sizeOfStackCommit));
			buffer.append(String.format("\n  Size Of Heap Reserve: .............0x%08x", sizeOfHeapReserve));
			buffer.append(String.format("\n  Size Of Heap Commit: ..............0x%08x", sizeOfHeapCommit));
		} else {
			buffer.append(String.format("\n  Size Of Stack Reserve: ............0x%8x%08x", sizeOfStackReserveHigh, sizeOfStackReserve));
			buffer.append(String.format("\n  Size Of Stack Commit: .............0x%8x%08x", sizeOfStackCommitHigh, sizeOfStackCommit));
			buffer.append(String.format("\n  Size Of Heap Reserve: .............0x%8x%08x", sizeOfHeapReserveHigh, sizeOfHeapReserve));
			buffer.append(String.format("\n  Size Of Heap Commit: ..............0x%8x%08x", sizeOfHeapCommitHigh, sizeOfHeapCommit));
		}

		buffer.append(String.format("\n  Loader Flags: .....................0x%08x", loaderFlags));
		buffer.append(String.format("\n  Number of Data Dir. Entries: ......%05d", numberOfDataDirectoryEntries));

		return buffer.toString();
	}
}

package at.pollaknet.api.facile.header.coffpe;

import at.pollaknet.api.facile.exception.DotNetContentNotFoundException;
import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ByteReader;


public class PEDataDirectories implements IDataHeader  {

	private final static int STATIC_HEADER_SIZE = 128;
	
	private long exportDirectoryRVA;
	private long exportDirectorySize;
	private long importDirectoryRVA;
	private long importDirectorySize;
	private long resourceDirectoryRVA;
	private long resourceDirectorySize;
	private long exceptionDirectoryRVA;
	private long exceptionDirectorySize;

	private long certificateDirectoryRVA;
	private long certificateDirectorySize;
	private long baseRelocDirectoryRVA;
	private long baseRelocDirectorySize;
	private long debugDirectoryRVA;
	private long debugDirectorySize;
	private long copyrightDirectoryRVA;
	private long copyrightDirectorySize;

	private long pointerDirectoryRVA;
	private long pointerDirectorySize;
	private long threadLocalStorageDirectoryRVA;
	private long threadLocalStorageDirectorySize;
	private long loadConfigDirectoryRVA;
	private long loadConfigDirectorySize;
	private long boundImportDirectoryRVA;
	private long boundImportDirectorySize;

	private long importAddrTabDirectoryRVA;
	private long importAddrTabDirectorySize;
	private long delayLoadDirectoryRVA;
	private long delayLoadDirectorySize;
	private long commonLanguageRuntimeHeaderRVA;
	private long commonLanguageRuntimeHeaderSize;
	private long reservedDirectoryRVA;
	private long reservedDirectorySize;
	
	private int headerSize = 0;

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.header.AbstractByteReader#read(byte[], int)
	 */
	public int read (byte [] data, int offset) throws UnexpectedHeaderDataException, DotNetContentNotFoundException {
		
		headerSize = offset;

		exportDirectoryRVA = ByteReader.getUInt8(data, offset);					offset +=4;
		exportDirectorySize = ByteReader.getUInt8(data, offset);				offset +=4;

		importDirectoryRVA = ByteReader.getUInt32(data, offset);				offset +=4;
		importDirectorySize = ByteReader.getUInt32(data, offset);				offset +=4;
		
		resourceDirectoryRVA = ByteReader.getUInt32(data, offset);				offset +=4;
		resourceDirectorySize = ByteReader.getUInt32(data, offset);				offset +=4;
	
		exceptionDirectoryRVA = ByteReader.getUInt32(data, offset);				offset +=4;
		exceptionDirectorySize = ByteReader.getUInt32(data, offset);			offset +=4;

		certificateDirectoryRVA = ByteReader.getUInt32(data, offset);			offset +=4;
		certificateDirectorySize = ByteReader.getUInt32(data, offset);			offset +=4;
		
		baseRelocDirectoryRVA = ByteReader.getUInt32(data, offset);				offset +=4;
		baseRelocDirectorySize = ByteReader.getUInt16(data, offset);			offset +=4;
		
		debugDirectoryRVA = ByteReader.getUInt8(data, offset);					offset +=4;
		debugDirectorySize = ByteReader.getUInt8(data, offset);					offset +=4;
	
		copyrightDirectoryRVA = ByteReader.getUInt32(data, offset);				offset +=4;
		copyrightDirectorySize = ByteReader.getUInt32(data, offset);			offset +=4;
	
		pointerDirectoryRVA = ByteReader.getUInt32(data, offset);				offset +=4;
		pointerDirectorySize = ByteReader.getUInt32(data, offset);				offset +=4;

		threadLocalStorageDirectoryRVA = ByteReader.getUInt32(data, offset);	offset +=4;
		threadLocalStorageDirectorySize = ByteReader.getUInt32(data, offset);	offset +=4;
	
		loadConfigDirectoryRVA = ByteReader.getUInt32(data, offset);			offset +=4;
		loadConfigDirectorySize = ByteReader.getUInt32(data, offset);			offset +=4;
	
		boundImportDirectoryRVA = ByteReader.getUInt32(data, offset);			offset +=4;
		boundImportDirectorySize = ByteReader.getUInt16(data, offset);			offset +=4;

		importAddrTabDirectoryRVA = ByteReader.getUInt32(data, offset);			offset +=4;
		importAddrTabDirectorySize = ByteReader.getUInt32(data, offset);		offset +=4;
		
		delayLoadDirectoryRVA = ByteReader.getUInt32(data, offset);				offset +=4;
		delayLoadDirectorySize = ByteReader.getUInt32(data, offset);			offset +=4;

		commonLanguageRuntimeHeaderRVA = ByteReader.getUInt32(data, offset);	offset +=4;
		commonLanguageRuntimeHeaderSize = ByteReader.getUInt32(data, offset);	offset +=4;
		
		if(commonLanguageRuntimeHeaderSize==0)
			throw new DotNetContentNotFoundException("No CLR header found in file.");
		
		reservedDirectoryRVA = ByteReader.getUInt32(data, offset);				offset +=4;
		reservedDirectorySize = ByteReader.getUInt16(data, offset);				offset +=4;

		headerSize = offset - headerSize;
		
		assert(headerSize == STATIC_HEADER_SIZE);
		
		return headerSize;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.header.AbstractByteReader#getSize()
	 */
	public int getSize() {
		return STATIC_HEADER_SIZE;
	}
	
	
	/**
	 * Returns the RVA of the common language runtime header (or CLI header).
	 * @return The relative virtual address.
	 */
	public long getClrHeaderRVA() {
		return commonLanguageRuntimeHeaderRVA;
	}
	
	/**
	 * Returns the size of the common language runtime header (or CLI header).
	 * @return The size in bytes.
	 */
	public long getClrHeaderSize() {
		return commonLanguageRuntimeHeaderSize;
	}
	
	/**
	 * Returns the RVA of the debug directory.
	 * @return The relative virtual address.
	 */
	public long getDebugDirectoryRVA() {
		return debugDirectoryRVA;
	}
	
	/**
	 * Returns the size of the debug directory.
	 * @return The size in bytes.
	 */
	public long getDebugDirectorySize() {
		return debugDirectorySize;
	}
	
	/**
	 * Returns the RVA of the load configuration directory.
	 * @return The relative virtual address.
	 */
	public long getLoadConfigDirectoryRVA() {
		return loadConfigDirectoryRVA;
	}

	/**
	 * Returns the size of the load configuration directory.
	 * @return The size in bytes.
	 */
	public long getLoadConfigDirectorySize() {
		return loadConfigDirectorySize;
	}

	/**
	 * Returns the RVA of the export directory.
	 * @return The relative virtual address.
	 */
	public long getExportDirectoryRVA() {
		return exportDirectoryRVA;
	}

	/**
	 * Returns the size of the export directory.
	 * @return The size in bytes.
	 */
	public long getExportDirectorySize() {
		return exportDirectorySize;
	}

	/**
	 * Returns the RVA of the import directory.
	 * @return The relative virtual address.
	 */
	public long getImportDirectoryRVA() {
		return importDirectoryRVA;
	}

	/**
	 * Returns the size of the import directory.
	 * @return The size in bytes.
	 */
	public long getImportDirectorySize() {
		return importDirectorySize;
	}

	/**
	 * Returns the RVA of the resource directory.
	 * @return The relative virtual address.
	 */
	public long getResourceDirectoryRVA() {
		return resourceDirectoryRVA;
	}

	/**
	 * Returns the size of the resource directory.
	 * @return The size in bytes.
	 */
	public long getResourceDirectorySize() {
		return resourceDirectorySize;
	}

	/**
	 * Returns the RVA of the exception directory.
	 * @return The relative virtual address.
	 */
	public long getExceptionDirectoryRVA() {
		return exceptionDirectoryRVA;
	}

	/**
	 * Returns the size of the exception directory.
	 * @return The size in bytes.
	 */
	public long getExceptionDirectorySize() {
		return exceptionDirectorySize;
	}

	/**
	 * Returns the RVA of the certificate (security) directory.
	 * @return The relative virtual address.
	 */
	public long getCertificateDirectoryRVA() {
		return certificateDirectoryRVA;
	}

	/**
	 * Returns the size of the certificate (security) directory.
	 * @return The size in bytes.
	 */
	public long getCertificateDirectorySize() {
		return certificateDirectorySize;
	}

	/**
	 * Returns the RVA of the base relocation directory.
	 * @return The relative virtual address.
	 */
	public long getBaseRelocDirectoryRVA() {
		return baseRelocDirectoryRVA;
	}

	/**
	 * Returns the size of the base relocation directory.
	 * @return The size in bytes.
	 */
	public long getBaseRelocDirectorySize() {
		return baseRelocDirectorySize;
	}

	/**
	 * Returns the RVA of the copyright directory (even called architecture directory).
	 * @return The relative virtual address.
	 */
	public long getCopyrightDirectoryRVA() {
		return copyrightDirectoryRVA;
	}

	/**
	 * Returns the size of the copyright directory (even called architecture directory).
	 * @return The size in bytes.
	 */
	public long getCopyrightDirectorySize() {
		return copyrightDirectorySize;
	}

	/**
	 * Returns the RVA of the global pointer directory.
	 * @return The relative virtual address.
	 */
	public long getPointerDirectoryRVA() {
		return pointerDirectoryRVA;
	}

	/**
	 * Returns the size of the global pointer directory.
	 * @return The size in bytes.
	 */
	public long getPointerDirectorySize() {
		return pointerDirectorySize;
	}

	/**
	 * Returns the RVA of the thread local storage directory.
	 * @return The relative virtual address.
	 */
	public long getThreadLocalStorageDirectoryRVA() {
		return threadLocalStorageDirectoryRVA;
	}

	/**
	 * Returns the size of the thread local storage directory.
	 * @return The size in bytes.
	 */
	public long getThreadLocalStorageDirectorySize() {
		return threadLocalStorageDirectorySize;
	}

	/**
	 * Returns the RVA of the bound import directory.
	 * @return The relative virtual address.
	 */
	public long getBoundImportDirectoryRVA() {
		return boundImportDirectoryRVA;
	}

	/**
	 * Returns the size of the bound import directory.
	 * @return The size in bytes.
	 */
	public long getBoundImportDirectorySize() {
		return boundImportDirectorySize;
	}

	/**
	 * Returns the RVA of the import address table directory.
	 * @return The relative virtual address.
	 */
	public long getImportAddressTableDirectoryRVA() {
		return importAddrTabDirectoryRVA;
	}

	/**
	 * Returns the size of the import address table directory.
	 * @return The size in bytes.
	 */
	public long getImportAddressTableDirectorySize() {
		return importAddrTabDirectorySize;
	}

	/**
	 * Returns the RVA of the delay load (delay import) directory.
	 * @return The relative virtual address.
	 */
	public long getDelayLoadDirectoryRVA() {
		return delayLoadDirectoryRVA;
	}

	/**
	 * Returns the size of the delay load (delay import) directory.
	 * @return The size in bytes.
	 */
	public long getDelayLoadDirectorySize() {
		return delayLoadDirectorySize;
	}

	/**
	 * Returns the RVA of the reserved directory.
	 * @return The relative virtual address.
	 */
	public long getReservedDirectoryRVA() {
		return reservedDirectoryRVA;
	}

	/**
	 * Returns the size of the reserved directory.
	 * @return The size in bytes.
	 */
	public long getReservedDirectorySize() {
		return reservedDirectorySize;
	}

	@Override
	public String toString() {

		return "PE Data Directories:" +
				String.format("\n  Import Directory RVA: ..............0x%08x", importDirectoryRVA) +
				String.format("\n  Import Directory Size: .............%010d", importDirectorySize) +
				String.format("\n  Export Directory RVA: ..............0x%08x", exportDirectoryRVA) +
				String.format("\n  Export Directory Size: .............%010d", exportDirectorySize) +
				String.format("\n  Resource Directory RVA: ............0x%08x", resourceDirectoryRVA) +
				String.format("\n  Resource Directory Size: ...........%010d", resourceDirectorySize) +
				String.format("\n  Exception Directory RVA: ...........0x%08x", exceptionDirectoryRVA) +
				String.format("\n  Exception Directory Size: ..........%010d", exceptionDirectorySize) +
				"\n  ------------------------------------" +
				String.format("\n  Certificate Directory RVA: .........0x%08x", certificateDirectoryRVA) +
				String.format("\n  Certificate Directory Size: ........%010d", certificateDirectorySize) +
				String.format("\n  Base Relocation Directory RVA: .....0x%08x", baseRelocDirectoryRVA) +
				String.format("\n  Base Relocation Directory Size: ....%010d", baseRelocDirectorySize) +
				String.format("\n  Debug Directory RVA: ...............0x%08x", debugDirectoryRVA) +
				String.format("\n  Debug Directory Size: ..............%010d", debugDirectorySize) +
				String.format("\n  Copyright Directory RVA: ...........0x%08x", copyrightDirectoryRVA) +
				String.format("\n  Copyright Directory Size: ..........%010d", copyrightDirectorySize) +
				"\n  ------------------------------------" +
				String.format("\n  Global Pointer Directory RVA: ......0x%08x", pointerDirectoryRVA) +
				String.format("\n  Global Pointer Directory Size: .....%010d", pointerDirectorySize) +
				String.format("\n  Thread Local Storage Dir. RVA: .....0x%08x", threadLocalStorageDirectoryRVA) +
				String.format("\n  Thread Local Storage Dir. Size: ....%010d", threadLocalStorageDirectorySize) +
				String.format("\n  Load Config Directory RVA: .........0x%08x", loadConfigDirectoryRVA) +
				String.format("\n  Load Config Directory Size: ........%010d", loadConfigDirectorySize) +
				String.format("\n  Bound Import Directory RVA: ........0x%08x", boundImportDirectoryRVA) +
				String.format("\n  Bound Import Directory Size: .......%010d", boundImportDirectorySize) +
				"\n  ------------------------------------" +
				String.format("\n  Import Address Table RVA: ..........0x%08x", importAddrTabDirectoryRVA) +
				String.format("\n  Import Address Table Size: .........%010d", importAddrTabDirectorySize) +
				String.format("\n  Delay Load (Import) Dir. RVA: ......0x%08x", delayLoadDirectoryRVA) +
				String.format("\n  Delay Load (Import) Dir. Size: .....%010d", delayLoadDirectorySize) +
				String.format("\n  Common Language Runtime RVA: .......0x%08x", commonLanguageRuntimeHeaderRVA) +
				String.format("\n  Common Language Runtime Size: ......%010d", commonLanguageRuntimeHeaderSize) +
				String.format("\n  Reserved Directory RVA: ............0x%08x", reservedDirectoryRVA) +
				String.format("\n  Reserved Directory Size: ...........%010d", reservedDirectorySize);
	}
	
}

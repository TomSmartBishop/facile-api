package at.pollaknet.api.facile.header.cli;

import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ByteReader;


public class CliHeader implements IDataHeader {

	public final static int FLAGS_IL_ONLY 					= 0x00000001;
	public final static int FLAGS_32_BIT_REQUIRED 			= 0x00000002;
	public final static int FLAGS_IL_LIBRARY 				= 0x00000004;
	public final static int FLAGS_STRONG_NAME_SIGNATURE 	= 0x00000008;
	public final static int FLAGS_NATIVE_ENTRY_POINT		= 0x00000010;
	public final static int FLAGS_TRACK_DEBUG_DATA		 	= 0x00010000;
	
	public final static int STATIC_HEADER_SIZE = 72;
	
	private long clrHeaderSize;
	private int majorRuntimeVersion;
	private int minorRuntimeVersion;
	private long addrOfMetadataDirectory;
	private long sizeOfMetadataDirectory;
	private long flags;
	private long entryPointToken;
	private long addrOfResourcesDirectory;
	private long sizeOfRecourcesDirectory;
	private long addrOfStrongNameSignature;
	private long sizeOfStrongNameSignature;
	private long addrOfCodeManagerTable;
	private long sizeOfCodeManagerTable;
	private long addrOfVTableFixupDirectory;
	private long sizeOfVTableFixupDirectory;
	private long addrOfExportAddressTable;
	private long sizeOfExportAddressTable;
	private long addrOfPrecompiledHeader;
	private long sizeOfPrecompiledHeader;
	
	private int headerSize;
	
	/* (non-Javadoc)
	 * @see facile.portableExecuteable.IFileHeader#read(byte[], int)
	 */
	public int read (byte [] data, int offset) throws UnexpectedHeaderDataException {
			
		headerSize = offset;
		
		clrHeaderSize = ByteReader.getUInt32(data, offset);					offset +=4;
		majorRuntimeVersion = ByteReader.getUInt16(data, offset);			offset +=2;
		minorRuntimeVersion = ByteReader.getUInt16(data, offset);			offset +=2;
		
		addrOfMetadataDirectory = ByteReader.getUInt32(data, offset);		offset +=4;
		sizeOfMetadataDirectory = ByteReader.getUInt32(data, offset);		offset +=4;
		
		flags = ByteReader.getUInt32(data, offset);							offset +=4;
		entryPointToken = ByteReader.getUInt32(data, offset);				offset +=4;
		
		addrOfResourcesDirectory = ByteReader.getUInt32(data, offset);		offset +=4;
		sizeOfRecourcesDirectory = ByteReader.getUInt32(data, offset);		offset +=4;
		addrOfStrongNameSignature = ByteReader.getUInt32(data, offset);		offset +=4;
		sizeOfStrongNameSignature = ByteReader.getUInt32(data, offset);		offset +=4;
		addrOfCodeManagerTable = ByteReader.getUInt32(data, offset);		offset +=4;
		sizeOfCodeManagerTable = ByteReader.getUInt32(data, offset);		offset +=4;
		addrOfVTableFixupDirectory = ByteReader.getUInt32(data, offset);	offset +=4;
		sizeOfVTableFixupDirectory = ByteReader.getUInt32(data, offset);	offset +=4;
		addrOfExportAddressTable = ByteReader.getUInt32(data, offset);		offset +=4;
		sizeOfExportAddressTable = ByteReader.getUInt32(data, offset);		offset +=4;
		addrOfPrecompiledHeader = ByteReader.getUInt32(data, offset);		offset +=4;
		sizeOfPrecompiledHeader = ByteReader.getUInt32(data, offset);		offset +=4;
		
		headerSize = offset - headerSize;
		
		assert(headerSize == STATIC_HEADER_SIZE);
		
		return headerSize;
	}

	public int getSize() {
		return STATIC_HEADER_SIZE;
	}

	public long getClrHeaderSize() {
		return clrHeaderSize;
	}

	public int getMajorRuntimeVersion() {
		return majorRuntimeVersion;
	}

	public int getMinorRuntimeVersion() {
		return minorRuntimeVersion;
	}

	public long getAddrOfMetadataDirectory() {
		return addrOfMetadataDirectory;
	}

	public long getSizeOfMetadataDirectory() {
		return sizeOfMetadataDirectory;
	}

	public long getFlags() {
		return flags;
	}
	

	public String toString() {
		StringBuffer buffer = new StringBuffer(512);
		
		buffer.append("PE Data Directories:");
		buffer.append(String.format("\n  Major Runtime Version: ...............%05d", majorRuntimeVersion));
		buffer.append(String.format("\n  Minor Runtime Version: ...............%05d", minorRuntimeVersion));
		buffer.append(String.format("\n  Address of Metadata Directory: .......0x%08x", addrOfMetadataDirectory));
		buffer.append(String.format("\n  Size of Metadata Directory: ..........%010d", sizeOfMetadataDirectory));
		buffer.append(String.format("\n  Flags: ...............................0x%08x", flags));
		buffer.append(String.format("\n  Entry Point Token: ...................0x%08x", entryPointToken));
		buffer.append(String.format("\n  Address of Resources Directory: ......0x%08x", addrOfResourcesDirectory));
		buffer.append(String.format("\n  Size of Resources Directory: .........%010d", sizeOfRecourcesDirectory));
		buffer.append(String.format("\n  Address of Strong Name Signature: ....0x%08x", addrOfStrongNameSignature));
		buffer.append(String.format("\n  Size of Strong Name Signature: .......%010d", sizeOfStrongNameSignature));
		buffer.append(String.format("\n  Address of Code Manager Table: .......0x%08x", addrOfCodeManagerTable));
		buffer.append(String.format("\n  Size of Code Manager Table: ..........%010d", sizeOfCodeManagerTable));
		buffer.append(String.format("\n  Address of VTable Fixup Directory: ...0x%08x", addrOfVTableFixupDirectory));
		buffer.append(String.format("\n  Size of VTable Fixup Directory: ......%010d", sizeOfVTableFixupDirectory));
		buffer.append(String.format("\n  Address of Strong Name Signature: ....0x%08x", addrOfExportAddressTable));
		buffer.append(String.format("\n  Size of Strong Name Signature: .......%010d", sizeOfExportAddressTable));
		buffer.append(String.format("\n  Address of Precompiled Header: .......0x%08x", addrOfPrecompiledHeader));
		buffer.append(String.format("\n  Size of Precompiled Header: ..........%010d", sizeOfPrecompiledHeader));
		
		return buffer.toString();
	}

	public long getAddrOfResourcesDirectory() {
		return addrOfResourcesDirectory;
	}

	public long getSizeOfRecourcesDirectory() {
		return sizeOfRecourcesDirectory;
	}

	public long getAddrOfStrongNameSignature() {
		return addrOfStrongNameSignature;
	}

	public long getSizeOfStrongNameSignature() {
		return sizeOfStrongNameSignature;
	}

	public long getAddrOfCodeManagerTable() {
		return addrOfCodeManagerTable;
	}

	public long getSizeOfCodeManagerTable() {
		return sizeOfCodeManagerTable;
	}

	public long getAddrOfVTableFixupDirectory() {
		return addrOfVTableFixupDirectory;
	}

	public long getSizeOfVTableFixupDirectory() {
		return sizeOfVTableFixupDirectory;
	}

	public long getAddrOfExportAddressTable() {
		return addrOfExportAddressTable;
	}

	public long getSizeOfExportAddressTable() {
		return sizeOfExportAddressTable;
	}

	public long getAddrOfPrecompiledHeader() {
		return addrOfPrecompiledHeader;
	}

	public long getSizeOfPrecompiledHeader() {
		return sizeOfPrecompiledHeader;
	}

	public long getEntryPointToken() {
		return entryPointToken;
	}
}

package at.pollaknet.api.facile.header.coffpe;

import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ByteReader;


public class PESectionHeader implements IDataHeader, Comparable<PESectionHeader>  {

	public final static int HEADER_SIZE = 40;
	
	public final static long CHARACTERISTICS_FLAGS_SCALE_TLS_INDEX		 		= 0x00000010; //TLS descriptor table index is scaled.
	public final static long CHARACTERISTICS_FLAGS_CONTAINS_EXECUTABLE_CODE 	= 0x00000020; //Section contains executable code.
	public final static long CHARACTERISTICS_FLAGS_CONTAINS_INITIALIZED_DATA	= 0x00000040; //Section contains initialized data.
	public final static long CHARACTERISTICS_FLAGS_CONTAINS_UNINITIALIZED_DATA	= 0x00000080; //Section contains uninitialized data.
	public final static long CHARACTERISTICS_FLAGS_HAS_COMMENTS	 				= 0x00000200; //Section contains comments or auxiliary info.
	public final static long CHARACTERISTICS_FLAGS_RESET_SPECULATIVE_EXCEPTIONS = 0x00004000; //Reset speculative exception handling in the translation lookaside buffer (TLB) of this section.
	public final static long CHARACTERISTICS_FLAGS_HAS_EXTENDED_RELOCATIONS  	= 0x01000000; //Section contains extended relocation information.
	public final static long CHARACTERISTICS_FLAGS_MEMORY_DISCARDABLE			= 0x02000000; //Section can be discarded as needed.
	public final static long CHARACTERISTICS_FLAGS_MEMORY_DONT_CACHE			= 0x04000000; //Section cannot be cached.
	public final static long CHARACTERISTICS_FLAGS_MEMORY_DONT_PAGE				= 0x08000000; //Section cannot be paged.
	public final static long CHARACTERISTICS_FLAGS_MEMORY_SHARED				= 0x10000000; //Section can be shared in memory.
	public final static long CHARACTERISTICS_FLAGS_MEMORY_EXECUTE				= 0x20000000; //Section can be executed.
	public final static long CHARACTERISTICS_FLAGS_MEMORY_READ					= 0x40000000; //Section can be read.
	public final static long CHARACTERISTICS_FLAGS_MEMORY_WRITE					= 0x80000000; //Section can be written to.
	
	private String sectionName; // 8 byte
	private long virtualSize;
	private long relativeVirtualAddress;
	private long sizeOfRawData;
	private long pointerToRawData;
	private long pointerToRelocations;
	private long pointerToLineNumbers;
	private int numberOfRelocations;
	private int numberOfLineNumbers;
	private long characteristics;
	
	private int headerSize = 0;
	
	public int read (byte [] data, int offset) throws UnexpectedHeaderDataException {
		
		headerSize = offset;

		sectionName = new String(ByteReader.getBytes(data, offset, 8));				offset +=8;
		virtualSize = ByteReader.getUInt32(data, offset);							offset +=4;
		relativeVirtualAddress = ByteReader.getUInt32(data, offset);				offset +=4;
		sizeOfRawData = ByteReader.getUInt32(data, offset);							offset +=4;
		pointerToRawData = ByteReader.getUInt32(data, offset);						offset +=4;
		pointerToRelocations = ByteReader.getUInt32(data, offset);					offset +=4;
		pointerToLineNumbers = ByteReader.getUInt32(data, offset);					offset +=4;
		numberOfRelocations = ByteReader.getUInt16(data, offset);					offset +=2;
		numberOfLineNumbers = ByteReader.getUInt16(data, offset);					offset +=2;
		characteristics = ByteReader.getUInt32(data, offset);						offset +=4;
		
		headerSize = offset - headerSize;
		
		assert (headerSize == HEADER_SIZE);
		
		return headerSize;
	}


	/**
	 * Get the size of the header.
	 * @return Size in bytes.
	 */
	public int getSize() {
		return HEADER_SIZE;
	}


	/**
	 * Get the name of the section defined in this header.
	 * @return The name as {@code string}.
	 */
	public String getSectionName() {
		return sectionName;
	}


	/**
	 * Get the virtual size of the section (size of the
	 * section when it is loaded into the memory - optimized
	 * for the memory alignment).
	 * @return The virtual size of the file section in bytes.
	 */
	public long getVirtualSize() {
		return virtualSize;
	}


	/**
	 * Get the RVA of the file section.
	 * @return The relative, virtual address of the file section.
	 */
	public long getRelativeVirtualAddress() {
		return relativeVirtualAddress;
	}

	/**
	 * Get the physical address of the file section.
	 * @return The physical address of the file section.
	 */
	public long getPointerToRawData() {
		return pointerToRawData;
	}


	/**
	 * Get the sections raw size (less or equal the virtual size).
	 * @return The section's size in bytes.
	 */
	public long getSizeOfRawData() {
		return sizeOfRawData;
	}


	/**
	 * Get the physical address of the relocation table.
	 * @return The address, pointing to the relocation table.
	 */
	public long getPointerToRelocations() {
		return pointerToRelocations;
	}


	/**
	 * Get the physical address of the line number table.
	 * @return The address, pointing to the line number table.
	 */
	public long getPointerToLineNumbers() {
		return pointerToLineNumbers;
	}

	/**
	 * Get the number of defined relocations in the relocation table
	 * @return The number of defined relocations.
	 */
	public int getNumberOfRelocations() {
		return numberOfRelocations;
	}

	/**
	 * Get the number of defined line numbers in the line number table
	 * @return The number of defined line numbers.
	 */
	public int getNumberOfLineNumbers() {
		return numberOfLineNumbers;
	}

	/**
	 * Get the define characteristics flags of the file section.
	 * @return Characteristics flags (flags are defined as CHARACTERISTICS_FLAG_*).
	 */
	public long getCharacteristicsFlags() {
		return characteristics;
	}


	@Override
	public int compareTo(PESectionHeader otherHeader) {
		return (int) (relativeVirtualAddress-otherHeader.getRelativeVirtualAddress());
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(128);
		buffer.append("PE Section Header");
		buffer.append("\n  Section Name: ...............\"");
		buffer.append(sectionName.replaceAll("\\p{Cntrl}","_"));
		buffer.append("\"");
		buffer.append(String.format("\n  Virtual Size: ...............%010d", virtualSize));
		buffer.append(String.format("\n  Relative Virtual Address: ...0x%08x", relativeVirtualAddress));
		buffer.append(String.format("\n  Size of Raw Data: ...........%010d", sizeOfRawData));
		buffer.append(String.format("\n  Pointer to Raw Data: ........0x%08x", pointerToRawData));
		buffer.append(String.format("\n  Pointer to Relocations: .....0x%08x", pointerToRelocations));
		buffer.append(String.format("\n  Pointer to Line Numbers: ....0x%08x", pointerToLineNumbers));
		buffer.append(String.format("\n  Number of Relocations: ......%010d", numberOfRelocations));
		buffer.append(String.format("\n  Number of Line Numbers: .....%010d", numberOfLineNumbers));
		buffer.append(String.format("\n  Characteristics: ............0x%08x", characteristics));
		
		return buffer.toString();
	}

}

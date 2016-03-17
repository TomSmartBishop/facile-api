package at.pollaknet.api.facile.header.coffpe;

import at.pollaknet.api.facile.exception.CoffPeDataNotFoundException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ByteReader;


public class DOSHeader implements IDataHeader {

	//the magic number of every DOS executable ("MZ")
	public static final int MAGIC_NUMBER = 0x5a4d;

	//the predefined static size of the header
	public static final int STATIC_HEADER_SIZE = 64;
	
	private int magicNumber;
	private int bytesOnLastPage;
	private int pagesInFile;
	private int relocations;
	private int sizeOfHeader;
	private int minExtraParagraphs;
	private int maxExtraParagraphs;
	private int initialRelativeSS;
	private int initialSP;
	private int checksum;
	private int initialIP;
	private int initialRelativeCS;
	private int fileAddrOfRelocTable;
	private int overlayNumber;
	private int OEMIdentifier;
	private int OEMInfo;
	private byte [] reservedSpace1 = new byte[8];
	private byte [] reservedSpace2 = new byte[20];
	private long fileAddrOfCOFFHeader;
	
	//the header size evaluated during the reading process
	private int headerSize;
	
	/* (non-Javadoc)
	 * @see facile.portableExecutable.IFileHeader#read(byte[], int)
	 */
	public int read (byte [] data, int offset) throws CoffPeDataNotFoundException {
			
		if(data==null || data.length<STATIC_HEADER_SIZE) 
			throw new CoffPeDataNotFoundException("The specified file is too small.");
		
		headerSize = offset;
		
		//read and check if the specified data is valid
		magicNumber = ByteReader.getUInt16(data, offset);
		
		if(magicNumber!=MAGIC_NUMBER)
			throw new CoffPeDataNotFoundException("Magic number check of PE header failed."); //$NON-NLS-1$
		
		offset +=2;
		
		//read all data field of the header
		bytesOnLastPage = ByteReader.getUInt16(data, offset);		offset +=2;
		pagesInFile = ByteReader.getUInt16(data, offset);			offset +=2;
		relocations = ByteReader.getUInt16(data, offset);			offset +=2;
		sizeOfHeader = ByteReader.getUInt16(data, offset);			offset +=2;
		minExtraParagraphs = ByteReader.getUInt16(data, offset);	offset +=2;
		maxExtraParagraphs = ByteReader.getUInt16(data, offset);	offset +=2;
		initialRelativeSS = ByteReader.getUInt16(data, offset);		offset +=2;
		initialSP = ByteReader.getUInt16(data, offset);				offset +=2;
		checksum = ByteReader.getUInt16(data, offset);				offset +=2;
		initialIP = ByteReader.getUInt16(data, offset);			offset +=2;
		initialRelativeCS = ByteReader.getUInt16(data, offset);		offset +=2;
		fileAddrOfRelocTable = ByteReader.getUInt16(data, offset);	offset +=2;
		overlayNumber = ByteReader.getUInt16(data, offset);			offset +=2;
		reservedSpace1 = ByteReader.getBytes(data, offset, 8);		offset +=8;
		OEMIdentifier = ByteReader.getUInt16(data, offset);			offset +=2;
		OEMInfo = ByteReader.getUInt16(data, offset);				offset +=2;
		reservedSpace2 = ByteReader.getBytes(data, offset, 20);		offset +=20;
		fileAddrOfCOFFHeader = ByteReader.getUInt32(data, offset);	offset +=4;
		
		//calculate the header size
		headerSize = offset - headerSize;
		
		assert(headerSize == STATIC_HEADER_SIZE);
		
		return headerSize;
	}

	/**
	 * Get the static size of the MS-DOS header in bytes.
	 * @return Size in bytes.
	 */
	public int getSize() {
		return STATIC_HEADER_SIZE;
	}

	/**
	 * Get the address of the COFF/PE header.
	 * @return The physical address of the COFF/PE header in the file.
	 */
	public long getFileAddrOfCOFFHeader() {
		return fileAddrOfCOFFHeader;
	}

	/**
	 * Get the number of relocations in the file.
	 * @return Amount of relocations.
	 */
	public int getNumberOfRelocations() {
		return relocations;
	}

	/**
	 * Get the magic number of the DOS-HEADER.
	 * @return Integer representation of "MZ" (0x5a4d).
	 */
	public int getMagicNumber() {
		return magicNumber;
	}

	/**
	 * Get the number of bytes on the last page.
	 * @return Number of bytes on the last page.
	 */
	public int getBytesOnLastPage() {
		return bytesOnLastPage;
	}

	/**
	 * Get the number of pages.
	 * @return The number of pages.
	 */
	public int getPagesInFile() {
		return pagesInFile;
	}

	/**
	 * Get specified size of the header (which is self-described within the header).
	 * @return The size of the header in bytes.
	 */
	public int getSizeOfHeader() {
		return sizeOfHeader;
	}

	/**
	 * Get the minimum number of required extra paragraphs.
	 * @return The minimum number of extra paragraphs.
	 */
	public int getMinExtraParagraphs() {
		return minExtraParagraphs;
	}

	/**
	 * Get the maximum number of required extra paragraphs.
	 * @return The maximum number of extra paragraphs.
	 */
	public int getMaxExtraParagraphs() {
		return maxExtraParagraphs;
	}

	/**
	 * Get the initial relative stack segment.
	 * @return The stack segment value.
	 */
	public int getInitialRelativeSS() {
		return initialRelativeSS;
	}

	/**
	 * Get the initial stack pointer.
	 * @return The value of the initial stack pointer.
	 */
	public int getInitialSP() {
		return initialSP;
	}

	/**
	 * Return the CRC32 Checksum of the file. If
	 * {@link at.pollaknet.api.facile.header.coffpe.DOSHeader#getFileAddrOfCOFFHeader()}
	 * returns 0 as address of the COFF/PE Header CRC16 was used in order
	 * to calculate the checksum (instead of CRC32).
	 * @return The checksum value of the current file (buffer).
	 */
	public int getChecksum() {
		return checksum;
	}

	/**
	 * Get the initial value of the instruction pointer.
	 * @return The initial instruction pointer.
	 */
	public int getInitialIP() {
		return initialIP;
	}

	/**
	 * Get the initial relative code segment.
	 * @return The code segment value.
	 */
	public int getInitialRelativeCS() {
		return initialRelativeCS;
	}

	/**
	 * Get the address of the relocation table inside the file.
	 * @return The physical address of the relocation table.
	 */
	public int getFileAddrOfRelocTable() {
		return fileAddrOfRelocTable;
	}

	/**
	 * Get the overlay number (program number).
	 * @return The value of the overlay number.
	 */
	public int getOverlayNumber() {
		return overlayNumber;
	}

	/**
	 * Get the identification number of the original equipment manufacturer (OEM).
	 * @return ID of the OEM as {@code int} value.
	 */
	public int getOEMIdentifier() {
		return OEMIdentifier;
	}

	/**
	 * Get the original equipment manufacturer (OEM) specific data.
	 * @return OEM specific information represented as {@code int}.
	 */
	public int getOEMInfo() {
		return OEMInfo;
	}

	/**
	 * Get the bytes of the reserved area 1.
	 * @return A {@code byte []} containing the data of the reserved area 1.
	 * The array is null if there is no reserved area.
	 */
	public byte[] getReservedSpace1() {
		return reservedSpace1;
	}
	
	/**
	 * Get the bytes of the reserved area 2.
	 * @return A {@code byte []} containing the data of the reserved area 2.
	 * The array is null if there is no reserved area.
	 */
	public byte[] getReservedSpace2() {
		return reservedSpace2;
	}
	
	public String toString() {

		return "DOS Header" +
				String.format("\n  Magic Number: ...............................0x%08x", magicNumber) +
				String.format("\n  Bytes on Last Page: .........................%010d", bytesOnLastPage) +
				String.format("\n  Pages in File: ..............................%010d", pagesInFile) +
				String.format("\n  Relocations: ................................%010d", relocations) +
				String.format("\n  Size of Header: .............................%010d", sizeOfHeader) +
				String.format("\n  Min Extra Paragraphs: .......................0x%08x", minExtraParagraphs) +
				String.format("\n  Max Extra Paragraphs: .......................0x%08x", maxExtraParagraphs) +
				String.format("\n  Initial Relative Stack Segment (SS): ........0x%08x", initialRelativeSS) +
				String.format("\n  Initial Stack Pointer (SP): .................0x%08x", initialSP) +
				String.format("\n  Checksum (%s): ...........................0x%08x", fileAddrOfCOFFHeader == 0 ? "CRC16" : "CRC32", checksum) +
				String.format("\n  Initial Instruction Pointer (IP): ...........0x%08x", initialIP) +
				String.format("\n  Initial Relative Code Segment (CS): .........0x%08x", initialRelativeCS) +
				String.format("\n  File Address of Reloc Table: ................0x%08x", fileAddrOfRelocTable) +
				String.format("\n  Overlay Number: .............................%010d", overlayNumber) +
				String.format("\n  Reserved Space 1: ...........................%02d Bytes", reservedSpace1 == null ? 0 : reservedSpace1.length) +
				String.format("\n  OEM Identifier: .............................0x%08x", OEMIdentifier) +
				String.format("\n  OEM Info: ...................................0x%08x", OEMInfo) +
				String.format("\n  Reserved Space 2: ...........................%02d Bytes", reservedSpace2 == null ? 0 : reservedSpace2.length) +
				String.format("\n  File Address of COFF Header: ................0x%08x", fileAddrOfCOFFHeader);
	}

}

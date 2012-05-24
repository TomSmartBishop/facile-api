package at.pollaknet.api.facile.header.cli.stream;

import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.header.cli.stream.metatable.AbstractTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyOsTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyProcessorTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyRefOsTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyRefProcessorTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ClassLayoutTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ConstantTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.CustomAttributeTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.DeclSecurityTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.EncLogTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.EncMapTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.EventMapTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.EventPtrTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.EventTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ExportedTypeTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FieldLayoutTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FieldMarshalTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FieldPtrTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FieldRVATable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FieldTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FileTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.GenericParamConstraintTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.GenericParamTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ImplMapTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.InterfaceImplTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ManifestResourceTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MemberRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MethodDefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MethodImplTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MethodPtrTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MethodSemanticsTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MethodSpecTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ModuleRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ModuleTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.NestedClassTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ParamPtrTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ParamTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.PropertyMapTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.PropertyPtrTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.PropertyTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.StandAloneSigTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.TypeDefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.TypeRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.TypeSpecTable;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.ByteReader;



public class MetadataStream implements IDataHeader {

	public final static int MIN_STREAM_SIZE = 24;
	public final static int MAX_NUMBER_OF_TABLES = 64;
	
	public final static int HEAPS_FLAGS_4BYTE_STRING_HEAP 						= 0x01;
	public final static int HEAPS_FLAGS_4BYTE_GUID_HEAP 						= 0x02;
	public final static int HEAPS_FLAGS_4BYTE_BLOB_HEAP 						= 0x04;
	public final static int HEAPS_FLAGS_UNOPTIMIZED_EDIT_AND_CONTINUE_DATA 		= 0x20;
	public final static int HEAPS_FLAGS_UNOPTIMIZED_CAN_CONTAIN_DELETED_DATA 	= 0x80;
	
	private long reservedValue;
	private int majorVersion;
	private int minorVersion;
	private int heaps;
	private int widthOfMaxRid;
	private long validMaskLow;
	private long validMaskHigh;
	private long sortedLow;
	private long sortedHigh;
		
	private int [] numberOfRows;
	
	private int byteSize;
	
	private AbstractTable metadataTable [] = new AbstractTable [MAX_NUMBER_OF_TABLES];
	
	private int emtpyTablesIndices [] = new int [] { 0x2d, 0x2e, 0x2f, 0x30, 0x31, 0x32,
			0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f };
	
	private final static byte SIZE_OF_LARGE_INDEX = 4;
	private final static byte SIZE_OF_SMALL_INDEX = 2;
	
	private boolean isUnoptimized;
		
	public MetadataStream (boolean isUnoptimized) {
		this.isUnoptimized = isUnoptimized;
		
		metadataTable[ModuleTable.TABLE_INDEX] = new ModuleTable();
		metadataTable[TypeRefTable.TABLE_INDEX] = new TypeRefTable();
		metadataTable[TypeDefTable.TABLE_INDEX] = new TypeDefTable();
		metadataTable[FieldPtrTable.TABLE_INDEX] = new FieldPtrTable();
		metadataTable[FieldTable.TABLE_INDEX] = new FieldTable();
		metadataTable[MethodPtrTable.TABLE_INDEX] = new MethodPtrTable();
		metadataTable[MethodDefTable.TABLE_INDEX] = new MethodDefTable();
		metadataTable[ParamPtrTable.TABLE_INDEX] = new ParamPtrTable();
		metadataTable[ParamTable.TABLE_INDEX] = new ParamTable();
		metadataTable[InterfaceImplTable.TABLE_INDEX] = new InterfaceImplTable();
		metadataTable[MemberRefTable.TABLE_INDEX] = new MemberRefTable();
		metadataTable[ConstantTable.TABLE_INDEX] = new ConstantTable();
		metadataTable[CustomAttributeTable.TABLE_INDEX] = new CustomAttributeTable();
		metadataTable[FieldMarshalTable.TABLE_INDEX] = new FieldMarshalTable();
		metadataTable[DeclSecurityTable.TABLE_INDEX] = new DeclSecurityTable();
		metadataTable[ClassLayoutTable.TABLE_INDEX] = new ClassLayoutTable();
		metadataTable[FieldLayoutTable.TABLE_INDEX] = new FieldLayoutTable();
		metadataTable[StandAloneSigTable.TABLE_INDEX] =  new StandAloneSigTable();
		metadataTable[EventMapTable.TABLE_INDEX] = new EventMapTable();
		metadataTable[EventPtrTable.TABLE_INDEX] = new EventPtrTable();
		metadataTable[EventTable.TABLE_INDEX] = new EventTable();
		metadataTable[PropertyMapTable.TABLE_INDEX] = new PropertyMapTable();
		metadataTable[PropertyPtrTable.TABLE_INDEX] = new PropertyPtrTable();
		metadataTable[PropertyTable.TABLE_INDEX] = new PropertyTable();
		metadataTable[MethodSemanticsTable.TABLE_INDEX] = new MethodSemanticsTable();
		metadataTable[MethodImplTable.TABLE_INDEX] = new MethodImplTable();
		metadataTable[ModuleRefTable.TABLE_INDEX] = new ModuleRefTable();
		metadataTable[TypeSpecTable.TABLE_INDEX] = new TypeSpecTable();
		metadataTable[ImplMapTable.TABLE_INDEX] = new ImplMapTable();
		metadataTable[FieldRVATable.TABLE_INDEX] = new FieldRVATable();
		metadataTable[EncLogTable.TABLE_INDEX] = new EncLogTable();
		metadataTable[EncMapTable.TABLE_INDEX] = new EncMapTable();
		metadataTable[AssemblyTable.TABLE_INDEX] = new AssemblyTable();
		metadataTable[AssemblyProcessorTable.TABLE_INDEX] = new AssemblyProcessorTable();
		metadataTable[AssemblyOsTable.TABLE_INDEX] = new AssemblyOsTable();
		metadataTable[AssemblyRefTable.TABLE_INDEX] = new AssemblyRefTable();
		metadataTable[AssemblyRefProcessorTable.TABLE_INDEX] = new AssemblyRefProcessorTable();
		metadataTable[AssemblyRefOsTable.TABLE_INDEX] = new AssemblyRefOsTable();
		metadataTable[FileTable.TABLE_INDEX] = new FileTable();
		metadataTable[ExportedTypeTable.TABLE_INDEX] = new ExportedTypeTable();
		metadataTable[ManifestResourceTable.TABLE_INDEX] = new ManifestResourceTable();
		metadataTable[NestedClassTable.TABLE_INDEX] = new NestedClassTable();
		metadataTable[GenericParamTable.TABLE_INDEX] = new GenericParamTable();
		metadataTable[MethodSpecTable.TABLE_INDEX] = new MethodSpecTable();
		metadataTable[GenericParamConstraintTable.TABLE_INDEX] = new GenericParamConstraintTable();
	}
	
	/* (non-Javadoc)
	 * @see facile.portableExecuteable.IFileHeader#read(byte[], int)
	 */
	public int read (byte [] data, int offset) throws UnexpectedHeaderDataException {
		
		//See ECMA 335 revision 4 - Partition II, 24.2.6: #~ stream
		//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=295&view=FitH

		byteSize = offset;
		
		//read the static header information
		offset += readStaticHeader(data, offset);
				
		//continue with the non-static row data
		numberOfRows = new int [64];
		long high = validMaskHigh;
		long low = validMaskLow;
		long value;
		
		//read the row number information (for the tables)
		for(int i=0;i<32;i++) {
			if((low&1)==1) {
				value = ByteReader.getUInt32(data, offset);
				assert(value<=ByteReader.INT32_MAX_VAL);
				numberOfRows[i] = (int) value;
				offset+=4;
			} else {
				numberOfRows[i] = 0;
			}     
			low>>=1;
		}
		
		//do the same again for the upper 32bit
		for(int i=32;i<64;i++) {
			if((high&1)==1) {
				value = ByteReader.getUInt32(data, offset);
				assert(value<=ByteReader.INT32_MAX_VAL);
				numberOfRows[i] = (int) value;
				offset+=4;
			} else {
				numberOfRows[i] = 0;
			}
			high>>=1;
		}
			
		//read all tables (empty tables are skipped by the
		//readRows operation - numberOfRows==0)
		for(AbstractTable t : metadataTable) {
			if(t!=null) {     
				offset += t.readRows(data, offset, numberOfRows, this);
			}
		}
		
		for(int index: emtpyTablesIndices) {
			if(numberOfRows[index]!=0) {
				Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
				String msg = String.format(
						"Found %d entries in unknown metadata table 0x%x!",
						numberOfRows[index], index);
				logger.log(Level.WARNING, msg);
			}
		}
		
		offset = ByteReader.alingToDWord(offset);

		byteSize = offset - byteSize;
		
		assert(byteSize >= MIN_STREAM_SIZE);
		
		return byteSize;
	}

	
	private int readStaticHeader(byte[] data, int offset) {
		int consumedBytes = offset;
		
		reservedValue = ByteReader.getUInt32(data, offset);		offset +=4;
		
		majorVersion = ByteReader.getUInt8(data, offset);		offset ++;
		minorVersion = ByteReader.getUInt8(data, offset);		offset ++;
		heaps = ByteReader.getUInt8(data, offset);				offset ++;
		widthOfMaxRid = ByteReader.getUInt8(data, offset);		offset ++;
		
		validMaskLow = ByteReader.getUInt32(data, offset);		offset +=4;
		validMaskHigh = ByteReader.getUInt32(data, offset);		offset +=4;
		sortedLow = ByteReader.getUInt32(data, offset);			offset +=4;
		sortedHigh = ByteReader.getUInt32(data, offset);		offset +=4;
			
		assert(offset - consumedBytes == MIN_STREAM_SIZE);
		
		return offset - consumedBytes;
	}

	public int getSize() {
		return byteSize;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(128);
		
		buffer.append(getName());
		buffer.append(" Stream (Metadata):");
		buffer.append(String.format("\n\n  Major Version: ........%010d", majorVersion));
		buffer.append(String.format("\n  Minor Version: ........%010d", minorVersion));
		buffer.append(String.format("\n  Heaps: ................%010d", heaps));
		buffer.append(String.format("\n  Width of max RID: .....0x%04x", widthOfMaxRid));
		buffer.append(String.format("\n  Valid Mask (low): .....0x%08x", validMaskLow));
		buffer.append(String.format("\n  Valid Mask (high): ....0x%08x", validMaskHigh));
		buffer.append(String.format("\n  Sorted (low): .........0x%08x", sortedLow));
		buffer.append(String.format("\n  Sorted (high): ........0x%08x", sortedHigh));
		
		buffer.append("\n\n\n  Tables:\n");
		
		for(int i=0; i<MAX_NUMBER_OF_TABLES; i++) {
			if(i%4==0) buffer.append("\n");
			buffer.append(String.format("    Table 0x%02x: %-5d", i, numberOfRows[i]));
		}

		return buffer.toString();
	}
	
	public String toExtendedString(int ... table) {
		StringBuffer buffer = new StringBuffer(toString());
		
		if(table.length==0) {
			for(AbstractTable t : metadataTable) {
				if(t!=null && numberOfRows[t.getRID()]!=0) {
					buffer.append("\n");
					buffer.append(t.toString());
				}
			}
		} else {
			for(AbstractTable t : metadataTable) {
				if(t!=null && ArrayUtils.contains(table, t.getRID())) {
					buffer.append("\n");
					buffer.append(t.toString());
				}
			}
		}
		
		return buffer.toString();
	}
	
	public String getName(){
		return isUnoptimized ? "#-" :  "#~";
	}
	
	public byte getByteSizeOfStringIndices () {
		return (heaps&HEAPS_FLAGS_4BYTE_STRING_HEAP)==HEAPS_FLAGS_4BYTE_STRING_HEAP ? SIZE_OF_LARGE_INDEX : SIZE_OF_SMALL_INDEX;
	}
	
	public byte getByteSizeOfGuidIndices () {
		return (heaps&HEAPS_FLAGS_4BYTE_GUID_HEAP)==HEAPS_FLAGS_4BYTE_GUID_HEAP ? SIZE_OF_LARGE_INDEX : SIZE_OF_SMALL_INDEX;
	}

	public byte getByteSizeOfBlobIndices () {
		return (heaps&HEAPS_FLAGS_4BYTE_BLOB_HEAP)==HEAPS_FLAGS_4BYTE_BLOB_HEAP ? SIZE_OF_LARGE_INDEX : SIZE_OF_SMALL_INDEX;
	}

	public AbstractTable[] getMetadataTable() {
		return metadataTable;
	}

	public boolean isUnoptimized() {
		return isUnoptimized;
	}

	public int getWidthOfMaxRid() {
		return widthOfMaxRid;
	}
	public long getReservedValue() {
		return reservedValue;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int getHeaps() {
		return heaps;
	}

	public long getValidMaskLow() {
		return validMaskLow;
	}

	public long getValidMaskHigh() {
		return validMaskHigh;
	}

	public long getSortedHigh() {
		return sortedHigh;
	}

	public int getByteSize() {
		return byteSize;
	}

	public int[] getEmtpyTablesIndices() {
		return emtpyTablesIndices;
	}

}

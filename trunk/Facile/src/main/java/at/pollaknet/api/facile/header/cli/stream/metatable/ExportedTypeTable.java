package at.pollaknet.api.facile.header.cli.stream.metatable;

/*
 * Checked against 5th edition (December 2010), not sure if typedDef id is handled correctly,
 * since ECMA 335 mentions it as reference to another module.
 */
import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.CodedIndex;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.ExportedTypeEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;
import at.pollaknet.api.facile.util.ByteReader;

public class ExportedTypeTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x27;
	public final static int MIN_ROW_SIZE = 0x0e;
	
	private long typeAttributesFlags [];
	private int typeDefId [];
	private int typeNameStringIndex [];
	private int typeNamespaceStringIndex [];
	private long implementationCodedIndex [];
	
	private byte sizeOfImplementationIndex;

	@Override
	protected void prepareTable(int[] numberOfRows) {
		typeAttributesFlags = new long [rows];
		typeDefId = new int [rows];
		typeNameStringIndex = new int [rows];
		typeNamespaceStringIndex = new int [rows];
		implementationCodedIndex = new long [rows];
		
		sizeOfImplementationIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.Implementation);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		typeAttributesFlags[row] = ByteReader.getUInt32(data, offset);			offset +=4;
		long value = ByteReader.getUInt32(data, offset);						offset +=4;
		assert(value<ByteReader.INT32_MAX_VAL);
		typeDefId[row] = (int) value;					
		
		offset += readStringIndex(data, offset, typeNameStringIndex, row);
		offset += readStringIndex(data, offset, typeNamespaceStringIndex, row);
		offset += readIndex(data, offset, implementationCodedIndex, row, sizeOfImplementationIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" ExportedType Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  TypeAttributes Flags: ");
			buffer.append(typeAttributesFlags[i]);
			buffer.append(";\tTypeDefId: ");
			buffer.append(typeDefId[i]);
			buffer.append(";\tTypeName StringIndex: ");
			buffer.append(typeNameStringIndex[i]);
			buffer.append(";\tTypeNamespace StringIndex: ");
			buffer.append(typeNamespaceStringIndex[i]);
			buffer.append(";\tImpl CodedIndex: ");
			buffer.append(implementationCodedIndex[i]);
			buffer.append(";");
		}
		
		return  buffer.toString();
	}
	

	@Override
	public int getRID() {
		return TABLE_INDEX;
	}

	@Override
	protected void fillRow(MetadataModel model, MetadataStream m, StringsStream s,
			UserStringStream u, GuidStream g, BlobStream b, RenderableCilElement[] entry, int index) {
		ExportedTypeEntry e = (ExportedTypeEntry) entry[index];
		
		e.setFlags(typeAttributesFlags[index]);
		e.setTypeDefId(typeDefId[index]);
		e.setName(s.getString(typeNameStringIndex[index]));
		e.setNamespace(s.getString(typeNamespaceStringIndex[index]));
		e.setImplementation(EntryDecoder.getImplementationEntry(model, implementationCodedIndex[index]));
		
	}

}

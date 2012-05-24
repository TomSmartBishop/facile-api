package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.CodedIndex;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.CustomAttributeEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;

/*
 * Checked against 5th edition (December 2010), not sure if fillRow is 100% compatible.
 */
public class CustomAttributeTable extends AbstractTable{

	//private static final int CUSTOM_ATTRIBUTE_TYPE_IS_STORED_AS_STRING = 4;
	public final static int TABLE_INDEX = 0x0c;
	public final static int MIN_ROW_SIZE = 0x06;
	
	private long parentCodedIndex [];
	private int typeCodedIndex [];
	private int valueBlobIndex [];

	private byte parentIndexSize;
	private byte typeIndexSize;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		parentCodedIndex = new long [rows];
		typeCodedIndex = new int [rows];
		valueBlobIndex = new int [rows];

		parentIndexSize = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.HasCustomAttribute);
		
		typeIndexSize = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.CustomAttributeType);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		offset += readIndex(data, offset, parentCodedIndex, row, parentIndexSize);
		offset += readIndex(data, offset, typeCodedIndex, row, typeIndexSize);
		offset += readBlobIndex(data, offset, valueBlobIndex, row);

		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" CustomAttribute Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Parent CodedIndex: ");
			buffer.append(parentCodedIndex[i]);
			buffer.append(";\tType CodedIndex: ");
			buffer.append(typeCodedIndex[i]);
			buffer.append(";\tValue BlobIndex: ");
			buffer.append(valueBlobIndex[i]);
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
		CustomAttributeEntry e = (CustomAttributeEntry) entry[index];
		
		e.setOwner(EntryDecoder.getHasCustomAttributeEntry(model, parentCodedIndex[index]));
		e.setCustomAttributeType(EntryDecoder.getCustomAttributeTypeEntry(model, typeCodedIndex[index]));	
		e.setValue(b.getBlob(valueBlobIndex[index]));
		e.setBinaryBlobIndex(valueBlobIndex[index]);
	}
	
}

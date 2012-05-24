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
import at.pollaknet.api.facile.metamodel.entries.FieldMarshalEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;

/*
 * Checked against 5th edition (December 2010).
 */
public class FieldMarshalTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x0d;
	public final static int MIN_ROW_SIZE = 0x04;
	
	private long parentCodedIndex [];
	private int nativeTypeBlobIndex [];

	private byte parentIndexSize;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		parentCodedIndex = new long [rows];
		nativeTypeBlobIndex = new int [rows];

		parentIndexSize = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.HasFieldMarshall);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		offset += readIndex(data, offset, parentCodedIndex, row, parentIndexSize);
		offset += readBlobIndex(data, offset, nativeTypeBlobIndex, row);

		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" FieldMarshal Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Parent CodedIndex: ");
			buffer.append(parentCodedIndex[i]);
			buffer.append(";\tNativeTye BlobIndex: ");
			buffer.append(nativeTypeBlobIndex[i]);
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
		FieldMarshalEntry e = (FieldMarshalEntry) entry[index];
		
		e.setParent(EntryDecoder.getHasFieldMarshallEntry(model, parentCodedIndex[index]));
		e.setNativeType(b.getBlob(nativeTypeBlobIndex[index]));
		
	}
}

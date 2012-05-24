package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.FieldRVAEntry;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010), could involve warning output.
 */
public class FieldRVATable extends AbstractTable {
	
	public final static int TABLE_INDEX = 0x1d;
	public final static int MIN_ROW_SIZE = 0x06;
	
	private long relativeVirtualAddress [];
	private int fieldFieldIndex [];

	private byte sizeOfFieldIndex; 
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		relativeVirtualAddress = new long [rows];
		fieldFieldIndex = new int [rows];
		
		sizeOfFieldIndex = IndexDecoder.getByteSizeOfTargetRow(numberOfRows, FieldTable.TABLE_INDEX);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		relativeVirtualAddress[row] = ByteReader.getUInt32(data, offset);		offset +=4;
		
		offset += readIndex(data, offset, fieldFieldIndex, row, sizeOfFieldIndex);

		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" FieldRVA Table (" + TABLE_INDEX + "):");

		for(int i=0;i<rows;i++) {
			buffer.append("\n  RelativeVirtualAddress: ");
			buffer.append(relativeVirtualAddress[i]);
			buffer.append(";\tField Index: ");
			buffer.append(fieldFieldIndex[i]);
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
		FieldRVAEntry e = (FieldRVAEntry) entry[index];
		
		e.setRelativeVirtualAddress(relativeVirtualAddress[index]);
		if(fieldFieldIndex[index]>0)
			e.setField(model.field[fieldFieldIndex[index]-1]);	
	}
}

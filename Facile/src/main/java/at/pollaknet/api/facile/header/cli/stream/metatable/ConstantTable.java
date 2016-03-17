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
import at.pollaknet.api.facile.metamodel.entries.ConstantEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010), requires additional warning output.
 */
public class ConstantTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x0b;
	public final static int MIN_ROW_SIZE = 0x06;
	
	private int type []; //1byte (+ 1 padding byte)
	private long parentCodedIndex [];
	private int valueBlobIndex [];

	private byte parentIndexSize;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		type = new int [rows];
		parentCodedIndex = new long [rows];
		valueBlobIndex = new int [rows];

		parentIndexSize = IndexDecoder.getByteSizeOfTargetRow(numberOfRows, CodedIndex.HasConstant);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		//read only 1 byte, but increment by 2 because of the required byte padding
		type[row] = ByteReader.getUInt8(data, offset);	offset +=2;
		
		//check if the padding byte is really 0
		assert(ByteReader.getUInt8(data, offset-1)==0);
		
		offset += readIndex(data, offset, parentCodedIndex, row, parentIndexSize);
		offset += readBlobIndex(data, offset, valueBlobIndex, row);
	
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" Constant Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Type: ");
			buffer.append(type[i]);
			buffer.append(";\tParent CodedIndex: ");
			buffer.append(parentCodedIndex[i]);
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
		ConstantEntry e = (ConstantEntry) entry[index];
		
		e.setType(type[index]);
		e.setParent(EntryDecoder.getHasConstantEntry(model, parentCodedIndex[index]));
		e.setValue(b.getBlob(valueBlobIndex[index]));
		
	}

}

package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;

public class PropertyPtrTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x16;
	public final static int MIN_ROW_SIZE = 0x02;
	
	private int propertyIndex [];
	private byte sizeOfPropertyIndex;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		propertyIndex = new int [rows];
		
		sizeOfPropertyIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, PropertyTable.TABLE_INDEX);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		offset += readIndex(data, offset, propertyIndex, row, sizeOfPropertyIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" Property Ptr Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Property Ptr Index: ");
			buffer.append(propertyIndex[i]);
			buffer.append(";");
		}
		
		return  buffer.toString();
	}
	

	@Override
	public int getRID() {
		return TABLE_INDEX;
	}
	
	public int getPointer(int row) {
		return propertyIndex==null?row+1:propertyIndex[row];
	}

	@Override
	protected void fillRow(MetadataModel model, MetadataStream m, StringsStream s,
			UserStringStream u, GuidStream g, BlobStream b, RenderableCilElement[] entry, int index) {
		
	}

}

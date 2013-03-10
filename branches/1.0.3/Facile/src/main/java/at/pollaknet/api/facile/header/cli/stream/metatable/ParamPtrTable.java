package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;

public class ParamPtrTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x07;
	public final static int MIN_ROW_SIZE = 0x02;
	
	private int paramIndex [];
	private byte sizeOfParamIndex;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		paramIndex = new int [rows];
		
		sizeOfParamIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, ParamTable.TABLE_INDEX);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		offset += readIndex(data, offset, paramIndex, row, sizeOfParamIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" Param Ptr Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Param Ptr Index: ");
			buffer.append(paramIndex[i]);
			buffer.append(";");
		}
		
		return  buffer.toString();
	}
	

	@Override
	public int getRID() {
		return TABLE_INDEX;
	}
	
	public int getPointer(int row) {
		return paramIndex==null?row+1:paramIndex[row];
	}

	@Override
	protected void fillRow(MetadataModel model, MetadataStream m, StringsStream s,
			UserStringStream u, GuidStream g, BlobStream b, RenderableCilElement[] entry, int index) {
		
	}

}

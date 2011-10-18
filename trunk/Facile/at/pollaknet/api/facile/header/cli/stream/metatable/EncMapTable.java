package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.util.ByteReader;

public class EncMapTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x1f;
	public final static int MIN_ROW_SIZE = 0x08;
	
	private long token [];
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		token = new long [rows];
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		token[row] = ByteReader.getUInt64(data, offset);
		offset +=8;
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" EncMap Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Token: ");
			buffer.append(token[i]);
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
		
	}

}

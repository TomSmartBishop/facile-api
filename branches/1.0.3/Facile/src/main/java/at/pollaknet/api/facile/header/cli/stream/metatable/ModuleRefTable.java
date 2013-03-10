package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.ModuleRefEntry;

/*
 * Checked against 5th edition (December 2010).
 */
public class ModuleRefTable extends AbstractTable {
	
	public final static int TABLE_INDEX = 0x1a;
	public final static int MIN_ROW_SIZE = 0x02;

	private int nameStringIndex [];
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		nameStringIndex = new int[rows];	
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		
		int rowSize = readStringIndex(data, offset, nameStringIndex, row);
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" ModuleRef Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Name StringIndex: ");
			buffer.append(nameStringIndex[i]);
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
		ModuleRefEntry e = (ModuleRefEntry) entry[index];

		e.setName(s.getString(nameStringIndex[index]));
		
	}
}
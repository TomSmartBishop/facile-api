package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.NestedClassEntry;

public class NestedClassTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x29;
	public final static int MIN_ROW_SIZE = 0x04;

	private int nestedClassTypeDefIndex [];
	private int enclosingClassTypeDefIndex [];
	
	private byte sizeOfTypeDefIndex;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		nestedClassTypeDefIndex = new int[rows];
		enclosingClassTypeDefIndex = new int[rows];
		
		sizeOfTypeDefIndex  =IndexDecoder.getByteSizeOfTargetRow(numberOfRows, TypeDefTable.TABLE_INDEX);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		offset += readIndex(data, offset, nestedClassTypeDefIndex, row, sizeOfTypeDefIndex);
		offset += readIndex(data, offset, enclosingClassTypeDefIndex, row, sizeOfTypeDefIndex);

		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" NestedClass Table (" + TABLE_INDEX + "):");
	
		for(int i=0;i<rows;i++) {
			buffer.append("\n  NestedClass Index: ");
			buffer.append(nestedClassTypeDefIndex[i]);
			buffer.append("\tEnclosingClass Index: ");
			buffer.append(enclosingClassTypeDefIndex[i]);
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
		
		NestedClassEntry e = (NestedClassEntry) entry[index];

		if(nestedClassTypeDefIndex[index]>0) e.setNestedClass(model.typeDef[nestedClassTypeDefIndex[index]-1]);
		if(enclosingClassTypeDefIndex[index]>0) e.setEnclosingClass(model.typeDef[enclosingClassTypeDefIndex[index]-1]);
		
	}

}

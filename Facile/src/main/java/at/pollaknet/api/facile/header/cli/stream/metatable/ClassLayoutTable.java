package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.ClassLayoutEntry;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010), fillRow requires update!
 */
public class ClassLayoutTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x0f;
	public final static int MIN_ROW_SIZE = 0x08;
	
	private int packingSize [];
	private long classSize [];
	private int parentTypeDefIndex [];

	private byte parentIndexSize;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		packingSize = new int [rows];
		classSize = new long [rows];
		parentTypeDefIndex = new int [rows];

		parentIndexSize = IndexDecoder.getByteSizeOfTargetRow(numberOfRows, TypeDefTable.TABLE_INDEX );
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		packingSize[row] = ByteReader.getUInt16(data, offset);						offset +=2;
		classSize[row] = ByteReader.getUInt32(data, offset);						offset +=4;

		offset += readIndex(data, offset, parentTypeDefIndex, row, parentIndexSize);

		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" ClassLayout Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  PackingSize: ");
			buffer.append(packingSize[i]);
			buffer.append("\tClassSize: ");
			buffer.append(classSize[i]);
			buffer.append(";\tParent Index: ");
			buffer.append(parentTypeDefIndex[i]);
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
		ClassLayoutEntry e = (ClassLayoutEntry) entry[index];
		
		//FIXME: Propagation to higher level class is required!
		e.setPackingSize(packingSize[index]);
		e.setClassSize(classSize[index]);
		if(parentTypeDefIndex[index]>0)
			e.setParent(model.typeDef[parentTypeDefIndex[index]-1]);
	}

}

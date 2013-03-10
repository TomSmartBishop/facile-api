package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.PropertyEntry;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010).
 */
public class PropertyTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x17;
	public final static int MIN_ROW_SIZE = 0x06;

	private int propertyAttributeFlags [];
	private int nameStringIndex [];
	private int typeBlobIndex [];
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		propertyAttributeFlags = new int [rows];
		nameStringIndex = new int [rows];
		typeBlobIndex = new int [rows];
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		propertyAttributeFlags[row] = ByteReader.getUInt16(data, offset);	offset +=2;
		
		offset += readStringIndex(data, offset, nameStringIndex, row);
		offset += readBlobIndex(data, offset, typeBlobIndex, row);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" Property Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  PropertyAttribute Flags: ");
			buffer.append(propertyAttributeFlags[i]);
			buffer.append("\tName StringIndex: ");
			buffer.append(nameStringIndex[i]);
			buffer.append("\tType BlobIndex: ");
			buffer.append(typeBlobIndex[i]);
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
		PropertyEntry e = (PropertyEntry) entry[index];
	
		e.setFlags(propertyAttributeFlags[index]);
		e.setName(s.getString(nameStringIndex[index]));
		e.setTypeSignature(b.getBlob(typeBlobIndex[index]));
	}
	
}

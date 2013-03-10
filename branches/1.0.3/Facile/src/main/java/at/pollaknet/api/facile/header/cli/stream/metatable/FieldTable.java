package at.pollaknet.api.facile.header.cli.stream.metatable;

/*
 * Checked against 5th edition (December 2010).
 */
import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.FieldEntry;
import at.pollaknet.api.facile.util.ByteReader;

public class FieldTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x04;
	public final static int MIN_ROW_SIZE = 0x06;
	
	private int fieldAttributesFlags [];
	private int nameStringIndex [];
	private int signatureBlobIndex [];
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		fieldAttributesFlags = new int [rows];
		nameStringIndex = new int [rows];
		signatureBlobIndex = new int [rows];
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		fieldAttributesFlags[row] = ByteReader.getUInt16(data, offset);		offset +=2;
		
		assert((fieldAttributesFlags[row]&0x4800)==0);
		
		offset += readStringIndex(data, offset, nameStringIndex, row);
		offset += readBlobIndex(data, offset, signatureBlobIndex, row);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" Field Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  FieldAttributes Flags: ");
			buffer.append(fieldAttributesFlags[i]);
			buffer.append(";\tName StringIndex: ");
			buffer.append(nameStringIndex[i]);
			buffer.append(";\tSignature BlobIndex: ");
			buffer.append(signatureBlobIndex[i]);
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

		FieldEntry e = (FieldEntry) entry[index];

		e.setFlags(fieldAttributesFlags[index]);
		
		//do exceptional handling of compiler generated backing fields (auto generated property)
		String fieldName = s.getString(nameStringIndex[index]);
		if(fieldName!=null)
		{
			if(fieldName.endsWith(">k__BackingField"))
				e.setName("'"+fieldName+"'");
			else
				e.setName(fieldName);
		}
		
		e.setBinarySignature(b.getBlob(signatureBlobIndex[index]));	
	
	}
}

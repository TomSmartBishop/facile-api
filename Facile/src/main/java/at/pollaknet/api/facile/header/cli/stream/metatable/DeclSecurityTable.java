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
import at.pollaknet.api.facile.metamodel.entries.DeclSecurityEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010).
 */
public class DeclSecurityTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x0e;
	public final static int MIN_ROW_SIZE = 0x06;
	
	private int action [];
	private long parentCodedIndex [];
	private int permissionSetBlobIndex [];

	private byte parentIndexSize;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		action = new int [rows];
		parentCodedIndex = new long [rows];
		permissionSetBlobIndex = new int [rows];

		parentIndexSize = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.HasDeclSecurity);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		action[row] = ByteReader.getUInt16(data, offset);
		offset +=2;
		
		offset += readIndex(data, offset, parentCodedIndex, row, parentIndexSize);
		offset += readBlobIndex(data, offset, permissionSetBlobIndex, row);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" DeclSecurity Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Action: ");
			buffer.append(action[i]);
			buffer.append("\tParent CodedIndex: ");
			buffer.append(parentCodedIndex[i]);
			buffer.append(";\tPermissionSet BlobIndex: ");
			buffer.append(permissionSetBlobIndex[i]);
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
		DeclSecurityEntry e = (DeclSecurityEntry) entry[index];
		
		e.setAction(action[index]);
		e.setParent(EntryDecoder.getHasDeclSecurityEntry(model, parentCodedIndex[index]));
		e.setPermissionSet(b.getBlob(permissionSetBlobIndex[index]));
		e.setBinaryBlobIndex(permissionSetBlobIndex[index]);
	}
	
}

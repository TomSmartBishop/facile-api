package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.FileEntry;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010).
 */
public class FileTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x26;
	public final static int MIN_ROW_SIZE = 0x08;
	
	private long fileAttributeFlags [];
	private int nameStringIndex [];
	private int hashValueBlobIndex [];
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		fileAttributeFlags = new long [rows];
		nameStringIndex = new int [rows];
		hashValueBlobIndex = new int [rows];
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		fileAttributeFlags[row] = ByteReader.getUInt32(data, offset);		offset +=4;

		offset += readStringIndex(data, offset, nameStringIndex, row);
		offset += readBlobIndex(data, offset, hashValueBlobIndex, row);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" File Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  FileAttribute Flags: ");
			buffer.append(fileAttributeFlags[i]);
			buffer.append(";\tName StringIndex: ");
			buffer.append(nameStringIndex[i]);
			buffer.append(";\tHashValue BlobIndex: ");
			buffer.append(hashValueBlobIndex[i]);
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
		
		FileEntry e = (FileEntry) entry[index];

		e.setFlags(fileAttributeFlags[index]);
		e.setName(s.getString(nameStringIndex[index]));
		e.setHashValue(b.getBlob(hashValueBlobIndex[index]));
		
	}

}

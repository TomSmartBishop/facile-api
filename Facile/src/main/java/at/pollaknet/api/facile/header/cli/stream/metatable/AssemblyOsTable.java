package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.AssemblyOsEntry;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010)
 */
public class AssemblyOsTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x22;
	public final static int MIN_TABLE_SIZE = 0x0c;

	private long platformID [];
	private long majorVersion [];
	private long minorVersion [];
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		platformID = new long[rows];
		majorVersion = new long[rows];
		minorVersion = new long[rows];
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		platformID[row] = ByteReader.getUInt32(data, offset);	offset +=4;
		majorVersion[row] = ByteReader.getUInt32(data, offset);	offset +=4;
		minorVersion[row] = ByteReader.getUInt32(data, offset);	offset +=4;
		
		rowSize = offset - rowSize;
		
		assert(rowSize == MIN_TABLE_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" AssemblyOS Table (" + TABLE_INDEX + "):");
	
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Platform ID: ");
			buffer.append(platformID[i]);
			buffer.append("\tMajorVersion: ");
			buffer.append(majorVersion[i]);
			buffer.append("\tMinorVersion: ");
			buffer.append(minorVersion[i]);
			buffer.append(";");
		}
		
		return  buffer.toString();
	}
	

	@Override
	public int getRID() {
		return TABLE_INDEX;
	}

	@Override
	protected void fillRow(MetadataModel model,  MetadataStream m, StringsStream s,
			UserStringStream u, GuidStream g, BlobStream b, RenderableCilElement[] entry, int index) {
		AssemblyOsEntry e = (AssemblyOsEntry) entry[index];
		
		e.setOsPlatformId(platformID[index]);
		e.setOsMajorVersion(majorVersion[index]);
		e.setOsMinorVersion(minorVersion[index]);
	}

}

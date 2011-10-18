package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.StandAloneSigEntry;

/*
 * Checked against 5th edition (December 2010).
 */
public class StandAloneSigTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x11;
	public final static int MIN_ROW_SIZE = 0x02;
	
	private int signatureBlobIndex [];
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		signatureBlobIndex = new int [rows];
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		offset += readBlobIndex(data, offset, signatureBlobIndex, row);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" StandAloneSig Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Signature BlobIndex: ");
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
		
		StandAloneSigEntry e = (StandAloneSigEntry) entry[index];
		
		e.setSignature(b.getBlob(signatureBlobIndex[index]));
	}
}

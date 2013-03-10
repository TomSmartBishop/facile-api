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
import at.pollaknet.api.facile.metamodel.entries.MethodSpecEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;

/*
 * Checked against 5th edition (December 2010).
 */
public class MethodSpecTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x2b;
	public final static int MIN_ROW_SIZE = 0x04;

	private long methodCodedIndex [];
	private int instantiationBlobIndex [];
	
	private byte sizeOfMethodIndex;

	@Override
	protected void prepareTable(int[] numberOfRows) {
		methodCodedIndex = new long[rows];
		instantiationBlobIndex = new int[rows];
		
		sizeOfMethodIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.MethodDefOrRef);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		offset += readIndex(data, offset, methodCodedIndex, row, sizeOfMethodIndex);
		offset += readBlobIndex(data, offset, instantiationBlobIndex, row);

		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" MethodSpec Table (" + TABLE_INDEX + "):");

		for(int i=0;i<rows;i++) {
			buffer.append("\n  Method CodedIndex: ");
			buffer.append(methodCodedIndex[i]);
			buffer.append("\tInstantiation BlobIndex: ");
			buffer.append(instantiationBlobIndex[i]);
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

		MethodSpecEntry e = (MethodSpecEntry) entry[index];

		e.setMethod(EntryDecoder.getMethodDefOrRefEntry(model, methodCodedIndex[index]));
		e.setInstantiation(b.getBlob(instantiationBlobIndex[index]));
	}

}

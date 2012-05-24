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
import at.pollaknet.api.facile.metamodel.entries.MemberRefEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;

/*
 * Checked against 5th edition (December 2010).
 */
public class MemberRefTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x0a;
	public final static int MIN_ROW_SIZE = 0x06;
	
	private long classCodedIndex [];
	private int nameStringIndex [];
	private int signatureBlobIndex [];

	private byte sizeOfClassIndex;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		classCodedIndex = new long [rows];
		nameStringIndex = new int [rows];
		signatureBlobIndex = new int [rows];
		
		sizeOfClassIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.MemberRefParent);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		offset += readIndex(data, offset, classCodedIndex, row, sizeOfClassIndex);
		offset += readStringIndex(data, offset, nameStringIndex, row);
		offset += readBlobIndex(data, offset, signatureBlobIndex, row);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" MemberRef Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Class CodedIndex: ");
			buffer.append(classCodedIndex[i]);
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
		MemberRefEntry e = (MemberRefEntry) entry[index];

		e.setOwnerClass(EntryDecoder.getMemberRefParentEntry(model, classCodedIndex[index]));
		e.setName(s.getString(nameStringIndex[index]));
		e.setBinarySignature(b.getBlob(signatureBlobIndex[index]));
		
	}
}

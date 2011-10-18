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
import at.pollaknet.api.facile.metamodel.entries.MethodSemanticsEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010).
 */
public class MethodSemanticsTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x18;
	public final static int MIN_ROW_SIZE = 0x06;

	private int methodSemanticsAttributeFlags [];
	private int methodMethodDefIndex [];
	private long associationCodedIndex [];
	
	private byte sizeOfMethodDefIndex;
	private byte sizeOfAssociationIndex;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		methodSemanticsAttributeFlags = new int [rows];
		methodMethodDefIndex = new int [rows];
		associationCodedIndex = new long [rows];
		
		sizeOfMethodDefIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, MethodDefTable.TABLE_INDEX);
		sizeOfAssociationIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.HasSemantics);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		methodSemanticsAttributeFlags[row] = ByteReader.getUInt16(data, offset);	offset +=2;
		
		offset += readIndex(data, offset, methodMethodDefIndex, row, sizeOfMethodDefIndex);
		offset += readIndex(data, offset, associationCodedIndex, row, sizeOfAssociationIndex);

		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" MethodSemantics Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  MethodSemanticsAttribute Flags: ");
			buffer.append(methodSemanticsAttributeFlags[i]);
			buffer.append("\tMethod Index: ");
			buffer.append(methodMethodDefIndex[i]);
			buffer.append("\tAssociation CodedIndex: ");
			buffer.append(associationCodedIndex[i]);
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
		
		MethodSemanticsEntry e = (MethodSemanticsEntry) entry[index];

		e.setSemantics(methodSemanticsAttributeFlags[index]);
		if(methodMethodDefIndex[index]>0) e.setMethod(model.methodDef[methodMethodDefIndex[index]-1]);
		e.setAssociation(EntryDecoder.getHasSemanticsEntry(model, associationCodedIndex[index]));
		
	}
}

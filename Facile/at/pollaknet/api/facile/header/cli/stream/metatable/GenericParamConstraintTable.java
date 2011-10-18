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
import at.pollaknet.api.facile.metamodel.entries.GenericParamConstraintEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;

/*
 * Checked against 5th edition (December 2010).
 */
public class GenericParamConstraintTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x2c;
	public final static int MIN_ROW_SIZE = 0x04;

	private int ownerGenericParamIndex [];
	private long constraintCodedIndex [];

	private byte sizeOfOwnerIndex;
	private byte sizeOfConstraintIndex;

	@Override
	protected void prepareTable(int[] numberOfRows) {
		ownerGenericParamIndex = new int [rows];
		constraintCodedIndex = new long [rows];
		
		sizeOfOwnerIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, GenericParamTable.TABLE_INDEX);
		
		sizeOfConstraintIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.TypeDefOrRef);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		offset += readIndex(data, offset, ownerGenericParamIndex, row, sizeOfOwnerIndex);
		offset += readIndex(data, offset, constraintCodedIndex, row, sizeOfConstraintIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" GenericParamConstraint Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Owner Index: ");
			buffer.append(ownerGenericParamIndex[i]);
			buffer.append(";\tConstraint CodedIndex: ");
			buffer.append(constraintCodedIndex[i]);
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
		
		GenericParamConstraintEntry e = (GenericParamConstraintEntry) entry[index];

		if(ownerGenericParamIndex[index]>0) e.setOwner(model.genericParam[ownerGenericParamIndex[index]-1]);
		e.setConstraint(EntryDecoder.getTypeDefOrRefEntry(model, constraintCodedIndex[index]));
	}

}

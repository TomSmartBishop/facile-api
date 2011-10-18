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
import at.pollaknet.api.facile.metamodel.entries.GenericParamEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010).
 */
public class GenericParamTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x2a;
	public final static int MIN_ROW_SIZE = 0x08;
	
	private int number [];
	private int paramAttributeFlags [];
	private int ownerCodedIndex [];
	private int nameStringIndex [];
	private int methodCodedIndex [];
	
	private byte sizeOfOwnerIndex;
	private byte sizeOfMethodIndex;

	@Override
	protected void prepareTable(int[] numberOfRows) {
		number = new int [rows];
		paramAttributeFlags = new int [rows];
		ownerCodedIndex = new int [rows];
		nameStringIndex = new int [rows];
		methodCodedIndex = new int [rows];
		
		sizeOfOwnerIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.TypeOrMethodDef);
		sizeOfMethodIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.TypeDefOrRef);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		number[row] = ByteReader.getUInt16(data, offset);					offset +=2;
		paramAttributeFlags[row] = ByteReader.getUInt16(data, offset);		offset +=2;
	
		offset += readIndex(data, offset, ownerCodedIndex, row, sizeOfOwnerIndex);
		offset += readStringIndex(data, offset, nameStringIndex, row);
		
		if(isVersion_1_1_MetadataStream) {
			offset += readIndex(data, offset, methodCodedIndex, row, sizeOfMethodIndex);
		}
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" GenericParam Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Number: ");
			buffer.append(number[i]);
			buffer.append(";\tParamAttribute Flags: ");
			buffer.append(paramAttributeFlags[i]);
			buffer.append(";\tOwner CodedIndex: ");
			buffer.append(ownerCodedIndex[i]);
			buffer.append(";\tName StringIndex: ");
			buffer.append(nameStringIndex[i]);
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
		
		GenericParamEntry e = (GenericParamEntry) entry[index];

		e.setNumber(number[index]);
		e.setFlags(paramAttributeFlags[index]);
		e.setOwner(EntryDecoder.getTypeOrMethodDefEntry(model, ownerCodedIndex[index]));
		e.setName(s.getString(nameStringIndex[index]));
		
		if(isVersion_1_1_MetadataStream) {
			e.setMethod(EntryDecoder.getTypeOrMethodDefEntry(model, methodCodedIndex[index]));
		}
	}

}

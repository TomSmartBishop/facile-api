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
import at.pollaknet.api.facile.metamodel.entries.InterfaceImplEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;

/*
 * Checked against 5th edition (December 2010).
 */
public class InterfaceImplTable extends AbstractTable {
	
	public final static int TABLE_INDEX = 0x09;
	public final static int MIN_ROW_SIZE = 0x04;
	
	private int classTypeDefIndex [];
	private int interfaceCodedIndex [];
	
	private byte sizeOfTypeDefIndex;
	private byte sizeOfInterfaceIndex; 

	@Override
	protected void prepareTable(int[] numberOfRows) {
		classTypeDefIndex = new int [rows];
		interfaceCodedIndex = new int [rows];
		
		sizeOfTypeDefIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, TypeDefTable.TABLE_INDEX);
		sizeOfInterfaceIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.TypeDefOrRef);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		offset += readIndex(data, offset, classTypeDefIndex, row, sizeOfTypeDefIndex);
		offset += readIndex(data, offset, interfaceCodedIndex, row, sizeOfInterfaceIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" InterfaceImpl Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Class Index: ");
			buffer.append(classTypeDefIndex[i]);
			buffer.append(";\tInterrface CodedIndex: ");
			buffer.append(interfaceCodedIndex[i]);
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
		InterfaceImplEntry e = (InterfaceImplEntry) entry[index];

		if(classTypeDefIndex[index]>0) e.setImplementationClass(model.typeDef[classTypeDefIndex[index]-1]);
		e.setInterface(EntryDecoder.getTypeDefOrRefEntry(model, interfaceCodedIndex[index]));		
		
	}
}

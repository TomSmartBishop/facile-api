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
import at.pollaknet.api.facile.metamodel.entries.MethodImplEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;

/*
 * Checked against 5th edition (December 2010).
 */
public class MethodImplTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x19;
	public final static int MIN_ROW_SIZE = 0x06;

	private int classTypeDefIndex [];
	private long methodBodyCodedIndex [];
	private long methodDeclarationCodedIndex [];
	
	private byte sizeOfTypeDefIndex;
	private byte sizeOfMethodBodyDeclIndex;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		classTypeDefIndex = new int [rows];
		methodBodyCodedIndex = new long [rows];
		methodDeclarationCodedIndex = new long [rows];
		
		sizeOfTypeDefIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, TypeDefTable.TABLE_INDEX);
		sizeOfMethodBodyDeclIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.MethodDefOrRef);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		offset += readIndex(data, offset, classTypeDefIndex, row, sizeOfTypeDefIndex);
		offset += readIndex(data, offset, methodBodyCodedIndex, row, sizeOfMethodBodyDeclIndex);
		
		offset += readIndex(data, offset, methodDeclarationCodedIndex, row, sizeOfMethodBodyDeclIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" MethodImpl Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Class Index: ");
			buffer.append(classTypeDefIndex[i]);
			buffer.append("\tMethodBody CodedIndex: ");
			buffer.append(methodBodyCodedIndex[i]);
			buffer.append("\tMethodDeclaration CodedIndex: ");
			buffer.append(methodDeclarationCodedIndex[i]);
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

		MethodImplEntry e = (MethodImplEntry) entry[index];

		if(classTypeDefIndex[index]>0) e.setOwnerClass(model.typeDef[classTypeDefIndex[index]-1]);
		e.setImplementationBody(EntryDecoder.getMethodDefOrRefEntry(model, methodBodyCodedIndex[index]));
		e.setMethodDeclaration(EntryDecoder.getMethodDefOrRefEntry(model, methodDeclarationCodedIndex[index]));
		
	}
}

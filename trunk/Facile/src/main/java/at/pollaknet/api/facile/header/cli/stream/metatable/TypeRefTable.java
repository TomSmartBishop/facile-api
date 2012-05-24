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
import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;

/*
 * Checked against 5th edition (December 2010).
 */
public class TypeRefTable extends AbstractTable {
	
	public final static int TABLE_INDEX = 0x01;
	public final static int MIN_ROW_SIZE = 0x06;
	
	private long resolutionScopeCodedIndex[];
	private int typeNameStringIndex[];
	private int typeNamespaceStringIndex[];

	private byte sizeOfResScopeIndex;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		resolutionScopeCodedIndex = new long [rows];
		typeNameStringIndex = new int [rows];
		typeNamespaceStringIndex = new int [rows];
		
		sizeOfResScopeIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.ResolutionScope );
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		
		int rowSize = offset;
					
		offset += readIndex(data, offset, resolutionScopeCodedIndex, row, sizeOfResScopeIndex);
		
		offset += readStringIndex(data, offset, typeNameStringIndex, row);
		offset += readStringIndex(data, offset, typeNamespaceStringIndex, row);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	public String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" TypeRef Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  ResolutionScope CodedIndex: ");
			buffer.append(resolutionScopeCodedIndex[i]);
			buffer.append(";\tTypeName StringIndex: ");
			buffer.append(typeNameStringIndex[i]);
			buffer.append(";\tTypeNameSpace StringIndex: ");
			buffer.append(typeNamespaceStringIndex[i]);
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
		
		TypeRefEntry e = (TypeRefEntry) entry[index];
		
		e.setResolutionScope(EntryDecoder.getResolutionScopeEntry(model, resolutionScopeCodedIndex[index]));
		e.setName(s.getString(typeNameStringIndex[index]));
		e.setNamespace(s.getString(typeNamespaceStringIndex[index]));
		
	}
}

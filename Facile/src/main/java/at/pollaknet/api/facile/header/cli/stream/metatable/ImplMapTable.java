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
import at.pollaknet.api.facile.metamodel.entries.ImplMapEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010).
 */
public class ImplMapTable extends AbstractTable {
	
	public final static int TABLE_INDEX = 0x1c;
	public final static int MIN_ROW_SIZE = 0x08;
	
	private int pInvokeAttributeFlags [];
	private long memberForwardedCodedIndex [];
	private int importNameStringIndex [];
	private int importScopeModuleRefIndex [];
	
	private byte sizeOfMemberForwardedIndex;
	private byte sizeOfModuleRefIndex; 
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		pInvokeAttributeFlags = new int [rows];
		memberForwardedCodedIndex = new long [rows];
		importNameStringIndex = new int [rows];
		importScopeModuleRefIndex = new int [rows];
		
		sizeOfModuleRefIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, ModuleRefTable.TABLE_INDEX);
		sizeOfMemberForwardedIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.MemberForwarded);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		//also known as MappingFlags
		pInvokeAttributeFlags[row] = ByteReader.getUInt16(data, offset);	offset +=2;
		
		offset += readIndex(data, offset, memberForwardedCodedIndex, row, sizeOfMemberForwardedIndex);
		offset += readStringIndex(data, offset, importNameStringIndex, row);
		offset += readIndex(data, offset, importScopeModuleRefIndex, row, sizeOfModuleRefIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" ImplMap Table (" + TABLE_INDEX + "):");

		for(int i=0;i<rows;i++) {
			buffer.append("\n  PInvokeAttribute Flags: ");
			buffer.append(pInvokeAttributeFlags[i]);
			buffer.append(";\tMemberForwarded CodedIndex: ");
			buffer.append(memberForwardedCodedIndex[i]);
			buffer.append(";\tName StringIndex: ");
			buffer.append(importNameStringIndex[i]);
			buffer.append(";\tImportScope Index: ");
			buffer.append(importScopeModuleRefIndex[i]);
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

		ImplMapEntry e = (ImplMapEntry) entry[index];

		e.setMappingFlags(pInvokeAttributeFlags[index]);
		e.setMemberForwarded(EntryDecoder.getMemberForwardedEntry(model, memberForwardedCodedIndex[index]));
		e.setImportName(s.getString(importNameStringIndex[index]));
		if(importScopeModuleRefIndex[index]>0) e.setImportScope(model.moduleRef[importScopeModuleRefIndex[index]-1]);
		
	}
}

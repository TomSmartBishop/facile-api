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
import at.pollaknet.api.facile.metamodel.entries.ManifestResourceEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010).
 */
public class ManifestResourceTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x28;
	public final static int MIN_ROW_SIZE = 0x0c;
	
	private long manifestOffset [];
	private long resourceAttributeFlags [];
	private int nameStringIndex [];
	private long implementationCodedIndex [];
	
	private byte sizeOfImplementationIndex;

	@Override
	protected void prepareTable(int[] numberOfRows) {
		manifestOffset = new long [rows];
		resourceAttributeFlags = new long [rows];
		nameStringIndex = new int [rows];
		implementationCodedIndex = new long [rows];
		
		sizeOfImplementationIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.Implementation);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		manifestOffset[row] = ByteReader.getUInt32(data, offset);				offset +=4;
		resourceAttributeFlags[row] = ByteReader.getUInt32(data, offset);		offset +=4;
		
		offset += readStringIndex(data, offset, nameStringIndex, row);
		offset += readIndex(data, offset, implementationCodedIndex, row, sizeOfImplementationIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" ManifestResource Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Offset: ");
			buffer.append(manifestOffset[i]);
			buffer.append(";\tResourceAttribute Flags: ");
			buffer.append(resourceAttributeFlags[i]);
			buffer.append(";\tName StringIndex: ");
			buffer.append(nameStringIndex[i]);
			buffer.append(";\tImpl CodedIndex: ");
			buffer.append(implementationCodedIndex[i]);
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
		ManifestResourceEntry e = (ManifestResourceEntry) entry[index];

		e.setOffset(manifestOffset[index]);
		e.setFlags(resourceAttributeFlags[index]);
		if(s==null) {
			e.setName(model.getAlternativeModuleName());
		} else {
			e.setName(s.getString(nameStringIndex[index]));
		}
		e.setImplementation(EntryDecoder.getImplementationEntry(model, implementationCodedIndex[index]));		

	}

}

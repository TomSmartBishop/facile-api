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
import at.pollaknet.api.facile.metamodel.entries.EventEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010).
 */
public class EventTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x14;
	public final static int MIN_ROW_SIZE = 0x06;
	
	private int eventAttributesFlags [];
	private int nameStringIndex [];
	private long eventTypeCodedIndex [];
	
	private byte sizeOfEventTypeIndex;

	@Override
	protected void prepareTable(int[] numberOfRows) {
		eventAttributesFlags = new int [rows];
		nameStringIndex = new int [rows];
		eventTypeCodedIndex = new long [rows];
		
		sizeOfEventTypeIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.TypeDefOrRef);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		eventAttributesFlags[row] = ByteReader.getUInt16(data, offset);	
		offset +=2;

		//See ECMA 335, Part.II 22.13 and 23.1.4
		assert((eventAttributesFlags[row]&0xf9ff)==0);
		
		offset += readStringIndex(data, offset, nameStringIndex, row);
		offset += readIndex(data, offset, eventTypeCodedIndex, row, sizeOfEventTypeIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" Event Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  EventAttributes Flags: ");
			buffer.append(eventAttributesFlags[i]);
			buffer.append(";\tName StringIndex: ");
			buffer.append(nameStringIndex[i]);
			buffer.append(";\tEventType CodedIndex: ");
			buffer.append(eventTypeCodedIndex[i]);
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
		
		EventEntry e = (EventEntry) entry[index];
		
		e.setEventFlags(eventAttributesFlags[index]);
		e.setName(s.getString(nameStringIndex[index]));
		e.setEventType(EntryDecoder.getTypeDefOrRefEntry(model, eventTypeCodedIndex[index]));
		
	}

}

package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.EventEntry;
import at.pollaknet.api.facile.metamodel.entries.EventMapEntry;

/*
 * Checked against 5th edition (December 2010).
 */
public class EventMapTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x12;
	public final static int MIN_ROW_SIZE = 0x04;
	
	private int parentTypeDefIndex [];
	private int eventListEventIndex [];

	private byte sizeOfTypeDefIndex;
	private byte sizeOfEventIndex;

	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		parentTypeDefIndex = new int [rows];
		eventListEventIndex = new int [rows];
		
		sizeOfTypeDefIndex = IndexDecoder.getByteSizeOfTargetRow(numberOfRows, TypeDefTable.TABLE_INDEX);
		sizeOfEventIndex = IndexDecoder.getByteSizeOfTargetRow(numberOfRows, EventTable.TABLE_INDEX);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		offset += readIndex(data, offset, parentTypeDefIndex, row, sizeOfTypeDefIndex);
		offset += readIndex(data, offset, eventListEventIndex, row, sizeOfEventIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" EventMap Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Parent Index: ");
			buffer.append(parentTypeDefIndex[i]);
			buffer.append("\tEventList Index: ");
			buffer.append(eventListEventIndex[i]);
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
		EventMapEntry e = (EventMapEntry) entry[index];

		if(parentTypeDefIndex[index]>0)
			e.setParent(model.typeDef[parentTypeDefIndex[index]-1]);
		
		EventPtrTable eventPtr = (EventPtrTable) m.getMetadataTable()[EventPtrTable.TABLE_INDEX];
		
		int offset = eventListEventIndex[index];
		
		if(offset>0 && offset<=model.event.length) {
			int length = (eventListEventIndex.length==index+1) ? model.event.length+1 : eventListEventIndex[index+1];
			length -= offset;
			
			EventEntry events [] = new EventEntry[length];
			for(int i=0;i<length;i++){
				if(m.isUnoptimized()) {
					events[i] = model.event[eventPtr.getPointer(offset-1 + i)-1];
				} else {
					events[i] = model.event[offset-1 + i];
				}
			}
			e.setEvents(events);
		}
	}
}

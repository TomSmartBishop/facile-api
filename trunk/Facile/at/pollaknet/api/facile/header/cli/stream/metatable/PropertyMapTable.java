package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.PropertyEntry;
import at.pollaknet.api.facile.metamodel.entries.PropertyMapEntry;

/*
 * Checked against 5th edition (December 2010), fillRow requires documentation.
 */
public class PropertyMapTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x15;
	public final static int MIN_ROW_SIZE = 0x04;
	
	private int parentTypeDefIndex [];
	private int propertyListPropertyIndex [];

	private byte sizeOfTypeDefIndex;
	private byte sizeOfPropertyIndex;

	@Override
	protected void prepareTable(int[] numberOfRows) {
		parentTypeDefIndex = new int [rows];
		propertyListPropertyIndex = new int [rows];
		
		sizeOfTypeDefIndex = IndexDecoder.getByteSizeOfTargetRow(numberOfRows, TypeDefTable.TABLE_INDEX);
		sizeOfPropertyIndex = IndexDecoder.getByteSizeOfTargetRow(numberOfRows, PropertyTable.TABLE_INDEX);
		
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		offset += readIndex(data, offset, parentTypeDefIndex, row, sizeOfTypeDefIndex);
		offset += readIndex(data, offset, propertyListPropertyIndex, row, sizeOfPropertyIndex);

		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" PropertyMap Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Parent Index: ");
			buffer.append(parentTypeDefIndex[i]);
			buffer.append("\tPropertyList Index: ");
			buffer.append(propertyListPropertyIndex[i]);
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
		PropertyMapEntry e = (PropertyMapEntry) entry[index];
		
		if(parentTypeDefIndex[index]>0)
			e.setParent(model.typeDef[parentTypeDefIndex[index]-1]);
		
		PropertyPtrTable propertyPtr = (PropertyPtrTable) m.getMetadataTable()[PropertyPtrTable.TABLE_INDEX];

		
		int offset = propertyListPropertyIndex[index];
		
		if(offset>0 && offset<=model.property.length) {
			int length = (propertyListPropertyIndex.length==index+1) ? model.property.length+1 : propertyListPropertyIndex[index+1];
			
			//this is required for obfuscated assemblies
			if(length>model.property.length+1)
				length = model.property.length+1;
			
			length -= offset;
			
			
			
			PropertyEntry properties [] = new PropertyEntry[length];
			for(int i=0;i<length;i++){
				if(m.isUnoptimized()) {
					properties[i] = model.property[propertyPtr.getPointer(offset-1 + i)-1];
				} else {
					properties[i] = model.property[offset-1 + i];
				}
			}
			e.setProperties(properties);
		}
	}

}

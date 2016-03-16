package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.AssemblyRefProcessorEntry;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010)
 */
public class AssemblyRefProcessorTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x24;
	public final static int MIN_ROW_SIZE = 0x06;

	private long processor [];
	private int assemblyRefAssemblyRefIndex [];
	
	private byte sizeOfAssemblyRefIndex;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		processor = new long[rows];
		assemblyRefAssemblyRefIndex = new int [rows];
		
		sizeOfAssemblyRefIndex = IndexDecoder.getByteSizeOfTargetRow(numberOfRows, AssemblyRefTable.TABLE_INDEX);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		processor[row] = ByteReader.getUInt32(data, offset);
		offset +=4;
		
		offset += readIndex(data, offset, assemblyRefAssemblyRefIndex, row, sizeOfAssemblyRefIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" AssemblyRefProcessor Table (" + TABLE_INDEX + "):");
	
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Processor: ");
			buffer.append(processor[i]);
			buffer.append(";\tAssemblyRef Index: ");
			buffer.append(assemblyRefAssemblyRefIndex[i]);
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
		AssemblyRefProcessorEntry e = (AssemblyRefProcessorEntry) entry[index];
		
		e.setProcessor(processor[index]);
		if(assemblyRefAssemblyRefIndex[index]>0)
			e.setAssemblyRef(model.assemblyRef[assemblyRefAssemblyRefIndex[index]-1]);
	}

}

package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.AssemblyProcessorEntry;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010)
 */
public class AssemblyProcessorTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x21;
	public final static int MIN_ROW_SIZE = 0x04;

	private long processor [];
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		processor = new long[rows];
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {

		processor[row] = ByteReader.getUInt32(data, offset);

		return 4;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" AssemblyProcesscor Table (" + TABLE_INDEX + "):");
	
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Processor: ");
			buffer.append(processor[i]);
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
		AssemblyProcessorEntry e = (AssemblyProcessorEntry) entry[index];
		
		e.setProcessor(processor[index]);
	}

}

package at.pollaknet.api.facile.header.cli.stream.metatable;

/*
 * Checked against 5th edition (December 2010), param validation requires a check and maybe debug output.
 */
import java.util.logging.Level;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.ParamEntry;
import at.pollaknet.api.facile.util.ByteReader;

public class ParamTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x08;
	public final static int MIN_ROW_SIZE = 0x06;

	private int paramAttributeFlags [];
	private int sequence [];
	private int nameStringIndex [];
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		paramAttributeFlags = new int [rows];
		sequence = new int [rows];
		nameStringIndex = new int [rows];
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		paramAttributeFlags[row] = ByteReader.getUInt16(data, offset);		offset +=2;
		
		//See EMCMA 335, Part.II 22.33 and 23.1.13
		if(paramAttributeFlags[row]==0xcfe0 || (paramAttributeFlags[row]&0xcfec)!=0) {
			Log(Level.WARNING, String.format("Param table: Found reserved param attribute flags 0x%x in row %d", paramAttributeFlags[row], row));
		}
		
		sequence[row] = ByteReader.getUInt16(data, offset);					offset +=2;
		
		offset += readStringIndex(data, offset, nameStringIndex, row);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}
	
	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" TypeSpec Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  ParamAttribute Flags: ");
			buffer.append(paramAttributeFlags[i]);
			buffer.append(";\tSequence (constant): ");
			buffer.append(sequence[i]);
			buffer.append(";\tName StringIndex: ");
			buffer.append(nameStringIndex[i]);
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
		ParamEntry e = (ParamEntry) entry[index];
		
		e.setFlags(paramAttributeFlags[index]);
		e.setSequence(sequence[index]);
		e.setName(s.getString(nameStringIndex[index]));
	}
}

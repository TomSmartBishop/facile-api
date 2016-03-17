package at.pollaknet.api.facile.header.cli.stream.metatable;

import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.ModuleEntry;
import at.pollaknet.api.facile.util.ByteReader;


/*
 * Checked against 5th edition (December 2010), additional warning out would be good.
 */
public class ModuleTable extends AbstractTable {
	
	public final static int TABLE_INDEX = 0x00;
	public final static int MIN_ROW_SIZE = 0x0a;

	private int generation [];
	private int nameStringIndex [];
	private int moduleVersionIdGuidIndex [];
	private int encIdGuidIndex [];
	private int encBaseIdGuidIndex [];
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		generation = new int [rows];
		nameStringIndex = new int [rows];
		moduleVersionIdGuidIndex = new int [rows];
		encIdGuidIndex = new int [rows];
		encBaseIdGuidIndex = new int [rows];
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		//the Module table "should" contain only one row
		if(row!=0) {
			Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
		    logger.log(Level.WARNING, "module table contains multiple entries!");
		}
		
		//is a reserved field and should be zero
		generation[row] = ByteReader.getUInt16(data, offset);	offset +=2;
		
		if(generation[row]!=0) {
			Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
		    logger.log(Level.WARNING, "Unknown value " + generation[row] + " in \"generation\" attribute of the module table (reserved, default 0).");
		}
		
		offset += readStringIndex(data, offset, nameStringIndex, row);
		
		//also known as Mvid
		offset += readGuidIndex(data, offset, moduleVersionIdGuidIndex, row);
		offset += readGuidIndex(data, offset, encIdGuidIndex, row);
		offset += readGuidIndex(data, offset, encBaseIdGuidIndex, row);
		
		//reserved, shall be zero:
		if(encIdGuidIndex[row]!=0) {
			Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
		    logger.log(Level.WARNING, "Unknown value " + encIdGuidIndex[row] + " in \"encIdGuidIndex\" attribute of the module table (reserved, default 0).");
		}
		if(encBaseIdGuidIndex[row]!=0) {
			Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
		    logger.log(Level.WARNING, "Unknown value " + encBaseIdGuidIndex[row] + " in \"encIdGuidIndex\" attribute of the module table (reserved, default 0).");
		}
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	public String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" Module Table (" + TABLE_INDEX + "):");		
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  Name Index: ");
			buffer.append(nameStringIndex[i]);
			buffer.append(";\tMVID GuidIndex: ");
			buffer.append(moduleVersionIdGuidIndex[i]);
			buffer.append(";\tEncId GuidIndex: ");
			buffer.append(encIdGuidIndex[i]);
			buffer.append(";\tEncBaseId GuidIndex: ");
			buffer.append(encBaseIdGuidIndex[i]);
			buffer.append(";");
		}
		
		return  buffer.toString();
	}
	

	@Override
	public int getRID() {
		return TABLE_INDEX;
	}

	@Override
	protected void fillRow(MetadataModel model, MetadataStream m, StringsStream s, UserStringStream u, GuidStream g, BlobStream b, RenderableCilElement entry[], int index) {
		ModuleEntry e = (ModuleEntry) entry[index];
		
		e.setGeneration(generation[index]);
		
		if(s!=null) {
			e.setName(s.getString(nameStringIndex[index]));
		} else {
			e.setName(model.getAlternativeModuleName());
		}
		
		if(g!=null) {
			e.setMvId(g.getGuid(moduleVersionIdGuidIndex[index]));
			e.setEncId(g.getGuid(encIdGuidIndex[index]));
			e.setEncBaseId(g.getGuid(encBaseIdGuidIndex[index]));
		}
	}

}
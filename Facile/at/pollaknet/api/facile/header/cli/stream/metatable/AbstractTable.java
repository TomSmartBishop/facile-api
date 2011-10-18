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
import at.pollaknet.api.facile.util.ByteReader;

public abstract class AbstractTable {

	protected int rows;
	protected byte stringIndexSize;
	protected byte blobIndexSize;
	protected byte guidIndexSize;
	protected boolean isVersion_1_1_MetadataStream = false;
	
	public int readRows (byte [] data, int offset, int[] numberOfRows, MetadataStream m) {

		long rowsLong = numberOfRows[getRID()];

		assert(rowsLong>=0);
		assert(rowsLong<=ByteReader.INT32_MAX_VAL);

		rows = (int) rowsLong;
		
		//do no further processing if there are no data rows 
		if(rows==0)
			return 0;
		
		stringIndexSize = m.getByteSizeOfStringIndices();
		blobIndexSize = m.getByteSizeOfBlobIndices();
		guidIndexSize = m.getByteSizeOfGuidIndices();
		isVersion_1_1_MetadataStream = m.getMajorVersion()==1 && m.getMinorVersion()==1;
		
		int consumedBytes = offset;
				
		prepareTable(numberOfRows);
		
		for(int i=0; i<this.rows;i++) {
			offset+= readRow(data, offset, i);
		}
		
		consumedBytes = offset - consumedBytes;
		
		return consumedBytes;
	}
	
	protected int readStringIndex(byte[] data, int offset, int[] indexRow, int row) {
		return readIndex(data, offset, indexRow, row, stringIndexSize);
	}
	
	protected int readGuidIndex(byte[] data, int offset, int[] indexRow, int row) {
		return readIndex(data, offset, indexRow, row, guidIndexSize);
	}
	
	protected int readBlobIndex(byte[] data, int offset, int[] indexRow, int row) {
		return readIndex(data, offset, indexRow, row, blobIndexSize);
	}
	
	protected int readIndex(byte[] data, int offset, int[] indexRow, int row, byte sizeOfIndex) {
		if(sizeOfIndex==4) {
			long value = ByteReader.getUInt32(data, offset);
			
			assert(value<=ByteReader.INT32_MAX_VAL);
			indexRow[row] = (int) value;
			return 4;
		} else if(sizeOfIndex==2) {
			
			indexRow[row] = ByteReader.getUInt16(data, offset);
			return 2;
		}
	
		indexRow[row] = ByteReader.getUInt8(data, offset);
	
		return 1;
	}
	
	protected int readIndex(byte[] data, int offset, long[] indexRow, int row, byte sizeOfIndex) {
		if(sizeOfIndex==4) {
			indexRow[row] = ByteReader.getUInt32(data, offset);
			return 4;
		} else if(sizeOfIndex==2) {
			indexRow[row] = ByteReader.getUInt16(data, offset);
			return 2;
		}
	
		indexRow[row] = ByteReader.getUInt8(data, offset);
		return 1;
	}
	
	public int getNumberOfRows() {
		return rows;
	}
	
	public void fill(MetadataModel model, MetadataStream m, StringsStream s, UserStringStream u, GuidStream g, BlobStream b, RenderableCilElement entry[]) {
		for(int i=0; i<this.rows;i++) {
			fillRow(model, m, s, u, g, b, entry, i);
		}
	}
	
	public String toString() {
		return createTableRepresentation();
	}
	
	protected static void Log(Level logLevel, String message) {
		Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
		logger.log(logLevel, message);
	}
	
	/**
	 * Returns the unique table row index (RID).
	 * @return the row index of the table.
	 */
	public abstract int getRID();
	
	protected abstract void prepareTable(int[] numberOfRows);
	
	protected abstract int readRow(byte[] data, int offset, int row);
	
	/**
	 * Method to force all sub classes to provide a string representation
	 * @return A string containing a nice string representation of the table.
	 */
	protected abstract String createTableRepresentation();
	
	protected abstract void fillRow(MetadataModel model, MetadataStream m, StringsStream s, UserStringStream u, GuidStream g, BlobStream b, RenderableCilElement entry[], int index);	
}

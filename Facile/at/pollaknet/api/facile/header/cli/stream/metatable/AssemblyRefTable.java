package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.AssemblyRefEntry;
import at.pollaknet.api.facile.util.ByteReader;


/*
 * Checked against 5th edition (December 2010)
 */
public class AssemblyRefTable extends AbstractTable {
	
	public final static int TABLE_INDEX = 0x23;
	public final static int MIN_ROW_SIZE = 0x14;

	private int majorVersion[];
	private int minorVersion[];
	private int buildNumber[];
	private int revisionNumber[];
	
	private long assemblyFlags[];
	private int publicKeyBlobIndex[];
	
	private int nameStringIndex[];
	private int cultureStringIndex[];
	
	private int hashValueBlobIndex[];

	
	@Override
	protected void prepareTable(int [] numberOfRows) {
		
		majorVersion = new int [rows];
		minorVersion = new int [rows];
		buildNumber = new int [rows];
		revisionNumber = new int [rows];
		assemblyFlags= new long [rows];
		
		publicKeyBlobIndex= new int [rows];
		nameStringIndex= new int [rows];
		cultureStringIndex= new int [rows];
		
		hashValueBlobIndex = new int [rows];
	}
	
	@Override
	protected int readRow(byte[] data, int offset, int row) {	
		
		int rowSize = offset;
		
		majorVersion[row] = ByteReader.getUInt16(data, offset);				offset +=2;
		minorVersion[row] = ByteReader.getUInt16(data, offset);				offset +=2;
		buildNumber[row] = ByteReader.getUInt16(data, offset);				offset +=2;
		revisionNumber[row] = ByteReader.getUInt16(data, offset);			offset +=2;
		
		assemblyFlags[row] = ByteReader.getUInt32(data, offset);			offset +=4;
		
		offset += readBlobIndex(data, offset, publicKeyBlobIndex, row);
		
		offset += readStringIndex(data, offset, nameStringIndex, row);
		offset += readStringIndex(data, offset, cultureStringIndex, row);

		offset += readBlobIndex(data, offset, hashValueBlobIndex, row);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" AssemblyRef Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  MajorVersion: ");
			buffer.append(majorVersion[i]);
			buffer.append(";\tMinorVersion: ");
			buffer.append(minorVersion[i]);
			buffer.append("\tBuildNumber: ");
			buffer.append(buildNumber[i]);
			buffer.append(";\tRevisionNumber: ");
			buffer.append(revisionNumber[i]);
			buffer.append(";\tAssemblyFlags: ");
			buffer.append(assemblyFlags[i]);
			buffer.append("\tPublicKey BlobIndex: ");
			buffer.append(publicKeyBlobIndex[i]);
			buffer.append(";\tName StringIndex: ");
			buffer.append(nameStringIndex[i]);
			buffer.append(";\tCulture StringIndex: ");
			buffer.append(cultureStringIndex[i]);
			buffer.append(";\tHashValue BlobIndex: ");
			buffer.append(hashValueBlobIndex[i]);
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
		AssemblyRefEntry e = (AssemblyRefEntry) entry[index];
		
		e.setMajorVersion(majorVersion[index]);
		e.setMinorVersion(minorVersion[index]);
		e.setBuildNumber(buildNumber[index]);
		e.setRevisionNumber(revisionNumber[index]);
		e.setFlags(assemblyFlags[index]);
		e.setPublicKey(b.getBlob(publicKeyBlobIndex[index]));
		e.setName(s.getString(nameStringIndex[index]));
		e.setCulture(s.getString(cultureStringIndex[index]));
		e.setHashValue(b.getBlob(hashValueBlobIndex[index]));
	}

}
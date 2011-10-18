package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.AssemblyEntry;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010)
 */
public class AssemblyTable extends AbstractTable {
	
	public final static int TABLE_INDEX = 0x20;
	public final static int MIN_ROW_SIZE = 0x16;
	
	private long hashAlgId[];
	private int majorVersion[];
	private int minorVersion[];
	private int buildNumber[];
	private int revisionNumber[];
	private long assemblyFlags[];
	
	private int publicKeyBlobIndex[];
	private int nameStringIndex[];
	private int cultureStringIndex[];
	
	@Override
	protected void prepareTable(int [] numberOfRows) {
		hashAlgId = new long [rows];
		majorVersion = new int [rows];
		minorVersion = new int [rows];
		buildNumber = new int [rows];
		revisionNumber = new int [rows];
		assemblyFlags= new long [rows];
		
		publicKeyBlobIndex= new int [rows];
		nameStringIndex= new int [rows];
		cultureStringIndex= new int [rows];
	}
	
	@Override
	protected int readRow(byte[] data, int offset, int row) {	
		
		int rowSize = offset;
		
		hashAlgId[row] = ByteReader.getUInt32(data, offset);				offset +=4;
		
		assert(hashAlgId[row] == 0 || hashAlgId[row] == 0x8003 || hashAlgId[row] == 0x8004);
		
		majorVersion[row] = ByteReader.getUInt16(data, offset);				offset +=2;
		minorVersion[row] = ByteReader.getUInt16(data, offset);				offset +=2;
		buildNumber[row] = ByteReader.getUInt16(data, offset);				offset +=2;
		revisionNumber[row] = ByteReader.getUInt16(data, offset);			offset +=2;
		assemblyFlags[row] = ByteReader.getUInt32(data, offset);			offset +=4;
		
		offset += readBlobIndex(data, offset, publicKeyBlobIndex, row);
		offset += readStringIndex(data, offset, nameStringIndex, row);
		offset += readStringIndex(data, offset, cultureStringIndex, row);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" Assembly Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  HashAlgId: ");
			buffer.append(hashAlgId[i]);
			buffer.append(";\tMajorVersion: ");
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
		AssemblyEntry e = (AssemblyEntry) entry[index];
		
		e.setHasAlgorithmId(hashAlgId[index]);
		e.setMajorVersion(majorVersion[index]);
		e.setMinorVersion(minorVersion[index]);
		e.setBuildNumber(buildNumber[index]);
		e.setRevisionNumber(revisionNumber[index]);
		e.setFlags(assemblyFlags[index]);
		
		//the blob stream is maybe empty!
		if(b!=null) e.setPublicKey(b.getBlob(publicKeyBlobIndex[index]));
		
		if(s!=null) {
			e.setName(s.getString(nameStringIndex[index]));
			e.setCulture(s.getString(cultureStringIndex[index]));
		} else {
			e.setName(model.getAlternativeModuleName());
		}
		
	}
}

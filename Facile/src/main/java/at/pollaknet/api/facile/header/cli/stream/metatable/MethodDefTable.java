package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.MethodDefEntry;
import at.pollaknet.api.facile.metamodel.entries.ParamEntry;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010), fillRow needs a closer look and documentation.
 */
public class MethodDefTable extends AbstractTable {

	public final static int TABLE_INDEX = 0x06;
	public final static int MIN_ROW_SIZE = 0x0e;
	
	private long relativeVirtualAddress [];
	private int methodImplAttributeFlags [];
	private int methodAttributeFlags [];
	
	private int nameStringIndex [];
	private int signatureBlobIndex [];
	private int paramListParamIndex [];

	private byte sizeOfParamIndex;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		relativeVirtualAddress = new long [rows];
		methodImplAttributeFlags = new int [rows];
		methodAttributeFlags = new int [rows];
		
		nameStringIndex = new int [rows];
		signatureBlobIndex = new int [rows];
		paramListParamIndex = new int [rows];

		sizeOfParamIndex = IndexDecoder.getByteSizeOfTargetRow(numberOfRows, ParamTable.TABLE_INDEX);
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;
		
		relativeVirtualAddress[row] = ByteReader.getUInt32(data, offset);				offset +=4;
		methodImplAttributeFlags[row] = ByteReader.getUInt16(data, offset);		offset +=2;
		methodAttributeFlags[row] = ByteReader.getUInt16(data, offset);			offset +=2;
  
		//.net 4.0 assemblies mask 0x10ff
		//assert(methodImplAttributeFlags[row]==0xffff || (methodImplAttributeFlags[row]&(~0x10bf))==0);
		
		offset += readStringIndex(data, offset, nameStringIndex, row);
		offset += readBlobIndex(data, offset, signatureBlobIndex, row);
		offset += readIndex(data, offset, paramListParamIndex, row, sizeOfParamIndex);
		
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	@Override
	protected String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" MethodDef Table (" + TABLE_INDEX + "):");
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  RelativeVirtualAddress: ");
			buffer.append(relativeVirtualAddress[i]);
			buffer.append(";\tImplAttribute Flags: ");
			buffer.append(methodImplAttributeFlags[i]);
			buffer.append(";\tAttribute Flags: ");
			buffer.append(methodAttributeFlags[i]);
			buffer.append("\tName StringIndex: ");
			buffer.append(nameStringIndex[i]);
			buffer.append(";\tSignature BlobIndex: ");
			buffer.append(signatureBlobIndex[i]);
			buffer.append(";\tParamList Index: ");
			buffer.append(paramListParamIndex[i]);
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
		MethodDefEntry e = (MethodDefEntry) entry[index];
		
		e.setRelativeVirtualAddress(relativeVirtualAddress[index]);
		e.setImplFlags(methodImplAttributeFlags[index]);
		e.setFlags(methodAttributeFlags[index]);
		e.setName(s.getString(nameStringIndex[index]));
		e.setSignature(b.getBlob(signatureBlobIndex[index]));
		
		ParamPtrTable paramPtr = (ParamPtrTable) m.getMetadataTable()[ParamPtrTable.TABLE_INDEX];
		
		int offset = paramListParamIndex[index];
		
		if(offset>0 && offset<=model.param.length) {
			int length = (paramListParamIndex.length==index+1) ? model.param.length+1 : paramListParamIndex[index+1];
			length -= offset;
			
			ParamEntry params [] = new ParamEntry[length];
			for(int i=0;i<length;i++){
				if(m.isUnoptimized() && paramPtr!=null) {
					params[i] = model.param[paramPtr.getPointer(offset-1 + i)-1];
				} else {
					params[i] = model.param[offset-1 + i];
				}
			}
			e.setParams(params);
		}
	}
}

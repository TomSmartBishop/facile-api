package at.pollaknet.api.facile.header.cli.stream.metatable;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.util.CodedIndex;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.FieldEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodDefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeDefEntry;
import at.pollaknet.api.facile.metamodel.util.EntryDecoder;
import at.pollaknet.api.facile.util.ByteReader;

/*
 * Checked against 5th edition (December 2010), warning output required, fillRow requires documentation.
 */
public class TypeDefTable extends AbstractTable{

	public final static int TABLE_INDEX = 0x02;
	public final static int MIN_ROW_SIZE = 0x0e;

	private long typeAttributeFlags []; 
	
	private int typeNameStringIndex [];
	private int typeNamespaceStringIndex [];
	
	private long extendsCodedIndex [];
	private int fieldListFieldIndex [];
	private int methodListMethodIndex [];

	private byte sizeOfExtendsIndex;
	private byte sizeOfFieldIndex;
	private byte sizeOfMethodIndex;
	
	@Override
	protected void prepareTable(int[] numberOfRows) {
		typeAttributeFlags = new long [rows];
		
		typeNameStringIndex = new int [rows];
		typeNamespaceStringIndex = new int [rows];
		
		extendsCodedIndex = new long [rows];
		fieldListFieldIndex = new int [rows];
		methodListMethodIndex = new int [rows];
		
		
		sizeOfExtendsIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, CodedIndex.TypeDefOrRef);
		sizeOfFieldIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, FieldTable.TABLE_INDEX);
		sizeOfMethodIndex = IndexDecoder.getByteSizeOfTargetRow(
				numberOfRows, MethodDefTable.TABLE_INDEX);
		
	}

	@Override
	protected int readRow(byte[] data, int offset, int row) {
		int rowSize = offset;

		typeAttributeFlags[row] = ByteReader.getUInt32(data, offset);		offset +=4;
		
		//See ECMA 335, Part.II 22.37 and 23.1.15
		//assert((typeAttributeFlags[row]&0xff28ca40L)==0);
		
		offset += readStringIndex(data, offset, typeNameStringIndex, row);
		offset += readStringIndex(data, offset, typeNamespaceStringIndex, row);
		
		offset += readIndex(data, offset, extendsCodedIndex, row, sizeOfExtendsIndex);		
		offset += readIndex(data, offset, fieldListFieldIndex, row, sizeOfFieldIndex);
		offset += readIndex(data, offset, methodListMethodIndex, row, sizeOfMethodIndex);
		
		rowSize = offset - rowSize;
		
		assert(rowSize >= MIN_ROW_SIZE);
		
		return rowSize;
	}

	public String createTableRepresentation() {
		StringBuffer buffer = new StringBuffer(" TypeDef Table (" + TABLE_INDEX + "):");		
		
		for(int i=0;i<rows;i++) {
			buffer.append("\n  TypeAttribute Flags: ");
			buffer.append(typeAttributeFlags[i]);
			buffer.append(";\tName StringIndex: ");
			buffer.append(typeNameStringIndex[i]);
			buffer.append(";\tNameSpace StringIndex: ");
			buffer.append(typeNamespaceStringIndex[i]);
			buffer.append(";\tExtends CodedIndex: ");
			buffer.append(extendsCodedIndex[i]);
			buffer.append(";\tFieldList Index: ");
			buffer.append(fieldListFieldIndex[i]);
			buffer.append(";\tMethodList Index: ");
			buffer.append(methodListMethodIndex[i]);
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

		TypeDefEntry e = (TypeDefEntry) entry[index];
		
		e.setFlags(typeAttributeFlags[index]);
		
		//extra handling for fixed buffers
		String className = s.getString(typeNameStringIndex[index]);
		
		if(className!=null)
		{
			if(className.contains(">e__FixedBuffer"))
				e.setName("'"+className+"'");
			else
				e.setName(className);
		}
		
		e.setNamespace(s.getString(typeNamespaceStringIndex[index]));
		e.setExtends(EntryDecoder.getTypeDefOrRefEntry(model, extendsCodedIndex[index]));

		FieldPtrTable fieldPtr = (FieldPtrTable) m.getMetadataTable()[FieldPtrTable.TABLE_INDEX];
		                     
		int offset = fieldListFieldIndex[index];
		
		if(offset>0 && offset<=model.field.length) {
			int length = (fieldListFieldIndex.length==index+1) ? model.field.length+1 : fieldListFieldIndex[index+1];
			length -= offset;

			FieldEntry fields [] = new FieldEntry[length];
			for(int i=0;i<length;i++) {
				if(m.isUnoptimized()) {
					fields[i] = model.field[fieldPtr.getPointer(offset-1 + i)-1];
				} else {
					fields[i] = model.field[offset-1 + i];
				}
			}
			e.setFields(fields);
		}
		
		MethodPtrTable methodPtr = (MethodPtrTable) m.getMetadataTable()[MethodPtrTable.TABLE_INDEX];

		offset = methodListMethodIndex[index];
		
		if(offset>0 && offset<=model.methodDef.length) {
			int length = (methodListMethodIndex.length==index+1) ? model.methodDef.length+1 : methodListMethodIndex[index+1];
			length -= offset;

			MethodDefEntry methods [] = new MethodDefEntry[length];
			for(int i=0;i<length;i++){
				if(m.isUnoptimized()) {
					methods[i] = model.methodDef[methodPtr.getPointer(offset-1 + i)-1];
				} else {
					methods[i] = model.methodDef[offset-1 + i];
				}
			}
			e.setMethods(methods);
		}
	}
}

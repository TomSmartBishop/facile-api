package at.pollaknet.api.facile.header.cli.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.DeclSecurityTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.EventTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ExportedTypeTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FieldTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FileTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.InterfaceImplTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ManifestResourceTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MemberRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MethodDefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ModuleRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ModuleTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ParamTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.PropertyTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.StandAloneSigTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.TypeDefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.TypeRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.TypeSpecTable;
import at.pollaknet.api.facile.util.ByteReader;


public class IndexDecoder {

	public static final int UNUSED_TABLE_INDEX = -1;
	
	private final static int MAX_TWO_BYTE_TABLE_SIZE = 0xffff;
	
	private final static byte SMALL_TABLE_BYTE_SIZE = 2;
	private final static byte LARGE_TABLE_BYTE_SIZE = 4;
	
	public final static int TYPE_DEF_OR_REF_BIT_SIZE = 2;
	public final static int TYPE_DEF_OR_REF_TABLE_INDICES [] = {
		TypeDefTable.TABLE_INDEX, TypeRefTable.TABLE_INDEX, TypeSpecTable.TABLE_INDEX };
	/*
	TypeDef 0
	TypeRef 1
	TypeSpec 2
	*/
	
	public final static int HAS_CONSTANT_BIT_SIZE = 2;
	public final static int HAS_CONSTANT_TABLE_INDICES [] = {
		FieldTable.TABLE_INDEX, ParamTable.TABLE_INDEX, PropertyTable.TABLE_INDEX };
	/*
	Field 0
	Param 1
	Property 2
	*/
	
	public final static int HAS_CUSTOM_ATTRIBUTE_BIT_SIZE = 5;
	public final static int HAS_CUSTOM_ATTRIBUTE_TABLE_INDICES [] = {
		MethodDefTable.TABLE_INDEX, FieldTable.TABLE_INDEX, TypeRefTable.TABLE_INDEX,
		TypeDefTable.TABLE_INDEX, ParamTable.TABLE_INDEX, InterfaceImplTable.TABLE_INDEX,
		MemberRefTable.TABLE_INDEX, ModuleTable.TABLE_INDEX, DeclSecurityTable.TABLE_INDEX,
		PropertyTable.TABLE_INDEX, EventTable.TABLE_INDEX, StandAloneSigTable.TABLE_INDEX,
		ModuleRefTable.TABLE_INDEX, TypeSpecTable.TABLE_INDEX, AssemblyTable.TABLE_INDEX,
		AssemblyRefTable.TABLE_INDEX, FileTable.TABLE_INDEX, ExportedTypeTable.TABLE_INDEX,
		ManifestResourceTable.TABLE_INDEX };
	/*
	MethodDef 0
	Field 1
	TypeRef 2
	TypeDef 3
	Param 4
	InterfaceImpl 5
	MemberRef 6
	Module 7
	Permission 8
	*/
	//NOTE: Permission is NO table. I guess DeclSecurity is meant instead.
	/*
	Property 9
	Event 10
	StandAloneSig 11
	ModuleRef 12
	TypeSpec 13
	Assembly 14
	AssemblyRef 15
	File 16
	ExportedType 17
	Partition II 171
	ManifestResource 18
	*/

	public final static int HAS_FIELD_MARSHALL_BIT_SIZE = 1;
	public final static int HAS_FIELD_MARSHALL_TABLE_INDICES [] = {
		FieldTable.TABLE_INDEX, ParamTable.TABLE_INDEX };
	/*
	Field 0
	Param 1
	*/
	
	public final static int HAS_DECL_SECURITY_BIT_SIZE = 2;
	public final static int HAS_DECL_SECURITY_TABLE_INDICES [] = {
		TypeDefTable.TABLE_INDEX, MethodDefTable.TABLE_INDEX, AssemblyTable.TABLE_INDEX };
	/*
	TypeDef 0
	MethodDef 1
	Assembly 2
	*/
	
	public final static int MEMBER_REF_PARENT_BIT_SIZE = 3;
	public final static int MEMBER_REF_PARENT_TABLE_INDICES [] = {
		TypeDefTable.TABLE_INDEX, TypeRefTable.TABLE_INDEX, ModuleRefTable.TABLE_INDEX,
		MethodDefTable.TABLE_INDEX, TypeSpecTable.TABLE_INDEX};
	/*
	TypeDef 0
	TypeRef 1
	ModuleRef 2
	MethodDef 3
	TypeSpec 4
	*/
	
	public final static int HAS_SEMANTICS_BIT_SIZE = 1;
	public final static int HAS_SEMANTICS_TABLE_INDICES [] = {
		EventTable.TABLE_INDEX, PropertyTable.TABLE_INDEX };
	/*
	Event 0
	Property 1
	*/
	
	public final static int METHOD_DEF_OR_REF_BIT_SIZE = 1;
	public final static int METHOD_DEF_OR_REF_TABLE_INDICES [] = {
		MethodDefTable.TABLE_INDEX, MemberRefTable.TABLE_INDEX };
	/*
	MethodDef 0
	MemberRef 1
	*/
	
	public final static int MEMBER_FORWARDED_BIT_SIZE = 1;
	public final static int MEMBER_FORWARDED_TABLE_INDICES [] = {
		FieldTable.TABLE_INDEX, MethodDefTable.TABLE_INDEX };
	/*
	Field 0
	MethodDef 1
	*/
	
	public final static int IMPLEMENTATION_BIT_SIZE = 2;
	public final static int IMPLEMENTATION_TABLE_INDICES [] = {
		FileTable.TABLE_INDEX, AssemblyRefTable.TABLE_INDEX, ExportedTypeTable.TABLE_INDEX };
	/*
	File 0
	AssemblyRef 1
	ExportedType 2
	*/
	
	public final static int CUSTOM_ATTRIBUTE_TYPE_BIT_SIZE = 3;
	public final static int CUSTOM_ATTRIBUTE_TYPE_TABLE_INDICES [] = {
		UNUSED_TABLE_INDEX, UNUSED_TABLE_INDEX,
		MethodDefTable.TABLE_INDEX, MemberRefTable.TABLE_INDEX, UNUSED_TABLE_INDEX };

	/*
	Not used 0
	Not used 1
	MethodDef 2
	MemberRef 3
	*/

	public final static int RESOLUTION_SCOPE_BIT_SIZE = 2;
	public final static int RESOLUTION_SCOPE_TABLE_INDICES [] = {
		ModuleTable.TABLE_INDEX, ModuleRefTable.TABLE_INDEX,
		AssemblyRefTable.TABLE_INDEX, TypeRefTable.TABLE_INDEX };
	/*
	Module 0
	ModuleRef 1
	AssemblyRef 2
	TypeRef 3
	*/
	
	public final static int TYPE_OR_METHOD_DEF_BIT_SIZE = 1;
	public final static int TYPE_OR_METHOD_DEF_TABLE_INDICES [] = {
		TypeDefTable.TABLE_INDEX, MethodDefTable.TABLE_INDEX };
	/*
	TypeDef 0
	MethodDef 1
	*/

	public static int getBitSize(CodedIndex index) {
		switch(index) {
			case TypeDefOrRef:
				return TYPE_DEF_OR_REF_BIT_SIZE;
			case HasConstant:
				return HAS_CONSTANT_BIT_SIZE;
			case HasCustomAttribute:
				return HAS_CUSTOM_ATTRIBUTE_BIT_SIZE;
			case HasFieldMarshall:
				return HAS_FIELD_MARSHALL_BIT_SIZE;
			case HasDeclSecurity:
				return HAS_DECL_SECURITY_BIT_SIZE;
			case MemberRefParent:
				return MEMBER_REF_PARENT_BIT_SIZE;
			case HasSemantics:
				return HAS_SEMANTICS_BIT_SIZE;
			case MethodDefOrRef:
				return METHOD_DEF_OR_REF_BIT_SIZE;
			case MemberForwarded:
				return MEMBER_FORWARDED_BIT_SIZE;
			case Implementation:
				return IMPLEMENTATION_BIT_SIZE;
			case CustomAttributeType:
				return CUSTOM_ATTRIBUTE_TYPE_BIT_SIZE;
			case ResolutionScope:
				return RESOLUTION_SCOPE_BIT_SIZE;
			case TypeOrMethodDef:
				return TYPE_OR_METHOD_DEF_BIT_SIZE;
		}
		
		Logger.getLogger(FacileReflector.LOGGER_NAME).logp(Level.WARNING,
				"CodedIndex", "getBitSize", 
				"Bit size of unknown CodedIndex requested. Returning 0 as bit requirement.");
		
		return 0;
	}

	public static int [] getTableIndices(CodedIndex index) {
		switch(index) {
		case TypeDefOrRef:
			return TYPE_DEF_OR_REF_TABLE_INDICES;
		case HasConstant:
			return HAS_CONSTANT_TABLE_INDICES;
		case HasCustomAttribute:
			return HAS_CUSTOM_ATTRIBUTE_TABLE_INDICES;
		case HasFieldMarshall:
			return HAS_FIELD_MARSHALL_TABLE_INDICES;
		case HasDeclSecurity:
			return HAS_DECL_SECURITY_TABLE_INDICES;
		case MemberRefParent:
			return MEMBER_REF_PARENT_TABLE_INDICES;
		case HasSemantics:
			return HAS_SEMANTICS_TABLE_INDICES;
		case MethodDefOrRef:
			return METHOD_DEF_OR_REF_TABLE_INDICES;
		case MemberForwarded:
			return MEMBER_FORWARDED_TABLE_INDICES;
		case Implementation:
			return IMPLEMENTATION_TABLE_INDICES;
		case CustomAttributeType:
			return CUSTOM_ATTRIBUTE_TYPE_TABLE_INDICES;
		case ResolutionScope:
			return RESOLUTION_SCOPE_TABLE_INDICES;
		case TypeOrMethodDef:
			return TYPE_OR_METHOD_DEF_TABLE_INDICES;
		}
		
		Logger.getLogger(FacileReflector.LOGGER_NAME).logp(Level.WARNING,
			"CodedIndex", "getTableIndices", 
			"Table indices of unknown CodedIndex requested. Returning emtpy array.");

		
		return new int [] {};
	}
	
	public static byte getByteSizeOfTargetRow(int[] numberOfRows, CodedIndex index ) {
		
		//get the number of required bits to encode the used tables
		int shiftBits = IndexDecoder.getBitSize(index);
		
		//max number of tables limits the number of bits
		assert(shiftBits<=6);
		
		int maxTableSize = MAX_TWO_BYTE_TABLE_SIZE>>shiftBits;
		
		//test size of every table and return the
		//appropriate size if the limit is exceeded
		for(int i : IndexDecoder.getTableIndices(index)) {
			if(i>IndexDecoder.UNUSED_TABLE_INDEX && numberOfRows[i]>maxTableSize)
				return LARGE_TABLE_BYTE_SIZE;
		}
		
		return SMALL_TABLE_BYTE_SIZE;
	}

	public static byte getByteSizeOfTargetRow(int[] numberOfRows, int tableIndex) {
		return numberOfRows[tableIndex]>MAX_TWO_BYTE_TABLE_SIZE ? LARGE_TABLE_BYTE_SIZE : SMALL_TABLE_BYTE_SIZE;
	}
	
	public static int decodeIndexContent(CodedIndex codedIndex, long index) {
		int bitSize = IndexDecoder.getBitSize(codedIndex);
		
		long value = index>>bitSize;
		
		assert(value <= ByteReader.INT32_MAX_VAL);
		
		return (int) (value-1);
	}
	
	public static int decodeIndex(CodedIndex codedIndex, long index) {
		int bitSize = IndexDecoder.getBitSize(codedIndex);
		
		int inverseBitMask = (-1)<<bitSize;
		return ((int)index)&(~inverseBitMask);
	}

	public static int decodeHasConstant(long index) {
		
		switch(decodeIndex(CodedIndex.HasConstant, index)) {
			case 0: return FieldTable.TABLE_INDEX;
			case 1: return ParamTable.TABLE_INDEX;
			case 2: return PropertyTable.TABLE_INDEX;
			default:break;
		}
		
		assert(false);
		return UNUSED_TABLE_INDEX;
	}

	public static int decodeHasCustomAttribute(long index) {
		switch(decodeIndex(CodedIndex.HasCustomAttribute, index)) {
			case 0: return MethodDefTable.TABLE_INDEX;
			case 1: return FieldTable.TABLE_INDEX;
			case 2: return TypeRefTable.TABLE_INDEX;
			case 3: return TypeDefTable.TABLE_INDEX;
			case 4: return ParamTable.TABLE_INDEX;
			case 5: return InterfaceImplTable.TABLE_INDEX;
			case 6: return MemberRefTable.TABLE_INDEX;
			case 7: return ModuleTable.TABLE_INDEX;
			case 8: return DeclSecurityTable.TABLE_INDEX;
			case 9: return PropertyTable.TABLE_INDEX;
			case 10: return EventTable.TABLE_INDEX;
			case 11: return StandAloneSigTable.TABLE_INDEX;
			case 12: return ModuleRefTable.TABLE_INDEX;
			case 13: return TypeSpecTable.TABLE_INDEX;
			case 14: return AssemblyTable.TABLE_INDEX;
			case 15: return AssemblyRefTable.TABLE_INDEX;
			case 16: return FileTable.TABLE_INDEX;
			case 17: return ExportedTypeTable.TABLE_INDEX;
			case 18: return ManifestResourceTable.TABLE_INDEX;
			default:break;
		}
		
		assert(false);
		return UNUSED_TABLE_INDEX;
	}

	public static int decodeHasFieldMarshall(long index) {

		switch(decodeIndex(CodedIndex.HasFieldMarshall, index)) {
			case 0: return FieldTable.TABLE_INDEX;
			case 1: return ParamTable.TABLE_INDEX;
			default:break;
		}
		
		assert(false);
		return UNUSED_TABLE_INDEX;
	}

	public static int decodeHasSemantics(long index) {
		switch(decodeIndex(CodedIndex.HasSemantics, index)) {
			case 0: return EventTable.TABLE_INDEX;
			case 1: return PropertyTable.TABLE_INDEX;
			default:break;
		}
		
		assert(false);
		return UNUSED_TABLE_INDEX;
	}

	public static int decodeCustomAttributeType(long index) {
		switch(decodeIndex(CodedIndex.HasSemantics, index)) {
			case 2: return MethodDefTable.TABLE_INDEX;
			case 3: return MemberRefTable.TABLE_INDEX;
			default:break;
		}
		
		//this case is also valid!
		return UNUSED_TABLE_INDEX;
	}

	public static int decodeHasDeclSecurity(long index) {
		switch(decodeIndex(CodedIndex.HasSemantics, index)) {
			case 0: return TypeDefTable.TABLE_INDEX;
			case 1: return MethodDefTable.TABLE_INDEX;
			case 2: return AssemblyTable.TABLE_INDEX;
		default:break;
	}
	
	assert(false);
	return UNUSED_TABLE_INDEX;
	}

	public static int decodeTypeOrMethodDef(long index) {
		switch(decodeIndex(CodedIndex.HasSemantics, index)) {
			case 0: return TypeDefTable.TABLE_INDEX;
			case 1: return MethodDefTable.TABLE_INDEX;
			default:break;
		}
		
		assert(false);
		return UNUSED_TABLE_INDEX;
	}

	public static int decodeMemberForwarded(long index) {
		switch(decodeIndex(CodedIndex.HasSemantics, index)) {
			case 0: return FieldTable.TABLE_INDEX;
			case 1: return MethodDefTable.TABLE_INDEX;
			default:break;
		}
		
		assert(false);
		return UNUSED_TABLE_INDEX;
	}

	public static int decodeMemberRefParent(long index) {
		switch(decodeIndex(CodedIndex.HasSemantics, index)) {
			case 0: return TypeDefTable.TABLE_INDEX;
			case 1: return TypeRefTable.TABLE_INDEX;
			case 2: return ModuleRefTable.TABLE_INDEX;
			case 3: return MethodDefTable.TABLE_INDEX;
			case 4: return TypeSpecTable.TABLE_INDEX;
			default:break;
		}
		
		assert(false);
		return UNUSED_TABLE_INDEX;
	}

	public static int decodeMethodDefOrRef(long index) {
		switch(decodeIndex(CodedIndex.HasSemantics, index)) {
			case 0: return MethodDefTable.TABLE_INDEX;
			case 1: return MemberRefTable.TABLE_INDEX;
			default:break;
		}
		
		assert(false);
		return UNUSED_TABLE_INDEX;
	}

			
}

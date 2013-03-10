package at.pollaknet.api.facile.tests.utils;

import at.pollaknet.api.facile.header.cli.util.CodedIndex;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.util.ByteReader;
import junit.framework.TestCase;

public class IndexDecoderTests extends TestCase {

	public void testGetBitSize() {
		
		assertEquals(IndexDecoder.CUSTOM_ATTRIBUTE_TYPE_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.CustomAttributeType));
		
		assertEquals(IndexDecoder.HAS_CONSTANT_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.HasConstant));
		
		assertEquals(IndexDecoder.HAS_CUSTOM_ATTRIBUTE_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.HasCustomAttribute));
		
		assertEquals(IndexDecoder.HAS_DECL_SECURITY_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.HasDeclSecurity));
		
		assertEquals(IndexDecoder.HAS_FIELD_MARSHALL_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.HasFieldMarshall));
		
		assertEquals(IndexDecoder.HAS_SEMANTICS_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.HasSemantics));
		
		assertEquals(IndexDecoder.IMPLEMENTATION_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.Implementation));
		
		assertEquals(IndexDecoder.MEMBER_FORWARDED_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.MemberForwarded));
		
		assertEquals(IndexDecoder.MEMBER_REF_PARENT_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.MemberRefParent));
		
		assertEquals(IndexDecoder.METHOD_DEF_OR_REF_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.MethodDefOrRef));
		
		assertEquals(IndexDecoder.RESOLUTION_SCOPE_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.ResolutionScope));
		
		assertEquals(IndexDecoder.TYPE_DEF_OR_REF_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.TypeDefOrRef));
		
		assertEquals(IndexDecoder.TYPE_OR_METHOD_DEF_BIT_SIZE,
				IndexDecoder.getBitSize(CodedIndex.TypeOrMethodDef));
	}

	public void testGetTableIndices() {
		//only check if the length matches with the predefined size
		
		double inverseLog2 = 1.0/Math.log10(2);
		
		assertEquals(IndexDecoder.CUSTOM_ATTRIBUTE_TYPE_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.CustomAttributeType).length)*inverseLog2));
		
		assertEquals(IndexDecoder.HAS_CONSTANT_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.HasConstant).length)*inverseLog2));
		
		assertEquals(IndexDecoder.HAS_CUSTOM_ATTRIBUTE_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.HasCustomAttribute).length)*inverseLog2));
		
		assertEquals(IndexDecoder.HAS_DECL_SECURITY_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.HasDeclSecurity).length)*inverseLog2));
		
		assertEquals(IndexDecoder.HAS_FIELD_MARSHALL_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.HasFieldMarshall).length)*inverseLog2));
		
		assertEquals(IndexDecoder.HAS_SEMANTICS_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.HasSemantics).length)*inverseLog2));
		
		assertEquals(IndexDecoder.IMPLEMENTATION_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.Implementation).length)*inverseLog2));
		
		assertEquals(IndexDecoder.MEMBER_FORWARDED_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.MemberForwarded).length)*inverseLog2));
		
		assertEquals(IndexDecoder.MEMBER_REF_PARENT_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.MemberRefParent).length)*inverseLog2));
		
		assertEquals(IndexDecoder.METHOD_DEF_OR_REF_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.MethodDefOrRef).length)*inverseLog2));
		
		assertEquals(IndexDecoder.RESOLUTION_SCOPE_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.ResolutionScope).length)*inverseLog2));
		
		assertEquals(IndexDecoder.TYPE_DEF_OR_REF_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.TypeDefOrRef).length)*inverseLog2));
		
		assertEquals(IndexDecoder.TYPE_OR_METHOD_DEF_BIT_SIZE,
				(int)Math.ceil(Math.log10(IndexDecoder.getTableIndices(CodedIndex.TypeOrMethodDef).length)*inverseLog2));
	}

	public void testGetByteSizeOfTargetRowInt() {

		int [] numberOfRows = new int [] {0, 2,4,100,65535,65536,2342343};
		
		assertEquals(IndexDecoder.getByteSizeOfTargetRow(numberOfRows, 0),2);
		assertEquals(IndexDecoder.getByteSizeOfTargetRow(numberOfRows, 1),2);
		assertEquals(IndexDecoder.getByteSizeOfTargetRow(numberOfRows, 2),2);
		assertEquals(IndexDecoder.getByteSizeOfTargetRow(numberOfRows, 3),2);
		assertEquals(IndexDecoder.getByteSizeOfTargetRow(numberOfRows, 4),2);
		assertEquals(IndexDecoder.getByteSizeOfTargetRow(numberOfRows, 5),4);
		assertEquals(IndexDecoder.getByteSizeOfTargetRow(numberOfRows, 6),4);

	}

	public void testDecodeIndexContent() {

		//basic tests
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.CustomAttributeType, 0xaa)+1,0x15);
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.HasConstant, 0xaa)+1,0x2a);
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.HasCustomAttribute, 0xaa)+1,0x05);
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.HasDeclSecurity, 0xaa)+1,0x2a);
		
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.HasFieldMarshall, 0xaa)+1,0x55);
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.HasSemantics, 0xaa)+1,0x55);
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.Implementation, 0xaa)+1,0x2a);
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.MemberForwarded, 0xaa)+1,0x55);
		
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.MemberRefParent, 0xaa)+1,0x15);
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.MethodDefOrRef, 0xaa)+1,0x55);
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.ResolutionScope, 0xaa)+1,0x2a);
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.TypeDefOrRef, 0xaa)+1,0x2a);
		
		assertEquals(IndexDecoder.decodeIndexContent(CodedIndex.TypeOrMethodDef, 0xaa)+1,0x55);
		
		//test the whole range from 0 to 255 (2^8 - 1)
		for(int i=0;i<256;i++) {
			int shift1 = i>>1;
			int shift2 = i>>2;
			int shift3 = i>>3;
			int shift5 = i>>5;
			assertEquals(shift3, IndexDecoder.decodeIndexContent(CodedIndex.CustomAttributeType, i)+1);
			assertEquals(shift2, IndexDecoder.decodeIndexContent(CodedIndex.HasConstant, i)+1);
			assertEquals(shift5, IndexDecoder.decodeIndexContent(CodedIndex.HasCustomAttribute, i)+1);
			assertEquals(shift2, IndexDecoder.decodeIndexContent(CodedIndex.HasDeclSecurity, i)+1);
			
			assertEquals(shift1, IndexDecoder.decodeIndexContent(CodedIndex.HasFieldMarshall, i)+1);
			assertEquals(shift1, IndexDecoder.decodeIndexContent(CodedIndex.HasSemantics, i)+1);
			assertEquals(shift2, IndexDecoder.decodeIndexContent(CodedIndex.Implementation, i)+1);
			assertEquals(shift1, IndexDecoder.decodeIndexContent(CodedIndex.MemberForwarded, i)+1);
			
			assertEquals(shift3, IndexDecoder.decodeIndexContent(CodedIndex.MemberRefParent, i)+1);
			assertEquals(shift1, IndexDecoder.decodeIndexContent(CodedIndex.MethodDefOrRef, i)+1);
			assertEquals(shift2, IndexDecoder.decodeIndexContent(CodedIndex.ResolutionScope, i)+1);
			assertEquals(shift2, IndexDecoder.decodeIndexContent(CodedIndex.TypeDefOrRef, i)+1);
			
			assertEquals(shift1, IndexDecoder.decodeIndexContent(CodedIndex.TypeOrMethodDef, i)+1);
		}
		
		//do further tests from 2^8 up to 2^31 (step size "i" is duplicated
		//in every iteration, this results in 23 loop-runs)
		for(long i=256;i<=ByteReader.INT32_MAX_VAL;i<<=1) {
			long shift1 = (i>>1);
			long shift2 = (i>>2);
			long shift3 = (i>>3);
			long shift5 = (i>>5);
			assertEquals(shift3, IndexDecoder.decodeIndexContent(CodedIndex.CustomAttributeType, i)+1);
			assertEquals(shift2, IndexDecoder.decodeIndexContent(CodedIndex.HasConstant, i)+1);
			assertEquals(shift5, IndexDecoder.decodeIndexContent(CodedIndex.HasCustomAttribute, i)+1);
			assertEquals(shift2, IndexDecoder.decodeIndexContent(CodedIndex.HasDeclSecurity, i)+1);
			
			assertEquals(shift1, IndexDecoder.decodeIndexContent(CodedIndex.HasFieldMarshall, i)+1);
			assertEquals(shift1, IndexDecoder.decodeIndexContent(CodedIndex.HasSemantics, i)+1);
			assertEquals(shift2, IndexDecoder.decodeIndexContent(CodedIndex.Implementation, i)+1);
			assertEquals(shift1, IndexDecoder.decodeIndexContent(CodedIndex.MemberForwarded, i)+1);
			
			assertEquals(shift3, IndexDecoder.decodeIndexContent(CodedIndex.MemberRefParent, i)+1);
			assertEquals(shift1, IndexDecoder.decodeIndexContent(CodedIndex.MethodDefOrRef, i)+1);
			assertEquals(shift2, IndexDecoder.decodeIndexContent(CodedIndex.ResolutionScope, i)+1);
			assertEquals(shift2, IndexDecoder.decodeIndexContent(CodedIndex.TypeDefOrRef, i)+1);
			
			assertEquals(shift1, IndexDecoder.decodeIndexContent(CodedIndex.TypeOrMethodDef, i)+1);
		}
	
	}

	public void testDecodeIndex() {
		
		assertEquals(0x05, IndexDecoder.decodeIndex(CodedIndex.CustomAttributeType, 0x55));
		assertEquals(0x02, IndexDecoder.decodeIndex(CodedIndex.CustomAttributeType, 0xaa));
		assertEquals(0x01, IndexDecoder.decodeIndex(CodedIndex.HasConstant, 0x55));
		assertEquals(0x02, IndexDecoder.decodeIndex(CodedIndex.HasConstant, 0xaa));
		assertEquals(0x15, IndexDecoder.decodeIndex(CodedIndex.HasCustomAttribute, 0x55));
		assertEquals(0x0a, IndexDecoder.decodeIndex(CodedIndex.HasCustomAttribute, 0xaa));
		assertEquals(0x01, IndexDecoder.decodeIndex(CodedIndex.HasDeclSecurity, 0x55));
		assertEquals(0x02, IndexDecoder.decodeIndex(CodedIndex.HasDeclSecurity, 0xaa));
		
		assertEquals(0x01, IndexDecoder.decodeIndex(CodedIndex.HasFieldMarshall, 0x55));
		assertEquals(0x00, IndexDecoder.decodeIndex(CodedIndex.HasFieldMarshall, 0xaa));
		assertEquals(0x01, IndexDecoder.decodeIndex(CodedIndex.HasSemantics, 0x55));
		assertEquals(0x00, IndexDecoder.decodeIndex(CodedIndex.HasSemantics, 0xaa));
		assertEquals(0x01, IndexDecoder.decodeIndex(CodedIndex.Implementation, 0x55));
		assertEquals(0x02, IndexDecoder.decodeIndex(CodedIndex.Implementation, 0xaa));
		assertEquals(0x01, IndexDecoder.decodeIndex(CodedIndex.MemberForwarded, 0x55));
		assertEquals(0x00, IndexDecoder.decodeIndex(CodedIndex.MemberForwarded, 0xaa));
		
		assertEquals(0x05, IndexDecoder.decodeIndex(CodedIndex.MemberRefParent, 0x55));
		assertEquals(0x02, IndexDecoder.decodeIndex(CodedIndex.MemberRefParent, 0xaa));
		assertEquals(0x01, IndexDecoder.decodeIndex(CodedIndex.MethodDefOrRef, 0x55));
		assertEquals(0x00, IndexDecoder.decodeIndex(CodedIndex.MethodDefOrRef, 0xaa));
		assertEquals(0x01, IndexDecoder.decodeIndex(CodedIndex.ResolutionScope, 0x55));
		assertEquals(0x02, IndexDecoder.decodeIndex(CodedIndex.ResolutionScope, 0xaa));
		assertEquals(0x01, IndexDecoder.decodeIndex(CodedIndex.TypeDefOrRef, 0x55));
		assertEquals(0x02, IndexDecoder.decodeIndex(CodedIndex.TypeDefOrRef, 0xaa));
		
		assertEquals(0x01, IndexDecoder.decodeIndex(CodedIndex.TypeOrMethodDef, 0x55));
		assertEquals(0x00, IndexDecoder.decodeIndex(CodedIndex.TypeOrMethodDef, 0xaa));
		
	}

}

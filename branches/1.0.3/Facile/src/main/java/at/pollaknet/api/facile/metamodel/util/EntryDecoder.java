package at.pollaknet.api.facile.metamodel.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.header.cli.util.CodedIndex;
import at.pollaknet.api.facile.header.cli.util.IndexDecoder;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ICustomAttributeType;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasConstant;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasDeclSecurity;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasFieldMarshal;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasSemantics;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IMemberForwarded;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IMethodDefOrRef;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeDefOrRef;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeOrMethodDef;
import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.symtab.symbols.meta.Implementation;

public class EntryDecoder extends IndexDecoder {

	public static ICustomAttributeType getCustomAttributeTypeEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.CustomAttributeType, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.CustomAttributeType, index);
		
		switch(decodedIndex) {
			case 0: return model.typeRef[decodedIndexContent];
			case 1: return model.typeDef[decodedIndexContent];
			case 2: return model.methodDef[decodedIndexContent];
			case 3:
			case 4: return model.memberRef[decodedIndexContent];
			
			//Case 4 is a nasty bug because it is reserved for strings and has to be
			//handled before calling this method. In reality case 4 is used for member
			//references (as well as case 3!).
			
			default:break;
		}
		
		String msg = "Decoded entry " + decodedIndex + " is not valid in CustomAttributeTypeEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
	
		return null;
	}

	public static IHasConstant getHasConstantEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.HasConstant, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.HasConstant, index);
		
		switch(decodedIndex) {
			case 0: return model.field[decodedIndexContent];
			case 1: return model.param[decodedIndexContent];
			case 2: return model.property[decodedIndexContent];
			default:break;
		}
		
		String msg = "Decoded entry " + decodedIndex + " is not valid in HasConstantEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
		
		return null;
	}

	public static IHasCustomAttribute getHasCustomAttributeEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.HasCustomAttribute, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.HasCustomAttribute, index);
		
		switch(decodedIndex) {
			case 0: return model.methodDef[decodedIndexContent];
			case 1: return model.field[decodedIndexContent];
			case 2: return model.typeRef[decodedIndexContent];
			case 3: return model.typeDef[decodedIndexContent];
			case 4: return model.param[decodedIndexContent];
			case 5: return model.interfaceImpl[decodedIndexContent];
			case 6: return model.memberRef[decodedIndexContent];
			case 7: return model.module[decodedIndexContent];
			case 8: return model.declSecurity[decodedIndexContent];
			case 9: return model.property[decodedIndexContent];
			case 10: return model.event[decodedIndexContent];
			case 11: return model.standAloneSig[decodedIndexContent];
			case 12: return model.moduleRef[decodedIndexContent];
			case 13: return model.typeSpec[decodedIndexContent];
			case 14: return model.assembly[decodedIndexContent];
			case 15: return model.assemblyRef[decodedIndexContent];
			case 16: return model.file[decodedIndexContent];
			case 17: return model.exportedType[decodedIndexContent];
			case 18: return model.manifestResource[decodedIndexContent];
			case 19: return model.genericParam[decodedIndexContent];
			case 20: return model.genericParamConstraint[decodedIndexContent];
			case 21: return model.methodSpec[decodedIndexContent];
			default:break;
		}
		
		String msg = "Decoded entry " + decodedIndex + " is not valid in HasCustomAttributeEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
		
		return null;
	}

	public static IHasDeclSecurity getHasDeclSecurityEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.HasDeclSecurity, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.HasDeclSecurity, index);
		
		switch(decodedIndex) {
			case 0: return model.typeDef[decodedIndexContent];
			case 1: return model.methodDef[decodedIndexContent];
			case 2: return model.assembly[decodedIndexContent];
			default:break;
		}
		
		String msg = "Decoded entry " + decodedIndex + " is not valid in HasDeclSecurityEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
	
		return null;
	}

	public static IHasFieldMarshal getHasFieldMarshallEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.HasFieldMarshall, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.HasFieldMarshall, index);
		
		switch(decodedIndex) {
			case 0: return model.field[decodedIndexContent];
			case 1: return model.param[decodedIndexContent];
			default:break;
		}

		String msg = "Decoded entry " + decodedIndex + " is not valid in HasFieldMarshallEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
		
		return null;
	}

	public static IHasSemantics getHasSemanticsEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.HasSemantics, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.HasSemantics, index);
		
		switch(decodedIndex) {
			case 0: return model.event[decodedIndexContent];
			case 1: return model.property[decodedIndexContent];
			default:break;
		}
		
		String msg = "Decoded entry " + decodedIndex + " is not valid in HasSemanticsEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
		
		return null;
	}

	public static Implementation getImplementationEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.Implementation, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.Implementation, index);
		
		switch(decodedIndex) {
			case 0: return model.file[decodedIndexContent];
			case 1: return model.assemblyRef[decodedIndexContent];
			case 2: return model.exportedType[decodedIndexContent];
			default:break;
		}
		
		String msg = "Decoded entry " + decodedIndex + " is not valid in ImplementationEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);

		return null;
	}

	public static IMemberForwarded getMemberForwardedEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.MemberForwarded, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.MemberForwarded, index);
		
		switch(decodedIndex) {
			case 0: return model.field[decodedIndexContent];
			case 1: return model.methodDef[decodedIndexContent];
			default:break;
		}
		
		String msg = "Decoded entry " + decodedIndex + " is not valid in MemberForwardedEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);

		return null;
	}

	public static MethodAndFieldParent getMemberRefParentEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.MemberRefParent, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.MemberRefParent, index);
		
		switch(decodedIndex) {
			case 0: return model.typeDef[decodedIndexContent];
			case 1: return model.typeRef[decodedIndexContent];
			case 2: return model.moduleRef[decodedIndexContent];
			case 3: return model.methodDef[decodedIndexContent];
			case 4: return model.typeSpec[decodedIndexContent];
			default:break;
		}

		String msg = "Decoded entry " + decodedIndex + " is not valid in MemberRefParentEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
		
		return null;
	}

	public static IMethodDefOrRef getMethodDefOrRefEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.MethodDefOrRef, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.MethodDefOrRef, index);
		
		switch(decodedIndex) {
			case 0: return model.methodDef[decodedIndexContent];
			case 1: return model.memberRef[decodedIndexContent];
			default:break;
		}

		String msg = "Decoded entry " + decodedIndex + " is not valid in MethodDefOrRefEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
		
		return null;
	}

	public static ResolutionScope getResolutionScopeEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.ResolutionScope, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.ResolutionScope, index);
		
		switch(decodedIndex) {
			case 0: return model.module[decodedIndexContent];
			case 1: return model.moduleRef[decodedIndexContent];
			case 2: return model.assemblyRef[decodedIndexContent];
			case 3: if(decodedIndexContent>=model.typeRef.length)
			{
				//FIXME: This is just a work around!
				return null;
			}
				
				return model.typeRef[decodedIndexContent];
			default:break;
		}

		String msg = "Decoded entry " + decodedIndex + " is not valid in ResolutionScopeEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
		
		return null;
	}

	public static ITypeDefOrRef getTypeDefOrRefEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.TypeDefOrRef, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.TypeDefOrRef, index);
		
		switch(decodedIndex) {
			case 0:
				if(decodedIndexContent>=model.typeDef.length)
					return null;
				return model.typeDef[decodedIndexContent];
			case 1:
				if(decodedIndexContent>=model.typeRef.length)
					return null;
				return model.typeRef[decodedIndexContent];
				
			case 2:
				if(decodedIndexContent>=model.typeSpec.length)
					return null;
				return model.typeSpec[decodedIndexContent];
			default:break;
		}
		
		String msg = "Decoded entry " + decodedIndex + " is not valid in TypeDefOrRefEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
		
		return null;
	}

	public static ITypeOrMethodDef getTypeOrMethodDefEntry(MetadataModel model, long index) {
		int decodedIndexContent = decodeIndexContent(CodedIndex.TypeOrMethodDef, index);
		
		if(decodedIndexContent<0) return null;
		
		int decodedIndex = decodeIndex(CodedIndex.TypeOrMethodDef, index);
		
		switch(decodedIndex) {
			case 0: return model.typeDef[decodedIndexContent];
			case 1: return model.methodDef[decodedIndexContent];
			default:break;
		}
		
		String msg = "Decoded entry " + decodedIndex + " is not valid in TypeOrMethodDefEntry.";
		Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);

		return null;
	}

}

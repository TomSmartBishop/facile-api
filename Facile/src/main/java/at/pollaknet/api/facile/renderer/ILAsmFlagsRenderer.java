package at.pollaknet.api.facile.renderer;

import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.code.ExceptionClause;
import at.pollaknet.api.facile.symtab.symbols.Event;
import at.pollaknet.api.facile.symtab.symbols.ExportedType;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.MethodSignature;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Property;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.meta.ManifestResource;
import at.pollaknet.api.facile.util.ByteReader;

public class ILAsmFlagsRenderer {

	public static String renderMethodImplFlags(Method method) {
		if(method==null) return "";
		
		StringBuffer buffer = new StringBuffer(64);
		int implFlags = method.getImplFlags();
		
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_CODE_TYPE_BIT_MASK, Method.IMPL_FLAGS_CODE_TYPE_IL)) {
			buffer.append("cil ");
		} else if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_CODE_TYPE_BIT_MASK, Method.IMPL_FLAGS_CODE_TYPE_NATIVE)) {
			buffer.append("native ");
		} else if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_CODE_TYPE_BIT_MASK, Method.IMPL_FLAGS_CODE_TYPE_OPTIL)) {
			buffer.append("optil ");
		} else if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_CODE_TYPE_BIT_MASK, Method.IMPL_FLAGS_CODE_TYPE_RUNTIME)) {
			buffer.append("runtime ");
		}
		
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_ORGANISATION_BIT_MASK, Method.IMPL_FLAGS_ORGANISATION_MANAGED)) {
			buffer.append("managed ");
		} else if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_ORGANISATION_BIT_MASK, Method.IMPL_FLAGS_ORGANISATION_UNMANAGED)) {
			buffer.append("unmanaged ");
		}
		
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_FORWARD_REF)) {
			buffer.append("forwardref ");
		}
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_PRESERVE_SIG)) {
			buffer.append("preservesig ");
		}
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_INTERNAL_CALL)) {
			buffer.append("internalcall ");
		}
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_SYNCHRONIZED)) {
			buffer.append("synchronized ");
		}
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_NO_INLINING)) {
			buffer.append("noinlining ");
		}
		
		return buffer.toString();
	}
	
	public static String renderMethodFlags(Method method) {
		if(method==null) return "";
		
		StringBuffer buffer = new StringBuffer(64);
		int flags = method.getFlags();
		
		if(ByteReader.testFlags(flags, Method.FLAGS_STATIC)) {
			buffer.append("static ");
		}
		
		switch(flags&Method.FLAGS_VISIBILITY_BIT_MASK) {
			case Method.FLAGS_VISIBILITY_PRIVATE: 				buffer.append("private "); break;
			case Method.FLAGS_VISIBILITY_PUBLIC:				buffer.append("public "); break;	
			case Method.FLAGS_VISIBILITY_FAMILIY: 				buffer.append("family "); break;
			case Method.FLAGS_VISIBILITY_ASSEMBLY: 				buffer.append("assembly "); break;
			case Method.FLAGS_VISIBILITY_FAMILY_AND_ASSEMBLY: 	buffer.append("famandassem "); break;
			case Method.FLAGS_VISIBILITY_FAMILY_OR_ASSEMBLY: 	buffer.append("famorassem "); break;
			//compilercontrolled??
			case Method.FLAGS_VISIBILITY_COMPILER_CONTROLLED: 	buffer.append("privatescope "); break;
			
			default: break;
		}	
		
		if(ByteReader.testFlags(flags, Method.FLAGS_FINAL)) {
			buffer.append("final ");
		}
		if(ByteReader.testFlags(flags, Method.FLAGS_VIRTUAL)) {
			buffer.append("virtual ");
		}
		if(ByteReader.testFlags(flags, Method.FLAGS_ABSTRACT)) {
			buffer.append("abstract ");
		}
		if(ByteReader.testFlags(flags, Method.FLAGS_HIDE_BY_SIG)) {
			buffer.append("hidebysig ");
		}
		if(ByteReader.testFlags(flags, Method.FLAGS_VTABLE_BIT_MASK, Method.FLAGS_VTABLE_NEW_SLOT)) {
			buffer.append("newslot ");
		}
		if(ByteReader.testFlags(flags, Method.FLAGS_ADDITIONAL_REQUIRE_SECURITY_OBJECT)) {
			buffer.append("reqsecobj ");
		}
		if(ByteReader.testFlags(flags, Method.FLAGS_SPECIAL_NAME)) {
			buffer.append("specialname ");
		}
		if(ByteReader.testFlags(flags, Method.FLAGS_ADDITIONAL_RT_SPECIAL_NAME)) {
			buffer.append("rtspecialname ");
		}
		if(ByteReader.testFlags(flags, Method.FLAGS_INTEROP_UNMANAGED_EXPORT)) {
			buffer.append("unmanagedexp ");
		}
		if(ByteReader.testFlags(flags, Method.FLAGS_INTEROP_PINVOKE)) {
			
			if(method.getNativeImplementation()==null) {
				//FIXME: handle this case proper
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.SEVERE, "Found PInvokeImpl without valid name!");
				buffer.append("pinvokeimpl [UNABLE_TO_RESOLVE] ");
			} else {
				buffer.append("pinvokeimpl ");
				buffer.append(method.getNativeImplementation().getImportName());
				buffer.append(" "); //confirm notation
			}
		}
		//further flags?
		
		return buffer.toString();
	}
	
	public static String renderParameterFlags(Parameter parameter) {
		if(parameter==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		int flags = parameter.getFlags();
		
		//TODO: check notation
		if(ByteReader.testFlags(flags, Parameter.FLAGS_OUT)) {
			buffer.append("[out] ");
		} else if(ByteReader.testFlags(flags, Parameter.FLAGS_IN)) {
			buffer.append("[in] ");
		}
		if(ByteReader.testFlags(flags, Parameter.FLAGS_IS_OPTIONAL)) {
			buffer.append("[opt] ");
		}
		if(ByteReader.testFlags(flags, Parameter.FLAGS_UNUSED)) {
			buffer.append("[unused] ");
		}
				
		if(ByteReader.testFlags(flags, Parameter.FLAGS_HAS_DEFAULT_VALUE)) {
			buffer.append("[defval] ");
		}
		
		return buffer.toString();
	}
	
	public static String renderFieldFlags(Field field) {
		if(field==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		int flags = field.getFlags();
		
		switch(flags&Field.FLAGS_VISIBILITY_BIT_MASK) {
			case Field.FLAGS_VISIBILITY_ASSEMBLY: buffer.append("assembly");break;
			case Field.FLAGS_VISIBILITY_PRIVATE: buffer.append("private");break;
			case Field.FLAGS_VISIBILITY_PUBLIC: buffer.append("public");break;
			case Field.FLAGS_VISIBILITY_FAMILY_OR_ASSEMBLY: buffer.append("famorassem");break;
			case Field.FLAGS_VISIBILITY_FAMILY_AND_ASSEMBLY: buffer.append("famandassem");break;
			case Field.FLAGS_VISIBILITY_FAMILY: buffer.append("family");break;
			default: buffer.append("privatescope");break;
		}
		
		if(ByteReader.testFlags(flags,Field.FLAGS_STATIC)) {
			buffer.append(" static");
		}
		
		if(ByteReader.testFlags(flags,Field.FLAGS_INIT_ONLY)) {
			buffer.append(" initonly");
		}
		
		if(ByteReader.testFlags(flags,Field.FLAGS_REMOTING_NOT_SERIALIZED)) {
			buffer.append(" notserialized");
		}
		
		if(ByteReader.testFlags(flags,Field.FLAGS_SPECIAL_NAME)) {
			buffer.append(" specialname");
		}
		
		return buffer.toString();
	}
	
	public static String renderTypeFlags(Type type) {
		if(type==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		long flags = type.getFlags();
		
		switch((int)(flags&Type.FLAGS_VISIBILITY_BIT_MASK)) {
			case Type.FLAGS_VISIBILITY_NESTED_ASSEMBLY: 			buffer.append("nested assembly "); break;
			case Type.FLAGS_VISIBILITY_NESTED_PUBLIC: 				buffer.append("nested public "); break;
			case Type.FLAGS_VISIBILITY_NESTED_PRIVATE: 				buffer.append("nested private "); break;
			case Type.FLAGS_VISIBILITY_NESTED_FAMILY: 				buffer.append("nested family "); break;
			case Type.FLAGS_VISIBILITY_NESTED_FAMILY_OR_ASSEMBLY: 	buffer.append("nested famorassem "); break;
			case Type.FLAGS_VISIBILITY_NESTED_FAMILY_AND_ASSEMBLY:	buffer.append("nested famandassem "); break;
			case Type.FLAGS_VISIBILITY_PRIVATE: 					buffer.append("private "); break;
			case Type.FLAGS_VISIBILITY_PUBLIC:						buffer.append("public "); break;
			default: break;
		}
		
		if(type.isInheritedFrom("System.ValueType")) {
			//value?
			buffer.append("value ");
		} else if(type.isInheritedFrom("System.Enum")) {
			buffer.append("enum ");
		}

		if(type.isInterface()) {
			buffer.append("interface ");
		}
		
		if(ByteReader.testFlags(flags, Type.FLAGS_SEMANTICS_IS_SEALED)) {
			buffer.append("sealed ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_SEMANTICS_IS_ABSTRACT)) {
			buffer.append("abstract ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_LAYOUT_BIT_MASK, Type.FLAGS_LAYOUT_AUTO_FIELDS)) {
			buffer.append("auto ");
		} else {
			if(ByteReader.testFlags(flags, Type.FLAGS_LAYOUT_SEQUENTIAL_FIELDS)) {
				buffer.append("sequential ");
			}
			if(ByteReader.testFlags(flags, Type.FLAGS_LAYOUT_EXPLICIT)) {
				buffer.append("explicit ");
			}
		}
		
		if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_ANSI)) {
			buffer.append("ansi ");
		} else if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_UNICODE)) {
			buffer.append("unicode ");
		} else if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_AUTO)) {
			buffer.append("autochar ");
		}  else if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_CUSTOM)) {
			//FIXME: handle this case proper
			String name = type.getFullQualifiedName();
			if(name==null || name.equals("")) name = "[Unknown Type]";
			Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.SEVERE, "Flags with unknown notation detected in " + name + "!");
		}

		if(ByteReader.testFlags(flags, Type.FLAGS_IMPLEMENTATION_IS_IMPORTED)) {
			buffer.append("import ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_IMPLEMENTATION_IS_SERIALIZABLE)) {
			buffer.append("serializeable ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_IMPLEMENTATION_BEFORE_FIELD_INIT)) {
			buffer.append("beforefieldinit ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_SEMANTICS_HAS_SPECIAL_NAME)) {
			buffer.append("specialname ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_RT_SPECIAL_NAME)) {
			buffer.append("rtspecialname ");
		}
		return buffer.toString();
	}
	
	public static String renderMethodSignatureFlags(MethodSignature signature) {
		if(signature==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		byte flags = signature.getFlags();
		
		switch(flags&MethodSignature.CALL_BITMASK) {
			case MethodSignature.CALL_HAS_THIS: 		buffer.append("instance "); break;
			case MethodSignature.CALL_EXPLICIT_THIS: 	buffer.append("explicit "); break;
			default: break;
		}
		
		switch(flags&MethodSignature.CALL_CONV_BITMASK) {
			case MethodSignature.CALL_CONV_DEFAULT: 	buffer.append("default "); break;
			case MethodSignature.CALL_CONV_VARARG: 		buffer.append("vararg "); break;
			case MethodSignature.CALL_CONV_C: 			buffer.append("unmanaged cdecl "); break;
			case MethodSignature.CALL_CONV_FAST: 		buffer.append("unmanaged fastcall "); break;
			case MethodSignature.CALL_CONV_STD: 		buffer.append("unmanaged stdcall "); break;
			case MethodSignature.CALL_CONV_THIS: 		buffer.append("unmanaged thiscall "); break;
			
			case MethodSignature.CALL_CONV_GENERIC:
			default:
				break;
		}
		
		return buffer.toString();
	}
	
	public static String renderExportedTypeFlags(ExportedType exportedType) {
		if(exportedType==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		long flags = exportedType.getFlags();
		
		if(ByteReader.testAny(flags, ExportedType.FLAGS_VISIBILITY_NESTED_PUBLIC)) {
			buffer.append("nested public ");
		} else if(ByteReader.testAny(flags, ExportedType.FLAGS_VISIBILITY_NESTED_PUBLIC)) {
			buffer.append("public ");
		} else if(ByteReader.testAny(flags, ExportedType.FLAGS_FORWARDER_TYPE)) {
			buffer.append("public ");
		}
		
		return buffer.toString();
	}
	
	public static String renderManifestResourceFlags(ManifestResource manifestResource) {
		if(manifestResource==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		long flags = manifestResource.getFlags();
		
		if(ByteReader.testAny(flags, ManifestResource.FLAGS_VISIBILITY_PUBLIC)) {
			buffer.append("nested public ");
		} else if(ByteReader.testAny(flags, ManifestResource.FLAGS_VISIBILITY_PRIVATE)) {
			buffer.append("public ");
		}
		
		return buffer.toString();
	}
	
	public static String renderPropertyFlags(Property property) {
		if(property==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		long flags = property.getFlags();
		
		if(ByteReader.testFlags(flags, Property.FLAGS_SPECIAL_NAME)) {
			buffer.append("specialname ");
		}
		if(ByteReader.testFlags(flags, Property.FLAGS_RT_SPECIAL_NAME)) {
			buffer.append("rtspecialname ");
		}
		
		return buffer.toString();
	}
	
	public static String renderExceptionClauseFlags(ILAsmRenderer r, ExceptionClause e) {
		if(r==null||e==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		long flags = e.getFlags();
		if(flags == ExceptionClause.FLAGS_CATCH_TYPE) {
			buffer.append("catch ");
			if(e.getExceptionType()==null) {
				buffer.append("[mscorlib]System.Exception");
			} else {
				buffer.append(r.renderAsReference(e.getExceptionType()));
			}
			buffer.append(" handler ");
		} else if(flags == ExceptionClause.FLAGS_FILTER_TYPE) {
			buffer.append(String.format("filter IL_%04x handler", e.getFilterOffset()));
		} else if(flags == ExceptionClause.FLAGS_FINAL_TYPE) {
			buffer.append("finally handler ");
		} else {
			buffer.append("fault handler ");
		}
		
		return buffer.toString();
	}
	
	public static String renderEventFlags(Event event) {
		if(event==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		
		int flags = event.getFlags();
		
		if(ByteReader.testFlags(flags,Event.FLAGS_SPECIAL_NAME)) {
			buffer.append("specialname ");
		}
		if(ByteReader.testFlags(flags,Event.FLAGS_RT_SPECIAL_NAME)) {
			buffer.append("rtspecialname ");
		}
		
		return buffer.toString();
	}
}

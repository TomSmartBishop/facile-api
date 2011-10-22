package at.pollaknet.api.facile.renderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.code.ExceptionClause;
import at.pollaknet.api.facile.code.MethodBody;
import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.dia.DebugInformation;
import at.pollaknet.api.facile.dia.InstructionInfo;
import at.pollaknet.api.facile.header.cli.CliHeader;
import at.pollaknet.api.facile.metamodel.entries.StandAloneSigEntry;
import at.pollaknet.api.facile.symtab.TypeKind;
import at.pollaknet.api.facile.symtab.symbols.ClassLayout;
import at.pollaknet.api.facile.symtab.symbols.Constant;
import at.pollaknet.api.facile.symtab.symbols.Event;
import at.pollaknet.api.facile.symtab.symbols.ExportedType;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.FieldLayout;
import at.pollaknet.api.facile.symtab.symbols.Instance;
import at.pollaknet.api.facile.symtab.symbols.MarshalSignature;
import at.pollaknet.api.facile.symtab.symbols.MemberRef;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.MethodSignature;
import at.pollaknet.api.facile.symtab.symbols.NativeImplementation;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Property;
import at.pollaknet.api.facile.symtab.symbols.PropertySignature;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;
import at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.symtab.symbols.meta.CustomAttribute;
import at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity;
import at.pollaknet.api.facile.symtab.symbols.meta.Implementation;
import at.pollaknet.api.facile.symtab.symbols.meta.ManifestResource;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;
import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.FileRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.Module;
import at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.ByteReader;
import at.pollaknet.api.facile.util.Pair;


public class ILAsmRenderer implements LanguageRenderer {

	private FacileReflector reflector;
	private int programCounter;
	
	
	protected FacileReflector getReflector() {
		return reflector;
	}
	
	public ILAsmRenderer(FacileReflector reflector) {
		this.reflector = reflector;
	}
	
	@Override
	public String render(Assembly assembly) {
		if(assembly==null) return "";
		
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(".assembly ");
		buffer.append(assembly.getName());
		buffer.append("\n{");
		
		//append the version string
		buffer.append("\n  .ver ");
		buffer.append(assembly.getMajorVersion());
		buffer.append(":");
		buffer.append(assembly.getMinorVersion());
		buffer.append(":");
		buffer.append(assembly.getRevisionNumber());
		buffer.append(":");
		buffer.append(assembly.getBuildNumber());
		
		buffer.append(String.format("\n  .hash algorithm 0x%08x", assembly.getHasAlgorithmId()));
		
		if(assembly.getCulture()!=null && !assembly.getCulture().equals("")) {
			buffer.append("\n  .locale \"");
			buffer.append(assembly.getCulture());
			buffer.append("\"");
		}
		
		if(assembly.getPublicKey()!=null) {
			buffer.append("\n  .publickey = (");
			buffer.append(ArrayUtils.formatByteArray(assembly.getPublicKey()));
			buffer.append(")");
		}
		
		for(CustomAttribute c: assembly.getCustomAttributes()) {
			if(c.getTypeRef().getFullQualifiedName().equals("System.Diagnostics.DebuggableAttribute"))
			{
				buffer.append("\n  //automatically generated, do not uncomment\n  //");
				buffer.append(render(c, new ArrayList<Integer>()));
			} else {
				buffer.append("\n  ");
				buffer.append(render(c));
			}
		}
		
		buffer.append("\n}");
		
		return buffer.toString();
	}

	@Override
	public String render(AssemblyRef assemblyRef) {
		if(assemblyRef==null) return "";
		
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(".assembly extern ");
		buffer.append(assemblyRef.getName());
		buffer.append("\n{");
		
		//append version string
		buffer.append("\n  .ver ");
		buffer.append(assemblyRef.getMajorVersion());
		buffer.append(":");
		buffer.append(assemblyRef.getMinorVersion());
		buffer.append(":");
		buffer.append(assemblyRef.getRevisionNumber());
		buffer.append(":");
		buffer.append(assemblyRef.getBuildNumber());
		
		if(assemblyRef.getCulture()!=null && !assemblyRef.getCulture().equals("")) {
			buffer.append("\n  .locale \"");
			buffer.append(assemblyRef.getCulture());
			buffer.append("\"");
		}
		
		if(assemblyRef.getPublicKey()!=null) {
			buffer.append("\n  .publickeytoken = (");
			buffer.append(ArrayUtils.formatByteArray(assemblyRef.getPublicKey()));
			buffer.append(")");
		}
		
		if(assemblyRef.getHashValue()!=null) {
			buffer.append("\n  .hash = (");
			buffer.append(ArrayUtils.formatByteArray(assemblyRef.getHashValue()));
			buffer.append(")");
		}
		
		for(CustomAttribute c: assemblyRef.getCustomAttributes()) {
			buffer.append("\n  ");
			buffer.append(render(c));
		}
		
		buffer.append("\n}");
		
		return buffer.toString();
	}

	@Override
	public String render(Module module) {
		if(module==null) return "";
		
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(".module ");
		buffer.append(module.getName());

		//TODO: add .imagebase
		//TODO: add .file
		//TODO: add .stackreserve
		//TODO: add .subsystem
		//TODO: add .corflags
		
		buffer.append(String.format("\n// Generation: 0x%04x", module.getGeneration()));
		if(module.getEncId()!=null)
			buffer.append(String.format("\n// EncId:{%s}",ArrayUtils.formatByteArray(module.getEncId())));
		if(module.getEncBaseId()!=null)
			buffer.append(String.format("\n// EncBaseId:{%s}",ArrayUtils.formatByteArray(module.getEncBaseId())));
		buffer.append(String.format("\n// MVID:{%s}", ArrayUtils.formatByteArray(module.getMvId())));

		//custom module attributes
		for(CustomAttribute c: module.getCustomAttributes()) {
			buffer.append("\n");
			buffer.append(render(c));
		}
	
		
		return buffer.toString();
	}

	@Override
	public String render(ModuleRef moduleRef) {
		if(moduleRef==null) return "";
		
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(".module extern ");
		buffer.append(moduleRef.getName());
		
		return buffer.toString();
	}

	@Override
	public String render(FileRef fileRef) {
		if(fileRef==null) return "";
		
		StringBuffer buffer = new StringBuffer(128);
		buffer.append(".file ");
		
		if(ByteReader.testFlags(fileRef.getFlags(), FileRef.FLAGS_CONTAINS_NO_META_DATA)) {
			buffer.append("nometadata ");
		}
		buffer.append(fileRef.getName());
			
		if(fileRef.getHashValue()!=null) {
			buffer.append(" .hash = (");
			buffer.append( ArrayUtils.formatByteArray(fileRef.getHashValue()) );
			buffer.append(")");
		}
		
		//TODO: resolve how to get the file entry point
		
		return buffer.toString();
	}

	@Override
	public String render(ExportedType exportedType) {
		if(exportedType==null) return "";
		
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(".class extern ");
		
		long flags = exportedType.getFlags();
		
		if(ByteReader.testAny(flags, ExportedType.FLAGS_VISIBILITY_NESTED_PUBLIC)) {
			buffer.append("nested public ");
		} else if(ByteReader.testAny(flags, ExportedType.FLAGS_VISIBILITY_NESTED_PUBLIC)) {
			buffer.append("public ");
		} else if(ByteReader.testAny(flags, ExportedType.FLAGS_FORWARDER_TYPE)) {
			buffer.append("public ");
		}
		
		buffer.append(exportedType.getFullQualifiedName());
		
		Implementation impl = exportedType.getImplementation();
		
		buffer.append(" {\n  ");
		
		if(impl.getFileRef()!=null) {
			buffer.append(renderAsReference(impl.getFileRef()));
		} else if(impl.getAssemblyRef()!=null) {	
			buffer.append(renderAsReference(impl.getAssemblyRef()));
		} else if(impl.getExportedType()!=null) {			
			buffer.append(renderAsReference(impl.getExportedType()));
		} else {			
			buffer.append(String.format(".class 0x%04x", exportedType.getTypeDefId()));
		} 
		
		for(CustomAttribute c: exportedType.getCustomAttributes()) {
			buffer.append("\n  ");
			buffer.append(render(c));
		}
		
		buffer.append("\n}");
		
		return buffer.toString();
	}
	
	@Override
	public String render(ManifestResource manifestResource) {
		if(manifestResource==null) return "";
		
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(".mresource ");
		
		long flags = manifestResource.getFlags();
		
		if(ByteReader.testAny(flags, ManifestResource.FLAGS_VISIBILITY_PUBLIC)) {
			buffer.append("nested public ");
		} else if(ByteReader.testAny(flags, ManifestResource.FLAGS_VISIBILITY_PRIVATE)) {
			buffer.append("public ");
		}
		
		buffer.append(manifestResource.getName());
		
		Implementation impl = manifestResource.getImplementation();
		
		if(impl!=null || manifestResource.getCustomAttributes().length>0) {
			buffer.append(" {\n  ");
			
			if(impl!=null) {
				if(impl.getFileRef()!=null) {
					buffer.append(renderAsReference(impl.getFileRef()));
				} else if(impl.getAssemblyRef()!=null) {	
					buffer.append(renderAsReference(impl.getAssemblyRef()));
				}
			}
			for(CustomAttribute c: manifestResource.getCustomAttributes()) {
				buffer.append("\n  ");
				buffer.append(render(c));
			}
			
			buffer.append("\n}");
		}
		
		return buffer.toString();
	}

	@Override
	public String render(CustomAttribute customAttribute) {
		return render(customAttribute, null);
	}
	
	public String render(CustomAttribute customAttribute, ArrayList<Integer> valueTypeIndices) {
		if(customAttribute==null) return "";
		
		
		StringBuffer buffer = new StringBuffer(128);
		boolean hasEntries = false;
		
		TypeRef type = customAttribute.getTypeRef();
		
		buffer.append(".custom ");
		
//		String callingConvtion = renderCallingConvention(type.getMethodRefSignature());
//		
//		if(!callingConvtion.equals("")) {
//			buffer.append(callingConvtion);
//			buffer.append(" void ");
//		} else {
//			buffer.append("void ");
//		}
		
		buffer.append("instance void ");
		
		buffer.append(renderClassRef(type));
		
		//mechanism to determinate potential value types
		boolean indicesPresent = valueTypeIndices!=null;
		int argumentIndex = 0;
		
		Instance [] fixedArguments = customAttribute.getFixedArguments();
		//this array will hold all value type candidates
		if(!indicesPresent) valueTypeIndices = new ArrayList<Integer>(fixedArguments.length);
		
		buffer.append("::.ctor(");
		
		for(Instance instance: fixedArguments) {
			if(hasEntries) buffer.append(", ");
			
			if(indicesPresent&&valueTypeIndices.contains(argumentIndex)) {
				buffer.append("valuetype ");
			}
				
			buffer.append(renderAsReference(instance.getTypeRef()));
			hasEntries = true;
			
			argumentIndex++;
		}
		
		buffer.append(") = { ");
		
		//reset param relative values
		hasEntries = false;
		argumentIndex = 0;
		
		//render fixed arguments
		for(Instance instance: customAttribute.getFixedArguments()) {
			if(hasEntries) buffer.append(", ");
			
			if(indicesPresent&&valueTypeIndices.contains(argumentIndex)) {
				buffer.append("int32("+instance.getValue()+")");
			} else {
				buffer.append(render(instance));
				hasEntries = true;
				
				//memorize potential value types
				if(instance.isPotentialValueType()) valueTypeIndices.add(argumentIndex);
			}
			argumentIndex++;
		}

		//render argument fields
		for(Pair<String, Instance> pair: customAttribute.getNamedFields()) {
			if(hasEntries) buffer.append(", ");
			
			Instance instance = pair.value;
			String typeName = render(instance.getTypeRef());
			
			if(indicesPresent&&valueTypeIndices.contains(argumentIndex)) {
				buffer.append(String.format("field %s '%s' = int32(%d)", typeName, pair.key, instance.getValue()));
			} else {
				buffer.append(String.format("field %s '%s' = %s(%s)", typeName, pair.key, typeName, instance));
				
				hasEntries = true;
				
				//memorize potential value types
				if(instance.isPotentialValueType()) valueTypeIndices.add(argumentIndex);
			}
			argumentIndex++;
		}
	
		//render argument properties
		for(Pair<String, Instance> pair: customAttribute.getNamedProperties()) {
			if(hasEntries) buffer.append(", ");
			
			Instance instance = pair.value;
			String typeName = render(instance.getTypeRef());
			
			if(indicesPresent&&valueTypeIndices.contains(argumentIndex)) {
				buffer.append(String.format("property %s '%s' = int32(%d)", typeName, pair.key, instance.getValue()));
			} else {
				buffer.append(String.format("property %s '%s' = %s(%s)", typeName, pair.key, typeName, instance));
				hasEntries = true;
				
				//memorize potential value types
				if(instance.isPotentialValueType()) valueTypeIndices.add(argumentIndex);
			}
			argumentIndex++;
		}
			
		buffer.append(" }");
	
		//this is the normal case of no re-interpretation
		if(indicesPresent||valueTypeIndices.isEmpty()) {
			return buffer.toString();
		}
	
		return render(customAttribute, valueTypeIndices) + "//re-interpreted, see original line below:\n//" + buffer.toString();		
	}

	private String renderClassRef(TypeRef type) {
		return renderClassRef(type, true);
	}

	private String renderClassRef(TypeRef type, boolean useShortSystemNames) {
		if(type==null) return "";
		
		if(type.getShortSystemName()!=null && useShortSystemNames) return type.getShortSystemName();
		
		StringBuffer buffer = new StringBuffer(32);
		if(type.getElementTypeKind()==TypeKind.ELEMENT_TYPE_VALUETYPE || 
		   (type.getType()!=null && type.getType().isInheritedFrom("System.ValueType")))
			buffer.append("valuetype ");
		
		if(type.getTypeSpec()!=null) {
			TypeSpec spec = type.getTypeSpec();

			//TODO: Missing items -  value class, method natives...
			return renderAsReference(spec);
		}
		
		ResolutionScope resolutionScope = type.getResolutionScope();
		
		while( resolutionScope.getTypeRef()!=null ) {
			resolutionScope = resolutionScope.getTypeRef().getResolutionScope();
		}
		
		if(resolutionScope!=null && !resolutionScope.isInAssembly()) {
			if(resolutionScope.getAssemblyRef()==null && resolutionScope.getModuleRef()!=null) {
				buffer.append("[module: ");
			} else {
				buffer.append("[");
			}
			buffer.append(resolutionScope.getName());
			buffer.append("]");
		}
		
		buffer.append(type.getFullQualifiedName());
		
		return buffer.toString();
	}

	public String renderCallingConvention(MethodSignature signature) {
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
			case MethodSignature.CALL_CONV_VARARG: 	buffer.append("vararg "); break;
			case MethodSignature.CALL_CONV_C: 		buffer.append("unmanaged cdecl "); break;
			case MethodSignature.CALL_CONV_FAST: 		buffer.append("unmanaged fastcall "); break;
			case MethodSignature.CALL_CONV_STD: 		buffer.append("unmanaged stdcall "); break;
			case MethodSignature.CALL_CONV_THIS: 		buffer.append("unmanaged thiscall "); break;
			
			case MethodSignature.CALL_CONV_GENERIC:
			default:
				break;
	}
		
		return buffer.toString();
	}

	@Override
	public String render(DeclarativeSecurity declarativeSecurity) {
		
		return "";
	}

	@Override
	public String render(TypeSpec typeSpec) {
		
		return "";
	}

	@Override
	public String render(TypeRef typeRef) {
		return renderClassRef(typeRef);
	}

	public String renderCompact(Type type) { 
		if(type==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		long flags = type.getFlags();
		
		buffer.append(".class ");
		
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

		if(type.isAnInterface()) {
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
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_LAYOUT_BIT_MASK, Type.FLAGS_LAYOUT_SEQUENTIAL_FIELDS)) {
			buffer.append("sequential ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_LAYOUT_BIT_MASK, Type.FLAGS_LAYOUT_EXPLICIT)) {
			buffer.append("explicit ");
		}
		
		if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_ANSI)) {
			buffer.append("ansi ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_UNICODE)) {
			buffer.append("unicode ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_AUTO)) {
			buffer.append("autochar ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_CUSTOM)) {
			//????
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

		buffer.append(type.getName());
		buffer.append(" ");
		
		if(type.getExtends()!=null) {
			buffer.append("extends ");
			buffer.append(renderClassRef(type.getExtends(), false));
		}
		
		TypeRef[] interfaces = type.getInterfaces();
		if(interfaces.length!=0) {
			buffer.append("implements ");
			for(int i=0;i<interfaces.length;i++) {
				if(i!=0) buffer.append(", ");
				buffer.append(renderClassRef(interfaces[i], false));
			}
		}
		
		buffer.append("\n{");
		
		buffer.append(render(type.getClassLayout()));
		
		for(CustomAttribute c:type.getCustomAttributes()) {
			buffer.append("\n  ");
			buffer.append(render(c).replaceAll("\n", "\n  "));
		}
					
		buffer.append("\n  // ...\n}");
		
		return buffer.toString();
	}
	
	@Override
	public String render(Type type) {
		if(type==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		long flags = type.getFlags();
		
		buffer.append(".class ");
		
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

		if(type.isAnInterface()) {
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
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_LAYOUT_BIT_MASK, Type.FLAGS_LAYOUT_SEQUENTIAL_FIELDS)) {
			buffer.append("sequential ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_LAYOUT_BIT_MASK, Type.FLAGS_LAYOUT_EXPLICIT)) {
			buffer.append("explicit ");
		}
		
		if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_ANSI)) {
			buffer.append("ansi ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_UNICODE)) {
			buffer.append("unicode ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_AUTO)) {
			buffer.append("autochar ");
		}
		if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_CUSTOM)) {
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

		buffer.append(type.getName());
		buffer.append(" ");
		
		if(type.getExtends()!=null) {
			buffer.append("extends ");
			buffer.append(renderClassRef(type.getExtends(), false));
		}
		
		TypeRef[] interfaces = type.getInterfaces();
		if(interfaces.length!=0) {
			buffer.append("implements ");
			for(int i=0;i<interfaces.length;i++) {
				if(i!=0) buffer.append(", ");
				buffer.append(renderClassRef(interfaces[i], false));
			}
		}
		
		buffer.append("\n{");
		
		buffer.append(render(type.getClassLayout()));
		
		for(CustomAttribute c:type.getCustomAttributes()) {
			buffer.append("\n  ");
			buffer.append(render(c).replaceAll("\n", "\n  "));
		}
		
		for(Property p:type.getProperties()) {
			buffer.append("\n  ");
			buffer.append(render(p).replaceAll("\n", "\n  "));
			buffer.append("\n");
		}
		
		for(Field f:type.getFields()) {
			buffer.append("\n  ");
			buffer.append(render(f).replaceAll("\n", "\n  "));
		}
		
		buffer.append("\n");
		
		for(Method m:type.getMethods()) {
			buffer.append("\n  ");
			buffer.append(render(m).replaceAll("\n", "\n  "));
			buffer.append("\n");
		}
		
		for(Type t:type.getEnclosingClasses()) {
			buffer.append("\n  ");
			buffer.append(render(t).replaceAll("\n", "\n  "));
			buffer.append("\n");
		}
		
		
		//events properties...
		
		//overrides
		
		buffer.append("\n}");
		
		return buffer.toString();
	}

	@Override
	public String render(PropertySignature propertySignature) {
		
		return "";
	}

	@Override
	public String render(Property property) {
		if(property==null) return "";
		
		StringBuffer buffer = new StringBuffer();
		long flags = property.getFlags();
		
		buffer.append(".property ");
		
		if(ByteReader.testFlags(flags, Property.FLAGS_SPECIAL_NAME)) {
			buffer.append("specialname ");
		}
		if(ByteReader.testFlags(flags, Property.FLAGS_RT_SPECIAL_NAME)) {
			buffer.append("rtspecialname ");
		}
		
		buffer.append("instance ");
		
		//specify the property's type
		for(Method m:property.getMethods()) {
			if(m.getName().startsWith("get_")) {
				buffer.append(renderAsReference(m.getMethodSignature().getReturnType())+" ");
				break;
			} else if(m.getName().startsWith("set_")) {
				buffer.append(renderAsReference(m.getMethodSignature().getParameters()[0].getTypeRef())+" ");
				break;
			}
		}
		
		buffer.append(property.getName());
		
		buffer.append("()\n{");
		
		for(Method m:property.getMethods()) {
			String methodRef = renderMethodRef(m);

			if(m.getName().startsWith("get_")) {
				buffer.append("\n  .get instance ");
			} else if(m.getName().startsWith("set_")) {
				buffer.append("\n  .set instance ");
			} else {
				buffer.append("\n  .other ");
			}
			buffer.append(renderMethodRef(m));
		}
		
		buffer.append("\n}");
		
		return buffer.toString();
	}

	@Override
	public String render(Parameter parameter) {
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
		

		buffer.append(renderClassRef(parameter.getTypeRef()));
		//buffer.append(parameter.getTypeRef().getFullQualifiedName());
		
		if(parameter.getMarshalSignature()!=null) {
			buffer.append(" ");
			buffer.append(render(parameter.getMarshalSignature()));
		}
		
		if(parameter.getName()!=null) {
			buffer.append(" ");
			buffer.append(parameter.getName());
		}
		
		return buffer.toString();
	}

	@Override
	public String render(NativeImplementation nativeImplementation) {
		
		return "";
	}

	public String renderMethodDefSig(Method method) {
		if(method==null) return "";
		
		StringBuffer buffer = new StringBuffer(32);

		if(method.getMethodSignature().getReturnParameter()!=null) {
			buffer.append(render(method.getMethodSignature().getReturnParameter()));
		} else {
			buffer.append(renderClassRef(method.getMethodSignature().getReturnType()));
		}
		buffer.append(" ");
		
//		MethodAndFieldParent parent = method.getOwnerClass();
//		
//		if(parent.getTypeRef()!=null) {
//			buffer.append(renderClassRef(parent.getTypeRef()));
//		} else if (parent.getModuleRef()!=null) {
//			buffer.append("[module: ");
//			buffer.append(parent.getModuleRef().getFullQualifiedName());
//			buffer.append("]");
//		} else if(parent.getMethod()!=method){
//			buffer.append(renderMethodDefSig(parent.getMethod()));
//		}
//		buffer.append("::");
		buffer.append(method.getName());
		
		buffer.append(render(method.getMethodSignature()));
		
		return buffer.toString();

	}
	
	@Override
	public String render(MethodSignature methodSignature) {
		if(methodSignature==null) return "";
		
		Parameter [] parameter = methodSignature.getParameters();
		Parameter returnParameter = methodSignature.getReturnParameter();
		
		StringBuffer buffer = new StringBuffer(32);

		buffer.append("(");
		
		boolean first = true;
		if(parameter!=null) {
			for(int i=(returnParameter==null?0:1);i<parameter.length;i++) {
				if(parameter[i]!=null) {
					if(!first) buffer.append(", ");
				
					buffer.append(render(parameter[i]));
					first = false;
				}
			}
		}
		
		buffer.append(")");

		return buffer.toString();
	}


	@Override
	public String render(Method method) {
		if(method==null || method.getMethodSignature()==null) return "";
		
		StringBuffer buffer = new StringBuffer(64);

		buffer.append(".method ");
		
		int flags = method.getFlags();
		int implFlags = method.getImplFlags();
		
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
		
		buffer.append(renderMethodDefSig(method));
		buffer.append(" ");
		
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_CODE_TYPE_BIT_MASK, Method.IMPL_FLAGS_CODE_TYPE_IL)) {
			buffer.append("cil ");
		}
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_CODE_TYPE_BIT_MASK, Method.IMPL_FLAGS_CODE_TYPE_NATIVE)) {
			buffer.append("native ");
		}
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_CODE_TYPE_BIT_MASK, Method.IMPL_FLAGS_CODE_TYPE_OPTIL)) {
			buffer.append("optil ");
		}
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_CODE_TYPE_BIT_MASK, Method.IMPL_FLAGS_CODE_TYPE_RUNTIME)) {
			buffer.append("runtime ");
		}
		
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_ORGANISATION_BIT_MASK, Method.IMPL_FLAGS_ORGANISATION_MANAGED)) {
			buffer.append("managed ");
		}
		if(ByteReader.testFlags(implFlags, Method.IMPL_FLAGS_ORGANISATION_BIT_MASK, Method.IMPL_FLAGS_ORGANISATION_UNMANAGED)) {
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
		
		buffer.append("\n{");	
		
		for(CustomAttribute c: method.getCustomAttributes()) {
			buffer.append("\n  ");
			buffer.append(render(c));
		}
		
		buffer.append("\n  ");
		buffer.append(render(method.getMethodBody(), method.getDebungInformation()).replaceAll("\n", "\n  "));
		
		buffer.append("\n}");
		
		return buffer.toString();
	}

	private String renderMethodRef(Method method) {
//		StringBuffer buffer = new StringBuffer(32);
//		MethodSignature signature = method.getMethodSignature();
//		Parameter returnParameter = signature.getReturnParameter();
//		
//		if(returnParameter!=null) {
//			buffer.append(render(returnParameter));
//		} else {
//			buffer.append(renderClassRef(signature.getReturnType()));
//		}
//		
//		buffer.append(" ");
//		
//		buffer.append(method.getName());
//		
//		buffer.append(render(signature));
//		
//		return buffer.toString();
		
		return renderMethodDefSig(method);
	}

	private String render(MethodBody methodBody, DebugInformation debugInformation) {
		//byte [] body = methodBody.getBody();
		if(methodBody==null) return "";
		
		InstructionInfo [] debugList = null;
		InstructionInfo info = null;
		boolean firstEntry=true;
		
		StringBuffer buffer = new StringBuffer(256);

		buffer.append("//Code size: ");
		buffer.append(methodBody.getCodeSize());
		buffer.append(" byte");
		
		if(	methodBody.getMethodToken() == reflector.getCliHeader().getEntryPointToken() &&
			!ByteReader.testFlags(	reflector.getCliHeader().getFlags(),
									CliHeader.FLAGS_NATIVE_ENTRY_POINT		)				) {
			buffer.append("\n.entrypoint ");
		}

		buffer.append("\n.maxstack ");
		buffer.append(methodBody.getMaxStack());
		
		TypeRef[] locals = methodBody.getLocalVars();
		if(locals!=null && locals.length>0) {
			if(ByteReader.testFlags(methodBody.getFlags(), MethodBody.FLAGS_INIT_LOCAL)) {
				buffer.append("\n.locals init(");
			} else {
				//this produces unverifiable code - however it is possible
				buffer.append("\n.locals(");
			}
			int counter = 0;
			int last = methodBody.getLocalVars().length-1;
			for(TypeRef type : methodBody.getLocalVars()) {
				//IMPROVE: Resolve local names - maybe via PDB
				buffer.append(String.format("\n  [%d] %s %s%s", counter, renderClassRef(type), "local" + counter, counter==last?"":","));
				counter++;
			}
			buffer.append("\n)");
		}
		
		int debugIndex = 0;
		
		if(debugInformation!=null) {
			debugList = debugInformation.getInstructionInfos();
			if(debugList!=null && debugList.length>0)
			info = debugList[0];
		}
		
		programCounter = 0;
		int instructionStart;
		
		for(CilInstruction i: methodBody.getCilInstructions()) {
			if(info!=null) {
				while(info.getProgramCounter()<programCounter && debugIndex+1<debugList.length) {
					debugIndex++;
					info = debugList[debugIndex];
				}
				if(info.getProgramCounter()==programCounter) {
					buffer.append("\n.line ");
					buffer.append(info.getLineNumber());
					buffer.append(":");
					buffer.append(info.getColNumber());
					
					if(firstEntry) {
						buffer.append(" '");
						buffer.append(debugInformation.getSourceFileName());
						buffer.append("'");
						firstEntry = false;
					}
				}
			}
			
			instructionStart = programCounter;
			programCounter += i.getByteSize();
			buffer.append(String.format("\nIL_%04x: %s", instructionStart, i.render(this)));	
		}

		if(methodBody.getExceptionClauses().length>0) {
			buffer.append("\n");
		}
		for(ExceptionClause e: methodBody.getExceptionClauses()) {
			buffer.append(String.format("\n.try IL_%04x to IL_%04x ", e.getTryOffset(), e.getTryOffset() + e.getTryLength()) );
			long flags = e.getFlags();
			if(flags == ExceptionClause.FLAGS_CATCH_TYPE) {
				buffer.append("catch ");
				if(e.getExcpetionType()==null) {
					buffer.append("[mscorlib]System.Exception");
				} else {
					buffer.append(renderAsReference(e.getExcpetionType()));
				}
				buffer.append(" handler ");
			} else if(flags == ExceptionClause.FLAGS_FILTER_TYPE) {
				buffer.append(String.format("filter IL_%04x handler", e.getFilterOffset()));
			} else if(flags == ExceptionClause.FLAGS_FINAL_TYPE) {
				buffer.append("finally handler ");
			} else {
				buffer.append("fault handler ");
			}
			buffer.append(String.format("IL_%04x to IL_%04x", e.getHandlerOffset(), e.getHandlerOffset() + e.getHanderLength()) );
		}
		
		return buffer.toString();
	}

	@Override
	public String render(MemberRef memberRef) {
		
		return "";
	}

	@Override
	public String render(MarshalSignature marshalSignature) {
		if(marshalSignature==null) return "";
		
		return marshalSignature.toString();
	}

//	@Override
//	public String render(Interface interfaze) {
//		if(interfaze.getTypeRef().getType()!=null) {
//			return render(interfaze.getTypeRef().getType());
//		}
//		return  render(interfaze.getTypeRef());
//	}

	@Override
	public String render(Instance instance) {
		
		if(instance.getTypeRef().getElementTypeKind()==TypeKind.ELEMENT_TYPE_BOOLEAN) {
			return instance.getValue()!=0?"bool(true)":"bool(false)";
		}

		StringBuffer buffer = new StringBuffer();
		
		TypeRef typeRef = instance.getTypeRef();
		
		Instance [] array = instance.getArrayInstance();
		
		if(array!=null) {
			boolean first = true;
			//buffer.append("(");
			buffer.append(typeRef.getFullQualifiedName());
			
			buffer.append("[");
			buffer.append(array.length);
			buffer.append("] {");
			
			for(Instance i: array) {
				buffer.append(render(i));
				if(first) {
					first = false;
				} else {
					buffer.append(" "); //correctness needs to be prooven
				}
			}
			
			buffer.append("}");
			return buffer.toString();
		}
				
		if(instance.getBoxedInstance()!=null) {
			buffer.append(render(instance));
		} else {
			//buffer.append("(");
			buffer.append(renderAsReference(typeRef));
			buffer.append("(");
			
			String stringValue = instance.getStringValue();
			if(stringValue!=null) {
				if(typeRef.getElementTypeKind()==TypeKind.ELEMENT_TYPE_STRING) {
					buffer.append("'");
					buffer.append(stringValue);
					buffer.append("'");
				} else {
					buffer.append(stringValue);
				}
			} else {
				long value = instance.getValue();
				if(value==0 && !ArrayUtils.contains(TypeKind.NUMERIC_TYPES, typeRef.getElementTypeKind())) {
					buffer.append("null");
				} else {
					buffer.append(value);
				}
			}
			buffer.append(")");
		}
		
		return buffer.toString();
	}

	@Override
	public String render(FieldLayout fieldLayout) {
		return "";
	}

	@Override
	public String render(Field field) {
		if(field==null) return "";
		
		StringBuffer buffer = new StringBuffer(64);
		
		buffer.append(".field ");

		buffer.append(renderFieldFlags(field));
		
		buffer.append(" ");
		
		buffer.append(renderClassRef(field.getTypeRef()));
		
		buffer.append(" ");
		
		buffer.append(field.getName());
		
		CustomAttribute [] attributes = field.getCustomAttributes();
		
		if(attributes.length>0) {
			buffer.append("\n{");	
			
			for(CustomAttribute c: field.getCustomAttributes()) {
				buffer.append("\n  ");
				buffer.append(render(c));
			}
		
			buffer.append("\n}");
		}
		
		return buffer.toString();
	}

	private String renderFieldFlags(Field field) {
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
		
		buffer.append(render(field.getMarshalSignature()));
		
		return buffer.toString();
	}

	@Override
	public String render(Event event) {
		if(event==null) return "";
		
		StringBuffer buffer = new StringBuffer(64);
		
		buffer.append(".event ");

		int flags = event.getFlags();
		
		if(ByteReader.testFlags(flags,Event.FLAGS_SPECIAL_NAME)) {
			buffer.append("specialname ");
		}
		
		if(ByteReader.testFlags(flags,Event.FLAGS_RT_SPECIAL_NAME)) {
			buffer.append("rtspecialname ");
		}
		
		
		buffer.append(" ");
		
		buffer.append(event.getName());
		
		buffer.append("\n{");
		
		for(Method m:event.getMethods()) {
			String methodRef = renderMethodRef(m);

			if(m.getName().startsWith("add_")) {
				buffer.append("\n  .addon ");
			} else if(m.getName().startsWith("remove_")) {
				buffer.append("\n  .removeon ");
			} else if(m.getName().startsWith("fire_")) {
				buffer.append("\n  .fire ");
			} else {
				buffer.append("\n  .other ");
			}
			buffer.append(renderMethodRef(m));
		}
		
		buffer.append("\n}");
		
		return buffer.toString();
	}

	@Override
	public String render(Constant constant) {
		
		return "";
	}

	@Override
	public String render(ClassLayout classLayout) {
		if(classLayout==null) return "";
		
		StringBuffer buffer = new StringBuffer(32);
		
		buffer.append("\n  .size ");
		buffer.append(classLayout.getClassSize());
		buffer.append("\n  .pack ");
		buffer.append(classLayout.getPackingSize());
		
		return buffer.toString();
		
	}

	@Override
	public String render(Namespace namespace) {
		return render(namespace, true);
	}
	
	public String render(Namespace namespace, boolean renderCompact) {
		if(namespace==null) return "";
		
		StringBuffer buffer = new StringBuffer(128);
		
		buffer.append(".namespace ");
		buffer.append(namespace.getFullQualifiedName());
		buffer.append("\n{");
		
		for(TypeRef type :namespace.getTypeRefs()) {
			if(type.getType()!=null) {
				buffer.append("\n  ");
				if(renderCompact) {
					buffer.append(renderCompact(type.getType()).replaceAll("\n", "\n  "));
				} else {
					buffer.append(render(type.getType()).replaceAll("\n", "\n  "));
				}
			}
		}
		
		buffer.append("\n}");
		
		return buffer.toString();
	}

	@Override
	public String renderAsReference(TypeSpec typeSpec) {
		StringBuffer buffer = new StringBuffer(32);
		ResolutionScope resolutionScope = typeSpec.getResolutionScope();
		TypeRef spec = typeSpec;
		
		if(typeSpec.isValueType()) buffer.append("valuetype ");
		else if(!typeSpec.isBasicType()){
			buffer.append("class ");
		}
		
		while((resolutionScope==null || resolutionScope.isInAssembly()) && spec.getTypeSpec()!=null && spec.getTypeSpec().getEnclosedTypeRef()!=null) {
			spec = spec.getTypeSpec().getEnclosedTypeRef();
			resolutionScope = spec.getResolutionScope();
		}
		if(resolutionScope!=null && !resolutionScope.isInAssembly()) {
			if(resolutionScope.getAssemblyRef()==null && resolutionScope.getModuleRef()!=null) {
				buffer.append("[module: ");
			} else {
				buffer.append("[");
			}
			buffer.append(resolutionScope.getName());
			buffer.append("]");
		}
		
		buffer.append(typeSpec.getFullQualifiedName());
		return buffer.toString();
	}

	@Override
	public String renderAsReference(TypeRef typeRef) {
		return renderClassRef(typeRef);
	}

	@Override
	public String renderAsReference(Type type) {
		return renderClassRef(type);
	}

	@Override
	public String renderAsReference(Method method) {
		if(method==null) return "";
		
		StringBuffer buffer = new StringBuffer(32);

		buffer.append(renderCallingConvention(method.getMethodSignature()));
		
		if(method.getMethodSignature().getReturnParameter()!=null) {
			buffer.append(render(method.getMethodSignature().getReturnParameter()));
		} else {
			buffer.append(renderClassRef(method.getMethodSignature().getReturnType()));
		}
		buffer.append(" ");
		
		MethodAndFieldParent parent = method.getOwner();
		
		if(parent.getTypeRef()!=null) {
			buffer.append(renderClassRef(parent.getTypeRef()));
		} else if (parent.getModuleRef()!=null) {
			buffer.append("[module: ");
			buffer.append(parent.getModuleRef().getFullQualifiedName());
			buffer.append("]");
		} else if(parent.getMethod()!=method){
			buffer.append(renderAsReference(parent.getMethod()));
		}
		buffer.append("::");
		buffer.append(method.getName());
		
		buffer.append(render(method.getMethodSignature()));
		
		return buffer.toString();

	}

	@Override
	public String renderAsReference(MemberRef memberRef) {
		if(memberRef==null) return "";
		
		StringBuffer buffer = new StringBuffer(32);
		MethodSignature signature = memberRef.getMethodRefSignature();
		
		if(signature!=null) {
			buffer.append(renderCallingConvention(signature));
			
			if(signature.getReturnParameter()!=null) {
				buffer.append(render(signature.getReturnParameter()));
			} else {
				buffer.append(renderClassRef(signature.getReturnType()));
			}
			buffer.append(" ");
			
			MethodAndFieldParent parent = memberRef.getOwner();
			
			if(parent.getTypeRef()!=null) {
				buffer.append(renderClassRef(parent.getTypeRef()));
			} else if (parent.getModuleRef()!=null) {
				buffer.append("[module: ");
				buffer.append(parent.getModuleRef().getFullQualifiedName());
				buffer.append("]");
			} else if(parent.getMethod()!=memberRef){
				buffer.append(renderAsReference(parent.getMethod()));
			}
			buffer.append("::");
			buffer.append(memberRef.getName());
			
			buffer.append(render(signature));
			
			return buffer.toString();
		}
		
		return renderAsReference(memberRef.getTypeRef()) + " " + memberRef.getName();
	}

	@Override
	public String renderAsReference(Field field) {
		return renderAsReference(field.getTypeRef()) + " " + field.getName();
	}

	@Override
	public String renderAsReference(StandAloneSigEntry standAlone) {
		if(standAlone.getTypeRef()!=null)
			return renderAsReference(standAlone.getTypeRef());
		else if(standAlone.getMethodSignature()!=null)
			return renderAsReference(standAlone.getMethodSignature());
		
		return "unimplemented local sig";
	}

	@Override
	public String renderAsReference(MethodSignature methodSignature) {
		return render(methodSignature);
	}
	
	public String renderAsReference(FileRef fileRef) {
		if(fileRef==null) return "";
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(".file ");
		buffer.append(fileRef.getFullQualifiedName());
		
		return buffer.toString();
	}
	
	public String renderAsReference(AssemblyRef assemblyRef) {
		if(assemblyRef==null) return "";
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(".assembly extern ");
		buffer.append(assemblyRef.getFullQualifiedName());
		
		return buffer.toString();
	}
	
	public String renderAsReference(ExportedType exportedType) {
		if(exportedType==null) return "";
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(".class extern ");
		buffer.append(exportedType.getFullQualifiedName());
		
		return buffer.toString();
	}

	public String renderRelativeAsLabel(int target) {
		return String.format("IL_%04x", programCounter+target);
	}

	@Override
	public void renderSourceFilesToDirectory(Assembly assembly, String directory) throws IOException {
		String head = "\n//Decompiled with Facile (" + assembly.getFileName() + ")";
		String file = assembly.getName() + ".il";
		byte [] extraSpace = "\n\n".getBytes();
		
		if(!directory.endsWith(System.getProperty("file.separator"))) {
			file = directory + System.getProperty("file.separator") + file;
		} else {
			file = directory + file;
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		
		fos.write(head.getBytes());
		fos.write(extraSpace);
		
		for(AssemblyRef ref :assembly.getAssemblyRefs()) {
			fos.write(render(ref).getBytes());
			fos.write(extraSpace);
		}
		
		fos.write(render(assembly).getBytes());
		fos.write(extraSpace);
		
		fos.write(render(assembly.getModule()).getBytes());
		fos.write(extraSpace);
		
		for(Namespace ns :assembly.getModule().getNamespaces()) {
			if(ns.getName()==null || ns.getName().equals("")) continue;
			fos.write(render(ns, false).getBytes());
			fos.write(extraSpace);
		}
		
		fos.close();
	}
	
}

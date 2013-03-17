package at.pollaknet.api.facile.renderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.code.ExceptionClause;
import at.pollaknet.api.facile.code.MethodBody;
import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.exception.SizeExceededException;
import at.pollaknet.api.facile.header.cli.CliHeader;
import at.pollaknet.api.facile.metamodel.entries.StandAloneSigEntry;
import at.pollaknet.api.facile.pdb.DebugInformation;
import at.pollaknet.api.facile.pdb.InstructionInfo;
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
	private String newLine;
	private boolean useBinaryCustomAttributes;
	
	protected FacileReflector getReflector() {
		return reflector;
	}
	
	public ILAsmRenderer(FacileReflector reflector) {
		this(reflector, false);
	}
	
	public ILAsmRenderer(FacileReflector reflector, boolean useBinaryCustomAttributes) {
		this.reflector = reflector;
		this.useBinaryCustomAttributes = useBinaryCustomAttributes;
		newLine = System.getProperty("line.separator");
	}
	
	private String addTab(String block) {
		return block.replaceAll(newLine, newLine + "  ");
	}
	private String addTab(String block, String prefix) {
		return block.replaceAll(newLine, newLine + "  " + prefix);
	}
	
	@Override
	public String render(Assembly assembly) {
		if(assembly==null) return "";
		
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(".assembly ");
		buffer.append(assembly.getName());
		buffer.append(newLine);
		buffer.append("{");
		buffer.append(newLine);
		
		//append the version string
		buffer.append("  .ver ");
		buffer.append(assembly.getMajorVersion());
		buffer.append(":");
		buffer.append(assembly.getMinorVersion());
		buffer.append(":");
		buffer.append(assembly.getRevisionNumber());
		buffer.append(":");
		buffer.append(assembly.getBuildNumber());
		
		buffer.append(String.format("%s  .hash algorithm 0x%08x", newLine, assembly.getHasAlgorithmId()));
		
		if(assembly.getCulture()!=null && !assembly.getCulture().equals("")) {
			buffer.append(newLine);
			buffer.append("  .locale \"");
			buffer.append(assembly.getCulture());
			buffer.append("\"");
		}
		
		if(assembly.getPublicKey()!=null) {
			buffer.append(newLine);
			buffer.append("  .publickey = (");
			buffer.append(ArrayUtils.formatByteArray(assembly.getPublicKey()));
			buffer.append(")");
		}
		
		for(CustomAttribute c: assembly.getCustomAttributes()) {
			if(c.getTypeRef().getFullQualifiedName().equals("System.Diagnostics.DebuggableAttribute"))
			{
				buffer.append(newLine);
				buffer.append("  //automatically generated, do not uncomment");
				buffer.append(newLine);
				buffer.append("  //");
				buffer.append(addTab( render(c, new ArrayList<Integer>()), "//" ));
			} else {
				buffer.append(newLine);
				buffer.append("  ");
				buffer.append(render(c));
			}
		}
		
		buffer.append(newLine);
		buffer.append("}");
		
		return buffer.toString();
	}

	@Override
	public String render(AssemblyRef assemblyRef) {
		if(assemblyRef==null) return "";
		
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(".assembly extern ");
		buffer.append(assemblyRef.getName());
		buffer.append(newLine);
		buffer.append("{");
		
		//append version string
		buffer.append(newLine);
		buffer.append("  .ver ");
		buffer.append(assemblyRef.getMajorVersion());
		buffer.append(":");
		buffer.append(assemblyRef.getMinorVersion());
		buffer.append(":");
		buffer.append(assemblyRef.getRevisionNumber());
		buffer.append(":");
		buffer.append(assemblyRef.getBuildNumber());
		
		if(assemblyRef.getCulture()!=null && !assemblyRef.getCulture().equals("")) {
			buffer.append(newLine);
			buffer.append("  .locale \"");
			buffer.append(assemblyRef.getCulture());
			buffer.append("\"");
		}
		
		if(assemblyRef.getPublicKey()!=null) {
			buffer.append(newLine);
			buffer.append("  .publickeytoken = (");
			buffer.append(ArrayUtils.formatByteArray(assemblyRef.getPublicKey()));
			buffer.append(")");
		}
		
		if(assemblyRef.getHashValue()!=null) {
			buffer.append(newLine);
			buffer.append("  .hash = (");
			buffer.append(ArrayUtils.formatByteArray(assemblyRef.getHashValue()));
			buffer.append(")");
		}
		
		for(CustomAttribute c: assemblyRef.getCustomAttributes()) {
			buffer.append(newLine);
			buffer.append("  ");
			buffer.append(render(c));
		}
		
		buffer.append(newLine);
		buffer.append("}");
		
		return buffer.toString();
	}

	@Override
	public String render(Module module) {
		if(module==null) return "";
		
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(".module ");
		buffer.append(module.getName());		
		
		buffer.append(String.format("%s// Generation: 0x%04x", newLine, module.getGeneration()));
		if(module.getEncId()!=null)
			buffer.append(String.format("%s// EncId:{%s}", newLine, ArrayUtils.formatByteArray(module.getEncId())));
		if(module.getEncBaseId()!=null)
			buffer.append(String.format("%s// EncBaseId:{%s}", newLine, ArrayUtils.formatByteArray(module.getEncBaseId())));
		buffer.append(String.format("%s// MVID:{%s}", newLine, ArrayUtils.formatByteArray(module.getMvId())));

		//custom module attributes
		for(CustomAttribute c: module.getCustomAttributes()) {
			buffer.append(newLine);
			buffer.append(render(c));
		}
		
		buffer.append(newLine);
		
		buffer.append(String.format("%s.imagebase 0x%08x // PE optional header: image base", newLine, reflector.getPeOptionalHeader().getImageBase()));
		buffer.append(String.format("%s.file alignment 0x%08x // PE optional header: file alignment", newLine, reflector.getPeOptionalHeader().getFileAlignment()));
		try {
			buffer.append(String.format("%s.stackreserve 0x%08x // PE optional header: stack reserve", newLine, reflector.getPeOptionalHeader().getSizeOfStackReserve()));
		} catch (SizeExceededException e) {
			buffer.append(String.format("%s//.stackreserve size of long data type exceeded!", newLine));
		}
		buffer.append(String.format("%s.subsystem 0x%04x // PE optional header: sub system", newLine, reflector.getPeOptionalHeader().getSubsystem()));
		buffer.append(String.format("%s.corflags 0x%08x // Cli header: flags", newLine, reflector.getCliHeader().getFlags()));
		
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
		
		buffer.append(" {");
		buffer.append(newLine);
		buffer.append("  ");
		
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
			buffer.append(newLine);
			buffer.append("  ");
			buffer.append(render(c));
		}
		
		buffer.append(newLine);
		buffer.append("}");
		
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
			buffer.append(" {");
			buffer.append(newLine);
			buffer.append("  ");
			
			if(impl!=null) {
				if(impl.getFileRef()!=null) {
					buffer.append(renderAsReference(impl.getFileRef()));
				} else if(impl.getAssemblyRef()!=null) {	
					buffer.append(renderAsReference(impl.getAssemblyRef()));
				}
			}
			for(CustomAttribute c: manifestResource.getCustomAttributes()) {
				buffer.append(newLine);
				buffer.append("  ");
				buffer.append(render(c));
			}
			
			buffer.append(newLine);
			buffer.append("}");
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
			
			if(indicesPresent && valueTypeIndices.contains(argumentIndex)) {
				buffer.append("valuetype ");
			} else if(instance.getTypeRef().getShortSystemName()==null) {
				buffer.append("class ");
			}
				
			buffer.append(renderAsReference(instance.getTypeRef()));
			hasEntries = true;
			
			argumentIndex++;
		} 
		
		//consider binary representation of custom attributes
		if(useBinaryCustomAttributes)
		{
			buffer.append(") = (");

			byte [] value = customAttribute.getValue();
			if(value!=null) {
				buffer.append(ArrayUtils.formatByteArray(value));
				
				String safeString = new String(value).replaceAll("\\p{Cntrl}",".");
				String emptyCheck = new String(value).replaceAll("\\p{Cntrl}","");
				if(!emptyCheck.isEmpty()) {
					buffer.append(") //");
					buffer.append(safeString);
				} else {
					buffer.append(")");
				}
			} else {
				buffer.append(")");
			}
			
			return buffer.toString();
		}
		
		//try to setup custom attributes by type
		
		buffer.append(") = {");
		argumentIndex = 0;
		
		//render fixed arguments
		for(Instance instance: fixedArguments) {
			if(argumentIndex>0) buffer.append(newLine);
			buffer.append("  ");
			
			//TODO: check again for correctness
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
			if(argumentIndex>0) buffer.append(newLine);
			buffer.append("  ");
			
			Instance instance = pair.value;
			String typeName = render(instance.getTypeRef());
			
			if(indicesPresent&&valueTypeIndices.contains(argumentIndex)) {
				buffer.append(String.format("field %s '%s' = int32(%d)", typeName, pair.key, instance.getValue()));
			} else {
				buffer.append(String.format("field %s '%s' = %s", typeName, pair.key, render(instance)));
				
				hasEntries = true;
				
				//memorize potential value types
				if(instance.isPotentialValueType()) valueTypeIndices.add(argumentIndex);
			}
			argumentIndex++;
		}
	
		//render argument properties
		for(Pair<String, Instance> pair: customAttribute.getNamedProperties()) {
			if(argumentIndex>0) buffer.append(newLine);
			buffer.append("  ");
			
			Instance instance = pair.value;
			String typeName = render(instance.getTypeRef());
			
			if(indicesPresent&&valueTypeIndices.contains(argumentIndex)) {
				buffer.append(String.format("property %s '%s' = int32(%d)", typeName, pair.key, instance.getValue()));
			} else {
				buffer.append(String.format("property %s '%s' = %s", typeName, pair.key, render(instance)));
				hasEntries = true;
				
				//memorize potential value types
				if(instance.isPotentialValueType()) valueTypeIndices.add(argumentIndex);
			}
			argumentIndex++;
		}
		
		if(argumentIndex>1)
			buffer.append(newLine + "}");
		else
			buffer.append("  }");
	
		//this is the normal case of no re-interpretation
		if(indicesPresent||valueTypeIndices.isEmpty()) {
			return buffer.toString();
		}
	
		return render(customAttribute, valueTypeIndices) +
				" //re-interpreted, see original lines below:" + newLine + "//" +
				buffer.toString().replace(newLine, newLine+"//");		
	}
	
	private String renderClassRef(TypeRef type) {
		TypeSpec typeSpec = type.getTypeSpec();
		if(typeSpec!=null) {
			//get the most inner type spec
			TypeRef encolsedTypeRef = typeSpec.getEnclosedTypeRef();
			while(encolsedTypeRef!=null) {
				if(encolsedTypeRef.getTypeSpec()!=null) {
					typeSpec = encolsedTypeRef.getTypeSpec();
					encolsedTypeRef = typeSpec.getEnclosedTypeRef();
				} else {
					encolsedTypeRef = null;
				}
			}
			if(typeSpec.isGenericInstance())
				return "!" + typeSpec.getGenericParameterNumber(); //typeSpec.getName();
//			else if(typeSpec.isGeneric())
//				return "!" + renderClassRef(type, true);
		}
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
		} else if(ByteReader.testFlags(flags, Type.FLAGS_STRING_FORMAT_BIT_MASK, Type.FLAGS_STRING_FORMAT_CUSTOM)) {
			//FIXME: custom string format
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
		
		if(type.getExtends()!=null && type.getExtends().getShortSystemName()!="object") {
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
		
		buffer.append(newLine);
		buffer.append("{");
		
		buffer.append(render(type.getClassLayout()));
		
		for(CustomAttribute c:type.getCustomAttributes()) {
			buffer.append(newLine);
			buffer.append("  ");
			buffer.append(addTab(render(c)));
		}
		
		buffer.append(newLine);
		buffer.append("  // ..."); //TODO: Thinks about a betters solution
		buffer.append(newLine);
		buffer.append("}");
		
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
		
		buffer.append(type.getName());
		
		Parameter [] genericParams = type.getGenericParameters();
		
		if(genericParams!=null &&genericParams.length>0) {
			boolean firstParam = true;
			buffer.append("<");
			for(Parameter p : genericParams) {
				if(!firstParam)
					buffer.append(",");
				else
					firstParam = false;
				buffer.append(p.getName());
				
			}
			buffer.append(">");
		}
		
		if(type.getExtends()!=null  && type.getExtends().getShortSystemName()!="object") {
			buffer.append(" extends ");
			buffer.append(renderClassRef(type.getExtends(), false));
		}
		
		TypeRef[] interfaces = type.getInterfaces();
		if(interfaces.length!=0) {
			buffer.append(" implements ");
			for(int i=0;i<interfaces.length;i++) {
				if(i!=0) buffer.append(", ");
				buffer.append(renderClassRef(interfaces[i], false));
			}
		}
		
		buffer.append(newLine);
		buffer.append("{");
		
		buffer.append(render(type.getClassLayout()));
		
		for(CustomAttribute c:type.getCustomAttributes()) {
			buffer.append(newLine);
			buffer.append("  ");
			buffer.append(addTab(render(c)));
		}
		
		for(Property p:type.getProperties()) {
			buffer.append(newLine);
			buffer.append("  ");
			buffer.append(addTab(render(p)));
			buffer.append(newLine);
		}
		
		for(Field f:type.getFields()) {
			buffer.append(newLine);
			buffer.append("  ");
			buffer.append(addTab(render(f)));
		}
		
		buffer.append(newLine);
		
		for(Method m:type.getMethods()) {
			buffer.append(newLine);
			buffer.append("  ");
			buffer.append(addTab(render(m)));
			buffer.append(newLine);
		}
		
		for(Type t:type.getEnclosingClasses()) {
			buffer.append(newLine);
			buffer.append("  ");
			buffer.append(addTab(render(t)));
			buffer.append(newLine);
		}
		
		
		//events properties...
		
		//overrides
		
		buffer.append(newLine);
		buffer.append("}");
		
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
		
		buffer.append("()");
		buffer.append(newLine);
		buffer.append("{");
		
		for(Method m:property.getMethods()) {
			if(m.getName().startsWith("get_")) {
				buffer.append(newLine);
				buffer.append("  .get instance ");
			} else if(m.getName().startsWith("set_")) {
				buffer.append(newLine);
				buffer.append("  .set instance ");
			} else {
				buffer.append(newLine);
				buffer.append("  .other ");
			}
			buffer.append(renderMethodRef(m, true));
		}
		
		buffer.append(newLine);
		buffer.append("}");
		
		return buffer.toString();
	}


	@Override
	public String render(Parameter parameter)
	{
		return render(parameter, false);
	}
	
	public String renderAsReference(Parameter parameter)
	{
		return render(parameter, true);
	}
	
	public String render(Parameter parameter, boolean asReference) {
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
		
		if(parameter.getMarshalSignature()!=null) {
			buffer.append(" ");
			buffer.append(render(parameter.getMarshalSignature()));
		}
		
		if(parameter.getName()!=null) {
			if(asReference)
				buffer.append(" /* ");
			else
				buffer.append(" '");
			
			buffer.append(parameter.getName());
			
			if(asReference)
				buffer.append(" */");
			else
				buffer.append("'");
		}
		
		return buffer.toString();
	}

	@Override
	public String render(NativeImplementation nativeImplementation) {
		
		return "";
	}

	public String renderMethodDefSig(Method method, boolean asReference) {
		if(method==null) return "";
		
		StringBuffer buffer = new StringBuffer(32);

		if(method.getMethodSignature().getReturnParameter()!=null) {
			buffer.append(render(method.getMethodSignature().getReturnParameter()));
		} else {
			buffer.append(renderClassRef(method.getMethodSignature().getReturnType()));
		}
		buffer.append(" ");
		
		MethodAndFieldParent parent = method.getOwner();
		
		if(asReference && parent.getTypeRef()!=null) {
			buffer.append(renderClassRef(parent.getTypeRef()));
			buffer.append("::");
		}

		buffer.append(method.getName());
		
		buffer.append(render(method.getMethodSignature(), asReference));
		
		return buffer.toString();

	}
	
	@Override
	public String render(MethodSignature methodSignature) {
		return render(methodSignature, false);
	}
	
	public String render(MethodSignature methodSignature, boolean asReference) {
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
				
					buffer.append(render(parameter[i], asReference));
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
		
		buffer.append(renderMethodDefSig(method, false));
		buffer.append(" ");
		
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
		
		buffer.append(newLine);
		buffer.append("{");	
		
		for(CustomAttribute c: method.getCustomAttributes()) {
			buffer.append(newLine);
			buffer.append("  ");
			buffer.append(render(c));
		}
		
		buffer.append(newLine);
		buffer.append("  ");
		buffer.append(addTab(render(method.getMethodBody(), method.getDebungInformation())));
		
		buffer.append(newLine);
		buffer.append("}");
		
		return buffer.toString();
	}

	private String renderMethodRef(Method method, boolean asReference) {
		return renderMethodDefSig(method, asReference);
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
			buffer.append(newLine);
			buffer.append(".entrypoint ");
		}

		buffer.append(newLine);
		buffer.append(".maxstack ");
		buffer.append(methodBody.getMaxStack());
		
		TypeRef[] locals = methodBody.getLocalVars();
		if(locals!=null && locals.length>0) {
			if(ByteReader.testFlags(methodBody.getFlags(), MethodBody.FLAGS_INIT_LOCAL)) {
				buffer.append(newLine);
				buffer.append(".locals init(");
			} else {
				//this produces unverifiable code - however it is possible
				buffer.append(newLine);
				buffer.append(".locals(");
			}
			int counter = 0;
			int last = methodBody.getLocalVars().length-1;
			for(TypeRef type : methodBody.getLocalVars()) {
				//IMPROVE: Resolve local names - maybe via PDB
				buffer.append(String.format("%s  [%d] %s %s%s", newLine, counter, renderClassRef(type), "local" + counter, counter==last?"":","));
				counter++;
			}
			buffer.append(newLine);
			buffer.append(")");
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
					buffer.append(newLine);
					buffer.append(".line ");
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
			buffer.append(String.format("%sIL_%04x: %s", newLine, instructionStart, i.render(this)));	
		}

		if(methodBody.getExceptionClauses().length>0) {
			buffer.append(newLine);
		}
		for(ExceptionClause e: methodBody.getExceptionClauses()) {
			buffer.append(String.format("%s.try IL_%04x to IL_%04x ", newLine, e.getTryOffset(), e.getTryOffset() + e.getTryLength()) );
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
		
		if(instance.getBoxedInstance()!=null) {
			return render(instance.getBoxedInstance());
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
				
		//buffer.append("(");
		boolean isSystemType = typeRef.getFullQualifiedName().equals("System.Type");
			
		if(isSystemType) {
			buffer.append("type (class ");
		} else {
			buffer.append(renderAsReference(typeRef));
			buffer.append("(");
		}
		
		String stringValue = instance.getStringValue();
		if(stringValue!=null) {
			//if(typeRef.getElementTypeKind()==TypeKind.ELEMENT_TYPE_STRING) {
				buffer.append("'");
				buffer.append(stringValue);
				buffer.append("'");
			//} else {
			//	buffer.append(stringValue);
			//}
		} else {
			long value = instance.getValue();
			if(value==0 && !ArrayUtils.contains(TypeKind.NUMERIC_TYPES, typeRef.getElementTypeKind())) {
				buffer.append("nullptr");
			} else {
				buffer.append(value);
			}
		}
		buffer.append(")");
	
		
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
		
		TypeRef typeRef = field.getTypeRef();
		TypeSpec typeSpec = typeRef.getTypeSpec();
		
		if(typeSpec!=null && typeSpec.isGenericInstance())
			buffer.append(String.format(".field %s !%s %s", renderFieldFlags(field), typeSpec.getName(), field.getName()));
		else
		   buffer.append(String.format(".field %s %s %s", renderFieldFlags(field), renderClassRef(typeRef), field.getName()));
		
		CustomAttribute [] attributes = field.getCustomAttributes();

		if(attributes.length==1) {
			buffer.append(newLine);
			buffer.append(render(field.getCustomAttributes()[0]));
		} else if(attributes.length>1) {
			buffer.append(newLine);
			buffer.append("{");	
			
			for(CustomAttribute c: field.getCustomAttributes()) {
				buffer.append(newLine);
				buffer.append("  ");
				buffer.append(render(c));
			}
		
			buffer.append(newLine);
			buffer.append("}");
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
		
		buffer.append(newLine);
		buffer.append("{");
		
		for(Method m:event.getMethods()) {

			if(m.getName().startsWith("add_")) {
				buffer.append(newLine);
				buffer.append("  .addon ");
			} else if(m.getName().startsWith("remove_")) {
				buffer.append(newLine);
				buffer.append("  .removeon ");
			} else if(m.getName().startsWith("fire_")) {
				buffer.append(newLine);
				buffer.append("  .fire ");
			} else {
				buffer.append(newLine);
				buffer.append("  .other ");
			}
			buffer.append(renderMethodRef(m, false));
		}
		
		buffer.append(newLine);
		buffer.append("}");
		
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
		
		buffer.append(newLine);
		buffer.append("  .size ");
		buffer.append(classLayout.getClassSize());
		buffer.append(newLine);
		buffer.append("  .pack ");
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
		buffer.append(newLine);
		buffer.append("{");
		
		for(TypeRef type :namespace.getTypeRefs()) {
			if(type.getType()!=null) {
				buffer.append(newLine);
				buffer.append("  ");
				if(renderCompact) {
					buffer.append(addTab(renderCompact(type.getType())));
				} else {
					buffer.append(addTab(render(type.getType())));
				}
			}
		}
		
		buffer.append(newLine);
		buffer.append("}");
		
		return buffer.toString();
	}

	@Override
	public String renderAsReference(TypeSpec typeSpec) {
		if(typeSpec.isGenericInstance())
			return "!" + typeSpec.getName();
			
		StringBuffer buffer = new StringBuffer(32);
		ResolutionScope resolutionScope = typeSpec.getResolutionScope();
		TypeRef typeRef = typeSpec;
		
		boolean addResolutionScope = false;
		if(typeSpec.isValueType()) {
			buffer.append("valuetype ");
			addResolutionScope = true;
			
		}
//		else if(!typeSpec.isBasicType()){
//			buffer.append("class ");
//		}
		else if(typeSpec.isClass() || (typeSpec.getEnclosedTypeRef()!=null&&typeSpec.getEnclosedTypeRef().isClass()) ) {
			buffer.append("class ");
			addResolutionScope = true;
		}
		
		if(addResolutionScope){	
			while((resolutionScope==null || resolutionScope.isInAssembly()) && typeRef.getTypeSpec()!=null && typeRef.getTypeSpec().getEnclosedTypeRef()!=null) {
				typeRef = typeRef.getTypeSpec().getEnclosedTypeRef();
				resolutionScope = typeRef.getResolutionScope();
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
		}
		
//		if(typeSpec.isGenericInstance())
//		{
//			buffer.append(typeSpec.getNamespace());
//			buffer.append(".");
//			buffer.append(typeSpec.getExtName());
//			
//			return buffer.toString();
//		}

		buffer.append(typeSpec.getExtFullQualifiedName());
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
		
		buffer.append(renderAsReference(method.getMethodSignature()));
		
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
		}
		
		MethodAndFieldParent parent = memberRef.getOwner();
		
		if(parent.getTypeRef()!=null) {
			if(memberRef.getTypeRef()!=null) {
				TypeSpec typeSpec = memberRef.getTypeRef().getTypeSpec();
				
				if(typeSpec!=null) {
					TypeRef encolsedTypeRef = typeSpec.getEnclosedTypeRef();
					while(encolsedTypeRef!=null) {
						if(encolsedTypeRef.getTypeSpec()!=null) {
							typeSpec = encolsedTypeRef.getTypeSpec();
							encolsedTypeRef = typeSpec.getEnclosedTypeRef();
						} else {
							encolsedTypeRef = null;
						}
					}
					if(typeSpec.isGenericInstance()) {
						buffer.append("!");
						buffer.append(typeSpec.getGenericParameterNumber());
						buffer.append(" ");
					}
				}
			}
			
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
			
		if(signature!=null) {
			buffer.append(renderAsReference(signature));
		}			
		
		return buffer.toString();
	}

	public String renderAsReference(Field field) {
		String type = renderAsReference(field.getTypeRef());
		
		if(field.getParent()==null)
			return type + field.getName();
		
		return  type + " " + field.getParent().getFullQualifiedName() + "::" + field.getName();
	}

	@Override
	public String renderAsReference(StandAloneSigEntry standAlone) {
		if(standAlone.getTypeRef()!=null)
			return renderAsReference(standAlone.getTypeRef());
		else if(standAlone.getMethodSignature()!=null)
			return renderAsReference(standAlone.getMethodSignature());
		
		return "unimplemented local sig";
	}

	public String renderAsReference(MethodSignature methodSignature) {
		return render(methodSignature, true);
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
		String head = 	"//Decompiled with Facile (" + assembly.getFileName() + ")" +
						newLine + "//===========================================" + 
						newLine + "//" + new Date(System.currentTimeMillis()).toString();
		String file = assembly.getName() + ".il";
		byte [] extraSpace = (newLine + newLine).getBytes("UTF8");
		
		if(!directory.endsWith(System.getProperty("file.separator"))) {
			file = directory + System.getProperty("file.separator") + file;
		} else {
			file = directory + file;
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		
		fos.write(head.getBytes("UTF8"));
		fos.write(extraSpace);
		
		for(AssemblyRef ref :assembly.getAssemblyRefs()) {
			fos.write(render(ref).getBytes("UTF8"));
			fos.write(extraSpace);
		}
		
		fos.write(render(assembly).getBytes("UTF8"));
		fos.write(extraSpace);
		
		fos.write(render(assembly.getModule()).getBytes("UTF8"));
		fos.write(extraSpace);
		
		for(Namespace ns :assembly.getModule().getNamespaces()) {
			if(ns.getName()==null || ns.getName().equals("")) continue;
			fos.write(render(ns, false).getBytes("UTF8"));
			fos.write(extraSpace);
		}
		
		fos.write(extraSpace);
		
		fos.close();
	}
	
}

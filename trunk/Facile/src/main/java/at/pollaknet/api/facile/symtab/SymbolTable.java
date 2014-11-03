package at.pollaknet.api.facile.symtab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.code.CilContainer;
import at.pollaknet.api.facile.code.MethodBody;
import at.pollaknet.api.facile.exception.InvalidMethodBodyException;
import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.header.cli.CliHeader;
import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.entries.AssemblyEntry;
import at.pollaknet.api.facile.metamodel.entries.ClassLayoutEntry;
import at.pollaknet.api.facile.metamodel.entries.ConstantEntry;
import at.pollaknet.api.facile.metamodel.entries.CustomAttributeEntry;
import at.pollaknet.api.facile.metamodel.entries.DeclSecurityEntry;
import at.pollaknet.api.facile.metamodel.entries.EventMapEntry;
import at.pollaknet.api.facile.metamodel.entries.FieldEntry;
import at.pollaknet.api.facile.metamodel.entries.FieldLayoutEntry;
import at.pollaknet.api.facile.metamodel.entries.FieldMarshalEntry;
import at.pollaknet.api.facile.metamodel.entries.FieldRVAEntry;
import at.pollaknet.api.facile.metamodel.entries.GenericParamConstraintEntry;
import at.pollaknet.api.facile.metamodel.entries.GenericParamEntry;
import at.pollaknet.api.facile.metamodel.entries.ImplMapEntry;
import at.pollaknet.api.facile.metamodel.entries.InterfaceImplEntry;
import at.pollaknet.api.facile.metamodel.entries.ManifestResourceEntry;
import at.pollaknet.api.facile.metamodel.entries.MemberRefEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodDefEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodImplEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodSemanticsEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodSpecEntry;
import at.pollaknet.api.facile.metamodel.entries.NestedClassEntry;
import at.pollaknet.api.facile.metamodel.entries.ParamEntry;
import at.pollaknet.api.facile.metamodel.entries.PropertyEntry;
import at.pollaknet.api.facile.metamodel.entries.PropertyMapEntry;
import at.pollaknet.api.facile.metamodel.entries.StandAloneSigEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.metamodel.entries.aggregation.INamespaceOwner;
import at.pollaknet.api.facile.pdb.PdbReader;
import at.pollaknet.api.facile.symtab.signature.CustomAttributeValueSignature;
import at.pollaknet.api.facile.symtab.signature.DeclSecuritySignature;
import at.pollaknet.api.facile.symtab.signature.FieldSignature;
import at.pollaknet.api.facile.symtab.signature.LocalVarSignature;
import at.pollaknet.api.facile.symtab.signature.MethodDefOrRefSignature;
import at.pollaknet.api.facile.symtab.signature.MethodSpecSignature;
import at.pollaknet.api.facile.symtab.signature.ParamOrFieldMarshalSignature;
import at.pollaknet.api.facile.symtab.signature.PropertyEntrySignature;
import at.pollaknet.api.facile.symtab.signature.Signature;
import at.pollaknet.api.facile.symtab.signature.TypeSpecSignature;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.util.ByteReader;


/**
 * The symbol table connects all available information and represents them as
 * a single assembly which has the properties of an ordinary .Net assembly.
 */
public class SymbolTable {

	private BasicTypesDirectory directory;
	private AssemblyEntry assembly;
	private MetadataModel metaModel;
	private CilContainer codeContainer;
	private PdbReader pdbReader;
	
	//private int numberOfSignatures = 0;
	//private int numberOfDecodedSignatures = 0;
	private BlobStream blobStream;
	private CliHeader cliHeader;

	private boolean haltOnErrors;
	private boolean haltOnJniErrors;
	
	public SymbolTable(CliHeader cliHeader, MetadataModel metaModel, BlobStream blobStream, CilContainer codeContainer, PdbReader pdbReader, String pathToAssemby) {
		assert(metaModel!=null);
		assert(codeContainer!=null);
		assert(cliHeader!=null);
		
		this.cliHeader = cliHeader;
		this.metaModel = metaModel;
		this.codeContainer = codeContainer;
		this.pdbReader = pdbReader;
		this.blobStream = blobStream;
		
		assert(metaModel.assembly!=null);
		assert(metaModel.assembly[0]!=null);
		int pos = pathToAssemby.lastIndexOf(System.getProperty("file.separator")) + 1;
		if(pos>0&&pos<pathToAssemby.length()) {
			metaModel.assembly[0].setFileName(pathToAssemby.substring(pos));
		} else {
			metaModel.assembly[0].setFileName(pathToAssemby);
		}
	}
	
	public void build(FacileReflector facileReflector) {
	
		haltOnErrors = facileReflector.getHaltOnErrors();
		haltOnJniErrors = facileReflector.getHaltOnJniErrors();
		
		//See ECMA 335-Partition II, 22. Metadata logical format
		//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=229&view=FitH

		//Connecting the information of the following meta data table entries:
		//
		//	Assembly : 0x20            		GenericParamConstraint : 0x2C  
		//	AssemblyOS : 0x22               ImplMap : 0x1C                 
		//	AssemblyProcessor : 0x21        InterfaceImpl : 0x09           
		//	AssemblyRef : 0x23             	ManifestResource : 0x28        
		//	AssemblyRefOS : 0x25           	MemberRef : 0x0A               
		//	AssemblyRefProcessor : 0x24    	MethodDef : 0x06               
		//	ClassLayout : 0x0F             	MethodImpl : 0x19              
		//	Constant : 0x0B                	MethodSemantics : 0x18         
		//	CustomAttribute : 0x0C         	MethodSpec : 0x2B              
		//	DeclSecurity : 0x0E            	Module : 0x00                  
		//	EventMap : 0x12                	ModuleRef : 0x1A               
		//	Event : 0x14                   	NestedClass : 0x29             
		//	ExportedType : 0x27            	Param : 0x08                   
		//	Field : 0x04                   	Property : 0x17                
		//	FieldLayout : 0x10            	PropertyMap : 0x15             
		//	FieldMarshal : 0x0D            	StandAloneSig : 0x11           
		//	FieldRVA : 0x1D                	TypeDef : 0x02                 
		//	File : 0x26                    	TypeRef : 0x01                 
		//	GenericParam : 0x2A            	TypeSpec : 0x1B        
		
		directory = new BasicTypesDirectory(
				metaModel, blobStream, facileReflector.getReferenceAssemblies(), facileReflector.getReferneceEnums());
		
		connectNestedClass();
		
		connectAllAssemblies();
		
		connectModule();
		
		connectModuleRef();
		
		connectFileRef();
		
		connectMethodImpls();
		
        connectTypes();

        connectGenericParamterAndConstraints();

		connectManifestResource();
		
		connectCustomAttribute();
				
		//connect events and methods with the methods (double link)
		connectMethodSemantics();
		
		connectFields();
			
		//set native types
		connectFieldMarshal();
		
		//build symbol table
		connectClassLayout();
		connectConstant();
		connectDeclSecurity();
		connectFieldLayout();
		connectFieldRva();
		
		connectParam();
		
		connectImplMap();
		
		connectInterface();	
		
		connectMethodDef(codeContainer, pdbReader);
		
		//set all properties and events (from the maps)
		connectPropertyMapAndProperty();
		connectEventMapAndEvent();
		
		connectMethodSpec();
		
		connectMemberRef();
		
		connectStandAloneSignatures();
		
		connectCustomAttributes();
		
		//Logger.getLogger(FacileReflector.LOGGER_NAME).info(String.format("Extracted %d out of %d signatures.", numberOfDecodedSignatures, numberOfSignatures));
		//assembly.setLoaded(true);
		
		connectExportedType();

		connectGenericTypeNames();
		
		connectSignatureEmbeddedTypes(metaModel.typeDef);
		connectSignatureEmbeddedTypes(metaModel.typeRef);
		connectSignatureEmbeddedTypes(metaModel.typeSpec);
		
		//get the type specs collected during signature parsing
		TypeSpecEntry [] signatureEmbeddedTypeSpecs = directory.getEmbeddedTypeSpecs().toArray(new TypeSpecEntry [] {});
		
		connectSignatureEmbeddedTypes(signatureEmbeddedTypeSpecs);
		
//		resolvedDelayedGenerics(metaModel.typeSpec);
//		resolvedDelayedGenerics(signatureEmbeddedTypeSpecs);

		finalizeAssembly(signatureEmbeddedTypeSpecs);
		
		//IMPROVE: remove unused data structures (initial byte buffer - depends on lazy loading)
	}

	public void connectGenericTypeNames() {
		for(ParamEntry param: metaModel.param)
			param.linkGenericNameToType();
				
		for(TypeSpecEntry typeSpec: metaModel.typeSpec) {
			typeSpec.propagateGenericArguments();
		}
		
//		for(MethodDefEntry methodDefEntry: metaModel.methodDef) {
//			Parameter param = methodDefEntry.getMethodSignature().getReturnParameter();
//			if(param==null) {
//				int z=0;
//				z++;
//			} else if(param.getTypeRef()!=null && param.getTypeRef().getName()!=null && param.getTypeRef().getName().equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME)) {
//				int t=0;
//				t++;
//			}
//		}
		
		//fixup method specs
		for(MethodSpecEntry methodSpecEntry: metaModel.methodSpec) {
			Method method = methodSpecEntry.getMethod().getMethod();
			
			if(method!=null) {
				Parameter [] genericParams = method.getGenericParameters();
				if(genericParams.length>0) {
					TypeRef typeRef = method.getMethodSignature().getReturnType(); //return getReturnParameter could be null!!!! Needs docu improvement
					if(typeRef!=null) {
						TypeSpecEntry typeSpecEntry = (TypeSpecEntry)typeRef.getTypeSpec();
						if(typeSpecEntry!=null && typeSpecEntry.isGenericInstance() && (typeSpecEntry.getName()==null )) {
							//|| typeSpecEntry.getName().equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME))) {
							typeSpecEntry.setName(genericParams[0].getName());
						}
					}
					
					TypeSpec [] genericInstances = method.getGenericInstances();
					
					if(genericInstances.length>0) {
						for(int i=0;i<genericInstances.length;i++) {
							if(i<genericParams.length && (genericInstances[i].getName()==null)) {
								//|| genericInstances[i].getName().equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME))) {
								((TypeSpecEntry)genericInstances[i]).setName(genericParams[i].getName());
							}
						}
					}
				}
				
			}
		}
	}
	
//	private static void resolvedDelayedGenerics(TypeRefEntry types[]) {
//		for(TypeRefEntry type: types) {
//
//			if(type.getName()==null && type.getTypeSpec()!=null && type.getTypeSpec().isGenericInstance()) {
//				int j=0;
//				j++;
//			}
//			else if(type.getName()!=null && type.getName().equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME)) {
//				int j=0;
//				j++;
//			}
//		}
//	}

	private void connectMethodImpls() {
		for(MethodImplEntry m: metaModel.methodImpl) {
			Method method = m.getImplementationBody().getMethod();
			if(m.getOwnerClass()!=null) {
				m.getOwnerClass().addMethod((MethodDefEntry)method);
			} else if (!metaModel.containsDeletedData()) {
				if(haltOnErrors)
					throw new NullPointerException("The owner of a method is null: " + m.toString());
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(
					Level.SEVERE, "The owner of a method is null: " + m.toString());				
			}		
		}
	}

	private void connectFields() {

		for(FieldEntry fieldEntry : metaModel.field) {
			try {
				FieldSignature.decodeAndAttach(directory, fieldEntry);
				ParamOrFieldMarshalSignature.decodeAndAttach(directory, fieldEntry);
				fieldEntry.linkGenericNameToType();
			} catch (InvalidSignatureException e) {
				if(haltOnErrors) throw e;
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(
						Level.SEVERE, "Faild to decode signature: " + fieldEntry.toString());
			}
		}
	}

	private void connectCustomAttributes() {
		for(CustomAttributeEntry customAttribute : metaModel.customAttribute) {
			assert(customAttribute!=null);
			
			if(customAttribute.getValue()!=null) {
				try {
					CustomAttributeValueSignature.decodeAndAttach(directory, metaModel, customAttribute);
				} catch (InvalidSignatureException e) {
					if(haltOnErrors) throw e;
					Logger.getLogger(FacileReflector.LOGGER_NAME).log(
								Level.SEVERE, "Faild to decode signature: " + customAttribute.toString());
				}
			}
		}
	}

	private void connectMemberRef() {
		for(MemberRefEntry memberRef : metaModel.memberRef) {
			assert(memberRef!=null);
			assert(memberRef.getBinarySignature()!=null);
			assert(memberRef.getBinarySignature().length>0);
			
			try {
				if(memberRef.getBinarySignature()[0]==Signature.PREFIX_FIELD) {
					FieldSignature.decodeAndAttach(directory, memberRef);
				} else {
					MethodDefOrRefSignature.decodeAndAttach(directory, memberRef);
				}
				//memberRef.getOwnerClass().addMemberRef(memberRef);
				
			} catch (InvalidSignatureException e) {
				if(haltOnErrors) throw e;
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(
							Level.SEVERE, "Faild to decode signature: " + memberRef.toString());
			}
		}
	}

	private void connectStandAloneSignatures() {
		for(StandAloneSigEntry standAlone : metaModel.standAloneSig) {
			assert(standAlone.getBinarySignature()!=null);
			assert(standAlone.getBinarySignature().length>0);
			
			try {
				switch(standAlone.getBinarySignature()[0]) {
					case Signature.PREFIX_FIELD:
						FieldSignature.decodeAndAttach(directory, standAlone);
						break;
					case Signature.PREFIX_LOCAL_VAR:
						//ignore locals because they are assigned to the methods
						break;
					default:
						MethodDefOrRefSignature.decodeAndAttach(directory, standAlone);
						break;
				}
				
			} catch (InvalidSignatureException e) {
				if(haltOnErrors) throw e;
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(
							Level.SEVERE, "Faild to decode signature: " + standAlone.toString());
			}
		}
	}

	private void connectMethodSpec() {
		for(MethodSpecEntry methodSpec : metaModel.methodSpec) {
			try {
				MethodSpecSignature.decodeAndAttach(directory, methodSpec);
			} catch (InvalidSignatureException e) {
				if(haltOnErrors) throw e;
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(
							Level.SEVERE, "Faild to decode signature: " + methodSpec.toString());
			}
		}
	}

	private void connectFileRef() {
		assembly.setFileRefs(metaModel.file);
	}

	private void connectModuleRef() {
		assembly.setModuleRefs(metaModel.moduleRef);
	}

	private void connectGenericParamterAndConstraints() {
		
		//set constraints for generic parameter
		for(GenericParamConstraintEntry constraint: metaModel.genericParamConstraint) {
			if(constraint.getOwner()!=null) {
				constraint.getOwner().addConstraint(constraint.getConstraint());
				//param.getOwner().setName(param.getName());
			} else if (!metaModel.containsDeletedData()) {
				throw new NullPointerException("The owner of a generic parameter constraint is null!");
			}
		}
			
		//connect generic parameters with the owners
		for(GenericParamEntry param: metaModel.genericParam) {
			if(param.getOwner()!=null) {
				param.getOwner().addGenericParam(param);
			} else if (!metaModel.containsDeletedData()) {
				throw new NullPointerException("The owner of a generic parameter is null!");
			}
		}
	}

	private void connectInterface() {
		for(InterfaceImplEntry interf: metaModel.interfaceImpl)
			interf.getImplementationClass().addInterface(interf.getInterface().getTypeRef());
	}

	private void connectImplMap() {
		for(ImplMapEntry impl: metaModel.implMap)
			impl.getMemberForwarded().setNativeImplementation(impl);
	}

	private void connectFieldRva() {
		for(FieldRVAEntry fieldRVA: metaModel.fieldRva)
			fieldRVA.getField().setRelativeVirtualAddress(fieldRVA.getRelativeVirtualAddress());
	}

	private void connectFieldLayout() {
		for(FieldLayoutEntry layout: metaModel.fieldLayout)
			layout.getField().setFieldLayout(layout);
	}

	private void connectDeclSecurity() {
		for(DeclSecurityEntry security: metaModel.declSecurity) {
			
			try {
				DeclSecuritySignature.decodeAndAttach(directory, security);
			} catch (InvalidSignatureException e) {
				if(haltOnErrors) throw e;
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(
							Level.SEVERE, "Faild to decode signature: " + security.toString());
			}
		}
	}

	private void connectConstant() {
		for(ConstantEntry constant: metaModel.constant)
			constant.getParent().setConstant(constant);
	}

	private void connectClassLayout() {
		for(ClassLayoutEntry layout: metaModel.classLayout)
			layout.getParent().setClassLayout(layout);
	}

	private void connectMethodDef(CilContainer codeContainer, PdbReader pdbReader) {
		//locals in order to generate appropriate log entries for assemblies with screwed up RVAs
		long prevRVA = -1;
		String prevMethod = "[not set]";
		
		//NOTE: methods are not always ordered ascending by the virtual address (the pdb should have the same system)
		
		//virtual address calculation
		long nativeCodeRVA = 0;
		long methodRVA = 0;
		int containerSize = codeContainer.getCodeBuffer().length;
	
		//counter in order to assign metadata tokens to the methods		
		int tokenCounter=1;
		
		//check each method
		for(MethodDefEntry method: metaModel.methodDef) {
			methodRVA = method.getVirtualAddress();
			
			if(method.getParams()!=null)
				for(ParamEntry param :method.getParams())
					param.setOwner(method);

			if(method.getGenericParameters()!=null)
			{
				for(GenericParamEntry param :method.getGenericParameters())
					param.setOwner(method);
			}
				
			//check if the address is valid
			if(	methodRVA!=0) {
				
				MethodBody methodBody = null;
				
				if(		metaModel.assemblyHasIlSection() ||
						ByteReader.testFlags(method.getImplFlags(),
							Method.IMPL_FLAGS_CODE_TYPE_NATIVE |
							Method.IMPL_FLAGS_ORGANISATION_UNMANAGED |
							Method.IMPL_FLAGS_PRESERVE_SIG )			) {
					//ignore unmanaged native (preserved) method bodies
					methodBody = new MethodBody(tokenCounter);
				} else if(	ByteReader.testFlags(method.getFlags(),
								Method.FLAGS_SPECIAL_NAME |
								Method.FLAGS_ADDITIONAL_RT_SPECIAL_NAME) && 		
							method.getName().toLowerCase().contains("_deleted")		){
					//perform an extra handling for deleted method bodies
					try {
						methodBody = new MethodBody(metaModel, codeContainer, methodRVA, tokenCounter);
					} catch(InvalidMethodBodyException e) {
						String msg = String.format("Detected deleted method body: %s.%s%s Flags: 0x%04x ImplFlags: 0x%04x",
								method.getOwner().getTypeRef().getName(),
								method.getName(),
								method.getMethodSignature()==null?"()":method.getMethodSignature().toString(),
								method.getFlags(),
								method.getImplFlags()
						);
						Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.INFO, msg);
						methodBody = new MethodBody(tokenCounter);
					}
				} else {
					try {
						methodBody = new MethodBody(metaModel, codeContainer, methodRVA, tokenCounter);
					} catch(InvalidMethodBodyException e) {
						//don't re-throw this exception because a obfuscated assembly could cause an invalid method body
						//if(FacileReflector.DEBUG_AND_HALT_ON_ERRORS) throw e;
						String msg = String.format("Detected invalid method body: %s.%s%s Flags: 0x%04x ImplFlags: 0x%04x",
								method.getOwner().getTypeRef().getName(),
								method.getName(),
								method.getMethodSignature()==null?"()":method.getMethodSignature().toString(),
								method.getFlags(),
								method.getImplFlags()
						);
						Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.WARNING, msg);
						methodBody = new MethodBody(tokenCounter);
					}
				}

				//extract the locals
				try {
					LocalVarSignature.decodeAndAttach(directory, methodBody);
				} catch (InvalidSignatureException e) {
					if(haltOnErrors) throw e;
					Logger.getLogger(FacileReflector.LOGGER_NAME).log(
								Level.SEVERE, "Faild to decode signature: " + methodBody.toString());
				}
				method.setMethodBody(methodBody);
				
				//set the related debug information (if available)
				if(pdbReader!=null) {
					try {
						method.setDebungInformation(pdbReader.getLineNumbersByRVA(nativeCodeRVA));
					} catch(Error e) {
						if(haltOnJniErrors) throw e;
						Logger.getLogger(FacileReflector.LOGGER_NAME).log(
									Level.SEVERE, "Failed to query line number information: " + method.toString() + " at RVA " + nativeCodeRVA);
					}
				}

				//IMPROVE: perform an alternative RVA calculation
				if(methodRVA<prevRVA) {
					Logger.getLogger(FacileReflector.LOGGER_NAME).log(pdbReader!=null?Level.SEVERE:Level.INFO,
							String.format("Method %s with VA 0x%x is extracted after method %s with RVA 0x%x.",
									method.getName(), methodRVA, prevMethod, prevRVA));
				}

				//compute virtual address from the code base (required for dia RVA)
				nativeCodeRVA+=method.getMethodBody().getCodeSize();
			}	
			
			//only process method with signatures
			//(some constructors require no signature)
			if(method.getBinarySignature()!=null) {
				//decode signatures
				try {
					MethodDefOrRefSignature.decodeAndAttach(directory, method);
				} catch (InvalidSignatureException e) {
					if(haltOnErrors) throw e;
					Logger.getLogger(FacileReflector.LOGGER_NAME).log(
								Level.SEVERE, "Faild to decode signature: " + method.toString());
				}
			}
			
			//prepare for next method body
			tokenCounter++;
			
			prevRVA = methodRVA;
			prevMethod = method.getName();
		}
	}
	
	private void connectModule() {
		
		//TODO: Handle assemblies with an invalid number of modules (e.g. more than 1)		
		if(metaModel.module.length>0) {
			assembly.setModule(metaModel.module[0]);
			
			//log stupid assemblies
			if(metaModel.module.length>1) {
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(metaModel.module.length<1?Level.INFO:Level.SEVERE,
						String.format("Unexpected number of module entries %d (allowed: 0 and 1).", metaModel.module.length));
			}
		}
	}

	private void connectAllAssemblies() {
	
		//for now don't care about the real number of present assemblies
		assembly = metaModel.assembly[0];
		
		//assign all types to the assembly (type ref and spec follow later)
		assembly.setTypes(metaModel.typeDef);
		
		//add the assembly as a reference for itself so that we can look up assembly internal types
		directory.getReferenceAssemblies().add(assembly);
		
		if(metaModel.assembly.length>1) {
			//TODO: Handling of multi module - multi assembly files
			Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.SEVERE,
					String.format("Unexpected number of assembly entries %d.", metaModel.assembly.length));
		}
		
		assembly.setAssemblyOs(metaModel.assemblyOs);
		assembly.setAssemblyProcessor(metaModel.assemblyProcessor);
		
		assembly.setAssemblyRefs(metaModel.assemblyRef);
		assembly.setAssemblyRefOs(metaModel.assemblyRefOs);
		assembly.setAssemblyRefProcessor(metaModel.assemblyRefProcessor);
	}
	
	private void finalizeAssembly(TypeSpecEntry[] signatureEmbeddedTypeSpecs) {
		Arrays.sort(metaModel.typeDef);
		Arrays.sort(metaModel.typeRef);
		Arrays.sort(metaModel.typeSpec);
		Arrays.sort(signatureEmbeddedTypeSpecs);
		
		//assign all types to the assembly
		//assembly.setTypes(metaModel.typeDef);
		assembly.setTypeRefs(metaModel.typeRef);
		assembly.setTypeSpecs(metaModel.typeSpec);
		assembly.setEmbeddedTypeSpecs(signatureEmbeddedTypeSpecs);
	}

	private void connectManifestResource() {
		long resourceDirectoryRVA = cliHeader.getAddrOfResourcesDirectory();
		byte [] buffer = codeContainer.getCodeBuffer();
		
		for(ManifestResourceEntry resource: metaModel.manifestResource) {
			//only resolve resources which are defined in the current file
			if(resource.getImplementation()==null) {
				//get the offset and translate it to a physical address
				long offset = resource.getOffset();
				int address = codeContainer.getPhysicalAddressOf(resourceDirectoryRVA + offset);
				
				//read the length and the resource
				long length = ByteReader.getUInt32(buffer, address);
				if(address+4+length<=buffer.length) {
					resource.setResource(ByteReader.getBytes(buffer, address+4, (int) length));
				} else {
					String msg = "Unable to locate resource \"" + resource.getName() + "\" at address " + (address+4) + ".";
					Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.INFO, msg);
				}
			}
		}
		
		assembly.setManifestResources(metaModel.manifestResource);
	}

	private void connectFieldMarshal() {
		for(FieldMarshalEntry marshal : metaModel.fieldMarshal)
			marshal.getParent().setBinaryMarshalTypeSignature(marshal.getNativeType());
	}

	private void connectMethodSemantics() {
		for(MethodSemanticsEntry semantics: metaModel.methodSemantics) {
			semantics.getMethod().setSemantics(semantics.getAssociation());
			semantics.getMethod().setSemanticsFlags(semantics.getSemantics());
		}
	}

	private void connectEventMapAndEvent() {
		for(EventMapEntry map: metaModel.eventMap)
			map.getParent().setEvents(map.getEvents());
	}

	private void connectPropertyMapAndProperty() {
		for(PropertyEntry property: metaModel.property) {
			try {
				PropertyEntrySignature.decodeAndAttach(directory, property);
			} catch (InvalidSignatureException e) {
				if(haltOnErrors) throw e;
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(
							Level.SEVERE, "Faild to decode signature: " + property.toString());
			}
		}
		
		for(PropertyMapEntry map: metaModel.propertyMap) {
			if(map.getParent()!=null)
				map.getParent().setProperties(map.getProperties());
		}
	}
	
	private void connectTypes() {
		HashMap<String, ArrayList<Type>> namespaceTypMap = new HashMap<String, ArrayList<Type>>();
		ArrayList<Type> currentTypesInNamespace;
		String namespace;		
		
		//handle all defined types
		if(metaModel.typeDef.length>0) {
			
			for(int index=0;index<metaModel.typeDef.length;index++) {

				//try to register the defined type
				directory.register(metaModel.typeDef[index]);
				
				//set a resolution scope
				//because the local type definitions
				//do not have an entry for that.
				if(!metaModel.typeDef[index].isExported() && metaModel.typeDef[index].getResolutionScope()==null)
					metaModel.typeDef[index].setResolutionScope(metaModel.module[0]);
				
				//resolve namespace
				if(!metaModel.typeDef[index].isNested()) {
					namespace = metaModel.typeDef[index].getNamespace();
					if(namespace==null)
						metaModel.typeDef[index].setNamespace("");
					currentTypesInNamespace = namespaceTypMap.get(namespace);
					if(currentTypesInNamespace==null) {
						currentTypesInNamespace = new ArrayList<Type>();
						namespaceTypMap.put(namespace, currentTypesInNamespace);
					}
					currentTypesInNamespace.add(metaModel.typeDef[index]);
				}
				//link methods of the current type to 
				//their parent (the type)
				metaModel.typeDef[index].linkMethodsToType();
			}
		}
		
		//collect the name spaces defined by the types
		NamespaceContainer [] namespaces = new NamespaceContainer[namespaceTypMap.values().size()];
		int index=0;
		for(ArrayList<Type> typesInNamespace: namespaceTypMap.values()) {
			String nameOfNamespace = typesInNamespace.get(0).getNamespace();
			TypeRef [] types = typesInNamespace.toArray(new Type[0]);
			Arrays.sort(types);
			namespaces[index] = new NamespaceContainer(	nameOfNamespace, types);
			index++;
		}
		
		//sort the name spaces and assign to the module (where they are defined)
		Arrays.sort(namespaces);
		metaModel.module[0].setNamespaces(namespaces);
		
		//collect the name spaces of the reference types 
		HashMap<ResolutionScope, HashMap<String, ArrayList<TypeRef>>> scopeMap = new HashMap<ResolutionScope, HashMap<String, ArrayList<TypeRef>>>();
		HashMap<String, ArrayList<TypeRef>> namespaceTypeRefMap = null;
		ResolutionScope currentScope = null;
		ArrayList<TypeRef> currentTypeRefNamespace = null;
		
		for(TypeRefEntry typeRef: metaModel.typeRef) {
			directory.register(typeRef);
			
			currentScope = typeRef.getResolutionScope();
			
			//use system scope if no scope has been set
			if(currentScope==null) currentScope = metaModel.module[0];
			
			namespaceTypeRefMap = scopeMap.get(currentScope);
			if(namespaceTypeRefMap==null) {
				namespaceTypeRefMap = new HashMap<String,ArrayList<TypeRef>>();
				scopeMap.put(currentScope, namespaceTypeRefMap);
			}
			
			//resolve namespace
			namespace = typeRef.getNamespace();
			//this also happens in some assemblies!
			if(namespace==null)
				typeRef.setNamespace(currentScope.getFullQualifiedName());
			
			//collect the name space
			currentTypeRefNamespace = namespaceTypeRefMap.get(namespace);
			if(currentTypeRefNamespace==null) {
				currentTypeRefNamespace = new ArrayList<TypeRef>();
				namespaceTypeRefMap.put(namespace, currentTypeRefNamespace);
			}
			currentTypeRefNamespace.add(typeRef);
			
		}

		//assign the collected name spaces to the scopes (e.g. ref. module)
		for(ResolutionScope scope: scopeMap.keySet()) {
			if(scope!=assembly.getModule()) {
				assert(namespaceTypeRefMap!=null);
				namespaceTypeRefMap = scopeMap.get(scope);
				
				namespaces = new NamespaceContainer[namespaceTypeRefMap.size()];
				index=0;
				for(ArrayList<TypeRef> typesRefsInNamespace: namespaceTypeRefMap.values()) {
					String nameOfNamespace = typesRefsInNamespace.get(0).getNamespace();
					TypeRef [] types = typesRefsInNamespace.toArray(new TypeRef[0]);
					Arrays.sort(types);
					namespaces[index] = new NamespaceContainer(	nameOfNamespace, types);
					index++;
				}
				
				Arrays.sort(namespaces);
				//every scope implements this interface
				((INamespaceOwner)scope).setNamespaces(namespaces);
			}
		}
		
		//type spec contain type def's and type ref's, so they have no own name space
		for(TypeSpecEntry typeSpec: metaModel.typeSpec) {
			try {
				TypeSpecSignature.decodeAndAttach(directory, typeSpec);
			} catch (InvalidSignatureException e) {
				if(haltOnErrors) throw e;
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(
							Level.SEVERE, "Failed to decode signature: " + typeSpec.toString());
			}
			
			//will re-evaluate the name space of the enclosed type as well
			typeSpec.setResolutionScope(assembly.getModule());
		}

	}
	
	private void connectSignatureEmbeddedTypes(TypeRefEntry [] typeRefs) {
		if(typeRefs!=null && typeRefs.length>0)
			for(TypeRefEntry type: typeRefs) {
				//will re-evaluate the name space of the enclosed type as well
				type.adjustNamespace(assembly.getModule());
			}
	}
	

	private void connectCustomAttribute() {
		//add the custom attributes
		for(CustomAttributeEntry customAttribute: metaModel.customAttribute) {
			if(customAttribute.getOwner()!=null) {
				customAttribute.getOwner().addCustomAttribute(customAttribute);
			} else if (!metaModel.containsDeletedData()) {
				if(haltOnErrors)
					throw new NullPointerException(
							"The owner of a custom attribute is null: " + customAttribute.toString());
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(Level.SEVERE,
						"The owner of a custom attribute is null: " + customAttribute.toString());
			}
		}
	}
	
	private void connectParam() {
		//add the custom attributes
		for(ParamEntry param: metaModel.param) {
			try {
				ParamOrFieldMarshalSignature.decodeAndAttach(directory, param);
				//param.linkGenericNameToType();
			} catch (InvalidSignatureException e) {
				if(haltOnErrors) throw e;
				Logger.getLogger(FacileReflector.LOGGER_NAME).log(
							Level.SEVERE, "Faild to decode signature: " + param.toString());
			}
		}
	}

	
	private void connectNestedClass() {
		for(NestedClassEntry nested: metaModel.nestedClass) {
			nested.getNestedClass().setNested(true);
			nested.getEnclosingClass().addNestedClass(nested.getNestedClass());
			
			String namespace = nested.getEnclosingClass().getNamespace();
			if(namespace==null || namespace.equals("")) {
				namespace = nested.getEnclosingClass().getName();
			} else {
				namespace += "." + nested.getEnclosingClass().getName();
			}
			nested.getNestedClass().setNamespace(namespace);
		}
	}

	private void connectExportedType() {
		
		//See ECMA 335-Partition II, 22.14 Exported Type
		//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=242&view=FitH
		
		Arrays.sort(metaModel.exportedType);
		assembly.getModule().setExportedTypes(metaModel.exportedType);
	}
}

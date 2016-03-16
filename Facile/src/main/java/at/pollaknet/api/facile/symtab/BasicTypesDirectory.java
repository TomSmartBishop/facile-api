package at.pollaknet.api.facile.symtab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.entries.AssemblyRefEntry;
import at.pollaknet.api.facile.metamodel.entries.GenericParamEntry;
import at.pollaknet.api.facile.metamodel.entries.StandAloneSigEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeDefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.signature.Signature;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;


/**
 * Registers all defined or referenced basic data types like int, string and
 * object in order to deliver them on a request for a basic data type (e.g. which is
 * embedded in a type specification). If the requested data type was not registered,
 * a type reference to the basic data type will be instantiated (and automatically
 * registered for further use).
 */
public class BasicTypesDirectory {

	//name of the core library
	private static final String MSCORLIB_IDENTIFIER_STRING = "mscorlib";
	
	//reference to the meta data model (to support the requests via meta data tokens)
	private MetadataModel metaModel;
	
	//the system's resolution scope is used for the basic types
	//(they are pointing to the core library)
	private ResolutionScope coreLibScope;
	
	//the directory of the basic types (even known as element types)

	//See ECMA 335 revision 4 - Partition II, 23.1.16 Element types used in signatures
	//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=276&view=FitH
	private HashMap<Integer, TypeRefEntry> typeRefs = new HashMap<>();
	
	private ArrayList<TypeSpecEntry> signatureEmbeddedTypeSpecs = new ArrayList<>();

	private BlobStream blobStream = null;
	
	private List<Assembly> referenceAssemblies = null;
	private Map<String, Byte> referenceEnums = null;

	/**
	 * Create a new basic data type directory. The basic types are
	 * even known as element types.
	 * 
	 * @param metaModel A reference to the {@link at.pollaknet.api.facile.metamodel.MetadataModel}
	 * class to resolve even non basic (element) types, which can occur in type
	 * specifications.
	 */
	public BasicTypesDirectory(
			MetadataModel metaModel, BlobStream blobStream, List<Assembly> referenceAssemblies, Map<String, Byte> referenceEnums) {
		this.metaModel = metaModel;
		this.blobStream  = blobStream;
		this.referenceAssemblies = referenceAssemblies;
		this.referenceEnums = referenceEnums;
		
		//search for a reference to the core library
		for(AssemblyRefEntry entry: metaModel.assemblyRef) {
			if(entry.getName() != null && entry.getName().equals(MSCORLIB_IDENTIFIER_STRING)) {
				coreLibScope = entry;
				break;
			}
		}
		
		assert(metaModel.assembly[0]!=null);
	}

	public List<Assembly> getReferenceAssemblies() {
		return referenceAssemblies;
	}

	public Map<String, Byte> getReferenceEnums() {
		return referenceEnums;
	}
	
	/**
	 * Register a valid defined or referenced basic data type. The type is getting registered
	 * in the basic type directory, if it is a basic type (like int, long, ...).
	 *  
	 * @param typeDefOrRef A type definition or reference to register.
	 * 
	 * @return A {@code boolean}, which is {@code true} if the type has been
	 * registered as basic type.
	 */
	public boolean register(TypeRefEntry typeDefOrRef) {

		if(typeRefs.values().contains(typeDefOrRef) ||
				typeDefOrRef.getFullQualifiedName()==null )
			return false;
		
		//compare the full qualified name in order to get sure,
		//that it is really one of the basic data types
		if(typeDefOrRef.getFullQualifiedName().equals("System.String")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_STRING);
			typeDefOrRef.setShortSystemName("string");
			typeRefs.put(TypeKind.ELEMENT_TYPE_STRING, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Boolean")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_BOOLEAN);
			typeDefOrRef.setShortSystemName("bool");
			typeRefs.put(TypeKind.ELEMENT_TYPE_BOOLEAN, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Char")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_CHAR);
			typeDefOrRef.setShortSystemName("char");
			typeRefs.put(TypeKind.ELEMENT_TYPE_CHAR, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.SByte")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_I1);
			typeDefOrRef.setShortSystemName("int8");
			typeRefs.put(TypeKind.ELEMENT_TYPE_I1, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Byte")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_U1);
			typeDefOrRef.setShortSystemName("unsigned int8");
			typeRefs.put(TypeKind.ELEMENT_TYPE_U1, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Int16")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_I2);
			typeDefOrRef.setShortSystemName("int16");
			typeRefs.put(TypeKind.ELEMENT_TYPE_I2, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.UInt16")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_U2);
			typeDefOrRef.setShortSystemName("unsigned int16");
			typeRefs.put(TypeKind.ELEMENT_TYPE_U2, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Int32")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_I4);
			typeDefOrRef.setShortSystemName("int32");
			typeRefs.put(TypeKind.ELEMENT_TYPE_I4, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.UInt32")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_U4);
			typeDefOrRef.setShortSystemName("unsigned int32");
			typeRefs.put(TypeKind.ELEMENT_TYPE_U4, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Int64")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_I8);
			typeDefOrRef.setShortSystemName("int64");
			typeRefs.put(TypeKind.ELEMENT_TYPE_I8, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.UInt64")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_U8);
			typeDefOrRef.setShortSystemName("unsigned int64");
			typeRefs.put(TypeKind.ELEMENT_TYPE_U8, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.TypedReference")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_TYPEDBYREF);
			typeRefs.put(TypeKind.ELEMENT_TYPE_TYPEDBYREF, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Void")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_VOID);
			typeDefOrRef.setShortSystemName("void");
			typeRefs.put(TypeKind.ELEMENT_TYPE_VOID, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Object")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_OBJECT);
			typeDefOrRef.setShortSystemName("object");
			typeRefs.put(TypeKind.ELEMENT_TYPE_OBJECT, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Single")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_R4);
			typeDefOrRef.setShortSystemName("float32");
			typeRefs.put(TypeKind.ELEMENT_TYPE_R4, typeDefOrRef);
			return true;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Double")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_R8);
			typeDefOrRef.setShortSystemName("float64");
			typeRefs.put(TypeKind.ELEMENT_TYPE_R8, typeDefOrRef);
			return true;
		}
		else if(typeDefOrRef.getFullQualifiedName().equals("System.IntPtr")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_PTR);
			typeDefOrRef.setShortSystemName("native int");
			typeRefs.put(TypeKind.ELEMENT_TYPE_I, typeDefOrRef);
			return true;
		}
		else if(typeDefOrRef.getFullQualifiedName().equals("System.UIntPtr")) {
			typeDefOrRef.setElementKind(TypeKind.ELEMENT_TYPE_PTR);
			typeDefOrRef.setShortSystemName("native unsigned int");
			typeRefs.put(TypeKind.ELEMENT_TYPE_U, typeDefOrRef);
			return true;
		}
		else if(typeDefOrRef.getFullQualifiedName().equals("System.Type")) {
			typeDefOrRef.setElementKind(Signature.UNNAMED_SYSTEM_TYPE);
			typeRefs.put(Signature.UNNAMED_SYSTEM_TYPE, typeDefOrRef);
			return true;
		}

		
		//no basic type
		return false;
	}
	
	/**
	 * Static helper method to get the type kind of a RefType, without using the directory. 
	 * 
	 * @param typeDefOrRef A type definition or reference to look up in the directory.
	 * 
	 * @return A type kind as int, 0 in case of no match.
	 */
	public static int getTypeKindByString(TypeRef typeDefOrRef) {
		
		//compare the full qualified name in order to get sure,
		//that it is really one of the basic data types
		if(typeDefOrRef.getFullQualifiedName().equals("System.String")) {
			return TypeKind.ELEMENT_TYPE_STRING;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Boolean")) {
			return TypeKind.ELEMENT_TYPE_BOOLEAN;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Char")) {
			return TypeKind.ELEMENT_TYPE_CHAR;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.SByte")) {
			return TypeKind.ELEMENT_TYPE_I1;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Byte")) {
			return TypeKind.ELEMENT_TYPE_U1;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Int16")) {
			return TypeKind.ELEMENT_TYPE_I2;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.UInt16")) {
			return TypeKind.ELEMENT_TYPE_U2;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Int32")) {
			return TypeKind.ELEMENT_TYPE_I4;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.UInt32")) {
			return TypeKind.ELEMENT_TYPE_U4;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Int64")) {
			return TypeKind.ELEMENT_TYPE_U8;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.UInt64")) {
			return TypeKind.ELEMENT_TYPE_U8;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.TypedReference")) {
			return TypeKind.ELEMENT_TYPE_TYPEDBYREF;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Void")) {
			return TypeKind.ELEMENT_TYPE_VOID;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Object")) {
			return TypeKind.ELEMENT_TYPE_OBJECT;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Single")) {
			return TypeKind.ELEMENT_TYPE_R4;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Double")) {
			return TypeKind.ELEMENT_TYPE_R8;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.Type")) {
			return Signature.UNNAMED_SYSTEM_TYPE;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.IntPtr")) {
			return TypeKind.ELEMENT_TYPE_I;
		} else if(typeDefOrRef.getFullQualifiedName().equals("System.UIntPtr")) {
			return TypeKind.ELEMENT_TYPE_U;
		}
		
		return 0;
	}
	
	/**
	 * Returns the referenced data type of the basic type directory of the same
	 * type as specified which has been specified. 
	 * 
	 * @param typeDefOrRef A type definition or reference to look up in the directory.
	 * 
	 * @return A reference to the registered version of he specified data type.
	 */
	public TypeRefEntry getRegisteredType(TypeRefEntry typeDefOrRef) {

		int typeIdentifier = getTypeKindByString(typeDefOrRef);		
		TypeRefEntry type = typeRefs.get(new Integer(typeIdentifier));
		
		return type==null?typeDefOrRef:type;
	}

	/**
	 * Get a type reference to a basic type via a type identifier, defined in
	 * {@link at.pollaknet.api.facile.symtab.signature.Signature}.
	 * 
	 * @param typeIdentifier An {@code int} with one of the following values
	 * (Type Identifier):
	 * <table>
	 * <hr><td><b>Type Identifier (value)</b></td><td align="center"><b>Short Name</b></td><td align="center"><b>Type</b></td></hr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_VOID}</td><td align="center">void</td><td align="center">System.void</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_OBJECT}</td><td align="center">object</td><td align="center">System.Object</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_TYPEDBYREF}</td><td></td><td align="center">System.TypedReference</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_BOOLEAN}</td><td align="center">bool</td><td align="center">System.Boolean</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_CHAR}</td><td align="center">char</td><td align="center">System.Char</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_I1}</td><td align="center">sbyte</td><td align="center">System.SByte</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_U1}</td><td align="center">byte</td><td align="center">System.Byte</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_I2}</td><td align="center">short</td><td align="center">System.Int16</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_U2}</td><td align="center">ushort</td><td align="center">System.UInt16</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_I4}</td><td align="center">int</td><td align="center">System.Int32</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_U4}</td><td align="center">uint</td><td align="center">System.UInt32</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_I8}</td><td align="center">long</td><td align="center">System.Int64</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_U8}</td><td align="center">ulong</td><td align="center">System.UInt64</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_R4}</td><td align="center">float</td><td align="center">System.Single</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_R4}</td><td align="center">double</td><td align="center">System.Double</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_I}</td><td></td><td align="center">System.IntPtr</td></tr>
	 * <tr><td> {@link at.pollaknet.api.facile.symtab.signature.Signature#ELEMENT_TYPE_U}</td><td></td><td align="center">System.UIntPtr</td></tr>
	 * </table>
	 * 
	 * @return A {@link at.pollaknet.api.facile.metamodel.entries.TypeRefEntry} object if a valid basic
	 * type identifier was passed, otherwise {@code null}.
	 */
	public TypeRefEntry getType(int typeIdentifier) {
		
		//check if the type is somewhere in the directory
		TypeRefEntry type = typeRefs.get(new Integer(typeIdentifier));
		if(type!=null) return type;
		
		
		//otherwise create such a type (as TypeRef)
		TypeRefEntry typeRef = null;

		switch(typeIdentifier) {
			case TypeKind.ELEMENT_TYPE_BOOLEAN:
				typeRef = new TypeRefEntry(coreLibScope, "Boolean", "bool"); //bool
				break;
			case TypeKind.ELEMENT_TYPE_CHAR:
				typeRef = new TypeRefEntry(coreLibScope, "Char", "char"); //char
				break;
			case TypeKind.ELEMENT_TYPE_I1:
				typeRef = new TypeRefEntry(coreLibScope, "SByte", "int8"); //sbyte
				break;
			case TypeKind.ELEMENT_TYPE_U1:
				typeRef = new TypeRefEntry(coreLibScope, "Byte", "uint8"); //byte
				break;
			case TypeKind.ELEMENT_TYPE_I2:
				typeRef = new TypeRefEntry(coreLibScope, "Int16", "int16"); //short
				break;
			case TypeKind.ELEMENT_TYPE_U2:
				typeRef = new TypeRefEntry(coreLibScope, "UInt16", "uint16"); //ushort
				break;
			case TypeKind.ELEMENT_TYPE_I4:
				typeRef = new TypeRefEntry(coreLibScope, "Int32", "int32"); //int
				break;
			case TypeKind.ELEMENT_TYPE_U4:
				typeRef = new TypeRefEntry(coreLibScope, "UInt32", "uint32"); //uint
				break;
			case TypeKind.ELEMENT_TYPE_I8:
				typeRef = new TypeRefEntry(coreLibScope, "Int64", "int64"); //long
				break;
			case TypeKind.ELEMENT_TYPE_U8:
				typeRef = new TypeRefEntry(coreLibScope, "UInt64", "uint64"); //ulong
				break;
			case TypeKind.ELEMENT_TYPE_R4:
				typeRef = new TypeRefEntry(coreLibScope, "Single", "float32"); //float
				break;
			case TypeKind.ELEMENT_TYPE_R8:
				typeRef = new TypeRefEntry(coreLibScope, "Double", "float64"); //double
				break;
			case TypeKind.ELEMENT_TYPE_I:
				typeRef = new TypeRefEntry(coreLibScope, "IntPtr");
				break;
			case TypeKind.ELEMENT_TYPE_U:
				typeRef = new TypeRefEntry(coreLibScope, "UIntPtr");
				break;
			case TypeKind.ELEMENT_TYPE_OBJECT:
				typeRef = new TypeRefEntry(coreLibScope, "Object", "object"); //object
				break;
			case TypeKind.ELEMENT_TYPE_STRING:
				typeRef = new TypeRefEntry(coreLibScope, "String", "string"); //string
				break;
			case TypeKind.ELEMENT_TYPE_VOID:
				typeRef = new TypeRefEntry(coreLibScope, "Void", "void"); //void
				break;
			case TypeKind.ELEMENT_TYPE_TYPEDBYREF:
				typeRef = new TypeRefEntry(coreLibScope, "TypedReference");
				break;
			case Signature.UNNAMED_SYSTEM_TYPE:
				typeRef = new TypeRefEntry(coreLibScope, "Type");
				break;
				
			default:
				return null;
		}
		
		register(typeRef);
		
		return typeRef;
	}
	
	/**
	 * This method tries to figure out the size of an enum (1 VS 4 byte)
	 * This is performed via (A) an internal dictionary of enums requireing just
	 * 1 byte and (B) by checking if the provided type is derrived from System.Enum
	 * @param typeRef The TypeRef which is possibly an enum.
	 * @return The suggested type kind for the specified type
	 * (in case it is not an enum the type kind of the passed TypeRef instance will be returned).
	 */
	public static int findSuperTypeKind(TypeRef typeRef)	{	
		TypeRef superType = typeRef;
		while(superType!=null) {
	
			if(superType.getFullQualifiedName().equals("System.Enum")) {
				return Signature.UNNAMED_CSTM_ATRB_ENUM;
			}
			
			Type type = superType.getType();
			
			if(type!=null && type.getExtends()!=null)
				superType = type.getExtends();
			else
				superType = null;
		}	
		return typeRef.getElementTypeKind();
	}

	/**
	 * Returns the type reference entries defined in this assembly
	 * (out of the meta model).
	 * 
	 * @return A {@link at.pollaknet.api.facile.metamodel.entries.TypeRefEntry}[] from
	 * the meta model.
	 */
	public TypeRefEntry[] getTypeRefs() {
		return metaModel.typeRef;
	}

	/**
	 * Returns the type specification entries defined in this assembly
	 * (out of the meta model).
	 * 
	 * @return A {@link at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry}[] from
	 * the meta model.
	 */
	public TypeSpecEntry[] getTypeSpecs() {
		return metaModel.typeSpec;
	}
	
	/**
	 * Returns the type definition entries defined in this assembly
	 * (out of the meta model).
	 * 
	 * @return A {@link at.pollaknet.api.facile.metamodel.entries.TypeDefEntry}[] from
	 * the meta model.
	 */
	public TypeDefEntry[] getTypeDefs() {
		return metaModel.typeDef;
	}

	/**
	 * Returns the stand alone signature entries defined in this assembly
	 * (out of the meta model).
	 * 
	 * @return A {@link at.pollaknet.api.facile.metamodel.entries.StandAloneSigEntry}[] from
	 * the meta model.
	 */
	public StandAloneSigEntry[] getStandAloneSignatures() {
		return metaModel.standAloneSig;
	}

	/**
	 * Returns the generic parameter entries defined in this assembly
	 * (out of the meta model).
	 * 
	 * @return A {@link at.pollaknet.api.facile.metamodel.entries.GenericParamEntry}[] from
	 * the meta model.
	 */
	public GenericParamEntry[] getGenericParams() {
		return metaModel.genericParam;
	}

	/**
	 * Returns a blob from the blob stream which is sometimes required to continue signature parsing.
	 * @return The {@code byte []} object of the assembly.
	 */
	public byte [] getBlob(int index) {
		return blobStream.getBlob(index);
	}

	public void registerEmbeddedTypeSpec(TypeSpecEntry embeddedTypeSpec) {
		if(!signatureEmbeddedTypeSpecs.contains(embeddedTypeSpec))
			signatureEmbeddedTypeSpecs.add(embeddedTypeSpec);
	}

	public ArrayList<TypeSpecEntry> getEmbeddedTypeSpecs() {
		return signatureEmbeddedTypeSpecs;
	}

}

package at.pollaknet.api.facile.symtab.signature;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.metamodel.HasBackupBlobIndex;
import at.pollaknet.api.facile.metamodel.entries.AssemblyRefEntry;
import at.pollaknet.api.facile.metamodel.entries.ParamEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.BasicTypesDirectory;
import at.pollaknet.api.facile.symtab.TypeInstance;
import at.pollaknet.api.facile.symtab.TypeKind;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.ByteReader;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Signature extends TypeKind {

	public final static int ELEMENT_TYPE_MODIFIER 		= 0x40; //Or d with following element types
	public final static int ELEMENT_TYPE_SENTINEL 		= 0x41; //Sentinel for vararg method signature
	public final static int ELEMENT_TYPE_PINNED 		= 0x45; //Denotes a local variable that points at a pinned object
	
	public final static int UNNAMED_SYSTEM_TYPE			= 0x50; //Indicates an argument of type System.Type.
	public final static int UNNAMED_BOXED_OBJECT		= 0x51; //Used in custom attributes to specify a boxed object
	public final static int UNNAMED_RESERVED			= 0x52;	//Reserved
	public final static int UNNAMED_CSTM_ATRB_FIELD		= 0x53; //Used in custom attributes to indicate a FIELD
	public final static int UNNAMED_CSTM_ATRB_PROPERTY	= 0x54; //Used in custom attributes to indicate a PROPERTY
	public final static int SERIALIZATION_TYPE_PROPERTY	= 0x54; //Used in declerative security permissions (equal to PROPERTY)
	public final static int UNNAMED_CSTM_ATRB_ENUM		= 0x55; //Used in custom attributes to specify an enum
	
	public final static int PREFIX_CUSTOM_ATTRIBUTE 	= 0x01; //followed by an additional 0x00
	
	public final static int PREFIX_FIELD				= 0x06;
	public final static int PREFIX_LOCAL_VAR 			= 0x07;
	public final static int PREFIX_PROPERTY 			= 0x08;
	
	public final static int PREFIX_GENERIC_INSTANCE 	= 0x0a;
	
	public final static int PREFIX_DECL_SECURITY 		= 0x2e; //is equivalent to the dot '.' character


	//coded type def or ref token id's
	public static final int CODED_TDOR_DEF_TOKEN_ID 	= 0x00;
	public static final int CODED_TDOR_REF_TOKEN_ID 	= 0x01;
	public static final int CODED_TDOR_SPEC_TOKEN_ID 	= 0x02;

	protected byte [] binarySignature;
	protected int currentToken;
	protected int currentIndex = -1;
	protected BasicTypesDirectory directory;

	protected boolean malformedSignature = false;

	/**
	 * Helper method to move the cursor "currentIndex" to the next position and checks
	 * throws an appropriate exception if the cursor moves beyond the end.
	 * @throws InvalidSignatureException if the size of the signature is exceeded.
	 */
	protected void nextToken() throws InvalidSignatureException {
		currentIndex++;
		if(currentIndex>binarySignature.length)
			throw new InvalidSignatureException("Size of signature exceeded!");
		else if(currentIndex<binarySignature.length)
			currentToken = ByteReader.getUInt8(binarySignature, currentIndex);
		else
			currentToken = -1; //end reached
	}

	/**
	 * Helper method to check if there is a further element in the signature available.
	 * @return {@code true} if the cursor "currentIndex" has not reached the end, otherwise {@code false}. 
	 */
	protected boolean hasNext() {
		return (currentIndex+1)<binarySignature.length;
	}
	

	protected void setBinarySignature(byte[] signature) {
		assert(signature!=null);
		assert(signature.length!=0);

		this.binarySignature = signature;
	}
	
	public byte [] getBinarySignature() {
		return binarySignature;
	}

	protected void setDirectory(BasicTypesDirectory directory) {
		this.directory = directory;
	}
	
	protected TypeRefEntry plainType() throws InvalidSignatureException {
		
		switch(currentToken) {
		
			case ELEMENT_TYPE_BOOLEAN:
			case ELEMENT_TYPE_CHAR:
			case ELEMENT_TYPE_I1:
			case ELEMENT_TYPE_U1:
			case ELEMENT_TYPE_I2:
			case ELEMENT_TYPE_U2:
			case ELEMENT_TYPE_I4:
			case ELEMENT_TYPE_U4:
			case ELEMENT_TYPE_I8:
			case ELEMENT_TYPE_U8:
			case ELEMENT_TYPE_R4:
			case ELEMENT_TYPE_R8:
			case ELEMENT_TYPE_I:
			case ELEMENT_TYPE_U:
			case ELEMENT_TYPE_OBJECT:
			case ELEMENT_TYPE_STRING:
			case ELEMENT_TYPE_VOID:
			case ELEMENT_TYPE_TYPEDBYREF:
			case UNNAMED_SYSTEM_TYPE:
				int token = currentToken;
				nextToken();
				return directory.getType(token);

		}

		return null;
	}
				
	protected void type(TypeSpecEntry enclosingType) throws InvalidSignatureException {
		
		//See ECMA 335-Partition II, 23.2.12 Type
		//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=286&view=FitH
		
		//Type ::=
		//x		BOOLEAN | CHAR | I1 | U1 | I2 | U2 | I4 | U4 | I8 | U8 | R4 | R8 | I | U
		//x		| ARRAY Type ArrayShape (general array, see 23.2.13)
		//x		| CLASS TypeDefOrRefEncoded
		//x		| FNPTR MethodDefSig
		//x		| FNPTR MethodRefSig
		//x		| GENERICINST (CLASS | VALUETYPE) TypeDefOrRefEncoded GenArgCount Type *
		//x		| MVAR number
		//x		| OBJECT
		//x		| PTR CustomMod* Type
		//x		| PTR CustomMod* VOID
		//x		| STRING
		//x		| SZARRAY CustomMod* Type (single dimensional, zero-based array i.e., vector)
		//x		| VALUETYPE TypeDefOrRefEncoded
		//x		| VAR number
		boolean belongsToMethod = false;

		switch(currentToken) {
		
			case ELEMENT_TYPE_BOOLEAN:
			case ELEMENT_TYPE_CHAR:
			case ELEMENT_TYPE_I1:
			case ELEMENT_TYPE_U1:
			case ELEMENT_TYPE_I2:
			case ELEMENT_TYPE_U2:
			case ELEMENT_TYPE_I4:
			case ELEMENT_TYPE_U4:
			case ELEMENT_TYPE_I8:
			case ELEMENT_TYPE_U8:
			case ELEMENT_TYPE_R4:
			case ELEMENT_TYPE_R8:
			case ELEMENT_TYPE_I:
			case ELEMENT_TYPE_U:
			case ELEMENT_TYPE_OBJECT:
			case ELEMENT_TYPE_STRING:
			case ELEMENT_TYPE_VOID:
			case ELEMENT_TYPE_TYPEDBYREF:
				enclosingType.setEnclosedTypeRef(directory.getType(currentToken));
				nextToken();
				break;
				
			
			case ELEMENT_TYPE_PTR:
			case ELEMENT_TYPE_ARRAY:
			case ELEMENT_TYPE_SZARRAY:	
			case ELEMENT_TYPE_FNPTR:
			case ELEMENT_TYPE_GENERICINST:
				//all these cases already handled by the typespec blob
				//do not consume this token!
				typeSpecBlob(enclosingType);
				break;

				
			case ELEMENT_TYPE_CLASS:
				nextToken();
				enclosingType.setAsClass(true);
				typeDefOrRefEncoded(enclosingType);
				break;
				
			case ELEMENT_TYPE_VALUETYPE:
				nextToken();
				enclosingType.setAsValueType(true);
				typeDefOrRefEncoded(enclosingType);
				break;
				
			case ELEMENT_TYPE_MVAR:
                belongsToMethod = true;
                // fall through
			case ELEMENT_TYPE_VAR:
				nextToken();
				int genericNumber;
				
				genericNumber = decodeIntegerInSignature();
				assert(genericNumber>=0);

				//IMPROVE: can we assign the name of the generic parameter here...?
				
				enclosingType.setAsGenericParameter(genericNumber, belongsToMethod);
				break;
				
			case Signature.UNNAMED_CSTM_ATRB_ENUM:
				nextToken();
				String fullQualifiedName = readSerString();
				setNameAndSpace(enclosingType, fullQualifiedName);

				//query the type kind (which is tricky for enums)
				int kind = BasicTypesDirectory.getEnumTypeKind(enclosingType);
				if(kind==0)
					kind = Signature.UNNAMED_CSTM_ATRB_ENUM;
				
				enclosingType.setElementKind(kind);
				break;
				
			case Signature.UNNAMED_SYSTEM_TYPE:
				nextToken();
				enclosingType.setElementKind(UNNAMED_SYSTEM_TYPE);//directory.getRegisteredType(enclosingType).getElementTypeKind());
				break;
				
			case Signature.UNNAMED_BOXED_OBJECT: {
				TypeSpecEntry typeSpec = new TypeSpecEntry();
				nextToken();
				enclosingType.setAsBoxed(true);
				enclosingType.setElementKind(TypeKind.ELEMENT_TYPE_OBJECT);
								
				type(typeSpec);
				//TODO: check if this is correct:
				directory.registerEmbeddedTypeSpec(typeSpec);
				enclosingType.setEnclosedTypeRef(typeSpec);
				break;
			}
				
			default:
				throw new InvalidSignatureException(currentToken);
		}
	}

	protected void setNameAndSpace(TypeSpecEntry enclosingType, String fullQualifiedName) {
		StringTokenizer tokenizer = new StringTokenizer(fullQualifiedName, ", ");
		int index=0;
		
		String assembly = null;
		String version = null;
		String culture = null;
		String publicKey = null;
		
		while(tokenizer.hasMoreTokens()) {
			String currentPart = tokenizer.nextToken();
			switch(index) {
				case 0: 
					int dotPosition = currentPart.lastIndexOf('.');
					enclosingType.setName(dotPosition<0?currentPart:currentPart.substring(dotPosition+1));
					enclosingType.setNamespace(dotPosition<0?"":currentPart.substring(0,dotPosition));
					break;
					
				case 1:
					assembly = currentPart;
					break;
					
				case 2:
					if(currentPart!=null) version = currentPart.substring(currentPart.indexOf('=')+1);
					break;
				case 3:
					if(currentPart!=null) culture = currentPart.substring(currentPart.indexOf('=')+1);
					break;
					
				case 4:
					if(currentPart!=null) publicKey = currentPart.substring(currentPart.indexOf('=')+1);
					
					AssemblyRefEntry dummyAssembly = setExtractedAssemblyData(
							fullQualifiedName, assembly, version, culture, publicKey);
					
					//set the dummy as scope for the current type
					enclosingType.setResolutionScope(dummyAssembly);
					break;
					
				default:
					break;
					
			}
			index++;
		}

	}

	private static AssemblyRefEntry setExtractedAssemblyData(String fullQualifiedName,
			String assembly, String version, String culture, String publicKey) {
		//create a dummy assembly with the collected data
		AssemblyRefEntry dummyAssembly = new AssemblyRefEntry();
		
		//set the name of the assembly
		dummyAssembly.setName(assembly);
		
		//set the version numbers (format: major.minor.build.revision)
		if (version != null) {
			int indexInVersionString = 0;
			StringTokenizer numberTokenizer = new StringTokenizer(version, ".");
			while (numberTokenizer.hasMoreTokens()) {
				String number = numberTokenizer.nextToken();
				
				try {
					switch (indexInVersionString) {
						case 0:dummyAssembly.setMajorVersion(Integer.parseInt(number));
							break;
		
						case 1:dummyAssembly.setMinorVersion(Integer.parseInt(number));
							break;
						
						case 2:dummyAssembly.setBuildNumber(Integer.parseInt(number));
							break;
						
						default:dummyAssembly.setRevisionNumber(Integer.parseInt(number));
							break;
					}
				} catch (NumberFormatException e) {
					Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
					StringBuffer buffer = new StringBuffer(128);
					
					buffer.append("Unable to convert element ");
					buffer.append(indexInVersionString);
					buffer.append(" of version string [");
					buffer.append(version);
					buffer.append("] of ");
					buffer.append(fullQualifiedName);
					buffer.append(".");
					
					logger.log(Level.WARNING, buffer.toString());
				}
				indexInVersionString++;
			}
		}

		//set culture string
		dummyAssembly.setCulture(culture);
		
		//set public key as byte buffer
		if(publicKey!=null) dummyAssembly.setPublicKey(publicKey.getBytes());
		
		return dummyAssembly;
	}

	protected int decode4ByteNullableIntegerInSignature() throws InvalidSignatureException {
		int value = ByteReader.decodeSignatureElement(binarySignature, currentIndex);
		int length = value<0?4:ByteReader.getSizeOfSignatureElement(value);

		skipTokens(length);
		return value;
	}
	
	protected int decodeNullableIntegerInSignature() throws InvalidSignatureException {
		int value = ByteReader.decodeSignatureElement(binarySignature, currentIndex);
		int length = ByteReader.getSizeOfSignatureElement(value);

		skipTokens(length);
		return value;
	}
	
	protected int decodeIntegerInSignature() throws InvalidSignatureException {
		int value = ByteReader.decodeSignatureElement(binarySignature, currentIndex);
		int length = ByteReader.getSizeOfSignatureElement(value);
		//null entry is invalid in this case
		if(length<0) throw new InvalidSignatureException(currentToken);
		//skip already processed number  
		skipTokens(length);
		return value;
	}

	protected void skipTokens(int numberOfTokens)
			throws InvalidSignatureException {
		for(int count=0;count<numberOfTokens;count++) nextToken();
	}

	private void arrayShape(TypeSpecEntry enclosingType)
			throws InvalidSignatureException {
		// See ECMA 335 revision 4 - Partition II, 23.2.13 ArrayShape
		// http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=287&view=FitH
		int rank = decodeIntegerInSignature();
		int sizes [] = new int[decodeIntegerInSignature()];
		
		for(int i=0;i<sizes.length;i++) sizes[i] = decodeIntegerInSignature();
		
		int lowerBounds [] = new int[decodeIntegerInSignature()];
		
		for(int i=0;i<lowerBounds.length;i++) lowerBounds[i] = decodeIntegerInSignature();
		
		enclosingType.setArrayShape(new ArrayShape(rank, sizes, lowerBounds));		
	}

	protected void typeDefOrRefEncoded(TypeSpecEntry enclosedType)
			throws InvalidSignatureException {
		//See ECMA 335 revision 4 - Partition II, 23.2.8 TypeDefOrRefEncoded
		//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=284&view=FitH
		
		int token = decodeIntegerInSignature();
		int index = (token>>2)-1;
		
		switch(token&0x03) {
			case CODED_TDOR_DEF_TOKEN_ID:
				enclosedType.setEnclosedTypeRef(directory.getTypeDefs()[index]);
				break;
				
			case CODED_TDOR_REF_TOKEN_ID:
				enclosedType.setEnclosedTypeRef(directory.getTypeRefs()[index]);
				break;
				
			case CODED_TDOR_SPEC_TOKEN_ID:
				//TypeSpecEntry spec = directory.getTypeSpecs()[index];
				enclosedType.setEnclosedTypeRef(directory.getTypeSpecs()[index]);
				break;
			
			default:
				throw new InvalidSignatureException(currentToken);
		}
	}

	protected TypeRefEntry returnType() throws InvalidSignatureException {
		TypeSpecEntry returnType = new TypeSpecEntry();
		TypeRefEntry plainType = null;
		boolean isPlainType = !customModifiers(returnType);
		
		if(isPlainType) plainType = plainType();
		
		if(plainType!=null) return plainType;

		type(returnType);
		directory.registerEmbeddedTypeSpec(returnType);
		return returnType;
	}

	protected void returnType(ParamEntry paramEntry) {
		TypeSpecEntry returnType = new TypeSpecEntry();
		TypeRefEntry plainType = null;
		boolean isPlainType = !customModifiers(returnType);

		if (isPlainType)
			plainType = plainType();

		if (plainType != null) {
			paramEntry.setTypeRef(plainType);
		} else {
			type(returnType);
			directory.registerEmbeddedTypeSpec(returnType);
			paramEntry.setTypeRef(returnType);
		}
	}

	protected boolean customModifiers(TypeSpecEntry typeSpec)
			throws InvalidSignatureException {
		boolean hasModifiers = false;
		while(	currentToken==ELEMENT_TYPE_CMOD_OPT ||
				currentToken==ELEMENT_TYPE_CMOD_REQD ||
				currentToken==ELEMENT_TYPE_BYREF ||
				currentToken==ELEMENT_TYPE_PINNED		) {
			hasModifiers = true;
			TypeSpecEntry optionalOrReqType = new TypeSpecEntry();
			if (currentToken == ELEMENT_TYPE_BYREF) {
				nextToken();
				typeSpec.setTypeByRef(true);
			} else if (currentToken == ELEMENT_TYPE_PINNED) {
				nextToken();
				typeSpec.setAsPinned(true);
			} else if (currentToken == ELEMENT_TYPE_CMOD_OPT) {
				nextToken();
				typeDefOrRefEncoded(optionalOrReqType);
				typeSpec.addOptionalModifier(optionalOrReqType);
				//TODO:check if this is correct:
				directory.registerEmbeddedTypeSpec(optionalOrReqType);
			} else {
				nextToken();
				typeDefOrRefEncoded(optionalOrReqType);
				typeSpec.addRequiredModifier(optionalOrReqType);
				//TODO:check if this is correct:
				directory.registerEmbeddedTypeSpec(optionalOrReqType);
			}
		}

		return hasModifiers;
	}
	
protected void typeSpecBlob(TypeSpecEntry enclosingType) throws InvalidSignatureException {
		
		//See ECMA 335-Partition II, 23.2.14
		//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=287&view=FitH
	
		//TypeSpecBlob ::=
		//		PTR CustomMod* VOID
		//		| PTR CustomMod* Type
		//		| FNPTR MethodDefSig
		//		| FNPTR MethodRefSig
		//		| ARRAY Type ArrayShape
		//		| SZARRAY CustomMod* Type
		//		| GENERICINST (CLASS | VALUETYPE) TypeDefOrRefEncoded GenArgCount Type Type*
		
		switch(currentToken) {
				
			case ELEMENT_TYPE_ARRAY: {
				nextToken();
				enclosingType.setGeneralArray(true);
				TypeSpecEntry typeSpec = new TypeSpecEntry();
				type(typeSpec);
				
				//TODO:check if this is correct:
				directory.registerEmbeddedTypeSpec(typeSpec);
				
				enclosingType.setEnclosedTypeRef(typeSpec);
				arrayShape(enclosingType);
				break;
			}
				
			case ELEMENT_TYPE_SZARRAY: {
				nextToken();
				enclosingType.setSingleDimensionalZeroBasedArray(true);				
				customModifiers(enclosingType);
				TypeSpecEntry typeSpec = new TypeSpecEntry();
				type(typeSpec);
				
				//TODO:check if this is correct:
				directory.registerEmbeddedTypeSpec(typeSpec);
				
				enclosingType.setEnclosedTypeRef(typeSpec);
				break;
			}
			
			case ELEMENT_TYPE_PTR: {
				nextToken();
				enclosingType.incPointer();
				customModifiers(enclosingType);
				type(enclosingType);
				break;
			}
			
			case ELEMENT_TYPE_FNPTR: {
				nextToken();
				enclosingType.setAsFunctionPointer(true);
				MethodDefOrRefSignature function;
				function = MethodDefOrRefSignature.decode(directory, binarySignature, currentIndex);
				enclosingType.setFunctionPointer(function);
				skipTokens(function.getBinarySignature().length);
				break;
			}
			
			// GENERICINST (CLASS | VALUETYPE) TypeDefOrRefEncoded GenArgCount Type *
			case ELEMENT_TYPE_GENERICINST: {
				nextToken();
				enclosingType.setAsGenericInstance(true);
				if(currentToken== ELEMENT_TYPE_CLASS)
					enclosingType.setAsClass(true);
				else if(currentToken==ELEMENT_TYPE_VALUETYPE)
					enclosingType.setAsValueType(true);
				else
					throw new InvalidSignatureException(currentToken);
				
				nextToken();
				typeDefOrRefEncoded(enclosingType);
				
				enclosingType.setGenericArguments(typeArray());
				break;
			}
				
			default:
				throw new InvalidSignatureException(currentToken);
		}
	}

	/**
	 * Parse an array of type specifications with trailing decoded
	 * integer specifying number of array elements.
	 * @return An array of type specifications as
	 * {@link at.pollaknet.api.facile.symtab.symbols.TypeRef}.
	 */
	protected TypeRefEntry[] typeArray() {
		int count = decodeIntegerInSignature();

		TypeRefEntry genericTypes[] = new TypeRefEntry[count];
		for (int i = 0; i < count; i++) {
			genericTypes[i] = plainType();
			if (genericTypes[i] == null) {
				TypeSpecEntry typeSpec = new TypeSpecEntry();

				// This is not part of the specification
				customModifiers(typeSpec);

				type(typeSpec);
				genericTypes[i] = typeSpec;
				
				//TODO:check if this is correct:
				directory.registerEmbeddedTypeSpec(typeSpec);
			}
		}
		return genericTypes;
	}

	/**
	 * Parse method parameters inside a signature.
	 * @param parameter The array of parameters as empty container
	 * for the types of the parameters.
	 * @param skipFirst Skips the first element if set to {@code true}
	 * (required if the first parameter is the return type).
	 * @return An {@code int} specifying the sentinel position. The
	 * default value is {@code -1}, which tells you that no sentinel
	 * position was found.
	 * @throws InvalidSignatureException if an unexpected token occurred.
	 */
	protected int params(ParamEntry[] parameter, boolean skipFirst)
			throws InvalidSignatureException {
		int sentinelPosition = -1;

		if (parameter == null)
			return sentinelPosition;

		for (int index = (skipFirst ? 1 : 0); index < parameter.length; index++) {
			if (parameter[index] == null) {
				parameter[index] = new ParamEntry();
			} else {
				ParamOrFieldMarshalSignature.decodeAndAttach(directory, parameter[index]);
			}
			
			if(currentToken==ELEMENT_TYPE_SENTINEL) {	
				nextToken();
				sentinelPosition = index;
			}
			
			param(parameter[index]);
		}
		
		return sentinelPosition;
	}
	
	/**
	 * Parse a single parameter of a method signature.
	 * @param paramEntry The container for the parameter type.
	 * @throws InvalidSignatureException if an unexpected token occurred.
	 */
	protected void param(ParamEntry paramEntry) throws InvalidSignatureException {
		assert(paramEntry!=null);
		
		TypeSpecEntry enclosingType = new TypeSpecEntry();
		TypeRefEntry plainType = null;
		boolean isPlainType = !customModifiers(enclosingType);

		if (currentToken == ELEMENT_TYPE_BYREF) {
			nextToken();
			enclosingType.setTypeByRef(true);
			isPlainType = false;
		}

		if (isPlainType)
			plainType = plainType();

		if (plainType != null) {
			paramEntry.setTypeRef(plainType);
		} else {
			type(enclosingType);
			assert (enclosingType != null);
			paramEntry.setTypeRef(enclosingType);
			
			//TODO:check if this is correct:
			directory.registerEmbeddedTypeSpec(enclosingType);
		}
	}

	/**
	 * <p/>Parse a fixed argument of a custom attribute and return the resulting 
	 * type instance (Because custom attributes are instances!).
	 * <p/>Note that this method is a recursion!
	 * @param customAttribute The custom attribute to process abstracted as
	 * {@link at.pollaknet.api.facile.metamodel.HasBackupBlobIndex}.
	 * @param typeRef The type of the fixed argument.
	 * @return An instance of the specified type as specified in the custom attribute.
	 * @throws InvalidSignatureException if a wrong token was detected in the
	 * signature of the custom attribute. 
	 */
	protected TypeInstance fixedArgument(HasBackupBlobIndex customAttribute, TypeRef typeRef) throws InvalidSignatureException {
		
		TypeSpec spec = typeRef.getTypeSpec();
		int backupIndex = customAttribute.getBinaryBlobIndex();

		long arrayLength = 0;
		TypeInstance arrayInstance = null;

		// extract the enclosed type
		if (spec != null) {

			if (spec.isSingleDimensionalZeroBasedArray()) {

				return typeInstnaceArray(customAttribute, spec, backupIndex);
			} else if (spec.getEnclosedTypeRef() != null) {

				if (spec.isBoxed()) {
					return new TypeInstance(spec, fixedArgument(customAttribute, spec.getEnclosedTypeRef()));
				}
				return fixedArgument(customAttribute, spec.getEnclosedTypeRef());
			}
		}

		//perform a behavior correction in case of enums (which have a type kind of 0)
		int kind = typeRef.getElementTypeKind();
		if(kind==0) {
			kind = BasicTypesDirectory.getEnumTypeKind(typeRef);
		}
		
		// handle values according to the defined kinds
		switch (kind) {

		    //unknown
			case 0: {				
	    		Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
	    		logger.log(Level.WARNING, "Found a fixed Argument without a type kind specification, typeRef: " + typeRef);
	    		
	    		return new TypeInstance(typeRef, readNumericValue(backupIndex, Signature.UNNAMED_CSTM_ATRB_ENUM));
			}
			
			case TypeKind.ELEMENT_TYPE_VOID:
				return new TypeInstance(typeRef, (TypeInstance)null);
	
			case Signature.UNNAMED_SYSTEM_TYPE:
			case TypeKind.ELEMENT_TYPE_STRING: {
				return new TypeInstance(typeRef, readSerString(backupIndex));
			}
	
			// this is the second specified option where : FixedArg = StrLenInt32 String
			case TypeKind.ELEMENT_TYPE_SZARRAY: {
				int length = ArrayUtils.findInByteArray(binarySignature, currentIndex, (byte) 0);
				String value = null;
				try {
					value = new String(binarySignature, currentIndex, length, "UTF8");
				} catch (UnsupportedEncodingException e) {
					value = "Undecoadeable UTF8 identifier in signature";
				}
				return new TypeInstance(typeRef, value);
			}
	
			case TypeKind.ELEMENT_TYPE_OBJECT:
			case Signature.UNNAMED_BOXED_OBJECT: {
				// boxed
				if (currentToken == TypeKind.ELEMENT_TYPE_SZARRAY) {
					nextToken();
	
					TypeRefEntry type = null;
					if (currentToken == UNNAMED_BOXED_OBJECT) {
						type = directory.getType(ELEMENT_TYPE_OBJECT);
						nextToken();
					} else {
						type = filedOrPropertyType();
					}
					assert (type != null);
	
					return typeInstnaceArray(customAttribute, type, backupIndex);
				}
	
				TypeRef boxedTypeRef = filedOrPropertyType();
				return new TypeInstance(typeRef, fixedArgument(customAttribute, boxedTypeRef));
				// return readBoxedValue(typeRef, backupIndex, boxedTypeRef);
	
			}
	
			case TypeKind.ELEMENT_TYPE_R4: {
				ensureSignatureLength(backupIndex, 4);
				Float value = Float.intBitsToFloat(ByteReader.getInt32(binarySignature, currentIndex));
				skipTokens(4);
				return new TypeInstance(typeRef, value.toString());
			}
	
			case TypeKind.ELEMENT_TYPE_U8: {
				ensureSignatureLength(backupIndex, 8);
				long value = ByteReader.getUInt64(binarySignature, currentIndex);
				skipTokens(8);
				return new TypeInstance(typeRef, String.format("0x%x", value));
			}
	
			case TypeKind.ELEMENT_TYPE_R8: {
				ensureSignatureLength(backupIndex, 8);
				Double value = Double.longBitsToDouble(ByteReader.getInt64(binarySignature, currentIndex));
				skipTokens(8);
				return new TypeInstance(typeRef, value.toString());
			}
	
			default: {
				return new TypeInstance(typeRef, readNumericValue(backupIndex, kind));
			}
		}
	}

	/**
	 * <p/>Processes arrays of type instances within a custom attribute argument.
	 * <p/>Called by fixedArgument method. 
	 * @param customAttribute The custom attribute to process abstracted as
	 * {@link at.pollaknet.api.facile.metamodel.HasBackupBlobIndex}.
	 * @param typeRef The type of the fixed argument.
	 * @param backupIndex The already extracted backup index for the signature.
	 * @return A {at.pollaknet.api.facile.symtab.TypeInstance} object containing
	 * an array of instances.
	 */
	private TypeInstance typeInstnaceArray(HasBackupBlobIndex customAttribute,
			TypeRef typeRef, int backupIndex) {
		long arrayLength;
		TypeInstance arrayInstance;

		TypeSpec typeSpec = typeRef.getTypeSpec();

		ensureSignatureLength(backupIndex, 4);
		arrayLength = ByteReader.getInt32(binarySignature, currentIndex);

		skipTokens(4);
		//null array
		if(arrayLength==-1)
			return TypeInstance.CreateArrayInstnace(typeRef, 0);
					
		assert (arrayLength <= ByteReader.INT32_MAX_VAL);
		arrayInstance = TypeInstance.CreateArrayInstnace(typeRef,(int) arrayLength);

		if (typeSpec == null) {
			for (int i = 0; i < arrayLength; i++) {
				arrayInstance.getArrayInstance()[i] = fixedArgument(customAttribute, typeRef);
			}
		} else {
			for (int i = 0; i < arrayLength; i++) {
				arrayInstance.getArrayInstance()[i] = fixedArgument(customAttribute, typeSpec.getEnclosedTypeRef());
			}
		}
		return arrayInstance;
	}

	/**
	 * Read a serialized string inside a signature with a backup index.
	 * @param backupBlobIndex An index of the string (or super container - like
	 * a custom attribute) to the blob heap where the serialized data is located.
	 * This is used in order to repair (continue) broken signatures.
	 * @return The restored string.
	 * @throws InvalidSignatureException if the signature length has been exceeded.
	 */
	protected String readSerString(int backupBlobIndex)
			throws InvalidSignatureException {
		int length = decodeNullableIntegerInSignature();

		if (length < 0) {
			return null;
		} else if (length == 0) {
			return "";
		} else {
			String value;
			if (length >= 0) ensureSignatureLength(backupBlobIndex, length);
			try {
				value = new String(binarySignature, currentIndex, length, "UTF8");
			} catch (UnsupportedEncodingException e) {
				value = "Undecoadeable UTF8 identifier in signature";
			}
			skipTokens(length);
			return value;
		}
	}

	/**
	 * Read a serialized string inside a signature.
	 * 
	 * @return The restored string.
	 * @throws InvalidSignatureException if the signature length has been exceeded.
	 */
	protected String readSerString() throws InvalidSignatureException {
		return readSerString(-1);
	}

	/**
	 * Ensure that the signature has the required length (e.g. to read a string
	 * with 2765 char elements). The binary signature gets extended if the
	 * current length does not satisfy the needs.
	 * 
	 * @param backupBlobIndex An index to the resource's blob heap location.
	 * @param requiredAdditionalLength The length which is required to process the demanded binary object.
	 */
	protected void ensureSignatureLength(int backupBlobIndex,
			int requiredAdditionalLength) {
		int iteration = 1;

		
		while(currentIndex+requiredAdditionalLength>binarySignature.length) {
			//expand
			byte [] extensionblob = directory.getBlobStream().getBlob(backupBlobIndex+binarySignature.length+iteration);
			assert(extensionblob!=null);
			byte [] newSignature = new byte[binarySignature.length+extensionblob.length];
			System.arraycopy(binarySignature, 0, newSignature, 0, binarySignature.length);
			System.arraycopy(extensionblob, 0, newSignature, binarySignature.length, extensionblob.length);
			
			binarySignature = newSignature;
			iteration++;
			malformedSignature = true;
		}
		
		//log this event
		if(malformedSignature) {
			Logger logger = Logger.getLogger(FacileReflector.LOGGER_NAME);
			logger.log(Level.WARNING, "Detected a malformed custom attribute signature. Correction has been applied.");
		}

	}

	/**
	 * Read a numeric value from the current location inside the signature.
	 * 
	 * @param backupBlobIndex An index to the resource's blob heap location.
	 * @param typeRefKind The type which specifies the kind of numeric value.
	 * @return The numeric value which has been decoded from the signature.
	 * @throws InvalidSignatureException if the signature length has been exceeded.
	 */
	protected long readNumericValue(int backupBlobIndex, int typeRefKind)
			throws InvalidSignatureException {
		int index = currentIndex;

		switch (typeRefKind) {
		case TypeKind.ELEMENT_TYPE_I1:
			ensureSignatureLength(backupBlobIndex, 1);
			skipTokens(1);
			return ByteReader.getInt8(binarySignature, index);
		case TypeKind.ELEMENT_TYPE_U1:
		case TypeKind.ELEMENT_TYPE_BOOLEAN:
			ensureSignatureLength(backupBlobIndex, 1);
			skipTokens(1);
			return ByteReader.getUInt8(binarySignature, index);

		case TypeKind.ELEMENT_TYPE_I2:
			ensureSignatureLength(backupBlobIndex, 2);
			skipTokens(2);
			return ByteReader.getInt16(binarySignature, index);
		case TypeKind.ELEMENT_TYPE_U2:
		case TypeKind.ELEMENT_TYPE_CHAR:
			ensureSignatureLength(backupBlobIndex, 2);
			skipTokens(2);
			return ByteReader.getUInt16(binarySignature, index);

		case TypeKind.ELEMENT_TYPE_I4:
			ensureSignatureLength(backupBlobIndex, 4);
			skipTokens(4);
			return ByteReader.getInt32(binarySignature, index);
		case TypeKind.ELEMENT_TYPE_U4:
			ensureSignatureLength(backupBlobIndex, 4);
			skipTokens(4);
			return ByteReader.getUInt32(binarySignature, index);

		case TypeKind.ELEMENT_TYPE_I8:
			ensureSignatureLength(backupBlobIndex, 8);
			skipTokens(8);
			return ByteReader.getInt64(binarySignature, index);

		case Signature.UNNAMED_CSTM_ATRB_ENUM:
			ensureSignatureLength(backupBlobIndex, 4);
			skipTokens(4);
			return ByteReader.getUInt32(binarySignature, index);
			
		default:
			assert(false) : "found unknown type while reading a numerical value";
			return 0;

		}
	}

	protected TypeRefEntry filedOrPropertyType()
			throws InvalidSignatureException {
		TypeRefEntry plainType = plainType();

		if (plainType != null) {
			return plainType;
		}

		TypeSpecEntry typeSpec = new TypeSpecEntry();

		type(typeSpec);
		
		//TODO:check if this is correct:
		directory.registerEmbeddedTypeSpec(typeSpec);
		return typeSpec;
	}

	@Override
	public int hashCode() {
		return 31 + Arrays.hashCode(binarySignature);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Signature other = (Signature) obj;
		if (!Arrays.equals(binarySignature, other.binarySignature))
			return false;

		return true;
	}
	
//	protected String FieldOrPropertyName() throws InvalidSignatureException, UnsupportedEncodingException {
//		int length = decodeNullableIntegerInSignature();
//		
//		if(length<0) return null;
//		if(length==0) return "";
//		
//		int offset = currentIndex;
//		skipTokens(length);
//		
//		return new String(binarySignature, offset, length, "UTF8");
//	}

}

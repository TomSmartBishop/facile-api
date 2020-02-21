package at.pollaknet.api.facile.symtab.signature;

import at.pollaknet.api.facile.code.MethodBody;
import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.BasicTypesDirectory;
import at.pollaknet.api.facile.symtab.TypeKind;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.util.ByteReader;

public class LocalVarSignature extends Signature {
	
	//See ECMA 335 revision 4 - Partition II, 23.2.6 LocalVarSig
	//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=284&view=FitH


	public static LocalVarSignature decodeAndAttach(BasicTypesDirectory directory, MethodBody methodBody) throws InvalidSignatureException {
		return new LocalVarSignature(directory, methodBody);
	}

	private LocalVarSignature(BasicTypesDirectory directory, MethodBody methodBody) throws InvalidSignatureException {
		long token = methodBody.getLocalVarSignatureToken();
		
		if(token==0) {
			methodBody.setLocalVars(null);
			return;
		}
		
		assert((token>>24)==MetadataModel.RECORD_ID_STAND_ALONE_SIGNATURE);
		token &= MetadataModel.TOKEN_VALUE_MASK;
		
		assert(token<=ByteReader.INT32_MAX_VAL);
		
		setBinarySignature(directory.getStandAloneSignatures()[(int)token-1].getBinarySignature());
		setDirectory(directory);
		nextToken();
		
		if(currentToken!=PREFIX_LOCAL_VAR) {
			throw new InvalidSignatureException(binarySignature, currentIndex, currentToken, malformedSignature, "PREFIX_LOCAL_VAR 0x07");
		}
		nextToken();
		
		//The description in ECMA 335 revision 4 - Partition II, 23.2.6 LocalVarSig (Count)
		//about the number of locals does not mention, that the value is stored as compressed
		//integer.
		int numberOfLocals = decodeIntegerInSignature(); 
		
		if(numberOfLocals<1) return;

		assert(numberOfLocals<0xffff);
		
		TypeRef [] localVars = new TypeRef[numberOfLocals];
		
		for(int i=0;i<numberOfLocals;i++) {
			localVars[i] = localVar();
		}
		methodBody.setLocalVars(localVars);
	}
	
	private TypeRef localVar() throws InvalidSignatureException {
		if(currentToken==TypeKind.ELEMENT_TYPE_TYPEDBYREF) {
			return plainType();
		}
		TypeSpecEntry typeSpec = new TypeSpecEntry();
		TypeRefEntry plainType = new TypeRefEntry();
		boolean isPlainType = !customModifierAndConstraint(typeSpec);
		
		//Diagram in ECMA 335 revision 4 - Partition II, 23.2.6 LocalVarSig
		//is wrong. BYREF is not a requirement for a local variable (even
		//if it is possible)!
		if(currentToken==TypeKind.ELEMENT_TYPE_BYREF) {
			nextToken();
			typeSpec.setTypeByRef(true);
			isPlainType = false;
		}
		
		
		if(isPlainType) plainType = plainType();
		
		if(plainType!=null) return plainType;
		
		type(typeSpec);
		directory.registerEmbeddedTypeSpec(typeSpec);
		return typeSpec;
	}

	private boolean customModifierAndConstraint(TypeSpecEntry typeSpec) throws InvalidSignatureException {
		boolean hasModifierOrConstraint = false;
		while(currentToken==ELEMENT_TYPE_CMOD_OPT||currentToken==ELEMENT_TYPE_CMOD_REQD||currentToken==ELEMENT_TYPE_PINNED) {
			hasModifierOrConstraint = true;
			if(currentToken==ELEMENT_TYPE_PINNED){
				nextToken();
				typeSpec.setAsPinned(true);
			} else if(currentToken==ELEMENT_TYPE_CMOD_OPT){
				TypeSpecEntry optionalType = new TypeSpecEntry();
				nextToken();
				typeDefOrRefEncoded(optionalType);
				typeSpec.addOptionalModifier(optionalType);
			} else {
				TypeSpecEntry requiredType = new TypeSpecEntry();
				nextToken();
				typeDefOrRefEncoded(requiredType);
				typeSpec.addRequiredModifier(requiredType);
			}
		}	
		return hasModifierOrConstraint;
	}

}

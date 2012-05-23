package at.pollaknet.api.facile.symtab.signature;

import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.metamodel.ITypeSpecInstances;
import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.BasicTypesDirectory;

public class FieldSignature extends Signature {

	public static FieldSignature decodeAndAttach(BasicTypesDirectory directory, ITypeSpecInstances owner)
			throws InvalidSignatureException {
		return new FieldSignature(directory, owner);
	}
	
	private FieldSignature(BasicTypesDirectory directory, ITypeSpecInstances owner) 
			throws InvalidSignatureException {

		setBinarySignature(owner.getBinarySignature());
		setDirectory(directory);
		nextToken();
		
		owner.setEnclosedTypeRef(field());
	}
	
	private TypeRefEntry field() throws InvalidSignatureException {
		if(currentToken!=PREFIX_FIELD) {
			throw new InvalidSignatureException(currentToken, PREFIX_FIELD);
		}
		nextToken();
		
		TypeSpecEntry typeSpec = new TypeSpecEntry();
		TypeRefEntry plainType = null;
		boolean isPlainType = !customModifiers(typeSpec);
		
		if(isPlainType) plainType = plainType();
		
		if(plainType!=null) {
			return plainType;
		}
		
		type(typeSpec);
		directory.registerEmbeddedTypeSpec(typeSpec);
		return typeSpec;
	}
}

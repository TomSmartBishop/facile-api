package at.pollaknet.api.facile.symtab.signature;

import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.metamodel.entries.MethodSpecEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.BasicTypesDirectory;


public class MethodSpecSignature extends Signature {

	public static MethodSpecSignature decodeAndAttach(BasicTypesDirectory directory, MethodSpecEntry methodSpec)
			throws InvalidSignatureException {
		return new MethodSpecSignature(directory, methodSpec);
	}

	public MethodSpecSignature(BasicTypesDirectory directory, MethodSpecEntry methodSpec)
			throws InvalidSignatureException {
		
		setBinarySignature(methodSpec.getInstantiation());
		setDirectory(directory);
		nextToken();
		
		if(currentToken!=PREFIX_GENERIC_INSTANCE)
			throw new InvalidSignatureException(currentToken, PREFIX_GENERIC_INSTANCE);
		
		nextToken();
		
		TypeSpecEntry typeSpec = new TypeSpecEntry();
		typeSpec.setAsGenericInstance(true);
		typeSpec.setGenericArguments(typeArray());
		
		directory.registerEmbeddedTypeSpec(typeSpec);
		methodSpec.getMethod().addGenericInstance(typeSpec);
	}
}

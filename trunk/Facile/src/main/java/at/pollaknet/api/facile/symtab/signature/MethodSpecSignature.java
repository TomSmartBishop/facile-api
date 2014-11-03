package at.pollaknet.api.facile.symtab.signature;

import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.metamodel.entries.MethodSpecEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.BasicTypesDirectory;
import at.pollaknet.api.facile.symtab.symbols.Parameter;


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
		Parameter[] genericParameters = null;
		if(methodSpec.getMethod()!=null && methodSpec.getMethod().getMethod()!=null)
			genericParameters = methodSpec.getMethod().getMethod().getGenericParameters();

		typeSpec.setGenericArguments(typeArray(genericParameters));
		
//		if(typeSpec.getName()==null) {
//			typeSpec.setName(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME);
//		}
		
		directory.registerEmbeddedTypeSpec(typeSpec);
		methodSpec.getMethod().addGenericInstance(typeSpec);
	}
}

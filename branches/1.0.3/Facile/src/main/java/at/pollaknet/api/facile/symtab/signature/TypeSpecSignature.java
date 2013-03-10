package at.pollaknet.api.facile.symtab.signature;

import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.BasicTypesDirectory;

public class TypeSpecSignature extends Signature {

	public static void decodeAndAttach(BasicTypesDirectory directory, TypeSpecEntry typeSpecEntry)
			throws InvalidSignatureException {

		TypeSpecSignature signature = new TypeSpecSignature();
		
		signature.setBinarySignature(typeSpecEntry.getBinarySignature());
		signature.setDirectory(directory);
		signature.nextToken();

		signature.customModifiers(typeSpecEntry);
		signature.type(typeSpecEntry);
	
		typeSpecEntry.evaluateBasicType();
		
		//return specType;
	}
}

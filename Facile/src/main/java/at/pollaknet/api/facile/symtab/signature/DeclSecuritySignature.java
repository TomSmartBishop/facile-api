package at.pollaknet.api.facile.symtab.signature;

import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.metamodel.entries.DeclSecurityEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;
import at.pollaknet.api.facile.symtab.BasicTypesDirectory;
import at.pollaknet.api.facile.symtab.TypeInstance;
import at.pollaknet.api.facile.symtab.symbols.Instance;
import at.pollaknet.api.facile.util.Pair;

public class DeclSecuritySignature extends Signature {

	public static DeclSecuritySignature decodeAndAttach(BasicTypesDirectory directory, DeclSecurityEntry security)
			throws InvalidSignatureException {
		return new DeclSecuritySignature(directory, security);
	}
	
	private DeclSecuritySignature(BasicTypesDirectory directory, DeclSecurityEntry security) 
			throws InvalidSignatureException {

		setBinarySignature(security.getPermissionSet());
		setDirectory(directory);
		nextToken();
		
		declSecurity(security);
		security.getParent().setDeclarativeSecurity(security);
	}
	
	@SuppressWarnings("unchecked")
	private void declSecurity(DeclSecurityEntry security) throws InvalidSignatureException {
		
		//See ECMA 335 revision 4 - Partition II, 22.11 DeclSecurity
		//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=238&view=FitH
		
		if(currentToken!=PREFIX_DECL_SECURITY) {
			return; //if the first char is not a dot, the securty settings are stored as xml string
		}
		nextToken();
		
		int numPermisssions = decodeIntegerInSignature();
		Permission [] permissions = new Permission [numPermisssions]; 
		
		//extract the stored permission settings
		for(int i=0;i<numPermisssions;i++) {
			//read the name of the permission
			String fullyQuallifiedName = readSerString();
			//read the size of the binary data stream
			int permissionBlobSize = decodeIntegerInSignature();
			//read the number of properties for this permission
			int numberOfProperties = decodeIntegerInSignature();
			
			Pair<String, Instance> [] properties = new Pair[numberOfProperties];
			
			//read each property
			for(int property=0;property<numberOfProperties;property++) {
				if(currentToken!=SERIALIZATION_TYPE_PROPERTY) {
					throw new InvalidSignatureException(currentToken, SERIALIZATION_TYPE_PROPERTY);
				}
				nextToken();
				
				TypeRefEntry fieldOrPropertyTypeRef = filedOrPropertyType();
			
				String name = readSerString();//FieldOrPropertyName();
				
				TypeInstance instance = fixedArgument(security, fieldOrPropertyTypeRef);
				
				properties[property] = new Pair<String, Instance>(name, instance);
			}
			
			permissions[i] = new Permission(fullyQuallifiedName, properties);
	
		}
		
		//check if there are some properties present
		if(numPermisssions>0) {
			security.setPermissions(permissions);
		}
	}

}

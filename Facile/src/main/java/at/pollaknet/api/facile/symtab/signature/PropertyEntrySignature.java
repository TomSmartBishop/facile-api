package at.pollaknet.api.facile.symtab.signature;

import java.util.Arrays;

import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.metamodel.entries.ParamEntry;
import at.pollaknet.api.facile.metamodel.entries.PropertyEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.symtab.BasicTypesDirectory;
import at.pollaknet.api.facile.symtab.symbols.MethodSignature;
import at.pollaknet.api.facile.symtab.symbols.PropertySignature;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.util.ByteReader;

public class PropertyEntrySignature extends Signature implements PropertySignature {

	private boolean hasThis;
	private ParamEntry[] parameter;
	private TypeRefEntry typeRef;

	private int parameterCount;
	
	public static PropertyEntrySignature decodeAndAttach(BasicTypesDirectory directory, PropertyEntry property)
			throws InvalidSignatureException {
		return new PropertyEntrySignature(directory, property);
	}
	
	public PropertyEntrySignature(BasicTypesDirectory directory, PropertyEntry property)
			throws InvalidSignatureException {
	
		setBinarySignature(property.getTypeSignature());
		setDirectory(directory);
		nextToken();
		
		hasThis = ByteReader.testFlags(currentToken, MethodSignature.CALL_HAS_THIS);
		
		if(!ByteReader.testFlags(currentToken, PREFIX_PROPERTY))
			throw new InvalidSignatureException(binarySignature, currentIndex, currentToken, malformedSignature, "MarshalSignature.NATIVE_*");
		
		nextToken();
		
		parameterCount = decodeIntegerInSignature();
		
		TypeSpecEntry typeSpec = new TypeSpecEntry();
		
		if(!customModifiers(typeSpec)) {
			typeRef = plainType();
		}
		
		if(typeRef==null) {
			type(typeSpec);
			directory.registerEmbeddedTypeSpec(typeSpec);
			typeRef = typeSpec;
		}
		
		parameter = new ParamEntry[parameterCount];
		
		params(parameter, false);
		
		//this code could be handled as backup parameter resolution
		//for(MethodDefEntry m: property.getMethods()) {
		//	boolean paramsIncludeReturn = (m.getParams().length==parameterCount+1);
		//	int index=paramsIncludeReturn?-1:0;
		//	for(int i=0;i<m.getParams().length;i++) {
		//		if(m.getParams()[i].getTypeRef()==null) {
		//			if(i==0 && paramsIncludeReturn) {
		//				m.getParams()[0].setTypeRef(typeRef);
		//			} else {
		//				m.getParams()[i].setTypeRef(parameter[index].getTypeRef());
		//			}
		//		}
		//		index++;
		//	}
		//}
		
		//add the extracted signature to the current property
		property.addPropertySignature(this);
	}

	/* (non-Javadoc)
	 * @see facile.symtab.signature.PropertySignature#hasThis()
	 */
	public boolean hasThis() {
		return hasThis;
	}

	/* (non-Javadoc)
	 * @see facile.symtab.signature.PropertySignature#getParameters()
	 */
	public ParamEntry[] getParameters() {
		if(parameter==null) return new ParamEntry[0];
		return parameter;
	}

	/* (non-Javadoc)
	 * @see facile.symtab.signature.PropertySignature#getTypeRef()
	 */
	public TypeRef getTypeRef() {
		return typeRef;
	}
	
	@Override
	public String toString() {

		StringBuffer buffer = new StringBuffer(32);
	
		if(parameter!=null && parameter.length>0) {
			buffer.append("(");
			for(int i=0;i<parameter.length;i++) {
				if(parameter[i]!=null && parameter[i].getTypeRef()!=null) {
					if(i!=0) buffer.append(", ");

					if(parameter[i].getTypeRef().getShortSystemName()!=null) {
						buffer.append(parameter[i].getTypeRef().getShortSystemName());
					} else {
						buffer.append(parameter[i].getTypeRef().getFullQualifiedName());
					}
					
					buffer.append(" ");
					buffer.append(parameter[i].getName());
				}
			}
			buffer.append(") : ");
		} else {
			buffer.append(" : ");
		}
		
		if(typeRef.getShortSystemName()!=null) {
			buffer.append(typeRef.getShortSystemName());
		} else {
			buffer.append(typeRef.getFullQualifiedName());
		}


		return buffer.toString();
	}

	@Override
	public int getParameterCount() {
		return parameterCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		//don't do this
		//int result = super.hashCode();
		int result = 31;
		result = prime * result + (hasThis ? 1231 : 1237);
		result = prime * result + parameterCount;
		result = prime * result + ((typeRef == null) ? 0 : typeRef.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyEntrySignature other = (PropertyEntrySignature) obj;
		if (hasThis != other.hasThis)
			return false;
		if (!Arrays.equals(parameter, other.parameter))
			return false;
		if (parameterCount != other.parameterCount)
			return false;
		if (typeRef == null) {
			if (other.typeRef != null)
				return false;
		} else if (!typeRef.equals(other.typeRef))
			return false;
		return true;
	}
	
	

}

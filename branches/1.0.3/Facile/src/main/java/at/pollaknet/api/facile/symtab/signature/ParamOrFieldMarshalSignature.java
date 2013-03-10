package at.pollaknet.api.facile.symtab.signature;

import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.metamodel.entries.FieldEntry;
import at.pollaknet.api.facile.metamodel.entries.ParamEntry;
import at.pollaknet.api.facile.symtab.BasicTypesDirectory;
import at.pollaknet.api.facile.symtab.symbols.MarshalSignature;

public class ParamOrFieldMarshalSignature extends Signature implements MarshalSignature {

	private int numberOfArrayElements = -1;
	private String marshalerClassName = null;
	private String marshalerCookie = null;
	private int sizeParameterNumber = -1;
		
	public static ParamOrFieldMarshalSignature decodeAndAttach(BasicTypesDirectory directory, ParamEntry param)
			throws InvalidSignatureException {
		if(param.getBinaryMarshalTypeSignature()!=null)
			return new ParamOrFieldMarshalSignature(directory, param);
		
		return null;
	}
	
	private ParamOrFieldMarshalSignature(BasicTypesDirectory directory, ParamEntry param)
			throws InvalidSignatureException {

		processSignature(directory, param.getBinaryMarshalTypeSignature());
		
		param.setMarshalSignature(this);
	}
	
	public static ParamOrFieldMarshalSignature decodeAndAttach(BasicTypesDirectory directory, FieldEntry field)
			throws InvalidSignatureException {
		if(field.getBinaryMarshalTypeSignature()!=null)
			return new ParamOrFieldMarshalSignature(directory, field);
		
		return null;
	}
	
	private ParamOrFieldMarshalSignature(BasicTypesDirectory directory, FieldEntry field)
			throws InvalidSignatureException {
		
		processSignature(directory, field.getBinaryMarshalTypeSignature());
		
		field.setMarshalSignature(this);
	}

	private void processSignature(BasicTypesDirectory directory, byte [] signature) {	
		setBinarySignature(signature);
		setDirectory(directory);
		nextToken();
		
		if(!nativeType())
			throw new InvalidSignatureException(currentToken);
		
	}

	@Override
	public int getNativeType() {
		return binarySignature[0];
	}
	

	@Override
	public int getArrayElementsNativeType() {
		int i=0;
		while(binarySignature[i]==NATIVE_TYPE_ARRAY) i++;
		
		if(i==0) return -1;
		
		return binarySignature[1];
	}

	@Override
	public boolean isArray() {
		switch(getNativeType()) {
			case NATIVE_TYPE_ARRAY:
			case NATIVE_TYPE_FIXED_SYSSTRING:
			case NATIVE_TYPE_FIXED_ARRAY: return true;
			default: return false;
		}
	}
	
	public int [] getVariantType() {
		int i=0;
		while(binarySignature[i]==NATIVE_TYPE_ARRAY) i++;
		
		if(binarySignature[i]!=MarshalSignature.NATIVE_TYPE_SAFE_ARRAY)
			return null;
		
		currentIndex=i-1;
		nextToken();
		int variant =decodeIntegerInSignature();
		if(variant<0)
			return null;
		
		int [] variantArray = new int [1];
		int currentIndex=0;
		
		variantArray[currentIndex] = variant;
		
		while((variant==0x1000 || variant==0x2000 || variant==0x4000) && hasNext()) {

			currentIndex++;
			
			if(variantArray.length<=currentIndex) {
				int [] resizedArray = new int[variantArray.length+1];
				System.arraycopy(variantArray, 0, resizedArray, 0, variantArray.length);
				variantArray = resizedArray;
			}
			variant = decodeIntegerInSignature();
			
			variantArray[currentIndex] = variant;
		}
		
		return variantArray;
	}
	
	public boolean nativeType() {
		switch(currentToken) {
			case MarshalSignature.NATIVE_TYPE_VOID:
			case MarshalSignature.NATIVE_TYPE_BOOLEAN:
			case MarshalSignature.NATIVE_TYPE_I1:
			case MarshalSignature.NATIVE_TYPE_U1:
			case MarshalSignature.NATIVE_TYPE_I2:
			case MarshalSignature.NATIVE_TYPE_U2:
			case MarshalSignature.NATIVE_TYPE_I4:
			case MarshalSignature.NATIVE_TYPE_U4:
			case MarshalSignature.NATIVE_TYPE_I8:
			case MarshalSignature.NATIVE_TYPE_U8:
			case MarshalSignature.NATIVE_TYPE_R4:
			case MarshalSignature.NATIVE_TYPE_R8:
			case MarshalSignature.NATIVE_TYPE_SYSCHAR:
			case MarshalSignature.NATIVE_TYPE_VARIANT:
			case MarshalSignature.NATIVE_TYPE_CURRENCY:
			case MarshalSignature.NATIVE_TYPE_PTR:
			case MarshalSignature.NATIVE_TYPE_DECIMAL:
			case MarshalSignature.NATIVE_TYPE_DATE:
			case MarshalSignature.NATIVE_TYPE_BSTR:
			case MarshalSignature.NATIVE_TYPE_LPSTR:
			case MarshalSignature.NATIVE_TYPE_LPWSTR:
			case MarshalSignature.NATIVE_TYPE_LPTSTR:
			case MarshalSignature.NATIVE_TYPE_OBJECT_REF:
			case MarshalSignature.NATIVE_TYPE_IUNKNOWN:
			case MarshalSignature.NATIVE_TYPE_IDISPATCH:
			case MarshalSignature.NATIVE_TYPE_STRUCT:
			case MarshalSignature.NATIVE_TYPE_INTERFACE:
			case MarshalSignature.NATIVE_TYPE_INT:
			case MarshalSignature.NATIVE_TYPE_UINT:
			case MarshalSignature.NATIVE_TYPE_NESTED_STRUCT:
			case MarshalSignature.NATIVE_TYPE_BYVAL_STRING:
			case MarshalSignature.NATIVE_TYPE_ANSI_BSTR:
			case MarshalSignature.NATIVE_TYPE_TBSTR:
			case MarshalSignature.NATIVE_TYPE_VARIANT_BOOL:
			case MarshalSignature.NATIVE_TYPE_FUNC:
			case MarshalSignature.NATIVE_TYPE_AS_ANY:
			case MarshalSignature.NATIVE_TYPE_LPSTRUCT:
			case MarshalSignature.NATIVE_TYPE_ERROR:
				nextToken();
				break;
				
			case MarshalSignature.NATIVE_UNKNOWN_0x2E_NI_V404:
			case MarshalSignature.NATIVE_UNKNOWN_0x2F_V404:
				nextToken();
				break;
				
			case MarshalSignature.NATIVE_TYPE_SAFE_ARRAY:
				nextToken();
				break;
			
			case MarshalSignature.NATIVE_TYPE_FIXED_SYSSTRING:
			case MarshalSignature.NATIVE_TYPE_FIXED_ARRAY:
				nextToken();
				numberOfArrayElements = this.decodeIntegerInSignature();
				break;
				

			case MarshalSignature.NATIVE_TYPE_ARRAY:
				nextToken();
				boolean detectedType = nativeType();
				
				//IMPROVE: resolve meaning of unknown token!
				if(!detectedType) nextToken();
				
				if(hasNext()) {
					numberOfArrayElements = this.decodeIntegerInSignature();
					if(numberOfArrayElements==0) {
						if(hasNext()) {
							sizeParameterNumber  = this.decodeIntegerInSignature();
						} else {
							//numberOfArrayElements = -1;
							sizeParameterNumber = 0;
						}
						
					}
				} else {
					sizeParameterNumber = 0;
				}
				break;
			
			case MarshalSignature.NATIVE_TYPE_CUSTOM_MARSHALER:
				nextToken();
				marshalerClassName = readSerString();
				marshalerCookie = readSerString();
				break;
			
			default: return false;
		}
		
		return true;
	}

	public String toString() {
		currentIndex=-1;
		nextToken();
		return formatType();
	}
	
	private String formatType() {
		
		switch(currentToken) {
			case MarshalSignature.NATIVE_TYPE_VOID:
			case MarshalSignature.NATIVE_TYPE_BOOLEAN:
			case MarshalSignature.NATIVE_TYPE_I1:
			case MarshalSignature.NATIVE_TYPE_U1:
			case MarshalSignature.NATIVE_TYPE_I2:
			case MarshalSignature.NATIVE_TYPE_U2:
			case MarshalSignature.NATIVE_TYPE_I4:
			case MarshalSignature.NATIVE_TYPE_U4:
			case MarshalSignature.NATIVE_TYPE_I8:
			case MarshalSignature.NATIVE_TYPE_U8:
			case MarshalSignature.NATIVE_TYPE_R4:
			case MarshalSignature.NATIVE_TYPE_R8:
			case MarshalSignature.NATIVE_TYPE_SYSCHAR:
			case MarshalSignature.NATIVE_TYPE_VARIANT:
			case MarshalSignature.NATIVE_TYPE_CURRENCY:
			case MarshalSignature.NATIVE_TYPE_PTR:
			case MarshalSignature.NATIVE_TYPE_DECIMAL:
			case MarshalSignature.NATIVE_TYPE_DATE:
			case MarshalSignature.NATIVE_TYPE_BSTR:
			case MarshalSignature.NATIVE_TYPE_LPSTR:
			case MarshalSignature.NATIVE_TYPE_LPWSTR:
			case MarshalSignature.NATIVE_TYPE_LPTSTR:
			case MarshalSignature.NATIVE_TYPE_OBJECT_REF:
			case MarshalSignature.NATIVE_TYPE_IUNKNOWN:
			case MarshalSignature.NATIVE_TYPE_IDISPATCH:
			case MarshalSignature.NATIVE_TYPE_STRUCT:
			case MarshalSignature.NATIVE_TYPE_INTERFACE:
			case MarshalSignature.NATIVE_TYPE_INT:
			case MarshalSignature.NATIVE_TYPE_UINT:
			case MarshalSignature.NATIVE_TYPE_NESTED_STRUCT:
			case MarshalSignature.NATIVE_TYPE_BYVAL_STRING:
			case MarshalSignature.NATIVE_TYPE_ANSI_BSTR:
			case MarshalSignature.NATIVE_TYPE_TBSTR:
			case MarshalSignature.NATIVE_TYPE_VARIANT_BOOL:
			case MarshalSignature.NATIVE_TYPE_FUNC:
			case MarshalSignature.NATIVE_TYPE_AS_ANY:
			case MarshalSignature.NATIVE_TYPE_LPSTRUCT:
			case MarshalSignature.NATIVE_TYPE_ERROR:
				return nativeToString(currentToken);
			
			case MarshalSignature.NATIVE_TYPE_SAFE_ARRAY:
				return nativeToString(currentToken) + " " + variantToString(getVariantType());
			
			case MarshalSignature.NATIVE_TYPE_FIXED_SYSSTRING:
			case MarshalSignature.NATIVE_TYPE_FIXED_ARRAY:
				return nativeToString(currentToken) + " " + numberOfArrayElements;
	
			case MarshalSignature.NATIVE_TYPE_ARRAY:
				nextToken();
				return formatType() + formatArrayParameter();
			
			case MarshalSignature.NATIVE_TYPE_CUSTOM_MARSHALER:
				return nativeToString(currentToken) + " \"" + marshalerClassName + "\", \"" + marshalerCookie + "\"";
		}
	
		return "";
	}

	public String nativeToString(int nativeType) {
		
		switch(nativeType) {
			case MarshalSignature.NATIVE_TYPE_VOID: return "void";
			case MarshalSignature.NATIVE_TYPE_BOOLEAN: return "bool";
			case MarshalSignature.NATIVE_TYPE_I1: return "int8";
			case MarshalSignature.NATIVE_TYPE_U1: return "unsigned int8";
			case MarshalSignature.NATIVE_TYPE_I2: return "int16";
			case MarshalSignature.NATIVE_TYPE_U2: return "unsigned int16";
			
			case MarshalSignature.NATIVE_TYPE_I4: return "int32";
			case MarshalSignature.NATIVE_TYPE_U4: return "unsigned int32";
			case MarshalSignature.NATIVE_TYPE_I8: return "int64";
			case MarshalSignature.NATIVE_TYPE_U8: return "unsigned int64";
			case MarshalSignature.NATIVE_TYPE_R4: return "float";
			
			case MarshalSignature.NATIVE_TYPE_R8: return "double";
			case MarshalSignature.NATIVE_TYPE_SYSCHAR: return "syschar";
			case MarshalSignature.NATIVE_TYPE_VARIANT: return "variant";
			case MarshalSignature.NATIVE_TYPE_CURRENCY: return "currency";
			case MarshalSignature.NATIVE_TYPE_PTR: return "*";
			
			case MarshalSignature.NATIVE_TYPE_DECIMAL: return "decimal";
			case MarshalSignature.NATIVE_TYPE_DATE: return "date";
			case MarshalSignature.NATIVE_TYPE_BSTR: return "bstr";
			case MarshalSignature.NATIVE_TYPE_LPSTR: return "lpstr";
			case MarshalSignature.NATIVE_TYPE_LPWSTR: return "lpwstr";
			
			case MarshalSignature.NATIVE_TYPE_LPTSTR: return "lptstr";
			case MarshalSignature.NATIVE_TYPE_FIXED_SYSSTRING: return "fixed systring";
			case MarshalSignature.NATIVE_TYPE_OBJECT_REF: return "objectref";
			case MarshalSignature.NATIVE_TYPE_IUNKNOWN: return "iunknown";
			case MarshalSignature.NATIVE_TYPE_IDISPATCH: return "idispatch";
			
			case MarshalSignature.NATIVE_TYPE_STRUCT: return "struct";
			case MarshalSignature.NATIVE_TYPE_INTERFACE: return "interface";
			case MarshalSignature.NATIVE_TYPE_SAFE_ARRAY: return "safearray";
			case MarshalSignature.NATIVE_TYPE_FIXED_ARRAY: return "fixed array";
			case MarshalSignature.NATIVE_TYPE_INT: return "int";
			
			case MarshalSignature.NATIVE_TYPE_UINT: return "unsigned int";
			case MarshalSignature.NATIVE_TYPE_NESTED_STRUCT: return "nested struct";
			case MarshalSignature.NATIVE_TYPE_BYVAL_STRING: return "byvalstr";
			case MarshalSignature.NATIVE_TYPE_ANSI_BSTR: return "ansi bstr";
			case MarshalSignature.NATIVE_TYPE_TBSTR: return "tbstr";
			
			case MarshalSignature.NATIVE_TYPE_VARIANT_BOOL: return "variant bool";
			case MarshalSignature.NATIVE_TYPE_FUNC: return "method";
			case MarshalSignature.NATIVE_TYPE_AS_ANY: return "as any";
			case MarshalSignature.NATIVE_TYPE_LPSTRUCT: return "lpstruct";
			case MarshalSignature.NATIVE_TYPE_CUSTOM_MARSHALER: return "custom";
			
			case MarshalSignature.NATIVE_TYPE_ERROR: return "error";
			case MarshalSignature.NATIVE_TYPE_ARRAY: return "array";
			
			default: return String.format("unknown_native_0x%x", nativeType);
		}
	}

	public String variantToString(int variant) {
		return variantToString(new int [] {variant});
	}
	
	public String variantToString(int [] variant) {
		return variantToString(variant, 0);
	}
	
	private String variantToString(int [] variant, int index) {
		if(index>=variant.length) return "";
		
		switch(variant[index]) {
			case 0x00: return ""; //<empty>
			case 0x01: return "null";
			case 0x02: return "int16";
			case 0x03: return "int32";
			case 0x04: return "float32";
			case 0x05: return "float64";
			case 0x06: return "currency";
			case 0x07: return "date";
			case 0x08: return "bstr";
			case 0x09: return "idispatch";
			case 0x0a: return "error";
			case 0x0b: return "bool";
			case 0x0c: return "variant";
			case 0x0d: return "iunknown";
			case 0x0e: return "decimal";
			case 0x10: return "int8";
			case 0x11: return "unsigned int8";
			case 0x12: return "unsigned int16";
			case 0x13: return "unsigned int32";
			case 0x14: return "int64";
			case 0x15: return "unsigned in64";
			case 0x16: return "int";
			case 0x17: return "unsigned int";
			case 0x18: return "void";
			case 0x19: return "hresult";
			case 0x1a: return "*";
			case 0x1b: return "safearray";
			case 0x1c: return "carray";
			case 0x1d: return "userdefined";
			case 0x1e: return "lpstr";
			case 0x1f: return "plwstr";
			case 0x24: return "record";
			case 0x40: return "filetime";
			case 0x41: return "blob";
			case 0x42: return "stream";
			case 0x43: return "storage";
			case 0x44: return "streamed_object";
			case 0x45: return "stored_object";
			case 0x46: return "blob_object";
			case 0x47: return "cf";
			case 0x48: return "clsid";
			case 0x1000: return variantToString(variant, ++index) + " vector";
			case 0x2000: return variantToString(variant, ++index) + " []";
			case 0x4000: return variantToString(variant, ++index) + " &";
			
			default: return String.format("unknown_variant_0x%x", variant);
		}
	}

	private String formatArrayParameter() {
		if(isArray()) {
			String sizeString = (numberOfArrayElements==-1?"":""+numberOfArrayElements);
			String sizeParameter = (sizeParameterNumber==-1?"":" + "+sizeParameterNumber);
			return " [" + sizeString + sizeParameter + "]";
		}
		return "";
	}

	public int getNumberOfArrayElements() {
		return numberOfArrayElements;
	}

	public String getMarshalerClassName() {
		return marshalerClassName;
	}

	public String getMarshalerCookie() {
		return marshalerCookie;
	}

	public int getSizeParameterNumber() {
		return sizeParameterNumber;
	}

	@Override
	public int hashCode() {
		return 37 * super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParamOrFieldMarshalSignature other = (ParamOrFieldMarshalSignature) obj;
		if (marshalerClassName == null) {
			if (other.marshalerClassName != null)
				return false;
		} else if (!marshalerClassName.equals(other.marshalerClassName))
			return false;
		if (marshalerCookie == null) {
			if (other.marshalerCookie != null)
				return false;
		} else if (!marshalerCookie.equals(other.marshalerCookie))
			return false;
		if (numberOfArrayElements != other.numberOfArrayElements)
			return false;
		if (sizeParameterNumber != other.sizeParameterNumber)
			return false;
		return true;
	}
	
	
}

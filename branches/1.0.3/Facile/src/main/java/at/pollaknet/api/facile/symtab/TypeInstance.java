package at.pollaknet.api.facile.symtab;

import java.util.Arrays;

import at.pollaknet.api.facile.symtab.symbols.Instance;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.util.ArrayUtils;

/**
 * Represents an instance of a type within a .Net assembly. All custom
 * attributes are instances of a class derived from CustomAttribute.
 */
public class TypeInstance implements Instance {

	//the type of the instance
	private TypeRef typeRef;
	
	//the numeric value of the instance (if set)
	private long numericValue;
	
	//the string value of the instance (if set)
	private String stringValue;
	
	//if the instance is a whole array, the instances are hold here
	private TypeInstance [] array = null;
	
	//used if it is a boxed type instance
	private TypeInstance boxedTypeInstance;

	//private constructor to set the type reference
	private TypeInstance(TypeRef typeRef) {
		this.typeRef = typeRef;
	}
	
	/**
	 * Create a numeric type instance (in case of an unsigned
	 * long 64bit or any float/double type please use the
	 * string instance constructor). 
	 * @param type The type of the numeric instance.
	 * @param value The value of the numeric instance.
	 */
	public TypeInstance(TypeRef type, long value) {
		this(type);
		this.numericValue = value;
	}
	
	/**
	 * Create a type instance which value can be represented as {@code String}. 
	 * @param type The type of the instance.
	 * @param stringValue The value of the instance represented as {@code String}.
	 */
	public TypeInstance(TypeRef type, String stringValue) {
		this(type);
		this.stringValue = stringValue;
	}

	/**
	 * Create a boxed type instance.
	 * @param type The outer type, which should be {@code Object}.
	 * @param boxedTypeInstance The boxed type instance.
	 */
	public TypeInstance(TypeRef type, TypeInstance boxedTypeInstance) {
		this(type);
		this.boxedTypeInstance = boxedTypeInstance;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.Instance#getTypeRef()
	 */
	public TypeRef getTypeRef() {
		return typeRef;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.Instance#setTypeRef(at.pollaknet.api.facile.symtab.symbols.TypeRef)
	 */
	public void setTypeRef(TypeRef typeRef) {
		this.typeRef = typeRef;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.Instance#getValue()
	 */
	public long getValue() {
		return numericValue;
	}
	
	/**
	 * Set the numeric value of the instance (no matter if byte, int, long, ...).
	 * @param value The numeric value to set.
	 */
	public void setValue(long value) {
		this.numericValue = value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.Instance#getStringValue()
	 */
	public String getStringValue() {
		return stringValue;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.Instance#isArray()
	 */
	public boolean isArray() {
		return array!=null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.Instance#getArrayInstance()
	 */
	public TypeInstance[] getArrayInstance() {
		return array;
	}

	/**
	 * Create an instance containing an array of instances.
	 * @param type The type of the array elements.
	 * @param arrayLength The length of the array.
	 * @return An instance containing an initialized array
	 * (use {@link at.pollaknet.api.facile.symtab.TypeInstance#getArrayInstance()}
	 * to get the array of the instance).
	 */
	public static TypeInstance CreateArrayInstnace(TypeRef type, int arrayLength) {
		TypeInstance instance = new TypeInstance(type);
		instance.array = new TypeInstance[arrayLength];
		
		return instance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.Instance#getBoxedInstance()
	 */
	public TypeInstance getBoxedInstance() {
		return boxedTypeInstance;
	}
	
	public String toString() {
		//IMPROVE: implement ILAsm conforming representation for boxed values
		if(getTypeRef().getElementTypeKind()==TypeKind.ELEMENT_TYPE_BOOLEAN) {
			return getValue()!=0?"true":"false";
		}

		StringBuffer buffer = new StringBuffer();
		
		
		if(array!=null) {
			boolean first = true;
			//buffer.append("(");
			buffer.append(getTypeRef().getFullQualifiedName());
			
			buffer.append("[");
			buffer.append(array.length);
			buffer.append("] {");
			
			for(TypeInstance i: array) {
				buffer.append(i.toString());
				if(first) {
					first = false;
				} else {
					buffer.append(" ");
				}
			}
			
			buffer.append("}");
			return buffer.toString();
		} 
		
		if(boxedTypeInstance!=null) {
			buffer.append(boxedTypeInstance.toString());
		} else {
			//buffer.append("(");
			if(getTypeRef().getShortSystemName()!=null) {
				buffer.append(getTypeRef().getName());
			} else {
				buffer.append(getTypeRef().getFullQualifiedName());
			}
			buffer.append("(");
			
			if(getStringValue()!=null) {
				if(getTypeRef().getElementTypeKind()==TypeKind.ELEMENT_TYPE_STRING) {
					buffer.append("'");
					buffer.append(getStringValue());
					buffer.append("'");
				} else {
					buffer.append(getStringValue());
				}
			} else {
				if(getValue()==0 && !ArrayUtils.contains(TypeKind.NUMERIC_TYPES, getTypeRef().getElementTypeKind())) {
					buffer.append("null");
				} else {
					buffer.append(getValue());
				}
			}
			buffer.append(")");
		}
			
		return buffer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + Arrays.hashCode(array);
		result = prime * result + (int) (numericValue ^ (numericValue >>> 32));
		return prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeInstance other = (TypeInstance) obj;
		if (!Arrays.equals(array, other.array))
			return false;
		
		if (boxedTypeInstance == null) {
			if (other.boxedTypeInstance != null)
				return false;
		} else if (!boxedTypeInstance.equals(other.boxedTypeInstance))
			return false;
		if (numericValue != other.numericValue)
			return false;
		if (stringValue == null) {
			if (other.stringValue != null)
				return false;
		} else if (!stringValue.equals(other.stringValue))
			return false;
		if (typeRef == null) {
			if (other.typeRef != null)
				return false;
		} else if (!typeRef.getFullQualifiedName().equals(
				other.typeRef.getFullQualifiedName()))
			return false;
		return true;
	}

	@Override
	public boolean isPotentialValueType() {
		Type type = typeRef.getType();
		if(type!=null) {
			return type.getElementTypeKind()==TypeKind.ELEMENT_TYPE_VALUETYPE || type.isInheritedFrom("System.ValueType");
		}
		
		if(BasicTypesDirectory.getTypeKindByString(typeRef)!=0) return false;
		
		return typeRef.getTypeSpec()==null&&boxedTypeInstance==null&&array==null&&stringValue==null;
	}

	
}

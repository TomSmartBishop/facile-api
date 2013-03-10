package at.pollaknet.api.facile.metamodel.entries;


import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.ITypeSpecInstances;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasConstant;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasFieldMarshal;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IMemberForwarded;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.signature.ParamOrFieldMarshalSignature;
import at.pollaknet.api.facile.symtab.symbols.Constant;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.FieldLayout;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.NativeImplementation;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Property;
import at.pollaknet.api.facile.util.ArrayUtils;

public class FieldEntry extends AbstractAttributable implements IHasCustomAttribute, IHasConstant,
		IHasFieldMarshal, IMemberForwarded, Field, ITypeSpecInstances {

	private int flags;
	private String name;
	private byte [] signature;
	private ConstantEntry constantEntry;
	private FieldLayoutEntry fieldLayoutEntry;
	
	private byte[] marshaledType;
	private long relativeVirtualAddress = -1;
	private NativeImplementation nativeImpl;
	private TypeRefEntry typeRef;
	private TypeDefEntry parent;
	private ParamOrFieldMarshalSignature fieldMarshalSignature;

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Field#getFlags()
	 */
	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Field#getName()
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Field#getBinarySignature()
	 */
	public byte [] getBinarySignature() {
		return signature;
	}

	public void setBinarySignature(byte [] signature) {
		this.signature = signature;
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Field#toString()
	 */
	@Override
	public String toString() {
		return String.format("Field: %s (Flags: 0x%04x) Signature: %s",
				name, flags, signature==null?"[not set]":ArrayUtils.formatByteArray(signature));
	}
	
	public String toExtendedString() {
		StringBuffer buffer = new StringBuffer(64);
		
		if(getCustomAttributes()!=null) {
			for(CustomAttributeEntry c: getCustomAttributes()) {
				buffer.append(c.toExtendedString());
				buffer.append("\n");
			}
		}
		
		buffer.append("Field: ");
		buffer.append(typeRef==null?"[not set]":typeRef.getFullQualifiedName());
		buffer.append(" ");
		buffer.append(name);
		buffer.append(String.format(" (Flags: 0x%04x)", flags));
		
		return buffer.toString();
	}

	@Override
	public void setConstant(ConstantEntry constantEntry) {
		this.constantEntry = constantEntry;
	}

	@SuppressWarnings("cast")
	public Constant getConstant() {
		return (Constant) constantEntry;
	}
	
	public void setFieldLayout(FieldLayoutEntry fieldLayoutEntry) {
		this.fieldLayoutEntry = fieldLayoutEntry;
	}

	public FieldLayout getFieldLayout() {
		return (FieldLayout) fieldLayoutEntry;
	}
	
	@Override
	public byte[] getBinaryMarshalTypeSignature() {
		return marshaledType;
	}
	
	public void setBinaryMarshalTypeSignature(byte [] type) {
		marshaledType = type;
	}

	@Override
	public long getRelativeVirtualAddress() {
		return relativeVirtualAddress;
	}
	
	public void setRelativeVirtualAddress(long virtualAddress) {
		this.relativeVirtualAddress = virtualAddress;
	}

	@Override
	public NativeImplementation getNativeImplementation() {
		return nativeImpl;
	}
	
	public void setNativeImplementation(NativeImplementation nativeImpl) {
		this.nativeImpl = nativeImpl;
	}

	@Override
	public Field getField() {
		return this;
	}

	@Override
	public Method getMethod() {
		return null;
	}

	@Override
	public Parameter getParameter() {
		return null;
	}

	@Override
	public Property getProperty() {
		return null;
	}

	@Override
	public TypeRefEntry getTypeRef() {
		return typeRef;
	}

	public void setEnclosedTypeRef(TypeRefEntry typeRef) {
		this.typeRef = typeRef; 
	}

	public void setMarshalSignature(ParamOrFieldMarshalSignature paramMarshalSignature) {
		this.fieldMarshalSignature = paramMarshalSignature;
	}
	
	@Override
	public ParamOrFieldMarshalSignature getMarshalSignature() {
		return fieldMarshalSignature;
	}

	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}

	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return renderer.renderAsReference(this);
	}

	@Override
	public int compareTo(Field o) {
		return ArrayUtils.compareStrings(o.getName(), getName());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ (int) (relativeVirtualAddress ^ (relativeVirtualAddress >>> 32));
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
		FieldEntry other = (FieldEntry) obj;
		if (constantEntry == null) {
			if (other.constantEntry != null)
				return false;
		} else if (!constantEntry.equals(other.constantEntry))
			return false;
		if (fieldLayoutEntry == null) {
			if (other.fieldLayoutEntry != null)
				return false;
		} else if (!fieldLayoutEntry.equals(other.fieldLayoutEntry))
			return false;
		if (fieldMarshalSignature == null) {
			if (other.fieldMarshalSignature != null)
				return false;
		} else if (!fieldMarshalSignature.equals(other.fieldMarshalSignature))
			return false;
		if (flags != other.flags)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nativeImpl == null) {
			if (other.nativeImpl != null)
				return false;
		} else if (!nativeImpl.equals(other.nativeImpl))
			return false;
		if (relativeVirtualAddress != other.relativeVirtualAddress)
			return false;
		if (typeRef == null) {
			if (other.typeRef != null)
				return false;
		} else if (!typeRef.getFullQualifiedName().equals(other.typeRef.getFullQualifiedName()))
			return false;
		return true;
	}

	@Override
	public String getFullQualifiedName() {
		if(parent!=null)
			return parent.getFullQualifiedName() + "." +  name;
		
		return name;
	}

	public void setParent(TypeDefEntry typeDefEntry) {
		this.parent = typeDefEntry;
	}
	
	public TypeDefEntry getParent() {
		return this.parent;
	}

	public void linkGenericNameToType() {
		//consider type specs and type definitions
		TypeSpecEntry typeSpec = typeRef.getTypeSpec();
		TypeDefEntry type = typeRef.getType();

		//in case of a type defintion
		if(type!=null) {
			for(GenericParamEntry param : parent.getGenericParameters()) {
				for(GenericParamEntry innerParam : parent.getGenericParameters()) {
					if(innerParam.getName()==null && param.getNumber()==innerParam.getNumber()) {
						innerParam.setName(param.getName());
						break; //break for inner loop
					} 
				}
			}
		}
		
		if(typeSpec==null)
			return;
		
		//dig down to the most inner type spec
		typeSpec = typeSpec.getMostInnerEnclosedTypeSpec();
		
		//"is generic" applies for members like 'public LinkedList<T> List', where
		//"is generic instance" is true for 'public T Node'
		if(typeSpec.isGenericInstance()||typeSpec.isGeneric()) {
			for(Parameter param : parent.getGenericParameters()) {
				int genericNumber = param.getNumber();
				if(genericNumber==typeSpec.getGenericParameterNumber()) {
					typeSpec.setName(param.getName());
					break;
				} else if (typeSpec.isGeneric()) {
					TypeRefEntry[] genericArguments = typeSpec.getGenericArguments();
					
					if(genericNumber<genericArguments.length && genericArguments[genericNumber].getName()==null) {
						genericArguments[genericNumber].setName(param.getName());
					}
					//no break in this case since there could be multiple generic parameter: Map<K,V>
				}
			}
		}
	}
}
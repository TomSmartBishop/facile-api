package at.pollaknet.api.facile.metamodel.entries;


import java.util.logging.Level;
import java.util.logging.Logger;

import at.pollaknet.api.facile.FacileReflector;
import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasConstant;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasFieldMarshal;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeDefOrRef;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeOrMethodDef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.signature.ParamOrFieldMarshalSignature;
import at.pollaknet.api.facile.symtab.symbols.Constant;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Property;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.util.ArrayUtils;

public class ParamEntry extends AbstractAttributable implements IHasCustomAttribute,
		IHasConstant, IHasFieldMarshal, Parameter {

	private int flags;
	private int sequence;
	private String name;
	private ConstantEntry constantEntry;
	private ITypeDefOrRef type;
	
	private byte[] marshaledType;
	private ParamOrFieldMarshalSignature paramMarshalSignature;
	private MethodDefEntry owner;

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Parameter#getFlags()
	 */
	public int getFlags() {
		return flags;
	}
	public void setFlags(int flags) {
		this.flags = flags;
	}
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Parameter#getSequence()
	 */
	public int getNumber() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Parameter#getName()
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    @Override
    public ITypeOrMethodDef getOwner() {
        return owner;
    }

    @Override
	public String toString() {
		return String.format("Param: %s %s (Flags: 0x%04x Sequence: %d)",
				(type==null?"[not set]":type.toString()), name, flags, sequence );
	}
	
	@Override
	public void setConstant(ConstantEntry constantEntry) {
		this.constantEntry = constantEntry;
	}

	public Constant getConstant() {
		return constantEntry;
	}

    @Override
    public TypeRef[] getConstraints() {
        return EMPTY;
    }

    @Override
	public byte[] getBinaryMarshalTypeSignature() {
		return marshaledType;
	}
	
	public void setBinaryMarshalTypeSignature(byte [] type) {
		marshaledType = type;
	}
	
	public void setTypeRef(ITypeDefOrRef type) {
		this.type = type;
	}
	
	public TypeRef getTypeRef() {
		if(type==null) return null;
		
		return type.getTypeRef();
	}
	@Override
	public Field getField() {
		return null;
	}
	@Override
	public Parameter getParameter() {
		return this;
	}
	@Override
	public Property getProperty() {
		return null;
	}
	@Override
	public boolean isGeneric() {
		return false;
	}
	
	public void setMarshalSignature(ParamOrFieldMarshalSignature paramMarshalSignature) {
		this.paramMarshalSignature = paramMarshalSignature;
	}
	
	public ParamOrFieldMarshalSignature getMarshalSignature() {
		return paramMarshalSignature;
	}
	
	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}
	
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}
	
	@Override
	public int compareTo(Parameter p) {
		return ArrayUtils.compareStrings(p.getName(), getName());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + flags;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + sequence;
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
		ParamEntry other = (ParamEntry) obj;
		if (constantEntry == null) {
			if (other.constantEntry != null)
				return false;
		} else if (!constantEntry.equals(other.constantEntry))
			return false;
		if (flags != other.flags)
			return false;
		if (sequence != other.sequence)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;

		//compare the type via the full qualified name
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (other.type == null) {
			return false;
		} else {
			String typeName = type.getFullQualifiedName();
			String otherTypeName = other.type.getFullQualifiedName();
			//first check also works if both strings are null
			if(typeName!=otherTypeName && typeName!=null && !typeName.equals(otherTypeName))
				return false;
		}
		
		if (paramMarshalSignature == null) {
			if (other.paramMarshalSignature != null)
				return false;
		} else if (!paramMarshalSignature.equals(other.paramMarshalSignature))
			return false;
		
		return true;
	}
	
	public void linkGenericNameToType() {
		//consider type specs and type definitions
		TypeSpecEntry typeSpec = type.getTypeSpec();
		//FIXME: the following assertion fails, so this case is possible..!
		//assert(type.getType()==null);
		if(type.getType()!=null) {
			Logger.getLogger(FacileReflector.LOGGER_NAME).log(
					Level.SEVERE, "Unhandled generic type: " + type.getType().toString() + " for param " + toString());
		}

		if(typeSpec==null)
			return;
		
		//dig down to the most inner type spec
		typeSpec = typeSpec.getMostInnerEnclosedTypeSpec();

		//in case the this is a generic method parameter (not a generic parameter of the owner type)
		if(typeSpec.isGenericMethodParameter()) {
			Parameter [] genericMethodParams = owner.getGenericParameters();
			assert(genericMethodParams!=null);
			
			for(Parameter param : genericMethodParams) {
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
			
			assert(typeSpec.getName()!=null);
			
			//in this case we are done here...
			return;
		}
		
		Type ownerType = owner.getOwner().getType();
		assert(ownerType!=null);
		
		//"is generic" applies for members like 'public LinkedList<T> List', where
		//"is generic instance" is true for 'public T Node'
		if(typeSpec.isGenericInstance()||typeSpec.isGeneric()) {
			
			//(1)
			if(typeSpec.getGenericArguments()!=null) {
				int genericIndexHelper = 0;
				for(TypeRefEntry t : typeSpec.getGenericArguments()) {
					TypeSpecEntry enclosedTypeSpec = typeSpec.getTypeSpec();
					if(enclosedTypeSpec!=null) {
						TypeRefEntry typeRefEntry = enclosedTypeSpec.getMostInnerEnclosedTypeSpec().getEnclosedTypeRef();
						TypeDefEntry typeDefEntry = typeRefEntry!=null ? typeRefEntry.getType() : null;
						if(typeDefEntry!=null) {
							GenericParamEntry[] genericParams = typeDefEntry.getGenericParameters();
								
							if(genericParams!=null && genericIndexHelper<genericParams.length &&
									(t.getName()==null)) {
													 //|| t.getName().equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME))) {
								t.setName(genericParams[genericIndexHelper].getName());
							}
						}
					}
					genericIndexHelper++;
				}
			}
			for(Parameter param : ownerType.getGenericParameters()) {
				int genericNumber = param.getNumber();
					
				if(genericNumber==typeSpec.getGenericParameterNumber()) {
					typeSpec.setName(param.getName());
					break;
				} else if (typeSpec.isGeneric()) {
					//this seems to be handled entirely by (1)
					TypeRefEntry[] genericArguments = typeSpec.getGenericArguments();
					
					if(genericNumber<genericArguments.length && (genericArguments[genericNumber].getName()==null)) {
							// || genericArguments[genericNumber].getName().equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME))) {
						genericArguments[genericNumber].setName(param.getName());
					}
					//no break in this case since there could be multiple generic parameter: Map<K,V>
					
//					if(param.getTypeRef()!=null && param.getTypeRef().getName()!=null && param.getTypeRef().getName().equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME)) {
//						int z=0;
//						z++;
//					}
					
//					for(TypeRefEntry tre : genericArguments) {
//						if(tre.getTypeRef()!=null && tre.getTypeRef().getName()!=null && tre.getTypeRef().getName().equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME)) {
//							int z=0;
//							z++;
//						}
//					}
				}
			}
		}
	}
	
	public void setOwner(MethodDefEntry owner) {
		this.owner = owner;
	}
}

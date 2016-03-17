package at.pollaknet.api.facile.metamodel.entries;


import java.util.ArrayList;
import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeDefOrRef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.signature.ArrayShape;
import at.pollaknet.api.facile.symtab.symbols.MethodSignature;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;
import at.pollaknet.api.facile.symtab.symbols.aggregation.ResolutionScope;
import at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.ByteReader;


public class TypeSpecEntry extends TypeRefEntry implements ITypeDefOrRef,
		IHasCustomAttribute, MethodAndFieldParent, TypeSpec, TypeRef {

	//See ECMA 335 revision 4 - Partition II, 23.2.14 TypeSpec
	//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=287&view=FitH
	
	//	TypeSpecBlob ::=
	//		PTR CustomMod* VOID
	//		| PTR CustomMod* Type
	//		| FNPTR MethodDefSig
	//		| FNPTR MethodRefSig
	//		| ARRAY Type ArrayShape	
	//		| SZARRAY CustomMod* Type
	//		| GENERICINST (CLASS | VALUETYPE) TypeDefOrRefEncoded GenArgCount Type Type*
	
	private static final int FLAGS_IS_POINTER 			= 0x0001;
	private static final int FLAGS_IS_FUNCTION_POINTER 	= 0x0002;
	private static final int FLAGS_IS_VALUE_TYPE	 	= 0x0004;
	private static final int FLAGS_IS_CLASS				= 0x0008;
	private static final int FLAGS_IS_SINGLE_DIM_ARRAY 	= 0x0010;
	private static final int FLAGS_IS_GENERAL_ARRAY 	= 0x0020;
	private static final int FLAGS_IS_TYPE_BY_REF		= 0x0040;
	private static final int FLAGS_IS_GENERIC_INSTANCE 	= 0x0080;
	private static final int FLAGS_IS_PINNED		 	= 0x0100;
	private static final int FLAGS_IS_BOXED			 	= 0x0200;
	
	private byte [] binarySignature;
	private TypeRefEntry enclosedType = null;

	private ArrayList<TypeSpec> optionalModifiers = null;
	private ArrayList<TypeSpec> requiredModifiers = null;

	private int flags;
	private MethodSignature functionPointer = null;
	private int genericNumber = -1;
    private boolean genericBelongsToMethod = false;
	private ArrayShape arrayShape = null;
	private TypeRefEntry[] genericArguments = null;

	private boolean isBasicType = false;
	private int pointer = 0;

	@Override
	public void setName(String name) {
		if(this.name==null) {//||this.name.equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME)) {
//			if(enclosedType!=null)
//				enclosedType.setName(name);
//			else
				super.setName(name);
		} else {
			assert(enclosedType!=null);
			enclosedType.setName(name);
		}
		
	}
	
	public byte[] getBinarySignature() {
		return binarySignature;
	}

	public void setSignature(byte[] binarySignature) {
		this.binarySignature = binarySignature;
	}
	
	@Override
	public String toString() {
		return String.format("TypeSpec: %s ResoltionScope: %s",
				getFullQualifiedName(),resolutionScope==null?"[not set]":resolutionScope.getName());
	}
	
	@Override
	public String getNamespace() {
		if(namespace==null && enclosedType!=null) return enclosedType.getNamespace();
		return namespace;
	}
	
	@Override
	public String getName() {
		if(name==null && enclosedType!=null) return enclosedType.getName();
		return name;
	}

    @Override
	public String getFullQualifiedName() {
		String namespace = getNamespace();
		if(namespace==null || namespace.length() == 0)
			return getName();
		return getNamespace() + namespaceSeparator + getName();
	}

	public void adjustNamespace(ModuleRef moduleRef) {
		
		TypeRefEntry enclosedTypeRef = this;
		while(enclosedTypeRef.getTypeSpec()!=null && enclosedTypeRef.getTypeSpec().getEnclosedTypeRef()!=null) {
			enclosedTypeRef = enclosedTypeRef.getTypeSpec().getEnclosedTypeRef();
		}
		
		adjustNamespace(enclosedTypeRef, moduleRef);
	}
	
	@Override
	public String getExtName() {
		StringBuffer buffer = new StringBuffer();
		
		if(getName()!=null) {
			buffer.append(getName());
		} else if(enclosedType!=null) {

			TypeSpec enclosedTypeSpec = enclosedType.getTypeSpec();
			
			if(enclosedTypeSpec!=null) { 
				buffer.append(enclosedTypeSpec.getExtName());
			} else {
				String shortName = enclosedType.getShortSystemName();
				
				buffer.append(shortName!=null?shortName:enclosedType.getName());
			}
			
			if(isSingleDimensionalZeroBasedArray()) {
				buffer.append("[]");
			} else if(isGeneralArray()) {
				buffer.append("[");
				for(int i=1;i<getArrayShape().getRank();i++) buffer.append(",");
				buffer.append("]");
			} else if(!isBasicType) {
				if(isTypeByRef()) buffer.append(" &");
				if(isPointer()) buffer.append(" *");
				
				if(requiredModifiers!=null) {
					for(TypeSpec spec: requiredModifiers) {
						ResolutionScope resolutionScope = spec.getResolutionScope();
						TypeRef typeRef = spec;
						while((resolutionScope==null || resolutionScope.isInAssembly()) && typeRef.getTypeSpec()!=null && typeRef.getTypeSpec().getEnclosedTypeRef()!=null) {
							typeRef = typeRef.getTypeSpec().getEnclosedTypeRef();
							resolutionScope = typeRef.getResolutionScope();
						}
						buffer.append(" modreq(");
						if(resolutionScope!=null && !resolutionScope.isInAssembly()) {
							buffer.append("[");
							buffer.append(resolutionScope.getName());
							buffer.append("] ");
						}
						buffer.append(spec.getExtName());
						buffer.append(")");
					}
				}

				if(optionalModifiers!=null) {
					for(TypeSpec spec: optionalModifiers) {
						ResolutionScope resolutionScope = spec.getResolutionScope();
						TypeRef typeRef = spec;
						while((resolutionScope==null || resolutionScope.isInAssembly()) && typeRef.getTypeSpec()!=null && typeRef.getTypeSpec().getEnclosedTypeRef()!=null) {
							typeRef = typeRef.getTypeSpec().getEnclosedTypeRef();
							resolutionScope = typeRef.getResolutionScope();
						}
						buffer.append(" modopt(");
						if(resolutionScope!=null && !resolutionScope.isInAssembly()) {
							buffer.append("[");
							buffer.append(resolutionScope.getName());
							buffer.append("] ");
						}
						buffer.append(spec.getExtName());
						buffer.append(")");
					}
				}
			}
		} else if(binarySignature!=null) {
			buffer.append("[Signature: ");
			buffer.append(ArrayUtils.formatByteArray(binarySignature));
			buffer.append("]");
		} else {
			buffer.append("[Signature: null]");
		}

		if(genericArguments!=null){
			buffer.append("<");
			for(int i=0;i<genericArguments.length;i++) {
				if(i!=0) buffer.append(", ");
				
				if(genericArguments[i].getTypeSpec()!=null && genericArguments[i].getTypeSpec().getGenericParameterNumber()>=0)
					buffer.append("!");
				
				String shortName = genericArguments[i].getShortSystemName();
				buffer.append(shortName!=null?shortName:genericArguments[i].getFullQualifiedName());
			}
			buffer.append(">");
		}
		
		return buffer.toString();
	}
	
	@Override
	public String getExtFullQualifiedName() {
		StringBuffer buffer = new StringBuffer();
		
		if(name!=null) {
			buffer.append(getFullQualifiedName());
		} else if(enclosedType!=null) {

			TypeSpec enclosedTypeSpec = enclosedType.getTypeSpec();
			
			if(enclosedTypeSpec!=null) { 
				buffer.append(enclosedTypeSpec.getExtFullQualifiedName());
			} else {
				String shortName = enclosedType.getShortSystemName();
				
				buffer.append(shortName!=null?shortName:enclosedType.getFullQualifiedName());
			}
			
			if(isSingleDimensionalZeroBasedArray()) {
				buffer.append("[]");
			} else if(isGeneralArray()) {
				buffer.append("[");
				for(int i=1;i<getArrayShape().getRank();i++) buffer.append(",");
				buffer.append("]");
			} else if (!isBasicType()) {
				if(isTypeByRef()) buffer.append(" &");
				if(isPointer()) buffer.append(" *");
				
				if(requiredModifiers!=null) {
					for(TypeSpec spec: requiredModifiers) {
						ResolutionScope resolutionScope = spec.getResolutionScope();
						TypeRef typeRef = spec;
						while((resolutionScope==null || resolutionScope.isInAssembly()) && typeRef.getTypeSpec()!=null && typeRef.getTypeSpec().getEnclosedTypeRef()!=null) {
							typeRef = typeRef.getTypeSpec().getEnclosedTypeRef();
							resolutionScope = typeRef.getResolutionScope();
						}
						buffer.append(" modreq(");
						if(resolutionScope!=null && !resolutionScope.isInAssembly()) {
							buffer.append("[");
							buffer.append(resolutionScope.getName());
							buffer.append("] ");
						}
						buffer.append(spec.getExtFullQualifiedName());
						buffer.append(")");
					}
				}

				if(optionalModifiers!=null) {
					for(TypeSpec spec: optionalModifiers) {
						ResolutionScope resolutionScope = spec.getResolutionScope();
						TypeRef typeRef = spec;
						while((resolutionScope==null || resolutionScope.isInAssembly()) && typeRef.getTypeSpec()!=null && typeRef.getTypeSpec().getEnclosedTypeRef()!=null) {
							typeRef = typeRef.getTypeSpec().getEnclosedTypeRef();
							resolutionScope = typeRef.getResolutionScope();
						}
						buffer.append(" modopt(");
						if(resolutionScope!=null && !resolutionScope.isInAssembly()) {
							buffer.append("[");
							buffer.append(resolutionScope.getName());
							buffer.append("] ");
						}
						buffer.append(spec.getExtFullQualifiedName());
						buffer.append(")");
					}
				}
			}
		} else if(binarySignature!=null) {
			buffer.append("[Signature: ");
			buffer.append(ArrayUtils.formatByteArray(binarySignature));
			buffer.append("]");
		} else {
			buffer.append("[Signature: null]");
		}

		if(genericArguments!=null){
			buffer.append("<");
			for(int i=0;i<genericArguments.length;i++) {
				if(i!=0) buffer.append(", ");
				
				if(genericArguments[i].getTypeSpec()!=null && genericArguments[i].getTypeSpec().getGenericParameterNumber()>=0)
					buffer.append("!");
				
				String shortName = genericArguments[i].getShortSystemName();
				buffer.append(shortName!=null?shortName:genericArguments[i].getFullQualifiedName());
				
			}
			buffer.append(">");
		}
		
		return buffer.toString();
	}

	public void addOptionalModifier(TypeSpec optionalOrReqType) {
		if(optionalModifiers==null) optionalModifiers = new ArrayList<>(4);
		optionalModifiers.add(optionalOrReqType);
	}
	
	public TypeSpec[] buildOptionalModifierArray() {
		if(optionalModifiers==null || optionalModifiers.size()==0) return new TypeSpec[0];
		
		TypeSpec [] modifiers = new TypeSpec[optionalModifiers.size()];
		optionalModifiers.toArray(modifiers);
		
		return modifiers;
	}

	public void addRequiredModifier(TypeSpec modifier) {
		if(requiredModifiers==null) requiredModifiers = new ArrayList<>(4);
		requiredModifiers.add(modifier);
	}
	
	public TypeSpec[] buildRequiredModifierArray() {
		if(requiredModifiers==null || requiredModifiers.size()==0) return new TypeSpec[0];
		
		TypeSpec [] modifiers = new TypeSpec[requiredModifiers.size()];
		requiredModifiers.toArray(modifiers);
		
		return modifiers;
	}

	@Override
	public TypeRefEntry getTypeRef() {
		return this;
	}
	
	@Override
	public TypeRefEntry getEnclosedTypeRef() {
		return enclosedType;
	}
	
	@Override
	public TypeRefEntry getMostInnerEnclosedTypeRef() {
		if(enclosedType==null) return this;

		TypeRefEntry  typeRef = enclosedType;
		TypeSpecEntry typeSpec = enclosedType.getTypeSpec();
		
		while(typeSpec!=null && typeSpec.getEnclosedTypeRef()!=null) {
			
			typeRef = typeSpec.getEnclosedTypeRef();
			typeSpec = typeRef.getTypeSpec();
		}
		
		return typeRef;
	}
	
	@Override
	public TypeSpecEntry getMostInnerEnclosedTypeSpec() {
		if(enclosedType==null) return this;

		TypeSpecEntry typeSpec = enclosedType.getTypeSpec();
		
		if(typeSpec==null) return this;
		
		while(typeSpec.getEnclosedTypeRef()!=null && typeSpec.getEnclosedTypeRef().getTypeSpec()!=null) {
			typeSpec =  typeSpec.getEnclosedTypeRef().getTypeSpec();
		}
		
		return typeSpec;
	}
	
	public void setEnclosedTypeRef(TypeRefEntry type) {
		this.enclosedType = type;
	}
	
	public void setTypeByRef(boolean typeByRef) {
		if(typeByRef)	this.flags |= FLAGS_IS_TYPE_BY_REF;
		else  			this.flags &= ~FLAGS_IS_TYPE_BY_REF;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isTypeByRef()
	 */
	public boolean isTypeByRef() {
//		if(ByteReader.testFlags(flags, FLAGS_IS_TYPE_BY_REF)) return true;
//		if(enclosedType!=null && enclosedType.getTypeSpec()!=null)
//			return enclosedType.getTypeSpec().isTypeByRef();
//		
//		return false;
		
		return ByteReader.testFlags(flags, FLAGS_IS_TYPE_BY_REF);
	}

	public void setSingleDimensionalZeroBasedArray(boolean isArray) {
		if(isArray)	this.flags |= FLAGS_IS_SINGLE_DIM_ARRAY;
		else  		this.flags &= ~FLAGS_IS_SINGLE_DIM_ARRAY;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isSingleDimensionalZeroBasedArray()
	 */
	public boolean isSingleDimensionalZeroBasedArray() {
		return ByteReader.testFlags(flags, FLAGS_IS_SINGLE_DIM_ARRAY);
	}

	public void setGeneralArray(boolean isArray) {
		if(isArray) this.flags |= FLAGS_IS_GENERAL_ARRAY;
		else  		this.flags &= ~FLAGS_IS_GENERAL_ARRAY;
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isGeneralArray()
	 */
	public boolean isGeneralArray() {
		return ByteReader.testFlags(flags, FLAGS_IS_GENERAL_ARRAY);
	}
	
	public boolean isArray() {
		return ByteReader.testAny(flags, FLAGS_IS_GENERAL_ARRAY | FLAGS_IS_SINGLE_DIM_ARRAY);
	}
	
	public void setAsClass(boolean clazz) {
		if(clazz)	this.flags |= FLAGS_IS_CLASS;
		else  		this.flags &= ~FLAGS_IS_CLASS;
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isClass()
	 */
	public boolean isClass() {
		return ByteReader.testFlags(flags, FLAGS_IS_CLASS);
	}

	public void setAsFunctionPointer(boolean functionPointer) {
		if(functionPointer) this.flags |= FLAGS_IS_FUNCTION_POINTER;
		else  				this.flags &= ~FLAGS_IS_FUNCTION_POINTER;
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isFunctionPointer()
	 */
	public boolean isFunctionPointer() {
		return ByteReader.testFlags(flags, FLAGS_IS_FUNCTION_POINTER);
	}

	public void setAsValueType(boolean valueType) {
		if(valueType)	this.flags |= FLAGS_IS_VALUE_TYPE;
		else  			this.flags &= ~FLAGS_IS_VALUE_TYPE;
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isValueType()
	 */
	public boolean isValueType() {
		return ByteReader.testFlags(flags, FLAGS_IS_VALUE_TYPE);
	}

	public void incPointer() {
		this.flags |= FLAGS_IS_POINTER;
		
		pointer ++;
	}
	
	public int getPointers() {
		return pointer;
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isPointer()
	 */
	public boolean isPointer() {
		return ByteReader.testFlags(flags, FLAGS_IS_POINTER);
	}
	
	public void setAsGenericInstance(boolean generic) {
		if(generic) this.flags |= FLAGS_IS_GENERIC_INSTANCE;
		else  		this.flags &= ~FLAGS_IS_GENERIC_INSTANCE;
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isGeneric()
	 */
	public boolean isGeneric() {
		return ByteReader.testFlags(flags, FLAGS_IS_GENERIC_INSTANCE);
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isGenericInstance()
	 */
	public boolean isGenericInstance() {
		return genericNumber>=0;
	}
	
	public void setAsPinned(boolean pinned) {
		if(pinned) 	this.flags |= FLAGS_IS_PINNED;
		else  		this.flags &= ~FLAGS_IS_PINNED;
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isPinned()
	 */
	public boolean isPinned() {
		return ByteReader.testFlags(flags, FLAGS_IS_PINNED);
	}
	
	public void setAsBoxed(boolean boxed) {
		if(boxed) 	this.flags |= FLAGS_IS_BOXED;
		else  		this.flags &= ~FLAGS_IS_BOXED;
	}
	
	public boolean isBoxed() {
		return ByteReader.testFlags(flags, FLAGS_IS_BOXED);
	}
	
	public void evaluateBasicType() {
		isBasicType = 	enclosedType == null &&
						(optionalModifiers==null || optionalModifiers.size() == 0) &&
						(requiredModifiers==null || requiredModifiers.size() == 0 ) &&
						flags == 0 && functionPointer ==null &&
						genericNumber == -1 && arrayShape==null &&
						genericArguments == null;
	}

	public void setFunctionPointer(MethodSignature function) {
		this.functionPointer = function;
	}
	
	public MethodSignature getFunctionPointer() {
		return functionPointer;
	}

	public void setAsGenericParameter(int genericNumber, boolean belongsToMethod) {
		this.genericNumber  = genericNumber;
        this.genericBelongsToMethod = belongsToMethod;
	}
	
	public int getGenericParameterNumber() {
		return genericNumber;
	}

    @Override
    public boolean isGenericMethodParameter() {
        return genericBelongsToMethod;
    }

    public void setArrayShape(ArrayShape arrayShape) {
		this.arrayShape = arrayShape;
	}
	
	public ArrayShape getArrayShape() {
		return arrayShape;
	}

	public void setGenericArguments(TypeRefEntry[] genericTypes) {
		assert(this.genericArguments==null);
		this.genericArguments = genericTypes;
	}
	
	public TypeRefEntry[] getGenericArguments() {
		return genericArguments;
	}

	@Override
	public TypeSpecEntry getTypeSpec() {
		return this;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.TypeSpec#isBasicType()
	 */
	public boolean isBasicType() {
		return isBasicType;
	}
	
	/**
	 * Get the name of the generic arguments of the inner type
	 * and propagate it to this type spec.
	 */
	public void propagateGenericArguments() {
		if(enclosedType!=null && genericArguments!=null) {
			TypeSpecEntry typeSpec = enclosedType.getTypeSpec();
			TypeDefEntry type = enclosedType.getType();

			//Improve: Evaluate if first case really happens
			
			if(typeSpec!=null) {
				//repeat that step for type specs
				typeSpec.propagateGenericArguments();
				for(int i=0;i<this.genericArguments.length;++i) {
					if(this.genericArguments[i].getName()==null)//||this.genericArguments[i].getName().equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME))
						this.genericArguments[i] = typeSpec.genericArguments[i];
				}
			} else if(type!=null) {
				//in case the enclosed type is a type definition
				for(int i=0;i<this.genericArguments.length;++i) {
					if(this.genericArguments[i].getName()==null)//||this.genericArguments[i].getName().equals(Signature.UNRESOLVED_GENERIC_TYPE_REF_NAME))
						this.genericArguments[i].setName(type.getGenericParameters()[i].getName());
				}
			}
		}
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
	public int compareTo(TypeRef other) {
		if(other == null ||other.getTypeSpec()==null) {
			return Integer.MAX_VALUE;
		}
		
		return ArrayUtils.compareStrings(other.getFullQualifiedName(),  getFullQualifiedName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(binarySignature);
		result = prime * result + flags;
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
		
		TypeSpecEntry other = (TypeSpecEntry) obj;
		
		//the binary signature holds all extracted data in compressed form
		if (!Arrays.equals(binarySignature, other.binarySignature))
			return false;

		return flags == other.flags;

	}
}


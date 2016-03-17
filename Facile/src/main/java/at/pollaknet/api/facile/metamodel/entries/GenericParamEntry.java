package at.pollaknet.api.facile.metamodel.entries;

import java.util.ArrayList;
import java.util.List;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeDefOrRef;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeOrMethodDef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.GenericParameter;
import at.pollaknet.api.facile.symtab.symbols.MarshalSignature;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.util.ArrayUtils;

public class GenericParamEntry extends AbstractAttributable
		implements RenderableCilElement , Parameter, IHasCustomAttribute, GenericParameter {

	private int number;
	private int flags;
	private ITypeOrMethodDef owner;
	private String name;
	
	private List<ITypeDefOrRef> typeConstraints = new ArrayList<>();
	private ITypeOrMethodDef deprecatedMethodOrTypeConstraint = null;

	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getFlags() {
		return flags;
	}
	public void setFlags(int flags) {
		this.flags = flags;
	}
	public ITypeOrMethodDef getOwner() {
		return owner;
	}
	public void setOwner(ITypeOrMethodDef owner) {
		this.owner = owner;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return String.format("GenericParam: %s (Number: %d Flags: 0x%04x) Owner: %s",
				name, number, flags, owner==null?"[DELETED]":owner.getName());
	}
	
	public void addConstraint(ITypeDefOrRef constraint) {
		if(constraint.getName()==null) {
			assert(constraint.getTypeSpec()!=null);
			constraint.setName(getName());
		} else {
			TypeSpecEntry typeSpec = constraint.getTypeSpec();
			if(typeSpec!=null) {
				TypeRefEntry [] typeRef = typeSpec.getGenericArguments(); //TODO: fix getGenericArguments that it returns never null, and fix all occurrences
				if(typeRef!=null && number<typeRef.length && typeRef[number].getName()==null) {
					typeRef[number].setName(getName());
				}
			} else {
				TypeDefEntry type = constraint.getType();
				
				if(type!=null) {
					GenericParamEntry [] genericParams = type.getGenericParameters(); //never null
					if(number<genericParams.length && genericParams[number].getName()==null) {
						genericParams[number].setName(getName());
					}
				}
			}
			
		}
		
		this.typeConstraints.add(constraint);
	}

	@Override
	public TypeRef getTypeRef() {
		return null;
	}

    @Override
    public TypeRef[] getConstraints() {
        if (typeConstraints.size() == 0)
            return EMPTY;

        TypeRef[] result = new TypeRef[typeConstraints.size()];

        int i = 0;

        for (ITypeDefOrRef ref : typeConstraints) {
            result[i++] = ref.getTypeRef();
        }
        return result;
    }

    @Override
	public boolean isGeneric() {
		return true;
	}

	@Override
	public MarshalSignature getMarshalSignature() {
		return null;
	}
	
	
	@Override
	public byte[] getBinaryMarshalTypeSignature() {
		return null;
	}

	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}
	
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}
	
	public void setMethod(ITypeOrMethodDef typeOrMethodDef) {
		deprecatedMethodOrTypeConstraint = typeOrMethodDef;
	}
	

	/* (non-Javadoc)
	 * @see at.pollaknet.api.facile.metamodel.entries.GenericParameterAdapter#getDeprecatedMethodConstraint()
	 */
	public Method getDeprecatedMethodConstraint() {
		return deprecatedMethodOrTypeConstraint.getMethod();
	}
	

	/* (non-Javadoc)
	 * @see at.pollaknet.api.facile.metamodel.entries.GenericParameterAdapter#getDeprecatedTypeConstraint()
	 */
	public Type getDeprecatedTypeConstraint() {
		return deprecatedMethodOrTypeConstraint.getType();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + flags;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + number;
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
		GenericParamEntry other = (GenericParamEntry) obj;
		if (deprecatedMethodOrTypeConstraint == null) {
			if (other.deprecatedMethodOrTypeConstraint != null)
				return false;
		} else if (!deprecatedMethodOrTypeConstraint
				.equals(other.deprecatedMethodOrTypeConstraint))
			return false;
		if (flags != other.flags)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (number != other.number)
			return false;
//		if (owner == null) {
//			if (other.owner != null)
//				return false;
//		} else if (!owner.equals(other.owner))
//			return false;
        if (typeConstraints.size() != other.typeConstraints.size())
            return false;
		return typeConstraints.equals(other.typeConstraints);
	}
	
	@Override
	public int compareTo(Parameter p) {
		if(p == null || p.getClass()!=getClass()) {
			return Integer.MAX_VALUE;
		}
	
		return ArrayUtils.compareStrings(p.getName(), getName());
	}

}

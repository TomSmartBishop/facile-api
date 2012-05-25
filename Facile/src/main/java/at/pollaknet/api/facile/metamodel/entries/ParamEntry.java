package at.pollaknet.api.facile.metamodel.entries;


import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasConstant;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasFieldMarshal;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeDefOrRef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.signature.ParamOrFieldMarshalSignature;
import at.pollaknet.api.facile.symtab.symbols.Constant;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Property;
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
		} else if(!type.getFullQualifiedName().equals(other.type.getFullQualifiedName())){
			return false;
		}
		
		if (paramMarshalSignature == null) {
			if (other.paramMarshalSignature != null)
				return false;
		} else if (!paramMarshalSignature.equals(other.paramMarshalSignature))
			return false;
		
		return true;
	}
	
	

}

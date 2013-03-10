package at.pollaknet.api.facile.metamodel.entries;


import java.util.ArrayList;
import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasConstant;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasSemantics;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.signature.PropertyEntrySignature;
import at.pollaknet.api.facile.symtab.symbols.Constant;
import at.pollaknet.api.facile.symtab.symbols.Event;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Property;
import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;
import at.pollaknet.api.facile.util.ArrayUtils;


public class PropertyEntry extends AbstractAttributable implements IHasConstant,
		IHasCustomAttribute, IHasSemantics, AttributableSymbol, Property {

	private int flags;
	private String name;
	private byte [] typeSignature;
	private ConstantEntry constantEntry;
	
	private PropertyEntrySignature propertySignature;
	private ArrayList<MethodDefEntry> methods;
	
	private TypeDefEntry parent;
	
	public int getFlags() {
		return flags;
	}
	
	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public byte[] getTypeSignature() {
		return typeSignature;
	}
	
	public void setTypeSignature(byte[] typeSignature) {
		this.typeSignature = typeSignature;
	}
	
	@Override
	public void setConstant(ConstantEntry constantEntry) {
		this.constantEntry = constantEntry;
	}

	public Constant getConstant() {
		return constantEntry;
	}
	
	@Override
	public String toString() {
		return String.format("Property: %s (Flags: 0x%x) TypeSignature: %s",
				name, flags, typeSignature==null?"[not set]":ArrayUtils.formatByteArray(typeSignature));
	}
	
	@Override
	public Event getEvent() {
		return null;
	}
	
	@Override
	public Field getField() {
		return null;
	}
	
	@Override
	public Parameter getParameter() {
		return null;
	}
	
	@Override
	public Property getProperty() {
		return this;
	}
	
	public void addPropertySignature(PropertyEntrySignature propertySignature) {
		this.propertySignature = propertySignature;
	}
	
	public PropertyEntrySignature getPropertySignature() {
		return propertySignature;
	}

	public boolean addMethod(MethodDefEntry method) {
		if(methods==null) methods = new ArrayList<MethodDefEntry>(4);
		return methods.add(method);
	}

	public MethodDefEntry[] getMethods() {
		if(methods==null || methods.size()==0) {
			return new MethodDefEntry[0]; 
		}
		
		return methods.toArray(new MethodDefEntry[0]);
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
	public int compareTo(Property o) {
		return ArrayUtils.compareStrings(o.getName(), getName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + flags;
		result = prime * result + Arrays.hashCode(typeSignature);
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
		
		PropertyEntry other = (PropertyEntry) obj;
		
		if (constantEntry == null) {
			if (other.constantEntry != null)
				return false;
		} else if (!constantEntry.equals(other.constantEntry))
			return false;
		
		if (flags != other.flags)
			return false;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		//the signature contains the methods in binary coded bytes
		if (propertySignature == null) {
			if (other.propertySignature != null)
				return false;
		} else if (!propertySignature.equals(other.propertySignature))
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
	
}

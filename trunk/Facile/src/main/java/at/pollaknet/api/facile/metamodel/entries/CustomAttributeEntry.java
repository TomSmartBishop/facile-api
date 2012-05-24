package at.pollaknet.api.facile.metamodel.entries;

import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.HasBackupBlobIndex;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ICustomAttributeType;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.Instance;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;
import at.pollaknet.api.facile.symtab.symbols.meta.CustomAttribute;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;
import at.pollaknet.api.facile.symtab.symbols.scopes.Module;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.Pair;

public class CustomAttributeEntry implements RenderableCilElement, CustomAttribute, HasBackupBlobIndex {
	
	//the parent is the actual owner of the custom attribute (method, field, assembly, ...)
	private IHasCustomAttribute owner;
	
	//the type of the custom attribute (a type, which has System.Attribute as super-class)
	private ICustomAttributeType customAttributeType;
	
	//the binary signature of the custom attribute
	private byte [] value;
	
	//decoded values and types of the custom attribute
	private Instance [] fixedArguments;
	private Pair<String, Instance> [] namedFields;
	private Pair<String, Instance> [] namedProperties;

	private int blobIndex;

	//this feature is marked as depreciated in the CLI
	//private String customAttributeStringRepresentation;

	public IHasCustomAttribute getOwner() {
		return owner;
	}
	
	public void setOwner(IHasCustomAttribute parent) {
		this.owner = parent;
	}
	
	public void setCustomAttributeType(ICustomAttributeType customAttributeType) {		
		this.customAttributeType = customAttributeType;
	}
	
	public byte[] getValue() {
		return value;
	}
	
	public void setValue(byte[] value) {
		this.value = value;
	}
	
	public void setBinaryBlobIndex(int blobIndex) {
		this.blobIndex = blobIndex;
	}
	
	/* (non-Javadoc)
	 * @see at.pollaknet.api.facile.metamodel.entries.HasBackupBlobIndex#getBinaryBlobIndex()
	 */
	public int getBinaryBlobIndex() {
		return blobIndex;
	}
	
	@Override
	public String toString() {
		return String.format("CustomAttribute: %s Type: %s Value: %s",
				owner==null?"[DELETED]":owner.getName(), customAttributeType.getName(),
				value==null?"[not set]":ArrayUtils.formatByteArray(value));
	}
	
	public ICustomAttributeType getCustomAttributeType() {
		return customAttributeType;
	}

	public void setFixedArguments(Instance [] fixedArguments) {
		this.fixedArguments = fixedArguments;
	}
	
	public void setNamedFields(Pair<String, Instance> [] namedFields) {
		this.namedFields = namedFields;		
	}
	
	public void setNamedProperties(Pair<String, Instance> [] namedProperties) {
		this.namedProperties = namedProperties;
	}
	
	public Instance [] getFixedArguments() {
		if(fixedArguments==null) return new Instance[0];
		
		return fixedArguments;
	}
	
	@SuppressWarnings("unchecked")
	public Pair<String, Instance> [] getNamedFields() {
		if(namedFields==null) return new Pair[0];
		return namedFields;
	}
	
	@SuppressWarnings("unchecked")
	public Pair<String, Instance> [] getNamedProperties() {
		if(namedProperties==null) return new Pair[0];
		return namedProperties;
	}

	public String toExtendedString() {
		//IMPROVE: uniform string representation
		
		StringBuffer buffer;
		boolean hasEntries = false;
		
		assert(customAttributeType!=null);
		MethodAndFieldParent parent = null;
		
		if(owner==null) {
			buffer = new StringBuffer("// DELETED CustomAttribute: [");
		} else {
			buffer = new StringBuffer("CustomAttribute: [");

			if(owner instanceof Assembly) {
				buffer.append("assembly: ");
			} else if(owner instanceof Module) {
				buffer.append("module: ");
			}
		}
		
		if(customAttributeType.getMethod()!=null) {
			parent = customAttributeType.getMethod().getOwner();
		} else {
			assert(customAttributeType.getMemberRef()!=null);
			parent = customAttributeType.getMemberRef().getOwner();
		}

		assert(parent!=null);
		assert(parent.getTypeRef().getFullQualifiedName()!=null);
		buffer.append(parent.getTypeRef().getFullQualifiedName());
		if(fixedArguments==null && namedFields==null && namedProperties==null) {
			buffer.append("]");
		} else {
			buffer.append("(");
			
			if(fixedArguments!=null) {
				for(Instance instance: fixedArguments) {
					formatFixedAttribute(buffer, instance, hasEntries);
					hasEntries = true;
				}
			}
			
			if(namedFields!=null) {
				for(Pair<String, Instance> pair: namedFields) {
					formatNamedProperty(buffer, pair, hasEntries);
					hasEntries = true;
				}
			}
			
			if(namedProperties!=null) {
				for(Pair<String, Instance> pair: namedProperties) {
					formatNamedProperty(buffer, pair, hasEntries);
					hasEntries = true;
				}
			}
			
			buffer.append(")]");
		}
		
		return buffer.toString();
	}
	
	private static void formatFixedAttribute(StringBuffer buffer, Instance instance, boolean hasEntries) {
		if(hasEntries) buffer.append(", ");
		buffer.append(instance);
	}
	
	private static void formatNamedProperty(StringBuffer buffer, Pair<String, Instance> pair, boolean hasEntries) {
		if(hasEntries) buffer.append(", ");
		buffer.append(pair.key);
		buffer.append(" = ");
		buffer.append(pair.value);
	}
	
	@Override
	public TypeRef getTypeRef() {
		if(customAttributeType.getMethod()!=null) {
			return customAttributeType.getMethod().getOwner().getTypeRef();
		}
		
		assert(customAttributeType.getMemberRef()!=null);
		return customAttributeType.getMemberRef().getOwner().getTypeRef();
	}
	
	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}

	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}
//
//	public void setCustomAttributeTypeByString(String string) {
//		customAttributeStringRepresentation = string;
//	}
//	
//	public String getCustomAttributeTypeByString() {
//		return customAttributeStringRepresentation;
//	}

	@Override
	public int hashCode() {
		return 31 * (31 + blobIndex) + Arrays.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomAttributeEntry other = (CustomAttributeEntry) obj;
		
		//ignore the blob index
		//if (blobIndex != other.blobIndex)
		//	return false;
		
		//		if (customAttributeStringRepresentation == null) {
		//			if (other.customAttributeStringRepresentation != null)
		//				return false;
		//		} else if (!customAttributeStringRepresentation
		//				.equals(other.customAttributeStringRepresentation))
		//			return false;
		
		//compare the types
		if (customAttributeType == null) {
			if (other.customAttributeType != null)
				return false;
		} else if (!customAttributeType.equals(other.customAttributeType))
			return false;
		
		//compare the binary instance of the custom attribute
		if (!Arrays.equals(value, other.value))
			return false;
		
		return true;
	}

	@Override
	public int compareTo(CustomAttribute a) {
		return ArrayUtils.compareStrings(a.getTypeRef().getFullQualifiedName(),getTypeRef().getFullQualifiedName());
	}
	
}

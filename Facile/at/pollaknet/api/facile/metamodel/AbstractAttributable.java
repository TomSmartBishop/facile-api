package at.pollaknet.api.facile.metamodel;

import java.util.ArrayList;

import at.pollaknet.api.facile.metamodel.entries.CustomAttributeEntry;


public abstract class AbstractAttributable {

	private ArrayList <CustomAttributeEntry> customAttributes = null;
	
	public CustomAttributeEntry [] getCustomAttributes() {
		if(customAttributes==null || customAttributes.size()==0) return new CustomAttributeEntry [0];
		
		return customAttributes.toArray(new CustomAttributeEntry [0]);
	}

	public boolean addCustomAttribute(CustomAttributeEntry customAttribute) {
		if(customAttributes==null) customAttributes = new ArrayList<CustomAttributeEntry>(2); 
		return customAttributes.add(customAttribute);
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractAttributable other = (AbstractAttributable) obj;
		if (customAttributes == null) {
			if (other.customAttributes != null)
				return false;
		} else if (!customAttributes.equals(other.customAttributes))
			return false;
		return true;
	}

}
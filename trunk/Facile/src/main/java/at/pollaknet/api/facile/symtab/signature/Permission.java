package at.pollaknet.api.facile.symtab.signature;

import java.util.Arrays;

import at.pollaknet.api.facile.symtab.symbols.Instance;
import at.pollaknet.api.facile.symtab.symbols.FullQualifiableSymbol;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.Pair;

/**
 * The {@link Permission} class describes ...
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public class Permission implements FullQualifiableSymbol {

	private String fullQualifiedName;
	private Pair<String, Instance> properties [];
	
	/**
	 * Create a new security permission with a given set (array) of security properties.
	 * @param fullQualifiedName The full qualified name of the security permission.
	 * @param properties The securty permissions as array of
	 * {@link at.pollaknet.api.facile.util.Pair}{@code <String, Instance>}.
	 */
	Permission(String fullQualifiedName, Pair<String, Instance>[] properties) {
		this.fullQualifiedName = fullQualifiedName;
		this.properties = properties;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.QualifiableSymbol#getFullQualifiedName()
	 */
	@Override
	public String getFullQualifiedName() {
		return fullQualifiedName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.QualifiableSymbol#getName()
	 */
	@Override
	public String getName() {
		assert(fullQualifiedName!=null);
		int index = fullQualifiedName.lastIndexOf(".");
		
		if(index>0) {
			return fullQualifiedName.substring(index+1);
		}
		return fullQualifiedName;
	}

	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.QualifiableSymbol#getNamespace()
	 */
	@Override
	public String getNamespace() {
		assert(fullQualifiedName!=null);
		int index = fullQualifiedName.lastIndexOf(".");
		
		if(index>0) {
			return fullQualifiedName.substring(0,index-1);
		}
		return null;
	}
	
	/**
	 * Returns all set security properties or an empty array.
	 * @return The security properties as named instance (
	 * {@link at.pollaknet.api.facile.util.Pair}{@code <String, Instance>}).
	 */
	@SuppressWarnings("unchecked")
	public Pair<String, Instance> [] getProperties() {
		if(properties == null) return new Pair[0];
		return properties;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.pollaknet.api.facile.symtab.symbols.QualifiableSymbol#getShortSystemName()
	 */
	@Override
	public String getShortSystemName() {
		return null;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(fullQualifiedName);
		buffer.append("(");
		boolean hasEntries = false;
		
		for(Pair <String, Instance> pair: properties) {			
			Instance instance = pair.value;
			if(hasEntries) buffer.append(", ");
			
			buffer.append(pair.key);
			buffer.append(" = ");
			buffer.append(pair.value);
			
			hasEntries = true;
		}
		buffer.append(")");

		return buffer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + ((fullQualifiedName == null) ? 0 : fullQualifiedName.hashCode());
		return prime * result + Arrays.hashCode(properties);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permission other = (Permission) obj;
		if (fullQualifiedName == null) {
			if (other.fullQualifiedName != null)
				return false;
		} else if (!fullQualifiedName.equals(other.fullQualifiedName))
			return false;
		
		//check the array of pairs
		if(properties==null) {
			if(other.properties==null) return true;
			return false;
		} else if(other.properties==null) {
			return false;
		}
		
		if(properties.length!=other.properties.length) return false;
		
		for(int i=0;i<properties.length;i++) {
			if(!ArrayUtils.stringsAreEqualN(properties[i].key, other.properties[i].key))
				return false;
			if(properties[i].value==null) {
				if(other.properties[i].value!=null) return false;
			} else if(!properties[i].value.equals(other.properties[i].value)) {
				return false;
			}
		}

		return true;
	}

}

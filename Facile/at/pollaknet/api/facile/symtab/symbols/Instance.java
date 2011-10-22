package at.pollaknet.api.facile.symtab.symbols;

/**
 * Represents an instance of a type within a .Net assembly. All custom
 * attributes are instances of a class derived from CustomAttribute.
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public interface Instance {

	/**
	 * Returns the type of the instance.
	 * @return The type reference of the type instance, which is always available.
	 */
	public abstract TypeRef getTypeRef();

	/**
	 * Returns the numeric representation of the instance if the type is a numeric one.
	 * @return The value of the numeric instance (default is 0).
	 */
	public abstract long getValue();

	/**
	 * Returns the string representation of the instance if set.
	 * @return The value of the instance as {@code String} or {@code null}.
	 */
	public abstract String getStringValue();
	
	/**
	 * Returns the boxed instance of this instance if present.
	 * @return The boxed type instance or {@code null}.
	 */
	public abstract Instance getBoxedInstance();
	
	/**
	 * Return weather the instance is an array or not.
	 * @return {@code true} if the instance is an array, otherwise {@code false}.
	 */
	public abstract boolean isArray();
	
	/**
	 * Returns an array of instances if the instance is an array.
	 * @return The array of instances or {@code null}.
	 */
	public abstract Instance [] getArrayInstance();
	

	/**
	 * Check if this type of this instance could be a value type.
	 * A real proof can only be done by loading the assembly of the reference!
	 * @return {@code true} if the type of the instance looks lika a value type, otherwise {@code false}.
	 */
	public abstract boolean isPotentialValueType();

	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

}
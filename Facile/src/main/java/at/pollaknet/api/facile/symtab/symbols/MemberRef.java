package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;

/**
 * A reference to a member of a class.
 */
public interface MemberRef {

	/**
	 * The name of of the referenced member.
	 * @return The name as {@code String}.
	 */
	public abstract String getName();
	
	/**
	 * The binary signature describing the member reference.
	 * @return The raw {@code byte} buffer, holding all information about
	 * the member reference.
	 */
	public abstract byte[] getBinarySignature();
	
	/**
	 * Get the generic type instances, which are probably part of the member.
	 * @return An array of {@link at.pollaknet.api.facile.symtab.symbols.TypeSpec}
	 * instances (empty if there are no generics).
	 */
	public abstract TypeSpec[] getGenericInstances();
	
	/**
	 * Get the type of the referenced member.
	 * @return The specified {@link at.pollaknet.api.facile.symtab.symbols.TypeRef}.
	 */
	public abstract TypeRef getTypeRef();
	
	/**
	 * Returns the method signature if the referenced member is method.
	 * @return A {@link at.pollaknet.api.facile.symtab.symbols.MethodSignature} instance
	 * or {@code null}.
	 */
	public abstract MethodSignature getMethodRefSignature();

	/**
	 * The owner of the reference (Type, TypeRef, TypeSpec or Module).
	 * @return The owner as
     * {@link at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent} instance.
	 */
	public abstract MethodAndFieldParent getOwner();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	int compareTo(MemberRef o);

}
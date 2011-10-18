package at.pollaknet.api.facile.symtab.symbols.meta;

import at.pollaknet.api.facile.symtab.signature.Permission;

public interface DeclarativeSecurity {
	
	public final static int ACTION_REQUEST 						= 0x0001;
	public final static int ACTION_DEMAND 						= 0x0002;
	public final static int ACTION_ASSERT 						= 0x0003;
	public final static int ACTION_DENY 						= 0x0004;
	public final static int ACTION_PERMIT_ONLY 					= 0x0005;
	public final static int ACTION_LINK_DEMAND 					= 0x0006;
	public final static int ACTION_INHERITANCE_DEMAND 			= 0x0007;
	public final static int ACTION_REQUEST_MINIMUM 				= 0x0008;
	public final static int ACTION_REQUEST_OPTIONAL 			= 0x0009;
	public final static int ACTION_REQUEST_REFUSE	 			= 0x000A;
	public final static int ACTION_PRE_JIT_GRANT 				= 0x000B;
	public final static int ACTION_PRE_JIT_DENY	 				= 0x000C;
	public final static int ACTION_NON_CAS_DEMAND 				= 0x000D;
	public final static int ACTION_NON_CAS_LINK_DEMAND			= 0x000E;
	public final static int ACTION_NON_CAS_INHERITANCE_DEMAND	= 0x000F;
	
	/**
	 * Returns the action type as {@code int}.
	 * @return The action code.
	 */
	public abstract int getAction();
	
	/**
	 * Returns the XML representation of the security permissions, which
	 * are only present in old assemblies (1.0 or 1.1).
	 * @return The XML representation of the security permissions or {@code null}.
	 */
	public abstract String getXMLPermissionSet();
	
	/**
	 * Returns the defined permission set.
	 * @return The permission set as array of
	 * {@link at.pollaknet.api.facile.symtab.signature.Permission} objects.
	 */
	public abstract Permission [] getPermissions();
	
	/**
	 * Returns the name of the security rule.
	 * @return The name as {@code string}.
	 */
	public abstract String getName();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	int compareTo(DeclarativeSecurity o);
}

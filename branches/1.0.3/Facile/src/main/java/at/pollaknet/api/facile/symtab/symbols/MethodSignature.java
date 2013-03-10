package at.pollaknet.api.facile.symtab.symbols;


public interface MethodSignature {
	
	public final static int CALL_BITMASK				= 0x60;
	
	public final static int CALL_HAS_THIS				= 0x20;
	public final static int CALL_EXPLICIT_THIS 			= 0x40;

	
	public final static int CALL_CONV_BITMASK			= 0x17;
	
	public final static int CALL_CONV_DEFAULT 			= 0x00;
	public final static int CALL_CONV_C					= 0x01;
	public final static int CALL_CONV_STD		 		= 0x02;
	public final static int CALL_CONV_THIS	 			= 0x03;
	public final static int CALL_CONV_FAST	 			= 0x04;
	public final static int CALL_CONV_VARARG 			= 0x05;
	public final static int CALL_CONV_GENERIC 			= 0x10;

	
	/**
	 * Returns the flags for the call an the calling convention
	 * of the signature.
	 * @return The flags of the method as {@code byte}.
	 */
	public abstract byte getFlags();
	
	/**
	 * Returns an array of all method parameters which is never
	 * {@code null}, but probably has a length of 0. If the length
	 * is greater 0, then the first element is the return parameter.
	 * @return An array of {@link at.pollaknet.api.facile.symtab.symbols.Parameter}.
	 */
	public abstract Parameter[] getParameters();
	
	/**
	 * This is a shortcut and performs exactly the same as 
	 * {@code getReturnParameter().getTypeRef()}.
	 * @return The type reference of the return type or
	 * {@code null} (see {@code getReturnParameter()}).
	 */
	public abstract TypeRef getReturnType();
	
	/**
	 * Returns the return parameter of the method which could result
	 * in a {@code null} value (which means {@code void}) if not
	 * specified by the assembly. The return parameter is always the
	 * first element in the paremeter array returned by {@code getParameters()}
	 * @return The return parameter, or {@code null}.
	 */
	public abstract Parameter getReturnParameter();
	
	/**
	 * <p/>Returns the number of parameters for this signature
	 * (which is specified in the binary signature - the return
	 * type is probably not counted).
	 * <p/>Please use the {@code getParameters().length} if you would like
	 * to know the real number of parameters!
	 * @return The (stored) number of parameters.
	 */
	public abstract int getParameterCount();
	

	/**
	 * Returns the number of generic parameters in the signature.
	 * @return The number of generic parameters.
	 */
	public abstract int getGenericParameterCount();
	
	/**
	 * Returns the position of the method's sentinel or -1 if there
	 * is no sentinel set. The sentinel is a pseudo argument and
	 * separates the required from the optional arguments.
	 * @return The position of the sentinel within the signature.
	 */
	public abstract int getSentinelPosition();

	/**
	 * Get the binary representation of the method signature
	 * @return The raw signature as {@code byte []}. 
	 */
	public abstract byte [] getBinarySignature();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();
}
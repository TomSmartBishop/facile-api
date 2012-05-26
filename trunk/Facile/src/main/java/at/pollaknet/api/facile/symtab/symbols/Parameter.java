package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeOrMethodDef;
import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;

public interface Parameter extends AttributableSymbol {

    public static final TypeRef[] EMPTY = new TypeRef[0];

    public final static int FLAGS_IN = 0x0001;
	public final static int FLAGS_OUT = 0x0002;
	public final static int FLAGS_IS_OPTIONAL = 0x0010;
	public final static int FLAGS_HAS_DEFAULT_VALUE = 0x1000;
	public final static int FLAGS_HAS_MARSHAL = 0x2000;
	public final static int FLAGS_UNUSED = 0xcfe0;
	                    
	public final static int MAX_PARAMETER = 0x1fffffff;

	/**
	 * Returns the set flags for this parameter (which specify
	 * [opt], [out] and all that stuff).
	 * @return The flags of the parameter as {@code int}.
	 */
	public abstract int getFlags();

	/**
	 * Returns the parameters number, which specifies the
	 * position within the method signature (unfortunately
	 * there is no rule to start with 1 or 0). 
	 * @return The paramerts's number (even known as sequence).
	 */
	public abstract int getNumber();

	/**
	 * Returns the name of the parameter.
	 * @return The parameter's name.
	 */
	public abstract String getName();

    /**
     * Returns the owner of this generic parameter
     * @return The owner or null, if isGeneric() returns false
     */
    public ITypeOrMethodDef getOwner();

	/**
	 * Returns the marshal signature if the parameter is a marshaled native type.
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.MarshalSignature}
	 * of the parameter. 
	 */
	public abstract MarshalSignature getMarshalSignature();
	
	/**
	 * Returns the type reference of the parameter. In case of a
	 * generic parameter ({@code isGeneric()==true}) this method
	 * returns null!
	 * @return The {@link at.pollaknet.api.facile.symtab.symbols.TypeRef}
	 * (or constraint) of the parameter.
	 */
	public abstract TypeRef getTypeRef();

    /**
     * Returns the type constraints for a generic parameter
     *
     * @return A potentially empty array of constraints
     */
    public abstract TypeRef[] getConstraints();

	/**
	 * <p/>Tells you if the parameter is generic or not.
	 * <p/>If it is a generic Parameter you can cast this object to 
	 * {@link at.pollaknet.api.facile.symtab.symbols.GenericParameter},
	 * which allows the access to the out dated method-or-type constraints
	 * of the v1.1 Metadata stream of pre 2.0 Assemblies *). In all other
	 * stream versions you can get the constraint via {@code getTypeRef()}.
	 * <p>*) Only do this if you really really need it - in a test run
	 * containing more than 7000 assemblies there was only one (very old)
	 * assembly containing such an entry and this entry was {@code null}!
	 * 
	 * @return {@code true} if the parameter is a generic type,
	 * otherwise {@code false}.
	 */
	public abstract boolean isGeneric();


	//public abstract Method getDeprecatedMethodConstraint();

	//public abstract Type getDeprecatedTypeConstraint();

	/**
	 * Get the raw representation of the marshaled parameter.
	 * @return The binary marshal signature as {@code byte []}
	 * ({@code null} if nothing has been marshaled).
	 */
	public abstract byte [] getBinaryMarshalTypeSignature();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

	int compareTo(Parameter p);
}
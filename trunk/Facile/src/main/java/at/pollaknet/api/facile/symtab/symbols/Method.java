package at.pollaknet.api.facile.symtab.symbols;

import at.pollaknet.api.facile.code.MethodBody;
import at.pollaknet.api.facile.pdb.DebugInformation;
import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;
import at.pollaknet.api.facile.symtab.symbols.meta.AttributableSymbol;
import at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity;

public interface Method extends AttributableSymbol, Comparable<Method> {

	public static final int FLAGS_VISIBILITY_BIT_MASK = 0x07;
	public static final int FLAGS_VTABLE_BIT_MASK = 0x0100;
	
	public static final int FLAGS_VISIBILITY_COMPILER_CONTROLLED = 0x00;
	public static final int FLAGS_VISIBILITY_PRIVATE = 0x01;
	public static final int FLAGS_VISIBILITY_FAMILY_AND_ASSEMBLY = 0x02;
	public static final int FLAGS_VISIBILITY_ASSEMBLY = 0x03;
	public static final int FLAGS_VISIBILITY_FAMILIY = 0x04;
	public static final int FLAGS_VISIBILITY_FAMILY_OR_ASSEMBLY = 0x05;
	public static final int FLAGS_VISIBILITY_PUBLIC = 0x06;
	public static final int FLAGS_STATIC = 0x0010;
	public static final int FLAGS_FINAL = 0x0020;
	public static final int FLAGS_VIRTUAL = 0x0040;
	public static final int FLAGS_HIDE_BY_SIG = 0x0080;
	
	public static final int FLAGS_VTABLE_REUSE_SLOT = 0x0000;
	public static final int FLAGS_VTABLE_NEW_SLOT = 0x0100;
	
	public static final int FLAGS_STRICT = 0x0200;
	public static final int FLAGS_ABSTRACT = 0x0400;
	public static final int FLAGS_SPECIAL_NAME = 0x0800;
	
	public static final int FLAGS_INTEROP_PINVOKE = 0x2000;
	public static final int FLAGS_INTEROP_UNMANAGED_EXPORT = 0x0008;

	public static final int FLAGS_ADDITIONAL_RT_SPECIAL_NAME = 0x1000;
	public static final int FLAGS_ADDITIONAL_HAS_SECURITY = 0x4000;
	public static final int FLAGS_ADDITIONAL_REQUIRE_SECURITY_OBJECT = 0x8000;

	public static final int IMPL_FLAGS_CODE_TYPE_BIT_MASK = 0x03;
	public static final int IMPL_FLAGS_ORGANISATION_BIT_MASK = 0x04;
	
	public static final int IMPL_FLAGS_CODE_TYPE_IL = 0x00;
	public static final int IMPL_FLAGS_CODE_TYPE_NATIVE = 0x01;
	public static final int IMPL_FLAGS_CODE_TYPE_OPTIL = 0x02;
	public static final int IMPL_FLAGS_CODE_TYPE_RUNTIME = 0x03;
	
	public static final int IMPL_FLAGS_ORGANISATION_UNMANAGED = 0x04;
	public static final int IMPL_FLAGS_ORGANISATION_MANAGED = 0x00;
	
	public static final int IMPL_FLAGS_FORWARD_REF = 0x0010;
	public static final int IMPL_FLAGS_PRESERVE_SIG = 0x0080;
	public static final int IMPL_FLAGS_INTERNAL_CALL = 0x1000;
	public static final int IMPL_FLAGS_SYNCHRONIZED = 0x0020;
	public static final int IMPL_FLAGS_NO_INLINING = 0x0008;
	
	public static final int SEMANTICS_FLAGS_PROP_IS_SETTER = 0x0001;
	public static final int SEMANTICS_FLAGS_PROP_IS_GETTER = 0x0002;
	public static final int SEMANTICS_FLAGS_IS_OTHER = 0x0004;
	public static final int SEMANTICS_FLAGS_EVENT_ADD_ON = 0x0008;
	public static final int SEMANTICS_FLAGS_EVENT_REMOVE_ON = 0x0010;
	public static final int SEMANTICS_FLAGS_EVENT_FIRE = 0x0020;
	
	/**
	 * <p/>Returns the owner of the method.
	 * <p/>This is only a backup, because the owner should be known
	 * by receiving the method threw the owner!
	 * @return The owner of the method as interface
	 * {@link at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent}.
	 */
	public abstract MethodAndFieldParent getOwner();

	/**
	 * Returns the name of the method.
	 * @return The name as {@code String}.
	 */
	public abstract String getName();
	
	/**
	 * Returns the assigned generic instances for this method or
	 * an empty array if there are no generic instances.
	 * @return An array of {@link at.pollaknet.api.facile.symtab.symbols.TypeSpec}
	 * objects, each containing the type specifications for the generic method instance.
	 */
	public abstract TypeSpec[] getGenericInstances();
	
	/**
	 * Returns the signature of the method.
	 * @return The signature as
	 * {@link at.pollaknet.api.facile.symtab.symbols.MethodSignature} object.
	 */
	public abstract MethodSignature getMethodSignature();

    /**
     * Returns the list fo generic parameters for this method
     * @return A potentially empty array with generic parameters
     */
    public abstract Parameter[] getGenericParameters();

	/**
	 * Return the method implementation flags (e.g. managed, unmanaged, ...).
	 * @return The method implementation flags as {@code int}.
	 */
	public abstract int getImplFlags();

	/**
	 * Return the flags of the method (e.g. visibility).
	 * @return The method flags as {@code int}.
	 */
	public abstract int getFlags();
	
	/**
	 * Return the semantics flags (event or property flags).
	 * @return The semantics flags as {@code int}.
	 */
	public abstract int getSemanticsFlags();

	/**
	 * Returns the declarative security permissions assigned to
	 * this method.
	 * @return A {@link at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity}
	 * object describing the security settings.
	 */
	public abstract DeclarativeSecurity getDeclarativeSecurity();
	
	/**
	 * Checks if the method is part of a property (has semantics).
	 * @return {@code true} if this method is part of a property, otherwise {@code null}.
	 */
	public abstract boolean isPropertyMethod();
	
	/**
	 * Checks if the method is part of an event (has semantics).
	 * @return {@code true} if this method is part of an event, otherwise {@code null}.
	 */
	public abstract boolean isEventMethod();
	
	/**
	 * Checks weather the method is used as event or property -
	 * in other words the method has semantics.
	 * @return {@code true} if the method has semantics, otherwise {@code null}.
	 */
	public abstract boolean hasSemantics();
	
	/**
	 * Returns information related to the property mechanism if
	 * this method is an implementation of an property.
	 * @return An {@link at.pollaknet.api.facile.symtab.symbols.Property}
	 * object holding the property mechanism related data or {@code null}.
	 */
	public abstract Property getPropertyInformation();
	
	/**
	 * Returns information related to the event mechanism if
	 * this method is an implementation of an event.
	 * @return An {@link at.pollaknet.api.facile.symtab.symbols.Event}
	 * object holding the event mechanism related data or {@code null}.
	 */
	public abstract Event getEventInformation();
	
	/**
	 * Return the body of the method, containing the CIL instructions.
	 * @return The method body as {@link at.pollaknet.api.facile.code.MethodBody} object.
	 */
	public abstract MethodBody getMethodBody();
	
	/**
	 * Returns a container with further information about the native
	 * implementation of this method or {@code null} if not present.
	 * @return An object describing the native method implementation.
	 */
	public abstract NativeImplementation getNativeImplementation();
	
	/**
	 * Returns the debug information of the method, holding
	 * the line numbers for the instructions of the method body.
	 * @return An instance of {@link at.pollaknet.api.facile.pdb.DebugInformation}.
	 */
	public abstract DebugInformation getDebungInformation();
	
	//draft method
	//public abstract String toExtendedString();
	
	public abstract boolean equals(Object obj);
	
	public abstract int hashCode();

}


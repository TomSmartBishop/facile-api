package at.pollaknet.api.facile.metamodel.entries;

import java.util.ArrayList;
import java.util.Arrays;

import at.pollaknet.api.facile.code.MethodBody;
import at.pollaknet.api.facile.metamodel.AbstractGenericInstanceConatiner;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ICustomAttributeType;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasDeclSecurity;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasSemantics;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IMemberForwarded;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IMethodDefOrRef;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeOrMethodDef;
import at.pollaknet.api.facile.pdb.DebugInformation;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.signature.Permission;
import at.pollaknet.api.facile.symtab.symbols.Event;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.MethodSignature;
import at.pollaknet.api.facile.symtab.symbols.NativeImplementation;
import at.pollaknet.api.facile.symtab.symbols.Property;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;
import at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity;
import at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef;
import at.pollaknet.api.facile.util.ArrayUtils;


public class MethodDefEntry extends AbstractGenericInstanceConatiner
		implements IHasCustomAttribute, IHasDeclSecurity, ITypeOrMethodDef,
			MethodAndFieldParent, IMethodDefOrRef, IMemberForwarded, ICustomAttributeType, Method {

    private final static GenericParamEntry[] EMPTY = new GenericParamEntry[0];

	private long relativeVirtualAddress = -1;
	private int implFlags;
	private int flags;
	private String name;
	private byte [] binarySignature;
	private ParamEntry params [];
	private DeclSecurityEntry declSecurityEntry;
	
	private Event event;
	private Property property;
	private MethodBody methodBody;
	private NativeImplementation nativeImpl;
	
	private ArrayList <GenericParamEntry> genericParams = null;

	//private boolean methodOverrides;
	//private MethodDefEntry overriddenMethod;
	private DebugInformation debugInformation;
	private MethodSignature methodDefSignature;
	private MethodAndFieldParent owner;
	private int semanticsFlags;
	
	public long getVirtualAddress() {
		return relativeVirtualAddress;
	}
	public void setRelativeVirtualAddress(long virtualAddress) {
		this.relativeVirtualAddress = virtualAddress;
	}
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Method#getImplFlags()
	 */
	public int getImplFlags() {
		return implFlags;
	}
	public void setImplFlags(int implFlags) {
		this.implFlags = implFlags;
	}
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Method#getName()
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Method#getBinarySignature()
	 */
	public byte [] getBinarySignature() {
		return binarySignature;
	}
	public void setSignature(byte [] signature) {
		this.binarySignature = signature;
	}
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Method#getParams()
	 */
	public ParamEntry[] getParams() {
		return params;
	}
	public void setParams(ParamEntry[] params) {
		this.params = params;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Method#getFlags()
	 */
	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	@Override
	public void setDeclarativeSecurity(DeclSecurityEntry declSecurityEntry) {
		this.declSecurityEntry = declSecurityEntry;
	}
	
	@Override
	public DeclarativeSecurity getDeclarativeSecurity() {
		return declSecurityEntry;
	}
	
	@Override
	public String toString() {	
		return String.format("MethodDef: %s (RVA: 0x%x Flags: 0x%04x ImplFlags 0x%04x) NumberOfParams: %d Signature: %s MethodBody: %s DebugInfo: %s",
				name, relativeVirtualAddress, flags, implFlags,
				params==null?0:params.length,
				binarySignature==null?"[not set]":ArrayUtils.formatByteArray(binarySignature),
				(methodBody==null)?"[not set]":("(" + methodBody.getCodeSize()+" bytes)"),
				debugInformation==null?"[not set]":debugInformation.toString());
	}
	
	public String toExtendedString() {
		StringBuffer buffer = new StringBuffer(128);
		
		if( getDeclarativeSecurity()!=null ) {
			//IMPROVE: uniform string representation
			if(getDeclarativeSecurity().getAction()!=0) {
				buffer.append("Declarative Security Permission: [SecurityPermission(SecurityAction Code ");
				buffer.append(getDeclarativeSecurity().getAction());
				buffer.append(")]\n");
			}
			for(Permission p: getDeclarativeSecurity().getPermissions()) {
				buffer.append("Declarative Security Permission: [");
				buffer.append(p.toString());
				buffer.append("]\n");
			}
			if(getDeclarativeSecurity().getXMLPermissionSet()!=null) {
				buffer.append("Declarative Security Permission (XML): ");
				buffer.append(getDeclarativeSecurity().getXMLPermissionSet().replaceAll("\n", ""));
				buffer.append("\n");
			}
		}
		
		if(getCustomAttributes()!=null) {
			for(CustomAttributeEntry c: getCustomAttributes()) {
				buffer.append(c.toExtendedString());
				buffer.append("\n");
			}
		}
		
		buffer.append("MethodDef: ");
		buffer.append(name);

		if(methodDefSignature==null) {
			buffer.append("()");
		} else {
			buffer.append(methodDefSignature.toString());
		}
		buffer.append(String.format(" (Flags: 0x%04x ImplFlags 0x%04x)", flags, implFlags));
		buffer.append(" MethodBody: ");
		if(methodBody==null) {
			buffer.append("[not set]");
		} else {
			buffer.append("(");
			buffer.append(methodBody.getCodeSize());
			buffer.append(" bytes, locals: ");
			buffer.append(methodBody.getLocalVars()==null?0:methodBody.getLocalVars().length);
			buffer.append(")");
		}
		if(debugInformation==null) {
			buffer.append(" DebugInfo: [not set]");
		} else {
			buffer.append("\n\tDebugInfo: ");
			buffer.append(debugInformation.toString());
		}
		
		return buffer.toString();
	}
	
	@Override
	public Event getEventInformation() {
		return event;
	}
	@Override
	public Property getPropertyInformation() {
		return property;
	}
	@Override
	public boolean hasSemantics() {
		return event!=null || property!=null;
	}
	@Override
	public boolean isEventMethod() {
		return event!=null;
	}
	@Override
	public boolean isPropertyMethod() {
		return property!=null;
	}
	
	public void setSemantics(IHasSemantics semantics) {
		event = semantics.getEvent();
		property = semantics.getProperty();
		
		semantics.addMethod(this);
	}
	
	public void setMethodBody(MethodBody methodBody) {
		this.methodBody = methodBody;
	}
	
	public MethodBody getMethodBody() {
		return methodBody;
	}
	
	@Override
	public NativeImplementation getNativeImplementation() {
		return nativeImpl;
	}
	
	public void setNativeImplementation(NativeImplementation nativeImpl) {
		this.nativeImpl = nativeImpl;
	}
	
	public GenericParamEntry [] getGenericParameters() {
		if(genericParams==null || genericParams.size()==0) return EMPTY;
		
		GenericParamEntry [] params = new GenericParamEntry[genericParams.size()];
		
		genericParams.toArray(params);
		
		return params;
	}

	public boolean addGenericParam(GenericParamEntry p) {
		if(genericParams==null) genericParams = new ArrayList<GenericParamEntry>(4);
		return genericParams.add(p);
	}
	/*
	public boolean methodOverrides() {
		return methodOverrides;
	}
	
	public void setOverride(boolean methodOverrides) {
		this.methodOverrides = methodOverrides;
	}
	
	public Method getOverriddenMethod() {
		return overriddenMethod;
	}
	
	public void setOverriddenMethod(MethodDefEntry overriddenMethod) {
		this.overriddenMethod = overriddenMethod;
	}
	*/
	
	public void setDebungInformation(DebugInformation lineNumberInfo) {
		this.debugInformation = lineNumberInfo;
	}
	
	@Override
	public DebugInformation getDebungInformation() {
		return debugInformation;
	}
	
	public void setMethodSignature(MethodSignature methodDefSignature) {
		this.methodDefSignature = methodDefSignature;
	}
	@Override
	public MethodSignature getMethodSignature() {
		return methodDefSignature;
	}
	
	public void setNumberOfParameters(int parameterCount) {
		if(params!=null && params.length>parameterCount && parameterCount>=0) {
			ParamEntry [] truncatedParams = new ParamEntry[parameterCount];
			
			for(int index=0;index<parameterCount;index++) {
				truncatedParams[index] = params[index];
			}
			
			params = truncatedParams;
		}
	}
	
	@Override
	public MethodDefEntry getMethod() {
		return this;
	}
	
	@Override
	public Type getType() {
		return null;
	}
	@Override
	public MemberRefEntry getMemberRef() {
		return null;
	}
	@Override
	public ModuleRef getModuleRef() {
		return null;
	}
	@Override
	public TypeRef getTypeRef() {
		return null;
	}
	@Override
	public Field getField() {
		return null;
	}
	@Override
	public MethodAndFieldParent getOwner() {
		return owner;
	}
	/*
	public void setNumberOfGenericParameters(int parameterCount) {
		Parameter [] truncatedParams = getGenericParameters();
		genericParams.clear();
		
		for(int index=0;index<parameterCount;index++)
			genericParams.add(truncatedParams[index]);
	}*/
	
	@Override
	public TypeSpec getTypeSpec() {
		return null;
	}
	public void setOwner(MethodAndFieldParent owner) {
		this.owner = owner;
	}
	public void setSemanticsFlags(int semantics) {
		this.semanticsFlags = semantics;
	}
	@Override
	public int getSemanticsFlags() {
		return semanticsFlags;
	}
	
	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}
	
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return renderer.renderAsReference(this);
	}
	@Override
	public int compareTo(Method o) {
		return ArrayUtils.compareStrings(o.getName(), getName());
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + flags;
		result = prime * result + implFlags;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(params);
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
		MethodDefEntry other = (MethodDefEntry) obj;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		
		if (flags != other.flags)
			return false;
		if (implFlags != other.implFlags)
			return false;
		if (semanticsFlags != other.semanticsFlags)
			return false;
		
		if (methodDefSignature == null) {
			if (other.methodDefSignature != null)
				return false;
		} else if (!methodDefSignature.equals(other.methodDefSignature))
			return false;
		
		if (methodBody == null) {
			if (other.methodBody != null)
				return false;
		} else if (!methodBody.equals(other.methodBody))
			return false;
		
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		
		if (declSecurityEntry == null) {
			if (other.declSecurityEntry != null)
				return false;
		} else if (!declSecurityEntry.equals(other.declSecurityEntry))
			return false;
		
		//array list
		if (genericParams == null) {
			if (other.genericParams != null)
				return false;
		} else if (!genericParams.equals(other.genericParams)) {
			return false;
		}
		
		if (nativeImpl == null) {
			if (other.nativeImpl != null)
				return false;
		} else if (!nativeImpl.equals(other.nativeImpl))
			return false;
		if (!Arrays.equals(params, other.params))
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		
		return true;
	}
	
	
	
}

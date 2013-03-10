package at.pollaknet.api.facile.metamodel.entries;

import java.util.ArrayList;
import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasDeclSecurity;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeDefOrRef;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeOrMethodDef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.signature.Permission;
import at.pollaknet.api.facile.symtab.symbols.ClassLayout;
import at.pollaknet.api.facile.symtab.symbols.Event;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Property;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.aggregation.MethodAndFieldParent;
import at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.ByteReader;


public class TypeDefEntry extends TypeRefEntry implements ITypeDefOrRef, IHasCustomAttribute,
		IHasDeclSecurity, MethodAndFieldParent, ITypeOrMethodDef, Type {
	
	private byte internalFlags;
	private final static byte INT_FLAGS_SORTED_FIELDS 		= 0x01;
	private final static byte INT_FLAGS_SORTED_METHODS		= 0x02; 
	private final static byte INT_FLAGS_SORTED_PROPERTIES 	= 0x04; 
	private final static byte INT_FLAGS_SORTED_EVENTS		= 0x08;
	private final static byte INT_FLAGS_IS_EXPORTED			= 0x10; 
	private final static byte INT_FLAGS_IS_NESTED			= 0x20; 
	
	private long flags;
	private ITypeDefOrRef extendz;
	private FieldEntry fields[];
	private MethodDefEntry methods [];
	private ClassLayoutEntry classLayout;
	private ConstantEntry[] constantEntries;
	private DeclSecurityEntry declSecurityEntry;
	private Property [] properties;
	private Event[] events;

	private ArrayList <TypeRef> implementedInterfaces = null;
	private ArrayList <Type> enclosingClasses = null;
	private ArrayList <GenericParamEntry> genericParams = null;
	private ArrayList <MethodDefEntry> additionalMethods = null;

	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Type#isClass()
	 */
	public boolean isClass() {
		return ByteReader.testFlags(flags, FLAGS_SEMANTICS_IS_A_CLASS);
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Type#isInterface()
	 */
	public boolean isInterface() {
		return ByteReader.testFlags(flags, FLAGS_SEMANTICS_IS_AN_INTERFACE);
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Type#getFlags()
	 */
	public long getFlags() {
		return flags;
	}

	public void setFlags(long flags) {
		this.flags = flags;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Type#getExtends()
	 */
	public TypeRef getExtends() {
		if(extendz==null) return null;
		return extendz.getTypeRef();
	}

	public void setExtends(ITypeDefOrRef extendz) {
		this.extendz = extendz;
	}

	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Type#getFields()
	 */
	public FieldEntry[] getFields() {
		if(fields==null) return new FieldEntry[0];
		
		if(!ByteReader.testFlags(internalFlags, INT_FLAGS_SORTED_FIELDS)) {
			ByteReader.setFlags(internalFlags, INT_FLAGS_SORTED_FIELDS, true);
			Arrays.sort(fields);
		}
		return fields;
	}

	public void setFields(FieldEntry[] fields) {
		if(fields!=null) {
			for(FieldEntry field: fields) {
				field.setParent(this);
			}
				
			this.fields = fields;	
		}
	}

	public boolean addMethod(MethodDefEntry method) {
		if(additionalMethods==null) additionalMethods = new ArrayList<MethodDefEntry>(4);
		return additionalMethods.add(method);
	}

	public void setMethods(MethodDefEntry[] methods) {
		this.methods = methods;
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Type#getMethods()
	 */
	public MethodDefEntry[] getMethods() {
		if(methods==null)
			return new MethodDefEntry[0];
		
		if(!ByteReader.testFlags(internalFlags, INT_FLAGS_SORTED_METHODS)) {
			ByteReader.setFlags(internalFlags, INT_FLAGS_SORTED_METHODS, true);
			Arrays.sort(methods);
		}

		return methods;
	}
	
	/* (non-Javadoc)
	 * @see facile.metamodel.entries.Type#toString()
	 */
	@Override
	public String toString() {
		return String.format("TypeDef: %s%s (NumberOfFields: %d NumberOfMethods: %d; Flags: 0x%08x)",
				getFullQualifiedName(), (extendz==null)?"":(" extends " + extendz.getFullQualifiedName()),
				fields==null?0:fields.length, methods==null?0:methods.length, flags);
	}

	@Override
	public ClassLayout getClassLayout() {
		return classLayout;
	}

	@Override
	public ConstantEntry[] getConstants() {
		if(constantEntries==null) return new ConstantEntry[0];
		
		//do not sort constants (makes no sense at all)
		return constantEntries;
	}

	public void setConstantEntries(ConstantEntry[] constantEntries) {
		this.constantEntries = constantEntries;
	}

	public void setClassLayout(ClassLayoutEntry classLayout) {
		this.classLayout = classLayout;
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
	public Property[] getProperties() {
		if(properties==null) return new Property[0];
		
		if(!ByteReader.testFlags(internalFlags, INT_FLAGS_SORTED_PROPERTIES)) {
			ByteReader.setFlags(internalFlags, INT_FLAGS_SORTED_PROPERTIES, true);
			Arrays.sort(properties);
		}
		
		return properties;
	}
	
	public void setProperties(PropertyEntry [] properties) {
		this.properties = properties;
		if(fields!=null) {
			for(PropertyEntry property: properties) {
				property.setParent(this);
			}
				
			this.properties = properties;	
		}
	}
	
	@Override
	public Event[] getEvents() {
		if(events==null) return new Event[0];
		
		if(!ByteReader.testFlags(internalFlags, INT_FLAGS_SORTED_EVENTS)) {
			ByteReader.setFlags(internalFlags, INT_FLAGS_SORTED_EVENTS, true);
			Arrays.sort(events);
		}
		
		return events;
	}
	
	public void setEvents(Event [] events) {
		this.events = events;
	}

	@Override
	public boolean isExported() {
		return ByteReader.testFlags(internalFlags, INT_FLAGS_IS_EXPORTED);
	}

	public void setExported(boolean isExported) {
		internalFlags = ByteReader.setFlags(
				internalFlags, INT_FLAGS_IS_EXPORTED, isExported );
	}
	
	public TypeRef [] getInterfaces() {
		if(implementedInterfaces==null || implementedInterfaces.size()==0)
			return new TypeRef[0];
		
		TypeRef [] interfaces = new TypeRef[implementedInterfaces.size()];
		
		implementedInterfaces.toArray(interfaces);
		Arrays.sort(interfaces);
		
		return interfaces;
	}

	public boolean addInterface(TypeRef i) {
		if(implementedInterfaces==null) implementedInterfaces = new ArrayList<TypeRef>(4);
		return implementedInterfaces.add(i);
	}
	
	public Type [] getEnclosingClasses() {
		if(enclosingClasses==null || enclosingClasses.size()==0)
			return new Type[0];
		
		Type [] classes = new Type[enclosingClasses.size()];
		
		enclosingClasses.toArray(classes);
		Arrays.sort(classes);
		
		return classes;
	}
	
	public boolean addNestedClass(TypeDefEntry type) {
		if(enclosingClasses==null) enclosingClasses = new ArrayList<Type>(4);
		return enclosingClasses.add(type);
	}
	

	@Override
	public boolean isNested() {
		return ByteReader.testFlags(internalFlags, INT_FLAGS_IS_NESTED);
	}
	
	public void setNested(boolean nested) {
		internalFlags = ByteReader.setFlags(
				internalFlags, INT_FLAGS_IS_NESTED, nested );
	}
	
	public GenericParamEntry [] getGenericParameters() {
		if(genericParams==null || genericParams.size()==0)
			return new GenericParamEntry[0];
		
		GenericParamEntry [] params = new GenericParamEntry[genericParams.size()];
		
		genericParams.toArray(params);
		return params;
	}

	public boolean addGenericParam(GenericParamEntry p) {
		if(genericParams==null) genericParams = new ArrayList<GenericParamEntry>(4);
		return genericParams.add(p);
	}
	
	@Override
	public TypeDefEntry getType() {
		return this;
	}

	@Override
	public MethodDefEntry getMethod() {
		return null;
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
		
//		if(getCustomAttributes()!=null) {
//			for(CustomAttributeEntry c: getCustomAttributes()) {
//				buffer.append(c.toExtendedString());
//				buffer.append("\n");
//			}
//		}
		
		
		buffer.append("TypeDef: ");
		buffer.append(getFullQualifiedName());
		
		if(genericParams!=null && genericParams.size()>0) {
			buffer.append("<");
			boolean first = true;
			for(Parameter p: genericParams) {
				if(!first) buffer.append(", ");
				
				buffer.append(p.getName());
				
				first = false;
			}
			buffer.append(">");
		}
		
		if(extendz!=null) {
			buffer.append(" extends ");
			buffer.append(extendz.getFullQualifiedName());
		}
		
		if(implementedInterfaces!=null && implementedInterfaces.size()>0) {
			buffer.append(" implements ");
			boolean first = true;
			for(TypeRef i: implementedInterfaces) {
				if(!first) buffer.append(", ");
				
				buffer.append(i.getFullQualifiedName());
				
				first = false;
			}
		}
		
		buffer.append("\n\t(NumberOfFields: ");
		buffer.append(fields==null?0:fields.length);
		buffer.append(" NumberOfMethods: ");
		buffer.append(methods==null?0:methods.length);
		buffer.append(String.format(" Flags: 0x%08x)", flags));
		
		
		return buffer.toString();
	}

	public void linkMethodsToType() {
		if(methods!=null) {
			for(MethodDefEntry m: methods) {
				m.setOwner(this);
			}
		}
	}
	
	public boolean isInheritedFrom(TypeRef type) {
		
		if(type.getFullQualifiedName().equals(getFullQualifiedName()))
			return true;
		
		if(extendz!=null) {
			if(extendz.getType()!=null) {
				return extendz.getType().isInheritedFrom(type);
			} else if(extendz.getFullQualifiedName().equals(type.getFullQualifiedName())) {
				return true;
			}
		}
		
		for(TypeRef i: getInterfaces()) {
			if(i.getFullQualifiedName().equals(type.getFullQualifiedName()))
				return true;
		}
		
		return false;
	}
	
	public boolean isInheritedFrom(String fullQualifiedName) {
		
		if(fullQualifiedName.equals(getFullQualifiedName()))
			return true;
		
		if(extendz!=null) {
			if(extendz.getType()!=null) {
				return extendz.getType().isInheritedFrom(fullQualifiedName);
			} else if(extendz.getFullQualifiedName().equals(fullQualifiedName)) {
				return true;
			}
		}
		
		for(TypeRef i: getInterfaces()) {
			if(i.getFullQualifiedName().equals(fullQualifiedName))
				return true;
		}
		
		return false;
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(fields);
		result = prime * result + (int) (flags ^ (flags >>> 32));
		result = prime * result + internalFlags;
		result = prime * result + Arrays.hashCode(methods);
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
		
		TypeDefEntry other = (TypeDefEntry) obj;
		
		if (internalFlags != other.internalFlags)
			return false;
		
		if (flags != other.flags)
			return false;
		
		
		if (!Arrays.equals(methods, other.methods)) {
			//use this code for debugging
//			for(int i=0;i<methods.length;i++) {
//				if(!methods[i].equals(other.methods[i])) {
//					methods[i].equals(other.methods[i]); //insert break point here
//				}
//			}
			return false;
		}
		
		if (!Arrays.equals(fields, other.fields)) {
			//use this code for debugging
//			for(int i=0;i<fields.length;i++) {
//				if(!fields[i].equals(other.fields[i])) {
//					fields[i].equals(other.fields[i]); //insert break point here
//				}
//			}
			return false;
		}
		
		if (!Arrays.equals(properties, other.properties))
			return false;
		
		if (!Arrays.equals(events, other.events))
			return false;
		
		if (!Arrays.equals(constantEntries, other.constantEntries))
			return false;
		
		if (classLayout == null) {
			if (other.classLayout != null)
				return false;
		} else if (!classLayout.equals(other.classLayout))
			return false;
		
		//compare extends only via full qualified name
		if (extendz == null) {
			if (other.extendz != null)
				return false;
		} else if (other.extendz == null) {
			return false;
		} else if(!extendz.getFullQualifiedName().equals(other.extendz.getFullQualifiedName())) {
			return false;
		}
		
		//array list - compare classes only via the full qualified name
		if (implementedInterfaces == null) {
			if (other.implementedInterfaces != null)
				return false;
		} else if (other.implementedInterfaces == null) {
			return false;
		} else if(implementedInterfaces.size()!=other.implementedInterfaces.size()){
			return false;
		} else {
			//both collections are sorted!
			for(int i=0;i<implementedInterfaces.size();i++) {
				if(!implementedInterfaces.get(i).getFullQualifiedName().equals(
						other.implementedInterfaces.get(i).getFullQualifiedName()))
					return false;
			}
		}

		//array list - compare classes only via the full qualified name
		if (enclosingClasses == null) {
			if (other.enclosingClasses != null)
				return false;
		} else if (other.enclosingClasses == null) {
			return false;
		} else if(enclosingClasses.size()!=other.enclosingClasses.size()){
			return false;
		} else {
			//both collections are sorted!
			for(int i=0;i<enclosingClasses.size();i++) {
				if(!enclosingClasses.get(i).getFullQualifiedName().equals(
						other.enclosingClasses.get(i).getFullQualifiedName()))
					return false;
			}
		}
		
		//array list
		if (genericParams == null) {
			if (other.genericParams != null)
				return false;
		} else if (!genericParams.equals(other.genericParams))
			return false;
		
		//array list
		if (additionalMethods == null) {
			if (other.additionalMethods != null)
				return false;
		} else if (!additionalMethods.equals(other.additionalMethods))
			return false;
		
		return true;
	}
	
	@Override
	public int compareTo(TypeRef other) {
		if(other == null || other.getType()==null) {
			return Integer.MAX_VALUE;
		}
	
		return ArrayUtils.compareStrings(other.getFullQualifiedName(),  getFullQualifiedName());
	}

}

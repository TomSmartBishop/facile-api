package at.pollaknet.api.facile.metamodel;

import at.pollaknet.api.facile.header.cli.stream.BlobStream;
import at.pollaknet.api.facile.header.cli.stream.GuidStream;
import at.pollaknet.api.facile.header.cli.stream.MetadataStream;
import at.pollaknet.api.facile.header.cli.stream.StringsStream;
import at.pollaknet.api.facile.header.cli.stream.UserStringStream;
import at.pollaknet.api.facile.header.cli.stream.metatable.AbstractTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyOsTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyProcessorTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyRefOsTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyRefProcessorTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.AssemblyTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ClassLayoutTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ConstantTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.CustomAttributeTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.DeclSecurityTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.EventMapTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.EventTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ExportedTypeTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FieldLayoutTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FieldMarshalTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FieldRVATable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FieldTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.FileTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.GenericParamConstraintTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.GenericParamTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ImplMapTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.InterfaceImplTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ManifestResourceTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MemberRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MethodDefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MethodImplTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MethodSemanticsTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.MethodSpecTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ModuleRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ModuleTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.NestedClassTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.ParamTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.PropertyMapTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.PropertyTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.StandAloneSigTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.TypeDefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.TypeRefTable;
import at.pollaknet.api.facile.header.cli.stream.metatable.TypeSpecTable;
import at.pollaknet.api.facile.metamodel.entries.AssemblyEntry;
import at.pollaknet.api.facile.metamodel.entries.AssemblyOsEntry;
import at.pollaknet.api.facile.metamodel.entries.AssemblyProcessorEntry;
import at.pollaknet.api.facile.metamodel.entries.AssemblyRefEntry;
import at.pollaknet.api.facile.metamodel.entries.AssemblyRefOsEntry;
import at.pollaknet.api.facile.metamodel.entries.AssemblyRefProcessorEntry;
import at.pollaknet.api.facile.metamodel.entries.ClassLayoutEntry;
import at.pollaknet.api.facile.metamodel.entries.ConstantEntry;
import at.pollaknet.api.facile.metamodel.entries.CustomAttributeEntry;
import at.pollaknet.api.facile.metamodel.entries.DeclSecurityEntry;
import at.pollaknet.api.facile.metamodel.entries.EventEntry;
import at.pollaknet.api.facile.metamodel.entries.EventMapEntry;
import at.pollaknet.api.facile.metamodel.entries.ExportedTypeEntry;
import at.pollaknet.api.facile.metamodel.entries.FieldEntry;
import at.pollaknet.api.facile.metamodel.entries.FieldLayoutEntry;
import at.pollaknet.api.facile.metamodel.entries.FieldMarshalEntry;
import at.pollaknet.api.facile.metamodel.entries.FieldRVAEntry;
import at.pollaknet.api.facile.metamodel.entries.FileEntry;
import at.pollaknet.api.facile.metamodel.entries.GenericParamConstraintEntry;
import at.pollaknet.api.facile.metamodel.entries.GenericParamEntry;
import at.pollaknet.api.facile.metamodel.entries.ImplMapEntry;
import at.pollaknet.api.facile.metamodel.entries.InterfaceImplEntry;
import at.pollaknet.api.facile.metamodel.entries.ManifestResourceEntry;
import at.pollaknet.api.facile.metamodel.entries.MemberRefEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodDefEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodImplEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodSemanticsEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodSpecEntry;
import at.pollaknet.api.facile.metamodel.entries.ModuleEntry;
import at.pollaknet.api.facile.metamodel.entries.ModuleRefEntry;
import at.pollaknet.api.facile.metamodel.entries.NestedClassEntry;
import at.pollaknet.api.facile.metamodel.entries.ParamEntry;
import at.pollaknet.api.facile.metamodel.entries.PropertyEntry;
import at.pollaknet.api.facile.metamodel.entries.PropertyMapEntry;
import at.pollaknet.api.facile.metamodel.entries.StandAloneSigEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeDefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeRefEntry;
import at.pollaknet.api.facile.metamodel.entries.TypeSpecEntry;
import at.pollaknet.api.facile.util.ByteReader;

public class MetadataModel {

	//all record is's of the metadata tables
	public static final int RECORD_ID_MODULE					= 0x00;
	public static final int RECORD_ID_TYPE_REF			 		= 0x01;
	public static final int RECORD_ID_TYPE_DEF 					= 0x02;
	public static final int RECORD_ID_FIELD_PTR		 			= 0x03;
	public static final int RECORD_ID_FIELD_DEF		 			= 0x04;
	public static final int RECORD_ID_METHOD_PTR	 			= 0x05;
	public static final int RECORD_ID_METHOD_DEF	 			= 0x06;
	public static final int RECORD_ID_PARAM_PTR		 			= 0x07;
	public static final int RECORD_ID_PARAM_DEF		 			= 0x08;
	public static final int RECORD_ID_INTERFACE_IMPL 			= 0x09;
	public static final int RECORD_ID_MEBMER_REF	 			= 0x0a;
	public static final int RECORD_ID_CONSTANT		 			= 0x0b;
	public static final int RECORD_ID_CUSTOM_ATTRIBUTE 			= 0x0c;
	public static final int RECORD_ID_FIELD_MARSHAL 			= 0x0d;
	public static final int RECORD_ID_DECL_SECURITY	 			= 0x0e;
	public static final int RECORD_ID_CLASS_LAYOUT	 			= 0x0f;
	public static final int RECORD_ID_FIELD_LAYOUT	 			= 0x10;
	public static final int RECORD_ID_STAND_ALONE_SIGNATURE		= 0x11;
	public static final int RECORD_ID_EVENT_MAP					= 0x12;
	public static final int RECORD_ID_EVENT_PTR					= 0x13;
	public static final int RECORD_ID_EVENT			 			= 0x14;
	public static final int RECORD_ID_PROPERTY_MAP	 			= 0x15;
	public static final int RECORD_ID_PROPERTY_PTR	 			= 0x16;
	public static final int RECORD_ID_PROPERTY		 			= 0x17;
	public static final int RECORD_ID_METHOD_SEMANTICS 			= 0x18;
	public static final int RECORD_ID_METHOD_IMPL	 			= 0x19;
	public static final int RECORD_ID_MODULE_REF	 			= 0x1a;
	public static final int RECORD_ID_TYPE_SPEC		 			= 0x1b;
	public static final int RECORD_ID_IMPL_MAP		 			= 0x1c;
	public static final int RECORD_ID_FIELD_RVA		 			= 0x1d;
	public static final int RECORD_ID_ENC_LOG		 			= 0x1e;
	public static final int RECORD_ID_ENC_MAP		 			= 0x1f;	
	public static final int RECORD_ID_ASSEMBLY					= 0x20;
	public static final int RECORD_ID_ASSEMBLY_PROCESSOR		= 0x21;
	public static final int RECORD_ID_ASSEMBLY_OS				= 0x22;
	public static final int RECORD_ID_ASSEMBLY_REF	 			= 0x23;
	public static final int RECORD_ID_ASSEMBLY_REF_PROCESSOR	= 0x23;
	public static final int RECORD_ID_ASSEMBLY_REF_OS 			= 0x23;
	public static final int RECORD_ID_FILE			 			= 0x26;
	public static final int RECORD_ID_EXPORTED_TYPE	 			= 0x27;
	public static final int RECORD_ID_MANIFEST_RESOURCE			= 0x28;
	public static final int RECORD_ID_NESTED_CLASS				= 0x29;
	public static final int RECORD_ID_GENERIC_PARAM				= 0x2a;
	public static final int RECORD_ID_METHOD_SPEC				= 0x2b;
	public static final int RECORD_ID_GENERIC_PARAM_CONSTRAINT	= 0x2c;
	
	public static final int TOKEN_VALUE_MASK		 			= 0x00ffffff;
	
	//the metadata items of the meta model
	public ModuleEntry module[];
	public TypeRefEntry typeRef[];
	public TypeDefEntry typeDef[];
	public FieldEntry field[];
	public MethodDefEntry methodDef[];
	public ParamEntry param[];
	public InterfaceImplEntry interfaceImpl[];
	public MemberRefEntry memberRef[];
	public ConstantEntry constant[];
	public CustomAttributeEntry customAttribute[];
	public FieldMarshalEntry fieldMarshal[];
	public DeclSecurityEntry declSecurity[];
	public ClassLayoutEntry classLayout[];
	public FieldLayoutEntry fieldLayout[];
	public StandAloneSigEntry standAloneSig[];
	public EventMapEntry eventMap[];
	public EventEntry event[];
	public PropertyMapEntry propertyMap[];
	public PropertyEntry property[];
	public MethodSemanticsEntry methodSemantics[];
	public MethodImplEntry methodImpl[];
	public ModuleRefEntry moduleRef[];
	public TypeSpecEntry typeSpec[];
	public ImplMapEntry implMap[];
	public FieldRVAEntry fieldRva[];
	public AssemblyEntry assembly[];
	public AssemblyProcessorEntry assemblyProcessor[];
	public AssemblyOsEntry assemblyOs[];
	public AssemblyRefEntry assemblyRef[];
	public AssemblyRefProcessorEntry assemblyRefProcessor[];
	public AssemblyRefOsEntry assemblyRefOs[];
	public FileEntry file[];
	public ExportedTypeEntry exportedType[];
	public ManifestResourceEntry manifestResource[];
	public NestedClassEntry nestedClass[];
	public GenericParamEntry genericParam[];
	public MethodSpecEntry methodSpec[];
	public GenericParamConstraintEntry genericParamConstraint[];

	private RenderableCilElement table [][];

	private UserStringStream userStringStream;
	private String alternativeModuleName;
	private boolean assemblyHasIlSection;
	private boolean containsDeletedData;
    private boolean loadByteCode;

	public MetadataModel(boolean assemblyHasIlSection, String alternativeModuleName,
			MetadataStream m,  StringsStream s, UserStringStream u, GuidStream g, BlobStream b, boolean loadByteCode) {

		//setup flags
		containsDeletedData = ByteReader.testFlags(m.getHeaps(), MetadataStream.HEAPS_FLAGS_UNOPTIMIZED_CAN_CONTAIN_DELETED_DATA);
		this.assemblyHasIlSection = assemblyHasIlSection;
		
		//the user string stream is required later
		userStringStream = u;

		//remember the alternative module name
		this.alternativeModuleName = alternativeModuleName;

        //do we want byte code loading
        this.loadByteCode = loadByteCode;

		//create the object representation of metadata items
		AbstractTable [] tableModel = m.getMetadataTable();
		table = new RenderableCilElement[64][];
		
		createArrayContainer(tableModel);
		
		createEmptyArrayElements();
		
		//assign the single (still empty) array to the over-all array
		table[ModuleTable.TABLE_INDEX] = module;
		table[TypeRefTable.TABLE_INDEX] = typeRef;
		table[TypeDefTable.TABLE_INDEX] = typeDef;
		table[FieldTable.TABLE_INDEX] = field;
		table[MethodDefTable.TABLE_INDEX] = methodDef;
		table[ParamTable.TABLE_INDEX] = param;
		table[InterfaceImplTable.TABLE_INDEX] = interfaceImpl;
		table[MemberRefTable.TABLE_INDEX] = memberRef;
		table[ConstantTable.TABLE_INDEX] = constant;
		table[CustomAttributeTable.TABLE_INDEX] = customAttribute;
		table[FieldMarshalTable.TABLE_INDEX] = fieldMarshal;
		table[DeclSecurityTable.TABLE_INDEX] = declSecurity;
		table[ClassLayoutTable.TABLE_INDEX] = classLayout;
		table[FieldLayoutTable.TABLE_INDEX] = fieldLayout;
		table[StandAloneSigTable.TABLE_INDEX] = standAloneSig;
		table[EventMapTable.TABLE_INDEX] = eventMap;
		table[EventTable.TABLE_INDEX] = event;
		table[PropertyMapTable.TABLE_INDEX] = propertyMap;
		table[PropertyTable.TABLE_INDEX] = property;
		table[MethodSemanticsTable.TABLE_INDEX] = methodSemantics;
		table[MethodImplTable.TABLE_INDEX] = methodImpl;
		table[ModuleRefTable.TABLE_INDEX] = moduleRef;
		table[TypeSpecTable.TABLE_INDEX] = typeSpec;
		table[ImplMapTable.TABLE_INDEX] = implMap;
		table[FieldRVATable.TABLE_INDEX] = fieldRva;
		table[AssemblyTable.TABLE_INDEX] = assembly;
		table[AssemblyProcessorTable.TABLE_INDEX] = assemblyProcessor;
		table[AssemblyOsTable.TABLE_INDEX] = assemblyOs;
		table[AssemblyRefTable.TABLE_INDEX] = assemblyRef;
		table[AssemblyRefProcessorTable.TABLE_INDEX] = assemblyRefProcessor;
		table[AssemblyRefOsTable.TABLE_INDEX] = assemblyRefOs;
		table[FileTable.TABLE_INDEX] = file;
		table[ExportedTypeTable.TABLE_INDEX] = exportedType;
		table[ManifestResourceTable.TABLE_INDEX] = manifestResource;
		table[NestedClassTable.TABLE_INDEX] = nestedClass;
		table[GenericParamTable.TABLE_INDEX] = genericParam;
		table[MethodSpecTable.TABLE_INDEX] = methodSpec;
		table[GenericParamConstraintTable.TABLE_INDEX] = genericParamConstraint;
		
		//fill the metadata item with the data from the table out of the metadata stream
		for(int i=0;i<tableModel.length;i++) {
			if(tableModel[i]!=null) {
				tableModel[i].fill(this, m, s, u, g, b, table[i]);
			}
		}
	}

	/**
	 * Create the empty array instances (with the appropriate length)
	 * for the metadata items.
	 * @param tableModel The raw table model of the metadata stream.
	 */
	private void createArrayContainer(AbstractTable[] tableModel) {
		
		module 				= new ModuleEntry [tableModel[ModuleTable.TABLE_INDEX].getNumberOfRows()];
		typeRef 			= new TypeRefEntry [tableModel[TypeRefTable.TABLE_INDEX].getNumberOfRows()];
		typeDef 			= new TypeDefEntry [tableModel[TypeDefTable.TABLE_INDEX].getNumberOfRows()];
		field 				= new FieldEntry [tableModel[FieldTable.TABLE_INDEX].getNumberOfRows()];
		methodDef 			= new MethodDefEntry [tableModel[MethodDefTable.TABLE_INDEX].getNumberOfRows()];
		param 				= new ParamEntry [tableModel[ParamTable.TABLE_INDEX].getNumberOfRows()];
		interfaceImpl 		= new InterfaceImplEntry [tableModel[InterfaceImplTable.TABLE_INDEX].getNumberOfRows()];
		memberRef 			= new MemberRefEntry [tableModel[MemberRefTable.TABLE_INDEX].getNumberOfRows()];
		constant 			= new ConstantEntry [tableModel[ConstantTable.TABLE_INDEX].getNumberOfRows()];
		customAttribute 	= new CustomAttributeEntry [tableModel[CustomAttributeTable.TABLE_INDEX].getNumberOfRows()];
		fieldMarshal 		= new FieldMarshalEntry [tableModel[FieldMarshalTable.TABLE_INDEX].getNumberOfRows()];
		declSecurity 		= new DeclSecurityEntry [tableModel[DeclSecurityTable.TABLE_INDEX].getNumberOfRows()];
		classLayout 		= new ClassLayoutEntry [tableModel[ClassLayoutTable.TABLE_INDEX].getNumberOfRows()];
		fieldLayout 		= new FieldLayoutEntry [tableModel[FieldLayoutTable.TABLE_INDEX].getNumberOfRows()];
		standAloneSig 		= new StandAloneSigEntry [tableModel[StandAloneSigTable.TABLE_INDEX].getNumberOfRows()];
		eventMap 			= new EventMapEntry [tableModel[EventMapTable.TABLE_INDEX].getNumberOfRows()];
		event 				= new EventEntry [tableModel[EventTable.TABLE_INDEX].getNumberOfRows()];
		propertyMap 		= new PropertyMapEntry [tableModel[PropertyMapTable.TABLE_INDEX].getNumberOfRows()];
		property 			= new PropertyEntry [tableModel[PropertyTable.TABLE_INDEX].getNumberOfRows()];
		methodSemantics 	= new MethodSemanticsEntry [tableModel[MethodSemanticsTable.TABLE_INDEX].getNumberOfRows()];
		methodImpl 			= new MethodImplEntry [tableModel[MethodImplTable.TABLE_INDEX].getNumberOfRows()];
		moduleRef 			= new ModuleRefEntry [tableModel[ModuleRefTable.TABLE_INDEX].getNumberOfRows()];
		typeSpec 			= new TypeSpecEntry [tableModel[TypeSpecTable.TABLE_INDEX].getNumberOfRows()];
		implMap 			= new ImplMapEntry [tableModel[ImplMapTable.TABLE_INDEX].getNumberOfRows()];
		fieldRva 			= new FieldRVAEntry [tableModel[FieldRVATable.TABLE_INDEX].getNumberOfRows()];
		assembly 			= new AssemblyEntry [tableModel[AssemblyTable.TABLE_INDEX].getNumberOfRows()];
		assemblyProcessor 	= new AssemblyProcessorEntry [tableModel[AssemblyProcessorTable.TABLE_INDEX].getNumberOfRows()];
		assemblyOs 			= new AssemblyOsEntry [tableModel[AssemblyOsTable.TABLE_INDEX].getNumberOfRows()];
		assemblyRef 		= new AssemblyRefEntry [tableModel[AssemblyRefTable.TABLE_INDEX].getNumberOfRows()];
		assemblyRefProcessor= new AssemblyRefProcessorEntry [tableModel[AssemblyRefProcessorTable.TABLE_INDEX].getNumberOfRows()];
		assemblyRefOs 		= new AssemblyRefOsEntry [tableModel[AssemblyRefOsTable.TABLE_INDEX].getNumberOfRows()];
		file 				= new FileEntry [tableModel[FileTable.TABLE_INDEX].getNumberOfRows()];
		exportedType 		= new ExportedTypeEntry [tableModel[ExportedTypeTable.TABLE_INDEX].getNumberOfRows()];
		manifestResource 	= new ManifestResourceEntry [tableModel[ManifestResourceTable.TABLE_INDEX].getNumberOfRows()];
		nestedClass 		= new NestedClassEntry [tableModel[NestedClassTable.TABLE_INDEX].getNumberOfRows()];
		genericParam 		= new GenericParamEntry [tableModel[GenericParamTable.TABLE_INDEX].getNumberOfRows()];
		methodSpec 			= new MethodSpecEntry [tableModel[MethodSpecTable.TABLE_INDEX].getNumberOfRows()];
		genericParamConstraint = new GenericParamConstraintEntry [tableModel[GenericParamConstraintTable.TABLE_INDEX].getNumberOfRows()];
	}
	
	/**
	 * Create new instances for all empty arrays of the metadata items.
	 */
	private void createEmptyArrayElements() {
		int index;

		for(index = 0;index<module.length; index++) { module[index] = new ModuleEntry(); }
		for(index = 0;index<typeRef.length; index++) { typeRef[index] = new TypeRefEntry(); }
		for(index = 0;index<typeDef.length; index++) { typeDef[index] = new TypeDefEntry(); }
		for(index = 0;index<field.length; index++) { field[index] = new FieldEntry(); }
		for(index = 0;index<methodDef.length; index++) { methodDef[index] = new MethodDefEntry(); }
		for(index = 0;index<param.length; index++) { param[index] = new ParamEntry(); }
		for(index = 0;index<interfaceImpl.length; index++) { interfaceImpl[index] = new InterfaceImplEntry(); }
		for(index = 0;index<memberRef.length; index++) { memberRef[index] = new MemberRefEntry(); }
		for(index = 0;index<constant.length; index++) { constant[index] = new ConstantEntry(); }
		for(index = 0;index<customAttribute.length; index++) { customAttribute[index] = new CustomAttributeEntry(); }
		for(index = 0;index<fieldMarshal.length; index++) { fieldMarshal[index] = new FieldMarshalEntry(); }
		for(index = 0;index<declSecurity.length; index++) { declSecurity[index] = new DeclSecurityEntry(); }
		for(index = 0;index<classLayout.length; index++) { classLayout[index] = new ClassLayoutEntry(); }
		for(index = 0;index<fieldLayout.length; index++) { fieldLayout[index] = new FieldLayoutEntry(); }
		for(index = 0;index<standAloneSig.length; index++) { standAloneSig[index] = new StandAloneSigEntry(); }
		for(index = 0;index<eventMap.length; index++) { eventMap[index] = new EventMapEntry(); }
		for(index = 0;index<event.length; index++) { event[index] = new EventEntry(); }
		for(index = 0;index<propertyMap.length; index++) { propertyMap[index] = new PropertyMapEntry(); }
		for(index = 0;index<property.length; index++) { property[index] = new PropertyEntry(); }
		for(index = 0;index<methodSemantics.length; index++) { methodSemantics[index] = new MethodSemanticsEntry(); }
		for(index = 0;index<methodImpl.length; index++) { methodImpl[index] = new MethodImplEntry(); }
		for(index = 0;index<moduleRef.length; index++) { moduleRef[index] = new ModuleRefEntry(); }
		for(index = 0;index<typeSpec.length; index++) { typeSpec[index] = new TypeSpecEntry(); }
		for(index = 0;index<implMap.length; index++) { implMap[index] = new ImplMapEntry(); }
		for(index = 0;index<fieldRva.length; index++) { fieldRva[index] = new FieldRVAEntry(); }
		for(index = 0;index<assembly.length; index++) { assembly[index] = new AssemblyEntry(); }
		for(index = 0;index<assemblyProcessor.length; index++) { assemblyProcessor[index] = new AssemblyProcessorEntry(); }
		for(index = 0;index<assemblyOs.length; index++) { assemblyOs[index] = new AssemblyOsEntry(); }
		for(index = 0;index<assemblyRef.length; index++) { assemblyRef[index] = new AssemblyRefEntry(); }
		for(index = 0;index<assemblyRefProcessor.length; index++) { assemblyRefProcessor[index] = new AssemblyRefProcessorEntry(); }
		for(index = 0;index<assemblyRefOs.length; index++) { assemblyRefOs[index] = new AssemblyRefOsEntry(); }
		for(index = 0;index<file.length; index++) { file[index] = new FileEntry(); }
		for(index = 0;index<exportedType.length; index++) { exportedType[index] = new ExportedTypeEntry(); }
		for(index = 0;index<manifestResource.length; index++) { manifestResource[index] = new ManifestResourceEntry(); }
		for(index = 0;index<nestedClass.length; index++) { nestedClass[index] = new NestedClassEntry(); }
		for(index = 0;index<genericParam.length; index++) { genericParam[index] = new GenericParamEntry(); }
		for(index = 0;index<methodSpec.length; index++) { methodSpec[index] = new MethodSpecEntry(); }
		for(index = 0;index<genericParamConstraint.length; index++) { genericParamConstraint[index] = new GenericParamConstraintEntry(); }
	}

	/**
	 * Accesses every metadata item (expect sting tokens) via a given metadata token.
	 * @param metadataToken The token for the demanded item.
	 * @return The metadata item for the given token or {@code null} if there is no such item.
	 */
	public RenderableCilElement getEntryByToken(int metadataToken) {
		int tableIndex = metadataToken>>24;
				
		if(tableIndex>=0 && tableIndex<=GenericParamConstraintTable.TABLE_INDEX) {
			
				RenderableCilElement [] entries = table[tableIndex];
				
				int element = (metadataToken&TOKEN_VALUE_MASK) - 1;
				
				if(element>=0&&element<entries.length)
					return entries[element];
		}
		
		return null;
	}

	/**
	 * Returns the value of a user string token.
	 * @param metadataToken The metadata token pointing to the required string.
	 * @return The resolved {@code String}.
	 */
	public String getUserStringbyToken(int metadataToken) {
		return userStringStream.getUserString(metadataToken&TOKEN_VALUE_MASK);
	}
	
	/**
	 * Returns the alternative name of the module (which is equal to
	 * the module name if the string stream is present).
	 * @return An alternative, generated name.
	 */
	public String getAlternativeModuleName() {
		return alternativeModuleName;
	}

	/**
	 * The .il file is a exception for thunks and wrapper assemblies
	 * and holds the metadata of the assembly.
	 * @return {@code true} if a .il metadata section has been detected, otherwise {@code false}.
	 */
	public boolean assemblyHasIlSection() {
		return assemblyHasIlSection;
	}
	
	/**
	 * Tells you weather the metamodel contains deleted data (which maybe
	 * references useless data or null-data) or not.
	 * @return {@code true} if the metamodel contains deleted data, otherwise {@code false}.
	 */
	public boolean containsDeletedData() {
		return containsDeletedData;
	}

    /**
     * Returns true if byte code needs to be loaded
     * @return true if byte code needs to be loaded, false otherwise
     */
    public boolean isByteCodeNeed() {
        return loadByteCode;
    }

    public String toString() {

		StringBuffer buffer = new StringBuffer("Metadata:");
		
		for(int tab=0; tab<table.length; tab++) {
			if(table[tab]!=null) {
				for(int i=0; i<table[tab].length; i++) {
					buffer.append("\n" + table[tab][i].toString());
				}
			}
		}

		return buffer.toString();
	}
}

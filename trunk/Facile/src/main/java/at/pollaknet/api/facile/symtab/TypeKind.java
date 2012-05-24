package at.pollaknet.api.facile.symtab;

/**
 * Defines all element (basic) types of .Net.
 */
public class TypeKind {

	public final static int ELEMENT_TYPE_END 			= 0x00; //Marks end of a list
	public final static int ELEMENT_TYPE_VOID 			= 0x01;
	public final static int ELEMENT_TYPE_BOOLEAN 		= 0x02;
	public final static int ELEMENT_TYPE_CHAR 			= 0x03;
	public final static int ELEMENT_TYPE_I1 			= 0x04;
	public final static int ELEMENT_TYPE_U1 			= 0x05;
	public final static int ELEMENT_TYPE_I2 			= 0x06;
	public final static int ELEMENT_TYPE_U2 			= 0x07;
	public final static int ELEMENT_TYPE_I4 			= 0x08;
	public final static int ELEMENT_TYPE_U4 			= 0x09;
	public final static int ELEMENT_TYPE_I8 			= 0x0a;
	public final static int ELEMENT_TYPE_U8 			= 0x0b;
	public final static int ELEMENT_TYPE_R4 			= 0x0c;
	public final static int ELEMENT_TYPE_R8 			= 0x0d;
	public final static int ELEMENT_TYPE_STRING 		= 0x0e;
	public final static int ELEMENT_TYPE_PTR 			= 0x0f; //Followed by type
	public final static int ELEMENT_TYPE_BYREF 			= 0x10; //Followed by type
	public final static int ELEMENT_TYPE_VALUETYPE 		= 0x11; //Followed by TypeDef or TypeRef token
	public final static int ELEMENT_TYPE_CLASS 			= 0x12; //Followed by TypeDef or TypeRef token
	public final static int ELEMENT_TYPE_VAR 			= 0x13; //Generic parameter in a generic type definition, represented as number
	public final static int ELEMENT_TYPE_ARRAY 			= 0x14; //type rank boundsCount bound1 ... loCount lo1 ...
	public final static int ELEMENT_TYPE_GENERICINST 	= 0x15; //Generic type instantiation. Followed by type typearg-count type-1 ... type-n
	public final static int ELEMENT_TYPE_TYPEDBYREF 	= 0x16;
	public final static int ELEMENT_TYPE_I 				= 0x18; //System.IntPtr
	public final static int ELEMENT_TYPE_U 				= 0x19; //System.UIntPtr
	public final static int ELEMENT_TYPE_FNPTR 			= 0x1b; //Followed by full method signature
	public final static int ELEMENT_TYPE_OBJECT 		= 0x1c; //System.Object
	public final static int ELEMENT_TYPE_SZARRAY 		= 0x1d; //Single-dim array with 0 lower bound
	public final static int ELEMENT_TYPE_MVAR 			= 0x1e; //Generic parameter in a generic method definition, represented as number
	public final static int ELEMENT_TYPE_CMOD_REQD 		= 0x1f; //Required modifier : followed by a TypeDef or TypeRef token
	public final static int ELEMENT_TYPE_CMOD_OPT 		= 0x20; //Optional modifier : followed by a TypeDef or TypeRef token
	public final static int ELEMENT_TYPE_INTERNAL 		= 0x21; //Implemented within the CLI
	
	//TODO: Consider moving this to the BasicTypeDirectory
	//These are the basic numeric value types which are getting
	//represented as a number in the toString method.
	public final static int [] NUMERIC_TYPES = new int [] {
		ELEMENT_TYPE_BOOLEAN,
		ELEMENT_TYPE_CHAR,
		ELEMENT_TYPE_I1,
		ELEMENT_TYPE_U1,
		ELEMENT_TYPE_I2,
		ELEMENT_TYPE_U2,
		ELEMENT_TYPE_I4,
		ELEMENT_TYPE_U4,
		ELEMENT_TYPE_I8,
		ELEMENT_TYPE_U8,
		ELEMENT_TYPE_R4,
		ELEMENT_TYPE_R8,
		ELEMENT_TYPE_I,
		ELEMENT_TYPE_U
	};

}
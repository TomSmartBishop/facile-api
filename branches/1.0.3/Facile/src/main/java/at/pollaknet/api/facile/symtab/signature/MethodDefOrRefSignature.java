package at.pollaknet.api.facile.symtab.signature;

import at.pollaknet.api.facile.exception.InvalidSignatureException;
import at.pollaknet.api.facile.metamodel.entries.MemberRefEntry;
import at.pollaknet.api.facile.metamodel.entries.MethodDefEntry;
import at.pollaknet.api.facile.metamodel.entries.ParamEntry;
import at.pollaknet.api.facile.metamodel.entries.StandAloneSigEntry;
import at.pollaknet.api.facile.symtab.BasicTypesDirectory;
import at.pollaknet.api.facile.symtab.symbols.MethodSignature;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.ByteReader;

public class MethodDefOrRefSignature extends Signature implements MethodSignature {

	//the fixupTable represents a correction for invalid method signatures
	private FixupElement [] fixupTable = new FixupElement []{
			new FixupElement(".ctor", "System.Runtime.CompilerServices.RequiredAttributeAttribute",
					new byte [] {0x20,0x00,0x01}, new byte[] {0x20,0x01,0x01,0x50})
		}; 

	private byte flags;
	
	private int parameterCount = 0;
	private int genericParameterCount = 0;
	
	private int sentinelPosition = -1;
	
	private TypeRef returnType;
	private ParamEntry[] parameter = null;
	
	public byte getFlags() {
		return flags;
	}

	public int getGenericParameterCount() {
		return genericParameterCount;
	}
	
	/* (non-Javadoc)
	 * @see facile.symtab.signature.MethodSignature#getParameterCount()
	 */
	public int getParameterCount() {
		return parameter.length;
	}

	@Override
	public Parameter[] getParameters() {
		if(parameter==null) return new Parameter[0];
		return parameter;
	}

	@Override
	public TypeRef getReturnType() {
		if(returnType==null && parameter!=null && parameter.length>0)
			return parameter[0].getTypeRef();
		
		return returnType;
	}
	
	public Parameter getReturnParameter() {
		if(returnType==null && parameter!=null && parameter.length>0)
			return parameter[0];
		
		return null;
	}
	
	public int getSentinelPosition() {
		return sentinelPosition;
	}
	
	public static MethodDefOrRefSignature decodeAndAttach(BasicTypesDirectory directory, MethodDefEntry methodDef) throws InvalidSignatureException {
		return new MethodDefOrRefSignature(directory, methodDef);
	}
	
	private MethodDefOrRefSignature(BasicTypesDirectory directory, MethodDefEntry methodDef) throws InvalidSignatureException {
	
		setBinarySignature(methodDef.getBinarySignature());		
		setDirectory(directory);
		nextToken();
		
		flags = (byte) currentToken;
		
		nextToken();
		
		if(ByteReader.testFlags(flags, CALL_CONV_GENERIC)) {
			genericParameterCount  = decodeIntegerInSignature();
			
			assert(methodDef.getGenericParameters()!=null);
			assert(genericParameterCount==methodDef.getGenericParameters().length);
		}
		
		parameterCount = decodeIntegerInSignature();
		
		parameter = methodDef.getParams();
		
		//adjust parameter count
		if(parameterCount!=0) {
			if(parameter==null || parameter.length==0) {
				parameter = new ParamEntry[parameterCount];
			} 
		} else if(parameter==null) {
			parameter = new ParamEntry[0];
		}
		
		//extract return type
		if(parameterCount+1==parameter.length) {
			returnType = null;
			returnType(parameter[0]);
			params(parameter, true);
		} else {
			returnType = returnType();
			params(parameter, false);
		}
	
		//add the extracted signature to the current method
		methodDef.setMethodSignature(this);
		methodDef.setParams(parameter);
	}
	
	@Override
	public String toString() {

		StringBuffer buffer = new StringBuffer(32);

		buffer.append("(");
		
		boolean first = true;
		if(parameter!=null) {
			for(int i=(returnType==null?1:0);i<parameter.length;i++) {
				if(parameter[i]!=null) {
					if(!first) buffer.append(", ");
				
					buffer.append(formatParameter(i));
					first = false;
				}
			}
		}
		
		buffer.append(") : ");
		if(returnType==null) {
			buffer.append(formatParameter(0));
		} else {
			if(returnType.getShortSystemName()!=null) {
				buffer.append(returnType.getShortSystemName());
			} else {
				buffer.append(returnType.getFullQualifiedName());
			}
		}


		return buffer.toString();
	}

	private String formatParameter(int i) {
		StringBuffer buffer = new StringBuffer();
		int flags = parameter[i].getFlags();
		
		//IMPROVE: Implement a ILAsm conforming representation
		if(ByteReader.testFlags(flags, Parameter.FLAGS_OUT)) {
			buffer.append("[out] ");
		} else if(ByteReader.testFlags(flags, Parameter.FLAGS_IN)) {
			buffer.append("[in] ");
		}
		if(ByteReader.testFlags(flags, Parameter.FLAGS_IS_OPTIONAL)) {
			buffer.append("[opt] ");
		}
		if(ByteReader.testFlags(flags, Parameter.FLAGS_UNUSED)) {
			buffer.append("[unused] ");
		}
		
//					if(ByteReader.testFlags(flags, Parameter.FLAGS_HAS_DEFAULT_VALUE)) {
//						buffer.append("[defval] ");
//					}
		
		if(parameter[i].getTypeRef()!=null) {
			if(parameter[i].getTypeRef().getShortSystemName()!=null) {
				buffer.append(parameter[i].getTypeRef().getShortSystemName());
			} else {
				buffer.append(parameter[i].getTypeRef().getFullQualifiedName());
			}
		}
		if(parameter[i].getMarshalSignature()!=null) {
			buffer.append(" marshal(");
			buffer.append(parameter[i].getMarshalSignature().toString());
			buffer.append(")");
		}
		
		if(parameter[i].getName()!=null) {
			buffer.append(" ");
			buffer.append(parameter[i].getName());
		}
		
		return buffer.toString();
	}

	public static MethodDefOrRefSignature decode(BasicTypesDirectory directory,
			byte[] binarySignature, int currentIndex) {
		return new MethodDefOrRefSignature(directory, binarySignature, currentIndex);
	}

	public static MethodDefOrRefSignature decodeAndAttach(BasicTypesDirectory directory, MemberRefEntry memberRef) throws InvalidSignatureException {
		return new MethodDefOrRefSignature(directory, memberRef);
	}
	
	private MethodDefOrRefSignature(BasicTypesDirectory directory, MemberRefEntry memberRef) throws InvalidSignatureException {
		
		//fix invalid references
		for(FixupElement element : fixupTable) {
			//search for the method reference pattern
			if(	memberRef.getName()!=null && memberRef.getName().equals(element.methodName) &&
				memberRef.getOwner().getTypeRef() != null &&
				memberRef.getOwner().getTypeRef().getFullQualifiedName()!=null &&
				memberRef.getOwner().getTypeRef().getFullQualifiedName().equals(element.fullQualifiedTypeName) &&
				ArrayUtils.arraysAreEqual(memberRef.getBinarySignature(), element.invalidSignature)) {
				
				//replace invalid signature with the corrected
				//version and stop further processing of the fixup table
				memberRef.setBinarySignature(element.correctedSignature);
				break;
			}
		}
		
		setBinarySignature(memberRef.getBinarySignature());
		setDirectory(directory);
		nextToken();

		flags = (byte) currentToken;
		
		nextToken();
		
		if(ByteReader.testFlags(flags, CALL_CONV_GENERIC)) {
			genericParameterCount = decodeIntegerInSignature();
		}
				
		parameterCount = decodeIntegerInSignature();
		
		returnType = returnType();

		parameter = new ParamEntry[parameterCount];
		
		sentinelPosition = params(parameter, false);
		
		//add the extracted signature to the current method
		memberRef.setMethodRefSignature(this);//.getOwnerClass().addMethodRefSignature(this);
	}
	
	public static MethodDefOrRefSignature decode(BasicTypesDirectory directory, StandAloneSigEntry standAlone) throws InvalidSignatureException {
		assert(standAlone!=null);
		assert(directory!=null);
		
		return new MethodDefOrRefSignature(directory, standAlone);
	}
	
	public MethodDefOrRefSignature(BasicTypesDirectory directory, StandAloneSigEntry standAlone) throws InvalidSignatureException {
		
		setBinarySignature(standAlone.getBinarySignature());		
		setDirectory(directory);
		nextToken();
		
		flags = (byte) currentToken;

		nextToken();
				
		parameterCount = decodeIntegerInSignature();
		
		returnType = returnType();

		parameter = new ParamEntry[parameterCount];
		
		sentinelPosition = params(parameter, false);
		
		standAlone.setMethodSignature(this);
	}

	public MethodDefOrRefSignature(BasicTypesDirectory directory,
			byte[] binarySignature, int currentIndex) {
		
		this.currentIndex= currentIndex-1;
		setBinarySignature(binarySignature);
		setDirectory(directory);
		nextToken();
		
		flags = (byte) currentToken;

		nextToken();
				
		parameterCount = decodeIntegerInSignature();
		
		returnType = returnType();

		parameter = new ParamEntry[parameterCount];
		
		sentinelPosition = params(parameter, false);
		
		setBinarySignature(ByteReader.getBytes(binarySignature, currentIndex, this.currentIndex-currentIndex));
	}

	public static MethodDefOrRefSignature decodeAndAttach(BasicTypesDirectory directory,
			StandAloneSigEntry standAlone) throws InvalidSignatureException {
		return new MethodDefOrRefSignature(directory, standAlone);
	}
	
	class FixupElement {
		protected String methodName;
		protected String fullQualifiedTypeName;
		protected byte [] invalidSignature;
		protected byte [] correctedSignature;
		
		public FixupElement(String methodName, String fullQualifiedTypeName, byte [] invalidSignature, byte [] correctedSignature) {
			this.methodName = methodName;
			this.fullQualifiedTypeName = fullQualifiedTypeName;
			this.invalidSignature = invalidSignature;
			this.correctedSignature = correctedSignature;
		}
	}
}

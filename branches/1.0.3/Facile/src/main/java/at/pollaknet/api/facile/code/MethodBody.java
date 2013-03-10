package at.pollaknet.api.facile.code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.pollaknet.api.facile.code.instruction.CilInstruction;
import at.pollaknet.api.facile.code.instruction.base.*;
import at.pollaknet.api.facile.code.instruction.object.Box;
import at.pollaknet.api.facile.code.instruction.object.Callvirt;
import at.pollaknet.api.facile.code.instruction.object.Castclass;
import at.pollaknet.api.facile.code.instruction.object.Cpobj;
import at.pollaknet.api.facile.code.instruction.object.Initobj;
import at.pollaknet.api.facile.code.instruction.object.Isinst;
import at.pollaknet.api.facile.code.instruction.object.Ldelem;
import at.pollaknet.api.facile.code.instruction.object.LdelemI;
import at.pollaknet.api.facile.code.instruction.object.LdelemI1;
import at.pollaknet.api.facile.code.instruction.object.LdelemI2;
import at.pollaknet.api.facile.code.instruction.object.LdelemI4;
import at.pollaknet.api.facile.code.instruction.object.LdelemI8;
import at.pollaknet.api.facile.code.instruction.object.LdelemR4;
import at.pollaknet.api.facile.code.instruction.object.LdelemR8;
import at.pollaknet.api.facile.code.instruction.object.LdelemRef;
import at.pollaknet.api.facile.code.instruction.object.LdelemU1;
import at.pollaknet.api.facile.code.instruction.object.LdelemU2;
import at.pollaknet.api.facile.code.instruction.object.LdelemU4;
import at.pollaknet.api.facile.code.instruction.object.Ldelema;
import at.pollaknet.api.facile.code.instruction.object.Ldfld;
import at.pollaknet.api.facile.code.instruction.object.Ldflda;
import at.pollaknet.api.facile.code.instruction.object.Ldlen;
import at.pollaknet.api.facile.code.instruction.object.Ldobj;
import at.pollaknet.api.facile.code.instruction.object.Ldsfld;
import at.pollaknet.api.facile.code.instruction.object.Ldsflda;
import at.pollaknet.api.facile.code.instruction.object.Ldstr;
import at.pollaknet.api.facile.code.instruction.object.Ldtoken;
import at.pollaknet.api.facile.code.instruction.object.Ldvirtftn;
import at.pollaknet.api.facile.code.instruction.object.Mkrefany;
import at.pollaknet.api.facile.code.instruction.object.Newarr;
import at.pollaknet.api.facile.code.instruction.object.Newobj;
import at.pollaknet.api.facile.code.instruction.object.Refanytype;
import at.pollaknet.api.facile.code.instruction.object.Refanyval;
import at.pollaknet.api.facile.code.instruction.object.Rethrow;
import at.pollaknet.api.facile.code.instruction.object.Sizeof;
import at.pollaknet.api.facile.code.instruction.object.Stelem;
import at.pollaknet.api.facile.code.instruction.object.StelemI;
import at.pollaknet.api.facile.code.instruction.object.StelemI1;
import at.pollaknet.api.facile.code.instruction.object.StelemI2;
import at.pollaknet.api.facile.code.instruction.object.StelemI4;
import at.pollaknet.api.facile.code.instruction.object.StelemI8;
import at.pollaknet.api.facile.code.instruction.object.StelemR4;
import at.pollaknet.api.facile.code.instruction.object.StelemR8;
import at.pollaknet.api.facile.code.instruction.object.StelemRef;
import at.pollaknet.api.facile.code.instruction.object.Stfld;
import at.pollaknet.api.facile.code.instruction.object.Stobj;
import at.pollaknet.api.facile.code.instruction.object.Stsfld;
import at.pollaknet.api.facile.code.instruction.object.Throw;
import at.pollaknet.api.facile.code.instruction.object.Unbox;
import at.pollaknet.api.facile.code.instruction.object.UnboxAny;
import at.pollaknet.api.facile.code.instruction.prefix.Constrained;
import at.pollaknet.api.facile.code.instruction.prefix.No;
import at.pollaknet.api.facile.code.instruction.prefix.ReadOnly;
import at.pollaknet.api.facile.code.instruction.prefix.Tail;
import at.pollaknet.api.facile.code.instruction.prefix.Unaligned;
import at.pollaknet.api.facile.code.instruction.prefix.Volatile;
import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.exception.InvalidMethodBodyException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.util.ByteReader;

/**
 * This class describes the body (CIL instructions, flags, ...) of a method.
 */
public class MethodBody {

	/**
	 * More sections flag is used to indicate that a data section
	 * (i.e. exception handling block) follows this method body
	 */
	public static final int FLAGS_MORE_SECTIONS 	= 0x08;
	
	/**
	 * Flag indicates that all locals are initialized with the default
	 * value or the default constructor.
	 */
	public static final int FLAGS_INIT_LOCAL 		= 0x10;
	
	//internal flags
	private static final int SIGNATURE_TINY_METHOD 	= 0x02;
	private static final int SIGNATURE_FAT_METHOD 	= 0x03;
	private static final int SIGNATURE_BIT_MASK 	= 0x03;
	
	//exception flags
	private static final int FLAGS_EXCEPTION_CLAUSE_MORE_SECTIONS 	= 0x80;
	private static final int FLAGS_EXCEPTION_CLAUSE_FAT 			= 0x40;

	//misc
	private final static int SIZE_OF_EXTRA_DATA_HEADER = 4;
	
	//properties of the method body
	private int flags = 0;
	private int headerSize = 0;
	private int maxStack = 8;
	private int codeSize = 0;
	
	//token pointing to the signature, describing all locals
	private long localVarSignatureToken = 0;
	
	//token stub for this method (completed via constructor)
	private long methodToken = 0x06000000L;

	//the method body as array of CIL instructions
	private CilInstruction[] cilInstructions;
	
	//the resolved types of the locals
	private TypeRef[] localVars;
	
	//all additional exception clauses
	private ExceptionClause [] exceptionClauses;
	
	/**
	 * Create an empty method body.
	 * @param methodNumber The continuous (unique) number of the method,
	 * used to create the identifying token of the method(body). 
	 */
	public MethodBody(int methodNumber) {
		//create a tiny method as default setup
		flags = 0x02;
		methodToken += methodNumber;
	}
	
	/**
	 * 
	 * @param metaModel The {@link at.pollaknet.api.facile.metamodel.MetadataModel}
	 * instance is used to generate a appropriate array of CIL instruction .
	 * @param container The code buffer containing the .net bytecode.
	 * @param relativeVirtualAddress The RVA of the method, which is used to locate
	 * the method inside the code buffer.
	 * @param methodNumber The continuous (unique) number of the method,
	 * used to create the identifying token of the method(body). 
	 * @throws InvalidMethodBodyException Occurs if the {@code byte} buffer
	 * contains invalid symbols.
	 */
	public MethodBody(MetadataModel metaModel, CilContainer container,
			long relativeVirtualAddress, int methodNumber) throws InvalidMethodBodyException {
		//initialize the metadata token
		methodToken += methodNumber;
		if (metaModel.isByteCodeNeed()) {
            //resolve the RVA
            int addressInByteBuffer = container.getPhysicalAddressOf(relativeVirtualAddress);

            //extract the data of the byte buffer
            extractBody(metaModel, container.getCodeBuffer(), addressInByteBuffer);
        }
	}


	private int extractBody(MetadataModel metaModel, byte[] data, int offset)
			throws InvalidMethodBodyException {

		//empty method
		if(offset <0 || offset>=data.length) {			
			throw new InvalidMethodBodyException(
					String.format("Virtual address %d is out of range [0,%d]",
							offset, data.length));
		}
		
		int byteCounter = offset;
		
		if(ByteReader.testFlags(data[byteCounter], SIGNATURE_BIT_MASK, SIGNATURE_FAT_METHOD)) {
			//fat method header
			byteCounter = processFatMethod(metaModel, data, byteCounter);
		} else  if(ByteReader.testFlags(data[byteCounter], SIGNATURE_BIT_MASK, SIGNATURE_TINY_METHOD)) {
			//tiny method header
			byteCounter = processTinyMethod(metaModel, data, byteCounter);
		} else {
			throw new InvalidMethodBodyException("Invalid method body");
		}
		return byteCounter-offset;
	}

	private int processFatMethod(MetadataModel metaModel, byte[] data, int byteCounter)
			throws InvalidMethodBodyException {
		flags = ByteReader.getUInt16(data, byteCounter);
		byteCounter += 2;
		
		//See ECMA 335 revision 4 - Partition II, 25.4.3: Size of this header
		//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=307&view=FitH
		//expressed as the count of 4-byte integers
		headerSize = (flags>>10)&0xfc; //multiply by 4 to get the byte-size
		
		if(headerSize!=12) {	
			throw new InvalidMethodBodyException("Size of Header is not allowed: "
					+ headerSize + " (expected 12)");
		}
		
		flags &= 0x0fff; //erase the upper 4 bit, containing the header-size
		
		boolean moreSections = ByteReader.testFlags(flags, FLAGS_MORE_SECTIONS);
		
		maxStack = ByteReader.getUInt16(data, byteCounter);
		byteCounter += 2;
		
		long size = ByteReader.getUInt32(data, byteCounter);
		assert(size<=ByteReader.INT32_MAX_VAL);
		codeSize = (int)size;
		byteCounter += 4;
		
		localVarSignatureToken = ByteReader.getUInt32(data, byteCounter);
		byteCounter += 4;
		
		cilInstructions = createInstructionList(metaModel, data, byteCounter, codeSize);
		
		//read the method body
		//body = ByteReader.getBytes(data, byteCounter, codeSize);
		
		assert(codeSize>=0);
		byteCounter += codeSize;
		
		//read extra data sections (exceptions)
		byteCounter += processExtraDataSections(metaModel, data, byteCounter, moreSections);
		
		return byteCounter;
	}

	private int processTinyMethod(MetadataModel metaModel, byte[] data, int byteCounter)
			throws InvalidMethodBodyException {
		flags = 0x02;
		
		codeSize = (ByteReader.getUInt8(data, byteCounter)>>2);
		byteCounter++;
		
		//assert(codeSize < 64);
		if(codeSize > 63) {	
			throw new InvalidMethodBodyException("Method body is too large for tiny header: " + codeSize + " (max. 63)");
		}
						
		cilInstructions = createInstructionList(metaModel, data, byteCounter, codeSize);
		
		assert(codeSize>=0);
		byteCounter += codeSize;
		
		return byteCounter;
	}

	private int processExtraDataSections(MetadataModel metaModel, byte[] data, int offset, boolean moreSections)
			throws InvalidMethodBodyException {
		
		if(!moreSections)
			return 0;
		
		int exceptionFlags;
		int byteCounter = offset;
		
		ArrayList<ExceptionClause> clauses = new ArrayList<ExceptionClause>();
		
		while(moreSections) {
			//continue with the method data section (starts 4 byte aligned)
			byteCounter = ByteReader.alingToDWord(byteCounter);
			
			exceptionFlags = ByteReader.getUInt8(data, byteCounter);
			byteCounter++;
							
			boolean fatClauses = ByteReader.testFlags(exceptionFlags, FLAGS_EXCEPTION_CLAUSE_FAT); 
		
			moreSections = ByteReader.testFlags(exceptionFlags, FLAGS_EXCEPTION_CLAUSE_MORE_SECTIONS); 
			
			int sectionSize;
			int numberOfClauses;
			
			if(!fatClauses) {
				//tiny exception clauses
				sectionSize = ByteReader.getUInt8(data, byteCounter);
				//skip the next two bytes (padding)
				byteCounter+=3;
				numberOfClauses = (sectionSize-SIZE_OF_EXTRA_DATA_HEADER) /
									ExceptionClause.SIZE_OF_TINY_EXCEPTION_CLAUSE;
				
			} else {
				//fat exception clauses
				//the section size is a 3 byte number
				sectionSize = ByteReader.getUInt16(data, byteCounter);
				byteCounter+=2;
				sectionSize += ByteReader.getUInt8(data, byteCounter)<<16;
				byteCounter++;
				numberOfClauses = (sectionSize-SIZE_OF_EXTRA_DATA_HEADER) /
									ExceptionClause.SIZE_OF_FAT_EXCEPTION_CLAUSE;
			}
			
			if((exceptionFlags&0x02)==0x02) {
				//skip the section if there is no exception data
				byteCounter += sectionSize-SIZE_OF_EXTRA_DATA_HEADER;
			} else {
				byteCounter = ByteReader.alingToDWord(byteCounter);
				
				//add the following clauses
				for(int i=0;i<numberOfClauses;i++) {
					ExceptionClause clause = new ExceptionClause(fatClauses);
					byteCounter += clause.extract(metaModel, data, byteCounter);
					clauses.add(clause);
				}
			}
		}
		
		int index=0;
		exceptionClauses = new ExceptionClause[clauses.size()];
		
		for(ExceptionClause clause: clauses) {
			exceptionClauses[index] = clause;
			index++;
		}
		
		return byteCounter-offset;
	}

	private static CilInstruction [] createInstructionList(MetadataModel metaModel, byte[] data, int offset, int length)
			throws InvalidByteCodeException {
		if(length<1) return new CilInstruction[0];
		
		List<CilInstruction> instructions = new ArrayList<CilInstruction>();
		
		int index=offset;
		CilInstruction currentInstruction = null;
		
		while(index<length+offset) {
			switch( data[index] ) {
				//one prefix byte basic instructions
				case Add.FIRST_TOKEN: 		currentInstruction = new Add(); break;
				case AddOvf.FIRST_TOKEN: 	currentInstruction = new AddOvf(); break;
				case AddOvfUn.FIRST_TOKEN: 	currentInstruction = new AddOvfUn(); break;
				case And.FIRST_TOKEN: 		currentInstruction = new And(); break;
				case Beq.FIRST_TOKEN: 		currentInstruction = new Beq(); break;
				case BeqS.FIRST_TOKEN: 		currentInstruction = new BeqS(); break;
				case Bge.FIRST_TOKEN: 		currentInstruction = new Bge(); break;
				case BgeS.FIRST_TOKEN: 		currentInstruction = new BgeS(); break;
				case BgeUn.FIRST_TOKEN: 	currentInstruction = new BgeUn(); break;
				case BgeUnS.FIRST_TOKEN: 	currentInstruction = new BgeUnS(); break;
				case Bgt.FIRST_TOKEN: 		currentInstruction = new Bgt(); break;
				case BgtS.FIRST_TOKEN: 		currentInstruction = new BgtS(); break;
				case BgtUn.FIRST_TOKEN: 	currentInstruction = new BgtUn(); break;
				case BgtUnS.FIRST_TOKEN: 	currentInstruction = new BgtUnS(); break;
				case Ble.FIRST_TOKEN: 		currentInstruction = new Ble(); break;
				case BleS.FIRST_TOKEN: 		currentInstruction = new BleS(); break;
				case BleUn.FIRST_TOKEN: 	currentInstruction = new BleUn(); break;
				case BleUnS.FIRST_TOKEN: 	currentInstruction = new BleUnS(); break;
				case Blt.FIRST_TOKEN: 		currentInstruction = new Blt(); break;
				case BltS.FIRST_TOKEN: 		currentInstruction = new BltS(); break;
				case BltUn.FIRST_TOKEN: 	currentInstruction = new BltUn(); break;
				case BltUnS.FIRST_TOKEN: 	currentInstruction = new BltUnS(); break;
				case BneUn.FIRST_TOKEN: 	currentInstruction = new BneUn(); break;
				case BneUnS.FIRST_TOKEN: 	currentInstruction = new BneUnS(); break;
				case Br.FIRST_TOKEN: 		currentInstruction = new Br(); break;
				case BrS.FIRST_TOKEN: 		currentInstruction = new BrS(); break;
				case Break.FIRST_TOKEN: 	currentInstruction = new Break(); break;
				case Brtrue.FIRST_TOKEN: 	currentInstruction = new Brtrue(); break;
				case BrtrueS.FIRST_TOKEN: 	currentInstruction = new BrtrueS(); break;
				case Brfalse.FIRST_TOKEN: 	currentInstruction = new Brfalse(); break;
				case BrfalseS.FIRST_TOKEN: 	currentInstruction = new BrfalseS(); break;
				case Call.FIRST_TOKEN: 		currentInstruction = new Call(); break;
				case Calli.FIRST_TOKEN: 	currentInstruction = new Calli(); break;
				case Ckfinite.FIRST_TOKEN: 	currentInstruction = new Ckfinite(); break;
				case ConvI.FIRST_TOKEN: 	currentInstruction = new ConvI(); break;
				case ConvI1.FIRST_TOKEN: 	currentInstruction = new ConvI1(); break;
				case ConvI2.FIRST_TOKEN: 	currentInstruction = new ConvI2(); break;
				case ConvI4.FIRST_TOKEN: 	currentInstruction = new ConvI4(); break;
				case ConvI8.FIRST_TOKEN: 	currentInstruction = new ConvI8(); break;
				case ConvOvfI.FIRST_TOKEN: 	currentInstruction = new ConvOvfI(); break;
				case ConvOvfI1.FIRST_TOKEN: 	currentInstruction = new ConvOvfI1(); break;
				case ConvOvfI1Un.FIRST_TOKEN: 	currentInstruction = new ConvOvfI1Un(); break;
				case ConvOvfI2.FIRST_TOKEN: 	currentInstruction = new ConvOvfI2(); break;
				case ConvOvfI2Un.FIRST_TOKEN: 	currentInstruction = new ConvOvfI2Un(); break;
				case ConvOvfI4.FIRST_TOKEN: 	currentInstruction = new ConvOvfI4(); break;
				case ConvOvfI4Un.FIRST_TOKEN: 	currentInstruction = new ConvOvfI4Un(); break;
				case ConvOvfI8.FIRST_TOKEN: 	currentInstruction = new ConvOvfI8(); break;
				case ConvOvfI8Un.FIRST_TOKEN: 	currentInstruction = new ConvOvfI8Un(); break;
				case ConvOvfIUn.FIRST_TOKEN: 	currentInstruction = new ConvOvfIUn(); break;
				case ConvOvfU.FIRST_TOKEN: 		currentInstruction = new ConvOvfU(); break;
				case ConvOvfU1.FIRST_TOKEN: 	currentInstruction = new ConvOvfU1(); break;
				case ConvOvfU1Un.FIRST_TOKEN: 	currentInstruction = new ConvOvfU1Un(); break;
				case ConvOvfU2.FIRST_TOKEN: 	currentInstruction = new ConvOvfU2(); break;
				case ConvOvfU2Un.FIRST_TOKEN: 	currentInstruction = new ConvOvfU2Un(); break;
				case ConvOvfU4.FIRST_TOKEN: 	currentInstruction = new ConvOvfU4(); break;
				case ConvOvfU4Un.FIRST_TOKEN: 	currentInstruction = new ConvOvfU4Un(); break;
				case ConvOvfU8.FIRST_TOKEN: 	currentInstruction = new ConvOvfU8(); break;
				case ConvOvfU8Un.FIRST_TOKEN: 	currentInstruction = new ConvOvfU8Un(); break;
				case ConvOvfUUn.FIRST_TOKEN: 	currentInstruction = new ConvOvfUUn(); break;
				case ConvR4.FIRST_TOKEN: 	currentInstruction = new ConvR4(); break;
				case ConvR8.FIRST_TOKEN: 	currentInstruction = new ConvR8(); break;
				case ConvRUn.FIRST_TOKEN: 	currentInstruction = new ConvRUn(); break;
				case ConvU.FIRST_TOKEN: 	currentInstruction = new ConvU(); break;
				case ConvU1.FIRST_TOKEN: 	currentInstruction = new ConvU1(); break;
				case ConvU2.FIRST_TOKEN: 	currentInstruction = new ConvU2(); break;
				case ConvU4.FIRST_TOKEN: 	currentInstruction = new ConvU4(); break;
				case ConvU8.FIRST_TOKEN: 	currentInstruction = new ConvU8(); break;
				case Div.FIRST_TOKEN: 		currentInstruction = new Div(); break;
				case DivUn.FIRST_TOKEN: 	currentInstruction = new DivUn(); break;
				case Dup.FIRST_TOKEN: 		currentInstruction = new Dup(); break;
				case Endfinally.FIRST_TOKEN:currentInstruction = new Endfinally(); break;
				case Jmp.FIRST_TOKEN: 		currentInstruction = new Jmp(); break;
				case Ldarg0.FIRST_TOKEN: 	currentInstruction = new Ldarg0(); break;
				case Ldarg1.FIRST_TOKEN: 	currentInstruction = new Ldarg1(); break;
				case Ldarg2.FIRST_TOKEN: 	currentInstruction = new Ldarg2(); break;
				case Ldarg3.FIRST_TOKEN: 	currentInstruction = new Ldarg3(); break;
				case LdargaS.FIRST_TOKEN: 	currentInstruction = new LdargaS(); break;
				case LdargS.FIRST_TOKEN: 	currentInstruction = new LdargS(); break;
				case LdcI4.FIRST_TOKEN: 	currentInstruction = new LdcI4(); break;
				case LdcI40.FIRST_TOKEN:	currentInstruction = new LdcI40(); break;
				case LdcI41.FIRST_TOKEN: 	currentInstruction = new LdcI41(); break;
				case LdcI42.FIRST_TOKEN:	currentInstruction = new LdcI42(); break;
				case LdcI43.FIRST_TOKEN: 	currentInstruction = new LdcI43(); break;
				case LdcI44.FIRST_TOKEN:	currentInstruction = new LdcI44(); break;
				case LdcI45.FIRST_TOKEN: 	currentInstruction = new LdcI45(); break;
				case LdcI46.FIRST_TOKEN:	currentInstruction = new LdcI46(); break;
				case LdcI47.FIRST_TOKEN: 	currentInstruction = new LdcI47(); break;
				case LdcI48.FIRST_TOKEN:	currentInstruction = new LdcI48(); break;
				case LdcI4M1.FIRST_TOKEN: 	currentInstruction = new LdcI4M1(); break;
				case LdcI4S.FIRST_TOKEN:	currentInstruction = new LdcI4S(); break;
				case LdcI8.FIRST_TOKEN: 	currentInstruction = new LdcI8(); break;
				case LdcR4.FIRST_TOKEN:		currentInstruction = new LdcR4(); break;
				case LdcR8.FIRST_TOKEN: 	currentInstruction = new LdcR8(); break;
				case LdindI.FIRST_TOKEN: 	currentInstruction = new LdindI(); break;
				case LdindI1.FIRST_TOKEN:	currentInstruction = new LdindI1(); break;
				case LdindI2.FIRST_TOKEN:	currentInstruction = new LdindI2(); break;
				case LdindI4.FIRST_TOKEN:	currentInstruction = new LdindI4(); break;
				case LdindI8.FIRST_TOKEN:	currentInstruction = new LdindI8(); break;
				case LdindR4.FIRST_TOKEN:	currentInstruction = new LdindR4(); break;
				case LdindR8.FIRST_TOKEN:	currentInstruction = new LdindR8(); break;
				case LdindRef.FIRST_TOKEN:	currentInstruction = new LdindRef(); break;
				case LdindU1.FIRST_TOKEN:	currentInstruction = new LdindU1(); break;
				case LdindU2.FIRST_TOKEN:	currentInstruction = new LdindU2(); break;
				case LdindU4.FIRST_TOKEN:	currentInstruction = new LdindU4(); break;
				case Ldloc0.FIRST_TOKEN:	currentInstruction = new Ldloc0(); break;
				case Ldloc1.FIRST_TOKEN:	currentInstruction = new Ldloc1(); break;
				case Ldloc2.FIRST_TOKEN:	currentInstruction = new Ldloc2(); break;
				case Ldloc3.FIRST_TOKEN:	currentInstruction = new Ldloc3(); break;
				case LdlocaS.FIRST_TOKEN:	currentInstruction = new LdlocaS(); break;
				case LdlocS.FIRST_TOKEN:	currentInstruction = new LdlocS(); break;
				case Ldnull.FIRST_TOKEN:	currentInstruction = new Ldnull(); break;
				case Leave.FIRST_TOKEN:		currentInstruction = new Leave(); break;
				case LeaveS.FIRST_TOKEN:	currentInstruction = new LeaveS(); break;
				case Mul.FIRST_TOKEN:		currentInstruction = new Mul(); break;
				case MulOvf.FIRST_TOKEN:	currentInstruction = new MulOvf(); break;
				case MulOvfUn.FIRST_TOKEN:	currentInstruction = new MulOvfUn(); break;
				case Neg.FIRST_TOKEN:		currentInstruction = new Neg(); break;
				case Nop.FIRST_TOKEN:		currentInstruction = new Nop(); break;
				case Not.FIRST_TOKEN:		currentInstruction = new Not(); break;
				case Or.FIRST_TOKEN:		currentInstruction = new Or(); break;
				case Pop.FIRST_TOKEN:		currentInstruction = new Pop(); break;				
				case Rem.FIRST_TOKEN:		currentInstruction = new Rem(); break;
				case RemUn.FIRST_TOKEN:		currentInstruction = new RemUn(); break;
				case Ret.FIRST_TOKEN:		currentInstruction = new Ret(); break;
				case Shl.FIRST_TOKEN:		currentInstruction = new Shl(); break;
				case Shr.FIRST_TOKEN:		currentInstruction = new Shr(); break;
				case ShrUn.FIRST_TOKEN:		currentInstruction = new ShrUn(); break;				
				case StargS.FIRST_TOKEN: 	currentInstruction = new StargS(); break;
				case StindI.FIRST_TOKEN: 	currentInstruction = new StindI(); break;
				case StindI1.FIRST_TOKEN: 	currentInstruction = new StindI1(); break;
				case StindI2.FIRST_TOKEN: 	currentInstruction = new StindI2(); break;
				case StindI4.FIRST_TOKEN: 	currentInstruction = new StindI4(); break;
				case StindI8.FIRST_TOKEN: 	currentInstruction = new StindI8(); break;
				case StindR4.FIRST_TOKEN: 	currentInstruction = new StindR4(); break;
				case StindR8.FIRST_TOKEN: 	currentInstruction = new StindR8(); break;
				case StindRef.FIRST_TOKEN: 	currentInstruction = new StindRef(); break;
				case Stloc0.FIRST_TOKEN: 	currentInstruction = new Stloc0(); break;
				case Stloc1.FIRST_TOKEN: 	currentInstruction = new Stloc1(); break;
				case Stloc2.FIRST_TOKEN: 	currentInstruction = new Stloc2(); break;
				case Stloc3.FIRST_TOKEN: 	currentInstruction = new Stloc3(); break;
				case StlocS.FIRST_TOKEN: 	currentInstruction = new StlocS(); break;
				case Sub.FIRST_TOKEN: 		currentInstruction = new Sub(); break;
				case SubOvf.FIRST_TOKEN: 	currentInstruction = new SubOvf(); break;
				case SubOvfUn.FIRST_TOKEN: 	currentInstruction = new SubOvfUn(); break;
				case Switch.FIRST_TOKEN: 	currentInstruction = new Switch(); break;
				case Xor.FIRST_TOKEN: 		currentInstruction = new Xor(); break;
				
				//one prefix byte object instructions
				case Box.FIRST_TOKEN: 		currentInstruction = new Box(); break;
				case Callvirt.FIRST_TOKEN: 	currentInstruction = new Callvirt(); break;
				case Castclass.FIRST_TOKEN: currentInstruction = new Castclass(); break;
				case Cpobj.FIRST_TOKEN: 	currentInstruction = new Cpobj(); break;
				case Isinst.FIRST_TOKEN: 	currentInstruction = new Isinst(); break;
				case Ldelem.FIRST_TOKEN: 	currentInstruction = new Ldelem(); break;
				case Ldelema.FIRST_TOKEN: 	currentInstruction = new Ldelema(); break;
				case LdelemI.FIRST_TOKEN: 	currentInstruction = new LdelemI(); break;
				case LdelemI1.FIRST_TOKEN: 	currentInstruction = new LdelemI1(); break;
				case LdelemI2.FIRST_TOKEN: 	currentInstruction = new LdelemI2(); break;
				case LdelemI4.FIRST_TOKEN: 	currentInstruction = new LdelemI4(); break;
				case LdelemI8.FIRST_TOKEN: 	currentInstruction = new LdelemI8(); break;
				case LdelemR4.FIRST_TOKEN: 	currentInstruction = new LdelemR4(); break;
				case LdelemR8.FIRST_TOKEN: 	currentInstruction = new LdelemR8(); break;
				case LdelemRef.FIRST_TOKEN: currentInstruction = new LdelemRef(); break;
				case LdelemU1.FIRST_TOKEN: 	currentInstruction = new LdelemU1(); break;
				case LdelemU2.FIRST_TOKEN: 	currentInstruction = new LdelemU2(); break;
				case LdelemU4.FIRST_TOKEN: 	currentInstruction = new LdelemU4(); break;
				case Ldfld.FIRST_TOKEN: 	currentInstruction = new Ldfld(); break;
				case Ldflda.FIRST_TOKEN: 	currentInstruction = new Ldflda(); break;
				case Ldlen.FIRST_TOKEN: 	currentInstruction = new Ldlen(); break;
				case Ldobj.FIRST_TOKEN: 	currentInstruction = new Ldobj(); break;
				case Ldsfld.FIRST_TOKEN: 	currentInstruction = new Ldsfld(); break;
				case Ldsflda.FIRST_TOKEN: 	currentInstruction = new Ldsflda(); break;
				case Ldstr.FIRST_TOKEN: 	currentInstruction = new Ldstr(); break;
				case Ldtoken.FIRST_TOKEN:	currentInstruction = new Ldtoken(); break;
				case Mkrefany.FIRST_TOKEN: 	currentInstruction = new Mkrefany(); break;
				case Newarr.FIRST_TOKEN: 	currentInstruction = new Newarr(); break;
				case Newobj.FIRST_TOKEN:	currentInstruction = new Newobj(); break;
				case Refanyval.FIRST_TOKEN: currentInstruction = new Refanyval(); break;
				case Stelem.FIRST_TOKEN: 	currentInstruction = new Stelem(); break;
				case StelemI.FIRST_TOKEN: 	currentInstruction = new StelemI(); break;
				case StelemI1.FIRST_TOKEN: 	currentInstruction = new StelemI1(); break;
				case StelemI2.FIRST_TOKEN: 	currentInstruction = new StelemI2(); break;
				case StelemI4.FIRST_TOKEN: 	currentInstruction = new StelemI4(); break;
				case StelemI8.FIRST_TOKEN: 	currentInstruction = new StelemI8(); break;
				case StelemR4.FIRST_TOKEN: 	currentInstruction = new StelemR4(); break;
				case StelemR8.FIRST_TOKEN: 	currentInstruction = new StelemR8(); break;
				case StelemRef.FIRST_TOKEN: currentInstruction = new StelemRef(); break;
				case Stfld.FIRST_TOKEN: 	currentInstruction = new Stfld(); break;
				case Stobj.FIRST_TOKEN: 	currentInstruction = new Stobj(); break;
				case Stsfld.FIRST_TOKEN: 	currentInstruction = new Stsfld(); break;
				case Throw.FIRST_TOKEN: 	currentInstruction = new Throw(); break;
				case Unbox.FIRST_TOKEN: 	currentInstruction = new Unbox(); break;
				case UnboxAny.FIRST_TOKEN: 	currentInstruction = new UnboxAny(); break;
				
				case (byte)0xfe:
					
					switch(data[index+1]) {
						//two prefix byte prefix instructions
						case Constrained.SECOND_TOKEN: 	currentInstruction = new Constrained(); break;
						case No.SECOND_TOKEN: 			currentInstruction = new No(); break;
						case ReadOnly.SECOND_TOKEN: 	currentInstruction = new ReadOnly(); break;
						case Tail.SECOND_TOKEN: 		currentInstruction = new Tail(); break;
						case Unaligned.SECOND_TOKEN: 	currentInstruction = new Unaligned(); break;
						case Volatile.SECOND_TOKEN: 	currentInstruction = new Volatile(); break;
						
						//two prefix byte basic instructions
						case ArgList.SECOND_TOKEN: 		currentInstruction = new ArgList(); break;
						case Ceq.SECOND_TOKEN: 			currentInstruction = new Ceq(); break;
						case Cgt.SECOND_TOKEN: 			currentInstruction = new Cgt(); break;
						case CgtUn.SECOND_TOKEN: 		currentInstruction = new CgtUn(); break;
						case Clt.SECOND_TOKEN: 			currentInstruction = new Clt(); break;
						case CltUn.SECOND_TOKEN: 		currentInstruction = new CltUn(); break;
						case Cpblk.SECOND_TOKEN: 		currentInstruction = new Cpblk(); break;
						case Endfilter.SECOND_TOKEN: 	currentInstruction = new Endfilter(); break;
						case Initblk.SECOND_TOKEN: 		currentInstruction = new Initblk(); break;
						case Ldarg.SECOND_TOKEN: 		currentInstruction = new Ldarg(); break;
						case Ldarga.SECOND_TOKEN: 		currentInstruction = new Ldarga(); break;
						case Ldftn.SECOND_TOKEN:		currentInstruction = new Ldftn(); break;
						case Ldloc.SECOND_TOKEN:		currentInstruction = new Ldloc(); break;						
						case Ldloca.SECOND_TOKEN:		currentInstruction = new Ldloca(); break;
						case Localloc.SECOND_TOKEN:		currentInstruction = new Localloc(); break;		
						case Starg.SECOND_TOKEN: 		currentInstruction = new Starg(); break;
						case Stloc.SECOND_TOKEN: 		currentInstruction = new Stloc(); break;
						
						//two prefix byte object instructions
						case Initobj.SECOND_TOKEN: 		currentInstruction = new Initobj(); break;
						case Ldvirtftn.SECOND_TOKEN: 	currentInstruction = new Ldvirtftn(); break;
						case Refanytype.SECOND_TOKEN:	currentInstruction = new Refanytype(); break;
						case Rethrow.SECOND_TOKEN: 		currentInstruction = new Rethrow(); break;
						case Sizeof.SECOND_TOKEN: 		currentInstruction = new Sizeof(); break;
						
					}
					break;
					
				default:
					//System.out.println(String.format("Length: %d current: 0x%02x", length, data[index]));
					assert(false) : "Unknown CIL Instruction";
					break;
			}
			
			assert(currentInstruction!=null);
			
			index += currentInstruction.parseInstruction(data, index, metaModel);
			//currentInstruction.setMetadataModel(metaModel);
			instructions.add(currentInstruction);
			
		}
		
		return instructions.toArray(new CilInstruction [0]);
	}

	/**
	 * Get the flags of the method body.
	 * @return The flags as {@code int}.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Get the byte size of the header.
	 * @return The size in bytes.
	 */
	public int getHeaderSize() {
		return headerSize;
	}

	/**
	 * Get the max required number of stack elements.
	 * @return Max number of stack elements.
	 */
	public int getMaxStack() {
		return maxStack;
	}

	/**
	 * Get the raw metadata token describing the locals.
	 * @return Raw metadata token as {@code long}.
	 */
	public long getLocalVarSignatureToken() {
		return localVarSignatureToken;
	}

	/**
	 * Get all defined exception clauses.
	 * @return An array of {@link at.pollaknet.api.facile.code.ExceptionClause} instances.
	 */
	public ExceptionClause[] getExceptionClauses() {
		if(exceptionClauses==null) return new ExceptionClause[0];
		return exceptionClauses;
	}

	/**
	 * Get the byte size of the .net bytecode.
	 * @return The number of bytes defined in the header.
	 */
	public long getCodeSize() {
		return codeSize;
	}


	/**
	 * Set the locals of the method. Please avoid doing that.
	 * @param localVars An array of types describing the locals.
	 */
	public void setLocalVars(TypeRef [] localVars) {
		this.localVars = localVars;
	}

	/**
	 * Get the types of the locals.
	 * @return An array of types describing the locals.
	 */
	public TypeRef[] getLocalVars() {
		if(localVars==null) return new TypeRef[0];
		return localVars;
	}

	/**
	 * Get the all instructions as defined in the body.
	 * @return An array of {@link at.pollaknet.api.facile.code.instruction.CilInstruction} instances.
	 */
	public CilInstruction[] getCilInstructions() {
		if(cilInstructions==null) return new CilInstruction[0];
		return cilInstructions;
	}
	
	/**
	 * Get the raw metadata token of the method(body).
	 * @return The raw metadata token as {@code long}.
	 */
	public long getMethodToken() {
		return methodToken;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(256);
		
		buffer.append("Method Body:");
		buffer.append(String.format("\n\t Flags: 0x%04x", flags));
		buffer.append("\tHeaderSize: ");
		buffer.append(headerSize);
		buffer.append(" bytes");
		buffer.append("\n\tCodeSize: ");
		buffer.append(codeSize);
		buffer.append(" bytes");
		buffer.append("\tMaxStack: ");
		buffer.append(maxStack);
		buffer.append(String.format("\tToken: 0x%08x", localVarSignatureToken));
		
		int index=0;
		if(localVars!=null) {
			buffer.append("\n\n\t Locals:");
			for(TypeRef typeRef :localVars) {
				buffer.append("\n\t\t");
				buffer.append(typeRef.getFullQualifiedName());
				buffer.append(" $");
				buffer.append(index);
				buffer.append(";");
				index++;
			}
		}
		buffer.append("\n\n\tCIL: ");
		
		int programCounter=0;
	
		for(CilInstruction i: getCilInstructions()) {
			buffer.append(String.format("\nIL_%04x: %s", programCounter, i.render(null)));
			programCounter += i.getByteSize();
		}

		
		if(exceptionClauses!=null) {
			buffer.append("\n\n\tExceptions: ");
			for(ExceptionClause ex :exceptionClauses) {
				buffer.append("\n\t\t");
				buffer.append(ex.toString());
			}
		}
		
		return buffer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + codeSize;
		result = prime * result + Arrays.hashCode(exceptionClauses);
		result = prime * result + flags;
		result = prime * result + maxStack;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodBody other = (MethodBody) obj;
		
		if (codeSize != other.codeSize)
			return false;
		if (flags != other.flags)
			return false;
		if (headerSize != other.headerSize)
			return false;
		if (maxStack != other.maxStack)
			return false;
		
		if(localVars==null) {
			if(other.localVars!=null) return false;
		} else if(other.localVars==null) {
			return false;
		} else if(localVars.length!=other.localVars.length) {
			return false;
		} else {
			String name, otherName;
			for(int i=0;i<localVars.length;i++) {
				//TODO: Check if "no name type" local corresponds with C# "var"
				name = localVars[i].getFullQualifiedName();
				otherName =other.localVars[i].getFullQualifiedName();
				if(name!=null && otherName!=null) {
					if(!name.equals(otherName)) {
						return false;
					}
				}
			}
		}

		if (!Arrays.equals(exceptionClauses, other.exceptionClauses))
			return false;
		
		//		if (localVarSignatureToken != other.localVarSignatureToken)
		//			return false;
		
		//		if (methodToken != other.methodToken)
		//			return false;

		if(cilInstructions!=null) {
			if(other.cilInstructions!=null) {
				
				if(cilInstructions.length==other.cilInstructions.length) {
					for(int i=0;i<cilInstructions.length;i++) {
						cilInstructions[i].equals(other.cilInstructions[i]);
					}
				}
			}
		} else if(other.cilInstructions!=null) {
			return false;
		}
		
		return true;
	}
	
	
}

package at.pollaknet.api.facile.code;

import at.pollaknet.api.facile.exception.InvalidMethodBodyException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.util.ByteReader;

public class ExceptionClause {

	
	public final static long FLAGS_CATCH_TYPE 	= 0x0000;
	public final static long FLAGS_FILTER_TYPE 	= 0x0001;
	public final static long FLAGS_FINAL_TYPE 	= 0x0002;
	public final static long FLAGS_FAULT_TYPE 	= 0x0004;
	
	public final static long FLAGS_BIT_MASK 	= 0x0007;
	
	private boolean fatClause;
	
	private long flags;
	private long tryOffset;
	private long tryLength;
	private long handlerOffset;
	private long handlerLength;
	//private long classToken;
	private long filterOffset;
	private TypeRef exceptionClass;
	
	public static final int SIZE_OF_TINY_EXCEPTION_CLAUSE = 12;
	public static final int SIZE_OF_FAT_EXCEPTION_CLAUSE = 24;
	
	//See ECMA 335 revision 4 - Partition II, 25.4.6 Exception handling clauses
	//http://www.ecma-international.org/publications/files/ECMA-ST/Ecma-335.pdf#page=309&view=FitH
	
	//Byte size specification:
	//
	//	 	TinyClause
	//	 	==========
	//	 	2	Flags.
	//	 	2	TryOffset Offset in bytes of try block from start of method body.
	//		1	TryLength Length in bytes of the try block
	//		2	HandlerOffset Location of the handler for this try block
	//		1	HandlerLength Size of the handler code in bytes
	//		4	ClassToken Meta data token for a catch type exception handler
	//			or	FilterOffset Offset in method body for filter type exception handler
	//
	//		FatClause
	//		=========
	//		4	Flags.
	//		4	TryOffset Offset in bytes of try block from start of method body.
	//		4	TryLength Length in bytes of the try block
	//		4	HandlerOffset Location of the handler for this try block
	//		4	HandlerLength Size of the handler code in bytes
	//		4	ClassToken Meta data token for a catch type exception handler
	//			or	FilterOffset Offset in method body for filter type exception handler
	 
	/**
	 * Create an empty exception.
	 * @param fatClauses Set to {@code true } if fat-format exception clauses are expected.
	 */
	public ExceptionClause(boolean fatClauses) {
		this.fatClause = fatClauses;
	}

	/**
	 * Extract the content and save to the current instance.
	 * @param metaModel The {@link at.pollaknet.api.facile.metamodel.MetadataModel} instance
	 * in order to resolve metadata items.
	 * @param data The raw {@code byte} buffer of the assembly.
	 * @param offset The total offset from the beginning.
	 * @return The number of processed bytes.
	 * @throws InvalidMethodBodyException Occurs if an unexpected byte has been detected.
	 */
	public int extract(MetadataModel metaModel, byte[] data, int offset)
			throws InvalidMethodBodyException {
		
		int byteCounter = offset;
		
		if(fatClause) {
			flags = ByteReader.getUInt32(data, byteCounter);
			byteCounter += 4;
			
			tryOffset = ByteReader.getUInt32(data, byteCounter);
			byteCounter += 4;
			
			tryLength = ByteReader.getUInt32(data, byteCounter);
			byteCounter += 4;
			
			handlerOffset = ByteReader.getUInt32(data, byteCounter);
			byteCounter += 4;
			
			handlerLength = ByteReader.getUInt32(data, byteCounter);
			byteCounter += 4;
			
			filterOffset = ByteReader.getUInt32(data, byteCounter);
			byteCounter += 4;
			
		} else {
			flags = ByteReader.getUInt16(data, byteCounter);
			byteCounter += 2;
			
			tryOffset = ByteReader.getUInt16(data, byteCounter);
			byteCounter += 2;
			
			tryLength = ByteReader.getUInt8(data, byteCounter);
			byteCounter += 1;
			
			handlerOffset = ByteReader.getUInt16(data, byteCounter);
			byteCounter += 2;
			
			handlerLength = ByteReader.getUInt8(data, byteCounter);
			byteCounter += 1;
			
			filterOffset = ByteReader.getUInt32(data, byteCounter);
			byteCounter += 4;
		}
		
		if(flags==FLAGS_CATCH_TYPE && filterOffset!=0) {
			assert(((int)filterOffset) == filterOffset);
			
			RenderableCilElement e = metaModel.getEntryByToken((int) filterOffset);
			
			if(e instanceof TypeRef) {
				exceptionClass = (TypeRef) e;
			} else {
				throw new InvalidMethodBodyException(
						String.format("Unable to identify exception class via token 0x%08x", filterOffset));
			}
		}
		
		return byteCounter - offset;
	}

	/**
	 * Get the flags, defined for this exception.
	 * @return The flags as {@code long}.
	 */
	public long getFlags() {
		return flags;
	}

	/**
	 * Offset of the try block, from the beginning of the method body.
	 * @return Offset in bytes.
	 */
	public long getTryOffset() {
		return tryOffset;
	}

	/**
	 * Length of the try block.
	 * @return The Length in bytes.
	 */
	public long getTryLength() {
		return tryLength;
	}

	/**
	 * Offset of the handler block, from the beginning of the method body.
	 * @return Offset in bytes.
	 */
	public long getHandlerOffset() {
		return handlerOffset;
	}

	/**
	 * Length of the handler block.
	 * @return The Length in bytes.
	 */
	public long getHandlerLength() {
		return handlerLength;
	}

	/**
	 * Get the exception class (if exception type matches).
	 * @return A {@link at.pollaknet.api.facile.symtab.symbols.TypeRef} or {@code null}.
	 */
	public TypeRef getExceptionType() {
		return exceptionClass;
	}

	/**
	 * Offset of the filter block, from the beginning of the method body.
	 * @return Offset in bytes.
	 */
	public long getFilterOffset() {
		return filterOffset;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + (int) (filterOffset ^ (filterOffset >>> 32));
		result = prime * result + (int) (handlerOffset ^ (handlerOffset >>> 32));
		result = prime * result + (int) (tryOffset ^ (tryOffset >>> 32));
		result = prime * result + (int) (flags ^ (flags >>> 32));
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
		ExceptionClause other = (ExceptionClause) obj;
		if (exceptionClass == null) {
			if (other.exceptionClass != null)
				return false;
		} else if (!exceptionClass.getFullQualifiedName().equals(
				other.exceptionClass.getFullQualifiedName()))
			return false;
		if (fatClause != other.fatClause)
			return false;
		if (filterOffset != other.filterOffset)
			return false;
		if (flags != other.flags)
			return false;
		if (handlerLength != other.handlerLength)
			return false;
		if (handlerOffset != other.handlerOffset)
			return false;
		if (tryLength != other.tryLength)
			return false;
		return tryOffset == other.tryOffset;
	}

}

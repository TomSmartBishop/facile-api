package at.pollaknet.api.facile.header.cli;

import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ArrayUtils;
import at.pollaknet.api.facile.util.ByteReader;

public class StreamHeader implements IDataHeader {

	public final static int MIN_HEADER_SIZE = 8;
	
	private long streamOffset;
	private long streamSize;
	private String name;
		
	private int headerSize = 0;

	//private boolean isUnaligned;
	private boolean isUnoptimized;
	
	public StreamHeader(/*boolean isUnaligned*/) {
		//this.isUnaligned = isUnaligned;
	}


	/* (non-Javadoc)
	 * @see facile.portableExecutable.IFileHeader#read(byte[], int)
	 */
	public int read (byte [] data, int offset) throws UnexpectedHeaderDataException {
		
		headerSize = offset;
		
		//int computedOffset = offset + 8;
		
		//every assembly is possibly not well aligned.
		//try to evaluate the position of the stream name string
		offset-=1;
		int hashPos = 0;
		int termPos = 0;
		do {
			offset++;
			hashPos = ArrayUtils.findInByteArray(data, offset+8, (byte) 35);
			termPos = ArrayUtils.findInByteArray(data, offset+8+hashPos, (byte) 0);
			
			byte [] buffer = ByteReader.getBytes(data, offset+8+hashPos, termPos);
			assert(buffer!=null);
			
			boolean valid = true;
			for(byte b: buffer) {
				if(b<' ') valid = false;
			}
				
			if(valid)
			  name = new String(buffer);
			
		} while(name==null || !name.startsWith("#") || name.length()<2); //isValidCandidateName(name)
		
		offset+=hashPos;
		
		streamOffset = ByteReader.getUInt32(data, offset); 		offset+=4;
		streamSize = ByteReader.getUInt32(data, offset); 		offset+=4;
		
		/*
		if(!isUnaligned) {
			isUnaligned = offset == computedOffset;
		}
		*/
		
		//find 0 of terminated string
		int length = ArrayUtils.findInByteArray(data, offset, (byte) 0);

		if(length>0) {
			name = new String(ByteReader.getBytes(data, offset, length));
		
			if(name.equals("#-")) {
				//isUnaligned = true;
				isUnoptimized = true;
			}
		}
		
		//increase byte-offset by the length of the string (including 0)
		offset += length;
		
		/*
		if(!isUnaligned) {
			int newOffset = ByteReader.alingToNextDWord(offset);
			
			if(data[newOffset-1]==0) {
				offset = newOffset;
			} else {
				isUnaligned = true;
			}
		}
		*/
		
		headerSize = offset - headerSize;
		
		assert(headerSize >= MIN_HEADER_SIZE);
		
		return headerSize;
	}

//
//	private static boolean isValidCandidateName(String nameCandidate) {
//		if(nameCandidate==null) return false;
//		
//		if(nameCandidate.equals("#-")) return true;
//		if(nameCandidate.equals("#~")) return true;
//		if(nameCandidate.equals("#US")) return true;
//		if(nameCandidate.equals("#GUID")) return true;
//		if(nameCandidate.equals("#Strings")) return true;
//		if(nameCandidate.equals("#Blob")) return true;
//		if(nameCandidate.equals("#!")) return true;
//		
//		return false;
//	}


	public int getSize() {
		return headerSize;
	}

	public long getStreamOffset() {
		return streamOffset;
	}

	public long getStreamSize() {
		return streamSize;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return String.format("Offset: ...0x%08x; Size: ...0x%08x; Name: ...%s", streamOffset, streamSize, name.replaceAll("\\p{Cntrl}","."));
	}

//
//	public boolean isUnaligned() {
//		return isUnaligned;
//	}
	
	public boolean isUnoptimized() {
		return isUnoptimized;
	}
}

package at.pollaknet.api.facile.header.cli;

import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ByteReader;


public class CliMetadataRootHeader implements IDataHeader  {

	private static final int MIN_HEADER_SIZE = 20;
	
	private final static long CLR_METADATA_SIGNATURE = 0x424a5342L;
	
	private long signature;
	private int majorVersion;
	private int minorVersion;
	private long extraDataOffset;
	private long versionStringLength;
	
	private String versionString;
	
	private int flags;
	private int numberOfStreams;
	
	private StreamHeader [] streams = null;
	
	private int headerSize = 0;

	private boolean isUnoptimized = false;

	
	public int read (byte [] data, int offset) throws UnexpectedHeaderDataException {
		
		headerSize = offset;
		
		signature = ByteReader.getUInt32(data, offset);
		
		if(signature!=CLR_METADATA_SIGNATURE)
			throw new UnexpectedHeaderDataException("Unknown signature: " +  //$NON-NLS-1$ 
					signature + "(expected " + CLR_METADATA_SIGNATURE + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		
		offset += 4;
		
		majorVersion = ByteReader.getUInt16(data, offset);			offset +=2;
		minorVersion = ByteReader.getUInt16(data, offset);			offset +=2;
		extraDataOffset = ByteReader.getUInt32(data, offset);		offset +=4;
		versionStringLength = ByteReader.getUInt32(data, offset);	offset +=4;
		
		assert(versionStringLength<ByteReader.INT32_MAX_VAL);
		assert(versionStringLength>0);
		
		versionString = new String(ByteReader.getBytes(data, offset, (int) versionStringLength));
		offset += versionStringLength;
		
		flags = ByteReader.getUInt16(data, offset);					offset +=2;
		numberOfStreams = ByteReader.getUInt16(data, offset);		offset +=2;
		
		streams = new StreamHeader[numberOfStreams];
		
		//boolean isUnaligned = false;
		
		for(int i=0;i<numberOfStreams;i++) {
			streams[i] = new StreamHeader(/*isUnaligned*/);
			offset += streams[i].read(data, offset);
			//isUnaligned = streams[i].isUnaligned();
			
			if(streams[i].isUnoptimized()) {
				isUnoptimized = true;
			}
		}
	
		headerSize = offset - headerSize;
		
		assert(headerSize >= MIN_HEADER_SIZE);
		
		return headerSize;
	}


	public int getSize() {
		return headerSize;
	}


	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}


	public int getFlags() {
		return flags;
	}


	public int getNumberOfStreams() {
		return numberOfStreams;
	}


	public long getExtraDataOffset() {
		return extraDataOffset;
	}


	public String getVersionString() {
		return versionString;
	}


	public StreamHeader[] getStreamHeaders() {
		return streams;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer("Metadata Root Header:");
		
		buffer.append(String.format("\n  Header Size: ..........%010d", headerSize));
		buffer.append(String.format("\n  Major Version: ........%010d", majorVersion));
		buffer.append(String.format("\n  Minor Version: ........%010d", minorVersion));
		buffer.append(String.format("\n  Extra Data Offset: ....0x%08x", extraDataOffset));
		buffer.append(String.format("\n  Version String: .......%s", versionString.replaceAll("\\p{Cntrl}","_")));
		buffer.append(String.format("\n  Flags: ................0x%08x", flags));
		buffer.append(String.format("\n  Number of Streams: ....%010d", numberOfStreams));
		buffer.append("\n\n  Stream Headers:");
		
		if(streams!=null) {
			for(StreamHeader h : streams) {
				buffer.append("\n    ");
				buffer.append(h.toString());
			}
		}
							 
		buffer.append("\n\n  Unoptimized Assembly: .").append(isUnoptimized);
		
		return buffer.toString();
	}


	public boolean isUnoptimized() {
		return isUnoptimized;
	}

}

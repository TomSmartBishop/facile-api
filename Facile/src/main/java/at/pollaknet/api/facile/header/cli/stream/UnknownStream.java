package at.pollaknet.api.facile.header.cli.stream;

import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;
import at.pollaknet.api.facile.header.IDataHeader;
import at.pollaknet.api.facile.util.ArrayUtils;

public class UnknownStream implements IDataHeader {

	private String name;
	private int size;
	private int offset;
	private byte [] content;
	private boolean valid;
	
	public UnknownStream(String name, int streamSize) {
		this.name = name;
		this.size = streamSize;
		this.valid = false;
	}

	@Override
	public int read(byte[] data, int offset)
			throws UnexpectedHeaderDataException {
		
		//do not read content of unknown streams when exceeding file size limit
		if(size+offset>data.length) {
			return 0;
		}
		
		//copy the raw content
		content = new byte[size];
		System.arraycopy(data, offset, content, 0, size);
		
		//remember the offset in the file buffer
		this.offset = offset;
		
		this.valid = true;
		
		//return the size of the copied buffer
		return size;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public String toString() {
		return getName() + " Stream";
	}
	
	public String toExtendedString() {
		
		if(!valid) return toString() + " (invalid)";
		
		StringBuffer buffer = new StringBuffer(size*4+32);
		
		//start with the name of the stream
		buffer.append(toString());
		buffer.append(" (Unknown Stream Type):\n");
		
		//format stream content
		buffer.append(ArrayUtils.formatAsHexTable(offset, content, true));
		
		//return as string
		return buffer.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public byte [] getContent() {
		return content;
	}

}

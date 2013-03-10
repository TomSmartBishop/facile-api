package at.pollaknet.api.facile;

import java.util.ArrayDeque;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * The {@link FacileLogHandler} extends the abstract {@link java.util.logging.Handler} and simply
 * writes the log messages to a buffer or outputs it to the console on demand. This log handler
 * is not thread safe.
 * 
 * @author Thomas Pollak
 * <p/>Email: <i>http://code.google.com/p/facile-api/people/detail?u=103590059941737035763</i>
 */
public class FacileLogHandler extends Handler {

	private ArrayDeque<String> logBuffer = new ArrayDeque<String>();
	private boolean intermediate = false;
	private boolean isClosed = false;
	private int maxNumLogLines = 64*1024;
	private int numLogLines = 0;

	/**
	 * Create a new log handler which writes all message into a buffer.
	 */
	public FacileLogHandler() {
		logBuffer = new ArrayDeque<String>();
	}
	
	/**
	 * Create a new log handler which writes all message into a buffer and
	 * outputs them to the console if the intermediate mode is set.
	 * @param intermediate Set to {@code true} if the log messages
	 * should appear on the console.
	 */
	public FacileLogHandler(boolean intermediate) {
		this();
		this.intermediate = intermediate;
	}
	
	/**
	 * Enable or disable intermediate mode.
	 * @param intermediate Set to {@code true} if the log messages
	 * should appear on the console.
	 */
	public void SetIntermediate(boolean intermediate) {
		this.intermediate = intermediate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	@Override
	public void close() {
		isClosed = true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	@Override
	public void flush() {
		if(intermediate)
			System.out.flush();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(LogRecord record) {
		if(!isClosed) {
			String msg = record.getLevel().toString() + ": " + record.getMessage() + "\n";
			
			if(intermediate)
				System.out.print(msg);
			
			logBuffer.add(msg);
			numLogLines++;
			
			if(numLogLines>maxNumLogLines)
				logBuffer.poll();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		//size approximation: average of 32 character per line
		int approximatedSize = numLogLines>maxNumLogLines ? maxNumLogLines*32 : numLogLines*32;
		
		StringBuffer buffer = new StringBuffer(approximatedSize);
		
		for(String line : logBuffer) {
			buffer.append(line);
		}
		
		return buffer.toString();
	}

}
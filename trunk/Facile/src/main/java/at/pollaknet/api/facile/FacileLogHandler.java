package at.pollaknet.api.facile;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * The {@link FacileLogHandler} extends the abstract
 * {@link java.util.logging.Handler} and simply writes the log messages to a
 * buffer or outputs it to the console on demand.
 *
 * @author Thomas Pollak
 *         <p/>
 *         Email: <i>http://code.google.com/p/facile-api/people/detail?u=
 *         103590059941737035763</i>
 */
public class FacileLogHandler extends Handler {

	private static final int CAPACITY = 1024;
	private StringBuffer logBuffer;
	private boolean intermediate = false;
	private boolean isClosed = false;

	/**
	 * Create a new log handler which writes all message into a buffer.
	 */
	public FacileLogHandler() {
		logBuffer = new StringBuffer(CAPACITY);
	}

	/**
	 * Create a new log handler which writes all message into a buffer and
	 * outputs them to the console if the intermediate mode is set.
	 * 
	 * @param intermediate
	 *            Set to {@code true} if the log messages should appear on the
	 *            console.
	 */
	public FacileLogHandler(boolean intermediate) {
		this();
		this.intermediate = intermediate;
	}

	/**
	 * Enable or disable intermediate mode.
	 * 
	 * @param intermediate
	 *            Set to {@code true} if the log messages should appear on the
	 *            console.
	 */
	public void SetIntermediate(boolean intermediate) {
		this.intermediate = intermediate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.Handler#close()
	 */
	@Override
	public void close() {
		isClosed = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.Handler#flush()
	 */
	@Override
	public void flush() {
		logBuffer = new StringBuffer(CAPACITY);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(LogRecord record) {
		if (!isClosed) {
			String msg = record.getLevel().toString() + ": "
					+ record.getMessage() + "\n";

			if (intermediate) {
				System.out.print(msg);
			}
			if (logBuffer.length() >= CAPACITY) {
				flush();
			}
			logBuffer.append(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return logBuffer.toString();
	}

}
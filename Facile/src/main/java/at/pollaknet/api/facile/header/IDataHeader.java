package at.pollaknet.api.facile.header;

import at.pollaknet.api.facile.exception.CoffPeDataNotFoundException;
import at.pollaknet.api.facile.exception.DotNetContentNotFoundException;
import at.pollaknet.api.facile.exception.UnexpectedHeaderDataException;

public interface IDataHeader {

	/**
	 * Read a data structure from the given {@code offset} in the buffer {@code data}.
	 * @param data The byte buffer which contains the data structure.
	 * @param offset The offset where to start-
	 * @return The number of read bytes.
	 * @throws UnexpectedHeaderDataException If a check on a data element in the
	 * data structure fails.
	 * @throws DotNetContentNotFoundException If a data element exposes that the
	 * containing buffer is not .Net assembly.
	 * @throws CoffPeDataNotFoundException If a data element exposes that the
	 * containing buffer is not a PE (portable executable).
	 */
	public abstract int read(byte[] data, int offset)
			throws UnexpectedHeaderDataException,
			CoffPeDataNotFoundException;
	
	/**
	 * Returns the size of the read bytes.
	 * @return The size in bytes.
	 */
	public abstract int getSize();

}
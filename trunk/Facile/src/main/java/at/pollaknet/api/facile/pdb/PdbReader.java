package at.pollaknet.api.facile.pdb;

import java.io.FileNotFoundException;

import at.pollaknet.api.facile.exception.NativeImplementationException;

public interface PdbReader {

	public abstract void open(String pathToPdbFile)
			throws NativeImplementationException, FileNotFoundException, UnexpectedPdbContent;
	
	public abstract DebugInformation getLineNumbersByRVA(long relativeVirtualAddress);
	
	public abstract boolean close();
}
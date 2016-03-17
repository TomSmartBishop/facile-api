package at.pollaknet.api.facile.pdb;

public interface InstructionInfo {

	/**
	 * Get the line number, where the symbol definition starts.
	 * @return The first line number.
	 */
	public abstract long getLineNumber();

	/**
	 * Get the column number, where the symbol definition starts.
	 * @return The start column line number.
	 */
	public abstract long getColNumber();

	/**
	 * Get the column number, where the symbol definition ends.
	 * @return The end column line number.
	 */
	public abstract long getColEndNumber();

	/**
	 * The value of the program counter, where the symbol definition starts.
	 * (The program counter value is equal to the number of bytes used by the
	 * CIL instructions)
	 * @return The program counter value inside a method.
	 */
	public abstract long getProgramCounter();

}
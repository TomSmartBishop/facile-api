package at.pollaknet.api.facile.pdb;

public interface DebugInformation {

	/**
	 * Get the name of the source file, where the symbol has been defined.
	 * @return The name and path of the source file.
	 */
	public abstract String getSourceFileName();

	/**
	 * Get an array of all line number information about each instruction of the
	 * original .net language.
	 * @return An array of {@link at.pollaknet.api.facile.pdb.InstructionInfo} instances.
	 */
	public abstract InstructionInfo[] getInstructionInfos();

}
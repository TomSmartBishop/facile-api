package at.pollaknet.api.facile.dia;

public class Instruction implements InstructionInfo {
	private long lineNumber;
	private long colNumber;
	private long colEndNumber;
	private long programCounter;
	
	public Instruction() {	
	}
	
	public Instruction(long lineNumber, long colNumber, long colEndNumber, long programCounter) {
		this.lineNumber = lineNumber;
		this.colNumber = colNumber;
		this.colEndNumber = colEndNumber;
		this.programCounter = programCounter;
	}

	/* (non-Javadoc)
	 * @see dia.JavaDIA.SymbolInformation#getLineNumber()
	 */
	public long getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	/* (non-Javadoc)
	 * @see dia.JavaDIA.SymbolInformation#getColNumber()
	 */
	public long getColNumber() {
		return colNumber;
	}

	public void setColNumber(long colNumber) {
		this.colNumber = colNumber;
	}

	/* (non-Javadoc)
	 * @see dia.JavaDIA.SymbolInformation#getColEndNumber()
	 */
	public long getColEndNumber() {
		return colEndNumber;
	}

	public void setColEndNumber(long colEndNumber) {
		this.colEndNumber = colEndNumber;
	}

	/* (non-Javadoc)
	 * @see dia.JavaDIA.SymbolInformation#getProgramCounter()
	 */
	public long getProgramCounter() {
		return programCounter;
	}

	public void setProgramCounter(long programCounter) {
		this.programCounter = programCounter;
	}
}

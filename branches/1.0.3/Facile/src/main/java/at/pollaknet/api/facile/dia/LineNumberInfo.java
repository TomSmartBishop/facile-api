package at.pollaknet.api.facile.dia;

import java.util.ArrayList;

public class LineNumberInfo implements DebugInformation {

	private String sourceFileName;
	
	private ArrayList<InstructionInfo> instructionList = new ArrayList<InstructionInfo>();
	
	public void addInstruction(long lineNumber, long colNumber, long colEndNumber, long programCounter) {
		instructionList.add(new Instruction(lineNumber,colNumber,colEndNumber,programCounter));
	}

	/* (non-Javadoc)
	 * @see dia.JavaDIA.DebugInformation#getSourceFileName()
	 */
	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	/* (non-Javadoc)
	 * @see dia.JavaDIA.DebugInformation#getInstructionList()
	 */
	public InstructionInfo [] getInstructionInfos() {
		if(instructionList==null || instructionList.size()==0) return new InstructionInfo[0];
		return instructionList.toArray(new InstructionInfo[0]);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(instructionList.size()*22);
		
		if(sourceFileName!=null) {
			buffer.append(sourceFileName);
		} else {
			buffer.append("<Unknown source file>");
		}
		buffer.append(" {");
		
		for(InstructionInfo s : instructionList) {
			buffer.append(String.format("PC(0x%x)@Line(%d) ", s.getProgramCounter(), s.getLineNumber()));
		}
		
		buffer.append("}");
		
		return buffer.toString();
	}
	
}

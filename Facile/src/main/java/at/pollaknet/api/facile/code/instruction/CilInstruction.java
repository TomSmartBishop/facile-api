package at.pollaknet.api.facile.code.instruction;

import at.pollaknet.api.facile.exception.InvalidByteCodeException;
import at.pollaknet.api.facile.metamodel.MetadataModel;
import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.renderer.LanguageRenderer;

public abstract class CilInstruction implements RenderableCilElement {
	
	/**
	 * The default value for the first token, if the instruction consist of two bytes
	 * (also default value for the second token, if the instruction takes one byte).
	 */
	public static final byte EXTENDED_INSTRUCTION_TOKEN = (byte) 0xfe;

	/**
	 * Parse the current instruction from the specified buffer. 
	 */
	public abstract int parseInstruction(byte [] buffer, int index, MetadataModel metaModel)
		throws InvalidByteCodeException;

	/**
	 * Render the instruction as {@code String} for the specified language.
	 */
	public abstract String render(LanguageRenderer renderer);
	
	/**
	 * Render the instruction for the specified language as reference.
	 */
	public String renderAsReference(LanguageRenderer renderer) {
		return render(renderer);
	}
	
	/**
	 * Get the number of bytes, used by the instruction.
	 * @return The number of bytes.
	 */
	public abstract byte getByteSize();

	protected static void ensure(byte [] buffer, int index, byte condition) throws InvalidByteCodeException {
		if(condition!=buffer[index]) throw new InvalidByteCodeException(buffer[index], condition);
	}

	/**
	 * Get the first token of the instruction.
	 * @return The first byte, representing the first token.
	 */
	public abstract byte getFirstToken();
	
	/**
	 * Get the second token of the instruction.
	 * @return The second byte, representing the first token.
	 */
	public abstract byte getSecondToken();
	
	/**
	 * Get the jump target, which is only defined if the instructions
	 * deals with a target.
	 * @return The jump target or 0.
	 */
	public int getJumpTarget() {
		return 0;
	}
	
	/**
	 * Get the metadata item as {@link at.pollaknet.api.facile.metamodel.RenderableCilElement}
	 * instance, if the instruction deals with a metadata item.
	 * @return The metadata item or {@code null}.
	 */
	public RenderableCilElement getReferencedElement() {
		return null;
	}

	/**
	 * Get the raw metadata token, if the instruction deals with a metadata item.
	 * @return The raw metadata token (which is usually represented as {@code long})
	 * or -1 if not defined.
	 */
	public int getMetadataToken() {
		return -1;
	}

	@Override
	public abstract boolean equals(Object o);
	
	@Override
	public abstract int hashCode();
}

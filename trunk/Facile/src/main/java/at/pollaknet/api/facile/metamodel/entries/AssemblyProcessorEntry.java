package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.misc.AssemblyProcessor;

public class AssemblyProcessorEntry implements RenderableCilElement, AssemblyProcessor {

	private long processor;

	public long getProcessor() {
		return processor;
	}

	public void setProcessor(long processor) {
		this.processor = processor;
	}
	
	@Override
	public String toString() {
		return "AssemblyProcessor: " + processor;
	}

	@Override
	public String render(LanguageRenderer renderer) {
		return null;
	}

	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}

	@Override
	public int hashCode() {
		return 31 + (int) (processor ^ (processor >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssemblyProcessorEntry other = (AssemblyProcessorEntry) obj;
		if (processor != other.processor)
			return false;
		return true;
	}
	
	
}

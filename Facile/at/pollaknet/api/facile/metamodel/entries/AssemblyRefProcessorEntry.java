package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.misc.AssemblyProcessorRef;

public class AssemblyRefProcessorEntry implements RenderableCilElement, AssemblyProcessorRef {

	private long processor;
	private AssemblyRefEntry assemblyRef;
	
	public long getProcessor() {
		return processor;
	}
	public void setProcessor(long processor) {
		this.processor = processor;
	}
	public AssemblyRefEntry getAssemblyRef() {
		return assemblyRef;
	}
	public void setAssemblyRef(AssemblyRefEntry assemblyRef) {
		this.assemblyRef = assemblyRef;
	}
	
	@Override
	public String toString() {
		return String.format("AssemblyRefProcessor: %d AssemblyRef: %s",
				processor, assemblyRef.getName());
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
		AssemblyRefProcessorEntry other = (AssemblyRefProcessorEntry) obj;
		if (assemblyRef != other.assemblyRef)
			return false;
		if (processor != other.processor)
			return false;
		return true;
	}
	
}

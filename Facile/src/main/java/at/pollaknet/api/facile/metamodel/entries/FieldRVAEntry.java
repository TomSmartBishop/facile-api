package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.renderer.LanguageRenderer;

public class FieldRVAEntry implements RenderableCilElement {

	private long relativeVirtualAddress;
	private FieldEntry field;
	
	public long getRelativeVirtualAddress() {
		return relativeVirtualAddress;
	}
	public void setRelativeVirtualAddress(long virtualAddress) {
		this.relativeVirtualAddress = virtualAddress;
	}
	public FieldEntry getField() {
		return field;
	}
	public void setField(FieldEntry field) {
		this.field = field;
	}
	
	@Override
	public String toString() {
		return String.format("FieldRVA: %s VA:0x%04x",
				field.getName(), relativeVirtualAddress);
	}
	@Override
	public String render(LanguageRenderer renderer) {
		
		return null;
	}
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		
		return null;
	}
	
//	@Override
//	public int hashCode() {
//		return 31 + (int) (relativeVirtualAddress ^ (relativeVirtualAddress >>> 32));
//	}
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		FieldRVAEntry other = (FieldRVAEntry) obj;
//		if (relativeVirtualAddress != other.relativeVirtualAddress)
//			return false;
//		return true;
//	}
}

package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.renderer.LanguageRenderer;

public class FieldLayoutEntry implements RenderableCilElement {

	private long offset;
	private FieldEntry field;
	
	public long getOffset() {
		return offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
	public FieldEntry getField() {
		return field;
	}
	public void setField(FieldEntry field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return String.format("FieldLayout: %s (Offset: 0x%x)",
				field.getName(), offset);
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
		return 31 + (int) (offset ^ (offset >>> 32));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return offset == ((FieldLayoutEntry) obj).offset;

	}
}

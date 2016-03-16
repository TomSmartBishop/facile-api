package at.pollaknet.api.facile.metamodel.entries;

import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasConstant;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.Constant;
import at.pollaknet.api.facile.util.ArrayUtils;

public class ConstantEntry extends Constant implements RenderableCilElement {

	private int type;
	private IHasConstant parent;
	private byte [] value;
	
	public int getElementTypeKind() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public IHasConstant getParent() {
		return parent;
	}
	public void setParent(IHasConstant parent) {
		this.parent = parent;
	}
	public byte[] getValue() {
		return value;
	}
	public void setValue(byte[] value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("Constant: %s Type: 0x%x Value %s",
				parent.getName(), type, value==null?"[not set]":ArrayUtils.formatByteArray(value));
	}
	
	@Override
	public String render(LanguageRenderer renderer) {
		return renderer.render(this);
	}
	
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}
	
	@Override
	public int hashCode() {
		return 31 * (31 + type) + Arrays.hashCode(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConstantEntry other = (ConstantEntry) obj;

		if (type != other.type)
			return false;
		return Arrays.equals(value, other.value);
	}
	
	@Override
	public int compareTo(Constant c) {
		return c.getElementTypeKind() - type;
	}
}

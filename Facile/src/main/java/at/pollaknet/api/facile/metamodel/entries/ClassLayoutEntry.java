package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.ClassLayout;

public class ClassLayoutEntry implements RenderableCilElement, ClassLayout {

	private int packingSize;
	private long classSize;
	private TypeDefEntry parent;
	
	public int getPackingSize() {
		return packingSize;
	}
	public void setPackingSize(int packingSize) {
		this.packingSize = packingSize;
	}
	public long getClassSize() {
		return classSize;
	}
	public void setClassSize(long classSize) {
		this.classSize = classSize;
	}
	public TypeDefEntry getParent() {
		return parent;
	}
	public void setParent(TypeDefEntry parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return String.format("ClassLayout: %s (PackingSize: %d ClassSize: %d)",
				parent.getFullQualifiedName(), packingSize, classSize);
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
		final int prime = 31;
		int result = prime + (int) (classSize ^ (classSize >>> 32));
		return prime * result + packingSize;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassLayoutEntry other = (ClassLayoutEntry) obj;
		if (classSize != other.classSize)
			return false;
		return packingSize == other.packingSize;

	}
	
	@Override
	public int compareTo(ClassLayout c) {
		return (int)(c.getClassSize() - classSize);
	}

}

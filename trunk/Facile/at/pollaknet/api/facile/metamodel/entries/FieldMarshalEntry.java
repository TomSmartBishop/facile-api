package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasFieldMarshal;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.util.ArrayUtils;

public class FieldMarshalEntry implements RenderableCilElement {

	private IHasFieldMarshal parent;
	private byte [] nativeType;
	
	public IHasFieldMarshal getParent() {
		return parent;
	}
	public void setParent(IHasFieldMarshal parent) {
		this.parent = parent;
	}
	public byte[] getNativeType() {
		return nativeType;
	}
	public void setNativeType(byte[] nativeType) {
		this.nativeType = nativeType;
	}
	
	@Override
	public String toString() {
		return String.format("FieldMarshal: %s (NativeType: %s)",
				parent.getName(), nativeType==null?"[not set]":ArrayUtils.formatByteArray(nativeType));
	}
	@Override
	public String render(LanguageRenderer renderer) {
		return null;
	}
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}

}

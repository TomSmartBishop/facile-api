package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.NativeImplementation;

public interface IMemberForwarded extends RenderableCilElement {
	
	public abstract void setNativeImplementation(NativeImplementation nativeImpl);
	
	public abstract NativeImplementation getNativeImplementation();
	
	public abstract String getName();
	
	public abstract Field getField();
	
	public abstract Method getMethod();
	
	//public abstract boolean equals(Object obj);
	
	//public abstract int hashCode();
}

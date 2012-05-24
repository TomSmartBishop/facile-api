package at.pollaknet.api.facile.metamodel.entries.aggregation;

import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.Parameter;

public interface IHasFieldMarshal  {
	public abstract String getName();

	public void setBinaryMarshalTypeSignature(byte [] type);
	
	public abstract byte [] getBinaryMarshalTypeSignature();

	public abstract Field getField();

	public abstract Parameter getParameter();
}

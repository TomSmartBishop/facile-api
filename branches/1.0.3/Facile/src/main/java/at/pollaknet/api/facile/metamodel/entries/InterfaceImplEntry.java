package at.pollaknet.api.facile.metamodel.entries;


import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.ITypeDefOrRef;
import at.pollaknet.api.facile.renderer.LanguageRenderer;

public class InterfaceImplEntry extends AbstractAttributable implements
		IHasCustomAttribute {

	private TypeDefEntry implementedClass;
	private ITypeDefOrRef interfaze;	
	
	/*
	private Type interfaceType;
	private TypeRef interfaceTypeRef;
	private TypeSpec interfaceTypeSpec;
*/
	public TypeDefEntry getImplementationClass() {
		return implementedClass;
	}
	
	public void setImplementationClass(TypeDefEntry implementedClass) {
		this.implementedClass = implementedClass;
	}
	
	public ITypeDefOrRef getInterface() {
		return interfaze;
	}
	
	public void setInterface(ITypeDefOrRef interfaze) {
		this.interfaze = interfaze;
	}

	@Override
	public String getName() {
		return interfaze.getName();
	}
	
	@Override
	public String toString() {
		return String.format("InterfaceImpl: %s Interface: %s",
				implementedClass.getFullQualifiedName(), interfaze.getFullQualifiedName());
	}

//	@Override
//	public TypeRef getTypeRef() {
//		if(interfaze==null) return null;
//		return interfaze.getTypeRef();
//	}
//
//	@Override
//	public String render(LanguageRenderer renderer) {
//		return renderer.render(this);
//	}
	
	@Override
	public String renderAsReference(LanguageRenderer renderer) {
		return null;
	}

	@Override
	public String render(LanguageRenderer renderer) {
		return null;
	}
}



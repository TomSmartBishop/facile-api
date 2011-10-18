package at.pollaknet.api.facile.renderer;

import java.io.IOException;

import at.pollaknet.api.facile.metamodel.entries.StandAloneSigEntry;
import at.pollaknet.api.facile.symtab.symbols.ClassLayout;
import at.pollaknet.api.facile.symtab.symbols.Constant;
import at.pollaknet.api.facile.symtab.symbols.Event;
import at.pollaknet.api.facile.symtab.symbols.ExportedType;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.FieldLayout;
import at.pollaknet.api.facile.symtab.symbols.Instance;
import at.pollaknet.api.facile.symtab.symbols.MarshalSignature;
import at.pollaknet.api.facile.symtab.symbols.MemberRef;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.MethodSignature;
import at.pollaknet.api.facile.symtab.symbols.NativeImplementation;
import at.pollaknet.api.facile.symtab.symbols.Parameter;
import at.pollaknet.api.facile.symtab.symbols.Property;
import at.pollaknet.api.facile.symtab.symbols.PropertySignature;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.symtab.symbols.aggregation.Namespace;
import at.pollaknet.api.facile.symtab.symbols.meta.CustomAttribute;
import at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity;
import at.pollaknet.api.facile.symtab.symbols.meta.ManifestResource;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;
import at.pollaknet.api.facile.symtab.symbols.scopes.AssemblyRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.FileRef;
import at.pollaknet.api.facile.symtab.symbols.scopes.Module;
import at.pollaknet.api.facile.symtab.symbols.scopes.ModuleRef;

public interface LanguageRenderer {
	
	public abstract String render(Assembly assembly);
	public abstract String render(AssemblyRef assemblyRef);
	public abstract String render(Module module);
	public abstract String render(ModuleRef moduleRef);
	public abstract String render(FileRef fileRef);
	public abstract String render(Namespace namespace);
	
	public abstract String render(CustomAttribute customAttribute);
	public abstract String render(DeclarativeSecurity declarativeSecurity);
	public abstract String render(ManifestResource manifestResource);
	
	public abstract String render(TypeSpec typeSpec);
	public abstract String render(TypeRef TypeRef);
	public abstract String render(Type type);
	public abstract String render(PropertySignature propertySignature);
	public abstract String render(Property property);
	public abstract String render(Parameter parameter);
	public abstract String render(NativeImplementation nativeImplementation);
	public abstract String render(MethodSignature methodSignature);
	public abstract String render(Method method);
	public abstract String render(MemberRef memberRef);
	public abstract String render(MarshalSignature marshalSignature);
	public abstract String render(Instance instance);
	public abstract String render(FieldLayout fieldLayout);
	public abstract String render(Field field);
	public abstract String render(Event event);
	public abstract String render(Constant constant);
	public abstract String render(ClassLayout classLayout);
	
	
	public abstract String renderAsReference(TypeSpec typeSpec);
	public abstract String renderAsReference(TypeRef TypeRef);
	public abstract String renderAsReference(Type type);
	
	public abstract String renderAsReference(Method method);
	public abstract String renderAsReference(MemberRef memberRef);
	public abstract String renderAsReference(Field field);
	
	public abstract String renderAsReference(StandAloneSigEntry standAlone);
	public abstract String renderAsReference(MethodSignature methodSignature);
	public abstract String render(ExportedType exportedType);
	
	public abstract void renderSourceFilesToDirectory(Assembly assembly, String directory) throws IOException;
	
}

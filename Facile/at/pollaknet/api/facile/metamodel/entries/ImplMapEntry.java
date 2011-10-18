package at.pollaknet.api.facile.metamodel.entries;

import at.pollaknet.api.facile.metamodel.RenderableCilElement;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IMemberForwarded;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.symbols.Field;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.NativeImplementation;

public class ImplMapEntry implements RenderableCilElement, NativeImplementation {

	private int mappingFlags;
	private IMemberForwarded memberForwarded;
	private String importName;
	private ModuleRefEntry importScope;
	
	public int getMappingFlags() {
		return mappingFlags;
	}
	public void setMappingFlags(int mappingFlags) {
		this.mappingFlags = mappingFlags;
	}
	public IMemberForwarded getMemberForwarded() {
		return memberForwarded;
	}
	public void setMemberForwarded(IMemberForwarded memberForwarded) {
		this.memberForwarded = memberForwarded;
	}
	public String getImportName() {
		return importName;
	}
	public void setImportName(String importName) {
		this.importName = importName;
	}
	public ModuleRefEntry getImportScope() {
		return importScope;
	}
	public void setImportScope(ModuleRefEntry importScope) {
		this.importScope = importScope;
	}
	
	@Override
	public String toString() {
		return String.format("ImplMapEntry: %s Scope %s (Flags: 0x%04x) MemberForwarded: %s",
				importName, importScope, mappingFlags, memberForwarded.getName());
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
	public Field getField() {
		assert(memberForwarded!=null);
		return memberForwarded.getField();
	}
	@Override
	public Method getMethod() {
		assert(memberForwarded!=null);
		return memberForwarded.getMethod();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * (prime + ((importName == null) ? 0 : importName.hashCode()))
					+ mappingFlags;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImplMapEntry other = (ImplMapEntry) obj;
		if (importName == null) {
			if (other.importName != null)
				return false;
		} else if (!importName.equals(other.importName))
			return false;
		if (importScope == null) {
			if (other.importScope != null)
				return false;
		} else if (!importScope.getFullQualifiedName().equals(
				other.importScope.getFullQualifiedName()))
			return false;
		if (mappingFlags != other.mappingFlags)
			return false;
		if (memberForwarded == null) {
			if (other.memberForwarded != null)
				return false;
		} else if (!memberForwarded.getName().equals(other.memberForwarded.getName()))
			return false;
		return true;
	}
	
	
}

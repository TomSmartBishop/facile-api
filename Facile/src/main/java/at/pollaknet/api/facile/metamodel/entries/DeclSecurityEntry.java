package at.pollaknet.api.facile.metamodel.entries;


import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.HasBackupBlobIndex;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasDeclSecurity;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.signature.Permission;
import at.pollaknet.api.facile.symtab.signature.Signature;
import at.pollaknet.api.facile.symtab.symbols.meta.DeclarativeSecurity;
import at.pollaknet.api.facile.util.ArrayUtils;

public class DeclSecurityEntry extends AbstractAttributable implements IHasCustomAttribute, DeclarativeSecurity, HasBackupBlobIndex {

	private int action;
	private IHasDeclSecurity parent;
	private byte [] permissionSet;
	private String permissionSetXML = null;

	//a backup index for broken blob entries
	private int blobIndex;
	
	//object representation of the byte buffer
	private Permission[] permissions;
	
	
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public IHasDeclSecurity getParent() {
		return parent;
	}
	public void setParent(IHasDeclSecurity parent) {
		this.parent = parent;
	}
	
	public String getXMLPermissionSet() {
		if(permissionSetXML==null && permissionSet[0]!=Signature.PREFIX_DECL_SECURITY) {
			permissionSetXML = new String(permissionSet);
		} 
		
		return permissionSetXML;
	}
	
	public byte [] getPermissionSet() {
		return permissionSet;
	}
	
	public void setPermissionSet(byte[] permissionSet) {
		this.permissionSet = permissionSet;
	}
	
	@Override
	public String getName() {
		return parent.getName();
	}
	
	@Override
	public String toString() {
		return String.format("DeclSecurity: %s (Action: 0x%x PermissionSet: %s)",
				parent.getName(), action, permissionSet==null?"[not set]":ArrayUtils.formatByteArray(permissionSet));
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
	public int getBinaryBlobIndex() {
		return 	blobIndex;
	}
	
	public void setBinaryBlobIndex(int blobIndex) {
		this.blobIndex = blobIndex;
	}
	
	public void setPermissions(Permission[] permissions) {
		this.permissions = permissions;
	}
	
	public Permission[] getPermissions() {
		if(permissions == null) return new Permission[0];
		return permissions;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + blobIndex;
		return prime * result + Arrays.hashCode(permissionSet);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeclSecurityEntry other = (DeclSecurityEntry) obj;
		if (action != other.action)
			return false;
		
		//ignore the blob index
		//		if (blobIndex != other.blobIndex)
		//			return false;
		
		if (!Arrays.equals(permissionSet, other.permissionSet))
			return false;
		
		if (permissionSetXML == null) {
			if (other.permissionSetXML != null)
				return false;
		} else if (!permissionSetXML.equals(other.permissionSetXML))
			return false;

		return Arrays.equals(permissions, other.permissions);

	}

	@Override
	public int compareTo(DeclarativeSecurity o) {
		return o.getAction()-action;
	}
}

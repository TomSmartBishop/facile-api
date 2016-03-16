package at.pollaknet.api.facile.metamodel.entries;

import java.util.Arrays;

import at.pollaknet.api.facile.metamodel.AbstractAttributable;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasCustomAttribute;
import at.pollaknet.api.facile.metamodel.entries.aggregation.IHasDeclSecurity;
import at.pollaknet.api.facile.renderer.LanguageRenderer;
import at.pollaknet.api.facile.symtab.signature.Permission;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.symtab.symbols.meta.ManifestResource;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;
import at.pollaknet.api.facile.util.ArrayUtils;

public class AssemblyEntry extends AbstractAttributable implements
		IHasCustomAttribute, IHasDeclSecurity, Assembly {

	private long hasAlgorithmId;
	private int majorVersion;
	private int minorVersion;
	private int buildNumber;
	private int revisionNumber;
	private long flags;
	private byte [] publicKey;
	private String name;
	private String culture;

	private DeclSecurityEntry declSecurityEntry;
	
	private AssemblyOsEntry [] assemblyOs;
	private AssemblyProcessorEntry [] assemblyProcessor;
	
	private AssemblyRefEntry[] assemblyRefs;
	private AssemblyRefOsEntry [] assemblyRefOs;
	private AssemblyRefProcessorEntry [] assemblyRefProcessor;
	
	private ModuleEntry module;
	private ModuleRefEntry [] moduleRefs;
	private ManifestResourceEntry [] manifestResources;	
	
	private TypeDefEntry [] types;
	private TypeRefEntry [] typeRefs;
	private TypeSpecEntry [] typeSpecs;
	private TypeSpecEntry [] embeddedTypeSpecs;
	private FileEntry[] fileRefs;
	//private boolean loaded;
	private String filename;


	
	public long getHasAlgorithmId() {
		return hasAlgorithmId;
	}

	public void setHasAlgorithmId(long hasAlgorithmId) {
		this.hasAlgorithmId = hasAlgorithmId;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public int getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}

	public int getRevisionNumber() {
		return revisionNumber;
	}

	public void setRevisionNumber(int revisionNumber) {
		this.revisionNumber = revisionNumber;
	}

	public long getFlags() {
		return flags;
	}

	public void setFlags(long flags) {
		this.flags = flags;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCulture() {
		return culture;
	}

	public void setCulture(String culture) {
		this.culture = culture;
	}

	@Override
	public String toString() {
		return String.format("Assembly: %s %s Version: %d.%d Build: %d Revision: %d (Flags: 0x%08x HashAlgoID: 0x%x) PublicKey: %s",
				name, culture==null?"[neutral]":culture, majorVersion, minorVersion,
				buildNumber, revisionNumber, flags, hasAlgorithmId,
				publicKey==null?"[not set]":ArrayUtils.formatByteArray(publicKey));
	}

	
	@Override
	public void setDeclarativeSecurity(DeclSecurityEntry declSecurityEntry) {
		this.declSecurityEntry = declSecurityEntry;
	}
	
	/* (non-Javadoc)
	 * @see facile.Assembly#getDeclarativeSecurity()
	 */
	@Override
	public DeclSecurityEntry getDeclarativeSecurity() {
		return declSecurityEntry;
	}

	/* (non-Javadoc)
	 * @see facile.Assembly#toExtendedString()
	 */
	public String toExtendedString() {
		StringBuffer buffer = new StringBuffer(2048);
				
//		buffer.append("Assembly:\n");
		
		if( getDeclarativeSecurity()!=null ) {
			//IMPROVE: uniform string representation
			if(getDeclarativeSecurity().getAction()!=0) {
				buffer.append("Declarative Security Permission: [assembly: SecurityPermission(SecurityAction Code ");
				buffer.append(getDeclarativeSecurity().getAction());
				buffer.append(")]\n");
			}
			for(Permission p: getDeclarativeSecurity().getPermissions()) {
				buffer.append("Declarative Security Permission: [assembly: ");
				buffer.append(p.toString());
				buffer.append("]\n");
			}
			if(getDeclarativeSecurity().getXMLPermissionSet()!=null) {
				buffer.append("Declarative Security Permission (XML): ");
				buffer.append(getDeclarativeSecurity().getXMLPermissionSet().replaceAll("\n", ""));
				buffer.append("\n");
			}
		}
		
//		buffer.append("Module:\n");
//		buffer.append(module.toExtendedString());
//		
//		if(getCustomAttributes()!=null) {
//			for(CustomAttributeEntry c: getCustomAttributes()) {
//				buffer.append(c.toExtendedString());
//				buffer.append("\n");
//			}
//		}
//		
		buffer.append(toString());
		
		buffer.append("\n");
		buffer.append(module.toExtendedString());
		buffer.append("\nTypes:");
		
		for(TypeDefEntry t: types) {
			buffer.append("\n\t");
			
			if(t.getCustomAttributes()!=null) {
				for(CustomAttributeEntry c: t.getCustomAttributes()) {
					buffer.append(c.toExtendedString());
					buffer.append("\n\t");
				}
			}
			buffer.append(t.toExtendedString().replaceAll("\n","\n\t"));
			if(t.getFields()!=null) {
				for(FieldEntry f: t.getFields()) {
					if(f!=null) {
						buffer.append("\n\t\t");
						buffer.append(f.toExtendedString().replaceAll("\n","\n\t\t"));
					} else {
						buffer.append("\n\t\t[no field found]");
					}
				}
			} 
			if(t.getMethods()!=null) {
				for(MethodDefEntry m: t.getMethods()) {
					if(m!=null) {
						buffer.append("\n\t\t");
						buffer.append(m.toExtendedString().replaceAll("\n","\n\t\t"));
					} else {
						buffer.append("\n\t\t[no method found]");
					}
					
				}
			}
		}
		
		buffer.append("\n\tTypeRefs:");
		for(TypeRefEntry t: typeRefs) {
			if(t.getCustomAttributes()!=null) {
				for(CustomAttributeEntry c: t.getCustomAttributes()) {
					buffer.append(c.toExtendedString());
					buffer.append("\n\t\t");
				}
			}
			buffer.append("\n\t\t");
			buffer.append(t.toString().replaceAll("\n","\n\t\t"));
		}
		
		buffer.append("\n\tTypeSpecs:");
		for(TypeSpecEntry t: typeSpecs) {		
			if(t.getCustomAttributes()!=null) {
				for(CustomAttributeEntry c: t.getCustomAttributes()) {
					buffer.append(c.toExtendedString());
					buffer.append("\n\t\t");
				}
			}
			buffer.append("\n\t\t");
			buffer.append(t.toString().replaceAll("\n","\n\t\t"));
		}
		
		buffer.append("\n\tTypeSpecs (embedded in signatures):");
		for(TypeSpecEntry t: embeddedTypeSpecs) {		
			if(t.getCustomAttributes()!=null) {
				for(CustomAttributeEntry c: t.getCustomAttributes()) {
					buffer.append(c.toExtendedString());
					buffer.append("\n\t\t");
				}
			}
			buffer.append("\n\t\t");
			buffer.append(t.toString().replaceAll("\n","\n\t\t"));
		}
		
		buffer.append("\n\tAssemblyRefs:");
		for(AssemblyRefEntry ref: assemblyRefs) {
			if(ref.getCustomAttributes()!=null) {
				for(CustomAttributeEntry c: ref.getCustomAttributes()) {
					buffer.append(c.toExtendedString());
					buffer.append("\n\t\t");
				}
			}
			buffer.append("\n\t\t");
			buffer.append(ref.toExtendedString().replaceAll("\n","\n\t\t"));
		}
		
		buffer.append("\n\tModuleRefs:");
		for(ModuleRefEntry ref: moduleRefs) {
			if(ref.getCustomAttributes()!=null) {
				for(CustomAttributeEntry c: ref.getCustomAttributes()) {
					buffer.append(c.toExtendedString());
					buffer.append("\n\t\t");
				}
			}
			buffer.append("\n\t\t");
			buffer.append(ref.toExtendedString().replaceAll("\n","\n\t\t"));
		}
		
		buffer.append("\n\tManifestResources:");
		for(ManifestResourceEntry res: manifestResources) {
			if(res.getCustomAttributes()!=null) {
				for(CustomAttributeEntry c: res.getCustomAttributes()) {
					buffer.append(c.toExtendedString());
					buffer.append("\n\t\t");
				}
			}
			buffer.append("\n\t\t");
			buffer.append(res.toString());
		}
		
		buffer.append("\n\tFileRefs:");
		for(FileEntry file: fileRefs) {
			if(file.getCustomAttributes()!=null) {
				for(CustomAttributeEntry c: file.getCustomAttributes()) {
					buffer.append(c.toExtendedString());
					buffer.append("\n\t\t");
				}
			}
			buffer.append("\n\t\t");
			buffer.append(file.toString());
		}
		
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see facile.Assembly#getTypes()
	 */
	public Type[] getAllTypes() {
		return types;
	}

	/* (non-Javadoc)
	 * @see facile.Assembly#getTypeRefs()
	 */
	public TypeRef[] getAllTypeRefs() {
		return typeRefs;
	}

	/* (non-Javadoc)
	 * @see facile.Assembly#getTypeSpecs()
	 */
	public TypeSpec[] getAllTypeSpecs() {
		return typeSpecs;
	}
	
	/* (non-Javadoc)
	 * @see facile.Assembly#getEmbeddedTypeSpecs()
	 */
	public TypeSpec[] getEmbeddedTypeSpecs() {
		return embeddedTypeSpecs;
	}
	
	/* (non-Javadoc)
	 * @see facile.Assembly#getModule()
	 */
	public ModuleEntry getModule() {
		return module;
	}

	/* (non-Javadoc)
	 * @see facile.Assembly#getManifestResources()
	 */
	public ManifestResource[] getManifestResources() {
		return manifestResources;
	}

	/* (non-Javadoc)
	 * @see facile.Assembly#getAssemblyOs()
	 */
	public AssemblyOsEntry[] getAssemblyOS() {
		return assemblyOs;
	}
	public void setAssemblyOs(AssemblyOsEntry[] assemblyOs) {
		this.assemblyOs = assemblyOs;
	}
	/* (non-Javadoc)
	 * @see facile.Assembly#getAssemblyProcessor()
	 */
	public AssemblyProcessorEntry[] getAssemblyProcessors() {
		return assemblyProcessor;
	}
	public void setAssemblyProcessor(AssemblyProcessorEntry[] assemblyProcessor) {
		this.assemblyProcessor = assemblyProcessor;
	}
	
	/* (non-Javadoc)
	 * @see facile.Assembly#getAssemblyRefs()
	 */
	public AssemblyRefEntry[] getAssemblyRefs() {
		return assemblyRefs;
	}
	public void setAssemblyRefs(AssemblyRefEntry[] assemblyRefs) {
		this.assemblyRefs = assemblyRefs;
	}
	/* (non-Javadoc)
	 * @see facile.Assembly#getAssemblyRefOs()
	 */
	public AssemblyRefOsEntry[] getAssemblyOSRefs() {
		return assemblyRefOs;
	}
	public void setAssemblyRefOs(AssemblyRefOsEntry[] assemblyRefOs) {
		this.assemblyRefOs = assemblyRefOs;
	}
	
	/* (non-Javadoc)
	 * @see facile.Assembly#getAssemblyRefProcessor()
	 */
	public AssemblyRefProcessorEntry[] getAssemblyProcessorRefs() {
		return assemblyRefProcessor;
	}
	public void setAssemblyRefProcessor(
			AssemblyRefProcessorEntry[] assemblyRefProcessor) {
		this.assemblyRefProcessor = assemblyRefProcessor;
	}
	/* (non-Javadoc)
	 * @see facile.Assembly#getModuleRefs()
	 */
	public ModuleRefEntry[] getModuleRefs() {
		return moduleRefs;
	}
	
	public FileEntry[] getFileRefs() {
		return fileRefs;
	}
	
	public void setModuleRefs(ModuleRefEntry[] moduleRefs) {
		this.moduleRefs = moduleRefs;
	}
	public void setModule(ModuleEntry module) {
		this.module = module;
	}
	public void setManifestResources(ManifestResourceEntry[] manifestResources) {
		this.manifestResources = manifestResources;
	}
	public void setTypes(TypeDefEntry[] types) {
		this.types = types;
	}
	public void setTypeRefs(TypeRefEntry[] typeRefs) {
		this.typeRefs = typeRefs;
	}
	
	public void setTypeSpecs(TypeSpecEntry[] typeSpecs) {
		this.typeSpecs = typeSpecs;
	}

	public void setFileRefs(FileEntry[] fileRefs) {
		this.fileRefs = fileRefs;
	}

//	public boolean isLoaded() {
//		return loaded;
//	}
//	
//	public void setLoaded(boolean loaded) {
//		this.loaded = loaded;
//	}

	public void setFileName(String filename) {
		this.filename = filename;
	}
	
	public String getFileName() {
		return filename;
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
		int result = super.hashCode();
		result = prime * result + buildNumber;
		result = prime * result + majorVersion;
		result = prime * result + minorVersion;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(publicKey);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssemblyEntry other = (AssemblyEntry) obj;
		if (buildNumber != other.buildNumber)
			return false;
		if (culture == null) {
			if (other.culture != null)
				return false;
		} else if (!culture.equals(other.culture))
			return false;
//		if (filename == null) {
//			if (other.filename != null)
//				return false;
//		} else if (!filename.equals(other.filename))
//			return false;
		if (flags != other.flags)
			return false;
		if (hasAlgorithmId != other.hasAlgorithmId)
			return false;
		if (majorVersion != other.majorVersion)
			return false;
		if (minorVersion != other.minorVersion)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (revisionNumber != other.revisionNumber)
			return false;
		if (!Arrays.equals(publicKey, other.publicKey))
			return false;
		if (!Arrays.equals(manifestResources, other.manifestResources))
			return false;
		if (declSecurityEntry == null) {
			if (other.declSecurityEntry != null)
				return false;
		} else if (!declSecurityEntry.equals(other.declSecurityEntry))
			return false;
		if (!Arrays.equals(fileRefs, other.fileRefs))
			return false;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		return Arrays.equals(moduleRefs, other.moduleRefs);

	}

	public void setEmbeddedTypeSpecs(TypeSpecEntry[] signatureEmbeddedTypeSpecs) {
		embeddedTypeSpecs = signatureEmbeddedTypeSpecs;
	}

}
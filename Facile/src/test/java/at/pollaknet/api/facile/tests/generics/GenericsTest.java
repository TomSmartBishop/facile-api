package at.pollaknet.api.facile.tests.generics;

import at.pollaknet.api.facile.Facile;
import at.pollaknet.api.facile.symtab.symbols.Method;
import at.pollaknet.api.facile.symtab.symbols.MethodSignature;
import at.pollaknet.api.facile.symtab.symbols.Type;
import at.pollaknet.api.facile.symtab.symbols.TypeRef;
import at.pollaknet.api.facile.symtab.symbols.TypeSpec;
import at.pollaknet.api.facile.symtab.symbols.scopes.Assembly;
import junit.framework.TestCase;

public class GenericsTest extends TestCase
{
    public void testSystemActivator()
    {
        try
        {
            Assembly assembly = Facile.loadAssembly("assemblies/v4.0.4.0.0.0_64/mscorlib.dll");

            for (Type type : assembly.getAllTypes())
            {
                if (type.getFullQualifiedName().equals("System.Activator"))
                {
                    for (Method m : type.getMethods())
                    {
                        MethodSignature sig = m.getMethodSignature();
                        TypeRef ref = sig.getReturnType();

                        if (ref instanceof TypeSpec)
                        {
                            TypeSpec spec = (TypeSpec) ref;

                            if (spec.isGenericInstance())
                            {
                            	if(spec.getName()==null)
                            	{
                            		assertNotNull(spec.getName());
                            	}
                                assertNotNull(spec.getName());
                            }
                        }
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
}

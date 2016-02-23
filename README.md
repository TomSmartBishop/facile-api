
# The Facile API
The Facile API is capable of reading (decompiling) .Net assemblies. Covering the metadata tables, the embedded types and methods, including their bodies as CIL (bytecode).

(Previously hosted on https://code.google.com/p/facile-api/)

#Background
Initially the Facile API has been written to perform analysis of .Net assemblies, independet of the authoring .Net language.

#Usage Sample
This is just code to get an idea of the API. Please also use the available JavaDoc.

##Inspect All Types of an Assembly

```
try {
    //specify a path, where to find the assembly String assemblyName = "mscorlib.dll"; String assemblyLocation = "../path/to/assembly/" + assemblyName;

    //reflect and load the assembly using the Facile factory
    Assembly assembly = Facile.loadAssembly(assemblyLocation);

    //perform your custom operations on the assembly...
    System.out.println("All defined types in " + assemblyName + ":\n");

    for(Type type : assembly.getAllTypes()) {
        System.out.println(type);
    }
} catch (Exception e) {
    e.printStackTrace();
}
```

##Make use of the ILDasmRenderer

```
try {
    //create a reflector object for later access (ILAsmRenderer)\
    FacileReflector facileReflector = Facile.load("../somewhere/myAssembly.exe", "../somewhere/myOptionalProgramDebugDatabase.pdb");

    //load the assembly
    Assembly assembly = facileReflector.loadAssembly();

    if(assembly!=null) {
        //output some useful information
        System.out.println(assembly.toExtendedString());

        //generate output
        ILAsmRenderer renderer = new ILAsmRenderer(facileReflector);
        renderer.renderSourceFilesToDirectory(assembly, System.getProperty("user.dir"));

        //print out the location of the files
        System.out.println("Generated decompiled files in: " + System.getProperty("user.dir"));
    } else {
        System.out.println("File maybe contains only resources...");
    }
} catch (DotNetContentNotFoundException e) {
    //maybe you selected a native executeable...
    System.out.println("No .Net content found...");
    e.printStackTrace();
} catch (IOException e) {
    //in case of missing file acces rights or similar problems
    e.printStackTrace();
} catch (Exception e) {
    //everything else
    e.printStackTrace();
}
```

#Build Instructions
There is an Eclipse project and a jardesc file in the repository which you can use to build the jar file.

#Development And Unit Test Environment
All required configurations can be found in the Eclipse 'Run' and 'External Tools' dialog.
For 'External Tools' -> 'Run ILAsm' you need a binary of ILAsm (comes with Visual Studio)
Run Configurations in Eclipse.
This is my development setup, it should work for you too:
* Windows7
* JDK 1.6x (any newer should work)
* Eclipse 3.7

* Dev Configurations

  * Facile (select assembly):
Read a dedicated assembly (full refelction) - this configuration is good for debugging a specific assembly.
  * Facile(select assembly and pdb):
The same as before but allows the additional specification of a program debug database, so that it additionally process Visual Studio debug information.
  * Facile IL Dump:
Test configuration... will read the specifid assembly and writes it as decompiled ILAsm program to the console. I use it to test the  * ILAsmRender implmentation:
If everything works correctly you can use this output to rebuild the program with ILAsm
Unit Test Configurations in Eclipse

* Unit Test Configurations

  * Facile Basic Test Suite:
Runs all basic unit tests (reading and decoding binary formats, decompiling all registered versions of mscorlib and some addtional basic tests)
  * Facile GAC Test Suite:
Reads (reflects, decompiles) all assemblies of your local Windows GAC. Errors will be dumped to the output...!
  * Local Drive Test Suite:
Will scan all drives and directories for assemblies and will decompile it. Errors will be dumped to the output...!
Especially the last two configurations are good to test the coverage and make sure that the reflection is working as expected.

* External Tools Configurations in Eclipse

  * Facile read output.txt:
Opens a text editor to view the output of the last run, which is always dumped to output.txt - maybe you have to change the path or text editor of this configuration
  * Run ILAsm (select IL file):
Run ILAsm to test if the decompiled output of facile can be compiled again (round tripping) - this is still in development..!

* Additional: Sometimes a program like Total Commader http://www.totalcmd.net/ is useful to copy assemblies from the GAC to a desired location!

#Binary
There is an older binary available here: http://tomsmartbishop.github.io/facile-api/

#Commercial Use
The Facile API is commercially used by the Sonargraph Explorer: http://www.hello2morrow.com

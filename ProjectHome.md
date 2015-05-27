# The Facile API  #

The Facile API is capable of reading (decompiling) .Net assemblies. Covering the metadata tables, the embedded types and methods, including their bodies as CIL (bytecode).

## Background ##

Initially the Facile API has been written to perform analysis of .Net assemblies, independet of the authoring .Net language.

## Support ##

Please make a donation if you like it:

[![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=4XAU6DKYLW7VA)


## Code Sample ##
```
try {
    //specify a path, where to find the assembly
    String assemblyName = "mscorlib.dll";
    String assemblyLocation = "../path/to/assembly/" + assemblyName;

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
### Current Focus ###

  * Bug [issue#4](https://code.google.com/p/facile-api/issues/detail?id=#4) has been resolved with [r77](https://code.google.com/p/facile-api/source/detail?r=77), but there is still some work to do (to be solved until 1.0.4).
  * Fix all FIXME and TODO's in the code base
  * IL Source Renderer for round tripping: basic version, but pretty poor
  * C# Renderer: not implementation so far

### Other Issues ###

  * FacileReader (RCP using the Facile API): There is an alpha version of that, see download section (still using API version 1.0.2).
  * Running CIL code in Java, emulating the VES: internal idea, collecting more infromation...
  * Code analysis for uncaught exception: internal idea, collecting more infromation...

## Downloads ##

The most recent stable API version as Java archive:

http://facile-api.googlecode.com/files/at.pollaknet.apps.facile.reader-1.0.3.jar


Using the Facile API for a decompiler application - FacileReader (alpha):

http://facile-api.googlecode.com/files/FacileReader.zip




---


_Please contact project owner if you find any assembly which cannot be fully refelected!_




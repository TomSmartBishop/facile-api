
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <atlbase.h>

#include "jni.h"

#include "dia2.h"
#include "diacreate.h"
#include "cvconst.h"

#include "../NativePdbReader.h"
#include "DiaContainer.h"
#include "DiaString.h"

/************************************************************************/
/*                                                                      */
/************************************************************************/
 JNIEXPORT jint JNICALL Java_at_pollaknet_api_facile_pdb_dia_NativePdbReader_openPdb(JNIEnv *env, jobject obj, jstring jsPdbPath) {

	#ifdef DEBUG
		 printf("Native: executing native function _openPdb...\n");
		 _flushall();
	#endif

	//get the class of the current object
	jclass jcSelf = env->GetObjectClass(obj);

	//assume that the current object has a class
	assert(jcSelf!=NULL);

	//get the field, which holds the pointer to the dia container
	jfieldID jfNativeHandleID = env->GetFieldID(jcSelf, "nativeHandle", "J");

	//return with error if this requiered field is not present in the class
	if(jfNativeHandleID==NULL) return ERROR_FIELD_NOT_FOUND;

  #ifdef DEBUG
    printf("Native: Creating DIA container...\n");
    _flushall();
  #endif

	//create a new debug interface access container
	DiaContainer *pDiaContainer = new DiaContainer();
	
	//return if it was not possible to create the object
	if(pDiaContainer==NULL) return ERROR_OUT_OF_MEMORY;

	//get the native string from java string
	size_t sSize = (env->GetStringLength(jsPdbPath)+1)*sizeof(jchar);
	char *szPdbPath = new char[sSize];
	assert(szPdbPath!=NULL);

	const jchar *pNativeString = env->GetStringChars(jsPdbPath, 0);
	assert(pNativeString!=NULL);

	//convert the jchar string to simple c style string
	sprintf_s(szPdbPath, sSize, "%ws", pNativeString);

  #ifdef DEBUG
    printf("Native: (DiaContainer *):%p Opening pdb file...\n", pDiaContainer);
    _flushall();
  #endif

	//open the pdb file
	jint jiReturnCode = pDiaContainer->openProgramDebugDatabase(szPdbPath);

	//release the pinned java string
	env->ReleaseStringChars(jsPdbPath, pNativeString);

	//delete the copied version of the string
	SAFE_DELETE(szPdbPath);

	//was the open procedure successfully?
	if(jiReturnCode==SUCCESSFULLY_INITED) {
		//save the pointer by using the handle field
		env->SetLongField(obj, jfNativeHandleID, (jlong)pDiaContainer);
	} else {
		//delete the object (because there is no need to keep it)
		SAFE_DELETE(pDiaContainer);

		//Ensure that even the long-parameter named nativeHandle is set to
		//zero in order to communicate the status of the DiaContainer object.
		env->SetLongField(obj, jfNativeHandleID, (jlong)0);
	}

	#ifdef DEBUG
		printf("Native: Leaving native function _openPdb...\n");
		_flushall();
	#endif

	//return the code returned by the open method
	return jiReturnCode;
 }

 JNIEXPORT void JNICALL Java_at_pollaknet_api_facile_pdb_dia_NativePdbReader_closePdb(JNIEnv *env, jobject obj) {

	#ifdef DEBUG
		 printf("NAtive: Executing native function _closePdb...\n");
		 _flushall();
	#endif

	//get the class of the current object
	jclass jcSelf = env->GetObjectClass(obj);

	//assume that the current object has a class
	assert(jcSelf!=NULL);

	//get the field, which holds the pointer to the dia container
	jfieldID jfNativeHandleID = env->GetFieldID(jcSelf, "nativeHandle", "J");

	if(jfNativeHandleID==NULL) return;

	//get the pointer via the native handle
	DiaContainer *pDiaContainer = (DiaContainer *)env->GetLongField(obj, jfNativeHandleID);

	if(pDiaContainer==NULL) return;

	//delete the container (the destructor does the remaining cleanup)
	SAFE_DELETE(pDiaContainer);

	//save the native handle also to NULL (0)
	env->SetLongField(obj, jfNativeHandleID, (jlong)0);

	#ifdef DEBUG
		printf("Native: Leaving native function _closePdb...\n");
		_flushall();
	#endif
 }


 JNIEXPORT jobject JNICALL Java_at_pollaknet_api_facile_pdb_dia_NativePdbReader_getLineNumbersByRVA(JNIEnv *env, jobject obj, jlong jlRVA) {
	ULONGLONG ullLength = 0;
	DWORD dwSection = 0;
	DWORD dwOffset = 0;

	#ifdef DEBUG
		printf("Native: Executing native function _getLineNumbersByRVA...\n");
		_flushall();
	#endif

	//get the class of the current object
	jclass jcSelf = env->GetObjectClass(obj);

	//assume that the current object has a class
	assert(jcSelf!=NULL);

	//get the field, which holds the pointer to the dia container
	jfieldID jfNativeHandleID = env->GetFieldID(jcSelf, "nativeHandle", "J");

	//assume that the field is still present
	assert(jfNativeHandleID!=NULL);

  #ifdef DEBUG
    printf("Native: Accessing DIA container...\n");
    _flushall();
  #endif

	//get the pointer to the container
	//(which has to be here because it was assingned just before this method call)
  DiaContainer *pDiaContainer = (DiaContainer *)env->GetLongField(obj, jfNativeHandleID);
	
  //this happens when somebody calls that function and no PDB exists
  if(pDiaContainer==NULL) return NULL;

  #ifdef DEBUG
    printf("Native: (DiaContainer *):%p Find class at/pollaknet/api/facile/pdb/LineNumberInfo...\n", pDiaContainer);
    _flushall();
  #endif

  //check if the session has been initialized
  if (pDiaContainer->getSession() == NULL) return NULL;

	//search for the java LineNumberInfo class
	jclass jcLineNumberInfoClass = env->FindClass("at/pollaknet/api/facile/pdb/LineNumberInfo");

	//do not continue if this class is missing
	if(jcLineNumberInfoClass==NULL) return NULL;

	//create an instance of the java LineNumberInfo class_getLineNumbersByRVA...
	jmethodID jmidConstructor = env->GetMethodID(jcLineNumberInfoClass, "<init>", "()V");
	jmethodID jmidSetSourceFile = env->GetMethodID(jcLineNumberInfoClass, "setSourceFileName", "(Ljava/lang/String;)V");
	jmethodID jmidAddInstruction = env->GetMethodID(jcLineNumberInfoClass, "addInstruction", "(JJJJ)V");

	//check that every method is present
	if(jmidConstructor==NULL || jmidSetSourceFile==NULL || jmidAddInstruction==NULL) return NULL;

	//create a new java line number object
	jobject joLineNumberInfo = env->NewObject(jcLineNumberInfoClass, jmidConstructor);
	
	if(joLineNumberInfo==NULL) return NULL;

	//search for the specifies function/method via the given virtual address
	IDiaSymbol *pSymbol = NULL;
	IDiaSession *pDiaSession = NULL;
	pDiaSession = pDiaContainer->getSession();

	if(pDiaSession==NULL) {
		return NULL;
	}

  #ifdef DEBUG
    printf("Native: Search for DIA at RVA %ul symbol...\n", (DWORD)jlRVA);
    _flushall();
  #endif

	if(pDiaSession->findSymbolByRVA((DWORD)jlRVA, SymTagFunction, &pSymbol)!=S_OK) {
		SAFE_RELEASE(pSymbol);
		return NULL;
	}

	//get the section, offset and length of the found symbol
	pSymbol->get_addressSection( &dwSection );
	pSymbol->get_addressOffset( &dwOffset );
	pSymbol->get_length( &ullLength );

  #ifdef DEBUG
    printf("Native: Checking symbol data...\n");
    _flushall();
  #endif

	//check if the data is useful
	if ( dwSection != 0 && ullLength > 0 ) {
		IDiaEnumLineNumbers *pLines = NULL;

		//enumerate all stored lines
		if ( SUCCEEDED(pDiaSession->findLinesByAddr( dwSection, dwOffset, static_cast<DWORD>( ullLength ), &pLines ) ) ) {
		 
			IDiaLineNumber *pLine = NULL;
			DWORD dwFetchedLines = 0;
			DWORD dwRvaOffset = 0;
			boolean bFirst = true;

      #ifdef DEBUG
        printf("Native: Check line by line...\n");
        _flushall();
      #endif

			//check each line
			while ( SUCCEEDED( pLines->Next( 1, &pLine, &dwFetchedLines ) ) && dwFetchedLines == 1 ) {
				if(bFirst) {
					//extract the source file info and remember the rva offset
					IDiaSourceFile *pSrc = NULL;
					DiaString bstrFileName;
					char szSourceFile[_MAX_FNAME];

					//convert the source file entry to a c style string
					pLine->get_sourceFile( &pSrc );
					pSrc->get_fileName( &bstrFileName);
					sprintf_s(szSourceFile, _MAX_FNAME,"%ws", bstrFileName);

					//call the method of the java object to set the source file info
					env->CallVoidMethod(joLineNumberInfo, jmidSetSourceFile, env->NewStringUTF(szSourceFile));

					//remember the rva offset (in order to generat program counter entries for the function)
					pLine->get_relativeVirtualAddress( &dwRvaOffset);

					bFirst = false;

					SAFE_RELEASE(pSrc);
				}

				DWORD dwCurrentLineNumber = 0;
				DWORD dwCurrentColNumber = 0;
				DWORD dwCurrentColEndNumber = 0;
				DWORD dwCurrentRva = 0;

				//get the required debug information for the current instruction
				pLine->get_lineNumber( &dwCurrentLineNumber );
				pLine->get_columnNumber( &dwCurrentColNumber );
				pLine->get_columnNumberEnd( &dwCurrentColEndNumber);
				pLine->get_relativeVirtualAddress( &dwCurrentRva);

				dwCurrentRva -= dwRvaOffset;

				//set the debug information by calling the appropriate method on the java object
				env->CallVoidMethod(joLineNumberInfo, jmidAddInstruction,
				 (jlong)dwCurrentLineNumber, (jlong)dwCurrentColNumber, (jlong)dwCurrentColEndNumber, (jlong)dwCurrentRva);

				SAFE_RELEASE(pLine);
			}
		}

		SAFE_RELEASE(pLines);
	}

	SAFE_RELEASE(pSymbol);

	#ifdef DEBUG
		printf("Native: Leaving native function _getLineNumbersByRVA...\n");
		_flushall();
	#endif

	//return the created object
	return joLineNumberInfo;
 }

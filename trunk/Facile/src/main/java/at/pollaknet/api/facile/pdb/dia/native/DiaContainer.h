#ifndef __DIA_CONTAINER_H
#define __DIA_CONTAINER_H

//use this switch to enable debuging (even in the release configuration)
//#define DEBUG

//default includes for the whole dll
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <atlbase.h>

//dia related header files
#include "dia2.h"
#include "diacreate.h"
#include "cvconst.h"

//common used constants
#define SUCCESSFULLY_INITED					 0
#define ERROR_COM_INIT_FAILED				-1 
#define	ERROR_DIA_INIT_FAILED				-2
#define ERROR_OUT_OF_MEMORY					-3
#define ERROR_PDB_NOT_FOUND					-4
#define ERROR_DIA_SESSION_FAILED			-5
#define ERROR_NO_GLOBAL_SCOPE_FOUND			-6
#define ERROR_FIELD_NOT_FOUND				-7

//macro to delete resources in a safe way
#define SAFE_DELETE(x)	{ if(x!=NULL){delete x;x=NULL;} }

#ifdef DEBUG
	#define SAFE_RELEASE(x)	{					\
		if((x)!=NULL) {							\
			ULONG count = (x)->Release();		\
			if(count==0) {						\
				x=NULL;							\
			} else {							\
				printf("COM Object in file \"%s\", line %d still holds %d reference(s)!", __FILE__, __LINE__, count);\
			}									\
		}										\
	}											
#endif
#ifndef DEBUG
	#define SAFE_RELEASE(x)	{ if(x!=NULL){if((x->Release())==0) {x=NULL;}} }
#endif

class DiaContainer {

private:

	IDiaSession *m_pSession;
	IDiaSymbol *m_pGlobal;
	IDiaDataSource *m_pSource;
	HRESULT m_hInitResult;

public:

	DiaContainer();
	~DiaContainer();

	int openProgramDebugDatabase(char* pFile);

	void cleanUpResources();

	IDiaSession * getSession() { return m_pSession; }
	IDiaSymbol * getGlobalSymbol() { return m_pGlobal; }
	IDiaDataSource * getDataSource() { return m_pSource; }

};




#endif //__DIA_CONTAINER_H


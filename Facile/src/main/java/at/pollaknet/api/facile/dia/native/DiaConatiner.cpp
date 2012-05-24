#include "DiaContainer.h"

DiaContainer::DiaContainer():m_pSource(NULL), m_pSession(NULL), m_pGlobal(NULL) {

	//initialize the com interface
	m_hInitResult = CoInitialize(NULL);

	#ifdef DEBUG
		printf("COM Interface initialized.\n");
		_flushall();
	#endif
}

DiaContainer::~DiaContainer() {

	//clean up all used resources
	cleanUpResources();

	//Uninitialize the com interface
	CoUninitialize();

	#ifdef DEBUG
		printf("COM Interface has been uninitialized.\n");
		_flushall();
	#endif
}

void DiaContainer::cleanUpResources() {

	#ifdef DEBUG
		printf("Releasing COM objects (if possible)...\n");
		_flushall();
	#endif

	//release all objects (if not NULL)
	SAFE_RELEASE(m_pGlobal);
	SAFE_RELEASE(m_pSession);
	SAFE_RELEASE(m_pSource);
}


int DiaContainer::openProgramDebugDatabase(char* pFilePath) {

	//check if the com interface initialization was alright
	if (FAILED(m_hInitResult)) return ERROR_COM_INIT_FAILED;

	//just in case that someone calls the method twice...
	cleanUpResources();

	//create an instance and assign to the interface class IDiaDataSource
	HRESULT result = CoCreateInstance( CLSID_DiaSource, NULL, CLSCTX_INPROC_SERVER, 
		__uuidof( IDiaDataSource ), (void **) &m_pSource);

	//check the result of the instantiation
	if (FAILED(result)) return ERROR_DIA_INIT_FAILED;

	//convert the c style string to an OLE string
	size_t sSize = strlen(pFilePath);
	size_t sWideSize = sSize+5;
	size_t sConverted;
	wchar_t *wszFilename = new wchar_t[sWideSize];

	if(wszFilename==NULL) return ERROR_OUT_OF_MEMORY;

	mbstowcs_s( &sConverted, wszFilename, sWideSize, pFilePath, sSize );

	#ifdef DEBUG
		printf("Opening \"%ws\".\n", wszFilename);
		_flushall();
	#endif

	//open the pdb file
	if ( FAILED( m_pSource->loadDataFromPdb( wszFilename ) ) ) {
		if( FAILED( m_pSource->loadDataForExe( wszFilename, NULL, NULL ) ) ){
			SAFE_DELETE(wszFilename);
			cleanUpResources();
			return ERROR_PDB_NOT_FOUND;
		}
	}

	//the file name is not needed any more
	SAFE_DELETE(wszFilename);

	//create a new session
	if( FAILED( m_pSource->openSession( &m_pSession ) ) ) {
		cleanUpResources();
		return ERROR_DIA_SESSION_FAILED;
	}

	//check the file for the global scope
	if( FAILED( m_pSession->get_globalScope( &m_pGlobal) ) ) {
		cleanUpResources();
		return ERROR_NO_GLOBAL_SCOPE_FOUND;
	}

	DWORD dwId = 0;
	m_pGlobal->get_symIndexId( &dwId );

	//check the updated id of the global scope
	if( dwId == 0 ) {
		cleanUpResources();
		return ERROR_NO_GLOBAL_SCOPE_FOUND;
	}

	#ifdef DEBUG
		printf("Pdb successfully opened.\n", wszFilename);
		_flushall();
	#endif

	//file looks ok; further operations possible
	return SUCCESSFULLY_INITED;
}



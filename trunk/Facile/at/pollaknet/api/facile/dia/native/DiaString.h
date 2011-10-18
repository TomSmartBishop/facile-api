#ifndef __DIA_STRING_H
#define __DIA_STRING_H


class DiaString 
{
    BSTR m_bstr;
public:
    DiaString()         { m_bstr = NULL; }
	~DiaString()        { if (m_bstr != NULL) { SysFreeString( m_bstr ); m_bstr=NULL; } }
    BSTR *operator &() { assert(m_bstr == NULL); return &m_bstr; }
    operator BSTR()    { assert(m_bstr != NULL); return m_bstr; }
};


#endif // __DIA_STRING_H


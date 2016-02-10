package com.sqsmv.productloc.database.upc;

import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.XMLDBAccess;


public class UPCAccess extends XMLDBAccess
{
    public UPCAccess(DBAdapter dbAdapter)
    {
        super(dbAdapter, new UPCContract());
    }
}

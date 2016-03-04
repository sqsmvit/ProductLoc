package com.sqsmv.productloc.database.inventory;

import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.XMLDBAccess;


public class InventoryAccess extends XMLDBAccess
{
    public InventoryAccess(DBAdapter dbAdapter)
    {
        super(dbAdapter, new InventoryContract());
    }
}

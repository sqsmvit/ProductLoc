package com.sqsmv.productloc.database.product;

import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.XMLDBAccess;

public class ProductAccess extends XMLDBAccess
{
    public ProductAccess(DBAdapter dbAdapter)
    {
        super(dbAdapter, new ProductContract());
    }
}

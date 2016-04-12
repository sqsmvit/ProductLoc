package com.sqsmv.productloc.database.inventory;

import com.sqsmv.productloc.database.XMLDBContract;

public class InventoryContract implements XMLDBContract
{
    protected static final String TABLE_NAME = "Inventory";
    protected static final String XML_FILE_NAME = "inventory.xml";
    public static final String COLUMN_NAME_MASNUM = "masnum";
    public static final String COLUMN_NAME_COHFP = "cohfp";

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreateString()
    {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                COLUMN_NAME_MASNUM + " TEXT PRIMARY KEY, " +
                COLUMN_NAME_COHFP + " INTEGER, " +
                COLUMN_NAME_SHA + " TEXT);";
    }

    @Override
    public String getTableDropString()
    {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    @Override
    public String getPrimaryKeyName()
    {
        return COLUMN_NAME_MASNUM;
    }

    @Override
    public String[] getColumnNames()
    {
        return new String[] {
                COLUMN_NAME_MASNUM,
                COLUMN_NAME_COHFP,
                COLUMN_NAME_SHA
        };
    }

    @Override
    public String getXMLFileName()
    {
        return XML_FILE_NAME;
    }
}

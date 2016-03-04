package com.sqsmv.productloc.database.upc;

import com.sqsmv.productloc.database.XMLDBContract;

public class UPCContract implements XMLDBContract
{
    protected static final String TABLE_NAME = "UPC";
    protected static final String XML_FILE_NAME = "upc.xml";
    public static final String COLUMN_NAME_UPC = "upc";
    public static final String COLUMN_NAME_MASNUM = "masnum";

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreateString()
    {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                COLUMN_NAME_UPC + " TEXT PRIMARY KEY, " +
                COLUMN_NAME_MASNUM + " TEXT, " +
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
        return COLUMN_NAME_UPC;
    }

    @Override
    public String[] getColumnNames()
    {
        return new String[] {
                COLUMN_NAME_UPC,
                COLUMN_NAME_MASNUM,
                COLUMN_NAME_SHA
        };
    }

    @Override
    public String getXMLFileName()
    {
        return XML_FILE_NAME;
    }
}

package com.sqsmv.productloc.database.prodloc;

import com.sqsmv.productloc.database.XMLDBContract;

public class ProdLocContract implements XMLDBContract
{
    protected static final String TABLE_NAME = "ProdLoc";
    protected static final String XML_FILE_NAME = "prodloc.xml";
    public static final String COLUMN_NAME_PKLOCSCANLINEID = "pklocscanlineid";
    public static final String COLUMN_NAME_BUILDINGID = "buildingid";
    public static final String COLUMN_NAME_ROOMID = "roomid";
    public static final String COLUMN_NAME_COLID = "colid";
    public static final String COLUMN_NAME_ROWID = "rowid";
    public static final String COLUMN_NAME_SCANSTAMP = "scanstamp";
    public static final String COLUMN_NAME_MASNUM = "masnum";
    public static final String COLUMN_NAME_LOCCODE = "loccode";
    public static final String COLUMN_NAME_NAME = "name";

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreateString()
    {
        return "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                COLUMN_NAME_PKLOCSCANLINEID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME_BUILDINGID + " TEXT, " +
                COLUMN_NAME_ROOMID + " TEXT, " +
                COLUMN_NAME_COLID + " TEXT, " +
                COLUMN_NAME_ROWID + " TEXT, " +
                COLUMN_NAME_SCANSTAMP + " TEXT, " +
                COLUMN_NAME_MASNUM + " TEXT, " +
                COLUMN_NAME_LOCCODE + " TEXT, " +
                COLUMN_NAME_NAME + " TEXT, " +
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
        return COLUMN_NAME_PKLOCSCANLINEID;
    }

    @Override
    public String[] getColumnNames()
    {
        return new String[] {
                COLUMN_NAME_PKLOCSCANLINEID,
                COLUMN_NAME_BUILDINGID,
                COLUMN_NAME_ROOMID,
                COLUMN_NAME_COLID,
                COLUMN_NAME_ROWID,
                COLUMN_NAME_SCANSTAMP,
                COLUMN_NAME_MASNUM,
                COLUMN_NAME_LOCCODE,
                COLUMN_NAME_NAME,
                COLUMN_NAME_SHA
        };
    }

    @Override
    public String getXMLFileName()
    {
        return XML_FILE_NAME;
    }
}

package com.sqsmv.productloc.database.findscan;

import android.provider.BaseColumns;

import com.sqsmv.productloc.database.DBContract;

public class FindScanContract implements DBContract, BaseColumns
{
    public static final String TABLE_NAME = "FindScan";
    public static final String COLUMN_NAME_MASNUM = "masNum";
    public static final String COLUMN_NAME_CREATESTAMP = "createStamp";
    public static final String COLUMN_NAME_WH1LOCCODE = "wh1LocCode";
    public static final String COLUMN_NAME_OLOCCODE = "oLocCode";
    public static final String COLUMN_NAME_READINGLOCCODE = "readingLocCode";
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
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_MASNUM + " TEXT, " +
                COLUMN_NAME_CREATESTAMP + " DATETIME, " +
                COLUMN_NAME_WH1LOCCODE + " TEXT, " +
                COLUMN_NAME_OLOCCODE + " TEXT, " +
                COLUMN_NAME_READINGLOCCODE + " TEXT, " +
                COLUMN_NAME_NAME + " TEXT);";
    }

    @Override
    public String getTableDropString()
    {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    @Override
    public String getPrimaryKeyName()
    {
        return _ID;
    }

    @Override
    public String[] getColumnNames()
    {
        return new String[] {
                _ID,
                COLUMN_NAME_MASNUM,
                COLUMN_NAME_CREATESTAMP,
                COLUMN_NAME_WH1LOCCODE,
                COLUMN_NAME_OLOCCODE,
                COLUMN_NAME_READINGLOCCODE,
                COLUMN_NAME_NAME,
        };
    }
}

package com.sqsmv.productloc.database.scan;

import android.provider.BaseColumns;

import com.sqsmv.productloc.database.DBContract;

public class ScanContract implements DBContract, BaseColumns
{
    public static final String TABLE_NAME = "Scan";
    public static final String COLUMN_NAME_MASNUM = "masNum";
    public static final String COLUMN_NAME_BUILDING = "building";
    public static final String COLUMN_NAME_ROOM = "room";
    public static final String COLUMN_NAME_COL = "col";
    public static final String COLUMN_NAME_ROW = "row";
    public static final String COLUMN_NAME_CREATESTAMP = "createstamp";


    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    @Override
    public String getTableCreateString()
    {
        return "CREATE TABLE " + getTableName() + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_MASNUM + " TEXT, " +
                COLUMN_NAME_BUILDING + " TEXT, " +
                COLUMN_NAME_ROOM + " TEXT, " +
                COLUMN_NAME_COL + " TEXT NOT NULL, " +
                COLUMN_NAME_ROW + " TEXT, " +
                COLUMN_NAME_CREATESTAMP + " DATETIME);";
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
                COLUMN_NAME_BUILDING,
                COLUMN_NAME_ROOM,
                COLUMN_NAME_COL,
                COLUMN_NAME_ROW,
                COLUMN_NAME_CREATESTAMP
        };
    }
}

package com.sqsmv.productloc.database;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

public abstract class DBRecord
{
    private DBContract dbContract;

    public DBRecord(DBContract dbContract)
    {
        this.dbContract = dbContract;
    }

    public abstract String[] getTableInsertData();

    public boolean buildWithCursor(Cursor dbCursor)
    {
        boolean success = false;
        List<String> columnList = Arrays.asList(dbContract.getColumnNames());

        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(columnList.contains(dbCursor.getColumnName(count)))
            {
                success = true;
            }
            setFromCursor(dbCursor);
        }
        return success;
    }

    protected abstract void setFromCursor(Cursor dbCursor);
}

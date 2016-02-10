package com.sqsmv.productloc.database;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

public abstract class XMLDBRecord extends DBRecord
{
    private XMLDBContract xmlDBContract;
    private String sha;

    public XMLDBRecord(XMLDBContract xmlDBContract)
    {
        super(xmlDBContract);
        this.xmlDBContract = xmlDBContract;
    }

    public String getSha()
    {
        return sha;
    }

    public void setSha(String sha)
    {
        this.sha = sha;
    }

    @Override
    public boolean buildWithCursor(Cursor dbCursor)
    {
        boolean success = false;
        List<String> columnList = Arrays.asList(xmlDBContract.getColumnNames());

        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(columnList.contains(dbCursor.getColumnName(count)) && !dbCursor.getColumnName(count).equals(XMLDBContract.COLUMN_NAME_SHA))
            {
                success = true;
            }
            setFromCursor(dbCursor);
        }
        return success;
    }
}

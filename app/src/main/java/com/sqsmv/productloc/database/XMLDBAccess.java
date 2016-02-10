package com.sqsmv.productloc.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class XMLDBAccess extends DBAccess
{
    private XMLDBContract xmlDBContract;

    public XMLDBAccess(DBAdapter dbAdapter, XMLDBContract xmlDBContract)
    {
        super(dbAdapter, xmlDBContract);
        this.xmlDBContract = xmlDBContract;
    }

    public void insertBatch(ArrayList<ArrayList<String>> batch)
    {
        String insertQuery = new QueryBuilder().buildInsertQuery(getTableName(), getTableColumns());
        SQLiteStatement query = getDB().compileStatement(insertQuery);
        getDB().beginTransaction();

        query.clearBindings();

        for(ArrayList<String> record : batch)
        {
            query.bindAllArgsAsStrings(record.toArray(new String[record.size()]));
            query.executeInsert();
        }
        query.close();
        getDB().setTransactionSuccessful();
        getDB().endTransaction();
    }

    public HashMap<String, String> getSha()
    {
        HashMap<String, String> mapIds= new HashMap<String, String>();

        String tempPK;
        String tempSha;

        String[] queryColumns = {xmlDBContract.getPrimaryKeyName(), XMLDBContract.COLUMN_NAME_SHA};

        Cursor dbCursor = getDB().query(xmlDBContract.getTableName(), queryColumns, null, null, null, null, null);

        if(dbCursor.moveToFirst())
        {
            int pkCol = dbCursor.getColumnIndex(xmlDBContract.getPrimaryKeyName());
            int shaCol = dbCursor.getColumnIndex(XMLDBContract.COLUMN_NAME_SHA);
            do
            {
                tempPK = dbCursor.getString(pkCol);
                tempSha = dbCursor.getString(shaCol);

                mapIds.put(tempPK, tempSha);
            }
            while(dbCursor.moveToNext());
        }
        dbCursor.close();
        return mapIds;
    }

    public String getXMLFileName()
    {
        return xmlDBContract.getXMLFileName();
    }
}

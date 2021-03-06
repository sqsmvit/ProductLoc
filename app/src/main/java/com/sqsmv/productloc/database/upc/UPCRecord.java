package com.sqsmv.productloc.database.upc;

import android.database.Cursor;

import com.sqsmv.productloc.database.XMLDBRecord;
import com.sqsmv.productloc.database.product.ProductRecord;

public class UPCRecord extends XMLDBRecord
{
    private String upc;
    private String masNum;

    public UPCRecord(String upc, String masNum, String sha)
    {
        super(new UPCContract());
        this.upc = upc;
        this.masNum = masNum;
        setSha(sha);
    }

    public UPCRecord(Cursor dbCursor)
    {
        super(new UPCContract(), dbCursor);
    }

    public String getUPC()
    {
        return upc;
    }

    public String getMasNum()
    {
        return masNum;
    }

    public void setUPC(String upc)
    {
        this.upc = upc;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    @Override
    public void initRecord()
    {
        setUPC("");
        setMasNum("");
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getUPC(), getMasNum(), getSha()
        };
    }

    @Override
    protected void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(dbCursor.getColumnName(count).equals(UPCContract.COLUMN_NAME_UPC))
            {
                setUPC(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(UPCContract.COLUMN_NAME_MASNUM))
            {
                setMasNum(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(UPCContract.COLUMN_NAME_SHA))
            {
                setSha(dbCursor.getString(count));
            }
        }
    }

    public static UPCRecord buildNewUPCRecordFromCursor(Cursor dbCursor)
    {
        dbCursor.moveToFirst();
        UPCRecord upcRecord = new UPCRecord(dbCursor);
        dbCursor.close();
        return upcRecord;
    }
}

package com.sqsmv.productloc.database.inventory;

import android.database.Cursor;

import com.sqsmv.productloc.database.XMLDBRecord;

public class InventoryRecord extends XMLDBRecord
{
    private String masNum;
    private int cohfp;

    public InventoryRecord(String masNum, int cohfp, String sha)
    {
        super(new InventoryContract());
        this.masNum = masNum;
        this.cohfp = cohfp;
        setSha(sha);
    }

    public InventoryRecord(Cursor dbCursor)
    {
        super(new InventoryContract(), dbCursor);
    }

    public String getMasNum()
    {
        return masNum;
    }

    public int getCOHFP()
    {
        return cohfp;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    public void setCOHFP(int cohfp)
    {
        this.cohfp = cohfp;
    }

    @Override
    public void initRecord()
    {
        setMasNum("");
        setCOHFP(-1);
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getMasNum(), Integer.toString(getCOHFP()), getSha()
        };
    }

    @Override
    protected void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(dbCursor.getColumnName(count).equals(InventoryContract.COLUMN_NAME_MASNUM))
            {
                setMasNum(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(InventoryContract.COLUMN_NAME_COHFP))
            {
                setCOHFP(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(InventoryContract.COLUMN_NAME_SHA))
            {
                setSha(dbCursor.getString(count));
            }
        }
    }
}

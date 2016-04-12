package com.sqsmv.productloc.database.prodloc;

import android.database.Cursor;

import com.sqsmv.productloc.database.XMLDBRecord;

public class ProdLocRecord extends XMLDBRecord
{
    private int pkLocScanLineId;
    private String buildingId;
    private String roomId;
    private String colId;
    private String rowId;
    private String scanStamp;
    private String masNum;
    private String locCode;
    private String name;

    public ProdLocRecord(Cursor dbCursor)
    {
        super(new ProdLocContract(), dbCursor);
    }

    public int getPKLocScanLineId()
    {
        return pkLocScanLineId;
    }

    public String getBuildingId()
    {
        return buildingId;
    }

    public String getRoomId()
    {
        return roomId;
    }

    public String getColId()
    {
        return colId;
    }

    public String getRowId()
    {
        return rowId;
    }

    public String getScanStamp()
    {
        return scanStamp;
    }

    public String getMasNum()
    {
        return masNum;
    }

    public String getLocCode()
    {
        return locCode;
    }

    public String getName()
    {
        return name;
    }

    public void setPkLocScanLineId(int pkLocScanLineId)
    {
        this.pkLocScanLineId = pkLocScanLineId;
    }

    public void setBuildingId(String buildingId)
    {
        this.buildingId = buildingId;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }

    public void setColId(String colId)
    {
        this.colId = colId;
    }

    public void setRowId(String rowId)
    {
        this.rowId = rowId;
    }

    public void setScanStamp(String scanStamp)
    {
        this.scanStamp = scanStamp;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    public void setLocCode(String locCode)
    {
        this.locCode = locCode;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public void initRecord()
    {
        setPkLocScanLineId(-1);
        setBuildingId("");
        setRoomId("");
        setColId("");
        setRowId("");
        setScanStamp("");
        setMasNum("");
        setLocCode("");
        setName("");
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                Integer.toString(getPKLocScanLineId()), getBuildingId(), getRoomId(), getColId(), getRowId(), getScanStamp(),
                getMasNum(), getLocCode(), getName(), getSha()
        };
    }

    @Override
    protected void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(dbCursor.getColumnName(count).equals(ProdLocContract.COLUMN_NAME_PKLOCSCANLINEID))
            {
                setPkLocScanLineId(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProdLocContract.COLUMN_NAME_BUILDINGID))
            {
                setBuildingId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProdLocContract.COLUMN_NAME_ROOMID))
            {
                setRoomId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProdLocContract.COLUMN_NAME_COLID))
            {
                setColId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProdLocContract.COLUMN_NAME_ROWID))
            {
                setRowId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProdLocContract.COLUMN_NAME_SCANSTAMP))
            {
                setScanStamp(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProdLocContract.COLUMN_NAME_MASNUM))
            {
                setMasNum(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProdLocContract.COLUMN_NAME_LOCCODE))
            {
                setLocCode(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProdLocContract.COLUMN_NAME_NAME))
            {
                setName(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(ProdLocContract.COLUMN_NAME_SHA))
            {
                setSha(dbCursor.getString(count));
            }
        }
    }

    public static ProdLocRecord buildNewProdLocRecordFromCursor(Cursor dbCursor)
    {
        dbCursor.moveToFirst();
        ProdLocRecord prodLocRecord = new ProdLocRecord(dbCursor);
        dbCursor.close();
        return prodLocRecord;
    }
}

package com.sqsmv.productloc.database.roomgrid;

import android.database.Cursor;

import com.sqsmv.productloc.database.XMLDBRecord;

public class RoomGridRecord extends XMLDBRecord
{
    private int pkLocScanLineId;
    private String buildingId;
    private String roomId;
    private String colId;
    private String rowId;
    private int ordinal;

    public RoomGridRecord(Cursor dbCursor)
    {
        super(new RoomGridContract(), dbCursor);
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

    public int getOrdinal()
    {
        return ordinal;
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

    public void setOrdinal(int ordinal)
    {
        this.ordinal = ordinal;
    }

    @Override
    public void initRecord()
    {
        setPkLocScanLineId(-1);
        setBuildingId("");
        setRoomId("");
        setColId("");
        setRowId("");
        setOrdinal(-1);
        setSha("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                Integer.toString(getPKLocScanLineId()), getBuildingId(), getRoomId(), getColId(), getRowId(), Integer.toString(getOrdinal()), getSha()
        };
    }

    @Override
    protected void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++)
        {
            if(dbCursor.getColumnName(count).equals(RoomGridContract.COLUMN_NAME_PKLOCSCANLINEID))
            {
                setPkLocScanLineId(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(RoomGridContract.COLUMN_NAME_BUILDINGID))
            {
                setBuildingId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(RoomGridContract.COLUMN_NAME_ROOMID))
            {
                setRoomId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(RoomGridContract.COLUMN_NAME_COLID))
            {
                setColId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(RoomGridContract.COLUMN_NAME_ROWID))
            {
                setRowId(dbCursor.getString(count));
            }
            else if(dbCursor.getColumnName(count).equals(RoomGridContract.COLUMN_NAME_ORDINAL))
            {
                setOrdinal(dbCursor.getInt(count));
            }
            else if(dbCursor.getColumnName(count).equals(RoomGridContract.COLUMN_NAME_SHA))
            {
                setSha(dbCursor.getString(count));
            }
        }
    }

    public static RoomGridRecord buildNewRoomGridRecordFromCursor(Cursor dbCursor)
    {
        dbCursor.moveToFirst();
        RoomGridRecord roomGridRecord = new RoomGridRecord(dbCursor);
        dbCursor.close();
        return roomGridRecord;
    }
}

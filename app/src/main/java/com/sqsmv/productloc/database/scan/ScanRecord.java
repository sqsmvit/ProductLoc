package com.sqsmv.productloc.database.scan;

import android.database.Cursor;

import com.sqsmv.productloc.database.DBRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author ChrisS
 *
 */
public class ScanRecord extends DBRecord
{
    private String id;
    private String masNum;
    private String building;
    private String room;
    private String col;
    private String row;
    private Date createstamp;

    public ScanRecord(String id, String masNum, String building, String room, String col, String row)
    {
        super(new ScanContract());
        this.id = id;
        this.masNum = masNum;
        this.building = building;
        this.room = room;
        this.col = col;
        this.row = row;
        this.createstamp = new Date();
    }

    public ScanRecord(String masNum,  String building, String room, String col, String row)
    {
        this("null", masNum, building, room, col, row);
    }


    public ScanRecord(Cursor dbCursor)
    {
        super(new ScanContract(), dbCursor);
    }

    public String initDate()
    {
        Date today = new Date();
        SimpleDateFormat dateFmt = new SimpleDateFormat("MM/dd/yy", Locale.US);
        return dateFmt.format(today);
    }

    public String getId()
    {
        return id;
    }

    public String getMasNum()
    {
        return masNum;
    }

    public String getBuilding() {
        return building;
    }

    public String getRoom() {
        return room;
    }

    public String getCol() {
        return col;
    }

    public String getRow() {
        return row;
    }

    public Date getCreatestamp() {
        return createstamp;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public void setRow(String row) {
        this.row = row;
    }

    @Override
    public void initRecord()
    {
        setId("null");
        setMasNum("");
        setBuilding("");
        setRoom("");
        setCol("");
        setRow("");
        createstamp = new Date();
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getId(), getMasNum(), getBuilding(), getRoom(), getCol(), getRow(), getCreatestamp().toString()
        };
    }

    @Override
    protected void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++) {
            if (dbCursor.getColumnName(count).equals(ScanContract._ID)) {
                setId(dbCursor.getString(count));
            } else if (dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_MASNUM)) {
                setMasNum(dbCursor.getString(count));
            } else if (dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_BUILDING)) {
                setBuilding(dbCursor.getString(count));
            } else if (dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_ROOM)) {
                setRoom(dbCursor.getString(count));
            } else if (dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_COL)) {
                setCol(dbCursor.getString(count));
            } else if (dbCursor.getColumnName(count).equals(ScanContract.COLUMN_NAME_ROW)) {
                setRow(dbCursor.getString(count));
            }
        }
    }
}

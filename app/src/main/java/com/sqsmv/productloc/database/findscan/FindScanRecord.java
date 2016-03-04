package com.sqsmv.productloc.database.findscan;

import android.database.Cursor;

import com.sqsmv.productloc.database.DBRecord;

import java.util.Date;

/**
 * @author ChrisS
 *
 */
public class FindScanRecord extends DBRecord
{
    private String id;
    private String masNum;
    private Date createStamp;
    private String wh1LocCode;
    private String oLocCode;
    private String readingLocCode;
    private String name;

    public FindScanRecord()
    {
        super(new FindScanContract());
        initRecord();
    }

    public FindScanRecord(String id, String masNum, String whLocCode, String oLocCode, String readingLocCode, String name)
    {
        super(new FindScanContract());
        setId(id);
        setMasNum(masNum);
        createStamp = new Date();
        setWH1LocCode(whLocCode);
        setOLocCode(oLocCode);
        setReadingLocCode(readingLocCode);
        setName(name);
    }

    public FindScanRecord(String masNum, String whLocCode, String oLocCode, String readingLocCode, String name)
    {
        this("null", masNum, whLocCode, oLocCode, readingLocCode, name);
    }


    public FindScanRecord(Cursor dbCursor)
    {
        super(new FindScanContract(), dbCursor);
    }

    public String getId()
    {
        return id;
    }

    public String getMasNum()
    {
        return masNum;
    }

    public Date getCreateStamp()
    {
        return createStamp;
    }

    public String getWH1LocCode()
    {
        return wh1LocCode;
    }

    public String getOLocCode()
    {
        return oLocCode;
    }

    public String getReadingLocCode()
    {
        return readingLocCode;
    }

    public String getName()
    {
        return name;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setMasNum(String masNum)
    {
        this.masNum = masNum;
    }

    public void setWH1LocCode(String whLocCode)
    {
        if(whLocCode.isEmpty())
        {
            whLocCode = "NF";
        }
        this.wh1LocCode = whLocCode;
    }

    public void setOLocCode(String oLocCode)
    {
        if(oLocCode.isEmpty())
        {
            oLocCode = "NF";
        }
        this.oLocCode = oLocCode;
    }

    public void setReadingLocCode(String readingLocCode)
    {
        if(readingLocCode.isEmpty())
        {
            readingLocCode = "NF";
        }
        this.readingLocCode = readingLocCode;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public void initRecord()
    {
        setId("null");
        setMasNum("");
        createStamp = new Date();
        setWH1LocCode("");
        setOLocCode("");
        setName("");
    }

    @Override
    public String[] getTableInsertData()
    {
        return new String[] {
                getId(), getMasNum(), getCreateStamp().toString(), getWH1LocCode(), getOLocCode(), getReadingLocCode(), getName()
        };
    }

    @Override
    protected void setFromCursor(Cursor dbCursor)
    {
        for(int count = 0; count < dbCursor.getColumnCount(); count++) {
            if (dbCursor.getColumnName(count).equals(FindScanContract._ID)) {
                setId(dbCursor.getString(count));
            } else if (dbCursor.getColumnName(count).equals(FindScanContract.COLUMN_NAME_MASNUM)) {
                setMasNum(dbCursor.getString(count));
            } else if (dbCursor.getColumnName(count).equals(FindScanContract.COLUMN_NAME_WH1LOCCODE)) {
                setWH1LocCode(dbCursor.getString(count));
            } else if (dbCursor.getColumnName(count).equals(FindScanContract.COLUMN_NAME_OLOCCODE)) {
                setOLocCode(dbCursor.getString(count));
            } else if (dbCursor.getColumnName(count).equals(FindScanContract.COLUMN_NAME_READINGLOCCODE)) {
                setReadingLocCode(dbCursor.getString(count));
            } else if (dbCursor.getColumnName(count).equals(FindScanContract.COLUMN_NAME_NAME)) {
                setName(dbCursor.getString(count));
            }
        }
    }
}

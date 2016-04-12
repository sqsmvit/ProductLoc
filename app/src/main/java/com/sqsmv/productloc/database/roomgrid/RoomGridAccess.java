package com.sqsmv.productloc.database.roomgrid;

import android.database.Cursor;

import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.QueryBuilder;
import com.sqsmv.productloc.database.XMLDBAccess;


public class RoomGridAccess extends XMLDBAccess
{
    public RoomGridAccess(DBAdapter dbAdapter)
    {
        super(dbAdapter, new RoomGridContract());
    }

    public boolean isValidLocation(String buildingId, String roomId, String rowId, String colId)
    {
        String[] selectColumns = new String[] {RoomGridContract.COLUMN_NAME_PKLOCSCANLINEID};
        String[] whereColumns = new String[] {RoomGridContract.COLUMN_NAME_BUILDINGID, RoomGridContract.COLUMN_NAME_ROOMID,
                                              RoomGridContract.COLUMN_NAME_ROWID, RoomGridContract.COLUMN_NAME_COLID};
        String[] args = new String[] {buildingId, roomId, rowId, colId};
        String query = QueryBuilder.buildSelectQuery(getTableName(), selectColumns, whereColumns);
        Cursor cursor = getDB().rawQuery(query, args);
        return cursor.getCount() > 0;
    }
}

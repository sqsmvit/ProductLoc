package com.sqsmv.productloc.database.prodloc;

import android.database.Cursor;

import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.QueryBuilder;
import com.sqsmv.productloc.database.XMLDBAccess;


public class ProdLocAccess extends XMLDBAccess
{
    public ProdLocAccess(DBAdapter dbAdapter)
    {
        super(dbAdapter, new ProdLocContract());
    }

    public Cursor selectByMasnumWH1(String masnum)
    {
        String[] selectColumns = new String[]{"*"};
        String[] whereColumns = new String[]{ProdLocContract.COLUMN_NAME_BUILDINGID, ProdLocContract.COLUMN_NAME_ROOMID, ProdLocContract.COLUMN_NAME_MASNUM};
        String query = QueryBuilder.buildSelectQuery(getTableName(), selectColumns, whereColumns) + " ORDER BY " +
                ProdLocContract.COLUMN_NAME_PKLOCSCANLINEID + " DESC";
        String[] args = new String[]{"B", "W1", masnum};
        return getDB().rawQuery(query, args);
    }

    public Cursor selectByMasnumOther(String masnum)
    {
        String query = "SELECT * FROM " + getTableName() +
                " WHERE " + ProdLocContract.COLUMN_NAME_BUILDINGID + " = ? AND " +
                ProdLocContract.COLUMN_NAME_ROOMID + " <> ? AND " +
                ProdLocContract.COLUMN_NAME_MASNUM + " = ? ORDER BY " +
                ProdLocContract.COLUMN_NAME_PKLOCSCANLINEID + " DESC";
        String[] args = new String[]{"B", "W1", masnum};
        return getDB().rawQuery(query, args);
    }

    public Cursor selectByMasnumReading(String masnum)
    {
        String[] selectColumns = new String[]{"*"};
        String[] whereColumns = new String[]{ProdLocContract.COLUMN_NAME_BUILDINGID, ProdLocContract.COLUMN_NAME_MASNUM};
        String query = QueryBuilder.buildSelectQuery(getTableName(), selectColumns, whereColumns) + " ORDER BY " +
                ProdLocContract.COLUMN_NAME_PKLOCSCANLINEID + " DESC";
        String[] args = new String[]{"A", masnum};
        return getDB().rawQuery(query, args);
    }
}

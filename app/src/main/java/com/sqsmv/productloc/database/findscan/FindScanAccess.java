  package com.sqsmv.productloc.database.findscan;

import android.database.Cursor;

import com.sqsmv.productloc.database.DBAccess;
import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.QueryBuilder;

  public class FindScanAccess extends DBAccess
  {
      public FindScanAccess(DBAdapter dbAdapter)
      {
          super(dbAdapter, new FindScanContract());
      }

      public Cursor selectScansForPrint()
      {
          String[] columns = new String[]{FindScanContract.COLUMN_NAME_MASNUM,
                  FindScanContract.COLUMN_NAME_WH1LOCCODE,
                  FindScanContract.COLUMN_NAME_OLOCCODE,
                  FindScanContract.COLUMN_NAME_READINGLOCCODE,
                  FindScanContract.COLUMN_NAME_NAME
          };
          String orderBy = FindScanContract._ID;
          return getDB().query(FindScanContract.TABLE_NAME, columns, null, null, null, null, orderBy);
      }

      public Cursor selectScansForDisplay()
      {
          String query = "SELECT fs.*, i.cohfp FROM FindScan fs LEFT JOIN Inventory i ON fs.masnum = i.masnum ORDER BY _id DESC";
          return getDB().rawQuery(query, null);
      }

      public int getTotalScans()
      {
          Cursor dbCursor = getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), new String[]{FindScanContract._ID}, new String[]{}), null);
          int totalScans = dbCursor.getCount();
          dbCursor.close();
          return totalScans;
      }
  }

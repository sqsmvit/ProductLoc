  package com.sqsmv.productloc.database.scan;

import android.database.Cursor;

import com.sqsmv.productloc.database.DBAccess;
import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.QueryBuilder;

  public class ScanAccess extends DBAccess
  {
      public ScanAccess(DBAdapter dbAdapter)
      {
          super(dbAdapter, new ScanContract());
      }


      public Cursor selectScansForPrint(int exportMode)
      {
          if(exportMode == 0)
          {
              //Normal Mode
              String[] cols = new String[]{ScanContract.COLUMN_NAME_MASNUM,
                      ScanContract.COLUMN_NAME_BUILDING,
                      ScanContract.COLUMN_NAME_ROOM,
                      ScanContract.COLUMN_NAME_COL,
                      ScanContract.COLUMN_NAME_ROW,
                      ScanContract.COLUMN_NAME_CREATESTAMP
              };
              return getDB().query(ScanContract.TABLE_NAME, cols, null, null, null, null, null);
          }
          else
          {
              //Fail for some reason
              return null;
          }
      }

      public Cursor selectScansForDisplay()
      {
          String query = "SELECT s._id, s.masNum, s.room, s.col, s.row, " +
                  "COALESCE(p1.name, p2.name, p3.name, 'Title Not Found') AS name FROM Scan s " +
                  "LEFT JOIN Product p1 ON s.masNum = p1.masnum " +
                  "LEFT JOIN UPC u1 ON s.masNum = u1.upc " +
                        "LEFT JOIN Product p2 ON u1.masnum = p2.masnum " +
                  "LEFT JOIN UPC u2 ON rtrim(s.masNum, '-N') = u2.upc " +
                        "LEFT JOIN Product p3 ON u2.masnum = p3.masnum " +
                  "ORDER BY _id DESC LIMIT 3";
          return getDB().rawQuery(query, null);
      }


      public int getTotalScans()
      {
          Cursor dbCursor = getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), new String[]{ScanContract._ID}, new String[]{}), null);
          int totalScans = dbCursor.getCount();
          dbCursor.close();
          return totalScans;
      }
  }

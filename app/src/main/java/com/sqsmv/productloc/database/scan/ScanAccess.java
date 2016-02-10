  package com.sqsmv.productloc.database.scan;

import android.content.ContentValues;
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
          if(exportMode == 1)
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


      public int getTotalScans()
      {
          Cursor dbCursor = getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), new String[]{ScanContract._ID}, new String[]{}), null);
          int totalScans = dbCursor.getCount();
          dbCursor.close();
          return totalScans;
      }



      public int updateRecordByID(int id, ScanRecord editRecord)
      {
          ContentValues values = new ContentValues();
          values.put(ScanContract.COLUMN_NAME_MASNUM, editRecord.getMasNum());
          values.put(ScanContract.COLUMN_NAME_ROOM, editRecord.getRoom());
          values.put(ScanContract.COLUMN_NAME_ROW, editRecord.getRow());
          values.put(ScanContract.COLUMN_NAME_COL, editRecord.getCol());
          values.put(ScanContract.COLUMN_NAME_BUILDING, editRecord.getBuilding());

          return getDB().update(ScanContract.TABLE_NAME, values, ScanContract._ID + " = ?", new String[]{String.valueOf(id)});
      }
  }

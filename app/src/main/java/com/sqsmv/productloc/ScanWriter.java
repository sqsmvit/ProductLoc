package com.sqsmv.productloc;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import com.sqsmv.productloc.database.scan.ScanContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by s7ry93r on 2/2/16.
 */
public class ScanWriter {
    public static File createExportFile(Context callingContext, Cursor dbCursor, int exportMode) throws IOException
    {
        String fileName = buildFileName(exportMode);
        File exportFile = new File(callingContext.getFilesDir() + File.separator + fileName);
        FileOutputStream output = new FileOutputStream(exportFile, true);

        while(dbCursor.moveToNext())
        {
            String writeString = buildStringFromCursor(dbCursor);

            output.write(writeString.getBytes());
        }

        output.close();
        return exportFile;
    }

    private static String buildStringFromCursor(Cursor dbCursor)
    {
        String writeString = "";
        int columnCount = dbCursor.getColumnCount();

        //Skip pKey
        for(int i = 0; i < columnCount; i++)
        {
           // writeString = dbCursor.getString(count);

            if (dbCursor.getColumnName(i).equals(ScanContract.COLUMN_NAME_CREATESTAMP)){
                writeString += getFMFormattedTimeStamp(dbCursor.getString(i));
            } else {
                writeString += dbCursor.getString(i);
            }

            if(i < columnCount - 1)
            {
                writeString += "\t";
            }
            else
            {
                writeString += "\n";
            }
        }
        return writeString;
    }

    private static String getFMFormattedTimeStamp(String rawDate){
        try{
            return Utilities.reformatTimeStamp(rawDate);
        }
        catch(Exception e){
            Date now = new Date();
            String dateString = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(now);
            return dateString;
        }
    }

    private static String buildFileName(int exportMode)
    {
        String fileName =  Utilities.getDeviceName() + "_" + Utilities.buildCurrentTimestamp() + ".txt";

/*
        switch(exportMode)
        {
            case 3:
                fileName = "BB_" + fileName;
                break;
            case 4:
                fileName = "DR_" + fileName;
                break;
            case 5:
                fileName = "RI_" + fileName;
                break;
            case 6:
                fileName = "S_" + fileName;
                break;
        }
*/

        return fileName;
    }

    public static void writeBackupFile(File exportFile) throws IOException
    {
        File root = new File(Environment.getExternalStorageDirectory().toString() + "/PLbackups");
        root.mkdirs();
        File backupFile = new File(root.getAbsolutePath(), "B_" + exportFile.getName());
        Utilities.copyFile(exportFile, backupFile);
    }}

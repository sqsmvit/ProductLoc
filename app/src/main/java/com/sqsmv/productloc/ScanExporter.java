package com.sqsmv.productloc;

import android.content.Context;

import java.io.File;

/**
 * Created by s7ry93r on 2/2/16.
 */
public class ScanExporter {
    public static boolean exportScan(Context callingContext, File exportFile, int exportMode, boolean fromCommit)
    {
        String exportPath = "/Default/";

        if(exportMode == 1)
        {
            exportPath = "/FMImport/";
        }
        else if(exportMode == 2)
        {
            exportPath = "/LocateScan/";
        }

        return exportDBX(callingContext, exportFile, exportPath, fromCommit);
    }

    private static boolean exportDBX(Context callingContext, File exportFile, String exportPath, boolean fromCommit)
    {
        DropboxManager dropboxManager = new DropboxManager(callingContext);
        if(dropboxManager.hasLinkedAccount())
        {
            String scanPath = exportPath + exportFile.getName();
            dropboxManager.writeToDropbox(exportFile, scanPath, fromCommit, true);
            Utilities.makeToast(callingContext, "File exported to DropBox");
            return true;
        }
        else
        {
            Utilities.makeToast(callingContext, "Error exporting to DropBox");
            return false;
        }
    }
}

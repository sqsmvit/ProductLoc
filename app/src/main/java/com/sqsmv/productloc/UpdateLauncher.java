package com.sqsmv.productloc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

public class UpdateLauncher
{
    private Context context;
    private DroidConfigManager appConfig;

    public UpdateLauncher(Context activityContext)
    {
        context = activityContext;
        appConfig = new DroidConfigManager(activityContext);
    }

    public Thread startDBUpdate(boolean startBlockingThread)
    {
        Intent popIntent = new Intent(context, PopDatabaseService.class);

        context.startService(popIntent);

        final ProgressDialog pausingDialog = new ProgressDialog(context);
        pausingDialog.setTitle("Updating Database");
        pausingDialog.setMessage("Please Stay in Wifi Range...");
        pausingDialog.setCancelable(true);

        Thread pausingDialogThread = new Thread()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(20000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                pausingDialog.dismiss();
            }
        };
        if(startBlockingThread)
        {
            pausingDialog.show();
            pausingDialogThread.start();
        }
        return pausingDialogThread;
    }

    public boolean checkNeedAppUpdate()
    {
        DropboxManager dropboxManager = new DropboxManager(context);
        boolean needUpdate = false;

        String apkFileName = context.getString(R.string.apk_file_name);
        String currentRev = appConfig.accessString(DroidConfigManager.CURRENT_APK_REV, null, "");
        String dbxFileRev = dropboxManager.getDbxFileRev("/out/" + apkFileName);

        if(currentRev.isEmpty())
        {
            appConfig.accessString(DroidConfigManager.CURRENT_APK_REV, dbxFileRev, "");
        }
        else if(!currentRev.equals(dbxFileRev) || appConfig.accessString(DroidConfigManager.PRIOR_VERSION, null, "").equals(Utilities.getVersion(context)))
        {
            needUpdate = true;
        }

        return needUpdate;
    }

    public void startAppUpdate()
    {
        appConfig.accessString(DroidConfigManager.PRIOR_VERSION, Utilities.getVersion(context), "");
        ProgressDialog.show(context, "Updating Application", "Please Stay in Wifi Range...", true);
        Intent appUpdateIntent = new Intent(context, AppUpdateService.class);
        context.startService(appUpdateIntent);
    }
}

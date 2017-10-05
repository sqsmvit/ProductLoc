package com.sqsmv.productloc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.core.android.Auth;

import java.io.File;
import java.util.Date;


public class LoadActivity extends Activity {

    private DroidConfigManager appConfig;
    private static final String TAG = "LoadActivity";
    private DropboxManager dropboxManager;
    private UpdateLauncher updateLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "in onCreate and for the LoadActivity!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Log.d(TAG, "onCreate: fired.");

        appConfig = new DroidConfigManager(this);
        dropboxManager = new DropboxManager(this);
        updateLauncher = new UpdateLauncher(this);

        TextView textVersionInfo = (TextView)findViewById(R.id.versionInfo);
        textVersionInfo.setText("Version: " + Utilities.getVersion(this));


        Button startScanningButton = (Button)findViewById(R.id.startScanningButton);
        startScanningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });




    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "in onResume and for the LoadActivity!");
        super.onResume();

        String accessToken = appConfig.accessString(DroidConfigManager.DROPBOX_ACCESS_TOKEN, null, null);
        if (accessToken == null)
        {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null)
            {
                appConfig.accessString(DroidConfigManager.DROPBOX_ACCESS_TOKEN, accessToken, null);
                dropboxManager.initDbxClient(accessToken);
            }
            else
            {
                if(Utilities.checkWifi(this))
                {
                    dropboxManager.linkDropboxAccount();
                }
                else
                {
                    Utilities.makeLongToast(this, "Must be connected to WiFi to link to Dropbox!");
                    finish();
                }
            }
        }
        else
        {
            dropboxManager.initDbxClient(accessToken);
        }
    }

    public void startScan()
    {
        Log.d(TAG, "in startScan and for the LoadActivity!");

        String buildDate = Utilities.formatYYMMDDDate(new Date());

        if(!buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, "")) && Utilities.checkWifi(this))
        {
            if(updateLauncher.checkNeedAppUpdate())
            {
                updateLauncher.startAppUpdate();
            }
            else
            {
                appConfig.accessString(DroidConfigManager.PRIOR_VERSION, "", "");
                Utilities.cleanFolder(new File(Environment.getExternalStorageDirectory().toString() + "/backups"), 180);
                launchDBUpdate(); //here it is .. the call to do the threaded update
                appConfig.accessString(DroidConfigManager.BUILD_DATE, buildDate, "");
            }
        }
        else if(buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, "")))
        {
            goToScanActivity();
        }
        else
        {
            Utilities.makeToast(this, getString(R.string.ERR_WIFI));
        }
    }

    private void launchDBUpdate()
    {
        Log.d(TAG, "in launchDBUpdate and for the LoadActivity!");

        final Thread blockingThread = updateLauncher.startDBUpdate();
        //about this thread ... it's a GUI thing
        new Thread()
        {
            @Override
            public void run() //current thread
            {
                try
                {
                    blockingThread.join();
                    goToScanActivity();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start(); //new thread
    }

    private void goToScanActivity()
    {
        Log.d(TAG, "in goToScanActivity and for the LoadActivity!");

        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

}

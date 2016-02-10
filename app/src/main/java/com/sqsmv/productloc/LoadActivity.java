package com.sqsmv.productloc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        if(Utilities.checkWifi(this))
        {
            if(dropboxManager.finishAuthentication())
            {
                String accessToken = dropboxManager.getOAuth2AccessToken();
                appConfig.accessString(DroidConfigManager.DROPBOX_ACCESS_TOKEN, accessToken, "");
            }
            linkDropboxAccount();
            updateLauncher = new UpdateLauncher(this);
        }
        else
        {
            Utilities.makeLongToast(this, getString(R.string.ERR_WIFI));
            finish();
        }

    }

    private void linkDropboxAccount()
    {
        Log.d(TAG, "in linkDropboxAccount and for the LoadActivity!");

        String accessToken = appConfig.accessString(DroidConfigManager.DROPBOX_ACCESS_TOKEN, null, "");
        if(!accessToken.isEmpty())
        {
            dropboxManager.setStaticOAuth2AccessToken(accessToken);
        }
        else
        {
            dropboxManager.linkDropboxAccount();
        }
    }

    public void startScan()
    {
        Log.d(TAG, "in startScan and for the LoadActivity!");

        String buildDate = Utilities.formatYYMMDDDate(new Date());

        if(!(buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, ""))))
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
        else
        {
            goToScanActivity();
        }
    }

    private void launchDBUpdate()
    {
        Log.d(TAG, "in launchDBUpdate and for the LoadActivity!");

        final Thread blockingThread = updateLauncher.startDBUpdate(true);
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

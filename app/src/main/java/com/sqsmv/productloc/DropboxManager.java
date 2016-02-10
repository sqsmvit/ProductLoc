package com.sqsmv.productloc;

import android.content.Context;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DropboxManager
{
    private static final String TAG = "CheckDropboxActivity";

    private Context context;
    private DropboxAPI<AndroidAuthSession> dropboxAPI;

    private static String oAuth2AccessToken;

    public DropboxManager(Context activityContext)
    {
        context = activityContext;

        String dropboxAppKey = context.getString(R.string.DBX_APP_KEY);
        String dropboxAppSecret = context.getString(R.string.DBX_SECRET_KEY);

        AppKeyPair appKeys = new AppKeyPair(dropboxAppKey, dropboxAppSecret);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        dropboxAPI = new DropboxAPI<AndroidAuthSession>(session);

        if(oAuth2AccessToken != null)
        {
            setOAuth2AccessToken(oAuth2AccessToken);
        }
    }

    public void linkDropboxAccount()
    {
        dropboxAPI.getSession().startOAuth2Authentication(context);
    }

    public String getOAuth2AccessToken()
    {
        return dropboxAPI.getSession().getOAuth2AccessToken();
    }

    public void setStaticOAuth2AccessToken(String accessToken)
    {
        oAuth2AccessToken = accessToken;
        setOAuth2AccessToken(oAuth2AccessToken);
    }

    public boolean finishAuthentication()
    {
        boolean success = true;
        if(dropboxAPI.getSession().authenticationSuccessful())
        {
            try
            {
                dropboxAPI.getSession().finishAuthentication();
            }
            catch(IllegalStateException e)
            {
                e.printStackTrace();
                success = false;
            }
        }
        else
        {
            success = false;
        }
        return success;
    }

    public boolean hasLinkedAccount()
    {
        return dropboxAPI.getSession().isLinked();
    }

    public Thread writeToStorage(final String dbxFilePath, String downloadPath, boolean fromMainThread)
    {
        Log.d(TAG, "writeToStorage: DROPBOX IS CALLED!");
        final File downloadFile = new File(downloadPath);
        Thread downloadThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    FileOutputStream outputStream = new FileOutputStream(downloadFile);
                    dropboxAPI.getFile(dbxFilePath, null, outputStream, null);
                }
                catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch(DropboxException e)
                {
                    e.printStackTrace();
                }
            };
        };
        if(fromMainThread)
        {
            downloadThread.start();
        }
        else
        {
            downloadThread.run();
        }
        return downloadThread;
    }

    public Thread writeToDropbox(final File fileToWrite, final String dbxFilePath, final boolean shouldSteal, boolean fromMainThread)
    {
        Thread uploadThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    FileInputStream inputStream = new FileInputStream(fileToWrite);
                    dropboxAPI.putFile(dbxFilePath, inputStream, fileToWrite.length(), null, null);
                    if(shouldSteal)
                    {
                        fileToWrite.delete();
                    }
                }
                catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch(DropboxException e)
                {
                    e.printStackTrace();
                }
            };
        };
        if(fromMainThread)
        {
            uploadThread.start();
        }
        else
        {
            uploadThread.run();
        }

        return uploadThread;
    }

    public String getDbxFileRev(final String dbxFilePath)
    {
        final dbxFileMetadata metadata = new dbxFileMetadata();
        Thread metadataThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    metadata.setDropboxEntry(dropboxAPI.metadata(dbxFilePath, 1, null, false, null));
                }
                catch(DropboxException e)
                {
                    e.printStackTrace();
                }
            }
        };
;
        metadataThread.start();
        try
        {
            metadataThread.join();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        return metadata.getDropboxEntry().rev;
    }

    private void setOAuth2AccessToken(String accessToken)
    {
        dropboxAPI.getSession().setOAuth2AccessToken(accessToken);
    }

    private class dbxFileMetadata
    {
        DropboxAPI.Entry dropboxEntry;

        public DropboxAPI.Entry getDropboxEntry()
        {
            return dropboxEntry;
        }

        public void setDropboxEntry(DropboxAPI.Entry dbxEntry)
        {
            dropboxEntry = dbxEntry;
        }
    }
}

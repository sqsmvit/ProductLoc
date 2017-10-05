package com.sqsmv.productloc;

import android.content.Context;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Manager class for the Dropbox API. Controls linking, access, reading, and writing to an account.
 */
public class DropboxManager
{
    private static final String TAG = "DropboxManager";

    private Context context;

    private static DbxClientV2 dbxClientV2;

    /**
     * Constructor.
     * @param context    The Context of the Activity or Service DropboxManager was instantiated for.
     */
    public DropboxManager(Context context)
    {
        this.context = context;
    }

    /**
     * Launches the Dropbox app if it is installed, the browser if not, for linking a Dropbox account. Access token is set when an account is
     * successfully linked.
     */
    public void linkDropboxAccount()
    {
        Auth.startOAuth2Authentication(context, context.getString(R.string.DBX_APP_KEY));
    }

    /**
     * Gets the access token stored in the API session.
     * @return The access token stored in the API session.
     */
    public String getOAuth2AccessToken()
    {
        return "";//dropboxAPI.getSession().getOAuth2AccessToken();
    }

    /**
     * Initializes the client connection to a Dropbox account.
     * @param accessToken     The Dropbox access token for the Dropbox account
     */
    public void initDbxClient(String accessToken)
    {
        if (dbxClientV2 == null)
        {
            dbxClientV2 = new DbxClientV2(new DbxRequestConfig(context.getString(R.string.app_name)), accessToken);
        }
    }

    /**
     * Writes a file from Dropbox to local storage.
     * @param dbxFilePath        The path to the file on Dropbox.
     * @param downloadPath       The path to the write location on local storage.
     * @param isAsyncDownload    Whether the write should be done asynchronously or not.
     */
    public void writeToStorage(final String dbxFilePath, String downloadPath, boolean isAsyncDownload)
    {
        final File downloadFile = new File(downloadPath);
        Thread downloadThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    FileOutputStream outputStream = new FileOutputStream(downloadFile);
                    dbxClientV2.files().download(dbxFilePath).download(outputStream);
                    outputStream.close();
                }
                catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                catch(DbxException e)
                {
                    e.printStackTrace();
                }
            };
        };
        downloadThread.start();
        if(!isAsyncDownload)
        {
            try
            {
                downloadThread.join();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes a file from local storage to Dropbox.
     * @param fileToWrite        The File to write to Dropbox.
     * @param dbxFilePath        The path to the write location on Dropbox.
     * @return true if the write to Dropbox was successful, otherwise false.
     */
    public boolean writeToDropbox(final File fileToWrite, final String dbxFilePath)
    {
        final boolean[] writeSuccessful = new boolean[1];
        writeSuccessful[0] = false;
        Thread uploadThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    FileInputStream inputStream = new FileInputStream(fileToWrite);
                    dbxClientV2.files().uploadBuilder(dbxFilePath).withMode(WriteMode.OVERWRITE).uploadAndFinish(inputStream);
                    writeSuccessful[0] = true;
                }
                catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                catch(DbxException e)
                {
                    e.printStackTrace();
                }
            };
        };
        uploadThread.start();
        try
        {
            uploadThread.join();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        return writeSuccessful[0];
    }

    /**
     * Gets the revision id metadata info of a file on Dropbox.
     * @param dbxFilePath    The path to the file on Dropbox to check.
     * @return The revision id metadata info of the file.
     */
    public String getDbxFileRev(final String dbxFilePath)
    {
        final String rev[] = new String[1];
        Thread metadataThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    rev[0] = dbxClientV2.files().download(dbxFilePath).getResult().getRev();
                }
                catch(DbxException e)
                {
                    e.printStackTrace();
                }
            }
        };
        metadataThread.start();
        try
        {
            metadataThread.join();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        return rev[0];
    }
}

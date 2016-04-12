package com.sqsmv.productloc;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.XMLDBAccess;
import com.sqsmv.productloc.database.inventory.InventoryAccess;
import com.sqsmv.productloc.database.prodloc.ProdLocAccess;
import com.sqsmv.productloc.database.product.ProductAccess;
import com.sqsmv.productloc.database.roomgrid.RoomGridAccess;
import com.sqsmv.productloc.database.upc.UPCAccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//import com.sqsmv.sqsscanner.database.productlens.ProductLensAccess;


/* special note ... this is a service ... woot woot! */
public class PopDatabaseService extends IntentService
{
	private static final String TAG = "PopDatabaseService";

    private String zipFileName = "pfiles.zip";
	
	public PopDatabaseService()
    {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent)
    {
        makeNotification("Dropbox Download Started", false);
        DBAdapter dbAdapter = new DBAdapter(this);
        Semaphore popDBSemaphore = new Semaphore(2, true);
        boolean isLockReleased = false;
        boolean isSlowUpdate = Utilities.totalDeviceMemory(this) <= 1024;

        //Download files.zip from DropBox
        downloadDBXZip();
        File zipFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + zipFileName);
        try
        {
            unzip(zipFile);
            zipFile.delete();

            resetTables(dbAdapter);
            makeNotification("Database Update Started", false);

            XMLDBAccess[] necessaryUpdateThreads = new XMLDBAccess[] {new ProdLocAccess(dbAdapter), new UPCAccess(dbAdapter),
                                                                      new RoomGridAccess(dbAdapter)};
            XMLDBAccess[] otherDataAccesses = new XMLDBAccess[] {new ProductAccess(dbAdapter), new InventoryAccess(dbAdapter)};
            ArrayList<Thread> updateThreads = new ArrayList<Thread>();
            XMLDBAccess[][] allXMLDBAccesses = new XMLDBAccess[][] {necessaryUpdateThreads, otherDataAccesses};
            for(XMLDBAccess[] xmlDBAccesses : allXMLDBAccesses)
            {
                for(XMLDBAccess xmlDBAccess : xmlDBAccesses)
                {
                    updateThreads.add(new FMDumpHandler(xmlDBAccess,isSlowUpdate, popDBSemaphore));
                }
            }
            isLockReleased = startUpdateThreads(updateThreads, necessaryUpdateThreads.length);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        if(!isLockReleased)
        {
            UpdateLauncher.releaseUpdateLock();
        }
        dbAdapter.close();
        makeNotification("Database Update Finished", true);
	}

    private void downloadDBXZip()
    {
        Log.d(TAG, "in copyDBXFile");
        DropboxManager dbxMan = new DropboxManager(this);

        dbxMan.writeToStorage("/out/" + zipFileName, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +
                "/" + zipFileName, false);
    }

    private void unzip(File zipFile) throws IOException
    {
        byte[] buffer = new byte[1024];

        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        //get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();
        String fileDirectory = zipFile.getParent();

        while(ze!=null)
        {
            String fileName = ze.getName();
            File newFile = new File(fileDirectory + File.separator + fileName);
            new File(newFile.getParent()).mkdirs();

            FileOutputStream fos = new FileOutputStream(newFile);

            int len;
            while ((len = zis.read(buffer)) > 0)
            {
                fos.write(buffer, 0, len);
            }

            fos.close();
            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }

	private void makeNotification(String message, boolean finished)
	{
        Log.d(TAG, "makeNotification: HEY HEY I'M HERE");
        NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setContentTitle("ProductLoc")
		        .setContentText(message)
		        .setTicker(message);
		long[] pattern = {0, 1000, 500, 1000};
		if(finished)
		{
	        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
			mBuilder.setVibrate(pattern);
		}
		else
        {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }
		// Creates an explicit intent for an Activity in your app
		Intent emptyIntent = new Intent();

		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT); 
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}

    private boolean startUpdateThreads(ArrayList<Thread> updateThreads, int numNecessaryUpdates)
    {
        int count = 0;
        boolean isLockReleased = false;
        for(Thread updateThread : updateThreads)
        {
            updateThread.start();
        }
        for(Thread updateThread : updateThreads)
        {
            try
            {
                updateThread.join();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            count++;
            if(count == numNecessaryUpdates)
            {
                UpdateLauncher.releaseUpdateLock();
                isLockReleased = true;
            }
        }
        return isLockReleased;
    }

    private void resetTables(DBAdapter dbAdapter)
    {
        DroidConfigManager droidConfigManager = new DroidConfigManager(this);
        Date currentDate = new Date();

        ProdLocAccess prodLocAccess = new ProdLocAccess(dbAdapter);
        prodLocAccess.open();
        prodLocAccess.reset();
    }
}

package com.sqsmv.productloc;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utilities
{
    private static final String TAG = "Utilities";

    public static void cleanFolder(File root, long days)
    {
        Log.d(TAG, "In cleanFolder()");
        if (root.exists())
        {
            File[] fileList = root.listFiles();

            long eligibleForDeletion = System.currentTimeMillis()
                    - (days * 24 * 60 * 60 * 1000L);

            for (File listFile : fileList)
            {
                if(listFile.lastModified() < eligibleForDeletion)
                {
                    if (!listFile.delete())
                        Log.w(TAG, "Unable to Delete File: " + listFile.getName());
                }
            }
        }
    }

    public static void makeToast(Context callingContext, String message)
    {
        Log.d(TAG, "In makeToast()");
        Toast.makeText(callingContext, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeLongToast(Context callingContext, String message)
    {
        Log.d(TAG, "In makeToast()");
        Toast.makeText(callingContext, message, Toast.LENGTH_LONG).show();
    }

    public static String getDefaultGateway(Context callingContext)
    {
        Log.d(TAG, "In getDefaultGateway()");
        WifiManager wifi = (WifiManager)callingContext.getSystemService(callingContext.WIFI_SERVICE);
        DhcpInfo d = wifi.getDhcpInfo();
        String s_gateway = intToIp(d.gateway);
        return s_gateway;
    }

    private static String intToIp(int i)
    {
        Log.d(TAG, "In intToIp()");
        return ((i >> 24 ) & 0xFF ) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ( i & 0xFF);
    }

    public static String getDeviceName()
    {
        return BluetoothAdapter.getDefaultAdapter().getName();
    }

    public static String getVersion(Context callingContext)
    {
        String version = "";

        try
        {
            PackageInfo packageInfo = callingContext.getPackageManager().getPackageInfo(callingContext.getPackageName(), 0);
            version = packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return version;
    }

    public static boolean checkWifi(Context callingContext)
    {
        ConnectivityManager connManager = (ConnectivityManager)callingContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String message = String.format("in checkWifi and is connected is %b", wifi.isConnected());
        Log.d(TAG, message);
        return wifi.isConnected();
    }

    public static long totalDeviceMemory(Context callingContext)
    {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)callingContext.getSystemService(callingContext.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi.totalMem / 1048576;
    }

    public static String formatYYMMDDDate(Date formateDate)
    {
        return new SimpleDateFormat("yyMMdd", Locale.US).format(formateDate);
    }

    public static String reformatTimeStamp(String parseString) throws ParseException
    {
        Date dt = new SimpleDateFormat("E MMM d hh:mm:ss z y", Locale.US).parse(parseString);
        return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US).format(dt);
    }

    public static Date parseYYMMDDString(String parseString) throws ParseException
    {
        return new SimpleDateFormat("yyMMdd", Locale.US).parse(parseString);
    }

    public static String buildCurrentTimestamp()
    {
        Date now = new Date();
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyMMdd_kkmm", Locale.US);
        return timestampFormat.format(now);
    }

    public static void copyFile(File src, File dest) throws IOException
    {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);

        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }

    /**
     * Sounds the Android device's set notification sound to alert the user.
     * @param callingContext    The Context for the Activity or Service making the alert.
     */
    public static void alertNotificationSound(Context callingContext)
    {
        AudioManager audioManager = (AudioManager)callingContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_ALLOW_RINGER_MODES);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        RingtoneManager.getRingtone(callingContext, notification).play();
    }

    /**
     * Sounds the Android device's set alarm sound to alert the user.
     * @param callingContext           The Context for the Activity or Service making the alert.
     * @param alertTimeMilliseconds    The time in milliseconds that the alarm should ring for.
     */
    public static void alertAlarm(Context callingContext, final long alertTimeMilliseconds)
    {
        AudioManager audioManager = (AudioManager)callingContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_ALLOW_RINGER_MODES);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone ringTone = RingtoneManager.getRingtone(callingContext, notification);
        ringTone.play();
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(alertTimeMilliseconds);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    ringTone.stop();
                }
            }
        }.start();
    }

    /**
     * Vibrates the Android device to alert the user.
     * @param callingContext    The Context for the Activity or Service making the alert.
     * @param vibratePattern    The long[] containing the pattern the vibration should be performed with.
     */
    public static void alertVibrate(Context callingContext, long[] vibratePattern)
    {
        ((Vibrator)callingContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(vibratePattern, -1);
    }
}

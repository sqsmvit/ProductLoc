package com.sqsmv.productloc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.socketmobile.apiintegration.ScanAPIApplication;
import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.findscan.FindScanAccess;
import com.sqsmv.productloc.database.findscan.FindScanRecord;
import com.sqsmv.productloc.database.prodloc.ProdLocAccess;
import com.sqsmv.productloc.database.prodloc.ProdLocRecord;
import com.sqsmv.productloc.database.product.ProductAccess;
import com.sqsmv.productloc.database.product.ProductRecord;
import com.sqsmv.productloc.database.upc.UPCAccess;
import com.sqsmv.productloc.database.upc.UPCRecord;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindScanActivity extends Activity
{
    private static final String TAG = "FindScanActivity";

    private DroidConfigManager appConfig;
    private DBAdapter dbAdapter;
    private UPCAccess upcAccess;
    private ProductAccess productAccess;
    private ProdLocAccess prodLocAccess;
    private FindScanAccess findScanAccess;

    private Pattern upcRegex;
    private Pattern sqsRegex;

    private EditText findScanInput;
    private ListView findScanList;

    private SimpleCursorAdapter findScanListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_scan);

        appConfig = new DroidConfigManager(this);
        dbAdapter = new DBAdapter(this);
        upcAccess = new UPCAccess(dbAdapter);
        productAccess = new ProductAccess(dbAdapter);
        prodLocAccess = new ProdLocAccess(dbAdapter);
        findScanAccess = new FindScanAccess(dbAdapter);

        upcRegex = Pattern.compile("^\\d{12,13}(-N)?$");
        sqsRegex = Pattern.compile("^SQS(\\d+)\\d{3}$");

        findScanInput = (EditText)findViewById(R.id.findScan);
        findScanList = (ListView)findViewById(R.id.findScanList);

        findScanListAdapter = new SimpleCursorAdapter(this, R.layout.row_find_scan, null,
                new String[]{"masNum", "cohfp", "name", "wh1LocCode", "oLocCode", "readingLocCode"},
                new int[]{R.id.findScanID, R.id.findCOHPulls, R.id.findScanTitle, R.id.findScanWH1, R.id.findScanO, R.id.findScanReading}, 0);

        findScanList.setAdapter(findScanListAdapter);

        setListeners();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "in onStart and for the FindScanActivity!");

        regBroadCastReceivers();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "in onResume");

        upcAccess.open();
        productAccess.open();
        prodLocAccess.open();
        findScanAccess.open();
        updateList();
        String buildDate = Utilities.formatYYMMDDDate(new Date());
        if(!(buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, ""))))
        {
            finish();
        }
    }

    @Override
    protected void onStop()
    {
        Log.d(TAG, "in onStop and for the FindScanActivity!");
        boolean scannerLock = appConfig.accessBoolean(DroidConfigManager.SCANNER_LOCK, null, false);
        unregisterReceiver(receiver);

        super.onStop();
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "in onPause for the FindScanActivity!");

        dbAdapter.close();
        super.onPause();
    }

    private void setListeners()
    {
        findScanInput.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE && !v.getText().toString().isEmpty())
                {
                    handleInput();
                }
                return true;
            }
        });
        findViewById(R.id.findBackButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        findViewById(R.id.findSubmitButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handleInput();
            }
        });

        findViewById(R.id.findDelAllButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clearScans();
            }
        });

        findViewById(R.id.findCommitButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                commitScans();
            }
        });

        findScanList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                deleteRow(position);
                return true;
            }
        });
    }

    private void regBroadCastReceivers()
    {
        IntentFilter filter;
        filter = new IntentFilter(ScanAPIApplication.NOTIFY_SCANPI_INITIALIZED);
        registerReceiver(receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.NOTIFY_SCANNER_ARRIVAL);
        registerReceiver(receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.NOTIFY_SCANNER_REMOVAL);
        registerReceiver(receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.NOTIFY_DECODED_DATA);
        registerReceiver(receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.NOTIFY_ERROR_MESSAGE);
        registerReceiver(receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.NOTIFY_CLOSE_ACTIVITY);
        registerReceiver(receiver, filter);
    }

    private void clearScans()
    {
        if(findScanAccess.getTotalScans() > 0)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder
                    .setTitle("Confirm Delete")
                    .setMessage("Delete All Scans?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            performMassDelete();
                            updateList();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    private void commitScans()
    {
        if(findScanAccess.getTotalScans() > 0)
        {
            if(Utilities.checkWifi(this))
            {
                try
                {
                    int exportModeChoice = 1;
                    File exportFile = writeFromDB(exportModeChoice);
                    if(ScanExporter.exportScan(this, exportFile, exportModeChoice, true))
                    {
                        performMassDelete();
                        Utilities.makeToast(this, "File successfully exported to Dropbox");
                    }
                    else
                    {
                        Utilities.alertAlarm(this, 2000);
                        Utilities.alertVibrate(this, new long[]{0, 500, 500, 500, 500});
                        Utilities.makeToast(this, "Error exporting to DropBox");
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    Utilities.makeLongToast(this, "Error Writing Files.");
                }
            }
            else
            {
                Utilities.makeToast(this, "Not Connected to WiFi - Cannot Commit Scan.");
            }
        }
        else
        {
            Utilities.makeToast(this, "No Scans to Commit.");
        }
    }

    private void handleScanInput(String scanInput)
    {
        Matcher upcMatch = upcRegex.matcher(scanInput);
        Matcher sqsMatch = sqsRegex.matcher(scanInput);
        if(upcMatch.find())
        {
            String upc = scanInput + "-N";
            UPCRecord upcRecord = UPCRecord.buildNewUPCRecordFromCursor(upcAccess.selectByPk(upc));
            if(!upcRecord.getMasNum().isEmpty())
            {
                findScanInput.setText(upcRecord.getMasNum());
            }
            else
            {
                findScanInput.setText(upc);
            }
        }
        else if(sqsMatch.find())
        {
            findScanInput.setText(sqsMatch.group(1));
        }
        else
        {
            Utilities.alertNotificationSound(this);
            Utilities.alertVibrate(this, new long[]{0, 500, 250, 500});
            Utilities.makeToast(this, "Did not understand Scan (NOT Recognized)!");
        }
        handleInput();
    }

    private void handleInput()
    {
        String input = findScanInput.getText().toString();
        if(!input.isEmpty())
        {
            ProductRecord productRecord = ProductRecord.buildNewProductRecordFromCursor(productAccess.selectByPk(input));
            ProdLocRecord prodLocRecordWH1 = ProdLocRecord.buildNewProdLocRecordFromCursor(prodLocAccess.selectByMasnumWH1(input));
            ProdLocRecord prodLocRecordOther = ProdLocRecord.buildNewProdLocRecordFromCursor(prodLocAccess.selectByMasnumOther(input));
            ProdLocRecord prodLocRecordReading = ProdLocRecord.buildNewProdLocRecordFromCursor(prodLocAccess.selectByMasnumReading(input));
            findScanAccess.insertRecord(new FindScanRecord(input, prodLocRecordWH1.getLocCode(), prodLocRecordOther.getLocCode(),
                    prodLocRecordReading.getLocCode(), productRecord.getName()));
            updateList();
            findScanInput.setText("");
            Utilities.makeToast(this, "Records: " + findScanAccess.getTotalScans());
        }
    }

    private void updateList()
    {
        findScanListAdapter.changeCursor(findScanAccess.selectScansForDisplay());
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver()
    {
        private static final String TAG = "BroadcastReceiver";

        @Override
        public void onReceive(Context c, Intent intent)
        {
            Log.d(TAG, "in onReceive");

            if(intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_DECODED_DATA))
            {
                String data = new String(intent.getCharArrayExtra(ScanAPIApplication.EXTRA_DECODEDDATA));
                handleScanInput(data);
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_SCANNER_ARRIVAL))
            {
                Utilities.makeToast(c, intent.getStringExtra(ScanAPIApplication.EXTRA_DEVICENAME) + " Connected");
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_SCANPI_INITIALIZED))
            {
                Utilities.makeToast(c, "Ready to pair with scanner");
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_CLOSE_ACTIVITY))
            {
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_ERROR_MESSAGE))
            {
                Utilities.makeToast(c, intent.getStringExtra(ScanAPIApplication.EXTRA_ERROR_MESSAGE));
            }
        }// end on Recieve
    };

    private void deleteRow(int position)
    {
        final Cursor cursor = findScanListAdapter.getCursor();
        cursor.moveToPosition(position);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("Confirm Delete")
                .setMessage("Delete Scan?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int id)
                    {
                        findScanAccess.deleteByPk(cursor.getString(0));
                        updateList();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private File writeFromDB(int exportModeChoice) throws IOException
    {
        Cursor exportCursor = findScanAccess.selectScansForPrint();
        File exportFile = ScanWriter.createExportFile(this, exportCursor, exportModeChoice);
        ScanWriter.writeBackupFile(exportFile);

        return exportFile;
    }

    private void performMassDelete()
    {
        findScanAccess.deleteAll();
        updateList();
    }
}

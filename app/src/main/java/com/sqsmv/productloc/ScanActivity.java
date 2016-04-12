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
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.socketmobile.apiintegration.ScanAPIApplication;
import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.product.ProductAccess;
import com.sqsmv.productloc.database.roomgrid.RoomGridAccess;
import com.sqsmv.productloc.database.scan.ScanAccess;
import com.sqsmv.productloc.database.scan.ScanRecord;
import com.sqsmv.productloc.database.upc.UPCAccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ScanActivity extends Activity {
    private static final String TAG = "ScanActivity";


    private DroidConfigManager appConfig;
    private DBAdapter dbAdapter;
    private ProductAccess productAccess;
    private UPCAccess upcAccess;
    private RoomGridAccess roomGridAccess;
    private ScanAccess scanAccess;
    private Pattern upcRegex;
    private Pattern sqsRegex;
    private Spinner buildingSpinner;
    private Spinner roomSpinner;
    private Spinner colSpinner;
    private Spinner rowSpinner;
    private EditText productScan;
    private ListView scannedList;
    private Pattern locRegex;
    private Pair<Integer, String> kvRooms;
    private Pair<Integer, String> kvCols;
    private Pair<Integer, String> kvRows;
    //private JSONObject json;
    private LinkedHashMap<String, String> buildingHashMap;
    private LinkedHashMap<String, String> roomHashMap;
    private TextView totalScans;

    private SimpleCursorAdapter scannedListAdapter;
    private boolean isLoadingNewActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate and for the ScanActivity!");

        setContentView(R.layout.activity_scan);

        appConfig = new DroidConfigManager(this);
        dbAdapter = new DBAdapter(this);
        productAccess = new ProductAccess(dbAdapter);
        upcAccess = new UPCAccess(dbAdapter);
        roomGridAccess = new RoomGridAccess(dbAdapter);
        scanAccess = new ScanAccess(dbAdapter);

        upcRegex = Pattern.compile("^\\d{12,13}(-N)?$");
        sqsRegex = Pattern.compile("^SQS(\\d+)\\d{3}$");
        locRegex = Pattern.compile("^([AB])(\\w{2})([A-T_])([0-9_][0-9_])$");

        buildingHashMap = new LinkedHashMap<String, String>();
        roomHashMap = new LinkedHashMap<String, String>();
        totalScans = (TextView)findViewById(R.id.totalScans);
        productScan = (EditText)findViewById(R.id.productScan);
        buildingSpinner = (Spinner)findViewById(R.id.buildings);
        roomSpinner = (Spinner)findViewById(R.id.roomNames);
        colSpinner = (Spinner)findViewById(R.id.colIds);
        rowSpinner = (Spinner)findViewById(R.id.rowIds);
        scannedList = (ListView)findViewById(R.id.scannedList);

        scannedListAdapter = new SimpleCursorAdapter(this, R.layout.row_scan, null,
                new String[]{"masNum", "room", "col", "row", "name"},
                new int[]{R.id.infoScanID, R.id.infoScanRoom, R.id.infoScanCol, R.id.infoScanRow, R.id.infoScanTitle}, 0);

        scannedList.setAdapter(scannedListAdapter);
        populateBuildings();
        populateRooms();
        populateCols();
        populateRows();

        setListeners();
    }

    protected void populateCounts() {
        int count = scanAccess.getTotalScans();
        totalScans.setText(Integer.toString(count));
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "in onStart and for the ScanActivity!");

        regBroadCastReceivers();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "in onResume");

        productAccess.open();
        upcAccess.open();
        roomGridAccess.open();
        scanAccess.open();

        populateCounts();
        updateScannedList();

        isLoadingNewActivity = false;

        String buildDate = Utilities.formatYYMMDDDate(new Date());
        if(!(buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, ""))))
        {
            finish();
        }
    }



    @Override
    protected void onStop()
    {
        Log.d(TAG, "in onStop and for the ScanActivity!");
        boolean scannerLock = appConfig.accessBoolean(DroidConfigManager.SCANNER_LOCK, null, false);
        unregisterReceiver(receiver);

        if(!scannerLock && !isLoadingNewActivity)
        {
            ScanAPIApplication.getApplicationInstance().forceRelease();
        }

        super.onStop();
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "in onPause for the ScanActivity!");

        dbAdapter.close();

        super.onPause();
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

        // increasing the Application View count from 0 to 1 will
        // cause the application to open and initialize ScanAPI
        ScanAPIApplication.getApplicationInstance().increaseViewCount();
    }


    private void setListeners()
    {
        findViewById(R.id.commit_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToCommit();
            }
        });
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToSubmit();
            }
        });

        findViewById(R.id.locate_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToLocate();
            }
        });

        findViewById(R.id.admin_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToAdmin();
            }
        });

        findViewById(R.id.pair_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToPair();
            }
        });

        scannedList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                deleteRow(position);
                return true;
            }
        });

        productScan.setOnEditorActionListener(scanFieldListener);

    }

    private String getLinkedHashMapSpinnerKey(Spinner spinner) {
        String value = spinner.getSelectedItem().toString();
        if (!value.equals("=") ) {
            return value.split("=")[0];
        }
        else {
            return "";
        }
    }

    private void goToSubmit() {
        handleInputs(productScan.getText().toString(),
                getLinkedHashMapSpinnerKey(buildingSpinner),
                getLinkedHashMapSpinnerKey(roomSpinner),
                rowSpinner.getSelectedItem().toString(),
                colSpinner.getSelectedItem().toString());
    }

    private void goToLocate() {
        Log.d(TAG, "in goToLocateActivity and for the ScanActivity!");
        isLoadingNewActivity = true;

        Intent intent = new Intent(this, FindScanActivity.class);
        startActivity(intent);
    }

    private void goToAdmin() {
        Log.d(TAG, "in goToLocateActivity and for the ScanActivity!");
        isLoadingNewActivity = true;

        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    private void goToPair() {
        Log.d(TAG, "in goToPairActivity and for the ScanActivity!");
        isLoadingNewActivity = true;

        Intent intent = new Intent(this, SocketMobilePairActivity.class);
        startActivity(intent);
    }


    private void goToCommit()
    {
        if(scanAccess.getTotalScans() > 0)
        {
            if(Utilities.checkWifi(this))
            {
                try
                {
                    int exportModeChoice = 0; /* set to 1 ... allows for multiple exportModeChoice */
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

    private void performMassDelete()
    {
        scanAccess.deleteAll();
        populateCounts();
        updateScannedList();
    }

    private File writeFromDB(int exportModeChoice) throws IOException
    {
        Cursor exportCursor = scanAccess.selectScansForPrint(exportModeChoice);
        File exportFile = ScanWriter.createExportFile(this, exportCursor, exportModeChoice);
        ScanWriter.writeBackupFile(exportFile);

        return exportFile;
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





    private final TextView.OnEditorActionListener scanFieldListener = new TextView.OnEditorActionListener()
    {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {
            if(actionId == EditorInfo.IME_ACTION_DONE && !v.getText().toString().isEmpty())
            {
                handleInputs(productScan.getText().toString(), getLinkedHashMapSpinnerKey(buildingSpinner),
                        getLinkedHashMapSpinnerKey(roomSpinner), rowSpinner.getSelectedItem().toString(), colSpinner.getSelectedItem().toString());
            }
            return true;
        }
    };

    private String fixedRow(String row) {
        if (row.charAt(0) == '0') {
            return Character.toString( row.charAt(1) );
        }
        else {
            return row;
        }
    }

    private void handleScanInput(String scanInput) {
        Log.d(TAG, "Just entered handleScanInput in ScanActivity!");
        Log.d(TAG, "scanInput: " + scanInput);
        Matcher locMatch = locRegex.matcher(scanInput);
        Matcher sqsMatch = sqsRegex.matcher(scanInput);
        Matcher upcMatch = upcRegex.matcher(scanInput);
        if (locMatch.find()) {
            String building = locMatch.group(1);
            String room = locMatch.group(2);
            String col = locMatch.group(3);
            String row = locMatch.group(4);
            buildingSpinner.setSelection(getLinkedHashMapAdapterIndex((LinkedHashMapAdapter<String, String>) buildingSpinner.getAdapter(), building));
            roomSpinner.setSelection(getLinkedHashMapAdapterIndex((LinkedHashMapAdapter<String, String>) roomSpinner.getAdapter(), room));
            colSpinner.setSelection(getSpinnerIndex(colSpinner, col));
            rowSpinner.setSelection(getSpinnerIndex(rowSpinner, fixedRow(row)));
        } else if (sqsMatch.find()) {
            String masnumAsString = sqsMatch.group(1);
            //int masnum = Integer.parseInt(masnumAsString);
            productScan.setText(masnumAsString);
            handleInputs(productScan.getText().toString(),
                    getLinkedHashMapSpinnerKey(buildingSpinner),
                    getLinkedHashMapSpinnerKey(roomSpinner),
                    rowSpinner.getSelectedItem().toString(),
                    colSpinner.getSelectedItem().toString());
        } else if (upcMatch.find()) {
            //todo: get the -N appending based off of a config boolean indicator
            String upc = scanInput + "-N";
            productScan.setText(upc);
            handleInputs(productScan.getText().toString(),
                    getLinkedHashMapSpinnerKey(buildingSpinner),
                    getLinkedHashMapSpinnerKey(roomSpinner),
                    rowSpinner.getSelectedItem().toString(),
                    colSpinner.getSelectedItem().toString());
        } else {
            Utilities.alertNotificationSound(this);
            Utilities.alertVibrate(this, new long[] {0, 500, 250, 500});
            Utilities.makeToast(this, "Did not understand Scan (NOT Recognized)!");
        }

    }

    private void handleInputs(String productScanValue, String buildingSpinnerValue, String roomSpinnerValue, String rowSpinnerValue, String colSpinnerValue) {
        if(productScanValue.isEmpty()) {
            Utilities.makeToast(this, "Need a Product Masnum or UPC.");
        } else if (buildingSpinnerValue.isEmpty()) {
            Utilities.makeToast(this, "Building is not defined.");
        } else if (roomSpinnerValue.isEmpty()) {
            Utilities.makeToast(this, "Room is not defined.");
        } else if (colSpinnerValue.isEmpty()) {
            Utilities.makeToast(this, "Grid (A-Z) is not defined.");
        } else if (rowSpinnerValue.isEmpty()) {
            Utilities.makeToast(this, "Grid (1-99) is not defined.");
        } else {
            if(!roomGridAccess.isValidLocation(buildingSpinnerValue, roomSpinnerValue, rowSpinnerValue, colSpinnerValue))
            {
                Utilities.alertVibrate(this, new long[]{0, 1500});
                Utilities.alertNotificationSound(this);
                Utilities.makeLongToast(this, "Not a valid location. Please let Rich/Cory know if it is valid.");
            }
            commitScan(productScanValue, buildingSpinnerValue, roomSpinnerValue, rowSpinnerValue, colSpinnerValue);
        }
    }

    private void commitScan(String productScanValue, String buildingSpinnerValue, String roomSpinnerValue, String rowSpinnerValue, String colSpinnerValue) {
        ScanRecord scanRec = new ScanRecord( productScanValue, buildingSpinnerValue, roomSpinnerValue, colSpinnerValue, rowSpinnerValue );
        scanAccess.insertRecord(scanRec);
        Utilities.makeToast(this, "Successful scan!");
        productScan.setText("");
        populateCounts();
        updateScannedList();
    }

    private int getLinkedHashMapAdapterIndex(LinkedHashMapAdapter<String, String> adapter, String key) {
        Map.Entry<String, String> mapItem;
        int pos = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            mapItem = adapter.getItem(i);
            if (mapItem.getKey().toLowerCase().equals(key.toLowerCase())) {
                pos = i;
            }
        }
        return pos;
    }


    private int getSpinnerIndex(Spinner spinner, String Value) {
        for(int i=0; i < spinner.getCount(); i++)
        {
            String valAtPos = (String)spinner.getItemAtPosition(i);
            if (!valAtPos.isEmpty()) {
                if (valAtPos.toLowerCase().equals(Value.toLowerCase())) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void populateBuildings() {

        JSONObject json;
        try {
            String roomJson = getResources().getString(R.string.buildingValues);
            buildingHashMap.put("", "");
            json = new JSONObject(roomJson);
            Iterator<String> iter = json.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    Object value = json.get(key);
                    buildingHashMap.put(key, value.toString());
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinkedHashMapAdapter roomAdapter = new LinkedHashMapAdapter<String, String>(this, android.R.layout.simple_spinner_item, buildingHashMap);

        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        buildingSpinner.setAdapter(roomAdapter);

    }

    private void populateRooms() {

        JSONObject json;
        try {
            String roomJson = getResources().getString(R.string.roomNameValues);
            roomHashMap.put("", "");
            json = new JSONObject(roomJson);
            Iterator<String> iter = json.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    Object value = json.get(key);
                    roomHashMap.put(key, value.toString());
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinkedHashMapAdapter roomAdapter = new LinkedHashMapAdapter<String, String>(this, android.R.layout.simple_spinner_item, roomHashMap);


        //ArrayAdapter<CharSequence> roomAdapter = ArrayAdapter.createFromResource(this,
        //        R.array.roomNameValues, android.R.layout.simple_spinner_item);

        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        roomSpinner.setAdapter(roomAdapter);

    }

    private void populateCols() {

        ArrayAdapter<CharSequence> colAdapter = ArrayAdapter.createFromResource(this,
                R.array.colValues, android.R.layout.simple_spinner_item);

        colAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        colSpinner.setAdapter(colAdapter);
    }

    private void populateRows() {

        ArrayAdapter<String> rowAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        rowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rowAdapter.add("");

        for(int i = 1; i <= 99; i++) {
            rowAdapter.add(Integer.toString(i));
        }

        rowSpinner.setAdapter(rowAdapter);

    }

    private void deleteRow(int position)
    {
        final Cursor cursor = scannedListAdapter.getCursor();
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
                        scanAccess.deleteByPk(cursor.getString(0));
                        updateScannedList();
                        populateCounts();
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

    private void updateScannedList()
    {
        scannedListAdapter.changeCursor(scanAccess.selectScansForDisplay());
    }
}

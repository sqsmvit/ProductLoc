package com.sqsmv.productloc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.socketmobile.apiintegration.ScanAPIApplication;
import com.sqsmv.productloc.database.DBAdapter;
import com.sqsmv.productloc.database.product.ProductAccess;
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


    //A = Reading, B = Pottstown
    private static final String BUILDING = "B";


    private DroidConfigManager appConfig;
    private DBAdapter dbAdapter;
    private ProductAccess productAccess;
    private UPCAccess upcAccess;
    private ScanAccess scanAccess;
    private Pattern upcRegex;
    private Pattern sqsRegex;
    private Spinner roomSpinner;
    private Spinner colSpinner;
    private Spinner rowSpinner;
    private EditText productScan;
    private Pattern locRegex;
    private Pair<Integer, String> kvRooms;
    private Pair<Integer, String> kvCols;
    private Pair<Integer, String> kvRows;
    //private JSONObject json;
    private LinkedHashMap<String, String> roomHashMap;
    private TextView totalScans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate and for the ScanActivity!");

        setContentView(R.layout.activity_scan);

        appConfig = new DroidConfigManager(this);
        dbAdapter = new DBAdapter(this);
        productAccess = new ProductAccess(dbAdapter);
        upcAccess = new UPCAccess(dbAdapter);
        scanAccess = new ScanAccess(dbAdapter);

        upcRegex = Pattern.compile("^\\d{12,13}(-N)?$");
        sqsRegex = Pattern.compile("^SQS(\\d+)\\d{3}$");
        locRegex = Pattern.compile("^B(\\w{2})([A-T_])([0-2_][0-9_])$");

        roomHashMap = new LinkedHashMap<String, String>();
        totalScans = (TextView)findViewById(R.id.totalScans);
        productScan = (EditText)findViewById(R.id.productScan);
        roomSpinner = (Spinner)findViewById(R.id.roomNames);
        colSpinner = (Spinner)findViewById(R.id.colIds);
        rowSpinner = (Spinner)findViewById(R.id.rowIds);

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

        //setConfig();
        //displayLocation();

        productAccess.open();
        upcAccess.open();
        scanAccess.open();

        populateCounts();

        //updateProductModeFieldVisibility(!isSkidScanMode);
/*        if(!isAutoCountMode || isManualCountMode)
        {
            enableQuantityInput();
        }
        else
        {
            quantityInput.setText(autoCountVal);
            disableQuantityInput();
        }*/

        String buildDate = Utilities.formatYYMMDDDate(new Date());
        if(!(buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, ""))))
        {
            finish();
        }
//ToDo: Uncomment crap
        //displayScannedRecordCount();
    }



    @Override
    protected void onStop()
    {
        Log.d(TAG, "in onStop and for the ScanActivity!");
        boolean scannerLock = appConfig.accessBoolean(DroidConfigManager.SCANNER_LOCK, null, false);
        unregisterReceiver(receiver);

        if(!scannerLock)
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

        findViewById(R.id.pair_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPair();
            }
        });

        //roomSpinner.setOnClickListener();


        productScan.setOnEditorActionListener(scanFieldListener);

    }

    private String getLinkedHashMapSpinnerKey(Spinner spinner){
        String roomValue = roomSpinner.getSelectedItem().toString();
        if (roomValue.length() > 0 ){
            return roomValue.substring(0, 2);
        }
        else {
            return "";
        }
    }



    private void goToSubmit() {

        handleInputs(productScan.getText().toString(),
                getLinkedHashMapSpinnerKey(roomSpinner),
                rowSpinner.getSelectedItem().toString(),
                colSpinner.getSelectedItem().toString());
    }




    private void goToPair(){
        Log.d(TAG, "in goToPairActivity and for the ScanActivity!");

        Intent intent = new Intent(this, SocketMobilePairActivity.class);
        startActivity(intent);
    }


    private void goToCommit()
    {

            if(Utilities.checkWifi(this))
            {
                try
                {
                    File exportFile = writeFromDB();
                    int exportModeChoice = 1; /* set to 1 ... allows for multiple exportModeChoice */
                    ScanExporter.exportScan(this, exportFile, exportModeChoice, true);
                    performMassDelete();
                    //fileExported = true;
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    Utilities.makeLongToast(this, "Error Writing Files.");
                }
            }
            else
            {
                Utilities.makeLongToast(this, "Not Connected to WiFi - Cannot Commit Scan.");
            }

    }

    private void performMassDelete()
    {
        scanAccess.deleteAll();
        populateCounts();
        //pullAdapter.notifyDataSetChanged();
    }



    private File writeFromDB() throws IOException
    {
        int exportModeChoice = 1; /* means nothing yet */
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
                Utilities.makeLongToast(c, intent.getStringExtra(ScanAPIApplication.EXTRA_DEVICENAME) + " Connected");
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_SCANPI_INITIALIZED))
            {
                Utilities.makeLongToast(c, "Ready to pair with scanner");
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_CLOSE_ACTIVITY))
            {
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_ERROR_MESSAGE))
            {
                Utilities.makeLongToast(c, intent.getStringExtra(ScanAPIApplication.EXTRA_ERROR_MESSAGE));
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
                handleInputs(productScan.getText().toString(), roomSpinner.getSelectedItem().toString(),
                        rowSpinner.getSelectedItem().toString(), colSpinner.getSelectedItem().toString());
            }
            return true;
        }
    };

    private String fixedRow(String row){
        if (row.charAt(0) == '0'){
            return Character.toString( row.charAt(1) );
        }
        else {
            return row;
        }
    }

    private void handleScanInput(String scanInput){
        Log.d(TAG, "Just entered handleScanInput in ScanActivity!");
        Log.d(TAG, "scanInput: " + scanInput);
        Matcher locMatch = locRegex.matcher(scanInput);
        Matcher sqsMatch = sqsRegex.matcher(scanInput);
        Matcher upcMatch = upcRegex.matcher(scanInput);
        if (locMatch.find()){
            String room = locMatch.group(1);
            String col = locMatch.group(2);
            String row = locMatch.group(3);
            roomSpinner.setSelection(getLinkedHashMapAdapterIndex((LinkedHashMapAdapter<String, String>) roomSpinner.getAdapter(), room));
            colSpinner.setSelection(getSpinnerIndex(colSpinner, col));
            rowSpinner.setSelection(getSpinnerIndex(rowSpinner, fixedRow(row)));
        } else if (sqsMatch.find()) {
            String masnumAsString = sqsMatch.group(1);
            //int masnum = Integer.parseInt(masnumAsString);
            productScan.setText(masnumAsString);
            handleInputs(productScan.getText().toString(),
                    getLinkedHashMapSpinnerKey(roomSpinner),
                    rowSpinner.getSelectedItem().toString(),
                    colSpinner.getSelectedItem().toString());
        } else if (upcMatch.find()) {
            //todo: get the -N appending based off of a config boolean indicator
            String upc = scanInput + "-N";
            productScan.setText(upc);
            handleInputs(productScan.getText().toString(),
                    getLinkedHashMapSpinnerKey(roomSpinner),
                    rowSpinner.getSelectedItem().toString(),
                    colSpinner.getSelectedItem().toString());
        } else {
            Utilities.makeToast(this, "Did not understand Scan (NOT Recognized)!");
        }

    }

    private void handleInputs(String productScanValue, String roomSpinnerValue, String rowSpinnerValue, String colSpinnerValue) {
        if(productScanValue.isEmpty()){
            Utilities.makeToast(this, "Need a Product Masnum or UPC.");
        } else if (roomSpinnerValue.isEmpty()){
            Utilities.makeToast(this, "Room is not defined.");
        } else if (colSpinnerValue.isEmpty()){
            Utilities.makeToast(this, "Grid(A-Z) is not defined.");
        } else if (rowSpinnerValue.isEmpty()){
            Utilities.makeToast(this, "Grid(1-20) is not defined.");
        } else {
            commitScan(productScanValue, roomSpinnerValue, rowSpinnerValue, colSpinnerValue);
        }
    }

    private void commitScan(String productScanValue, String roomSpinnerValue, String rowSpinnerValue, String colSpinnerValue) {
        ScanRecord scanRec = new ScanRecord( productScanValue, BUILDING, roomSpinnerValue, colSpinnerValue, rowSpinnerValue );
        scanAccess.insertRecord(scanRec);
        Utilities.makeToast(this, "Successful scan!");
        productScan.setText("");
        populateCounts();

    }

    private int getLinkedHashMapAdapterIndex(LinkedHashMapAdapter<String, String> adapter, String key){
        Map.Entry<String, String> mapItem;
        int pos = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            mapItem = adapter.getItem(i);
            if (mapItem.getKey().toLowerCase().equals(key.toLowerCase())){
                pos = i;
            }
        }
        return pos;
    }


    private int getSpinnerIndex(Spinner spinner, String Value){
        for(int i=0; i < spinner.getCount(); i++)
        {
            String valAtPos = (String)spinner.getItemAtPosition(i);
            if (!valAtPos.isEmpty()){
                if (valAtPos.toLowerCase().equals(Value.toLowerCase())) {
                    return i;
                }
            }
        }
        return 0;
    }


    private void populateRooms(){

        JSONObject json;
        try {
            String roomJson = getResources().getString(R.string.roomNameValues);
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

    private void populateCols(){

        ArrayAdapter<CharSequence> colAdapter = ArrayAdapter.createFromResource(this,
                R.array.colValues, android.R.layout.simple_spinner_item);

        colAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        colSpinner.setAdapter(colAdapter);
    }

    private void populateRows(){

        ArrayAdapter<String> rowAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        rowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rowAdapter.add("");

        for(int i = 1; i < 21; i++) {
            rowAdapter.add(Integer.toString(i));
        }

        rowSpinner.setAdapter(rowAdapter);

    }
}

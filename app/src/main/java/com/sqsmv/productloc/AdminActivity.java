package com.sqsmv.productloc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sqsmv.productloc.database.DBAdapter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminActivity extends Activity
{
    private UpdateLauncher updateLauncher;

    private ListView listView;
    private SimpleAdapter backupAdapter;

    private ArrayList<HashMap<String, String>> backupList;
    private File backupDirectory;
    private SimpleDateFormat fileDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        backupList = new ArrayList<HashMap<String, String>>();

        updateLauncher = new UpdateLauncher(this);

        fileDateFormat = new SimpleDateFormat("MM-dd-yy", Locale.US);

        backupDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/PLbackups");
        backupDirectory.mkdir();
        createAdapterDataset();

        backupAdapter = new SimpleAdapter(this, backupList, R.layout.admin_file_row,
                new String[] {"fileName","fileDate","fileSize"},
                new int[]{R.id.adminFileName, R.id.adminFileDate, R.id.adminFileSize});

        listView = (ListView)findViewById(R.id.fileListView);
        listView.setTextFilterEnabled(true);
        listView.setAdapter(backupAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                onListItemClick(position);
            }
        });
        setListeners();
    }

    private void setListeners()
    {
        findViewById(R.id.adminHeadBack).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
        findViewById(R.id.btnForceAppUpdate).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                forceAppUpdate();
            }
        });

        findViewById(R.id.btnForceUpdate).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                forceDBUpdate();
            }
        });

        findViewById(R.id.ResetDB).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                forceResetDB();
            }
        });

        findViewById(R.id.headBackupName).setOnClickListener(sortListener);
        findViewById(R.id.headBackupDate).setOnClickListener(sortListener);
        findViewById(R.id.headBackupSize).setOnClickListener(sortListener);
    }

    private void forceAppUpdate()
    {
        if(Utilities.checkWifi(this))
        {
            if(updateLauncher.checkNeedAppUpdate())
            {
                updateLauncher.startAppUpdate();
            }
            else
            {
                Utilities.makeToast(this, "Already at the latest version.");
            }
        }
        else
        {
            Utilities.makeToast(this, "Error: WiFi Not Connected.");
        }
    }

    private void forceDBUpdate()
    {
        if(Utilities.checkWifi(this))
        {
            updateLauncher.startDBUpdate();
            Utilities.makeLongToast(this, "DB Update starting...");
        }
        else
        {
            Utilities.makeToast(this, "Error: WiFi Not Connected.");
        }
    }

    private void forceResetDB()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("Reset DB")
                .setMessage("Are you sure you want to reset the database?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,int id)
                    {
                        resetDB();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void onClickSort(int id)
    {
        String key = "";

        if(id == R.id.headBackupName)
        {
            key = "fileName";
        }
        else if(id == R.id.headBackupDate)
        {
            key = "fileDate";
        }
        else if(id == R.id.headBackupSize)
        {
            key = "fileSize";
        }
        sortList(key);
        backupAdapter.notifyDataSetChanged();
    }

    private void sortList(final String key)
    {
        Comparator<HashMap<String, String>> comp = new Comparator<HashMap<String, String>>()
        {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs)
            {
                int compareVal = 0;
                if(key.equals("fileName"))
                {
                    String val1 = lhs.get("fileName");
                    String val2 = rhs.get("fileName");
                    compareVal = val1.compareTo(val2);
                }
                else if(key.equals("fileDate"))
                {
                    try
                    {
                        Date val1 = fileDateFormat.parse(lhs.get("fileDate"));
                        Date val2 = fileDateFormat.parse(rhs.get("fileDate"));
                        compareVal = val1.compareTo(val2);
                    }
                    catch(ParseException e)
                    {}
                }
                else if(key.equals("fileSize"))
                {
                    Pattern fileSizeRegEx = Pattern.compile("^(.*)kb$");
                    Matcher val1Matcher = fileSizeRegEx.matcher(lhs.get("fileSize"));
                    Matcher val2Matcher = fileSizeRegEx.matcher(rhs.get("fileSize"));
                    if(val1Matcher.find() && val2Matcher.find())
                    {
                        Float val1 = Float.parseFloat(val1Matcher.group(1));
                        Float val2 = Float.parseFloat(val2Matcher.group(1));
                        compareVal = val1.compareTo(val2);
                    }
                }
                return compareVal;
            }
        };
        Collections.sort(backupList, Collections.reverseOrder(comp));
    }

    private void createAdapterDataset()
    {
        HashMap<String, String> backupEntry;
        File[] files = backupDirectory.listFiles();

        for (File file : files)
        {
            backupEntry = new HashMap<String, String>();
            backupEntry.put("fileName", file.getName());
            backupEntry.put("fileDate", fileDateFormat.format(new Date(file.lastModified())));
            backupEntry.put("fileSize", String.format("%.2f",(file.length()/1024.0)) + "kb");
            backupList.add(backupEntry);
        }
        sortList("fileDate");
    }

    protected void onListItemClick(int position)
    {
        final File selected = new File(backupDirectory, backupList.get(position).get("fileName"));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Export Backup");

        // set dialog message
        alertDialogBuilder
            .setMessage("Send Backup to DropBox?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    exportBackup(selected);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();
                }
            });
        alertDialogBuilder.show();
    }

    private void resetDB()
    {
        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.resetImportData();
        dbAdapter.close();
    }

    private void exportBackup(File backupFile)
    {
        if(Utilities.checkWifi(this))
        {
            ScanExporter.exportScan(this, backupFile, 0, false);
        }
        else
        {
            Utilities.makeToast(this, "Error: WiFi Not Connected.");
        }
    }

    View.OnClickListener sortListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            onClickSort(v.getId());
        }
    };
}

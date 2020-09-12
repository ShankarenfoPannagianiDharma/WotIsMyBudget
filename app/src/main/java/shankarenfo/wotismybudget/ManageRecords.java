package shankarenfo.wotismybudget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class ManageRecords extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_records);

        final Context context = this;
        final File recordsDirectory = new File(Environment.getExternalStorageDirectory() + "/WotBudgets?");

        ListView recordsList = (ListView) findViewById(R.id.lst_RecordsList);

        //fill listview with filenames
        File[] records = recordsDirectory.listFiles();
        ArrayList<String> listitems = new ArrayList<String>();
        for (File record : records) {
            String filename = record.getName();
            //get the extension of the file  (getName() also returns extension)
            int indexofextension = filename.lastIndexOf('.');              //returns index of the extension
            String filelabel = filename.substring(0, indexofextension);      //the actual name without extension of file
            listitems.add(filelabel);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listitems);
        recordsList.setAdapter(adapter);

        //on click, ask to del
        recordsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l)
            {
                //build popup for file deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Delete Record?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            //if yes, delete file
                            public void onClick(DialogInterface dialog, int which)
                            {
                                ListView recordsList = (ListView) findViewById(R.id.lst_RecordsList);
                                String target = recordsList.getAdapter().getItem(i).toString();
                                File targetfile = new File(recordsDirectory+"/"+target+".txt");
                                targetfile.delete();
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    public void backOut(View v)
    {finish();}


}

package shankarenfo.wotismybudget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BudgetRecords extends AppCompatActivity
{
    Calendar currentCal;
    final Context context = this;
    DateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");    //heading format

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_records);

        //initialize the componentsCalendar currentCal;
        currentCal = Calendar.getInstance();
        buildList(currentCal.getTime());
    }

    public void prevMonth(View v)
    {
        currentCal.add(Calendar.MONTH, -1);
        buildList(currentCal.getTime());
    }

    //next month button
    public void nextMonth(View v)
    {
        currentCal.add(Calendar.MONTH, +1);
        buildList(currentCal.getTime());
    }

    // using the date, fill compnents with data
    private void buildList(Date date)
    {
        //set date
        TextView currentDate = findViewById(R.id.txt_CurrentRecordDate);
        final ListView expenseList = findViewById(R.id.lst_Budgets);
        currentDate.setText(dateFormat.format(date));
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //find file with date
        final File targetFile = UnifiedOperations.findFile(date);
        //is file found?
        if(!targetFile.exists())
        {
            //no match- switch components
            LinearLayout contentLayout = findViewById(R.id.llv_RecordData);
            TextView notFoundText = findViewById(R.id.txt_NoData);
            Button inferButton = findViewById(R.id.btn_Infer);
            inferButton.setEnabled(false);
            contentLayout.setVisibility(View.GONE);
            notFoundText.setVisibility(View.VISIBLE);
            TextView monthlyExpense = findViewById(R.id.txt_TotalBudget);
            monthlyExpense.setText("N/A");
        }
        else
        {
            //just in case- ensure component visibility
            LinearLayout contentLayout = findViewById(R.id.llv_RecordData);
            TextView notFoundText = findViewById(R.id.txt_NoData);
            Button inferButton = findViewById(R.id.btn_Infer);
            inferButton.setEnabled(true);
            contentLayout.setVisibility(View.VISIBLE);
            notFoundText.setVisibility(View.GONE);
            //match found, read data
            ArrayList<String> data = UnifiedOperations.getFileListData(targetFile);
            //parse data for list-
            ArrayList<String> expense = new ArrayList<String>();
            ArrayList<String> cost = new ArrayList<String>();
            ArrayList<String> day = new ArrayList<String>();
            double totals = 0;
            for(String line : data)
            {
                //separate @
                System.out.println(line);
                String[] anExpense = line.split("@");
                expense.add(anExpense[0]);
                cost.add(anExpense[1]);
                day.add(anExpense[2]);
                totals += Double.parseDouble(anExpense[1]);
            }
            //fill in total budget.
            TextView monthlyExpense = findViewById(R.id.txt_TotalBudget);
            monthlyExpense.setText("$ "+String.format("%.2f",totals));

            //insert data into list
            ArrayAdapter_BudgetRecordItems customAdapter = new ArrayAdapter_BudgetRecordItems(this,expense.toArray(new String[0]),cost.toArray(new String[0]),day.toArray(new String[0]));
            expenseList.setAdapter(customAdapter);

            //add list listeners: on long click, delete record data
            expenseList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {

                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int row, long l)
                {
                    //build popup for entry deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Delete expense entry?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                //if yes, delete file
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    ListView recordsList = findViewById(R.id.lst_RecordsList);

                                    UnifiedOperations.removeEntry(targetFile,row);

                                    //refresh components
                                    finish();
                                    startActivity(getIntent());
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    return true;
                }
            });

            //add list listeners: on click, edit record data (label and amount)
            expenseList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, final int row, long l)
                {
                    LinearLayout DialogLayout = new LinearLayout(context);
                    DialogLayout.setOrientation(LinearLayout.HORIZONTAL);
                    final EditText txt_NewLabelText = new EditText(context);
                    txt_NewLabelText.setEms(8);
                    txt_NewLabelText.setText( ((TextView) view.findViewById(R.id.txt_RecordItemName)).getText().toString() );
                    txt_NewLabelText.setHint( ((TextView) view.findViewById(R.id.txt_RecordItemName)).getText().toString() );
                    txt_NewLabelText.setEnabled(true);
                    final EditText txt_NewCostText = new EditText(context);
                    txt_NewCostText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    txt_NewCostText.setEms(8);
                    txt_NewCostText.setText( ((TextView) view.findViewById(R.id.txt_RecordItemCost)).getText().toString().replaceAll(",","") );
                    txt_NewCostText.setHint( ((TextView) view.findViewById(R.id.txt_RecordItemCost)).getText().toString().replaceAll(",","") );
                    txt_NewCostText.setEnabled(true);
                    DialogLayout.addView(txt_NewLabelText);
                    DialogLayout.addView(txt_NewCostText);

                    builder.setMessage("Enter new expense Label and Cost.")
                            .setTitle("Update expense data")
                            .setView(DialogLayout)
                            .setPositiveButton("OK",new DialogInterface.OnClickListener()
                            {
                                //edit the cost of the expense
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    //check: both texts ok and valid?
                                    if(!txt_NewLabelText.getText().toString().isEmpty() && !txt_NewCostText.getText().toString().isEmpty())
                                    {
                                        //legal data, get data
                                        String newLabel = txt_NewLabelText.getText().toString().toUpperCase();
                                        String newCost = String.format("%.2f", Double.parseDouble(txt_NewCostText.getText().toString()));

                                        //APPLY NEW DATA TO RECORD FILE.
                                        UnifiedOperations.replaceLine(currentCal,row,newLabel+"@"+newCost);

                                        //refresh components
                                        finish();
                                        startActivity(getIntent());
                                    }
                                }
                            })
                            .setNegativeButton("No", null)
                            .setNeutralButton(null,null)
                            .show();

                }
            });//end delete onLongClick popup
        }//end if

    }//end buildList function

    //exit button
    public void backOut(View v)
    {finish();}

    //move to expenses by category
    public void infer(View v)
    {
        Intent intent = new Intent(this, InferredExpenses.class);
        intent.putExtra("currentCalendar", currentCal);
        startActivity(intent);
    }
}

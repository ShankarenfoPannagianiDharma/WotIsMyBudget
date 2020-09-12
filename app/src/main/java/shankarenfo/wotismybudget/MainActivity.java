package shankarenfo.wotismybudget;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private File recordsDirectory = new File(Environment.getExternalStorageDirectory()+"/WotBudgets?");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fill data with onResume function
        onResume();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(!recordsDirectory.exists())
        {recordsDirectory.mkdir();}

        TextView currentDate = (TextView) findViewById(R.id.txt_CurrentDate);
        currentDate.setText(setCurrentDate());

        TextView currentBudget = (TextView) findViewById(R.id.txt_CurrentExpenses);
        currentBudget.setText(String.format("%.2f" ,UnifiedOperations.getMonthTotals(new Date())) );
    }

    //function to generate current date text
    private String setCurrentDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    //move functions
    //go to new expenses
    public void moveToNewExpenses(View v)
    {
        Intent intent = new Intent(this, NewExpense.class);
        startActivity(intent);
    }
    //go to records
    public void moveToRecords(View v)
    {
        Intent intent = new Intent(this, BudgetRecords.class);
        startActivity(intent);
    }
    //manage records
    public void manageRecords(View v)
    {
        Intent intent = new Intent(this, ManageRecords.class);
        startActivity(intent);
    }
}

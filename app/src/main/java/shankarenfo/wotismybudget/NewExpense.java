package shankarenfo.wotismybudget;

import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NewExpense extends AppCompatActivity
{

    File recordsDirectory = new File(Environment.getExternalStorageDirectory()+"/WotBudgets?");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        //add in data for selection box
        String[] itemTypes = UnifiedOperations.getItemTypes(new Date());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,itemTypes);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.txt_InputName);
        textView.setAdapter(adapter);

        TextView lbl_expense = (TextView) findViewById(R.id.lbl_currentdate);
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("dd MMM, yyyy");
        lbl_expense.setText("New expense for "+df.format(date));
    }

    public void newBudget(View v)
    {
        //get input data
        EditText expenseName = (EditText) findViewById(R.id.txt_InputName);
        EditText expenseCost = (EditText) findViewById(R.id.txt_InputCost);
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        String day = String.valueOf(dayOfMonth);

        //check: input is entered?
        if(expenseName.getText() != null && expenseName.getText().length() > 0 &&
                expenseCost.getText() != null && expenseCost.getText().length() > 0)
        {
            //save input
            ArrayList<String> linesToWrite = new ArrayList<String>();
            ArrayList<Double> budgets = new ArrayList<Double>();
            //does targetfile exist?
            File targetFile = UnifiedOperations.findFile(new Date());
            UnifiedOperations.addEntry(targetFile,expenseName.getText().toString().toUpperCase(),Double.parseDouble(expenseCost.getText().toString()),day);
        }
        //once done, backout
        finish();
    }

    public void backOut(View v)
    {finish();}
}

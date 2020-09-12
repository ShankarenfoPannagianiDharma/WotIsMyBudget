package shankarenfo.wotismybudget;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class InferredExpenses extends AppCompatActivity
{
    static final DateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");    //heading format
    Calendar currentCal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inferred_expenses);

        currentCal = (Calendar) getIntent().getExtras().get("currentCalendar");
        TextView heading = (TextView) findViewById(R.id.lbl_InferredCosts);
        heading.setText("Expenses by Category, for "+dateFormat.format(currentCal.getTime()));
        TextView amounts = (TextView) findViewById(R.id.lbl_SubHeadingAmount);
        amounts.setText(String.format("$ %.2f",UnifiedOperations.getMonthTotals(currentCal.getTime())));

        currentCal = Calendar.getInstance();

        File targetFile = UnifiedOperations.findFile(currentCal);

        //build listview, 2 items
        ListView expenseList = (ListView) findViewById(R.id.lst_CategoryExpenses);
        //INFERENCE ENGINE: category types
        ArrayList<String> lines = UnifiedOperations.getFileListData(targetFile);      //raw lines

        //get unique names
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> totals = new ArrayList<String>();
        ArrayList<String> occurences = new ArrayList<String>();
        for(String line : lines)
        {
            String parts[] = line.split("@");
            names.add(parts[0]);
        }
        Set<String> ExpenseCategories = new HashSet<String>(names);
        //get the totals for each category
        for(String cat : ExpenseCategories)
        {
            double total = 0;
            long occurence = 0;
            for(String line : lines)
            {
                String parts[] = line.split("@");
                if(cat.equals(parts[0]))
                {
                    total += Double.parseDouble(parts[1]);
                    occurence++;
                }
            }
            //calculations done, add into lists
            totals.add(String.format("%.2f", total));
            occurences.add(""+occurence);
        }
        //compose adapter
        ArrayAdapter_BudgetRecordItems customAdapter = new ArrayAdapter_BudgetRecordItems(this,ExpenseCategories.toArray(new String[0]),totals.toArray(new String[0]),occurences.toArray(new String[0]));
        //attach adapter to list
        expenseList.setAdapter(customAdapter);
    }

    public void indivExpenses(View v)
    {finish();}
}

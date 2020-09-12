package shankarenfo.wotismybudget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Shankarenfo on 8/17/2018.
 */

public class ArrayAdapter_BudgetRecordItems extends ArrayAdapter<String>
{
    private final Context context;
    private final String[] expenseNames;
    private final String[] expenseCost;
    private final String[] expenseDay;

    public ArrayAdapter_BudgetRecordItems(Context context, String[] expenseNames, String[] expenseCost, String[] expenseDay)
    {
        super(context,-1,expenseNames);
        this.context = context;
        this.expenseNames = expenseNames;
        this.expenseCost = expenseCost;
        this.expenseDay = expenseDay;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.record_item, parent, false);
        TextView RecordItemName = (TextView) rowView.findViewById(R.id.txt_RecordItemName);
        TextView RecordItemCost = (TextView) rowView.findViewById(R.id.txt_RecordItemCost);
        TextView RecordItemDay = (TextView) rowView.findViewById(R.id.txt_RecordItemDay);

        RecordItemName.setText(expenseNames[position]);
        RecordItemCost.setText(expenseCost[position]);
        RecordItemDay.setText(expenseDay[position]);

        return rowView;
    }
}

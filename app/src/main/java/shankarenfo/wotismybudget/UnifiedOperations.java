package shankarenfo.wotismybudget;

import android.content.Intent;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Shankarenfo on 6/24/2019.
 */


public class UnifiedOperations
{
    static final DateFormat BudgetMonth = new SimpleDateFormat("MMM|yyyy");  //file format
    static final File recordsDirectory = new File(Environment.getExternalStorageDirectory()+"/WotBudgets?");
    static Date cal;

    //method to replace a line in the file
    public static void replaceLine(int row, String line)
    {
        File targetFile = findFile(cal);
        //reread the entire file
        try
        {
            FileReader fileReader = new FileReader(targetFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //read entries, if any.
            ArrayList<String> linesToWrite = new ArrayList<String>();
            String FileLine;

            int counter = 0;
            //next lines until EOF is list of individual expenses
            while((FileLine = bufferedReader.readLine() )!= null)
            {
                //read lines, but skip row number (delete line)
                if(counter != row)
                {
                    //add into write
                    linesToWrite.add(FileLine);
                }
                else
                {
                    //if concerned row replace the label with newlabel
                    String raws[] = FileLine.split("@");
                    String newLine = line+"@"+raws[2];
                    linesToWrite.add(newLine);
                }
                counter++;
            }
            // Always close files.
            bufferedReader.close();

            //write into file
            FileWriter writeOut = new FileWriter(targetFile,false);
            boolean first = true;
            for(String data : linesToWrite)
            {
                if(first != true)
                {writeOut.write("\n");}
                else
                {first = false;}
                writeOut.write(data);
            }

            //close outputstream
            writeOut.close();

        }
        catch(FileNotFoundException ex)
        {System.out.println("Unable to open file '" + targetFile + "'");}
        catch(IOException ex)
        {System.out.println("Error reading file '" + targetFile + "'");}
    }

    //method to read file entirely
    public static ArrayList<String> getFileListData(File targetFile)
    {
        ArrayList<String> allLines = new ArrayList<String>();;
        try
        {
            FileReader fileReader = new FileReader(targetFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //next lines until EOF is list of individual expenses
            String line;
            while((line = bufferedReader.readLine() )!= null)
            {
                allLines.add(line);
            }
            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex)
        {System.out.println("Unable to open file '" + targetFile + "'");}
        catch(IOException ex)
        {System.out.println("Error reading file '" + targetFile + "'");}

        return allLines;
    }

    //method to calculate totals
    public static double getMonthTotals(Date date)
    {
        double totals = 0;

        File targetFile = UnifiedOperations.findFile(date);
        //if target does not exists, 0.
        if(!targetFile.exists())
        {return totals;}
        //if target exists, read
        else
        {
            //get raw data
            ArrayList<String> lines = getFileListData(targetFile);
            //parse costs of each file
            for(String line : lines)
            {
                String[] datas = line.split("@");
                totals += Double.parseDouble(datas[1]);
            }
        }

        return totals;
    }

    //method to add an entry to file
    public static void addEntry(File targetFile,String name,double cost,String day)
    {
        try
        {
            //if target does not exists, make new file
            if (!targetFile.exists())
            {targetFile.createNewFile();}

            //target exists, open
            FileReader fr = new FileReader(targetFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            //read entries, if any.
            ArrayList<String> linesToWrite = new ArrayList<String>();
            while ((line = br.readLine()) != null)
            {
                //line to rewrite
                linesToWrite.add(line);
                //get budget of each item (for total)
                String[] parts = line.split("@");
            }
            //add new entry at the end
            String cst = String.format("%.2f", cost);
            String BudgetToAdd = name+"@"+cst+"@"+day;
            linesToWrite.add(BudgetToAdd);

            //close inputstreams, data acquired
            br.close();
            fr.close();

            //write to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
            boolean first = true;
            for(String data : linesToWrite)
            {
                if(first != true)
                {writer.write("\n");}
                else
                {first = false;}
                writer.write(data);
            }

            //close outputstream
            writer.close();
        }
        catch (IOException e)
        {e.printStackTrace();}
    }

    //method to determine which file to read
    public static File findFile()
    {
        final File targetFile = new File(recordsDirectory+"//"+BudgetMonth.format(cal.getTime())+".txt");
        return targetFile;
    }
    public static File findFile(Date date)
    {
        final File targetFile = new File(recordsDirectory+"//"+BudgetMonth.format(date)+".txt");
        return targetFile;
    }

    //method to remove an entry in expenses file
    public static void removeEntry(File targetFile, int row)
    {
        try
        {
            FileReader fileReader = new FileReader(targetFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            ArrayList<String> linesToWrite = new ArrayList<String>();
            String FileLine;
            int counter = 0;
            //next lines until EOF is list of individual expenses
            while((FileLine = bufferedReader.readLine() )!= null)
            {
                //read lines, but skip row number (delete line)
                if(counter != row)
                {
                    //add into write
                    linesToWrite.add(FileLine);
                }
                counter++;
            }
            // Always close files.
            bufferedReader.close();

            //write into file
            FileWriter writeOut = new FileWriter(targetFile,false);
            boolean first = true;
            for(String data : linesToWrite)
            {
                if(first != true)
                {writeOut.write("\n");}
                else
                {first = false;}
                writeOut.write(data);
            }

            //close outputstream
            writeOut.close();

        }
        catch(FileNotFoundException ex)
        {System.out.println("Unable to open file '" + targetFile + "'");}
        catch(IOException ex)
        {System.out.println("Error reading file '" + targetFile + "'");}
    }

    //method to get the itemtypes of a given date
    public static String[] getItemTypes(Date date)
    {

        ArrayList<String> lines = getFileListData(findFile(date));      //raw lines

        if(!lines.isEmpty())
        {
            //get unique names
            ArrayList<String> names = new ArrayList<String>();
            for (String line : lines) {
                String parts[] = line.split("@");
                names.add(parts[0]);
            }
            Set<String> ExpenseCategories = new HashSet<String>(names);
            return ExpenseCategories.toArray(new String[0]);
        }
        else
        {
            String[] empty = {};
            return empty;
        }
    }
}

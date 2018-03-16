package app.and.cashtracker.calculator;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Egorman on 13.03.2018.
 */

public class Calculator {
    private String equation;
    private boolean solved;
    public Calculator(){
        equation="";
        solved = false;
    }
    public boolean isSolved() {
        return solved;
    }
    public String addSymbol(char c){
        if(equation.isEmpty() && c=='.') equation="0.";
        if(c=='d' && !equation.isEmpty()) equation = equation.substring(0, equation.length()-1);
        else equation += c;
        solved=false;
        if(c=='=') {
            if(equation.isEmpty()) equation="0=";
            Log.d("CALCULATOR", "Equation: '" + equation + "'");
            equation = solve();
            if(equation.equals("null")) solved=false; else solved=true;
            return equation;
        }
        return equation;
    }
    private String solve(){
        ArrayList<Double> numbers = new ArrayList<>();
        for(String num : equation.split("[+\\-*/=]")){
            if(!num.isEmpty())
            {
                numbers.add(Double.valueOf(num));
                Log.d("CALCULATOR", "Find number = " + num);
            }
        }
        ArrayList<String> operations = new ArrayList<>();
        for(String op : equation.split("[^+\\-*/]")){
            if(!op.isEmpty()){
                operations.add(op);
                Log.d("CALCULATOR", "Find operation '" + op + "'");
            }
        }
        DecimalFormat df = new DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.US));
        Double res = doOperations(numbers,operations);
        if(res!=null)
            return df.format(res);
        return String.valueOf(res);
    }
    private Double doOperations(ArrayList<Double> numbers, ArrayList<String> operations){
        // do * and /
        Log.d("CALCULATOR", "Solving equation...");
        for(int i=0; i<operations.size(); i++){
            char op = operations.get(i).charAt(0);
            if(op=='*' || op=='/'){
                if(numbers.size()<2) return null;
                double a = numbers.get(i), b = numbers.get(i+1);
                Log.d("CALCULATOR", "Operation * or /");
                Log.d("CALCULATOR", "a = " + a + " , b = " + b);
                switch (op){
                    case '*': a=a*b;
                        Log.d("CALCULATOR", "a * b = " + a);break;
                    case '/': a=a/b;
                        Log.d("CALCULATOR", "a / b = " + a);break;
                }
                numbers.remove(i+1); numbers.remove(i);
                Log.d("CALCULATOR", "Numbers deleted! " + numbers.toString());
                numbers.add(i,a);
                Log.d("CALCULATOR", "Number added! " + numbers.toString());
                operations.remove(i);
                Log.d("CALCULATOR", "Operation deleted! " + operations.toString());
                return doOperations(numbers,operations);
            }
        }
        // do + and -
        for(int i=0; i<operations.size(); i++){
            char op = operations.get(i).charAt(0);
            if(op=='+' || op=='-'){
                if(numbers.size()<2) return null;
                double a = numbers.get(i), b = numbers.get(i+1);
                Log.d("CALCULATOR", "Operation + or -");
                Log.d("CALCULATOR", "a = " + a + " , b = " + b);
                switch (op){
                    case '+': a=a+b;
                        Log.d("CALCULATOR", "a + b = " + a);break;
                    case '-': a=a-b;
                        Log.d("CALCULATOR", "a - b = " + a);break;
                }
                numbers.remove(i+1); numbers.remove(i);
                Log.d("CALCULATOR", "Numbers deleted! " + numbers.toString());
                numbers.add(i,a);
                Log.d("CALCULATOR", "Number added! " + numbers.toString());
                operations.remove(i);
                Log.d("CALCULATOR", "Operation deleted! " + operations.toString());
                return doOperations(numbers,operations);
            }
        }
        Log.d("CALCULATOR", "Equation solved!");
        if (operations.size() == 0 && numbers.size()>0) return numbers.get(0);
        return null;
    }
    public void erase(){ equation=""; solved=false;}
}

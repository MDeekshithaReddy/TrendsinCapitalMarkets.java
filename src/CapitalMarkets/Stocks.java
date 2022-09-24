package CapitalMarkets;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;




public class Stocks {
    static ArrayList<Stocks> stocks = new ArrayList<>();
    String stockName;
    public double[] price = new double[500];
    String errorCheck = " { ";
    double[] ema12 = new double[200];
    double[] ema26 = new double[200];
    double simpleMA50;// exponential moving average
    double simpleMA100;
    double simpleMA200;
    double exponentialMA12;
    double exponentialMA26;



    void getInfo(String apiKey,String stockCode){
        BufferedReader br = null;
        String line = "";
        this.stockName = stockCode;

        try {
            stockCode = stockCode.toUpperCase();
            InputStream input1 = new URL("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + stockCode + "&outputsize=full&apikey=" + apiKey + "&datatype=csv").openStream();
            br = new BufferedReader(new InputStreamReader(input1, "UTF-8"));
            this.errorCheck  = br.readLine();

            if (!this.errorCheck.contains("{")){
                for (int i = 0; i < 500; i++){
                    line = br.readLine();
                    String[] info = line.split(",");
                    if (line != null){this.price[i] = Double.parseDouble(info[4]);}
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"There was a connection error. Please Try Again");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"There was a connection error. Please Try Again");
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    double timedEMACalc(int count, int daysAgo){ // This calculates the EMA for any amount of days upto 500
        double total = 0;
        double ema = 0;
        int multipliers = 0;
        int count2 = count;
        for (int i = daysAgo; i < count + daysAgo; i++){
            total += this.price[i] * count2;
        }
        for (int i = count; i > 0; i--){
            multipliers += i;
        }
        for (int i = daysAgo; i < count + daysAgo; i++){
            total += this.price[i] * count2;
            count2--;
        }
        ema = total/multipliers;
        return ema;
    }

    double simpleMovingAverageCalc(int count){
        double simpleMovingAverage = 0.0;
        double total = 0.0;
        if (count > 200){
            count = 200;
        }
        for (int i = 0; i < count; i++){
            total += this.price[i];
        }
        simpleMovingAverage = total / count;
        return simpleMovingAverage;
    }

    double exponentialMovingAverage(int count){
        double total = 0.0;
        double exponentialMovingAverage = 0.0;
        int count2 = count;
        int multipliers = 0;
        for (int i = count; i > 0; i--){
            multipliers += i;
        }
        for (int i = 0; i < count; i++){
            total += this.price[i] * count2;
            count2--;
        }
        exponentialMovingAverage = total / multipliers;
        return exponentialMovingAverage;
    }

    void findManyEMA(){
        for (int  i = 0; i < 200; i++){
            ema12[i] = this.timedEMACalc(12, i);
        }
        for (int  i = 0; i < 200; i++){
            ema26[i] = this.timedEMACalc(26, i);
        }
    }

    void checkStock(){
        simpleMA50 = this.simpleMovingAverageCalc(50);
        simpleMA100 = this.simpleMovingAverageCalc(100);
        simpleMA200 = this.simpleMovingAverageCalc(200);
        exponentialMA12 = this.exponentialMovingAverage(12);
        exponentialMA26 = this.exponentialMovingAverage(26);


        String sma50 = Double.toString(simpleMA50);
        String sma100 = Double.toString(simpleMA100);
        String sma200 = Double.toString(simpleMA200);
        String ema12 = Double.toString(exponentialMA12);
        String ema26 = Double.toString(exponentialMA26);


        String msgbegin = "STATISTICS:\n\n50 DAY SIMPLE MOVING AVERAGE:      "+sma50;
        msgbegin += "\n100 DAY SIMPLE MOVING AVERAGE:     "+sma100;
        msgbegin += "\n200 DAY SIMPLE MOVING AVERAGE:     "+sma200;
        msgbegin += "\n12 DAY EXPONENTIAL MOVING AVERAGE: "+ema12;
        msgbegin += "\n26 DAY EXPONETIAL MOVING AVERAGE:  "+ema26+"\n\n";

        String finalmsg = "";

        if (simpleMA50 > simpleMA200){

            finalmsg += "Because the 50 day SMA is higher than \nthe 200 day SMA it indicates that this stock\n is in an upward trend. ";


            if (exponentialMA12 > exponentialMA26){

                finalmsg += "\n\nAlso because the 12 day EMA is\n above the 26 day EMA this mean this is a \nvery strong stock pick. ";

            } else {

                finalmsg += "\n\nDespite the upward trend, because\n the 12 day EMA is less than the 26 day \nEMA this may not be the best pick.";

            }
        } else {
            finalmsg += "\n\nPERDICTIONS: \nBecause the 50 day SMA is lower than the 200 day\nSMA it indicates that the stock \nis in a downward trend. ";

            if (exponentialMA12 > exponentialMA26){
                finalmsg += "Despite this because the 12 day EMA is higher the 26 day EMA, \nthis trend may be reversing and might be a good prick. ";

            } else{

                finalmsg += "Also because the 12 day EMA is lower than the 26 \nthis downward trend is continuing. \nThis stock is a really bad pick. ";

            }
        }
        ImageIcon icon = new ImageIcon("/Users/deekshitha/Downloads/imgonline-com-ua-resize-rtF6N4k1I2Sxvp58.jpg");

        JOptionPane.showMessageDialog(null, msgbegin+finalmsg,"Capital Markets",0,icon);

    }

    static void showAllStocks(){  // this show a short sample of stats from every stock that the user has entered

        ImageIcon icon = new ImageIcon("/Users/deekshitha/Downloads/imgonline-com-ua-resize-rtF6N4k1I2Sxvp58.jpg");

        String display = "Given below are the "+ Stocks.stocks.size()+" stocks that you entered.";
        for (int i = 0; i < Stocks.stocks.size(); i++){

            display += "\nName: "+Stocks.stocks.get(i).stockName;
            display += "\nMost recent Price: "+Stocks.stocks.get(i).price[i];
            display += "\n50 day SMA: "+Stocks.stocks.get(i).simpleMA50;
            display += "\n200 day SMA: "+Stocks.stocks.get(i).simpleMA200;


        }

        JOptionPane.showMessageDialog(null,display, "Capital Markets",0,icon);

    }

    public static void main(String[] args) {
        ImageIcon img = new ImageIcon("/Users/deekshitha/Downloads/imgonline-com-ua-resize-rtF6N4k1I2Sxvp58.jpg");
        float[] hsv = new float[3];
        UIManager um=new UIManager();
        um.put("OptionPane.background", Color.RGBtoHSB(229,255,204,hsv));
        int option;
        String currentStock;
        Scanner scanner = new Scanner(System.in);
        final String apiKey = "MK6GJRU4RGFD8PU3" ;
        boolean keepGoing = true;
        while (keepGoing){
            Stocks current_stock = new Stocks();
            System.out.println("");


            currentStock = JOptionPane.showInputDialog(null, "Hello! Brewing your stock updates\nEnter the four letter stock code");


            if (currentStock.length() > 4){ currentStock = currentStock.substring(0, 4);}
            current_stock.getInfo(apiKey, currentStock);
            if (current_stock.errorCheck.contains("{")){
                JOptionPane.showMessageDialog(null,"This api request returned an error\nplease enter a different stock code.","Capital Markets", JOptionPane.INFORMATION_MESSAGE,img);

            } else {
                current_stock.checkStock();
                Stocks.stocks.add(current_stock);
            }

            option = JOptionPane.showConfirmDialog(null,"Select 'YES' to show all previous stocks from this session.\nSelect 'NO' to quit the program.", "Capital Markets",JOptionPane.INFORMATION_MESSAGE, JOptionPane.INFORMATION_MESSAGE,img);



            if (option == JOptionPane.YES_OPTION){
                Stocks.showAllStocks();
                option = JOptionPane.showConfirmDialog(null,"Select 'YES' to show all previous stocks from this session.\nSelect 'NO' to quit the program.","Capital Markets", JOptionPane.INFORMATION_MESSAGE);


                if (option == JOptionPane.NO_OPTION){
                    keepGoing = false;
                }
            }

            if (option == JOptionPane.NO_OPTION){
                keepGoing = false;
            }
        }
    }


}

package pw.lena.loancalculator;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;

import static android.R.attr.y;
import static java.sql.Types.ARRAY;
import static pw.lena.loancalculator.MainActivity.Status.Interest;
import static pw.lena.loancalculator.R.id.textbody;

public class ResultActivity extends Activity {

    private static final String TAG = "ResultActivity";
    private TextView textbody;
    private  WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_result);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         //   setSupportActionBar(toolbar);

            textbody = (TextView) findViewById(R.id.textbody);
            //final LinearLayout linearLayoutResult = (LinearLayout) findViewById(R.id.linearLayoutResult);

            wv = (WebView) findViewById(R.id.resultWebView);

            /*
            for( int i = 0; i < 10; i++ )
            {
                TextView textView = new TextView(this);
                textView.setText("now number is  " + i);
                linearLayoutResult.addView(textView);
            }
            */



        }
        catch (Exception ex)
        {
            Log.e(TAG, "error onCreate " + ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private String GetResultLoanEvenTotalPayments(int loan, double year, double interestpercent)
    {
        String result = "ok";
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        try {
            double interest = interestpercent / 100;
            double monthly = (loan * (interest / 12)) / (1 - (1 / Math.pow(1 + (interest / 12), (year * 12)))); // fixed

            String monthlyText = formatter.format(monthly);

            double principalTotal = (loan / (monthly * year * 12)) * 100;
            double InterestTotal = (((monthly * year * 12) - loan) / (monthly * year * 12)) * 100;

            String totalText = String.format("%10.2f", (monthly * year * 12));
            String percentText = String.format("%10.2f", ((monthly * year * 12) - loan));
            result =  "case 2: Even Total Payments"
                            + "\n" +
                            "Results:"
                            + "\n" +
                            "Payment every month	    "+ monthlyText
                            + "\n" +
                            "Total interest: "+ InterestTotal
                            + "\n" +
                            "Total of " + String.valueOf(year * 12) + "  payments   "+ totalText
                                                                                //formatter.format();
                            + "\n" +
                            "---------------------------"
                            + "\n" +
                            "Principal             " + "4"
                            + "\n" +
                            "Interest               " + "5"
                            + "\n" +
                            "6";
                    /*

                   "1",// ,
                   "2",// totalText,
                   "3", //percentText,
                   "4",// String.format("%10.2f", principalTotal),
                   "5", // String.format("%10.2f", InterestTotal),
                   "6" /*geturl(
                            "a",
                            (int)(((monthly * year * 12) - loan )),
                            loan,
                            loan,
                            (int)year,
                            interestpercent)

                   );
                         */
        }
        catch (Exception ex)
        {
            Log.e(TAG, "error GetResultLoanEvenTotalPayments " + ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
        }

        return result;
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            String result =  GetResultLoanEvenTotalPayments(Prefs.getLoan(this), Prefs.getTerm(this), Prefs.getInterest(this));
            textbody.setText(result);

            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setAllowContentAccess(true);
            wv.getSettings().setAllowFileAccess(true);

            // Get the Android assets folder path
            String folderPath = "file:android_asset/";

            // Get the HTML file name
            String fileName = "calculator.html";

            // Get the exact file location
            String file = folderPath + fileName;

            // Render the HTML file on WebView
            //
            wv.loadUrl(file);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "error onResume " + ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
        }

    }

    private String geturl(String type, int per, int pri, int a, int y, double i)
    {
        return "urlurl!!!";
        /*return String.format("Chart 1:"
                + "\n" +
        "http://lena.pw/chart.html?a={0}&y={1}&i={2}&f={3}&t={4}"
                + "\n" +
        "Chart 2:"
                + "\n" +
        "http://lena.pw/diagram.html?per={5}&pri={6}&fee={7}", a, y, i, 0, type,
        per, pri, 0);
        */
    }

    /*
    private String readHtml(String remoteUrl) {
        String out = "";
        BufferedReader in = null;
        try {
            URL url = new URL(remoteUrl);
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                out += str;
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out;
    }
    */
}

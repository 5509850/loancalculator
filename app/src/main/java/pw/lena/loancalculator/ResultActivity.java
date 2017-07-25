package pw.lena.loancalculator;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

import static pw.lena.loancalculator.utils.haveNetworkConnectionType;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final String TAG = "ResultActivity";
    private TextView text_title, textbody, textbody2;
    private  WebView chart;
    private  WebView diagram;
    private Context context = this;
    private Button exit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_result);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            textbody = (TextView) findViewById(R.id.textbody);
            textbody2 = (TextView) findViewById(R.id.textbody2);
            text_title = (TextView) findViewById(R.id.text_title);
            //final LinearLayout linearLayoutResult = (LinearLayout) findViewById(R.id.linearLayoutResult);

            chart = (WebView) findViewById(R.id.chartWebView);
            diagram = (WebView) findViewById(R.id.diagramWebView);

            exit = (Button)findViewById(R.id.btn_exit);
            exit.setOnClickListener(this);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "error onCreate " + ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_exit)
        {
           finish();
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
            double percentTotal = ((monthly * year * 12) - loan);

            String totalText = formatter.format(monthly * year * 12);
            //String percentText = String.format("%10.2f", ((monthly * year * 12) - loan));
            result =  "\n" +
                            "case 2: Even Total Payments"
                            + "\n" +
                            "Payment every month	    "+ monthlyText
                            + "\n" +
                            "Total interest             "+ formatter.format(percentTotal)
                            + "\n" +
                            "Total of " + (int)(year * 12) + "  payments   "+ totalText
                                                                                //formatter.format();
                            + "\n" +
                            "----------------------------------"
                            + "\n" +
                            "Principal             " + String.format("%10.1f", (principalTotal)) + "%"
                            + "\n" +
                            "Interest               " + String.format("%10.1f", (InterestTotal)) + "%"
                            ;

        }
        catch (Exception ex)
        {
            Log.e(TAG, "error GetResultLoanEvenTotalPayments " + ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
        }

        return result;
    }


    private String GetResultLoanEvenPrincipalPayments(int loan, double year, double interestpercent)
    {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        double interest = interestpercent / 100;
        double percTotal = 0;
        double monthlyTotal = 0;

        double amountRest = loan; //остаток основного долга
        double mainloan = (loan / (year * 12)); //fixed
        double percent = (amountRest * interest / 12);
        double monthly = mainloan + percent;
        String monthlyFirstText = formatter.format(monthly);
        double lastmonth = 0;

        for (int i = 1; i < (year * 12) + 1; i++)
        {
            amountRest -= mainloan;
            percTotal += percent;
            monthlyTotal += monthly;
            percent = (loan - (i * (loan / (year * 12)))) * (interest / 12);
            lastmonth = monthly;
            monthly = mainloan + percent;
        }

        double principalTotal = (loan  / monthlyTotal) * 100;
        double InterestTotal = (percTotal  / monthlyTotal) * 100;

        String monthlyTotalText = formatter.format(monthlyTotal);
        String percTotalText = formatter.format(percTotal);
        String monthlyLastText = formatter.format(lastmonth);

        return "\n" +
        "case 1: Even Principal Payments"
                + "\n" +
        "Pay in the first month  " + monthlyFirstText
                + "\n" +
        "Pay in the last month  " + monthlyLastText
                + "\n" +
        "Total interest	       " + percTotalText
                + "\n" +
        "Total of " + (int)(year * 12) +  " payments  " + monthlyTotalText
                + "\n" +
        "----------------------------------"
                        + "\n" +
        "Principal             " + String.format("%10.1f", (principalTotal)) + "%"
                + "\n" +
        "Interest               " + String.format("%10.1f", (InterestTotal)) + "%"
        + "\n" +
        "==================================";


    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            textbody.setText(GetResultLoanEvenPrincipalPayments(Prefs.getLoan(this), Prefs.getTerm(this), Prefs.getInterest(this)));
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String title =  formatter.format(Prefs.getLoan(this)) + " for " + Prefs.getTerm(this) + " years and " + Prefs.getInterest(this) + "%";
            text_title.setText(title);
            this.setTitle(title);

            textbody2.setText(GetResultLoanEvenTotalPayments(Prefs.getLoan(this), Prefs.getTerm(this), Prefs.getInterest(this)));

            chart.getSettings().setJavaScriptEnabled(true);
            chart.getSettings().setAllowContentAccess(true);
            chart.getSettings().setAllowFileAccess(true);

            diagram.getSettings().setJavaScriptEnabled(true);
            diagram.getSettings().setAllowContentAccess(true);
            diagram.getSettings().setAllowFileAccess(true);


            if (haveNetworkConnectionType(context) != 0)
            {
                chart.loadUrl("http://lena.pw/chart.html?a=" + Prefs.getLoan(this) + "&y=" + Prefs.getTerm(this) +"&i=" + Prefs.getInterest(this) + "&f=0&t=b");
                diagram.loadUrl("http://lena.pw/chart.html?a=" + Prefs.getLoan(this) + "&y=" + Prefs.getTerm(this) +"&i=" + Prefs.getInterest(this) + "&f=0&t=a");
            }
            else
            {
                Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "error onResume " + ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
        }

    }

    private String geturl(String type, int per, int pri, int a, int y, double i)
    {

        return "Chart 1:"
                + "\n" +
        "http://lena.pw/chart.html?a=" + a +
                "&y=" + y + "&i= " + i + " &f=0&t=" + type +
                "\n" +
        "Chart 2:"   +
                "\n" +
        "http://lena.pw/diagram.html?per=" + per + "&pri=" + pri + "&fee=0";

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

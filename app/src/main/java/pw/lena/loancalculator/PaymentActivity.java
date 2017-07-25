package pw.lena.loancalculator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Principal;
import java.text.NumberFormat;

import static pw.lena.loancalculator.MainActivity.Status.Interest;
import static pw.lena.loancalculator.R.id.textbody;

public class PaymentActivity extends AppCompatActivity {

    String KEY_TYPE_PAYMENT = "KEY_TYPE_PAYMENT";
    TextView tv_payment;
    int typeInfo = 0;
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    private Context context = this;
    private ProgressBar pmHeaderProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            if (bundle.getString(KEY_TYPE_PAYMENT).equals("EPPS"))
            {
                typeInfo = 1;
            }
            else if(bundle.getString(KEY_TYPE_PAYMENT).equals("ETPS"))
            {
                typeInfo = 2;
            }
        }

        tv_payment = (TextView) findViewById(R.id.tv_payment);
        pmHeaderProgress = (ProgressBar)findViewById(R.id.pmHeaderProgress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.setTitle(formatter.format(Prefs.getLoan(this)) + " for " + Prefs.getTerm(this) + " years and " + Prefs.getInterest(this) + "%");
        if (typeInfo == 1)
        {
            new EvenPrincipalPaymentsScheduleAsync().execute();
            //EvenPrincipalPaymentsSchedule();

        }
       else if (typeInfo == 2)
        {
            new EvenTotalPaymentsScheduleAsync().execute();
            //EvenTotalPaymentsSchedule();
           // Toast.makeText(this, "Even Total Payments Schedule", Toast.LENGTH_SHORT).show();
        }
    }

    //EvenPrincipalPaymentsSchedule
    private void EvenTotalPaymentsSchedule()
    {
        String title =  formatter.format(Prefs.getLoan(this)) + " for " + Prefs.getTerm(this) + " years and " + Prefs.getInterest(this) + "%";
        this.setTitle(title);

        Double percTotal = Double.valueOf(0);
        Double mainTotal = Double.valueOf(0);
        Double monthlyTotal = Double.valueOf(0);

        Double interest = Double.valueOf(Prefs.getInterest(this) * 1.0 / 100);
        Double amountRest = Double.valueOf(Prefs.getLoan(this));
        Double percent = Double.valueOf(amountRest * interest / 12.0 );
        Double monthly = (Prefs.getLoan(this) * (interest / 12.0 )) / ( 1.0 - ( 1.0 / Math.pow( 1.0 + (interest / 12.0), (Prefs.getTerm(this) * 12.0)))); // fixed
        Double mainloan = monthly - percent;

        StringBuilder sb = new StringBuilder("Loan " + title);
        sb.append("\n"); sb.append("\n");
        sb.append("Even Total Payments Schedule");
        sb.append("\n");
        sb.append("\n");
        sb.append("--------------------------------------------------------------------");
        sb.append("\n");
        sb.append("Month |");
        sb.append("\t");
        sb.append("Balance |");
        sb.append("\t");
        sb.append("Interest |");
        sb.append("\t");
        sb.append("Principal |");
        sb.append("\t");
        sb.append("Payment |");
        sb.append("\n");
        sb.append("--------------------------------------------------------------------");
        sb.append("\n");
        for (int i = 1; i < (Prefs.getTerm(this) * 12)+ 1; i++)
        {
            if (i < 10)
            {
                sb.append("0" + i);
            }
            else
            {
                sb.append(i);
            }
            sb.append("\t\t\t | ");
            sb.append(Math.round(amountRest));
            sb.append("\t\t | ");
            sb.append(Math.round(percent));
            sb.append("\t\t | ");
            sb.append(Math.round(mainloan));
            sb.append("\t\t | ");
            sb.append(Math.round(monthly));
            sb.append("\n");

            amountRest -= mainloan;
            mainTotal += mainloan;
            percTotal += percent;
            monthlyTotal += monthly;

            percent = Double.valueOf(amountRest * interest / 12);
            mainloan = monthly - percent;
        }
        sb.append("--------------------------------------------------------------------");
        sb.append("\nTotal: ");
        sb.append(formatter.format(monthlyTotal));
        Double overpay = (monthlyTotal / mainTotal) * 100;
        sb.append(" (");
        sb.append(String.format("%10.2f", overpay));
        sb.append(" %)");
        sb.append("\n ");

        sb.append("\nInterest: ");
        sb.append(formatter.format(percTotal));
        Double percpart = (percTotal / monthlyTotal) * 100;
        sb.append(" (");
        sb.append(String.format("%10.2f", percpart));
        sb.append(" %)");
        sb.append("\nPrincipal: ");
        sb.append(formatter.format(mainTotal));
        Double mainTotalpart = 100 - percpart;
        sb.append(" (");
        sb.append(String.format("%10.2f", mainTotalpart));
        sb.append(" %)");

        sb.append("\n");
        tv_payment.setText(sb);
        //(getResources().getString(R.string.large_text));

    }

    private void EvenPrincipalPaymentsSchedule()
    {
        String title =  formatter.format(Prefs.getLoan(this)) + " for " + Prefs.getTerm(this) + " years and " + Prefs.getInterest(this) + "%";
        this.setTitle(title);

        Double percTotal = Double.valueOf(0);
        Double mainTotal = Double.valueOf(0);
        Double monthlyTotal = Double.valueOf(0);

        Double interest = Double.valueOf(Prefs.getInterest(this) * 1.0 / 100);
        Double amountRest = Double.valueOf(Prefs.getLoan(this));
        Double mainloan = Double.valueOf(Prefs.getLoan(this) / (Prefs.getTerm(this) * 12.0)); //fixed
        Double percent = Double.valueOf(amountRest * interest / 12.0 );
        Double monthly = mainloan + percent;

        //Double lastm = Double.valueOf(0);


        StringBuilder sb = new StringBuilder("Loan " + title);
        sb.append("\n"); sb.append("\n");
        sb.append("Even Principal Payments Schedule");
        sb.append("\n");
        sb.append("\n");
        sb.append("--------------------------------------------------------------------");
        sb.append("\n");
        sb.append("Month |");
        sb.append("\t");
        sb.append("Balance |");
        sb.append("\t");
        sb.append("Interest |");
        sb.append("\t");
        sb.append("Principal |");
        sb.append("\t");
        sb.append("Payment |");
        sb.append("\n");
        sb.append("--------------------------------------------------------------------");
        sb.append("\n");
        for (int i = 1; i < (Prefs.getTerm(this) * 12)+ 1; i++)
        {
            if (i < 10)
            {
                sb.append("0" + i);
            }
            else
            {
                sb.append(i);
            }
            sb.append("\t\t\t | ");
            sb.append(Math.round(amountRest));
            sb.append("\t\t | ");
            sb.append(Math.round(percent));
            sb.append("\t\t | ");
            sb.append(Math.round(mainloan));
            sb.append("\t\t | ");
            sb.append(Math.round(monthly));
            sb.append("\n");

            amountRest -= mainloan;
            mainTotal += mainloan;
            percTotal += percent;
            monthlyTotal += monthly;

            //lastm = monthly;

            percent = Double.valueOf((Prefs.getLoan(this) - (i * (Prefs.getLoan(this) / (Prefs.getTerm(this) * 12.0)))) * (interest / 12.0));
            monthly = mainloan + percent;
        }
        sb.append("--------------------------------------------------------------------");
        sb.append("\nTotal: ");
        sb.append(formatter.format(monthlyTotal));
        Double overpay = (monthlyTotal / mainTotal) * 100;
        sb.append(" (");
        sb.append(String.format("%10.2f", overpay));
        sb.append(" %)");
        sb.append("\n ");

        sb.append("\nInterest: ");
        sb.append(formatter.format(percTotal));
        Double percpart = (percTotal / monthlyTotal) * 100;
        sb.append(" (");
        sb.append(String.format("%10.2f", percpart));
        sb.append(" %)");
        sb.append("\nPrincipal: ");
        sb.append(formatter.format(mainTotal));
        Double mainTotalpart = 100 - percpart;
        sb.append(" (");
        sb.append(String.format("%10.2f", mainTotalpart));
        sb.append(" %)");

        sb.append("\n");
        tv_payment.setText(sb);
    }

    public class EvenPrincipalPaymentsScheduleAsync extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");
            Double percTotal = Double.valueOf(0);
            Double mainTotal = Double.valueOf(0);
            Double monthlyTotal = Double.valueOf(0);

            Double interest = Double.valueOf(Prefs.getInterest(context) * 1.0 / 100);
            Double amountRest = Double.valueOf(Prefs.getLoan(context));
            Double mainloan = Double.valueOf(Prefs.getLoan(context) / (Prefs.getTerm(context) * 12.0)); //fixed
            Double percent = Double.valueOf(amountRest * interest / 12.0 );
            Double monthly = mainloan + percent;

            StringBuilder sb = new StringBuilder("Loan " + formatter.format(Prefs.getLoan(context)) + " for " + Prefs.getTerm(context) + " years and " + Prefs.getInterest(context) + "%");
            sb.append("\n"); sb.append("\n");
            sb.append("Even Principal Payments Schedule");
            sb.append("\n");
            sb.append("\n");
            sb.append("--------------------------------------------------------------------");
            sb.append("\n");
            sb.append("Month |");
            sb.append("\t");
            sb.append("Balance |");
            sb.append("\t");
            sb.append("Interest |");
            sb.append("\t");
            sb.append("Principal |");
            sb.append("\t");
            sb.append("Payment |");
            sb.append("\n");
            sb.append("--------------------------------------------------------------------");
            sb.append("\n");
            for (int i = 1; i < (Prefs.getTerm(context) * 12)+ 1; i++)
            {
                publishProgress(i);
                if (i < 10)
                {
                    sb.append("0" + i);
                }
                else
                {
                    sb.append(i);
                }
                sb.append("\t\t\t | ");
                sb.append(Math.round(amountRest));
                sb.append("\t\t | ");
                sb.append(Math.round(percent));
                sb.append("\t\t | ");
                sb.append(Math.round(mainloan));
                sb.append("\t\t | ");
                sb.append(Math.round(monthly));
                sb.append("\n");

                amountRest -= mainloan;
                mainTotal += mainloan;
                percTotal += percent;
                monthlyTotal += monthly;

                percent = Double.valueOf((Prefs.getLoan(context) - (i * (Prefs.getLoan(context) / (Prefs.getTerm(context) * 12.0)))) * (interest / 12.0));
                monthly = mainloan + percent;
            }
            sb.append("--------------------------------------------------------------------");
            sb.append("\nTotal: ");
            sb.append(formatter.format(monthlyTotal));
            Double overpay = (monthlyTotal / mainTotal) * 100;
            sb.append(" (");
            sb.append(String.format("%10.2f", overpay));
            sb.append(" %)");
            sb.append("\n ");

            sb.append("\nInterest: ");
            sb.append(formatter.format(percTotal));
            Double percpart = (percTotal / monthlyTotal) * 100;
            sb.append(" (");
            sb.append(String.format("%10.2f", percpart));
            sb.append(" %)");
            sb.append("\nPrincipal: ");
            sb.append(formatter.format(mainTotal));
            Double mainTotalpart = 100 - percpart;
            sb.append(" (");
            sb.append(String.format("%10.2f", mainTotalpart));
            sb.append(" %)");

            sb.append("\n");
            return sb.toString();
        }

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
            int percent_complete = (int)(100.0 * a[0] / (Prefs.getTerm(context) * 12.0));
            pmHeaderProgress.setProgress(percent_complete);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG + " onPostExecute", "" + result);
            pmHeaderProgress.setVisibility(View.INVISIBLE);
            tv_payment.setText(result);
        }
    }

    public class EvenTotalPaymentsScheduleAsync extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");
            Double percTotal = Double.valueOf(0);
            Double mainTotal = Double.valueOf(0);
            Double monthlyTotal = Double.valueOf(0);

            Double interest = Double.valueOf(Prefs.getInterest(context) * 1.0 / 100);
            Double amountRest = Double.valueOf(Prefs.getLoan(context));
            Double percent = Double.valueOf(amountRest * interest / 12.0 );
            Double monthly = (Prefs.getLoan(context) * (interest / 12.0 )) / ( 1.0 - ( 1.0 / Math.pow( 1.0 + (interest / 12.0), (Prefs.getTerm(context) * 12.0)))); // fixed
            Double mainloan = monthly - percent;

            StringBuilder sb = new StringBuilder("Loan " + formatter.format(Prefs.getLoan(context)) + " for " + Prefs.getTerm(context) + " years and " + Prefs.getInterest(context) + "%");
            sb.append("\n"); sb.append("\n");
            sb.append("Even Total Payments Schedule");
            sb.append("\n");
            sb.append("\n");
            sb.append("--------------------------------------------------------------------");
            sb.append("\n");
            sb.append("Month |");
            sb.append("\t");
            sb.append("Balance |");
            sb.append("\t");
            sb.append("Interest |");
            sb.append("\t");
            sb.append("Principal |");
            sb.append("\t");
            sb.append("Payment |");
            sb.append("\n");
            sb.append("--------------------------------------------------------------------");
            sb.append("\n");
            for (int i = 1; i < (Prefs.getTerm(context) * 12)+ 1; i++)
            {
                publishProgress(i);
                if (i < 10)
                {
                    sb.append("0" + i);
                }
                else
                {
                    sb.append(i);
                }
                sb.append("\t\t\t | ");
                sb.append(Math.round(amountRest));
                sb.append("\t\t | ");
                sb.append(Math.round(percent));
                sb.append("\t\t | ");
                sb.append(Math.round(mainloan));
                sb.append("\t\t | ");
                sb.append(Math.round(monthly));
                sb.append("\n");

                amountRest -= mainloan;
                mainTotal += mainloan;
                percTotal += percent;
                monthlyTotal += monthly;

                percent = Double.valueOf(amountRest * interest / 12);
                mainloan = monthly - percent;
            }
            sb.append("--------------------------------------------------------------------");
            sb.append("\nTotal: ");
            sb.append(formatter.format(monthlyTotal));
            Double overpay = (monthlyTotal / mainTotal) * 100;
            sb.append(" (");
            sb.append(String.format("%10.2f", overpay));
            sb.append(" %)");
            sb.append("\n ");

            sb.append("\nInterest: ");
            sb.append(formatter.format(percTotal));
            Double percpart = (percTotal / monthlyTotal) * 100;
            sb.append(" (");
            sb.append(String.format("%10.2f", percpart));
            sb.append(" %)");
            sb.append("\nPrincipal: ");
            sb.append(formatter.format(mainTotal));
            Double mainTotalpart = 100 - percpart;
            sb.append(" (");
            sb.append(String.format("%10.2f", mainTotalpart));
            sb.append(" %)");

            sb.append("\n");
            return sb.toString();
        }

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
            int percent_complete = (int)(100.0 * a[0] / (Prefs.getTerm(context) * 12.0));
            pmHeaderProgress.setProgress(percent_complete);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG + " onPostExecute", "" + result);
            pmHeaderProgress.setVisibility(View.INVISIBLE);
            tv_payment.setText(result);
        }
    }
}

package pw.lena.loancalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (typeInfo == 1)
        {
           // Toast.makeText(this, "Even Principal Payments Schedule", Toast.LENGTH_SHORT).show();
            EvenPrincipalPaymentsSchedule();

        }
            else if (typeInfo == 2)
        {
            EvenTotalPaymentsSchedule();
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

            percent = Double.valueOf(amountRest * interest / 12);
            mainloan = monthly - percent;
        }
        sb.append("--------------------------------------------------------------------");
        sb.append("\n");
        sb.append("Total:");
        sb.append("\t\t\t | ");
        sb.append(0);
        sb.append("\t Interest: ");
        sb.append(formatter.format(percTotal));
        sb.append("\t Principal: ");
        sb.append(formatter.format(mainTotal));
        sb.append("\t Total: ");
        sb.append(formatter.format(monthlyTotal));
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
        sb.append("\n");
        sb.append("Total:");
        sb.append("\t\t\t | ");
        sb.append(0);
        sb.append("\t Interest: ");
        sb.append(formatter.format(percTotal));
        sb.append("\t Principal: ");
        sb.append(formatter.format(mainTotal));
        sb.append("\t Total: ");
        sb.append(formatter.format(monthlyTotal));
        sb.append("\n");
        tv_payment.setText(sb);
    }
}

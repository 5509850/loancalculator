package pw.lena.loancalculator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.text.NumberFormat;

import static pw.lena.loancalculator.R.id.text_title;
import static pw.lena.loancalculator.R.id.textbody;
import static pw.lena.loancalculator.R.id.textbody2;
import static pw.lena.loancalculator.utils.haveNetworkConnectionType;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView chart;
    private Context context = this;
    String KEY_TYPE = "KEY_TYPE";
    int typeInfo = 0;
    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        chart = (WebView) findViewById(R.id.chartWebView);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            if (bundle.getString(KEY_TYPE).equals("EPPS"))
            {
                typeInfo = 1;
            }
            else if(bundle.getString(KEY_TYPE).equals("ETPS"))
            {
                typeInfo = 2;
            }
        }
        exit = (Button)findViewById(R.id.btn_exit);
        exit.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String title =  formatter.format(Prefs.getLoan(this)) + " for " + Prefs.getTerm(this) + " years and " + Prefs.getInterest(this) + "%";
            this.setTitle(title);

            chart.getSettings().setJavaScriptEnabled(true);
            chart.getSettings().setAllowContentAccess(true);
            chart.getSettings().setAllowFileAccess(true);

            if (haveNetworkConnectionType(context) != 0)
            {
                if (typeInfo == 1)
                {
                    chart.loadUrl("http://lena.pw/chart.html?a=" + Prefs.getLoan(this) + "&y=" + Prefs.getTerm(this) +"&i=" + Prefs.getInterest(this) + "&f=0&t=b");
                }
                else if (typeInfo == 2)
                {
                    chart.loadUrl("http://lena.pw/chart.html?a=" + Prefs.getLoan(this) + "&y=" + Prefs.getTerm(this) +"&i=" + Prefs.getInterest(this) + "&f=0&t=a");
                }
                else
                {
                    finish();
                }
            }
            else
            {
                Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG);
            }
        }
        catch (Exception ex)
        {
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
}

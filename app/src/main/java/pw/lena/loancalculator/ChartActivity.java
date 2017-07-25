package pw.lena.loancalculator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.NumberFormat;

import static pw.lena.loancalculator.R.id.text_title;
import static pw.lena.loancalculator.R.id.textbody;
import static pw.lena.loancalculator.R.id.textbody2;
import static pw.lena.loancalculator.utils.haveNetworkConnectionType;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView mWebView;
    private Context context = this;
    String KEY_TYPE = "KEY_TYPE";
    int typeInfo = 0;
    private Button exit;
    final Activity activity = this;
    private ProgressBar pbHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        mWebView = (WebView) findViewById(R.id.chartWebView);
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
        pbHeaderProgress = (ProgressBar) findViewById(R.id.pbHeaderProgress);
        exit.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String title =  formatter.format(Prefs.getLoan(this)) + " for " + Prefs.getTerm(this) + " years and " + Prefs.getInterest(this) + "%";
            this.setTitle(title);

           /* mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setAllowContentAccess(true);
            mWebView.getSettings().setAllowFileAccess(true);
*/
            if (haveNetworkConnectionType(context) != 0)
            {
                if (typeInfo == 1)
                {
                    LoadUrl("http://lena.pw/chart.html?a=" + Prefs.getLoan(this) + "&y=" + Prefs.getTerm(this) +"&i=" + Prefs.getInterest(this) + "&f=0&t=b");
                    //mWebView.loadUrl("http://lena.pw/chart.html?a=" + Prefs.getLoan(this) + "&y=" + Prefs.getTerm(this) +"&i=" + Prefs.getInterest(this) + "&f=0&t=b");
                }
                else if (typeInfo == 2)
                {
                    LoadUrl("http://lena.pw/chart.html?a=" + Prefs.getLoan(this) + "&y=" + Prefs.getTerm(this) +"&i=" + Prefs.getInterest(this) + "&f=0&t=a");
                    //mWebView.loadUrl("http://lena.pw/chart.html?a=" + Prefs.getLoan(this) + "&y=" + Prefs.getTerm(this) +"&i=" + Prefs.getInterest(this) + "&f=0&t=a");
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

    private void LoadUrl(String url_site)
    {
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {

                activity.setTitle("Wait please");
                pbHeaderProgress.setProgress(progress * 100);//
                activity.setProgress(progress * 100);

                if(progress == 100)
                {
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    String title =  formatter.format(Prefs.getLoan(context)) + " for " + Prefs.getTerm(context) + " years and " + Prefs.getInterest(context) + "%";
                    activity.setTitle(title);
                    pbHeaderProgress.setVisibility(View.INVISIBLE);
                }

            }
        });

        mWebView.loadUrl(url_site);
    }
}

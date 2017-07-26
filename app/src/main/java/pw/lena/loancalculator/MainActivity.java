package pw.lena.loancalculator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;

import static pw.lena.loancalculator.Prefs.getInterest;
import static pw.lena.loancalculator.Prefs.getTerm;
import static pw.lena.loancalculator.R.string.years;
import static pw.lena.loancalculator.utils.haveNetworkConnectionType;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private String currentInput = "";
    private TextView display, loan, term, interest;
    private Button one, two, three, four, five, six, seven, eight, nine, zero, clear, back;
    private ConstraintLayout mainLayer;
    private Integer _loan = 0, _term = 0, _interest = 0;

    public enum Status { Loan, Term, Interest, Fee, Ready }
    private Status status = Status.Loan;
    private Context context = this;
    private View snapView = null;

    private Vibrator mVibrator;
    private static final int VIBRATE_MILLIS = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snapView = view;
                NextStep(view);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        one = (Button)findViewById(R.id.btn_one);
        two = (Button)findViewById(R.id.btn_two);
        three = (Button)findViewById(R.id.btn_three);
        four = (Button)findViewById(R.id.btn_four);
        five = (Button)findViewById(R.id.btn_five);
        six = (Button)findViewById(R.id.btn_six);
        seven = (Button)findViewById(R.id.btn_seven);
        eight = (Button)findViewById(R.id.btn_eight);
        nine = (Button)findViewById(R.id.btn_nine);
        zero = (Button)findViewById(R.id.btn_zero);
        clear = (Button)findViewById(R.id.btn_clear);
        back = (Button)findViewById(R.id.btn_back);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zero.setOnClickListener(this);
        clear.setOnClickListener(this);
        back.setOnClickListener(this);

        display = (TextView) findViewById(R.id.display);
        loan = (TextView) findViewById(R.id.loan);
        term = (TextView) findViewById(R.id.term);
        interest = (TextView) findViewById(R.id.interest);

        mainLayer = (ConstraintLayout) findViewById(R.id.main_content_layer);


        status = Status.Loan;

        this.setTitle(R.string.loan_summary);
        LoadDefaultSavedData();
    }


    private boolean PreviousStep() {
        String mess = "Replace with your own action2";
   //     Log.i(TAG, "status is before PreviousStep" + status);
        try{
        if (status == Status.Loan)
        {
            return true;
        }
        else
        if (status == Status.Term)
        {
            SaveTerm();
            status = Status.Loan;
            mess = getString(R.string.loan);
            _loan = Prefs.getLoan(context);
            currentInput = _loan.toString();
            display.setText(NumberFormat.getIntegerInstance().format(_loan));
            this.setTitle(R.string.loan_summary);
        }
        else
        if (status == Status.Interest)
        {
            SaveInterest();
            mess = getString(R.string.term_summary);
            _term = Prefs.getTerm(context);
            currentInput = _term.toString();
            display.setText(NumberFormat.getIntegerInstance().format(_term));
            this.setTitle(R.string.term_summary);
            status = Status.Term;
           // startActivity(new Intent(this, ResultActivity.class));
        }

        if (snapView != null)
        {
            Snackbar.make(snapView, mess, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        }
        catch (Exception ex)
        {
          //  Log.e(TAG, "error PreviousStep" + ex.getMessage());
        }
       // Log.i(TAG, "status is after PreviousStep" + status);
        SetBoldCurrentStatus();
        return false;
    }

    private void NextStep(View view) {
     //   Log.i(TAG, "before NextStep status is " + status);
        String mess = "Replace with your own action";
        try {
            if (currentInput == null || currentInput.equals("") || currentInput.equals("0")) {
                Snackbar.make(view, getString(R.string.emptyval), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mVibrator.vibrate(
                        new long[]{0l, VIBRATE_MILLIS,
                                50l, VIBRATE_MILLIS,
                                50l, VIBRATE_MILLIS},
                        -1);
                Animation shake = AnimationUtils.loadAnimation(context,
                        R.anim.shake);
                mainLayer.startAnimation(shake);
                return;
            }

        //    Log.w(TAG, "before NextStep currentInput " + currentInput);

            if (status == Status.Loan) {
                SaveLoan();
                status = Status.Term;
                mess = getString(R.string.loanterm);
                currentInput = getTerm(context).toString();
                display.setText(NumberFormat.getIntegerInstance().format(getTerm(context)));
                this.setTitle(R.string.term_summary);
            } else {
                if (status == Status.Term) {
                    currentInput = "";
                    SaveTerm();
                    status = Status.Interest;
                    mess = getString(R.string.interest_desc);
                    currentInput =  Prefs.getInterest(context).toString();
                    display.setText(NumberFormat.getIntegerInstance().format(getInterest(context)));
                    this.setTitle(R.string.interest_summary);
                } else if (status == Status.Interest) {
                    SaveInterest();
                    if(CheckValidData())
                    {
                        status = Status.Loan;
                        Animation fade = AnimationUtils.loadAnimation(context,
                                R.anim.fade_in);
                        mainLayer.startAnimation(fade);
                        LoadDefaultSavedData();
                        startActivity(new Intent(this, ResultActivity.class));
                    }
                }
                else if (status == Status.Ready)
                {
                    LoadDefaultSavedData();
                }
            }
            SetBoldCurrentStatus();
        }
        catch (Exception ex)
        {
          //  Log.e(TAG, "error NextStep " + ex.getMessage());
        }
      //  Log.i(TAG, "after NextStep status is " + status);
      //  Log.w(TAG, "after NextStep currentInput " + currentInput);
        Snackbar.make(view, mess, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private boolean CheckValidData()
    {
        boolean valid =  Prefs.getLoan(this)!= 0 && Prefs.getTerm(this)!= 0 && Prefs.getInterest(this)!= 0;
        if (!valid)
        {
            Warning();
            Toast.makeText(context, getString(R.string.emptyval), Toast.LENGTH_LONG);
        }
        return valid;
    }

    private void SaveCurrentData()
    {
        try {
            if (status == Status.Loan) {
                SaveLoan();}
            else
                if (status == Status.Term) {
                    currentInput = "";
                    SaveTerm();}
                else if (status == Status.Interest) {
                    SaveInterest();}
        }
        catch (Exception ex)
        {
           // Log.e(TAG, "error SaveCurrentData " + ex.getMessage());
        }
    }

    private void LoadDefaultSavedData() {
        try {
            this.setTitle(R.string.loan_summary);
            SetBoldCurrentStatus();
            _loan = Prefs.getLoan(this);
            _term = getTerm(this);
            _interest = getInterest(this);
            currentInput = _loan.toString();

            loan.setText(getString(R.string.amount) + ": " + NumberFormat.getIntegerInstance().format(_loan) + " " + getString(R.string.currency));
            term.setText(getString(R.string.term) + ": " + _term + " " + getString(years));
            interest.setText(getString(R.string.interest) + ": " + _interest + "%");
            if (_loan != 0)
            {
                display.setText(NumberFormat.getIntegerInstance().format(_loan));
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG);
          //  Log.e(TAG, "error LoadDefaultSavedData " + ex.getMessage());
        }
    }

    private void SetBoldCurrentStatus() {

        if (status == Status.Loan) {
            loan.setTypeface(null, Typeface.BOLD);
            interest.setTypeface(null, Typeface.ITALIC);
            term.setTypeface(null, Typeface.ITALIC);
        } else if (status == Status.Term)
        {
            loan.setTypeface(null, Typeface.ITALIC);
            interest.setTypeface(null, Typeface.ITALIC);
            term.setTypeface(null, Typeface.BOLD);
        }
        else if (status == Status.Interest)
        {
            loan.setTypeface(null, Typeface.ITALIC);
            interest.setTypeface(null, Typeface.BOLD);
            term.setTypeface(null, Typeface.ITALIC);
        }
        else
        {
            loan.setTypeface(null, Typeface.NORMAL);
            interest.setTypeface(null, Typeface.NORMAL);
            term.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (status.equals(Status.Loan))
        {
            SaveLoan();
            SaveStatus();
            SaveTerm();
            SaveInterest();
        }
    }

    private void SaveStatus() {
        try {
            Prefs.setStatus(this, status.name());
        }
            catch(Exception ex)
            {
               // Log.e(TAG, "error SaveStatus " + ex.getMessage());
            }
    }

    private void SaveLoan() {
            try {
                Prefs.setLoan(this, _loan);
            }
            catch (Exception ex)
            {
             //   Log.e(TAG, "error SaveLoan " + ex.getMessage());
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG);
            }
    }

    private void SaveTerm() {
        if (_term > 100)
        {
            _term = 100;
        }
            try{
                Prefs.setTerm(this, _term);
            }
            catch (Exception ex)
            {
              //  Log.e(TAG, "error SaveTerm " + ex.getMessage());
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG);
            }
    }

    private void SaveInterest() {
            try {
                Prefs.setInterest(this, _interest);
            }
            catch (Exception ex)
            {
              //  Log.e(TAG, "error SaveInterest " + ex.getMessage());
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG);
            }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(PreviousStep())
            {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SetMenuEnable();
        return true;
    }

    private void SetMenuEnable()
    {
        try{
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menuNav = navigationView.getMenu();

            boolean enable = haveNetworkConnectionType(context) != 0;
            menuNav.findItem(R.id.nav_chart_epp).setEnabled(enable);
            menuNav.findItem(R.id.nav_chart_etp).setEnabled(enable);
        }
        catch (Exception ex)
        {
          //  Log.e(TAG, "error SetMenuEnable" + ex.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void StartPrefs()
    {
        try{
            startActivity(new Intent(this, Prefs.class));
        }
        catch (Exception ex)
        {
         Toast.makeText(this, ex.getMessage(),Toast.LENGTH_LONG);
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_schedule_epp) {
            SaveCurrentData();
            //Toast.makeText(this, "Even Principal Payments Schedule", Toast.LENGTH_SHORT).show();
            if (CheckValidData())
            {
                Intent intent = new Intent();
                intent.setClass(this, PaymentActivity.class);
                Bundle b = new Bundle();
                b.putString("KEY_TYPE_PAYMENT", "EPPS");
                intent.putExtras(b);
                startActivity(intent);
            }
        } else if (id == R.id.nav_schedule_etp) {
            SaveCurrentData();
            if (CheckValidData())
            {
                Intent intent = new Intent();
                intent.setClass(this, PaymentActivity.class);
                Bundle b = new Bundle();
                b.putString("KEY_TYPE_PAYMENT", "ETPS");
                intent.putExtras(b);
                startActivity(intent);
            }
        } else if (id == R.id.nav_chart_epp) {
            SaveCurrentData();
            if (haveNetworkConnectionType(context) != 0 && CheckValidData())
            {
                Intent intent = new Intent();
                intent.setClass(this, ChartActivity.class);
                Bundle b = new Bundle();
                b.putString("KEY_TYPE", "EPPS");
                intent.putExtras(b);
                startActivity(intent);
            }
             else
            {
                Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG);
            }

        } else if (id == R.id.nav_chart_etp) {
            SaveCurrentData();
            if (haveNetworkConnectionType(context) != 0 && CheckValidData())
            {
                Intent intent = new Intent();
                intent.setClass(this, ChartActivity.class);
                Bundle b = new Bundle();
                b.putString("KEY_TYPE", "ETPS");
                intent.putExtras(b);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG);
            }
        } else if (id == R.id.nav_result) {
            SaveCurrentData();
            if (CheckValidData())
            {
                LoadDefaultSavedData();
                startActivity(new Intent(this, ResultActivity.class));
            }

        } else if (id == R.id.nav_share) {
            if (CheckValidData())
            {
                Sharing();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {

        if ((view.getId() == R.id.btn_one
                || view.getId() == R.id.btn_two
                || view.getId() == R.id.btn_two
                || view.getId() == R.id.btn_three
                || view.getId() == R.id.btn_four
                || view.getId() == R.id.btn_five
                || view.getId() == R.id.btn_six
                || view.getId() == R.id.btn_seven
                || view.getId() == R.id.btn_eight
                || view.getId() == R.id.btn_nine
                || view.getId() == R.id.btn_zero
                )&& isDigitFull(currentInput))
        {
//            Toast.makeText(this, "Warning! The maximum value for it\n" +
//                    "\n", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "", Toast.LENGTH_SHORT).cancel();
           Warning();
            return;
        }
       switch (view.getId())
        {

            case R.id.btn_one:
            {
                Pressed("1");
                break;
            }
            case R.id.btn_two:
            {
                Pressed("2");
                break;
            }
            case R.id.btn_three:
            {
                Pressed("3");
                break;
            }
            case R.id.btn_four:
            {
                Pressed("4");
                break;
            }
            case R.id.btn_five:
            {
                Pressed("5");
                break;
            }
            case R.id.btn_six:
            {
                Pressed("6");
                break;
            }
            case R.id.btn_seven:
            {
                Pressed("7");
                break;
            }
            case R.id.btn_eight:
            {
                Pressed("8");
                break;
            }
            case R.id.btn_nine:
            {
                Pressed("9");
                break;
            }
            case R.id.btn_zero:
            {
                if (!currentInput.equals("0") && !currentInput.equals(""))
                {
                    Pressed("0");
                }
                break;
            }
            case R.id.btn_clear:
            {
                currentInput = "";
                display.setText(R.string.zero);
                if (status == Status.Loan) {
                    _loan = 0;
                } else if (status == Status.Term) {
                    _term = 0;
                } else if (status == Status.Interest) {
                    _interest = 0;
                }
                break;
            }
            case R.id.btn_back:
            {
                currentInput = removeMethod(currentInput);
                if (currentInput.equals(""))
                {
                    display.setText(R.string.zero);
                    if (status == Status.Loan) {
                        _loan = 0;
                    } else if (status == Status.Term) {
                        _term = 0;
                    } else if (status == Status.Interest) {
                        _interest = 0;
                    }
                    display.setText(NumberFormat.getIntegerInstance().format(0));
                }
                else
                {
                    display.setText(currentInput);
                    int i = Integer.parseInt(currentInput);
                    if (status == Status.Loan) {
                        _loan = i;
                    } else if (status == Status.Term) {
                        _term = i;
                    } else if (status == Status.Interest) {
                        _interest = i;
                    }
                    display.setText(NumberFormat.getIntegerInstance().format(i));
                }
                break;
            }
        }
        try
        {
           ShowInfo(view.getId());

        }
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void Sharing()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String title =  getString(R.string.loan) + " " + formatter.format(Prefs.getLoan(this)) + " " + getString(R.string.fors) + " " + getTerm(this)
                + " " + getString(R.string.yearsand) + " " +
                + getInterest(this) + "%";
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                title + "\n" +
                GetResultLoanEvenPrincipalPayments(Prefs.getLoan(this), getTerm(this), getInterest(this)) + "\n" +
                GetResultLoanEvenTotalPayments(Prefs.getLoan(this), getTerm(this), getInterest(this)));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
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
                    getString(R.string.case2)
                    + "\n" +
                    getString(R.string.payment_every_month) + ": "+ monthlyText
                    + "\n" +
                    getString(R.string.total_interest) + ": " + formatter.format(percentTotal)
                    + "\n" +
                    getString(R.string.totalof) + " " + (int)(year * 12) + "  " + getString(R.string.payments) + "  " + totalText
                    //formatter.format();
                    + "\n" +
                    "----------------------------------"
                    + "\n" +
                    getString(R.string.principal)       + ": " + String.format("%10.1f", (principalTotal)) + "%"
                    + "\n" +
                    getString(R.string.interests) + ": " + String.format("%10.1f", (InterestTotal)) + "%";

        }
        catch (Exception ex)
        {
            // Log.e(TAG, "error GetResultLoanEvenTotalPayments " + ex.getMessage());
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
                getString(R.string.case1)
                + "\n" +
                getString(R.string.payinthefirstmonth) + ":  " + monthlyFirstText
                + "\n" +
                getString(R.string.payinthelasttmonth) +  ":  " + monthlyLastText
                + "\n" +
                getString(R.string.total_interest) + ": " + percTotalText
                + "\n" +
                getString(R.string.totalof) + " " + (int)(year * 12) +  "  " + getString(R.string.payments) + "  " + monthlyTotalText
                + "\n" +
                "----------------------------------"
                + "\n" +
                getString(R.string.principal)       + ": " + String.format("%10.1f", (principalTotal)) + "%"
                + "\n" +
                getString(R.string.interests) + ": " + String.format("%10.1f", (InterestTotal)) + "%"
                + "\n" +
                "==================================";


    }

    private void Warning()
    {
        mVibrator.vibrate(
                new long[]{0l, VIBRATE_MILLIS,
                        50l, VIBRATE_MILLIS,
                        50l, VIBRATE_MILLIS},
                -1);
        Animation fade = AnimationUtils.loadAnimation(context,
                R.anim.fade_in);
        mainLayer.startAnimation(fade);
    }

    private void ShowInfo(int id) {
        try {
            if (status == Status.Loan) {
                if (!currentInput.equals("")) {
                    loan.setText(getString(R.string.amount) + ": "+ NumberFormat.getIntegerInstance().format(_loan) + " " + getString(R.string.currency));
                } else {
                    if (id == R.id.btn_back || id == R.id.btn_clear) {
                        loan.setText("");
                    }
                }
            } else if (status == Status.Term) {
                if (!currentInput.equals("")) {
                    term.setText(getString(R.string.term) + ": " +  _term + " " + getString(years));
                } else {
                    if (id == R.id.btn_back || id == R.id.btn_clear) {
                        term.setText("");
                    }
                }
            } else if (status == Status.Interest) {
                if (!currentInput.equals("")) {
                    interest.setText(getString(R.string.interest) + ": " + _interest + "%");
                } else {
                    if (id == R.id.btn_back || id == R.id.btn_clear) {
                        interest.setText("");
                    }
                }
            }
        }
        catch (Exception ex)
        {
          //  Log.e(TAG, "error ShowInfo" + ex.getMessage());
          //  Log.i(TAG, "status is" + status);
        }

    }

    private void Pressed(String digit) {
        if (CheckOverFlow(digit))
        {
            Warning();
            return;
        }
        try {
            currentInput += digit;
            int i = Integer.parseInt(currentInput);
            if (status == Status.Loan) {
                _loan = i;
            } else if (status == Status.Term) {
                _term = i;
            } else if (status == Status.Interest) {
                _interest = i;
            }
            display.setText(NumberFormat.getIntegerInstance().format(i));
        }
        catch (Exception ex)
        {
           // Log.e(TAG, "error Pressed" + ex.getMessage());
        }
    }

    private boolean CheckOverFlow(String digit)
    {
        String currentI = currentInput;
        currentI += digit;
        int i = Integer.parseInt(currentI);
        if (status == Status.Term) {
            if (i > 100)
            {
                return true;
            }
        } else
            if (status == Status.Interest) {
                if (i > 1000) {
                    return true;
                }
            }
        return false;
    }

    private boolean isDigitFull(String dig)
    {
        if (dig == null || dig == "")
        {
            return false;
        }
        if (dig.length() >= 9)
        {
            return true;
        }
        return false;
    }

    public String removeMethod(String str) {
        if (str != null && str.length() > 0 ) {
            //&& str.charAt(str.length() - 1) == 'x'
            str = str.substring(0, str.length() - 1);
        }
        else
        {
            if (str == null)
            {
             return "";
            }
        }
        return str;
    }


    //---------------------------------------GPS
    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_MODE);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void turnGPSOff(){
    //    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_MODE);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); //may be 2 for battary saving
            sendBroadcast(poke);
        }
    }

    private boolean canToggleGPS() {
        PackageManager pacman = getPackageManager();
        PackageInfo pacInfo = null;

        try {
            pacInfo = pacman.getPackageInfo("com.android.settings", PackageManager.GET_RECEIVERS);
        } catch (Exception e) {
            return false; //package not found
        }

        if(pacInfo != null){
            for(ActivityInfo actInfo : pacInfo.receivers){
                //test if recevier is exported. if so, we can toggle GPS.
                if(actInfo.name.equals("com.android.settings.widget.SettingsAppWidgetProvider") && actInfo.exported){
                    return true;
                }
            }
        }

        return false; //default
    }

    public void _turnGPSOn()
    {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        this.context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.context.sendBroadcast(poke);


        }
    }
    // automatic turn off the gps
    public void _turnGPSOff()
    {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.context.sendBroadcast(poke);
        }
    }

    /*/GPS
////ENABLE GPS:

Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
intent.putExtra("enabled", true);
sendBroadcast(intent);

///DISABLE GPS:

Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
intent.putExtra("enabled", false);
sendBroadcast(intent);

     */

}



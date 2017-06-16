package pw.lena.loancalculator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import static android.R.attr.id;
import static pw.lena.loancalculator.Prefs.getInterest;
import static pw.lena.loancalculator.Prefs.getTerm;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


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
      // LoadDefaultSavedData();
    }


    private boolean PreviousStep() {
        String mess = "Replace with your own action2";

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
            _term = getTerm(context);
            currentInput = _term.toString();
            display.setText(NumberFormat.getIntegerInstance().format(_term));
            this.setTitle(R.string.term_summary);
            status = Status.Term;
            startActivity(new Intent(this, ResultActivity.class));
        }

        if (snapView != null)
        {
            Snackbar.make(snapView, mess, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        return false;
    }

    private void NextStep(View view) {
        String mess = "Replace with your own action";
        if (currentInput == null || currentInput.equals(""))
        {
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
        if (status == Status.Loan)
        {
            SaveLoan();
            status = Status.Term;
            mess = getString(R.string.loanterm);
            currentInput = Prefs.getTerm(context).toString();
            display.setText(NumberFormat.getIntegerInstance().format(Prefs.getTerm(context)));
            this.setTitle(R.string.term_summary);
        }
        else {
            if (status == Status.Term) {
                currentInput = "";
                SaveTerm();
                status = Status.Interest;
                mess = getString(R.string.interest_desc);
                currentInput =  getInterest(context).toString();
                display.setText(NumberFormat.getIntegerInstance().format(Prefs.getInterest(context)));
                this.setTitle(R.string.interest_summary);
            } else if (status == Status.Interest) {
                SaveInterest();
                mess = getString(R.string.result);
                currentInput = "";
                display.setText(getString(R.string.zero));
                this.setTitle(R.string.result);
                status = Status.Loan;
                startActivity(new Intent(this, ResultActivity.class));
            }
        }

        Snackbar.make(view, mess, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void LoadDefaultSavedData() {
        try{
                //currentInput = Prefs.getLoan(this).toString();

                _loan = Prefs.getLoan(this);
                if (_loan == 0)
                {
                    loan.setText("");
                }
                else
                {
                    loan.setText("Loan: $" + NumberFormat.getIntegerInstance().format(_loan));
                }
            _term = getTerm(this);
            if (_term == 0)
            {
                term.setText("");
            }
            else
            {
                term.setText("Loan term in years: " + _term);
            }
            _interest = Prefs.getInterest(this);
            if (_interest == 0)
            {
                interest.setText("");
            }
            else
            {
                interest.setText("Interest rate:" + String.format("%.2f", _interest) + "%");
            }

            if (status == Status.Loan)
            {
                currentInput = _loan.toString();
                display.setText(NumberFormat.getIntegerInstance().format(_loan));
            }
            else
            if (status == Status.Term)
            {
                currentInput = _term.toString();
                display.setText(NumberFormat.getIntegerInstance().format(_term));
            }
            else
            if (status == Status.Interest)
            {
                currentInput = _interest.toString();
                display.setText(NumberFormat.getIntegerInstance().format(_interest));
            }
        }
        catch (Exception ex)
        {
         Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
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
        Prefs.setStatus(this, status.name());
    }

    private void SaveLoan() {

            try {
                Prefs.setLoan(this, _loan);
            }
            catch (Exception ex)
            {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG);
            }
    }

    private void SaveTerm() {
            try{
                Prefs.setTerm(this, _term);
            }
            catch (Exception ex)
            {
             Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG);
            }
    }

    private void SaveInterest() {
            try {
                Prefs.setInterest(this, _interest);
            }
            catch (Exception ex)
            {
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            StartPrefs();
//            return true;
       // }

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

        if (id == R.id.nav_schedule) {

            Toast.makeText(this, "schedule", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_chart) {

            Toast.makeText(this, "chart", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_diagram) {

            Toast.makeText(this, "diagram", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
            mVibrator.vibrate(
                    new long[]{0l, VIBRATE_MILLIS,
                            50l, VIBRATE_MILLIS,
                            50l, VIBRATE_MILLIS},
                    -1);
            Animation fade = AnimationUtils.loadAnimation(context,
                    R.anim.fade_in);
            mainLayer.startAnimation(fade);
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
                break;
            }
            case R.id.btn_back:
            {
                currentInput = removeMethod(currentInput);
                if (currentInput.equals(""))
                {
                    display.setText(R.string.zero);
                }
                else
                {
                    display.setText(currentInput);
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

    private void ShowInfo(int id) {
        if (status == Status.Loan)
        {
            if (!currentInput.equals(""))
            {
                loan.setText("Loan: $" + NumberFormat.getIntegerInstance().format(_loan));
            }
            else
            {
                if (id ==  R.id.btn_back || id == R.id.btn_clear)
                {
                    loan.setText("");
                }
            }
        }
        else
            if (status == Status.Term)
            {
                if (!currentInput.equals(""))
                {
                    term.setText("Loan term: " + _term + " years");
                }
                else
                {
                    if (id ==  R.id.btn_back || id == R.id.btn_clear)
                    {
                        term.setText("");
                    }
                }
            }
            else
            if (status == Status.Interest)
            {
                if (!currentInput.equals(""))
                {
                    interest.setText("Interest rate per year:" + _interest + "%");
                }
                else
                {
                    if (id ==  R.id.btn_back || id == R.id.btn_clear)
                    {
                        interest.setText("");
                    }
                }
            }

    }

    private void Pressed(String digit) {
        currentInput += digit;
        int i = Integer.parseInt(currentInput);
        if (status == Status.Loan)
        {
            _loan = i;
        }
        else
            if (status == Status.Term)
        {
            _term = i;
        }
        else
            if (status == Status.Interest)
            {
                _interest = i;
            }
        display.setText(NumberFormat.getIntegerInstance().format(i));
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
        return str;
    }


}



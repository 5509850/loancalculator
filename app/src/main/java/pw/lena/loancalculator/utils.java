package pw.lena.loancalculator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Aliaksandr_Harbunou on 7/25/2017.
 */

public class utils {

    public static int  haveNetworkConnectionType(Context context)
    {
        //return int
        //0 - no network
        //1 - only wifi
        //2 - only 3G
        int WIFI = 1;
        int GGG = 2;
        int type = 0;

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null)
        {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (netInfo.isConnected())
                {
                    haveConnectedWifi = true;
                    //Log.v("WIFI CONNECTION ", "AVAILABLE");
                    //Toast.makeText(this,"WIFI CONNECTION is Available", Toast.LENGTH_LONG).show();
                    type = WIFI;
                } else
                {
                    // Log.v("WIFI CONNECTION ", "NOT AVAILABLE");
                    //Toast.makeText(this,"WIFI CONNECTION is NOT AVAILABLE", Toast.LENGTH_LONG).show();
                }
            }
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
            {
                if (netInfo.isConnected())
                {
                    haveConnectedMobile = true;
                    // Log.v("MOBILE INTERNET CONNECTION ", "AVAILABLE");
                    //Toast.makeText(this,"MOBILE INTERNET CONNECTION - AVAILABLE", Toast.LENGTH_LONG).show();
                    type = GGG;
                } else
                {
                    // Log.v("MOBILE INTERNET CONNECTION ", "NOT AVAILABLE");
                    //Toast.makeText(this,"MOBILE INTERNET CONNECTION - NOT AVAILABLE", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (!haveConnectedWifi && !haveConnectedMobile)
            return 0;

        return type;
    }
}

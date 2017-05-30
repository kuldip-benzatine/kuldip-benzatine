package com.benzatine.lovestickerimages;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.benzatine.lovestickerimages.Edit_Image.showFullAdd;

/**
 * Created by ADMIN on 24-Apr-17.
 */

public class Utils {

    public static final String MAIN_URL = "http://benzatineinfotech.com/whatsapp_share/";
    public static final String FACEB_TWITTER = MAIN_URL + "love_stickers_android.php";
    public static final String WHATSAPP = MAIN_URL + "love_stickers_whatsapp_android.php";
    public static final String PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=com.benzatine.lovestickerimages";

    //-------------------Appliction Share------------------------
    public static final String App_Share_Facebook_Twitter = "https://tinyurl.com/lxwdny5";
    public static final String App_Share_Whatsapp = "https://tinyurl.com/lx57ffw";
    public static final String App_Share_ImageURL = "http://benzatineinfotech.com/whatsapp_share/img/1000000.jpg";


    public static Boolean isOnline(Context context) {
        boolean connected = false;
        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            connected = true;
        } else if (netInfo != null && netInfo.isConnected()
                && cm.getActiveNetworkInfo().isAvailable()) {
            connected = true;
        } else if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection urlc = (HttpURLConnection) url
                        .openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    connected = true;
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (cm != null) {
            final NetworkInfo[] netInfoAll = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfoAll) {
                System.out.println("get network type :::" + ni.getTypeName());
                if ((ni.getTypeName().equalsIgnoreCase("WIFI") || ni
                        .getTypeName().equalsIgnoreCase("MOBILE"))
                        && ni.isConnected() && ni.isAvailable()) {
                    connected = true;
                    if (connected) {
                        break;
                    }
                }
            }
        }
        return connected;
    }

    public static Integer selectSticker = 1;
    static Integer nextAddCount = 5;
    public static void CountSticker(){
        selectSticker++;
        if(selectSticker > nextAddCount){
            showFullAdd();
            nextAddCount += 5;
        }
    }
}

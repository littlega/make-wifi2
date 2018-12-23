package com.felhr.serialportexample;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Context mContext = this;
    /*
     * Notifications from UsbService will be received here.
     */
    int totalSize = 0;
    int ads = 0;


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("ผิดพลาด");
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready1", Toast.LENGTH_SHORT).show();
                    usb_connect=1;
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    app_adblock=1;
                    //setContentView(R.layout.usb_error);
                    usb_connect=0;
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    app_adblock=1;
                    //setContentView(R.layout.usb_error);
                    usb_connect=0;
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                    app_adblock=1;
                    //setContentView(R.layout.usb_error);
                    usb_connect=0;
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                    app_adblock=1;
                    //setContentView(R.layout.usb_error);
                    usb_connect=0;
                    break;
            }
        }
    };
    private UsbService usbService;
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
            if(app_timer==1){
                app_timer=0;
                usb_connect=1;
                Toast.makeText(mContext, "USB Ready2", Toast.LENGTH_SHORT).show();

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
            app_timer=1;
            //setContentView(R.layout.usb_error);
            usb_connect=0;
            Toast.makeText(mContext, "USB Not Ready", Toast.LENGTH_SHORT).show();
        }
    };
    public int app_version = 168;
    public int app_type = 1;
    public int board_offline = 0;
    public static final long DISCONNECT_TIMEOUT = 30000;
    public int DISCONNECT_TIMEOUT2 = 30000;
    public int water_fc = 0;
    public int topup_ft = 0;
    public int promotion_ft = 0;
    public int bill_ft = 0;
    public int card_ft = 0;
    public int pump_fc = 0;
    public int weight_fc = 0;
    public int topup_count = 0;
    public int nonet_setting = 0;

    public int water_start = 0;
    public int coin_status = 0;
    public int coin_chk = 0;
    public int water_level = 8;
    public int pump_level = 180;
    public int num_dot = 0;
    public int water_total_sec = 0;
    public int water_used_sec = 0;
    public int setting_count = 0;
    public int water_pump = 0;
    public int topup_menu = 0;
    public int app_lang = 0;
    public int app_oper = 0;
    public int app_api_false = 0;
    public int app_api_coin = 0;
    public String app_api_ref = "";
    public int usb_connect = 1;
    public int board_connect = 0;
    public int board_count = 0;
    public int board_count_status = 0;
    public int board_connect_sw = 0;
    public int board_timer = 0;
    public int app_adblock = 0;
    public int app_amt = 0;
    public int app_loop_timer = 1;
    public int app_remain = 0;
    public int app_total = 0;
    public double app_bill_amt_total = 0;
    public int app_loop = 0;
    public int app_idle = 0;
    public int app_timer = 0;
    public String  coin_data = "0";
    public String  weight_data = "0";
    public String top_url = "";
    public String transaction_id = "";
    public String app_uuid = "";
    public String app_card_oper = "";
    public String app_number = "";
    /// Bill
    public String bill_ref1 = "";
    public String bill_ref2 = "";
    public String bill_ref3 = "";
    public String bill_ref4 = "";
    public String bill_ref5 = "";
    public String bill_chk_ref1 = "";
    public String bill_chk_ref2 = "";
    public String bill_chk_ref3 = "";
    public String bill_chk_ref4 = "";
    public String bill_chk_ref5 = "";

    public String web_url = "file:///android_asset/menu3d_topup.html";
    public String storedPreferenceUsername = "";
    public String storedPreferenceToken = "";
    public String storedPreferenceSN = "";
    public String Onlinepass = "";

    public int bill_chk_ref = 0;
    public int bill_date = 0;
    public double bill_amt = 0;
    public double app_bill_total;
    public int ads1_type = 0;
    public int ads2_type = 0;
    public int ads3_type = 0;
    public int ads4_type = 0;
    public int ads5_type = 0;
    public int ads6_type = 0;
    public int ads7_type = 0;
    public int ads8_type = 0;
    public int ads1_time = 0;
    public int ads2_time = 0;
    public int ads3_time = 0;
    public int ads4_time = 0;
    public int ads5_time = 0;
    public int ads6_time = 0;
    public int ads7_time = 0;
    public int ads8_time = 0;
    public String ads1_img = "";
    public String ads2_img = "";
    public String ads3_img = "";
    public String ads4_img = "";
    public String ads5_img = "";
    public String ads6_img = "";
    public String ads7_img = "";
    public String ads8_img = "";
    public String ads1_sound = "";
    public String ads2_sound = "";
    public String ads3_sound = "";
    public String ads4_sound = "";
    public String ads5_sound = "";
    public String ads6_sound = "";
    public String ads7_sound = "";
    public String ads8_sound = "";
    public String ads1_vdo = "";
    public String ads2_vdo = "";
    public String ads3_vdo = "";
    public String ads4_vdo = "";
    public String ads5_vdo = "";
    public String ads6_vdo = "";
    public String ads7_vdo = "";
    public String ads8_vdo = "";
    public int wash1_price = 0;
    public int wash2_price = 0;
    public int wash3_price = 0;
    public int wash4_price = 0;
    public int wash5_price = 0;
    public int wash6_price = 0;
    public int wash7_price = 0;
    public int wash8_price = 0;
    public int wash9_price = 0;
    public int wash10_price = 0;
    public int app_ping_cmd = 0;
    public int wash_work = 0;
    public int wash_ft = 0;

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };




    VideoView myVideo;
    private MediaController media_control;
    public void coin_control(int data) {
        if(data==1) {
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":coin=1\n").getBytes());

            }
            sendMessage(":coin=1");
        }else if(data==2) {
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":coin=?\n").getBytes());

            }
            sendMessage(":coin=?");
        }else{
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":coin=0\n").getBytes());

            }
            sendMessage(":coin=0");
        }
    }

    public void pump_control(int data) {

        setContentView(R.layout.activity_main);
        if(data==1) {
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":pump=1\n").getBytes());
            }
            sendMessage(":pump=1");
        }else{
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":pump=0\n").getBytes());
            }
            sendMessage(":pump=0");
        }
        setContentView(R.layout.water_screen);
    }

    public void air_control(int data) {

        setContentView(R.layout.activity_main);
        if(data==1) {
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":air=1\n").getBytes());
            }
            sendMessage(":air=1");
        }else{
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":air=0\n").getBytes());
            }
            sendMessage(":air=0");
        }
        setContentView(R.layout.airpump);
    }
    public void light_control(int data) {
        if(data==1) {
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":light=1\n").getBytes());
            }
            sendMessage(":light=1");
        }else if(data==2) {
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":light=2\n").getBytes());
            }
            sendMessage(":light=2");
        }else{
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":light=0\n").getBytes());
            }
            sendMessage(":light=0");
        }
    }
    public void board_watchdog(int data) {
        if(data==1) {
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":help\n").getBytes());
            }
            //sendMessage(":help");
        }else{
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write((":help\n").getBytes());
            }
            //sendMessage(":help");
        }
    }
    int ads_show_type;
    String ads_show_img;
    String ads_show_sound;
    String ads_show_vdo;
    int ads_show_time;
    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect




            totalSize = totalSize+1;
            Log.d("chk_oper", "ADS Start! "+totalSize);
            if(totalSize==15){

                totalSize=7;
            }
            if(totalSize<6){
                //totalSize=7;
            }
            if(totalSize>6) {
                ads = 1;
                if((totalSize - 6)==1) {
                    ads_show_type = ads1_type;
                    ads_show_img = ads1_img;
                    ads_show_sound = ads1_sound;
                    ads_show_vdo = ads1_vdo;
                    DISCONNECT_TIMEOUT2 = ads1_time*1000;
                }else if((totalSize - 6)==2) {
                    ads_show_type = ads2_type;
                    ads_show_img = ads2_img;
                    ads_show_sound = ads2_sound;
                    ads_show_vdo = ads2_vdo;
                    DISCONNECT_TIMEOUT2 = ads2_time*1000;
                }else if((totalSize - 6)==3) {
                    ads_show_type = ads3_type;
                    ads_show_img = ads3_img;
                    ads_show_sound = ads3_sound;
                    ads_show_vdo = ads3_vdo;
                    DISCONNECT_TIMEOUT2 = ads3_time*1000;
                }else if((totalSize - 6)==4) {
                    ads_show_type = ads4_type;
                    ads_show_img = ads4_img;
                    ads_show_sound = ads4_sound;
                    ads_show_vdo = ads4_vdo;
                    DISCONNECT_TIMEOUT2 = ads4_time*1000;
                }else if((totalSize - 6)==5) {
                    ads_show_type = ads5_type;
                    ads_show_img = ads5_img;
                    ads_show_sound = ads5_sound;
                    ads_show_vdo = ads5_vdo;
                    DISCONNECT_TIMEOUT2 = ads5_time*1000;
                }else if((totalSize - 6)==6) {
                    ads_show_type = ads6_type;
                    ads_show_img = ads6_img;
                    ads_show_sound = ads6_sound;
                    ads_show_vdo = ads6_vdo;
                    DISCONNECT_TIMEOUT2 = ads6_time*1000;
                }else if((totalSize - 6)==7) {
                    ads_show_type = ads7_type;
                    ads_show_img = ads7_img;
                    ads_show_sound = ads7_sound;
                    ads_show_vdo = ads7_vdo;
                    DISCONNECT_TIMEOUT2 = ads7_time*1000;
                }else if((totalSize - 6)==8) {
                    ads_show_type = ads8_type;
                    ads_show_img = ads8_img;
                    ads_show_sound = ads8_sound;
                    ads_show_vdo = ads8_vdo;
                    DISCONNECT_TIMEOUT2 = ads8_time*1000;
                }
                if (app_adblock == 0) {

                    if (ads_show_type == 1) {
                        String adsPathDir = "/data/data/com.felhr.serialportexample/files/images/" + ads_show_img;

                        File myDir = new File(adsPathDir);
                        if (myDir.exists()) {
                            setContentView(R.layout.ads_img_screen);
                            ImageView image = (ImageView) findViewById(R.id.ImageView);
                            image.setImageURI(Uri.parse(adsPathDir));
                        }
                    } else if (ads_show_type == 2) {
                        String adsPathDir = "/data/data/com.felhr.serialportexample/files/images/" + ads_show_img;
                        File myDir = new File(adsPathDir);
                        if (myDir.exists()) {
                            setContentView(R.layout.ads_img_screen);
                            ImageView image = (ImageView) findViewById(R.id.ImageView);
                            image.setImageURI(Uri.parse(adsPathDir));
                            String adsSound = "/data/data/com.felhr.serialportexample/files/sound/" + ads_show_sound;
                            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(adsSound));
                            mPlayer.start();
                        }

                    } else if (ads_show_type == 3) {
                        String adsPathDir = "/data/data/com.felhr.serialportexample/files/video/" + ads_show_vdo;
                        File myDir = new File(adsPathDir);
                        if (myDir.exists()) {
                            setContentView(R.layout.ads_screen);
                            VideoView videoview = (VideoView) findViewById(R.id.VideoView);
                            videoview.setVideoURI(Uri.parse(adsPathDir));
                            videoview.start();
                        }
                    } else if(ads1_type==0) {
                        DISCONNECT_TIMEOUT2 = 30000;
                        int checkExistence = mContext.getResources().getIdentifier("ads" + (totalSize - 6), "raw", mContext.getPackageName());

                        if (checkExistence == 0) {

                            totalSize = 7;
                        }
                        int rawId = getResources().getIdentifier("ads" + (totalSize - 6), "raw", getPackageName());

                        TypedValue value = new TypedValue();
                        getResources().getValue(rawId, value, true);
                        String resname = value.string.toString().substring(13, value.string.toString().length());
                        if (app_adblock == 0) {
                            //Toast.makeText(mContext,"ADS"+resname,Toast.LENGTH_LONG).show();
                            if (resname.indexOf("mp4") >= 0) {
                                setContentView(R.layout.ads_screen);

                                VideoView videoview = (VideoView) findViewById(R.id.VideoView);
                                videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/raw/ads" + (totalSize - 6)));
                                videoview.start();
                                light_control(2);
                            } else {
                                setContentView(R.layout.ads_img_screen);
                                ImageView image = (ImageView) findViewById(R.id.ImageView);
                                image.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/raw/ads" + (totalSize - 6)));
                                light_control(2);

                            }
                        }
                    }else{
                        DISCONNECT_TIMEOUT2 = 1000;
                        totalSize = 6;
                    }
                }
            }
            resetDisconnectTimer();
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT2);
        //ads=0;
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        //Log.d("chk_oper", "onUserInteraction! "+ads);
        if(ads==1) {
            app_startup();
        }
        resetDisconnectTimer();
    }

    int amt_left=0;
    int sec_left=0;

    private String sendPing(String longitude,String latitude){
        //String link="https://member.welovetopupline.com/api/get_web.php?cmd=ping&uuid="+app_uuid+"&lat="+latitude+"&long="+longitude+"&status="+usb_connect;
        String status_detail = "B:"+usb_connect+"("+app_version+")";
        String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=command","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN+"&data1="+status_detail);
        return "".toString();
    }
    public static final int BUFFER_SIZE = 2048;

    private void sendMessage(final String msg) {
        //Log.d("chk_oper", "Step 1 : ");
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.
                    Socket s = new Socket("192.168.43.100", 8000);

                    OutputStream out = s.getOutputStream();

                    PrintWriter output = new PrintWriter(out);

                    output.println(msg);
                    output.flush();
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    //String st = input.readLine();
                    String message = "";
                    int charsRead = 0;
                    char[] buffer = new char[BUFFER_SIZE];

                    while ((charsRead = input.read(buffer)) != -1) {
                        message += new String(buffer).substring(0, charsRead);
                    }


                    Log.d("chk_oper", "Data1 : "+message);
                    String data1 = message.toString();
                    if (data1.trim().length() != 0) {
                        post_data(data1);
                    }
                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    private void sendMessage2(final String msg) {
        //Log.d("chk_oper", "Step 1 : ");
        sendMessage(msg);
        //sendMessage3(msg);
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.
                    Socket s = new Socket("192.168.43.101", 8000);

                    OutputStream out = s.getOutputStream();

                    PrintWriter output = new PrintWriter(out);

                    output.println(msg);
                    output.flush();
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    //String st = input.readLine();
                    String message = "";
                    int charsRead = 0;
                    char[] buffer = new char[BUFFER_SIZE];

                    while ((charsRead = input.read(buffer)) != -1) {
                        message += new String(buffer).substring(0, charsRead);
                    }


                    Log.d("chk_oper", "Data1 : "+message);
                    String data1 = message.toString();
                    if (data1.trim().length() != 0) {
                        post_data(data1);
                    }
                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    private void sendMessage3(final String msg) {
        //Log.d("chk_oper", "Step 1 : ");
        sendMessage(msg);
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.
                    Socket s = new Socket("192.168.43.102", 8000);

                    OutputStream out = s.getOutputStream();

                    PrintWriter output = new PrintWriter(out);

                    output.println(msg);
                    output.flush();
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    //String st = input.readLine();
                    String message = "";
                    int charsRead = 0;
                    char[] buffer = new char[BUFFER_SIZE];

                    while ((charsRead = input.read(buffer)) != -1) {
                        message += new String(buffer).substring(0, charsRead);
                    }


                    Log.d("chk_oper", "Data1 : "+message);
                    String data1 = message.toString();
                    if (data1.trim().length() != 0) {
                        post_data(data1);
                    }
                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    private void sendMessage4(final String msg) {
        //Log.d("chk_oper", "Step 1 : ");
        sendMessage(msg);
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.
                    Socket s = new Socket("192.168.43.103", 8000);

                    OutputStream out = s.getOutputStream();

                    PrintWriter output = new PrintWriter(out);

                    output.println(msg);
                    output.flush();
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    //String st = input.readLine();
                    String message = "";
                    int charsRead = 0;
                    char[] buffer = new char[BUFFER_SIZE];

                    while ((charsRead = input.read(buffer)) != -1) {
                        message += new String(buffer).substring(0, charsRead);
                    }


                    Log.d("chk_oper", "Data1 : "+message);
                    String data1 = message.toString();
                    if (data1.trim().length() != 0) {
                        post_data(data1);
                    }
                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private boolean pingCommand(){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("ping -c 1 192.168.43.100");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }
    private boolean pingCommand2(){
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("ping -c 1 8.8.8.8");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue "+mExitValue);
            if(mExitValue==0){
                return true;
            }else{
                return false;
            }
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }

    private String getTopupFromUrl(String cmd,int amt,String num){

        String link="https://member.welovetopupline.com/api/get_web.php?cmd="+cmd+"&uuid="+app_uuid+"&amt="+amt+"&num="+num;
        ArrayList<String> al=new ArrayList<>();
        try{
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    al.add(line);
                }
            } finally {
                br.close();
                return al.get(0).toString();
            }
        }catch (IOException e){
            e.printStackTrace();
            setContentView(R.layout.main_screen);

            if(cmd.equals("pic")){
                return "menu_topup_12call".toString();
            }else if(cmd.equals("get_promotion_amt")){
                return "0-0".toString();
            }else {
                return "0".toString();
            }
        }
    }
    private String getDataFromUrl(String link,String data ) {
        if(isInternetAvailable()) {
            ArrayList<String> al = new ArrayList<>();
            try {
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                conn.connect();
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        al.add(line);
                    }
                } finally {
                    br.close();
                    return al.get(0).toString();
                }
            } catch (IOException e) {
                return "0";
            }
        }else{
            setContentView(R.layout.main_screen);
            return "0";
        }
    }
    private String sentDataFromUrl(String link,String data ) {
        if (isInternetAvailable()) {
            ArrayList<String> al = new ArrayList<>();
            try {
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                conn.connect();
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        al.add(line);
                    }
                } finally {
                    br.close();
                    //setContentView(R.layout.main_screen);
                    //app_api_false = 1;
                    return al.get(0).toString();
                }
            } catch (IOException e) {
                setContentView(R.layout.main_screen);
                app_api_false = 1;
                return "";
            }
        } else {
            Toast.makeText(mContext, "Offline", Toast.LENGTH_SHORT).show();
            app_api_false = 1;
            return "";
        }

    }

    private String getADSFromURL(String downloadUrl,String downloadType){
        try {
            URL url = new URL(downloadUrl);//Create Download URl
            HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
            c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
            c.connect();//connect the URL Connection

            String adsPathDir = "/data/data/com.felhr.serialportexample/files/"+downloadType;
            File myDir = new File(adsPathDir);
            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            String mainUrl = "https://www.totpayonline.com/system/upload/ads/";
            String downloadFileName = downloadUrl.replace(mainUrl, "");
            File outputFile = new File(myDir, downloadFileName);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();//Get InputStream for connection

            byte[] buffer = new byte[1024];//Set buffer type
            int len1 = 0;//init length
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);//Write new file
            }

            //Close all connection after doing task
            fos.close();
            is.close();
            Toast.makeText(mContext,"Download done.",Toast.LENGTH_LONG).show();
        } catch (Exception e) {

            //Read exception if something went wrong
            e.printStackTrace();
        }
        return "";
    }
    private String getMachineFromUrl(){
        String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=machine","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        String[] separated = jsonString.split(",");
        if(separated[0].contains("1")){
            if(separated[6].contains("open")){
                String[] separated2 = jsonString.split("passwords");
                String[] separated3 = separated2[1].split("\"");
                Onlinepass=separated3[2];
                if (TextUtils.isEmpty(Onlinepass)) {
                    Onlinepass = "abcd";
                }
                ImageView Menu1 = (ImageView)findViewById(R.id.Menu1);
                ImageView Menu2 = (ImageView)findViewById(R.id.Menu2);
                ImageView Menu3 = (ImageView)findViewById(R.id.Menu3);
                ImageView Menu4 = (ImageView)findViewById(R.id.Menu4);
                ImageView Menu5 = (ImageView)findViewById(R.id.Menu5);
                ImageView Menu6 = (ImageView)findViewById(R.id.Menu6);
                Imageview_Status(0,Menu2);
                //Imageview_Status(0,Menu3);
                //Imageview_Status(0,Menu4);
                Imageview_Status(0,Menu5);
                Imageview_Status(0,Menu6);

                String[] separated4 = jsonString.split("hardware_id");
                String[] separated5 = separated4[1].split("\"");
                if(pingCommand()){
                    return "ON"+separated5[2];
                }else{
                    //setContentView(R.layout.usb_error);
                    return "OFF"+separated5[2];
                }
            }else{
                setContentView(R.layout.main_screen);
                return "ล็อค";
            }

        }else if(separated[0].contains("0")){
            if (TextUtils.isEmpty(Onlinepass)) {
                Onlinepass = "abcd";
            }
            ImageView Menu1 = (ImageView)findViewById(R.id.Menu1);
            ImageView Menu2 = (ImageView)findViewById(R.id.Menu2);
            ImageView Menu3 = (ImageView)findViewById(R.id.Menu3);
            ImageView Menu4 = (ImageView)findViewById(R.id.Menu4);
            ImageView Menu5 = (ImageView)findViewById(R.id.Menu5);
            ImageView Menu6 = (ImageView)findViewById(R.id.Menu6);
            Imageview_Status(0,Menu1);
            Imageview_Status(0,Menu2);
            Imageview_Status(0,Menu3);
            Imageview_Status(0,Menu4);
            Imageview_Status(0,Menu5);
            Imageview_Status(0,Menu6);
            return "รหัสไม่ถูกต้อง";
        }else{
            if (TextUtils.isEmpty(Onlinepass)) {
                Onlinepass = "abcd";
            }
            return "OFFLINE";
        }
    }


    protected void app_startup() {
        app_loop_timer=0;
        handler_loop.removeCallbacks(t);
        t.interrupt();
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        //mContext.shutdown();
        startActivity(i);
        //Intent i = new Intent(this, MainActivity.class);
        //finish();  //Kill the activity from which you will go to next activity
        //startActivity(i);
        //super.onRestart();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_main);
        final Context context = this.getApplicationContext();

        hideSystemUI();//#new


        mHandler = new MyHandler(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        storedPreferenceUsername = prefs.getString("topupepay_user" , "");
        storedPreferenceToken = prefs.getString("topupepay_token" , "");
        storedPreferenceSN = prefs.getString("topupepay_sn" , "");
        app_type = prefs.getInt("topupepay_apptype", 0);

        water_level = prefs.getInt("topupepay_waterlevel", 0);
        app_adblock=prefs.getInt("topupepay_adsblock", 0);
        ads1_type = prefs.getInt("topupepay_ads1_type", 0);
        ads2_type = prefs.getInt("topupepay_ads2_type", 0);
        ads3_type = prefs.getInt("topupepay_ads3_type", 0);
        ads4_type = prefs.getInt("topupepay_ads4_type", 0);
        ads5_type = prefs.getInt("topupepay_ads5_type", 0);
        ads6_type = prefs.getInt("topupepay_ads6_type", 0);
        ads7_type = prefs.getInt("topupepay_ads7_type", 0);
        ads8_type = prefs.getInt("topupepay_ads8_type", 0);
        ads1_time = prefs.getInt("topupepay_ads1_time", 0);
        ads2_time = prefs.getInt("topupepay_ads2_time", 0);
        ads3_time = prefs.getInt("topupepay_ads3_time", 0);
        ads4_time = prefs.getInt("topupepay_ads4_time", 0);
        ads5_time = prefs.getInt("topupepay_ads5_time", 0);
        ads6_time = prefs.getInt("topupepay_ads6_time", 0);
        ads7_time = prefs.getInt("topupepay_ads7_time", 0);
        ads8_time = prefs.getInt("topupepay_ads8_time", 0);
        wash1_price = prefs.getInt("topupepay_washl", 0);
        wash2_price = prefs.getInt("topupepay_wash2", 0);
        wash3_price = prefs.getInt("topupepay_wash3", 0);
        wash4_price = prefs.getInt("topupepay_wash4", 0);
        wash5_price = prefs.getInt("topupepay_wash5", 0);
        wash6_price = prefs.getInt("topupepay_wash6", 0);
        wash7_price = prefs.getInt("topupepay_wash7", 0);
        wash8_price = prefs.getInt("topupepay_wash8", 0);
        wash9_price = prefs.getInt("topupepay_wash9", 0);
        wash10_price = prefs.getInt("topupepay_wash10", 0);
        pump_level = prefs.getInt("topupepay_airpump", 0);
        app_ping_cmd = prefs.getInt("topupepay_reboot", 0);

        ads1_img = prefs.getString("topupepay_ads1_img" , "");
        ads2_img = prefs.getString("topupepay_ads2_img" , "");
        ads3_img = prefs.getString("topupepay_ads3_img" , "");
        ads4_img = prefs.getString("topupepay_ads4_img" , "");
        ads5_img = prefs.getString("topupepay_ads5_img" , "");
        ads6_img = prefs.getString("topupepay_ads6_img" , "");
        ads7_img = prefs.getString("topupepay_ads7_img" , "");
        ads8_img = prefs.getString("topupepay_ads8_img" , "");
        ads1_sound = prefs.getString("topupepay_ads1_sound" , "");
        ads2_sound = prefs.getString("topupepay_ads2_sound" , "");
        ads3_sound = prefs.getString("topupepay_ads3_sound" , "");
        ads4_sound = prefs.getString("topupepay_ads4_sound" , "");
        ads5_sound = prefs.getString("topupepay_ads5_sound" , "");
        ads6_sound = prefs.getString("topupepay_ads6_sound" , "");
        ads7_sound = prefs.getString("topupepay_ads7_sound" , "");
        ads8_sound = prefs.getString("topupepay_ads8_sound" , "");
        ads1_vdo = prefs.getString("topupepay_ads1_vdo" , "");
        ads2_vdo = prefs.getString("topupepay_ads2_vdo" , "");
        ads3_vdo = prefs.getString("topupepay_ads3_vdo" , "");
        ads4_vdo = prefs.getString("topupepay_ads4_vdo" , "");
        ads5_vdo = prefs.getString("topupepay_ads5_vdo" , "");
        ads6_vdo = prefs.getString("topupepay_ads6_vdo" , "");
        ads7_vdo = prefs.getString("topupepay_ads7_vdo" , "");
        ads8_vdo = prefs.getString("topupepay_ads8_vdo" , "");

        if(isPermissionGranted1()) {
            if(isPermissionGranted2()) {

                if(app_type==0){
                    app_type =1;
                }
                Log.d("chk_oper", "Data1 : " + ads2_vdo + " Data2 : " + ads2_type);
                coin_status=0;
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                light_control(1);
                resetDisconnectTimer();

                coin_control(0);

                app_idle=0;


                if(app_type==1){
                    setContentView(R.layout.main_topup_tot);
                    light_control(1);
                    TextView myAwesomeTextView = (TextView)findViewById(R.id.textView);
                    myAwesomeTextView.setText(getMachineFromUrl());
                }else {
                    WebView myWebView = (WebView) findViewById(R.id.webView);
                    myWebView.setWebViewClient(new MyWebViewClient());
                    WebSettings webSettings = myWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);

                    //myWebView.loadUrl("file:///android_asset/menu3d.html");
                    if(app_type==2) {
                        myWebView.loadUrl("file:///android_asset/menu3d_topup.html");
                    }else{
                        //myWebView.loadUrl("file:///android_asset/menu3d_topup_wash_dwater.html");
                        //myWebView.loadUrl("file:///android_asset/menu3d_topup_wash.html");
                        myWebView.loadUrl("file:///android_asset/menu3d.html");
                    }
                    myWebView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return (event.getAction() == MotionEvent.ACTION_MOVE);
                        }
                    });
                    myWebView.setLongClickable(true);
                    myWebView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return true;
                        }
                    });
                    myWebView.setWebChromeClient(new WebChromeClient() {
                        public void onProgressChanged(WebView view, int progress) {
                            MainActivity.this.setTitle("Loading...");
                            MainActivity.this.setProgress(progress * 100);

                            if (progress == 100)
                                MainActivity.this.setTitle(R.string.app_name);
                        }
                    });

                }

                try {
                    //Runtime.getRuntime().exec("su");
                }catch (Exception e){

                }

            }
        }

    }

    //-------------------------------------------

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }





    private Thread t;
    private Handler handler_loop = new Handler();

    @Override
    protected void onStart() {
        super.onStart();

        t = new Thread() {

            @Override
            public void run() {
                if (app_loop_timer == 1) {
                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update TextView here!

                                    /// AD Block///
                                    if (app_idle > 0) {
                                        app_idle++;
                                        if (app_idle == 600) {
                                            app_startup();
                                            app_idle = 0;
                                            coin_control(0);
                                            light_control(0);
                                        }
                                    }

                                    if (app_adblock == 1) {
                                        resetDisconnectTimer();
                                    }
                                    /// PING SERVICE ///
                                    app_loop++;
                                    if (app_loop == 1) {

                                        //Log.v("TAG","Ping Send");

                                        sendPing("0", "0");
                                    } else if (app_loop > 180) {
                                        app_loop = 0;
                                    }
                                    Log.v("TAG", "Ping : " + app_loop);
                                    /// Watch Dog ///
                                    //board_timer++;
                                    //if(board_timer==1){
                                    //    board_watchdog(1);
                                    //}
                                    //if(board_timer>60){
                                    //    board_timer=0;
                                    //}
                                    board_count++;
                                    if (board_count > 90) {
                                        board_count_status = 0;
                                    }
                                    board_timer++;

                                    if (board_timer > 60) {
                                        board_timer = 0;
                                        if(app_ping_cmd==1) {
                                            if (pingCommand()) {
                                                board_offline = 0;
                                                usb_connect = 1;
                                                Log.d("chk_ping", "Online " + board_offline);
                                            } else {
                                                board_offline++;
                                                usb_connect = 0;
                                                Log.d("chk_ping", "Offline " + board_offline);
                                                if (board_offline > 1) {
                                                    try {
                                                        //Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"}).waitFor();
                                                    } catch (Throwable e) {

                                                    }
                                                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                                                    pm.reboot(null);

                                                }
                                            }
                                        }
                                        if (app_api_false == 1) {
                                            String jsonString = sentDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=mobiletopup2", "username=" + storedPreferenceUsername + "&token=" + storedPreferenceToken + "&sn=" + storedPreferenceSN + "&transaction_id=" + app_api_ref + "&coinbanknote=" + app_api_coin);
                                            app_api_false = 0;
                                            Toast.makeText(mContext, "Topup done.", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    if (water_fc == 1) {

                                        /// Water Function
                                        if (coin_status == 0) {
                                            coin_control(1);
                                            coin_status = 1;
                                            if (water_level == 0) {
                                                water_level = 13;
                                            }
                                            app_idle = 1;
                                        }
                                        if (water_start == 2) {
                                            pump_control(0);
                                            water_start = 0;
                                        }
                                        int coin_data_int = Integer.parseInt(coin_data);
                                        water_total_sec = coin_data_int * water_level;
                                        if (water_start == 1) {
                                            app_idle = 1;
                                            if (water_used_sec < water_total_sec) {
                                                if (water_pump == 0) {
                                                    pump_control(1);
                                                    water_pump = 1;
                                                }
                                                water_used_sec++;
                                            } else {
                                                if (water_pump == 1) {
                                                    if (usbService != null) { // if UsbService was correctly binded, Send data
                                                        pump_control(0);
                                                        water_start = 0;
                                                        water_pump = 0;
                                                        water_used_sec = water_total_sec;
                                                    }
                                                }
                                            }
                                        }
                                        if (coin_status == 1) {
                                            coin_control(2);
                                        }

                                        sec_left = water_total_sec - water_used_sec;
                                        amt_left = sec_left / water_level;
                                        if (amt_left < 0) {
                                            amt_left = 0;
                                        }
                                        if (sec_left < 0) {
                                            sec_left = 0;
                                        }
                                        Button p1_button = (Button) findViewById(R.id.btn_Money);
                                        p1_button.setText(String.valueOf(amt_left));
                                        Button p2_button = (Button) findViewById(R.id.btn_Money2);
                                        p2_button.setText(String.valueOf(sec_left));
                                        resetDisconnectTimer();

                                    }
                                    // END Run!
                                    if (weight_fc == 1) {
                                        if (coin_status == 1) {
                                            coin_control(2);
                                        }
                                        int coin_data_int = Integer.parseInt(coin_data);
                                        if(coin_data_int>0){
                                            coin_status = 0;
                                            sendMessage(":weight=?");
                                            TextView p2_button = (TextView) findViewById(R.id.txtWeight);
                                            p2_button.setText(weight_data);
                                            resetDisconnectTimer();
                                        }

                                    }
                                    // END Run!
                                    if (pump_fc == 1) {

                                        /// Water Function
                                        if (coin_status == 0) {
                                            coin_control(1);
                                            coin_status = 1;
                                            if (pump_level == 0) {
                                                pump_level = 180;
                                            }
                                            app_idle = 1;
                                        }
                                        if (water_start == 2) {
                                            air_control(0);
                                            water_start = 0;
                                        }
                                        int coin_data_int = Integer.parseInt(coin_data);
                                        water_total_sec = coin_data_int * pump_level;
                                        if (water_start == 1) {
                                            app_idle = 1;
                                            if (water_used_sec < water_total_sec) {
                                                if (water_pump == 0) {
                                                    air_control(1);
                                                    water_pump = 1;
                                                }
                                                water_used_sec++;
                                            } else {
                                                if (water_pump == 1) {
                                                    if (usbService != null) { // if UsbService was correctly binded, Send data
                                                        air_control(0);
                                                        water_start = 0;
                                                        water_pump = 0;
                                                        water_used_sec = water_total_sec;
                                                    }
                                                }
                                            }
                                        }
                                        if (coin_status == 1) {
                                            coin_control(2);
                                        }

                                        sec_left = water_total_sec - water_used_sec;
                                        amt_left = sec_left / pump_level;
                                        if (amt_left < 0) {
                                            amt_left = 0;
                                        }
                                        if (sec_left < 0) {
                                            sec_left = 0;
                                        }
                                        Button p2_button = (Button) findViewById(R.id.btn_Money2);
                                        p2_button.setText(String.valueOf(sec_left));
                                        resetDisconnectTimer();
                                    }
                                    // END Run!
                                    // Wash Function
                                    if (wash_ft == 1) {
                                        if (coin_status == 0) {
                                            coin_control(1);
                                            coin_status = 1;
                                        }
                                        if (coin_status == 1) {
                                            coin_control(2);
                                        }
                                        int coin_data_int = Integer.parseInt(coin_data);
                                        resetDisconnectTimer();
                                        EditText txtInsert = (EditText) findViewById(R.id.txtInsert);
                                        txtInsert.setText(String.valueOf(coin_data_int));
                                        EditText txtLeft = (EditText) findViewById(R.id.txtLeft);
                                        int amt_left = app_total - app_remain - coin_data_int;
                                        if (amt_left <= 0) {
                                            topup_count++;
                                            if (topup_count == 4) {
                                                app_api_false = 0;
                                                wash_ft = 0;


                                                if(wash_work==1){
                                                    sendMessage2(":wash_A=1");
                                                }else if(wash_work==2){
                                                    sendMessage2(":wash_B=1");
                                                }else if(wash_work==3){
                                                    sendMessage2(":wash_C=1");
                                                }else if(wash_work==4){
                                                    sendMessage3(":wash_A=1");
                                                }else if(wash_work==5){
                                                    sendMessage3(":wash_B=1");
                                                }else if(wash_work==6){
                                                    sendMessage3(":wash_C=1");
                                                }else if(wash_work==7){
                                                    sendMessage4(":wash_A=1");
                                                }else if(wash_work==8){
                                                    sendMessage4(":wash_B=1");
                                                }
                                                String machine_id = String.valueOf(wash_work);
                                                String jsonString2 = getDataFromUrl("https://b.welovetopup.com/api/v1/services/vendingcredit","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN+"&service_id=123&coinbanknote="+coin_data);


                                                String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php","act=1&mc="+machine_id+"&uuid="+storedPreferenceSN+"&num="+app_number);
                                                setContentView(R.layout.topup_success);
                                                if (app_lang == 1) {
                                                    MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_finish);
                                                    mPlayer.start();
                                                } else {
                                                    MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.finish);
                                                    mPlayer.start();
                                                }
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {setContentView(R.layout.main_topup_tot);
                                                        TextView myAwesomeTextView = (TextView) findViewById(R.id.textView);
                                                        myAwesomeTextView.setText(getMachineFromUrl());
                                                    }
                                                }, 5000);

                                                coin_control(0);
                                                coin_status = 0;

                                            }
                                            amt_left = 0;
                                        } else {
                                            topup_count = 0;
                                        }
                                        txtLeft.setText(String.valueOf(amt_left));

                                    }
                                    // Topup Function
                                    if (topup_ft == 1) {
                                        if (coin_status == 0) {
                                            coin_control(1);
                                            coin_status = 1;
                                        }
                                        if (coin_status == 1) {
                                            coin_control(2);
                                        }
                                        int coin_data_int = Integer.parseInt(coin_data);
                                        resetDisconnectTimer();
                                        EditText txtInsert = (EditText) findViewById(R.id.txtInsert);
                                        txtInsert.setText(String.valueOf(coin_data_int));
                                        EditText txtLeft = (EditText) findViewById(R.id.txtLeft);
                                        int amt_left = app_total - app_remain - coin_data_int;
                                        if (amt_left <= 0) {
                                            topup_count++;

                                            if (topup_count == 4) {
                                                app_api_false = 0;
                                                String jsonString = sentDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=mobiletopup2", "username=" + storedPreferenceUsername + "&token=" + storedPreferenceToken + "&sn=" + storedPreferenceSN + "&transaction_id=" + transaction_id + "&coinbanknote=" + coin_data_int);
                                                app_api_coin = coin_data_int;
                                                app_api_ref = transaction_id;
                                                int topup_status = (coin_data_int + app_remain) - app_total;

                                                if (topup_status == 0) {


                                                    setContentView(R.layout.topup_success);
                                                    if (app_lang == 1) {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_finish);
                                                        mPlayer.start();
                                                    } else {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.finish);
                                                        mPlayer.start();
                                                    }
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            setContentView(R.layout.main_topup_tot);
                                                            TextView myAwesomeTextView = (TextView) findViewById(R.id.textView);
                                                            myAwesomeTextView.setText(getMachineFromUrl());

                                                        }
                                                    }, 5000);
                                                } else {
                                                    setContentView(R.layout.topup_success_save_credit);
                                                    if (app_lang == 1) {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_finish);
                                                        mPlayer.start();
                                                    } else {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.finish);
                                                        mPlayer.start();
                                                    }

                                                    EditText textNum = (EditText) findViewById(R.id.credit_left);
                                                    textNum.setText(String.valueOf(topup_status));
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            setContentView(R.layout.main_topup_tot);
                                                            TextView myAwesomeTextView = (TextView) findViewById(R.id.textView);
                                                            myAwesomeTextView.setText(getMachineFromUrl());

                                                        }
                                                    }, 5000);
                                                }
                                                coin_control(0);
                                                coin_status = 0;
                                                topup_ft = 0;
                                            }
                                            amt_left = 0;
                                        } else {
                                            topup_count = 0;
                                        }
                                        txtLeft.setText(String.valueOf(amt_left));

                                    }
                                    // Card Function
                                    if (card_ft == 1) {
                                        if (coin_status == 0) {
                                            coin_control(1);
                                            coin_status = 1;
                                        }
                                        if (coin_status == 1) {
                                            coin_control(2);
                                        }
                                        int coin_data_int = Integer.parseInt(coin_data);
                                        resetDisconnectTimer();
                                        EditText txtInsert = (EditText) findViewById(R.id.txtInsert);
                                        txtInsert.setText(String.valueOf(coin_data_int));
                                        EditText txtLeft = (EditText) findViewById(R.id.txtLeft);
                                        int amt_left = app_total - app_remain - coin_data_int;
                                        if (amt_left <= 0) {
                                            topup_count++;
                                            if (topup_count == 4) {
                                                String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=topupcard2", "username=" + storedPreferenceUsername + "&token=" + storedPreferenceToken + "&sn=" + storedPreferenceSN + "&transaction_id=" + transaction_id + "&coinbanknote=" + coin_data_int);
                                                int topup_status = (coin_data_int + app_remain) - app_total;
                                                if (topup_status == 0) {
                                                    setContentView(R.layout.topup_success);
                                                    if (app_lang == 1) {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_finish);
                                                        mPlayer.start();
                                                    } else {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.finish);
                                                        mPlayer.start();
                                                    }
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            app_startup();
                                                        }
                                                    }, 5000);
                                                } else {
                                                    setContentView(R.layout.topup_success_save_credit);
                                                    if (app_lang == 1) {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_finish);
                                                        mPlayer.start();
                                                    } else {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.finish);
                                                        mPlayer.start();
                                                    }
                                                    EditText textNum = (EditText) findViewById(R.id.credit_left);
                                                    textNum.setText(String.valueOf(topup_status));
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            app_startup();
                                                        }
                                                    }, 5000);
                                                }
                                                coin_control(0);
                                                coin_status = 0;
                                                card_ft = 0;
                                            }
                                            amt_left = 0;
                                        } else {
                                            topup_count = 0;
                                        }
                                        txtLeft.setText(String.valueOf(amt_left));
                                    }
                                    // Promotion Function
                                    if (promotion_ft == 1) {
                                        if (coin_status == 0) {
                                            coin_control(1);
                                            coin_status = 1;
                                        }
                                        if (coin_status == 1) {
                                            coin_control(2);
                                        }
                                        int coin_data_int = Integer.parseInt(coin_data);
                                        resetDisconnectTimer();
                                        EditText txtInsert = (EditText) findViewById(R.id.txtInsert);
                                        txtInsert.setText(String.valueOf(coin_data_int));
                                        EditText txtLeft = (EditText) findViewById(R.id.txtLeft);
                                        int amt_left = app_total - app_remain - coin_data_int;
                                        if (amt_left <= 0) {
                                            topup_count++;
                                            if (topup_count == 4) {
                                                String jsonString = getDataFromUrl("https://b.welovetopup.com/api/v1/services/promotiontopup2", "username=" + storedPreferenceUsername + "&token=" + storedPreferenceToken + "&sn=" + storedPreferenceSN + "&transaction_id=" + transaction_id + "&coinbanknote=" + coin_data_int);
                                                int topup_status = (coin_data_int + app_remain) - app_total;
                                                if (topup_status == 0) {
                                                    setContentView(R.layout.topup_success);
                                                    if (app_lang == 1) {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_finish);
                                                        mPlayer.start();
                                                    } else {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.finish);
                                                        mPlayer.start();
                                                    }
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            app_startup();
                                                        }
                                                    }, 5000);
                                                } else {
                                                    setContentView(R.layout.topup_success_save_credit);
                                                    if (app_lang == 1) {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_finish);
                                                        mPlayer.start();
                                                    } else {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.finish);
                                                        mPlayer.start();
                                                    }
                                                    EditText textNum = (EditText) findViewById(R.id.credit_left);
                                                    textNum.setText(String.valueOf(topup_status));
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            app_startup();
                                                        }
                                                    }, 5000);
                                                }
                                                coin_control(0);
                                                coin_status = 0;
                                                promotion_ft = 0;
                                            }
                                            amt_left = 0;
                                        } else {
                                            topup_count = 0;
                                        }
                                        txtLeft.setText(String.valueOf(amt_left));
                                    }
                                    // Bill Function
                                    if (bill_ft == 1) {
                                        if (coin_status == 0) {
                                            coin_control(1);
                                            coin_status = 1;
                                        }
                                        if (coin_status == 1) {
                                            coin_control(2);
                                        }
                                        int coin_data_int = Integer.parseInt(coin_data);
                                        resetDisconnectTimer();
                                        EditText txtInsert = (EditText) findViewById(R.id.txtInsert);
                                        txtInsert.setText(String.valueOf(coin_data_int));
                                        EditText txtLeft = (EditText) findViewById(R.id.txtLeft);
                                        double amt_left = app_bill_total - app_remain - coin_data_int;
                                        if (amt_left <= 0) {
                                            topup_count++;
                                            if (topup_count == 4) {
                                                String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=payment2", "username=" + storedPreferenceUsername + "&token=" + storedPreferenceToken + "&sn=" + storedPreferenceSN + "&transaction_id=" + transaction_id + "&coinbanknote=" + coin_data_int);
                                                double topup_status = (coin_data_int + app_remain) - app_bill_total;

                                                if (topup_status == 0) {
                                                    setContentView(R.layout.topup_success);
                                                    if (app_lang == 1) {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_finish);
                                                        mPlayer.start();
                                                    } else {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.finish);
                                                        mPlayer.start();
                                                    }
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            app_startup();
                                                        }
                                                    }, 5000);
                                                } else {
                                                    setContentView(R.layout.topup_success_save_credit);
                                                    if (app_lang == 1) {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_finish);
                                                        mPlayer.start();
                                                    } else {
                                                        MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.finish);
                                                        mPlayer.start();
                                                    }
                                                    EditText textNum = (EditText) findViewById(R.id.credit_left);
                                                    textNum.setText(String.valueOf(topup_status));
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            app_startup();

                                                        }
                                                    }, 5000);
                                                }
                                                coin_control(0);
                                                coin_status = 0;
                                                bill_ft = 0;
                                            }
                                            amt_left = 0;
                                        } else {
                                            topup_count = 0;
                                        }
                                        txtLeft.setText(String.valueOf(amt_left));
                                    }
                                }
                            });
                        }

                    } catch (InterruptedException e) {
                    }
                    handler_loop.postDelayed(this, 1000);
                }
            }
        };
        t.interrupt();
        t.start();
    }

    // Water Buttom
    public void wat_start(View view) {
        water_start=1;
        app_idle=1;
        if (water_used_sec < water_total_sec) {
            if (water_pump == 0) {
                pump_control(1);
                water_pump = 1;
            }
        }else{
            pump_control(0);
            water_start = 0;
            water_pump = 0;

        }
    }
    public void wat_stop(View view) {
        water_start=2;
        water_pump=0;
        app_idle=1;
        pump_control(0);
    }

    public void wash_main(View view) {
        wash_ft = 0;
        coin_status=0;
        coin_control(0);
        setContentView(R.layout.washing_menu);
        ImageView Menu1 = (ImageView) findViewById(R.id.Menu1);
        ImageView Menu2 = (ImageView) findViewById(R.id.Menu2);
        ImageView Menu3 = (ImageView) findViewById(R.id.Menu3);
        ImageView Menu4 = (ImageView) findViewById(R.id.Menu4);
        ImageView Menu5 = (ImageView) findViewById(R.id.Menu5);
        ImageView Menu6 = (ImageView) findViewById(R.id.Menu6);
        ImageView Menu7 = (ImageView) findViewById(R.id.Menu7);
        ImageView Menu8 = (ImageView) findViewById(R.id.Menu8);
        TextView txtWash1 = (TextView) findViewById(R.id.textView1);
        TextView txtWash2 = (TextView) findViewById(R.id.textView2);
        TextView txtWash3 = (TextView) findViewById(R.id.textView3);
        TextView txtWash4 = (TextView) findViewById(R.id.textView4);
        TextView txtWash5 = (TextView) findViewById(R.id.textView5);
        TextView txtWash6 = (TextView) findViewById(R.id.textView6);
        TextView txtWash7 = (TextView) findViewById(R.id.textView7);
        TextView txtWash8 = (TextView) findViewById(R.id.textView8);
        if (wash1_price > 0) {
            txtWash1.setText("# 1 ราคา " + wash1_price + " บาท");
            String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=1&uuid=" + storedPreferenceSN);
            int wash1_status = Integer.parseInt(jsonString);
            if (wash1_status == 0) {
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                Menu1.setTag("1");
            } else {
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                Menu1.setTag("0");
            }
        } else {
            txtWash1.setText("");
            Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
            Menu1.setTag("0");
        }
        if (wash2_price > 0) {
            txtWash2.setText("# 2 ราคา " + wash2_price + " บาท");
            String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=2&uuid=" + storedPreferenceSN);
            int wash2_status = Integer.parseInt(jsonString);
            if (wash2_status == 0) {
                Menu2.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                Menu2.setTag("2");
            } else {
                Menu2.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                Menu2.setTag("0");
            }
        } else {
            txtWash2.setText("");
            Menu2.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
            Menu2.setTag("0");
        }
        if (wash3_price > 0) {
            txtWash3.setText("# 3 ราคา " + wash3_price + " บาท");
            String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=3&uuid=" + storedPreferenceSN);
            int wash3_status = Integer.parseInt(jsonString);
            if (wash3_status == 0) {
                Menu3.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                Menu3.setTag("3");
            } else {
                Menu3.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                Menu3.setTag("0");
            }
        } else {
            txtWash3.setText("");
            Menu3.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
            Menu3.setTag("0");
        }
        if (wash4_price > 0) {
            txtWash4.setText("# 4 ราคา " + wash4_price + " บาท");
            String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=4&uuid=" + storedPreferenceSN);
            int wash4_status = Integer.parseInt(jsonString);
            if (wash4_status == 0) {
                Menu4.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                Menu4.setTag("4");
            } else {
                Menu4.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                Menu4.setTag("0");
            }
        } else {
            txtWash4.setText("");
            Menu4.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
            Menu4.setTag("0");
        }
        if (wash5_price > 0) {
            txtWash5.setText("# 5 ราคา " + wash5_price + " บาท");
            String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=5&uuid=" + storedPreferenceSN);
            int wash5_status = Integer.parseInt(jsonString);
            if (wash5_status == 0) {
                Menu5.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                Menu5.setTag("5");
            } else {
                Menu5.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                Menu5.setTag("0");
            }
        } else {
            txtWash5.setText("");
            Menu5.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
            Menu5.setTag("0");
        }
        if (wash6_price > 0) {
            txtWash6.setText("# 6 ราคา " + wash6_price + " บาท");
            String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=6&uuid=" + storedPreferenceSN);
            int wash6_status = Integer.parseInt(jsonString);
            if (wash6_status == 0) {
                Menu6.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                Menu6.setTag("6");
            } else {
                Menu6.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                Menu6.setTag("0");
            }
        } else {
            txtWash6.setText("");
            Menu6.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
            Menu6.setTag("0");
        }
        if (wash7_price > 0) {
            txtWash7.setText("# 7 ราคา " + wash7_price + " บาท");
            String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=7&uuid=" + storedPreferenceSN);
            int wash7_status = Integer.parseInt(jsonString);
            if (wash7_status == 0) {
                Menu7.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                Menu7.setTag("7");
            } else {
                Menu7.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                Menu7.setTag("0");
            }
        } else {
            txtWash7.setText("");
            Menu7.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
            Menu7.setTag("0");
        }
        if (wash8_price > 0) {
            txtWash8.setText("# 8 ราคา " + wash6_price + " บาท");
            String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=8&uuid=" + storedPreferenceSN);
            int wash8_status = Integer.parseInt(jsonString);
            if (wash8_status == 0) {
                Menu8.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                Menu8.setTag("8");
            } else {
                Menu8.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                Menu8.setTag("0");
            }
        } else {
            txtWash8.setText("");
            Menu8.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
            Menu8.setTag("0");
        }
    }
    public void wat_main(View view) {
        app_startup();


        water_fc=0;
        if(water_pump==1){
            pump_control(0);
        }
        light_control(0);
        coin_status=0;
        coin_control(0);
        water_used_sec=0;
    }
    public void air_start(View view) {
        water_start=1;
        app_idle=1;
        if (water_used_sec < water_total_sec) {
            if (water_pump == 0) {
                air_control(1);
                water_pump = 1;
            }
        }else{
            air_control(0);
            water_start = 0;
            water_pump = 0;

        }
    }
    public void air_main(View view) {
        app_startup();


        pump_fc=0;
        if(water_pump==1){
            air_control(0);
        }
        light_control(0);
        coin_status=0;
        coin_control(0);
        water_used_sec=0;
    }
    public void weight_main(View view) {
        app_startup();
        weight_fc=0;
        light_control(0);
        coin_status=0;
        coin_control(0);
    }
    public void top_num(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        String number = view.getTag().toString();
        if((number.indexOf("0") >= 0)&&(frm_number.length()==1)){
            textNum.setText("0");
        }else{
            if(frm_number.length()<10){
                textNum.setText(frm_number + number);
            }

        }
        resetDisconnectTimer();
    }
    public void top_del_all(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        textNum.setText("0");
        resetDisconnectTimer();
    }
    public void top_del_one(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        if(frm_number.length()>1){
            textNum.setText(frm_number.substring(0,(frm_number.length()-1)));
        }
        resetDisconnectTimer();
    }
    public void washing_number(View view) {
        coin_data = String.valueOf("0");
        String mc = view.getTag().toString();
        int mc_id = Integer.parseInt(mc);
        if(mc_id>0){
            setContentView(R.layout.washing_number);
            wash_work = mc_id;

        }else{
            Toast.makeText(mContext,"กรุณาเลือกเครื่องที่ว่าง",Toast.LENGTH_LONG).show();
        }
    }
    public void washing_confirm(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        if(frm_number.length()==10){
            app_number = frm_number;
            setContentView(R.layout.washing_paid);
            if(app_lang==1){
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_money);
                mPlayer.start();
            }else{
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.money);
                mPlayer.start();
            }
            app_remain = 0;
            if(wash_work==1){
                app_total = wash1_price;
            }else if(wash_work==2){
                app_total = wash2_price;
            }else if(wash_work==3){
                app_total = wash3_price;
            }else if(wash_work==4){
                app_total = wash4_price;
            }else if(wash_work==5){
                app_total = wash5_price;
            }else if(wash_work==6){
                app_total = wash6_price;
            }else if(wash_work==7){
                app_total = wash7_price;
            }else if(wash_work==8){
                app_total = wash8_price;
            }

            EditText txtFee = (EditText) findViewById(R.id.txtFee);
            txtFee.setText(String.valueOf(app_total));
            coin_status = 0;
            wash_ft = 1;
        }else{
            Toast.makeText(mContext,"กรุณาเลือกเครื่องที่ว่าง",Toast.LENGTH_LONG).show();
        }
        resetDisconnectTimer();
    }
    public void bill_confirm(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        if(frm_number.length()==10){
            app_number = frm_number;
            setContentView(R.layout.bill_paid);
            if(app_lang==1){
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_money);
                mPlayer.start();
            }else{
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.money);
                mPlayer.start();
            }

            String get_topup1 = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=payment1","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN+"&service_id="+app_oper+"&mobile_number="+app_number+"&price="+bill_amt+"&data1="+bill_ref1+"&data2="+bill_ref2+"&data3="+bill_ref3+"&data4="+bill_ref4);
            if (TextUtils.isEmpty(get_topup1)) {
                setContentView(R.layout.main_screen);
            }else {
                String[] separated = get_topup1.split("status");
                String[] separated1 = separated[1].split("\"");
                String topup_status = separated1[1];
                if(topup_status.contains("1")) {
                    String[] separated8 = get_topup1.split("transaction_id");
                    String[] separated9 = separated8[1].split("\"");
                    transaction_id = separated9[2];

                    String[] separated2 = get_topup1.split("customer");
                    String[] separated3 = separated2[1].split("credit");
                    String[] separated4 = separated3[1].split("\"");
                    double remain = Double.parseDouble(separated4[2]);
                    String new_remain = Integer.toString((int)remain);
                    String[] separated6 = get_topup1.split("fee");
                    String[] separated7 = separated6[1].split("\"");
                    double fee = Double.parseDouble(separated7[2]);
                    int new_fee = (int)fee;
                    //int total_amt = new_fee+bill_amt;
                    double total_amt = new_fee +bill_amt;
                    //String new_total_amt = Integer.toString((int)total_amt);
                    String new_total_amt = String.valueOf(total_amt);
                    Log.d("chk_oper", "Data1 : " + transaction_id + " Data2 : " + get_topup1+" Total : "+total_amt+" Remain :"+new_remain);

                    app_remain = Integer.parseInt(new_remain);
                    app_bill_total = Double.parseDouble(new_total_amt);

                    EditText txtNumShow = (EditText) findViewById(R.id.txtNumShow);
                    txtNumShow.setText(app_number);
                    EditText txtFee = (EditText) findViewById(R.id.txtFee);
                    txtFee.setText(new_total_amt);
                    EditText txtRemain = (EditText) findViewById(R.id.txtRemain);
                    txtRemain.setText(new_remain);
                    ImageView Menu1 = (ImageView) findViewById(R.id.Menu5);
                    Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + getTopupFromUrl("bill_pic", app_oper, "")));
                    coin_status = 0;
                    bill_ft = 1;
                }else{
                    setContentView(R.layout.main_screen);
                }
            }
        }else{
            Toast.makeText(mContext,"กรุณากรอกตัวเลขให้ถูกต้อง",Toast.LENGTH_LONG).show();
        }
        resetDisconnectTimer();
    }

    public void promotion_confirm(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        if(frm_number.length()==10){
            app_number = frm_number;
            setContentView(R.layout.promotion_paid);
            if(app_lang==1){
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_money);
                mPlayer.start();
            }else{
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.money);
                mPlayer.start();
            }
            String get_topup1 = getDataFromUrl("https://b.welovetopup.com/api/v1/services/promotiontopup1","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN+"&service_id="+app_card_oper+"&mobile_number="+app_number+"&price="+app_amt);
            if (TextUtils.isEmpty(get_topup1)) {
                setContentView(R.layout.main_screen);
            }else {
                String[] separated = get_topup1.split("status");
                String[] separated1 = separated[1].split("\"");
                String topup_status = separated1[1];
                if(topup_status.contains("1")) {
                    String[] separated8 = get_topup1.split("transaction_id");
                    String[] separated9 = separated8[1].split("\"");
                    transaction_id = separated9[2];

                    String[] separated2 = get_topup1.split("customer");
                    String[] separated3 = separated2[1].split("credit");
                    String[] separated4 = separated3[1].split("\"");
                    double remain = Double.parseDouble(separated4[2]);
                    String new_remain = Integer.toString((int)remain);
                    String[] separated6 = get_topup1.split("fee");
                    String[] separated7 = separated6[1].split("\"");
                    double fee = Double.parseDouble(separated7[2]);
                    int new_fee = (int) fee;
                    int total_amt = new_fee+app_amt;
                    String new_total_amt = Integer.toString((int)total_amt);
                    Log.d("chk_oper", "Data1 : " + transaction_id + " Data2 : " + get_topup1);

                    app_remain = Integer.parseInt(new_remain);
                    app_total = Integer.parseInt(new_total_amt);

                    EditText txtNumShow = (EditText) findViewById(R.id.txtNumShow);
                    txtNumShow.setText(app_number);
                    EditText txtFee = (EditText) findViewById(R.id.txtFee);
                    txtFee.setText(new_total_amt);
                    EditText txtRemain = (EditText) findViewById(R.id.txtRemain);
                    txtRemain.setText(new_remain);
                    ImageView Menu1 = (ImageView) findViewById(R.id.Menu5);
                    Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/service" + app_oper));
                    coin_status = 0;
                    promotion_ft = 1;
                }else{
                    setContentView(R.layout.main_screen);
                }
            }
        }else{
            Toast.makeText(mContext,"กรุณากรอกตัวเลขให้ถูกต้อง",Toast.LENGTH_LONG).show();
        }
        resetDisconnectTimer();
    }
    public void card_confirm(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        if(frm_number.length()==10){
            app_number = frm_number;
            setContentView(R.layout.card_paid);
            if(app_lang==1){
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_money);
                mPlayer.start();
            }else{
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.money);
                mPlayer.start();
            }

            String get_topup1 = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=topupcard1","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN+"&service_id="+app_card_oper+"&mobile_number="+app_number+"&price="+app_amt);
            if (TextUtils.isEmpty(get_topup1)) {
                setContentView(R.layout.main_screen);
            }else {
                String[] separated = get_topup1.split("status");
                String[] separated1 = separated[1].split("\"");
                String topup_status = separated1[1];
                if(topup_status.contains("1")) {
                    String[] separated8 = get_topup1.split("transaction_id");
                    String[] separated9 = separated8[1].split("\"");
                    transaction_id = separated9[2];

                    String[] separated2 = get_topup1.split("customer");
                    String[] separated3 = separated2[1].split("credit");
                    String[] separated4 = separated3[1].split("\"");
                    double remain = Double.parseDouble(separated4[2]);
                    String new_remain = Integer.toString((int)remain);
                    String[] separated6 = get_topup1.split("fee");
                    String[] separated7 = separated6[1].split("\"");
                    double fee = Double.parseDouble(separated7[2]);
                    int new_fee = (int) fee;
                    int total_amt = new_fee+app_amt;
                    String new_total_amt = Integer.toString((int)total_amt);
                    Log.d("chk_oper", "Data1 : " + transaction_id + " Data2 : " + get_topup1);

                    app_remain = Integer.parseInt(new_remain);
                    app_total = Integer.parseInt(new_total_amt);


                    EditText txtNumShow = (EditText) findViewById(R.id.txtNumShow);
                    txtNumShow.setText(app_number);
                    EditText txtFee = (EditText) findViewById(R.id.txtFee);
                    txtFee.setText(new_total_amt);
                    EditText txtRemain = (EditText) findViewById(R.id.txtRemain);
                    txtRemain.setText(new_remain);
                    ImageView Menu1 = (ImageView) findViewById(R.id.Menu5);
                    Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/service" + app_card_oper));
                    coin_status = 0;
                    card_ft = 1;
                }else{
                    setContentView(R.layout.main_screen);
                }
            }
        }else{
            Toast.makeText(mContext,"กรุณากรอกตัวเลขให้ถูกต้อง",Toast.LENGTH_LONG).show();
        }
        resetDisconnectTimer();
    }

    public void top_confirm(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        if(frm_number.length()==10){
            app_number = frm_number;
            setContentView(R.layout.topup_paid);
            if(app_lang==1){
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_money);
                mPlayer.start();
            }else{
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.money);
                mPlayer.start();
            }

            String get_topup1 = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=mobiletopup1","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN+"&service_id="+app_oper+"&mobile_number="+app_number+"&price="+app_amt);
            if (TextUtils.isEmpty(get_topup1)) {
                setContentView(R.layout.main_screen);
            }else {
                String[] separated = get_topup1.split("status");
                String[] separated1 = separated[1].split("\"");
                String topup_status = separated1[1];

                if(topup_status.contains("1")) {
                    String[] separated8 = get_topup1.split("transaction_id");
                    String[] separated9 = separated8[1].split("\"");
                    transaction_id = separated9[2];

                    String[] separated2 = get_topup1.split("customer");
                    String[] separated3 = separated2[1].split("credit");
                    String[] separated4 = separated3[1].split("\"");
                    double remain = Double.parseDouble(separated4[2]);
                    String new_remain = Integer.toString((int)remain);
                    String[] separated6 = get_topup1.split("fee");
                    String[] separated7 = separated6[1].split("\"");
                    double fee = Double.parseDouble(separated7[2]);
                    int new_fee = (int) fee;
                    int total_amt = new_fee+app_amt;
                    String new_total_amt = Integer.toString((int)total_amt);
                    Log.d("chk_oper", "Data1 : " + transaction_id + " Data2 : " + get_topup1);

                    app_remain = Integer.parseInt(new_remain);
                    app_total = Integer.parseInt(new_total_amt);

                    EditText txtNumShow = (EditText) findViewById(R.id.txtNumShow);
                    txtNumShow.setText(app_number);
                    EditText txtFee = (EditText) findViewById(R.id.txtFee);
                    txtFee.setText(new_total_amt);
                    EditText txtRemain = (EditText) findViewById(R.id.txtRemain);
                    txtRemain.setText(new_remain);
                    ImageView Menu1 = (ImageView) findViewById(R.id.Menu5);
                    Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/service" + app_oper));
                    topup_ft = 1;
                }else{
                    setContentView(R.layout.main_screen);
                }
            }
        }else{
            Toast.makeText(mContext,"กรุณากรอกตัวเลขให้ถูกต้อง",Toast.LENGTH_LONG).show();
        }
        resetDisconnectTimer();
    }
    public void top_home(View view) {
        app_startup();

    }
    public void report_form(View view) {
        setContentView(R.layout.report_screen);
        coin_control(0);
        resetDisconnectTimer();
    }
    public void report_confirm(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        if(frm_number.length()==10){
            setContentView(R.layout.report_success);
            getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=contact","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN+"&data1=2&mobile_number="+frm_number);
        }else{
            Toast.makeText(mContext,"กรุณากรอกตัวเลขให้ถูกต้อง",Toast.LENGTH_LONG).show();
        }
        resetDisconnectTimer();
    }
    public void set_num(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        String number = view.getTag().toString();

        if(bill_date==1){
            if(frm_number.length()<10){
                textNum.setText(frm_number + number);
            }

            if(frm_number.length()==2){
                textNum.setText(frm_number + "-"+number);
            }else if(frm_number.length()==5){
                textNum.setText(frm_number + "-"+number);
            }
        }else{
            if(frm_number.length()<16){
                textNum.setText(frm_number + number);
            }
        }
        resetDisconnectTimer();
    }
    public void set_num2(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        String number = view.getTag().toString();
        Button Menu1Button = (Button) findViewById(R.id.buttondot);
        String new_number = frm_number + number;

        if(frm_number.length()==0 && number.equals("0")){
            textNum.setText("");
            Menu1Button.setVisibility(View.GONE);
        }else{
            if(frm_number.indexOf(".") > 0){
                Menu1Button.setVisibility(View.GONE);

                if(frm_number.length()<10){
                    if(num_dot<3) {
                        textNum.setText(new_number);
                        num_dot++;
                    }
                    //textNum.setText(new_number);
                    //Toast.makeText(mContext,"NUM : "+arr_frm_number.length,Toast.LENGTH_LONG).show();

                }
            }else{
                Menu1Button.setVisibility(View.VISIBLE);
                if(frm_number.length()<10){
                    textNum.setText(new_number);
                }
            }

        }

        resetDisconnectTimer();
    }
    public void set_dot(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        Button Menu1Button = (Button) findViewById(R.id.buttondot);
        num_dot=1;
        if(frm_number.length()<10){
            textNum.setText(frm_number + ".");
            Menu1Button.setVisibility(View.GONE);
        }

        resetDisconnectTimer();
    }
    public void set_del_all(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        textNum.setText("");
        num_dot=0;
        resetDisconnectTimer();
    }
    public void set_del_one(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        if(num_dot>1){
            num_dot--;
        }
        if(frm_number.length()>0){
            String new_number = frm_number.substring(0,(frm_number.length()-1));
            textNum.setText(new_number);
        }

        resetDisconnectTimer();
    }

    public void set_del_all2(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        textNum.setText("");
        Button Menu1Button = (Button) findViewById(R.id.buttondot);
        Menu1Button.setVisibility(View.GONE);
        resetDisconnectTimer();
    }
    public void set_del_one2(View view) {
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        Button Menu1Button = (Button) findViewById(R.id.buttondot);

        if(frm_number.length()>0){
            String new_number = frm_number.substring(0,(frm_number.length()-1));
            if(new_number.indexOf(".") >= 0){
                Menu1Button.setVisibility(View.GONE);
            }else{
                Menu1Button.setVisibility(View.VISIBLE);
            }
            textNum.setText(new_number);
        }
        resetDisconnectTimer();
    }

    public void setting_touch(View view) {
        setting_count++;
        if(setting_count>4){
            setting_count=0;
            setContentView(R.layout.login_screen);
            //Toast.makeText(mContext,"setting",Toast.LENGTH_LONG).show();
        }
        resetDisconnectTimer();
    }
    public void disconnect_touch(View view) {
        setting_count++;
        if(setting_count>4){
            setting_count=0;
            nonet_setting=1;
            setContentView(R.layout.login_screen);
            //Toast.makeText(mContext,"setting",Toast.LENGTH_LONG).show();
        }
        resetDisconnectTimer();
    }
    public void setting_confirm(View view) {
        app_adblock=1;
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String frm_number = textNum.getText().toString();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String getPassFromLocal = prefs.getString("topupepay_machinepass" , "");
        if (TextUtils.isEmpty(getPassFromLocal)) {
            getPassFromLocal = "0000000000";
        }
        Toast.makeText(mContext,"Chk_Setting",Toast.LENGTH_LONG).show();
        if(frm_number.compareTo(getPassFromLocal)==0 || Onlinepass.equals(frm_number)){
            if(nonet_setting==1){
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
            setContentView(R.layout.setting_main);
            EditText textW1 = (EditText)findViewById(R.id.txtW1);
            EditText textW2 = (EditText)findViewById(R.id.txtW2);
            EditText textW3 = (EditText)findViewById(R.id.txtW3);
            EditText textW4 = (EditText)findViewById(R.id.txtW4);
            EditText textW5 = (EditText)findViewById(R.id.txtW5);
            EditText textW6 = (EditText)findViewById(R.id.txtW6);
            EditText textW7 = (EditText)findViewById(R.id.txtW7);
            EditText textW8 = (EditText)findViewById(R.id.txtW8);
            EditText textW9 = (EditText)findViewById(R.id.txtW9);
            EditText textW10 = (EditText)findViewById(R.id.txtW10);
            EditText textPump = (EditText)findViewById(R.id.txtPump);
            Button buttonAmt = (Button)findViewById(R.id.app_amt);
            CheckBox chkReboot = (CheckBox)findViewById(R.id.checkbox_closeads);
            int appreboot = prefs.getInt("topupepay_reboot" , 0);
            if(appreboot==1){
                chkReboot.setChecked(true);
            }

            textW1.setText(String.valueOf(prefs.getInt("topupepay_washl" , 0)));
            textW2.setText(String.valueOf(prefs.getInt("topupepay_wash2" , 0)));
            textW3.setText(String.valueOf(prefs.getInt("topupepay_wash3" , 0)));
            textW4.setText(String.valueOf(prefs.getInt("topupepay_wash4" , 0)));
            textW5.setText(String.valueOf(prefs.getInt("topupepay_wash5" , 0)));
            textW6.setText(String.valueOf(prefs.getInt("topupepay_wash6" , 0)));
            textW7.setText(String.valueOf(prefs.getInt("topupepay_wash7" , 0)));
            textW8.setText(String.valueOf(prefs.getInt("topupepay_wash8" , 0)));
            textW9.setText(String.valueOf(prefs.getInt("topupepay_wash9" , 0)));
            textW10.setText(String.valueOf(prefs.getInt("topupepay_wash10" , 0)));
            textPump.setText(String.valueOf(prefs.getInt("topupepay_airpump" , 0)));

            String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=machine","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
            String[] separated1 = jsonString.split(",");
            if(separated1[0].contains("1")) {
                if (separated1[6].contains("open")) {

                    String[] separated = jsonString.split("user");
                    String[] separated2 = separated[1].split("credits");
                    String[] separated3 = separated2[1].split("\"");
                    String balance = separated3[2];
                    buttonAmt.setText("ยอดเงินคงเหลือ " + balance);
                }
            }
        }else{
            Toast.makeText(mContext,"รหัสไม่ถูกต้อง",Toast.LENGTH_LONG).show();
        }
        resetDisconnectTimer();
    }
    public void setting_extra(View view) {
        app_adblock=1;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String getPassFromLocal = prefs.getString("topupepay_machinepass" , "");
        setContentView(R.layout.setting);
        EditText textSN = (EditText)findViewById(R.id.txtSN);
        EditText textTOKEN = (EditText)findViewById(R.id.txtTOKEN);
        EditText textUSER = (EditText)findViewById(R.id.txtUSER);
        EditText textPass = (EditText)findViewById(R.id.txtPASS);
        RadioButton myRadio1 = (RadioButton)findViewById(R.id.topup01);
        RadioButton myRadio2 = (RadioButton)findViewById(R.id.topup02);
        RadioButton myRadio3 = (RadioButton)findViewById(R.id.topup03);
        ImageView imgUpdate = (ImageView)findViewById(R.id.BackUpdate);
        CheckBox chkAds = (CheckBox)findViewById(R.id.checkbox_closeads);
        int appads = prefs.getInt("topupepay_adsblock" , 0);
        if(appads==1){
            chkAds.setChecked(true);
        }
        if(app_type==1){
            myRadio1.setChecked(true);
        }else if(app_type==2){
            myRadio2.setChecked(true);
        }else if(app_type==3){
            myRadio3.setChecked(true);
        }
        textSN.setText(prefs.getString("topupepay_sn" , ""));
        textTOKEN.setText(prefs.getString("topupepay_token" , ""));
        textUSER.setText(prefs.getString("topupepay_user" , ""));
        textPass.setText(getPassFromLocal);
        if(app_type==3){
            LinearLayout labelWater = (LinearLayout) findViewById(R.id.labelWater);
            EditText textWATER = (EditText)findViewById(R.id.txtWATER);
            labelWater.setVisibility(View.VISIBLE);
            textWATER.setVisibility(View.VISIBLE);
            textWATER.setText(String.valueOf(prefs.getInt("topupepay_waterlevel" , 0)));
        }
        String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/update.php","");

        int last_version = Integer.parseInt(jsonString.toString());
        if(app_version<last_version){
            imgUpdate.setVisibility(View.VISIBLE);
        }

        resetDisconnectTimer();
    }
    public void setting_hidekeyboard(View view) {

        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        EditText textSN = (EditText)findViewById(R.id.txtSN);
        EditText textTOKEN = (EditText)findViewById(R.id.txtTOKEN);
        EditText textUSER = (EditText)findViewById(R.id.txtUSER);
        EditText textPass = (EditText)findViewById(R.id.txtPASS);
        if(app_type==3) {
            EditText textWATER = (EditText) findViewById(R.id.txtWATER);
            imm.hideSoftInputFromWindow(textWATER.getWindowToken(), 0);
        }

        imm.hideSoftInputFromWindow(textSN.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textTOKEN.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textUSER.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textPass.getWindowToken(), 0);

    }
    public void setting_hidekeyboard2(View view) {

        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        EditText textW1 = (EditText)findViewById(R.id.txtW1);
        EditText textW2 = (EditText)findViewById(R.id.txtW2);
        EditText textW3 = (EditText)findViewById(R.id.txtW3);
        EditText textW4 = (EditText)findViewById(R.id.txtW4);
        EditText textW5 = (EditText)findViewById(R.id.txtW5);
        EditText textW6 = (EditText)findViewById(R.id.txtW6);
        EditText textW7 = (EditText)findViewById(R.id.txtW7);
        EditText textW8 = (EditText)findViewById(R.id.txtW8);
        EditText textW9 = (EditText)findViewById(R.id.txtW9);
        EditText textW10 = (EditText)findViewById(R.id.txtW10);
        EditText textPump = (EditText)findViewById(R.id.txtPump);

        imm.hideSoftInputFromWindow(textW1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textW2.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textW3.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textW4.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textW5.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textW6.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textW7.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textW8.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textW9.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textW10.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(textPump.getWindowToken(), 0);


    }
    public void setting_main_save(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        EditText textW1 = (EditText)findViewById(R.id.txtW1);
        EditText textW2 = (EditText)findViewById(R.id.txtW2);
        EditText textW3 = (EditText)findViewById(R.id.txtW3);
        EditText textW4 = (EditText)findViewById(R.id.txtW4);
        EditText textW5 = (EditText)findViewById(R.id.txtW5);
        EditText textW6 = (EditText)findViewById(R.id.txtW6);
        EditText textW7 = (EditText)findViewById(R.id.txtW7);
        EditText textW8 = (EditText)findViewById(R.id.txtW8);
        EditText textW9 = (EditText)findViewById(R.id.txtW9);
        EditText textW10 = (EditText)findViewById(R.id.txtW10);
        EditText textPump = (EditText)findViewById(R.id.txtPump);
        CheckBox chkReboot = (CheckBox)findViewById(R.id.checkbox_closeads);

        editor.putInt("topupepay_washl", Integer.parseInt(textW1.getText().toString()));
        editor.putInt("topupepay_wash2", Integer.parseInt(textW2.getText().toString()));
        editor.putInt("topupepay_wash3", Integer.parseInt(textW3.getText().toString()));
        editor.putInt("topupepay_wash4", Integer.parseInt(textW4.getText().toString()));
        editor.putInt("topupepay_wash5", Integer.parseInt(textW5.getText().toString()));
        editor.putInt("topupepay_wash6", Integer.parseInt(textW6.getText().toString()));
        editor.putInt("topupepay_wash7", Integer.parseInt(textW7.getText().toString()));
        editor.putInt("topupepay_wash8", Integer.parseInt(textW8.getText().toString()));
        editor.putInt("topupepay_wash9", Integer.parseInt(textW9.getText().toString()));
        editor.putInt("topupepay_wash10", Integer.parseInt(textW10.getText().toString()));
        editor.putInt("topupepay_airpump", Integer.parseInt(textPump.getText().toString()));

        if(chkReboot.isChecked()){
            editor.putInt("topupepay_reboot", 1);
        }else{
            editor.putInt("topupepay_reboot", 0);
        }

        editor.commit();
        app_startup();

    }
    public void setting_save(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        EditText textSN = (EditText)findViewById(R.id.txtSN);
        EditText textTOKEN = (EditText)findViewById(R.id.txtTOKEN);
        EditText textUSER = (EditText)findViewById(R.id.txtUSER);
        EditText textPass = (EditText)findViewById(R.id.txtPASS);
        CheckBox chkAds = (CheckBox)findViewById(R.id.checkbox_closeads);
        if(app_type==3) {
            EditText textWATER = (EditText) findViewById(R.id.txtWATER);
            editor.putInt("topupepay_waterlevel", Integer.parseInt(textWATER.getText().toString()));
        }
        RadioButton myRadio1 = (RadioButton)findViewById(R.id.topup01);
        RadioButton myRadio2 = (RadioButton)findViewById(R.id.topup02);
        RadioButton myRadio3 = (RadioButton)findViewById(R.id.topup03);

        editor.putString("topupepay_sn", textSN.getText().toString());
        editor.putString("topupepay_token", textTOKEN.getText().toString());
        editor.putString("topupepay_user", textUSER.getText().toString());
        editor.putString("topupepay_machinepass", textPass.getText().toString());

        if(myRadio1.isChecked()){
            editor.putInt("topupepay_apptype", 1);
        }else if(myRadio2.isChecked()){
            editor.putInt("topupepay_apptype", 2);
        }else if(myRadio3.isChecked()){
            editor.putInt("topupepay_apptype", 3);
        }

        if(chkAds.isChecked()){
            editor.putInt("topupepay_adsblock", 1);
        }else{
            editor.putInt("topupepay_adsblock", 0);
        }

        editor.commit();
        app_startup();

    }
    public void setting_exit(View view) {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    public void setting_update(View view) throws IOException {
        //String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/update.php","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cp.welovetopupline.com/m/usb"+jsonString+".apk"));
        //startActivity(browserIntent);

        String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=ads_request","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        if (TextUtils.isEmpty(jsonString)) {
            setContentView(R.layout.main_screen);
        }else {
            String[] arr_ads = jsonString.split("-");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            String ads_id;
            String ads_type;
            String ads_data1;
            String ads_data2;
            String ads_data3;
            String ads_data4;
            String ads_data5;
            String ads_data6;
            editor.putInt("topupepay_ads1_type", 0);
            editor.putInt("topupepay_ads2_type", 0);
            editor.putInt("topupepay_ads3_type", 0);
            editor.putInt("topupepay_ads4_type", 0);
            editor.putInt("topupepay_ads5_type", 0);
            editor.putInt("topupepay_ads6_type", 0);
            editor.putInt("topupepay_ads7_type", 0);
            editor.putInt("topupepay_ads8_type", 0);
            for (byte i = 0; i < arr_ads.length; i++) {
                String[] arr_ads_data = arr_ads[i].split(",");
                Log.d("chk_oper", "Data1 : " + arr_ads_data[0] + " Data2 : " + jsonString);
                ads_id = arr_ads_data[0];
                ads_type = arr_ads_data[1];
                int ads_type_int = Integer.parseInt(ads_type.toString());
                int ads_data7 = Integer.parseInt(arr_ads_data[8].toString());
                editor.putInt("topupepay_ads"+ads_id+"_type", ads_type_int);
                editor.putInt("topupepay_ads"+ads_id+"_time", ads_data7);
                if(ads_type_int==1){
                    ads_data1 = arr_ads_data[2];
                    ads_data4 = arr_ads_data[5];
                    editor.putString("topupepay_ads"+ads_id+"_img", ads_data4);
                    Log.d("chk_oper", "Data1 : " + ads_type_int + " Data2 : " + ads_data4);
                    getADSFromURL(ads_data1,"images");
                }else if(ads_type_int==2){
                    ads_data1 = arr_ads_data[2];
                    ads_data2 = arr_ads_data[3];
                    ads_data4 = arr_ads_data[5];
                    ads_data5 = arr_ads_data[6];
                    editor.putString("topupepay_ads"+ads_id+"_img", ads_data4);
                    getADSFromURL(ads_data1,"images");
                    editor.putString("topupepay_ads"+ads_id+"_sound", ads_data5);
                    Log.d("chk_oper", "Data1 : " + ads_type_int + " Data2 : " + ads_data4);
                    Log.d("chk_oper", "Data1 : " + ads_type_int + " Data2 : " + ads_data5);
                    getADSFromURL(ads_data2,"sound");
                }else if(ads_type_int==3){
                    ads_data3 = arr_ads_data[4];
                    ads_data6 = arr_ads_data[7];
                    Log.d("chk_oper", "Data1 : " + ads_type_int + " Data2 : " + ads_data6+ "Data3 :"+ads_id);
                    editor.putString("topupepay_ads"+ads_id+"_vdo", ads_data6);
                    getADSFromURL(ads_data3,"video");
                }
            }
            editor.commit();
            app_startup();
        }

    }
    public void setting_update_app(View view) throws IOException {
        String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/update.php","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cp.welovetopupline.com/m/usb"+jsonString+".apk"));
        startActivity(browserIntent);
    }
    public void setting_resetamt(View view) {
        String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=reset","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        app_startup();
    }
    public void setting_device(View view) {//#new

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.custom_dialog_menu_select, null);
        dialog.setContentView(view1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final View boxMenu1 = dialog.findViewById(R.id.boxMenu1);
        final View boxMenu2 = dialog.findViewById(R.id.boxMenu2);
        final View boxMenu3 = dialog.findViewById(R.id.boxMenu3);
        final View boxMenu4 = dialog.findViewById(R.id.boxMenu4);
        TextView menu1 = (TextView) dialog.findViewById(R.id.menu1);
        TextView menu2 = (TextView) dialog.findViewById(R.id.menu2);
        TextView menu3 = (TextView) dialog.findViewById(R.id.menu3);
        TextView menu4 = (TextView) dialog.findViewById(R.id.menu4);

       boxMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);

                dialog.dismiss();
            }
        });

        boxMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.co.th/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                dialog.dismiss();

            }
        });

        boxMenu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "Your Phone_number"));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);


                dialog.dismiss();

            }
        });

        boxMenu4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
        });

        dialog.show();



    }
    public void topup_main(View view) {
        setContentView(R.layout.main_topup_tot);
        if(app_lang==1){
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_welocome);
            mPlayer.start();
        }else{
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.welcome);
            mPlayer.start();
        }
        TextView myAwesomeTextView = (TextView)findViewById(R.id.textView);
        myAwesomeTextView.setText(getMachineFromUrl());
        light_control(1);
        light_control(1);
        resetDisconnectTimer();
    }
    public void Imageview_Status(int data,ImageView img_id) {
        if (img_id == null) {
            return;
        }
        if(data==0) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);  //0 means grayscale
            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
            img_id.setColorFilter(cf);
            //img_id.setImageAlpha(128);   // 128 = 0.5
            img_id.setClickable(false);
        }else{
            img_id.setColorFilter(null);
            //img_id.setImageAlpha(255);   // 128 = 0.5
            img_id.setClickable(true);
        }

    }

    public void topup_network(View view) {
        setContentView(R.layout.topup_operator);
        if(app_lang==0) {
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.network);
            mPlayer.start();
        }else{
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_network);
            mPlayer.start();
        }
        ImageView Menu1 = (ImageView)findViewById(R.id.Menu1);
        ImageView Menu2 = (ImageView)findViewById(R.id.Menu2);
        ImageView Menu3 = (ImageView)findViewById(R.id.Menu3);
        ImageView Menu4 = (ImageView)findViewById(R.id.Menu4);
        ImageView Menu10 = (ImageView)findViewById(R.id.Menu5);
        ImageView Menu11 = (ImageView)findViewById(R.id.Menu6);
        ImageView Menu12 = (ImageView)findViewById(R.id.Menu7);
        ImageView Menu13 = (ImageView)findViewById(R.id.Menu8);
        ImageView Menu14 = (ImageView)findViewById(R.id.menu9);
        ImageView Menu99 = (ImageView)findViewById(R.id.menu10);

        Imageview_Status(0,Menu99);

        String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=topup_request","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        if (TextUtils.isEmpty(jsonString)) {
            setContentView(R.layout.main_screen);
        }else {
            String[] oper_separated = jsonString.split(",");
            for (byte i = 0; i < oper_separated.length; i++) {
                String[] oper_separated_data = oper_separated[i].split("-");

                int data_id = Integer.parseInt(oper_separated_data[0].toString());
                int data_status = Integer.parseInt(oper_separated_data[1].toString());

                if (data_id == 1) {
                    Imageview_Status(data_status, Menu1);
                } else if (data_id == 2) {
                    Imageview_Status(data_status, Menu2);
                } else if (data_id == 3) {
                    Imageview_Status(data_status, Menu3);
                } else if (data_id == 4) {
                    Imageview_Status(data_status, Menu4);
                } else if (data_id == 10) {
                    Imageview_Status(data_status, Menu10);
                } else if (data_id == 11) {
                    Imageview_Status(data_status, Menu11);
                } else if (data_id == 12) {
                    Imageview_Status(data_status, Menu12);
                } else if (data_id == 13) {
                    Imageview_Status(data_status, Menu13);
                } else if (data_id == 14) {
                    Imageview_Status(data_status, Menu14);
                }
            }
        }
        resetDisconnectTimer();

    }
    public void topup_amt(View view) {
        setContentView(R.layout.topup_amt);
        if(app_lang==1){
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_amt);
            mPlayer.start();
        }else{
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.amt);
            mPlayer.start();
        }
        String topup_get_amt = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=topup_amt_request","id="+app_oper+"&username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        if (TextUtils.isEmpty(topup_get_amt)) {
            setContentView(R.layout.main_screen);
        }else {
            String[] arr_amt = topup_get_amt.split(",");

            for (byte i = 0; i < arr_amt.length; i++) {
                if (i == 0) {
                    Button Menu1Button = (Button) findViewById(R.id.Menu1);
                    Menu1Button.setText(arr_amt[i]);
                    Menu1Button.setVisibility(View.VISIBLE);
                    Menu1Button.setTag(arr_amt[i]);
                } else if (i == 1) {
                    Button Menu2Button = (Button) findViewById(R.id.Menu2);
                    Menu2Button.setText(arr_amt[i]);
                    Menu2Button.setVisibility(View.VISIBLE);
                    Menu2Button.setTag(arr_amt[i]);
                } else if (i == 2) {
                    Button Menu3Button = (Button) findViewById(R.id.Menu3);
                    Menu3Button.setText(arr_amt[i]);
                    Menu3Button.setVisibility(View.VISIBLE);
                    Menu3Button.setTag(arr_amt[i]);
                } else if (i == 3) {
                    Button Menu4Button = (Button) findViewById(R.id.Menu4);
                    Menu4Button.setText(arr_amt[i]);
                    Menu4Button.setVisibility(View.VISIBLE);
                    Menu4Button.setTag(arr_amt[i]);
                } else if (i == 4) {
                    Button Menu5Button = (Button) findViewById(R.id.Menu5);
                    Menu5Button.setText(arr_amt[i]);
                    Menu5Button.setVisibility(View.VISIBLE);
                    Menu5Button.setTag(arr_amt[i]);
                } else if (i == 5) {
                    Button Menu6Button = (Button) findViewById(R.id.Menu6);
                    Menu6Button.setText(arr_amt[i]);
                    Menu6Button.setVisibility(View.VISIBLE);
                    Menu6Button.setTag(arr_amt[i]);
                } else if (i == 6) {
                    Button Menu7Button = (Button) findViewById(R.id.Menu7);
                    Menu7Button.setText(arr_amt[i]);
                    Menu7Button.setVisibility(View.VISIBLE);
                    Menu7Button.setTag(arr_amt[i]);
                } else if (i == 7) {
                    Button Menu8Button = (Button) findViewById(R.id.Menu8);
                    Menu8Button.setText(arr_amt[i]);
                    Menu8Button.setVisibility(View.VISIBLE);
                    Menu8Button.setTag(arr_amt[i]);
                } else if (i == 8) {
                    Button Menu9Button = (Button) findViewById(R.id.Menu9);
                    Menu9Button.setText(arr_amt[i]);
                    Menu9Button.setVisibility(View.VISIBLE);
                    Menu9Button.setTag(arr_amt[i]);
                } else if (i == 9) {
                    Button Menu10Button = (Button) findViewById(R.id.Menu10);
                    Menu10Button.setText(arr_amt[i]);
                    Menu10Button.setVisibility(View.VISIBLE);
                    Menu10Button.setTag(arr_amt[i]);
                } else if (i == 10) {
                    Button Menu11Button = (Button) findViewById(R.id.Menu11);
                    Menu11Button.setText(arr_amt[i]);
                    Menu11Button.setVisibility(View.VISIBLE);
                    Menu11Button.setTag(arr_amt[i]);
                } else if (i == 11) {
                    Button Menu12Button = (Button) findViewById(R.id.Menu12);
                    Menu12Button.setText(arr_amt[i]);
                    Menu12Button.setVisibility(View.VISIBLE);
                    Menu12Button.setTag(arr_amt[i]);
                }
            }
        }

        resetDisconnectTimer();
    }
    public void topup_number(View view) {
        setContentView(R.layout.topup_number);
        if(app_lang==1){
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_number);
            mPlayer.start();
        }else{
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.number);
            mPlayer.start();
        }

        resetDisconnectTimer();
        topup_ft=0;
        coin_status = 0;
        coin_data = String.valueOf("0");
        coin_control(0);
    }
    public void card_number(View view) {
        setContentView(R.layout.card_number);

        resetDisconnectTimer();
        card_ft=0;
        coin_status = 0;
        coin_data = String.valueOf("0");
        coin_control(0);
    }
    public void promotion_number(View view) {
        setContentView(R.layout.promotion_number);

        resetDisconnectTimer();
        promotion_ft=0;
        coin_status = 0;
        coin_data = String.valueOf("0");
        coin_control(0);
    }
    public void bill_number(View view) {
        setContentView(R.layout.bill_number);

        resetDisconnectTimer();
        bill_ft=0;
        coin_status = 0;
        coin_data = String.valueOf("0");
        coin_control(0);
    }

    public void set_card_oper(View view) {
        String oper = view.getTag().toString();
        app_card_oper = oper;
        String card_get_amt = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=card_amt_request","id="+app_card_oper+"&username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        if (TextUtils.isEmpty(card_get_amt)) {
            setContentView(R.layout.main_screen);
        }else {
            setContentView(R.layout.card_amt);
            String[] arr_amt = card_get_amt.split(",");

            for (byte i = 0; i < arr_amt.length; i++) {
                if (i == 0) {
                    Button Menu1Button = (Button) findViewById(R.id.Menu1);
                    Menu1Button.setText(arr_amt[i]);
                    Menu1Button.setVisibility(View.VISIBLE);
                    Menu1Button.setTag(arr_amt[i]);
                } else if (i == 1) {
                    Button Menu2Button = (Button) findViewById(R.id.Menu2);
                    Menu2Button.setText(arr_amt[i]);
                    Menu2Button.setVisibility(View.VISIBLE);
                    Menu2Button.setTag(arr_amt[i]);
                } else if (i == 2) {
                    Button Menu3Button = (Button) findViewById(R.id.Menu3);
                    Menu3Button.setText(arr_amt[i]);
                    Menu3Button.setVisibility(View.VISIBLE);
                    Menu3Button.setTag(arr_amt[i]);
                } else if (i == 3) {
                    Button Menu4Button = (Button) findViewById(R.id.Menu4);
                    Menu4Button.setText(arr_amt[i]);
                    Menu4Button.setVisibility(View.VISIBLE);
                    Menu4Button.setTag(arr_amt[i]);
                } else if (i == 4) {
                    Button Menu5Button = (Button) findViewById(R.id.Menu5);
                    Menu5Button.setText(arr_amt[i]);
                    Menu5Button.setVisibility(View.VISIBLE);
                    Menu5Button.setTag(arr_amt[i]);
                } else if (i == 5) {
                    Button Menu6Button = (Button) findViewById(R.id.Menu6);
                    Menu6Button.setText(arr_amt[i]);
                    Menu6Button.setVisibility(View.VISIBLE);
                    Menu6Button.setTag(arr_amt[i]);
                } else if (i == 6) {
                    Button Menu7Button = (Button) findViewById(R.id.Menu7);
                    Menu7Button.setText(arr_amt[i]);
                    Menu7Button.setVisibility(View.VISIBLE);
                    Menu7Button.setTag(arr_amt[i]);
                } else if (i == 7) {
                    Button Menu8Button = (Button) findViewById(R.id.Menu8);
                    Menu8Button.setText(arr_amt[i]);
                    Menu8Button.setVisibility(View.VISIBLE);
                    Menu8Button.setTag(arr_amt[i]);
                } else if (i == 8) {
                    Button Menu9Button = (Button) findViewById(R.id.Menu9);
                    Menu9Button.setText(arr_amt[i]);
                    Menu9Button.setVisibility(View.VISIBLE);
                    Menu9Button.setTag(arr_amt[i]);
                } else if (i == 9) {
                    Button Menu10Button = (Button) findViewById(R.id.Menu10);
                    Menu10Button.setText(arr_amt[i]);
                    Menu10Button.setVisibility(View.VISIBLE);
                    Menu10Button.setTag(arr_amt[i]);
                } else if (i == 10) {
                    Button Menu11Button = (Button) findViewById(R.id.Menu11);
                    Menu11Button.setText(arr_amt[i]);
                    Menu11Button.setVisibility(View.VISIBLE);
                    Menu11Button.setTag(arr_amt[i]);
                } else if (i == 11) {
                    Button Menu12Button = (Button) findViewById(R.id.Menu12);
                    Menu12Button.setText(arr_amt[i]);
                    Menu12Button.setVisibility(View.VISIBLE);
                    Menu12Button.setTag(arr_amt[i]);
                }
            }
        }
    }
    public void set_bill_oper(View view) {
        String oper = view.getTag().toString();
        app_oper = Integer.parseInt(oper);
        setContentView(R.layout.bill_amt);
    }
    public void set_promotion_oper(View view) {
        String oper = view.getTag().toString();
        app_oper = Integer.parseInt(oper);
        setContentView(R.layout.promotion_amt);
        ImageView Menu1 = (ImageView)findViewById(R.id.logo);

        Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/service" + app_oper));

        if(app_lang==1){
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_amt);
            mPlayer.start();
        }else{
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.amt);
            mPlayer.start();
        }
        String promotion_get_amt = getDataFromUrl("https://member.welovetopupline.com/webservice/promotion_request.php","id="+app_oper+"&username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        if (TextUtils.isEmpty(promotion_get_amt)) {
            setContentView(R.layout.main_screen);
        }else {

            String[] arr_amt = promotion_get_amt.split(",");

            for (byte i = 0; i < arr_amt.length; i++) {
                String[] pro_data = arr_amt[i].split("-");
                String pro_name = pro_data[0];
                String pro_amt = pro_data[1];
                if (i == 0) {
                    Button Menu1Button = (Button) findViewById(R.id.Menu1);
                    Menu1Button.setText(pro_name);
                    Menu1Button.setVisibility(View.VISIBLE);
                    Menu1Button.setTag(pro_amt);
                } else if (i == 1) {
                    Button Menu2Button = (Button) findViewById(R.id.Menu2);
                    Menu2Button.setText(pro_name);
                    Menu2Button.setVisibility(View.VISIBLE);
                    Menu2Button.setTag(pro_amt);
                } else if (i == 2) {
                    Button Menu3Button = (Button) findViewById(R.id.Menu3);
                    Menu3Button.setText(pro_name);
                    Menu3Button.setVisibility(View.VISIBLE);
                    Menu3Button.setTag(pro_amt);
                } else if (i == 3) {
                    Button Menu4Button = (Button) findViewById(R.id.Menu4);
                    Menu4Button.setText(pro_name);
                    Menu4Button.setVisibility(View.VISIBLE);
                    Menu4Button.setTag(pro_amt);
                } else if (i == 4) {
                    Button Menu5Button = (Button) findViewById(R.id.Menu5);
                    Menu5Button.setText(pro_name);
                    Menu5Button.setVisibility(View.VISIBLE);
                    Menu5Button.setTag(pro_amt);
                } else if (i == 5) {
                    Button Menu6Button = (Button) findViewById(R.id.Menu6);
                    Menu6Button.setText(pro_name);
                    Menu6Button.setVisibility(View.VISIBLE);
                    Menu6Button.setTag(pro_amt);
                } else if (i == 6) {
                    Button Menu7Button = (Button) findViewById(R.id.Menu7);
                    Menu7Button.setText(pro_name);
                    Menu7Button.setVisibility(View.VISIBLE);
                    Menu7Button.setTag(pro_amt);
                } else if (i == 7) {
                    Button Menu8Button = (Button) findViewById(R.id.Menu8);
                    Menu8Button.setText(pro_name);
                    Menu8Button.setVisibility(View.VISIBLE);
                    Menu8Button.setTag(pro_amt);
                } else if (i == 8) {
                    Button Menu9Button = (Button) findViewById(R.id.Menu9);
                    Menu9Button.setText(pro_name);
                    Menu9Button.setVisibility(View.VISIBLE);
                    Menu9Button.setTag(pro_amt);
                } else if (i == 9) {
                    Button Menu10Button = (Button) findViewById(R.id.Menu10);
                    Menu10Button.setText(pro_name);
                    Menu10Button.setVisibility(View.VISIBLE);
                    Menu10Button.setTag(pro_amt);
                } else if (i == 10) {
                    Button Menu11Button = (Button) findViewById(R.id.Menu11);
                    Menu11Button.setText(pro_name);
                    Menu11Button.setVisibility(View.VISIBLE);
                    Menu11Button.setTag(pro_amt);
                } else if (i == 11) {
                    Button Menu12Button = (Button) findViewById(R.id.Menu12);
                    Menu12Button.setText(pro_name);
                    Menu12Button.setVisibility(View.VISIBLE);
                    Menu12Button.setTag(pro_amt);
                } else if (i == 12) {
                    Button Menu13Button = (Button) findViewById(R.id.Menu13);
                    Menu13Button.setText(pro_name);
                    Menu13Button.setVisibility(View.VISIBLE);
                    Menu13Button.setTag(pro_amt);
                }
            }
        }
        resetDisconnectTimer();
    }

    public void set_oper(View view) {
        String oper = view.getTag().toString();
        app_oper = Integer.parseInt(oper);
        setContentView(R.layout.topup_amt);
        if(app_lang==1){
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_amt);
            mPlayer.start();
        }else{
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.amt);
            mPlayer.start();
        }
        String topup_get_amt = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=topup_amt_request","id="+app_oper+"&username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        if (TextUtils.isEmpty(topup_get_amt)) {
            setContentView(R.layout.main_screen);
        }else {
            String[] arr_amt = topup_get_amt.split(",");

            for (byte i = 0; i < arr_amt.length; i++) {
                if (i == 0) {
                    Button Menu1Button = (Button) findViewById(R.id.Menu1);
                    Menu1Button.setText(arr_amt[i]);
                    Menu1Button.setVisibility(View.VISIBLE);
                    Menu1Button.setTag(arr_amt[i]);
                } else if (i == 1) {
                    Button Menu2Button = (Button) findViewById(R.id.Menu2);
                    Menu2Button.setText(arr_amt[i]);
                    Menu2Button.setVisibility(View.VISIBLE);
                    Menu2Button.setTag(arr_amt[i]);
                } else if (i == 2) {
                    Button Menu3Button = (Button) findViewById(R.id.Menu3);
                    Menu3Button.setText(arr_amt[i]);
                    Menu3Button.setVisibility(View.VISIBLE);
                    Menu3Button.setTag(arr_amt[i]);
                } else if (i == 3) {
                    Button Menu4Button = (Button) findViewById(R.id.Menu4);
                    Menu4Button.setText(arr_amt[i]);
                    Menu4Button.setVisibility(View.VISIBLE);
                    Menu4Button.setTag(arr_amt[i]);
                } else if (i == 4) {
                    Button Menu5Button = (Button) findViewById(R.id.Menu5);
                    Menu5Button.setText(arr_amt[i]);
                    Menu5Button.setVisibility(View.VISIBLE);
                    Menu5Button.setTag(arr_amt[i]);
                } else if (i == 5) {
                    Button Menu6Button = (Button) findViewById(R.id.Menu6);
                    Menu6Button.setText(arr_amt[i]);
                    Menu6Button.setVisibility(View.VISIBLE);
                    Menu6Button.setTag(arr_amt[i]);
                } else if (i == 6) {
                    Button Menu7Button = (Button) findViewById(R.id.Menu7);
                    Menu7Button.setText(arr_amt[i]);
                    Menu7Button.setVisibility(View.VISIBLE);
                    Menu7Button.setTag(arr_amt[i]);
                } else if (i == 7) {
                    Button Menu8Button = (Button) findViewById(R.id.Menu8);
                    Menu8Button.setText(arr_amt[i]);
                    Menu8Button.setVisibility(View.VISIBLE);
                    Menu8Button.setTag(arr_amt[i]);
                } else if (i == 8) {
                    Button Menu9Button = (Button) findViewById(R.id.Menu9);
                    Menu9Button.setText(arr_amt[i]);
                    Menu9Button.setVisibility(View.VISIBLE);
                    Menu9Button.setTag(arr_amt[i]);
                } else if (i == 9) {
                    Button Menu10Button = (Button) findViewById(R.id.Menu10);
                    Menu10Button.setText(arr_amt[i]);
                    Menu10Button.setVisibility(View.VISIBLE);
                    Menu10Button.setTag(arr_amt[i]);
                } else if (i == 10) {
                    Button Menu11Button = (Button) findViewById(R.id.Menu11);
                    Menu11Button.setText(arr_amt[i]);
                    Menu11Button.setVisibility(View.VISIBLE);
                    Menu11Button.setTag(arr_amt[i]);
                } else if (i == 11) {
                    Button Menu12Button = (Button) findViewById(R.id.Menu12);
                    Menu12Button.setText(arr_amt[i]);
                    Menu12Button.setVisibility(View.VISIBLE);
                    Menu12Button.setTag(arr_amt[i]);
                }
            }
        }
        resetDisconnectTimer();
    }

    public void bill_sub(View view) {
        String oper = view.getTag().toString();
        app_oper = Integer.parseInt(oper);
        setContentView(R.layout.bill_sub);
        String billlist =getTopupFromUrl("get_bill_sub",app_oper,"");
        String[] arr_amt =  billlist.split(",");
        for (byte i = 0; i < arr_amt.length; i++) {
            String[] bill_data = arr_amt[i].split("-");
            String bill_img = bill_data[0];
            String bill_code = bill_data[1];
            if(i==0) {
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView1);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==1) {
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView2);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==2) {
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView3);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==3) {
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView4);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==4) {
                ImageView Menu2 = (ImageView)findViewById(R.id.imageView6);
                Menu2.setVisibility(View.VISIBLE);
                ImageView Menu3 = (ImageView)findViewById(R.id.imageView7);
                Menu3.setVisibility(View.VISIBLE);
                ImageView Menu4 = (ImageView)findViewById(R.id.imageView8);
                Menu4.setVisibility(View.VISIBLE);
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView5);

                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==5) {
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView6);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==6) {
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView7);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==7) {
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView8);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==8) {
                ImageView Menu2 = (ImageView)findViewById(R.id.imageView10);
                Menu2.setVisibility(View.VISIBLE);
                ImageView Menu3 = (ImageView)findViewById(R.id.imageView11);
                Menu3.setVisibility(View.VISIBLE);
                ImageView Menu4 = (ImageView)findViewById(R.id.imageView12);
                Menu4.setVisibility(View.VISIBLE);
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView9);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==9) {
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView10);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==10) {
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView11);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }else if(i==11) {
                ImageView Menu1 = (ImageView)findViewById(R.id.imageView12);
                Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/" + bill_img));
                Menu1.setVisibility(View.VISIBLE);
                Menu1.setTag(bill_code);
            }
        }
        resetDisconnectTimer();
    }

    public void set_amt(View view) {
        if(isInternetAvailable()) {
            String amt = view.getTag().toString();
            app_amt = Integer.parseInt(amt);
            String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=machine", "username=" + storedPreferenceUsername + "&token=" + storedPreferenceToken + "&sn=" + storedPreferenceSN);
            String[] separated = jsonString.split("user");
            String[] separated2 = separated[1].split("credits");
            String[] separated3 = separated2[1].split("\"");
            String balance = separated3[2];
            double balance_int = Double.parseDouble(balance);
            if (balance_int < Integer.parseInt(amt)) {
                setContentView(R.layout.topup_error_credit);
            } else {
                setContentView(R.layout.topup_number);
                if (app_lang == 1) {
                    MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_number);
                    mPlayer.start();
                } else {
                    MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.number);
                    mPlayer.start();
                }
                coin_data = String.valueOf("0");
            }
        }else{
            setContentView(R.layout.main_screen);
        }
        resetDisconnectTimer();
    }
    public void set_card_amt(View view) {
        card_ft=0;
        coin_status = 0;
        coin_data = String.valueOf("0");
        coin_control(0);
        String amt = view.getTag().toString();
        app_amt = Integer.parseInt(amt);
        String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=machine","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        String[] separated = jsonString.split("user");
        String[] separated2 = separated[1].split("credits");
        String[] separated3 = separated2[1].split("\"");
        String balance=separated3[2];
        double balance_int=Double.parseDouble(balance);
        if(balance_int<Integer.parseInt(amt)) {
            setContentView(R.layout.topup_error_credit);
        }else{
            setContentView(R.layout.card_number);
        }
        resetDisconnectTimer();

    }
    public void set_promotion_amt(View view) {
        promotion_ft=0;
        coin_status = 0;
        coin_data = String.valueOf("0");
        coin_control(0);
        String amt = view.getTag().toString();
        app_amt = Integer.parseInt(amt);
        String oper = getDataFromUrl("https://member.welovetopupline.com/webservice/promotion_request_id.php","id="+app_oper+"&amt="+app_amt+"&username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        if (TextUtils.isEmpty(oper)) {
            setContentView(R.layout.main_screen);
        }else {
            app_card_oper = oper;
        }
        String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=machine","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        if (TextUtils.isEmpty(jsonString)) {
            setContentView(R.layout.main_screen);
        }else {
            String[] separated = jsonString.split("user");
            String[] separated2 = separated[1].split("credits");
            String[] separated3 = separated2[1].split("\"");
            String balance = separated3[2];
            double balance_int = Double.parseDouble(balance);
            if (balance_int < Integer.parseInt(amt)) {
                setContentView(R.layout.topup_error_credit);
            } else {
                setContentView(R.layout.promotion_number);
            }
        }
        resetDisconnectTimer();

    }
    public void set_bill_amt(View view) {
        bill_ft=0;
        coin_status = 0;
        coin_data = String.valueOf("0");
        coin_control(0);
        bill_chk_ref1="";
        bill_chk_ref2="";
        bill_chk_ref3="";
        bill_chk_ref4="";
        bill_chk_ref5="";
        bill_ref1="";
        bill_ref2="";
        bill_ref3="";
        bill_ref4="";
        bill_ref5="";
        bill_chk_ref = 1;
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String amt = textNum.getText().toString();
        if(amt.equals("")){
            Toast.makeText(mContext,"กรุณากรอกตัวเลข",Toast.LENGTH_LONG).show();
        }else {
            bill_amt = Double.parseDouble(amt);
            String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=machine","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
            if (TextUtils.isEmpty(jsonString)) {
                setContentView(R.layout.main_screen);
            }else {
                //Toast.makeText(mContext,"Amt "+bill_amt,Toast.LENGTH_LONG).show();
                String[] separated = jsonString.split("user");
                String[] separated2 = separated[1].split("credits");
                String[] separated3 = separated2[1].split("\"");
                String balance = separated3[2];
                double balance_int = Double.parseDouble(balance) - 15;
                if (balance_int < bill_amt) {
                    setContentView(R.layout.topup_error_credit);
                } else {
                    String bill_chk_ref = getTopupFromUrl("chk_bill_ref", app_oper, "");
                    String[] Chk_ref = bill_chk_ref.split(",");
                    bill_chk_ref1 = Chk_ref[0];
                    bill_chk_ref2 = Chk_ref[1];
                    bill_chk_ref3 = Chk_ref[2];
                    bill_chk_ref4 = Chk_ref[3];
                    bill_chk_ref5 = Chk_ref[4];
                    setContentView(R.layout.bill_ref);
                    TextView myAwesomeTextView = (TextView) findViewById(R.id.ref_detail);
                    myAwesomeTextView.setText("กรุณากรอก : " + bill_chk_ref1);

                }
            }
        }
        resetDisconnectTimer();

    }
    public void set_bill_ref(View view) {
        /// Save Ref.
        EditText textNum = (EditText)findViewById(R.id.txtNum);
        String ref = textNum.getText().toString();
        if(bill_chk_ref==1){
            bill_ref1=ref;
        }else if(bill_chk_ref==2){
            bill_ref2=ref;
        }else if(bill_chk_ref==3){
            bill_ref3=ref;
        }else if(bill_chk_ref==4){
            bill_ref4=ref;
        }else if(bill_chk_ref==5){
            bill_ref5=ref;
        }
        bill_date=0;
        if(!bill_chk_ref1.equals(" ") && bill_ref1.equals("")){
            TextView myAwesomeTextView = (TextView)findViewById(R.id.ref_detail);
            myAwesomeTextView.setText("กรุณากรอก : "+bill_chk_ref1);
            textNum.setText("");
            bill_chk_ref=1;
        }else if(!bill_chk_ref2.equals(" ") && bill_ref2.equals("")){
            TextView myAwesomeTextView = (TextView)findViewById(R.id.ref_detail);
            myAwesomeTextView.setText("กรุณากรอก : "+bill_chk_ref2);
            textNum.setText("");
            bill_chk_ref=2;
        }else if(!bill_chk_ref3.equals(" ") && bill_ref3.equals("")){
            TextView myAwesomeTextView = (TextView)findViewById(R.id.ref_detail);
            myAwesomeTextView.setText("กรุณากรอก : "+bill_chk_ref3);
            textNum.setText("");
            bill_chk_ref=3;
        }else if(!bill_chk_ref4.equals(" ") && bill_ref4.equals("")){
            TextView myAwesomeTextView = (TextView)findViewById(R.id.ref_detail);
            myAwesomeTextView.setText("กรุณากรอก : "+bill_chk_ref4);
            textNum.setText("");
            bill_chk_ref=4;
        }else if(!bill_chk_ref5.equals(" ") && bill_ref5.equals("")){
            TextView myAwesomeTextView = (TextView)findViewById(R.id.ref_detail);
            myAwesomeTextView.setText("กรุณากรอก : "+bill_chk_ref5);
            textNum.setText("");
            bill_date=1;
            bill_chk_ref=5;
        }else{
            setContentView(R.layout.bill_number);
        }
    }
    public void bill_main(View view) {
        coin_control(0);
        light_control(1);
        coin_data="0";
        coin_status=0;

        //Toast.makeText(mContext,"Cards",Toast.LENGTH_LONG).show();
        setContentView(R.layout.bill_list);
    }
    public void cards_tot(View view) {
        setContentView(R.layout.main_topup);
        app_card_oper = "23";
        coin_control(0);
        resetDisconnectTimer();
    }
    public void cards_main(View view) {
        //Toast.makeText(mContext,"Cards",Toast.LENGTH_LONG).show();
        setContentView(R.layout.cards_screen);
        light_control(1);
        ImageView Menu5 = (ImageView)findViewById(R.id.menu5);
        ImageView Menu15 = (ImageView)findViewById(R.id.menu15);
        ImageView Menu16 = (ImageView)findViewById(R.id.menu16);
        ImageView Menu17 = (ImageView)findViewById(R.id.menu17);
        ImageView Menu18 = (ImageView)findViewById(R.id.menu18);
        ImageView Menu19 = (ImageView)findViewById(R.id.menu19);
        ImageView Menu20 = (ImageView)findViewById(R.id.menu20);
        ImageView Menu21 = (ImageView)findViewById(R.id.menu21);
        ImageView Menu22 = (ImageView)findViewById(R.id.menu22);
        ImageView Menu23 = (ImageView)findViewById(R.id.menu23);
        ImageView Menu24 = (ImageView)findViewById(R.id.menu24);
        ImageView Menu25 = (ImageView)findViewById(R.id.menu25);
        ImageView Menu26 = (ImageView)findViewById(R.id.menu26);
        ImageView Menu27 = (ImageView)findViewById(R.id.menu27);
        ImageView Menu28 = (ImageView)findViewById(R.id.menu28);
        ImageView Menu29 = (ImageView)findViewById(R.id.menu29);
        ImageView Menu30 = (ImageView)findViewById(R.id.menu30);
        ImageView Menu31 = (ImageView)findViewById(R.id.menu31);
        ImageView Menu32 = (ImageView)findViewById(R.id.menu32);
        ImageView Menu33 = (ImageView)findViewById(R.id.menu33);
        ImageView Menu34 = (ImageView)findViewById(R.id.menu34);
        ImageView Menu35 = (ImageView)findViewById(R.id.menu35);
        ImageView Menu36 = (ImageView)findViewById(R.id.menu36);
        ImageView Menu37 = (ImageView)findViewById(R.id.menu37);
        ImageView Menu38 = (ImageView)findViewById(R.id.menu38);
        ImageView Menu39 = (ImageView)findViewById(R.id.menu39);
        ImageView Menu40 = (ImageView)findViewById(R.id.menu40);
        ImageView Menu41 = (ImageView)findViewById(R.id.menu41);
        ImageView Menu42 = (ImageView)findViewById(R.id.menu42);
        ImageView Menu43 = (ImageView)findViewById(R.id.menu43);
        ImageView Menu44 = (ImageView)findViewById(R.id.menu44);
        ImageView Menu45 = (ImageView)findViewById(R.id.menu45);
        ImageView Menu46 = (ImageView)findViewById(R.id.menu46);
        ImageView Menu47 = (ImageView)findViewById(R.id.menu47);
        ImageView Menu48 = (ImageView)findViewById(R.id.menu48);
        ImageView Menu49 = (ImageView)findViewById(R.id.menu49);
        ImageView Menu50 = (ImageView)findViewById(R.id.menu50);
        ImageView Menu51 = (ImageView)findViewById(R.id.menu51);


        String jsonString = getDataFromUrl("https://www.totpayonline.com/system/api.php?cmd=card_request","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN);
        if (TextUtils.isEmpty(jsonString)) {
            setContentView(R.layout.main_screen);
        }else {
            String[] oper_separated = jsonString.split(",");
            for (byte i = 0; i < oper_separated.length; i++) {
                String[] oper_separated_data = oper_separated[i].split("-");

                int data_id = Integer.parseInt(oper_separated_data[0].toString());
                int data_status = Integer.parseInt(oper_separated_data[1].toString());

                if (data_id == 5) {
                    Imageview_Status(data_status, Menu5);
                } else if (data_id == 15) {
                    Imageview_Status(data_status, Menu15);
                } else if (data_id == 16) {
                    Imageview_Status(data_status, Menu16);
                } else if (data_id == 17) {
                    Imageview_Status(data_status, Menu17);
                } else if (data_id == 18) {
                    Imageview_Status(data_status, Menu18);
                } else if (data_id == 19) {
                    Imageview_Status(data_status, Menu19);
                } else if (data_id == 20) {
                    Imageview_Status(data_status, Menu20);
                } else if (data_id == 21) {
                    Imageview_Status(data_status, Menu21);
                } else if (data_id == 22) {
                    Imageview_Status(data_status, Menu22);
                } else if (data_id == 23) {
                    Imageview_Status(data_status, Menu23);
                } else if (data_id == 24) {
                    Imageview_Status(data_status, Menu24);
                } else if (data_id == 25) {
                    Imageview_Status(data_status, Menu25);
                } else if (data_id == 26) {
                    Imageview_Status(data_status, Menu26);
                } else if (data_id == 27) {
                    Imageview_Status(data_status, Menu27);
                } else if (data_id == 28) {
                    Imageview_Status(data_status, Menu28);
                } else if (data_id == 29) {
                    Imageview_Status(data_status, Menu29);
                } else if (data_id == 30) {
                    Imageview_Status(data_status, Menu30);
                } else if (data_id == 31) {
                    Imageview_Status(data_status, Menu31);
                } else if (data_id == 32) {
                    Imageview_Status(data_status, Menu32);
                } else if (data_id == 33) {
                    Imageview_Status(data_status, Menu33);
                } else if (data_id == 34) {
                    Imageview_Status(data_status, Menu34);
                } else if (data_id == 35) {
                    Imageview_Status(data_status, Menu35);
                } else if (data_id == 36) {
                    Imageview_Status(data_status, Menu36);
                } else if (data_id == 37) {
                    Imageview_Status(data_status, Menu37);
                } else if (data_id == 38) {
                    Imageview_Status(data_status, Menu38);
                } else if (data_id == 39) {
                    Imageview_Status(data_status, Menu39);
                } else if (data_id == 40) {
                    Imageview_Status(data_status, Menu40);
                } else if (data_id == 41) {
                    Imageview_Status(data_status, Menu41);
                } else if (data_id == 42) {
                    Imageview_Status(data_status, Menu42);
                } else if (data_id == 43) {
                    Imageview_Status(data_status, Menu43);
                } else if (data_id == 44) {
                    Imageview_Status(data_status, Menu44);
                } else if (data_id == 45) {
                    Imageview_Status(data_status, Menu45);
                } else if (data_id == 46) {
                    Imageview_Status(data_status, Menu46);
                } else if (data_id == 47) {
                    Imageview_Status(data_status, Menu47);
                } else if (data_id == 48) {
                    Imageview_Status(data_status, Menu48);
                } else if (data_id == 49) {
                    Imageview_Status(data_status, Menu49);
                } else if (data_id == 50) {
                    Imageview_Status(data_status, Menu50);
                } else if (data_id == 51) {
                    Imageview_Status(data_status, Menu51);
                }
            }
        }
        resetDisconnectTimer();
    }
    public void promotion_main(View view) {
        //Toast.makeText(mContext,"Cards",Toast.LENGTH_LONG).show();
        setContentView(R.layout.promotion_oper);
        if(app_lang==0) {
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.network);
            mPlayer.start();
        }else{
            MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_network);
            mPlayer.start();
        }
        light_control(1);
        resetDisconnectTimer();

    }

    public void post_data(String data){
        //Toast.makeText(this, "DATA : "+data, Toast.LENGTH_SHORT).show();
        if(data.indexOf("OK") >= 0){
            board_connect=0;
            board_connect_sw = 1;
            //Toast.makeText(this, "Count : "+board_connect, Toast.LENGTH_SHORT).show();
        }
        if(data.indexOf("ONLINE") >= 0){
            board_count=0;
            board_count_status=1;
            //Toast.makeText(this, "Count : "+board_connect, Toast.LENGTH_SHORT).show();
        }
        String[] separated = data.split(",");
        //coin_data="0";
        if (separated.length == 2) {
            String coin = separated[0];
            String bank = "";
            int coin_in = Integer.parseInt(coin.replaceAll("[\\D]", ""));
            //do something
            bank = separated[1];
            int bank_in = Integer.parseInt(bank.replaceAll("[\\D]", ""));
            int coin_total = coin_in + bank_in;
            if(water_fc==1){
                int coint_data_old = Integer.parseInt(coin_data);
                if(coin_total>coint_data_old){
                    int coint_data_diff = coin_total-coint_data_old;
                    String jsonString = getDataFromUrl("https://b.welovetopup.com/api/v1/services/vendingcredit","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN+"&service_id=122&coinbanknote="+coint_data_diff);
                }
            }else if(pump_fc==1){
                int coint_data_old = Integer.parseInt(coin_data);
                if(coin_total>coint_data_old){
                    int coint_data_diff = coin_total-coint_data_old;
                    String jsonString = getDataFromUrl("https://b.welovetopup.com/api/v1/services/vendingcredit","username="+storedPreferenceUsername+"&token="+storedPreferenceToken+"&sn="+storedPreferenceSN+"&service_id=125&coinbanknote="+coint_data_diff);
                }
            }
            coin_data = String.valueOf(coin_total);
        }else if (separated.length == 3) {
            String weight = separated[0];
            weight_data = String.valueOf(weight);
        }

    }
    public  boolean isPermissionGranted1() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");

                return true;

            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }
    public  boolean isPermissionGranted2() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED&&checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");


                return true;

            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(mContext,"No Internet connection",Toast.LENGTH_LONG).show();
            if(failingUrl.indexOf("/api/") >= 0){
                app_startup();

            }else if(failingUrl.indexOf("cmd=") >= 0){
                app_startup();
            }

            setContentView(R.layout.main_screen);
        }
        @Override
        public void onLoadResource(WebView view, String url){
            if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=chkcoin")) {
                coin_control(2);
            }else if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=oncoin")) {
                coin_control(1);
            }else if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=offcoin")) {
                coin_control(0);
            }else if (Uri.parse(url).toString().equals("file:///android_asset/topup.html?cmd=chkcoin")) {
                coin_control(2);
            }else if (Uri.parse(url).toString().equals("file:///android_asset/topup.html?cmd=oncoin")) {
                coin_control(1);
            }else if (Uri.parse(url).toString().equals("file:///android_asset/topup.html?cmd=offcoin")) {
                coin_control(0);
            }else if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=onpump")) {
                pump_control(1);
            }else if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=offpump")) {
                pump_control(0);
            }else if (url.indexOf("report.php") >= 0) {
                setContentView(R.layout.report_screen);
            }
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=exit")) {
                // Designate Urls that you want to load in WebView still.
                System.exit(0);
                return false;
            }else if (Uri.parse(url).toString().equals("wltl://reload")) {
                // Designate Urls that you want to load in WebView still.
                app_startup();
                return false;
            }else if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=chkcoin")) {
                // Designate Urls that you want to load in WebView still.
                if (usbService != null) { // if UsbService was correctly binded, Send data
                    usbService.write((":coin=?\n").getBytes());
                }
                return false;
            }else if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=oncoin")) {
                // Designate Urls that you want to load in WebView still.
                if (usbService != null) { // if UsbService was correctly binded, Send data
                    usbService.write((":coin=1\n").getBytes());
                }
                return false;
            }else if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=offcoin")) {
                // Designate Urls that you want to load in WebView still.
                if (usbService != null) { // if UsbService was correctly binded, Send data
                    usbService.write((":coin=0\n").getBytes());
                }
                return false;
            }else if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=onpump")) {
                // Designate Urls that you want to load in WebView still.
                if (usbService != null) { // if UsbService was correctly binded, Send data
                    usbService.write((":pump=1\n").getBytes());
                }
                return false;
            }else if (Uri.parse(url).toString().equals("https://member.welovetopupline.com/m/?cmd=offpump")) {
                // Designate Urls that you want to load in WebView still.
                if (usbService != null) { // if UsbService was correctly binded, Send data
                    usbService.write((":pump=0\n").getBytes());
                }
                return false;
            }else if (Uri.parse(url).toString().equals("file:///android_asset/menu3d.html?water")) {
                water_fc = 1;
                coin_control(1);
                light_control(1);
                setContentView(R.layout.water_screen);
                return false;
            }else if (Uri.parse(url).toString().equals("file:///android_asset/menu3d.html?airpump")) {
                pump_fc = 1;
                coin_control(1);
                light_control(1);
                setContentView(R.layout.airpump);
                return false;
            }else if (Uri.parse(url).toString().equals("file:///android_asset/menu3d.html?weight")) {
                weight_fc = 1;
                coin_status = 1;
                coin_control(1);
                light_control(1);
                setContentView(R.layout.wieght);
                return false;
            }else if (Uri.parse(url).toString().equals("file:///android_asset/menu3d.html?washing")) {
                sendMessage2(":wash_E=1");
                setContentView(R.layout.washing_menu);
                ImageView Menu1 = (ImageView) findViewById(R.id.Menu1);
                ImageView Menu2 = (ImageView) findViewById(R.id.Menu2);
                ImageView Menu3 = (ImageView) findViewById(R.id.Menu3);
                ImageView Menu4 = (ImageView) findViewById(R.id.Menu4);
                ImageView Menu5 = (ImageView) findViewById(R.id.Menu5);
                ImageView Menu6 = (ImageView) findViewById(R.id.Menu6);
                ImageView Menu7 = (ImageView) findViewById(R.id.Menu7);
                ImageView Menu8 = (ImageView) findViewById(R.id.Menu8);
                TextView txtWash1 = (TextView) findViewById(R.id.textView1);
                TextView txtWash2 = (TextView) findViewById(R.id.textView2);
                TextView txtWash3 = (TextView) findViewById(R.id.textView3);
                TextView txtWash4 = (TextView) findViewById(R.id.textView4);
                TextView txtWash5 = (TextView) findViewById(R.id.textView5);
                TextView txtWash6 = (TextView) findViewById(R.id.textView6);
                TextView txtWash7 = (TextView) findViewById(R.id.textView7);
                TextView txtWash8 = (TextView) findViewById(R.id.textView8);
                if(wash1_price>0){
                    txtWash1.setText("# 1 ราคา "+wash1_price+" บาท");
                    String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php","mc=1&uuid="+storedPreferenceSN);
                    int wash1_status = Integer.parseInt(jsonString);
                    if(wash1_status==0) {
                        Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                        Menu1.setTag("1");
                    }else{
                        Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                        Menu1.setTag("0");
                    }
                }else{
                    txtWash1.setText("");
                    Menu1.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
                    Menu1.setTag("0");
                }
                if(wash2_price>0){
                    txtWash2.setText("# 2 ราคา "+wash2_price+" บาท");
                    String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php","mc=2&uuid="+storedPreferenceSN);
                    int wash2_status = Integer.parseInt(jsonString);
                    if(wash2_status==0) {
                        Menu2.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                        Menu2.setTag("2");
                    }else{
                        Menu2.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                        Menu2.setTag("0");
                    }
                }else{
                    txtWash2.setText("");
                    Menu2.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
                    Menu2.setTag("0");
                }
                if(wash3_price>0){
                    txtWash3.setText("# 3 ราคา "+wash3_price+" บาท");
                    String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php","mc=3&uuid="+storedPreferenceSN);
                    int wash3_status = Integer.parseInt(jsonString);
                    if(wash3_status==0) {
                        Menu3.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                        Menu3.setTag("3");
                    }else{
                        Menu3.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                        Menu3.setTag("0");
                    }
                }else{
                    txtWash3.setText("");
                    Menu3.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
                    Menu3.setTag("0");
                }
                if (wash4_price > 0) {
                    txtWash4.setText("# 4 ราคา " + wash4_price + " บาท");
                    String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=4&uuid=" + storedPreferenceSN);
                    int wash4_status = Integer.parseInt(jsonString);
                    if (wash4_status == 0) {
                        Menu4.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                        Menu4.setTag("4");
                    } else {
                        Menu4.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                        Menu4.setTag("0");
                    }
                } else {
                    txtWash4.setText("");
                    Menu4.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
                    Menu4.setTag("0");
                }
                if (wash5_price > 0) {
                    txtWash5.setText("# 5 ราคา " + wash5_price + " บาท");
                    String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=5&uuid=" + storedPreferenceSN);
                    int wash5_status = Integer.parseInt(jsonString);
                    if (wash5_status == 0) {
                        Menu5.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                        Menu5.setTag("5");
                    } else {
                        Menu5.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                        Menu5.setTag("0");
                    }
                } else {
                    txtWash5.setText("");
                    Menu5.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
                    Menu5.setTag("0");
                }
                if (wash6_price > 0) {
                    txtWash6.setText("# 6 ราคา " + wash6_price + " บาท");
                    String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=6&uuid=" + storedPreferenceSN);
                    int wash6_status = Integer.parseInt(jsonString);
                    if (wash6_status == 0) {
                        Menu6.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                        Menu6.setTag("6");
                    } else {
                        Menu6.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                        Menu6.setTag("0");
                    }
                } else {
                    txtWash6.setText("");
                    Menu6.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
                    Menu6.setTag("0");
                }
                if (wash7_price > 0) {
                    txtWash7.setText("# 7 ราคา " + wash7_price + " บาท");
                    String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=7&uuid=" + storedPreferenceSN);
                    int wash7_status = Integer.parseInt(jsonString);
                    if (wash7_status == 0) {
                        Menu7.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                        Menu7.setTag("7");
                    } else {
                        Menu7.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                        Menu7.setTag("0");
                    }
                } else {
                    txtWash7.setText("");
                    Menu7.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
                    Menu7.setTag("0");
                }
                if (wash8_price > 0) {
                    txtWash8.setText("# 8 ราคา " + wash6_price + " บาท");
                    String jsonString = getDataFromUrl("https://member.welovetopupline.com/webservice/wash.php", "mc=8&uuid=" + storedPreferenceSN);
                    int wash8_status = Integer.parseInt(jsonString);
                    if (wash8_status == 0) {
                        Menu8.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine1"));
                        Menu8.setTag("8");
                    } else {
                        Menu8.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine2"));
                        Menu8.setTag("0");
                    }
                } else {
                    txtWash8.setText("");
                    Menu8.setImageURI(Uri.parse("android.resource://" + getPackageName() + "/mipmap/machine3"));
                    Menu8.setTag("0");
                }

                return false;
            }else if (Uri.parse(url).toString().equals("file:///android_asset/topup.html")) {
                if(app_lang==1){
                    MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_welocome);
                    mPlayer.start();
                }else{
                    MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.welcome);
                    mPlayer.start();
                }
                setContentView(R.layout.main_topup_tot);
                light_control(1);
                TextView myAwesomeTextView = (TextView)findViewById(R.id.textView);
                myAwesomeTextView.setText(getMachineFromUrl());


                return false;
            }else if (Uri.parse(url).toString().equals("file:///android_asset/menu3d.html?lang=mm")) {
                app_lang=1;
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_welocome);
                mPlayer.start();
                return false;
            }else if (Uri.parse(url).toString().equals("file:///android_asset/menu3d_topup.html?lang=mm")) {
                app_lang=1;
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.mm_welocome);
                mPlayer.start();
                return false;
            }else if (Uri.parse(url).toString().equals("file:///android_asset/menu3d.html?lang=th")) {
                app_lang=0;
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.welcome);
                mPlayer.start();
                return false;
            }else if (Uri.parse(url).toString().equals("file:///android_asset/menu3d_topup.html?lang=th")) {
                app_lang=0;
                MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.welcome);
                mPlayer.start();
                return false;
            }else {
                Log.d("MainActivity", Uri.parse(url).toString());

                WebView myWebView = (WebView) findViewById(R.id.webView);
                myWebView.setWebViewClient(new MyWebViewClient());
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                myWebView.loadUrl(Uri.parse(url).toString());
            }
            return true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        if (usbService != null) { // if UsbService was correctly binded, Send data
            usbService.write((":coin=?\n").getBytes());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        private WebView mWebview;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    mActivity.get().post_data(data);
                    //
                    //Toast.makeText(mActivity.get(), "DATA = 1", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }

    }
}
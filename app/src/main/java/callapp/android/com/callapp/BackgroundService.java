package callapp.android.com.callapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;


import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.Date;


/**
 * Created by Jerry on 1/5/2018.
 */

public class BackgroundService extends Service implements View.OnTouchListener{

//    private CallReceiver mReceiver = null;
private static final int mLayoutParamFlags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

    // Views
    private View mDemoView;
    IntentFilter intentFilter;
    private View topLeftView;

    private Button overlayedButton;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager wm;


    private static final String TAG = BackgroundService.class.getSimpleName();
    WindowManager mWindowManager;
    View mView;
    Animation mAnimation;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //This is a thread that stays alive for as long as you need
//        new CheckActivityStatus().execute();
        // Create an IntentFilter instance.

//        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//
//        overlayedButton = new Button(this);
//        overlayedButton.setText("Overlay button");
//        overlayedButton.setOnTouchListener(this);
////        overlayedButton.setAlpha(0.0f);
//        overlayedButton.setVisibility(View.VISIBLE);
//        overlayedButton.setBackgroundColor(Color.BLACK);
////        overlayedButton.setOnClickListener(this);
//
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
//        params.gravity = Gravity.CENTER;
//        params.x = 0;
//        params.y = 0;
//        wm.addView(overlayedButton, params);
//
//        topLeftView = new View(this);
//        WindowManager.LayoutParams topLeftParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
//        topLeftParams.gravity = Gravity.CENTER ;
//        topLeftParams.x = 0;
//        topLeftParams.y = 0;
//        topLeftParams.width = 0;
//        topLeftParams.height = 0;
//        wm.addView(topLeftView, topLeftParams);

//*FOR TRIAL USEE THIS
//        HandlerThread handlerThread = new HandlerThread("HandlerThread");
//        handlerThread.start();
//        final Handler handler = new Handler(handlerThread.getLooper());
//        Runnable runnable = new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                intentToUnistallApk(getApplicationContext());
//                handler.postDelayed(this, 1000*60);
//            }
//        };
//        handler.postDelayed(runnable, 1000*60*60);

        intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
//        intentFilter.addAction("android.intent.action.SCREEN_OFF");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        mReceiver = new CallReceiver();

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(mReceiver, intentFilter);
//        Toast.makeText(this, "Service on create", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
//            Toast.makeText(this, "Start Stop", Toast.LENGTH_SHORT).show();
        }
    }
    private PhonecallReceiver mReceiver=new PhonecallReceiver() {
        @Override
        protected void onIncomingCallStarted(Context ctx, String number, Date start) {

//showDialog("incoming Call");
//            showAlertDialog(ctx,number,"incoming Call");

//            Toast.makeText(ctx, "onIncomingCall" +number, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
//            showAlertDialog(ctx,number,"CallEnded");

//            Toast.makeText(ctx, "CallEnded" +number, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onMissedCall(Context ctx, String number, Date start) {
//            showAlertDialog(ctx,"Missed Call",number);
        }
    };

    private void showNotificationDialog(Context ctx, String number,String msg) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx)


                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.ic_launcher_background))
                .setContentTitle("Alert")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg+"\n"+number))
                .setContentText(msg+"\n"+number)
                .setAutoCancel(true);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }

    private void showAlertDialog(Context context, String msg, String number)
    {


        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(true);
        dialog.setTitle(msg);
        dialog.setMessage(""+number);


        final AlertDialog alert = dialog.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        alert.getWindow().addFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//        );
        alert.show();


    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getRawX();
            float y = event.getRawY();

            moving = false;

            int[] location = new int[2];
            overlayedButton.getLocationOnScreen(location);

            originalXPos = location[0];
            originalYPos = location[1];

            offsetX = originalXPos - x;
            offsetY = originalYPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            topLeftView.getLocationOnScreen(topLeftLocationOnScreen);

            System.out.println("topLeftY="+topLeftLocationOnScreen[1]);
            System.out.println("originalY="+originalYPos);

            float x = event.getRawX();
            float y = event.getRawY();

            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayedButton.getLayoutParams();

            int newX = (int) (offsetX + x);
            int newY = (int) (offsetY + y);

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false;
            }

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);

            wm.updateViewLayout(overlayedButton, params);
            moving = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (moving) {
                return true;
            }
        }

        return false;
    }
    /**
     * Show notification
     */
    private void showNotification(Context context) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Demo App")
                .setContentText("Demo Message");

        startForeground(9999, notificationBuilder.build());
    }
    private void showDialog(String aTitle){
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mView = View.inflate(getApplicationContext(), R.layout.fragment_overlay, null);
        mView.setTag(TAG);

        int top = getApplicationContext().getResources().getDisplayMetrics().heightPixels / 2;

        RelativeLayout dialog = (RelativeLayout) mView.findViewById(R.id.dialog);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) dialog.getLayoutParams();
        lp.topMargin = top;
        lp.bottomMargin = top;
        mView.setLayoutParams(lp);

//        ImageButton imageButton = (ImageButton) mView.findViewById(R.id.close);
//        lp = (RelativeLayout.LayoutParams) imageButton.getLayoutParams();
//        lp.topMargin = top - 58;
//        imageButton.setLayoutParams(lp);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mView.setVisibility(View.INVISIBLE);
//            }
//        });

        TextView title = (TextView) mView.findViewById(R.id.Title);

        title.setText(aTitle);

        final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON ,
                PixelFormat.RGBA_8888);

        mView.setVisibility(View.VISIBLE);
//        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.in);
//        mView.startAnimation(mAnimation);
        mWindowManager.addView(mView, mLayoutParams);

    }

    public boolean uninstallPackage(Context context, String packageName) {
        ComponentName name = new ComponentName(packageName, MainActivity.class.getCanonicalName());
        PackageManager packageManger = context.getPackageManager();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            PackageInstaller packageInstaller = packageManger.getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            params.setAppPackageName(packageName);
            int sessionId = 0;
            try {
                sessionId = packageInstaller.createSession(params);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            packageInstaller.uninstall(packageName, PendingIntent.getBroadcast(context, sessionId,
                    new Intent("android.intent.action.MAIN"), 0).getIntentSender());

            Toast.makeText(context, "Trial Finish", Toast.LENGTH_SHORT).show();
            return true;
        }
        System.err.println("old sdk");
        return false;
    }

    private void intentToUnistallApk(Context context) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        //Enter app package name that app you wan to install
        intent.setData(Uri.parse("package:callapp.android.com.callapp"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
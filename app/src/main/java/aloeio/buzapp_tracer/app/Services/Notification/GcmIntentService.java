package aloeio.buzapp_tracer.app.Services.Notification;

import aloeio.buzapp_tracer.app.MainActivity;
import aloeio.buzapp_tracer.app.Services.BackgroundService;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import aloeio.buzapp_tracer.app.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by root on 9/20/15.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private String msgTitle="Aviso Buzapp";
    private String msgContent="Aviso Buzapp";

    private String postPushRoute="";
    private String postPushID="";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);


        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType) && !extras.containsKey("CMD")) {
            //extrai titulo e conteudo da msg
            notificationProcess(extras.toString());
            sendNotification(msgContent);
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.buzapp_menu_icon))
                            .setSmallIcon(R.mipmap.ic_buzapp)
                            .setContentTitle(msgTitle)
                            .setVibrate(new long[] { 0, 500, 100, 500})
                            .setSound(uri)
                            .setColor(getResources().getColor(R.color.light_dark_blue))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg))
                            .setContentText(msg);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());



    }

    private void notificationProcess(String message)
    {
        msgTitle="";
        msgContent="";
        int init;
        init = message.indexOf("title=");
        init+=6;
        char initial=message.charAt(init);


        while(message.charAt(init)!=',') {
            msgTitle += message.charAt(init);
            init++;
        }
        if(msgTitle.equals("GET"))
            BackgroundService.getDeviceInfo();

        else if(msgTitle.equals("UPDATE"))
        {
            postNotificationProcess(message);
            BackgroundService.updateBusInfo(postPushRoute,postPushID);
        }

        else if(msgTitle.equals("MOBIZEN"))
            BackgroundService.launchMobizen();




        init = message.indexOf("message=");
        init+=8;

        while(message.charAt(init)!=',') {
            msgContent += message.charAt(init);
            init++;
        }

    }

    private void postNotificationProcess(String message) {

        int init;
        init = message.indexOf("newRoute=");
        init+=9;
        char initial=message.charAt(init);

        while(message.charAt(init)!=',') {
            postPushRoute += message.charAt(init);
            init++;
        }

        init = message.indexOf("newID=");
        init+=6;

        while(message.charAt(init)!=',') {
            postPushID += message.charAt(init);
            init++;
        }

    }


}

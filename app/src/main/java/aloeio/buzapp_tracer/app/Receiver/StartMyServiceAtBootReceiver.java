package aloeio.buzapp_tracer.app.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import aloeio.buzapp_tracer.app.Services.BackgroundService;

/**
 * Created by root on 07/09/15.
 */
public class StartMyServiceAtBootReceiver
        extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, BackgroundService.class);
            context.startService(serviceIntent);
        }
    }
}

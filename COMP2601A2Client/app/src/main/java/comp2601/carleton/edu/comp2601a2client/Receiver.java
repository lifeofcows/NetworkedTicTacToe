package comp2601.carleton.edu.comp2601a2client;

/**
 * Created by tonywhite on 2017-01-28.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MessageService.CUSTOM_INTENT)) {
            Message data = (Message) intent.getSerializableExtra("myKey");
            if (data.header.type.equals("CONNECTED_RESPONSE")) {
                MainActivity.getInstance().showConnectedToast();
            }
            else if (data.header.type.equals("USERS_UPDATED")) {
                //Updates list of users currently connected
            }
        }
    }
}

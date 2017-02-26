package comp2601.carleton.edu.comp2601a2client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public MessageService s;
    static MainActivity mainInstance;
    TextView userStatus;
    Toast connected;
    String userName;
    public static boolean isFirst;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainInstance = this;

        userStatus = (TextView) findViewById(R.id.userStatus);

        isFirst = true; //assume true in beginning
        //popup modal dialogue/toast and prompt for user name, store name and connect (send CONNECT_REQUEST)

        userName = "Bob"; //change to modal dialog input

        //implement lazyadapter after you get stuff working

        s = new MessageService();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   // s.connect(address.getText().toString(), Integer.parseInt(port.getText().toString()), name.getText().toString());
                    Message message = new Message();
                    message.header.type = "CONNECT_REQUEST";
                    message.header.id = userName;
                    s.request(message);
                    //display progress spinner until receiver responds
                } catch (Exception e) {
                    System.out.println("IOException occurred");
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static MainActivity getInstance() {
        return mainInstance;
    }

    public void playGameRequest(String requester) {
      //show modal dialog (requester wants to play game)
      Message message = new Message();
      message.header.type = "PLAY_GAME_RESPONSE";
      message.header.id = userName;
      if (true) {
          message.header.play = true;
      }
        else {
          message.header.play = false;
      }
      s.request(message);
      if (message.header.play == true) {
        isFirst = false;
        Intent game = new Intent(this, GameActivity.class);
        startActivity(game);
      }
    }

    public void playGameResponse(String opponentName, boolean play) {
      if (play) { //start new intent with game activity_main
        Intent game = new Intent(this, GameActivity.class);
        startActivity(game);
      }
      else {
        String doesNotWantToPlay = opponentName + " does not want to play";
        userStatus.setText(doesNotWantToPlay);
      }
    }

    public void showConnectedToast() {
        connected.setDuration(Toast.LENGTH_SHORT);
        connected.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            MessageService.MyBinder b = (MessageService.MyBinder) binder;
            s = b.getService();
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT)
                    .show();
        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
        }
    };

}

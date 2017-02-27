package comp2601.carleton.edu.comp2601a2client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public MessageService s;
    static MainActivity mainInstance;
    TextView userStatus;
    Toast connected;
    String address, userName;
    int port;
    public static boolean isFirst;
    ProgressDialog dialog1;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> values;
    private Handler mHandler;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address = "192.168.0.17";
        port = 7000;

        mHandler = new Handler();

        mainInstance = this;

        userStatus = (TextView) findViewById(R.id.userStatus);

        isFirst = true; //assume true in beginning
        //popup modal dialogue/toast and prompt for user name, store name and connect (send CONNECT_REQUEST)

        System.out.println("Started");

        s = new MessageService();

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // Defined Array values to show in ListView
        values = new ArrayList<String>();
        values.add("meme");

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String  userClicked    = (String) listView.getItemAtPosition(position);

                Message message = new Message();
                message.header.type = "PLAY_GAME_REQUEST";
                message.header.id = userName;
                message.header.recipient = userClicked;
                s.request(message);
            }
        });

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Type your username here: ");
        builder1.setCancelable(true);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder1.setView(input);

        builder1.setPositiveButton(
                "Done",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        userName = input.getText().toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    System.out.println("connected from thread");
                                    s.connect(address, port, userName);
                                    Message message = new Message();
                                    message.header.type = "CONNECT_REQUEST";
                                    message.header.id = userName;
                                    System.out.println("Sent connect_request from thread");
                                    s.request(message);
                                } catch (Exception e) {
                                    System.out.println("IOException occurred");
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        dialog1 = ProgressDialog.show(MainActivity.this, "","Loading. Please wait...", true);
                    }
                });

        builder1.setNegativeButton(
                "Quit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //quit application
                        MainActivity.getInstance().finish();
                        System.exit(0);
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static MainActivity getInstance() {
        return mainInstance;
    }

    public void updateList(final ArrayList<String> names) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        values.clear();
                        for (String name : names) {
                            values.add(name);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public void playGameRequest(final String recipient) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setMessage("Type your username here: ");
        builder2.setCancelable(true);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder2.setView(input);

        builder2.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Message message = new Message();
                        message.header.play = true;
                        message.header.id = userName;
                        message.header.recipient = recipient;
                        s.request(message);
                        startGame();
                    }
                });

        builder2.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Message message = new Message();
                        message.header.play = false;
                        message.header.id = userName;
                        message.header.recipient = userName;
                        s.request(message);
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder2.create();
        alert11.show();
    }

    public void startGame() {
        Intent game = new Intent(MainActivity.getInstance(), GameActivity.class);
        startActivity(game);
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
        System.out.println("Showing ConnectedToast");
        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
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

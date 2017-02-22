package comp2601.carleton.edu.comp2601a2client;

/**
 * Created by tonywhite on 2017-01-28.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class MessageService extends Service {
    private final IBinder mBinder = new MyBinder();
    private ArrayList<String> list = new ArrayList<String>();
    public static final String CUSTOM_INTENT = "com.example.maximkuzmenko.custom.intent.action.TEST";

    private String clientId;
    private Socket s;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        MessageService getService() {
            return MessageService.this;
        }
    }

    public void connect(String host, int port) {
        try {
            System.out.println("Connecting");
            s = new Socket(host, port);
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
            this.clientId = clientId;
        }
        catch (IOException e) {
            System.out.println("IOException happened in MessageServer");
            e.printStackTrace();
        }
    }

    public void request(final Message msg) {
        try {
            oos.writeObject(msg);
            Message message2 = (Message) ois.readObject();
            broadcast(message2); //call broadcast function
        }
        catch (Exception e) {
            System.out.println("Exception happened in the request method in the MessageService class");
            e.printStackTrace();
        }
    }

    public void broadcast(Serializable obj) {
        Intent i = new Intent();
        i.putExtra("myKey", obj);
        i.setAction(CUSTOM_INTENT);
        sendBroadcast(i);
    }

}
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
import java.util.Map;
import java.util.Random;

public class MessageService extends Service {
    private final IBinder mBinder = new MyBinder();
    private ArrayList<String> list = new ArrayList<String>();
    public static final String CUSTOM_INTENT = "com.example.maximkuzmenko.custom.intent.action.TEST";

    private String username;
    private Socket s;
    private EventStream es;

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

    public void connect(String host, int port, String username) {
        try {
            this.username = username;
            s = new Socket(host, port);
            es = new EventStreamImpl(s.getOutputStream(), s.getInputStream());
            ThreadWithReactor twr = new ThreadWithReactor(es);
            twr.register("CONNECT_RESPONSE", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("Connected to server");
                    Message message = new Message();
                    message.header.type = event.type;
                    broadcast(message);
                }
            });
            twr.register("USERS_UPDATED", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    Message message = new Message();
                    message.header.type = event.type;
                    if (event.get(Fields.BODY) != null) {
                        message.body.getMap().putAll((Map<? extends String, ? extends Serializable>)event.get(Fields.BODY));
                    }
                    broadcast(message);
                }
            });
            twr.register("PLAY_GAME_REQUEST", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    Message message = new Message();
                    message.header.type = event.type;
                    message.header.id = (String) event.get(Fields.ID);
                    message.header.recipient = (String) event.get(Fields.RECIPIENT);
                    broadcast(message);
                }
            });
            twr.register("PLAY_GAME_RESPONSE", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    Message message = new Message();
                    message.header.type = event.type;
                    message.header.play = (boolean) event.get(Fields.PLAY);
                    message.header.recipient = (String) event.get(Fields.RECIPIENT);
                    broadcast(message);
                }
            });
            twr.register("DISCONNECT_RESPONSE", new EventHandler() {
                @Override
                public void handleEvent(Event event) {

                }
            });
            twr.register("GAME_ON", new EventHandler() {
                @Override
                public void handleEvent(Event event) {

                }
            });
            twr.register("MOVE_MESSAGE", new EventHandler() {
                @Override
                public void handleEvent(Event event) {

                }
            });
            twr.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message request(Message msg) {
        try {
            System.out.println("Sending message request to server with type " + msg.header.type);
            Event event = new Event(msg.header.type, es);
            event.put(Fields.ID, username);
            event.put(Fields.RECIPIENT, msg.header.recipient);
            if ((Serializable) msg.header.play != null) {
                event.put(Fields.PLAY, msg.header.play);
            }
            if ((Serializable) msg.header.move != null) {
                event.put(Fields.MOVE, msg.header.move);
            }
            if (!msg.body.getMap().isEmpty()) {
                event.put(Fields.BODY, msg.body.getMap());
            }
            es.putEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void broadcast(Serializable obj) {
        Intent i = new Intent();
        i.putExtra("myKey", obj);
        i.setAction(CUSTOM_INTENT);
        sendBroadcast(i);
    }

}
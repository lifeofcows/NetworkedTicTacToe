package comp2601.carleton.edu.comp2601a2server;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by maximkuzmenko on 2017-02-24.
 */
//Server needs to handle:
//Menu page: CONNECT_REQUEST, PLAY_GAME_REQUEST, PLAY_GAME_RESPONSE, DISCONNECT_REQUEST
//Game page: GAME_ON, MOVE_MESSAGE
public class NameServer {
    public static int PORT = 7000;
    ConcurrentHashMap<String, ThreadWithReactor> clients;
    ServerSocket listener;
    ServerSocket ss;

    private void sendEmptyMessage(String message, ThreadWithReactor twr) {
        try {
            EventSource es = twr.getEventSource();
            Event msg = new Event(message, es);
            es.putEvent(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void add(String client, ThreadWithReactor twr) {
        clients.put(client, twr);
    }

    private void remove(String client) {
        clients.remove(client);
    }

    public void init() {
        try {
            Reactor r = new Reactor();
            r.register("CONNECT_REQUEST", new EventHandler() {
                @Override
                public void handleEvent(Event event) {  //do connect stuff; add to clients, add to active users, and after send CONNECTED_RESPONSE to user and USERS_UPDATED to all connected users
                    //Iterate through hashmap to send message USERS_UPDATED to all the clients
                    //clients.keySet();
                    //Event usersUpdated = new Event("USERS_UPDATED", //custom twr.es)

                    sendEmptyMessage("CONNECTED_RESPONSE", clients.get(event.get(Fields.ID)));
                }
            });
            r.register("PLAY_GAME_REQUEST", new EventHandler() { //send request to player indicated in the message
                @Override
                public void handleEvent(Event event) {
                  try {
                    ThreadWithReactor twr = clients.get(event.get(Fields.RECIPIENT));
                    EventSource es = twr.getEventSource();
                    Event msg = new Event("PLAY_GAME_REQUEST", es);
                    msg.put(Fields.RECIPIENT, event.get(Fields.ID));
                    es.putEvent(msg);
                  }
                  catch (Exception e) {
                    e.printStackTrace();
                  }
                }
            });
            r.register("PLAY_GAME_RESPONSE", new EventHandler() { //this method is only called on the person who initially requested the game
                @Override
                public void handleEvent(Event event) {
                    //maybe indicate that player is unavailable to play afterwards
                    ThreadWithReactor twr = clients.get(event.get(Fields.RECIPIENT));
                    EventSource es = twr.getEventSource();
                    Event msg = new Event("PLAY_GAME_RESPONSE", es);
                    msg.put(Fields.PLAY, event.get(Fields.PLAY));
                    es.putEvent(msg);
                    //sendMessage("PLAY_GAME_RESPONSE", clients.get(event.get(Fields.RECIPIENT)));
                }
            });
            r.register("DISCONNECT_REQUEST", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    sendEmptyMessage("DISCONNECT_RESPONSE", clients.get(event.get(Fields.ID)));
                }
            });
            r.register("GAME_ON", new EventHandler() { //called by person who started game, given to other player
                @Override
                public void handleEvent(Event event) {
                    // ThreadWithReactor twr = clients.get(event.get(Fields.RECIPIENT));
                    // EventSource es = twr.getEventSource();
                    // Event msg = new Event("GAME_ON", es);
                    // //potentially send opponents name as well
                    // es.putEvent(msg);
                    sendEmptyMessage("GAME_ON", clients.get(event.get(Fields.RECIPIENT)));
                }
            });
            r.register("MOVE_MESSAGE", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                  ThreadWithReactor twr = clients.get(event.get(Fields.RECIPIENT));
                  EventSource es = twr.getEventSource();
                  Event msg = new Event("MOVE_MESSAGE", es);
                  msg.put(Fields.MOVE, event.get(Fields.MOVE));
                  es.putEvent(msg);
                }
            });
            r.register("GAME_OVER", new EventHandler() { //TODO
                @Override
                public void handleEvent(Event event) {
                    sendMessage("GAME_OVER", clients.get(event.get(Fields.RECIPIENT)));
                }
            });
            run(r);
        } catch (Exception e) {
            System.out.println("Exception on server side");
        }
    }

    public void run(Reactor r) throws IOException {
        try {
            ss = new ServerSocket(PORT);
            while (true) {
                Socket s = ss.accept();
                EventStream es = new EventStreamImpl(s);
                ThreadWithReactor twr = new ThreadWithReactor(es,r);
                twr.start();
            }
        }
        catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NameServer ns = new NameServer();
        ns.init();
    }
}

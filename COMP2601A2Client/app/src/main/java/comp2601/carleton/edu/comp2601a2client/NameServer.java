package comp2601.carleton.edu.comp2601a2client;


import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
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
      EventStream es = twr.getEventSource();
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
      clients = new ConcurrentHashMap<String, ThreadWithReactor>();
      Reactor r = new Reactor();
      r.register("CONNECT_REQUEST", new EventHandler() {
        @Override
        public void handleEvent(Event event) {  //do connect stuff; add to clients, add to active users, and after send CONNECTED_RESPONSE to user and USERS_UPDATED to all connected users
          //Iterate through hashmap to send message USERS_UPDATED to all the clients
          //clients.keySet();

          System.out.println("Entering CONNECT_REQUEST");
          String id = (String) event.get(Fields.ID);
          ThreadWithReactor twr = (ThreadWithReactor) Thread.currentThread(); //gets clients thread
          final EventStream es = twr.getEventSource(); //also get their eventsource
          //System.out.println(es.getEvent().type + "is the type");
          add(id, twr);

          ThreadWithReactor currTwr;
          ArrayList<String> users = new ArrayList<String>();

          HashMap<String,Serializable> map = new HashMap<String, Serializable>();

          for (String client : clients.keySet()) {
            users.add(client);
          }

          map.put("userList", users);

          for (String client : clients.keySet()) {
            currTwr = clients.get(client);
            EventStream currEs = currTwr.getEventSource();
            Event sendEvent = new Event("USERS_UPDATED", currEs); //String type, EventSource es
            sendEvent.put(Fields.BODY, map);
            try {
              currEs.putEvent(sendEvent);
            }
            catch (Exception e) {
              e.printStackTrace();
            }
          }

          System.out.println("Sent messages to users");

          Event connected = new Event("CONNECT_RESPONSE", es); //String type, EventSource es
          try {
            es.putEvent(connected);
            System.out.println("Sent connected event");
          } catch (Exception e) {
            e.printStackTrace();
          }
          System.out.println("Finished CONNECT_REQUEST");
        }
      });
      r.register("PLAY_GAME_REQUEST", new EventHandler() { //send request to player indicated in the message
        @Override
        public void handleEvent(Event event) {
          try {
            System.out.println("Entering PLAY_GAME_REQUEST");
            ThreadWithReactor twr = clients.get(event.get(Fields.RECIPIENT));
            //System.out.println("event.get RECIPIENT is " + event.get(Fields.RECIPIENT));
            EventStream es = twr.getEventSource();
            Event msg = new Event("PLAY_GAME_REQUEST", es);
            msg.put(Fields.RECIPIENT, event.get(Fields.ID));
            msg.put(Fields.ID, event.get(Fields.RECIPIENT));
            es.putEvent(msg);
            System.out.println("Finished PLAY_GAME_REQUEST");
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
      r.register("PLAY_GAME_RESPONSE", new EventHandler() { //this method is only called on the person who initially requested the game
        @Override
        public void handleEvent(Event event) {
          System.out.println("Entering PLAY_GAME_RESPONSE");
          try {
            ThreadWithReactor twr = clients.get(event.get(Fields.RECIPIENT));
            EventStream es = twr.getEventSource();
            Event msg = new Event("PLAY_GAME_RESPONSE", es);
            msg.put(Fields.PLAY, event.get(Fields.PLAY));
            es.putEvent(msg);
            System.out.println("Finished PLAY_GAME_RESPONSE");

          }
          catch (Exception e) {
            e.printStackTrace();
          }
          //sendMessage("PLAY_GAME_RESPONSE", clients.get(event.get(Fields.RECIPIENT)));
        }
      });
      r.register("DISCONNECT_REQUEST", new EventHandler() {
        @Override
        public void handleEvent(Event event) {
          System.out.println("Entering DISCONNECT_REQUEST");
          sendEmptyMessage("DISCONNECT_RESPONSE", clients.get(event.get(Fields.ID)));
          System.out.println("Finished DISCONNECT_REQUEST");

        }
      });
      r.register("GAME_ON", new EventHandler() { //called by person who started game, given to other player
        @Override
        public void handleEvent(Event event) {
          System.out.println("Entering GAME_ON");
          // ThreadWithReactor twr = clients.get(event.get(Fields.RECIPIENT));
          // EventSource es = twr.getEventSource();
          // Event msg = new Event("GAME_ON", es);
          // //potentially send opponents name as well
          // es.putEvent(msg);
          sendEmptyMessage("GAME_ON", clients.get(event.get(Fields.RECIPIENT)));
          System.out.println("Finished GAME_ON");

        }
      });
      r.register("MOVE_MESSAGE", new EventHandler() {
        @Override
        public void handleEvent(Event event) {
          try {
            System.out.println("Entering MOVE_MESSAGE");
            ThreadWithReactor twr = clients.get(event.get(Fields.RECIPIENT));
            EventStream es = twr.getEventSource();
            Event msg = new Event("MOVE_MESSAGE", es);
            msg.put(Fields.MOVE, event.get(Fields.MOVE));
            es.putEvent(msg);
            System.out.println("Finished MOVE_MESSAGE");

          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
      r.register("GAME_OVER", new EventHandler() { //TODO
        @Override
        public void handleEvent(Event event) {
          System.out.println("Entering GAME_OVER");
          sendEmptyMessage("GAME_OVER", clients.get(event.get(Fields.RECIPIENT)));
          System.out.println("Finished GAME_ON");
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
        System.out.println("Listening...");
        Socket s = ss.accept();
        System.out.println("Accepted...");
        EventStream es = new EventStreamImpl(s);
        ThreadWithReactor twr = new ThreadWithReactor(es,r);
        twr.start();
        System.out.println("Created and started ...");
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

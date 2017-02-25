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

    private void add(String client, ThreadWithReactor twr) {
        clients.put(client, twr);
    }

    private void remove(String client) {
        clients.remove(client);
    }

    public void init() {
        try {
            Reactor r = new Reactor();
            r.register("sendMessage", new EventHandler() {
                @Override
                public void handleEvent(Event event) {
                    try {
                        ThreadWithReactor currTwr = clients.get(event.get(Fields.ID)); //get twr of the person sending the message
                        EventStream currEs = currTwr.getEventSource(); //also get their eventsource
                        ThreadWithReactor recTwr = clients.get(event.get(Fields.RECIPIENT));
                        EventStream recEs = recTwr.getEventSource();

                        Serializable messageToSend = event.get(Fields.ID) + " says: " + currEs.getEvent().get(Fields.BODY);

                        //send message to receiver
                        Event sendEvent = new Event("receiveMessage", recEs); //String type, EventSource es
                        sendEvent.put(Fields.BODY, messageToSend);
                        recEs.putEvent(sendEvent);

                        //send message to sender confirming message has been sent
                        Event msgSent = new Event("messageSent", currEs);
                        currEs.putEvent(msgSent);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            r.register("connection", new EventHandler() { //connect
                @Override
                public void handleEvent(Event event) {
                    try {
                        String id = (String) event.get(Fields.ID);
                        System.out.println(id + " is the id");
                        ThreadWithReactor twr = (ThreadWithReactor) Thread.currentThread(); //gets clients thread
                        EventStream es = twr.getEventSource(); //also get their eventsource
                        add(id, twr);
                        Event connected = new Event("connected", es); //String type, EventSource es
                        es.putEvent(connected);
                    }
                    catch (Exception e) {
                        System.out.println("Handling exception event in connection handler");
                        e.printStackTrace();
                    }
                }
            });
            r.register("termination", new EventHandler() { //terminate connection
                @Override
                public void handleEvent(Event event) {
                    try {
                        String id = (String) event.get(Fields.ID);
                        remove(id);
                    }
                    catch (Exception e) {
                        System.out.println("Handling exception event in connection handler");
                    }
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

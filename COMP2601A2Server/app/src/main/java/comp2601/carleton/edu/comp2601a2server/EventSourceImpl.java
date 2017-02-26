package comp2601.carleton.edu.comp2601a2server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Map;

import edu.carleton.COMP2601.common.messaging.Message;

public class EventSourceImpl implements EventSource {

	ObjectInputStream ois;
	ObjectOutputStream oos;
	Socket socket;

	/*
	 * Allows streams to be created: input followed by output
	 */
	public EventSourceImpl(InputStream is, OutputStream os) throws IOException {
		ois = new ObjectInputStream(is);
		oos = new ObjectOutputStream(os);
	}

	/*
	 * Allows streams to be created: output followed by input
	 */
	public EventSourceImpl(OutputStream os, InputStream is) throws IOException {
		oos = new ObjectOutputStream(os);
		ois = new ObjectInputStream(is);
	}
	/*
	 * Designed for server-side usage when a socket has been accepted
	 */
	public EventSourceImpl(Socket s) throws IOException {
		this(s.getInputStream(), s.getOutputStream());
		this.socket = s;
	}

	@Override
	public Event getEvent() throws IOException, ClassNotFoundException {
		Message msg = (Message)ois.readObject();
		Event evt = new Event(msg.header.type, ois, oos, msg.body.getMap());
		evt.put(Fields.ID, msg.header.id);
		evt.put(Fields.RET_ID, msg.header.retId);
		evt.put(Fields.SEQ_ID, msg.header.seqNo);
		return evt;
	}

	@SuppressWarnings("unchecked")
	public void putEvent(Event e) throws IOException {
		Message m = new Message();
		m.header.type = e.type;
		m.header.id = (String) e.get(Fields.ID);
		m.header.retId = (String) e.get(Fields.RET_ID);
		m.header.seqNo = (Long) e.get(Fields.SEQ_ID);
		m.header.recipient = (String) e.get(Fields.RECIPIENT);
		m.header.play = (boolean) e.get(Fields.PLAY);
		m.header.move = (int) e.get(Fields.MOVE);
		if (e.get(Fields.BODY) != null)
			m.body.getMap().putAll((Map<? extends String, ? extends Serializable>)e.get(Fields.BODY));
		oos.writeObject(m);
	}

	public void close() {
		try {
			if (socket != null)
				socket.close();
			if (oos != null)
				oos.close();
			if (ois != null)
				ois.close();
			socket = null;
			oos = null;
			ois = null;
		} catch (IOException e) {
			// Fail quietly
		}
	}
}

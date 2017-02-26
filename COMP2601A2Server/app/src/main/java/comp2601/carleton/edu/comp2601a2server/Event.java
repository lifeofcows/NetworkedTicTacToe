package comp2601.carleton.edu.comp2601a2server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class Event {
	public final String type;
	private final InputStream is;
	private final OutputStream os;
	private final EventSource es;
	private HashMap<String, Serializable> map;

	public Event(String type, InputStream is, OutputStream os, HashMap<String, Serializable> map) {
		this.map = map;
		this.type = type;
		this.is = is;
		this.os = os;
		this.es = null;
	}

	public Event(String type, InputStream is, OutputStream os) {
		this.type = type;
		this.is = is;
		this.os = os;
		this.es = null;
		this.map = new HashMap<String, Serializable>();
	}

	public Event(String type, EventSource es) {
		this.type = type;
		this.es = es;
		this.is = null;
		this.os = null;
		this.map = new HashMap<String, Serializable>();
	}

	public void put(String key, Serializable value) {
		map.put(key, value);
	}

	public Serializable get(String key) {
		return map.get(key);
	}

	public InputStream getInputStream() {
		return is;
	}

	public OutputStream getOutputStream() {
		return os;
	}

	public void putEvent(Event e) throws IOException, ClassNotFoundException {
		if (es != null)
			es.putEvent(e);
	}

	public Event getEvent() throws ClassNotFoundException, IOException {
		if (es != null) {
			return es.getEvent();
		}
		return null;
	}
}

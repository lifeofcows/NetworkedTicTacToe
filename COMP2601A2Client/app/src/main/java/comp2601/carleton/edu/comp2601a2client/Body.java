package comp2601.carleton.edu.comp2601a2client;

import java.io.Serializable;
import java.util.HashMap;

//Body of message; contains a hashmap that accepts names as keys and objects as values

public class Body implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5728956330855011743L;
	private HashMap<String,Serializable> map;	// Contains all properties for the body of the message
	
	Body() {
		map = new HashMap<String, Serializable>();
	}
	
	public void addField(String name, Serializable value) {
		map.put(name, value);
	}
	
	public void removeField(String name) {
		map.remove(name);
	}
	
	public Serializable getField(String name) {
		return map.get(name);
	}
	
	public HashMap<String, Serializable> getMap() {
		return map;
	}
}

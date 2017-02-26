package comp2601.carleton.edu.comp2601a2server;

import java.io.IOException;

public interface EventSource {
	public Event getEvent() throws IOException, ClassNotFoundException;
	public void putEvent(Event e) throws IOException, ClassNotFoundException;
}

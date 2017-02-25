package comp2601.carleton.edu.comp2601a2server;

import java.io.IOException;

public interface EventInputStream {
	public Event getEvent() throws IOException, ClassNotFoundException;
}

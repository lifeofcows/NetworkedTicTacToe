package comp2601.carleton.edu.comp2601a2client;

import java.io.IOException;

public interface EventOutputStream {
	public void putEvent(Event e) throws IOException, ClassNotFoundException;
}

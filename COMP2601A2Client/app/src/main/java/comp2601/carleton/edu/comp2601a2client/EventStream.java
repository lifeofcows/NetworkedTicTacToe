package comp2601.carleton.edu.comp2601a2client;


public interface EventStream extends EventInputStream, EventOutputStream {
	public void close();
}

package comp2601.carleton.edu.comp2601a2server;


public interface EventStream extends EventInputStream, EventOutputStream {
	public void close();
}

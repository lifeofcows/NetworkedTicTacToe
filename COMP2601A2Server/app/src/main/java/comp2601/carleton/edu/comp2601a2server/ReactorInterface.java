package comp2601.carleton.edu.comp2601a2server;

public interface ReactorInterface {
	public void register(String type, EventHandler event);
	public void deregister(String type);
	public void dispatch(Event event) throws NoEventHandler;
}

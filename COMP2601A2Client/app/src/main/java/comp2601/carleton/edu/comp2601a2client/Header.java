package comp2601.carleton.edu.comp2601a2client;

import java.io.Serializable;

public class Header implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -7729816603167728273L;
	public String id;	// Identity of sender; e.g., Bob
	public long seqNo;	// Sequence number for message
	public String retId;	// Return identity for routing
	public String type;		// Type of message (for reactor usage)
	public String recipient;
	public boolean play;
	
	public Header() {
		id = Fields.DEFAULT;
		retId = Fields.DEFAULT;
		type = Fields.NO_ID;
		seqNo = Fields.DEFAULT_SEQ_ID;
		recipient = Fields.DEFAULT_RECIPIENT;
		play = Fields.PLAY;
	}
}

package wot.replay;


public class Packet {

	private final int id;
	private final int time;
	
	private final byte[ ] data;
	
	public Packet( int id, int time, byte[ ] data ) {
		this.id = id;
		this.time = time;
		
		this.data = data;
	}
	
	public int getId( ) {
		return id;
	}

	
	public int getTime( ) {
		return time;
	}

	
	public byte[ ] getData( ) {
		return data;
	}
	
}

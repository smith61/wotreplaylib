package wot.replay.writer;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import wot.replay.IOUtils;
import wot.replay.Packet;

public class PacketOutputStream implements Closeable {

	private final DataOutputStream rawStream;
	
	public PacketOutputStream( OutputStream out ) {
		this.rawStream = new DataOutputStream( out );
	}
	
	public void writePacket( Packet packet ) throws IOException {
		final int id       = packet.getId( );
		final int time     = packet.getTime( );
		final byte[ ] data = packet.getData( );
		final int length   = data.length;
		
		IOUtils.writeInt( this.rawStream, length );
		IOUtils.writeInt( this.rawStream, id );
		IOUtils.writeInt( this.rawStream, time );
		this.rawStream.write( data );
	}
	
	@Override
	public void close( ) throws IOException {
		this.rawStream.close( );
	}
	
}

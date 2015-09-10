package wot.replay.reader;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import wot.replay.IOUtils;
import wot.replay.Packet;

public class PacketInputStream implements Closeable {

	private final DataInputStream rawStream;
	
	public PacketInputStream( InputStream is ) {
		this.rawStream = new DataInputStream( is );
	}

	public Packet readPacket( ) throws IOException {
		final int length   = IOUtils.readInt( this.rawStream );
		final int id       = IOUtils.readInt( this.rawStream );
		final int time     = IOUtils.readInt( this.rawStream );
		final byte[ ] data = new byte[ length ];
		this.rawStream.readFully( data );
		
		return new Packet( id, time, data );
	}
	
	@Override
	public void close( ) throws IOException {
		this.rawStream.close( );
	}
	
}

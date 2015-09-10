package wot.replay.reader;

import java.io.IOException;
import java.io.InputStream;

import wot.replay.ReplayConstants;

public class EncryptionInputStream extends InputStream {

	private final InputStream rawStream;
	
	private final byte[ ] prev;
	private int prevIndex;
	
	public EncryptionInputStream( InputStream rawStream ) {
		this.rawStream = ReplayConstants.wrapStream( rawStream );
		
		this.prev = new byte[ 8 ];
		this.prevIndex = 0;
	}
	
	@Override
	public int read( ) throws IOException {
		int b = this.rawStream.read( );
		if( b == -1 ) return -1;
		
		if( this.prevIndex >= this.prev.length ) this.prevIndex = 0;
		
		return this.prev[ this.prevIndex++ ] ^= b;
	}

	@Override
	public int read( byte[ ] b, int off, int len ) throws IOException {
		int read = this.rawStream.read( b, off, len );
		if( read == -1 ) return -1;
		
		for( int i = off; i < off + read; i++ ) {
			if( this.prevIndex >= this.prev.length ) {
				this.prevIndex = 0;
			}
			b[ i ] = this.prev[ this.prevIndex++ ] ^= b[ i ];
		}
		
		return read;
	}
	
	@Override
	public void close( ) throws IOException {
		this.rawStream.close( );
	}
	
}

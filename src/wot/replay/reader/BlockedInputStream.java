package wot.replay.reader;

import java.io.IOException;
import java.io.InputStream;

public class BlockedInputStream extends InputStream {

	private long remBytes;
	private final InputStream iStream;
	
	public BlockedInputStream( InputStream iStream, long limit ) {
		if( limit < 0 ) {
			throw new IllegalArgumentException( "Limit must be >= 0." );
		}
		this.iStream = iStream;
		this.remBytes = limit;
	}
	
	@Override
	public int read( ) throws IOException {
		if( this.remBytes <= 0 ) {
			return -1;
		}
		int b = this.iStream.read( );
		if( b >= 0 ) this.remBytes--;
		return b;
	}

	@Override
	public int read( byte[ ] buf, int off, int len ) throws IOException {
		if( this.remBytes <= 0 ) {
			return -1;
		}
		int read = this.iStream.read( buf, off, ( int ) Math.min( len, this.remBytes ) );
		if( read >= 0 ) this.remBytes -= read;
		
		return read;
	}
	
	@Override
	public long skip( long n ) throws IOException {
		if( n <= 0 || this.remBytes <= 0 ) return 0;
		
		long skipped = this.iStream.skip( Math.min( n, this.remBytes ) );
		this.remBytes -= skipped;
		return skipped;
	}
	
	@Override
	public void close( ) throws IOException {
		while( this.remBytes > 0 ) {
			this.skip(  this.remBytes );
		}
	}
	
}

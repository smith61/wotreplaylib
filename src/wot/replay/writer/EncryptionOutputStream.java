package wot.replay.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import wot.replay.ReplayConstants;

public class EncryptionOutputStream extends OutputStream {

	private final OutputStream rawStream;
	
	private final byte[ ] prev;
	private int index;
	
	public EncryptionOutputStream( OutputStream os ) {
		this.rawStream = ReplayConstants.wrapStream( os );
		
		this.prev = new byte[ 8 ];
		this.index = 0;
	}
	
	@Override
	public void write( int b ) throws IOException {
		if( this.index >= this.prev.length ) {
			this.index = 0;
		}
		
		b = this.prev[ this.index++ ] ^= b;
		this.rawStream.write( b );
	}
	
	@Override
	public void write( byte[ ] b, int off, int len ) throws IOException {
		b = Arrays.copyOfRange( b, off, off + len );
		
		for( int i = 0; i < len; i++ ) {
			if( this.index >= this.prev.length ) {
				this.index = 0;
			}
			byte bTmp = b[ i ];
			b[ i ] ^= this.prev[ this.index ];
			this.prev[ this.index ] = bTmp;
			
			this.index++;
		}
		
		this.rawStream.write( b, 0, b.length );
	}
	
	@Override
	public void close( ) throws IOException {
		this.rawStream.close( );
	}
	
}

package wot.replay.writer;

import java.io.IOException;
import java.io.OutputStream;

import wot.replay.IOUtils;

public class BlockedOutputStream extends OutputStream {

	private final OutputStream rawStream;
	
	private long bytesWritten;
	
	public BlockedOutputStream( OutputStream rawStream ) {
		this.rawStream = rawStream;
		
		this.bytesWritten = 0;
	}
	
	private void checkOverflow( int c ) throws IOException {
		if( this.bytesWritten + c > 0xFFFFFFFFL ) {
			IOUtils.ioerror( "BLOCK OVERFLOW. Block size must be <= %d.", 0xFFFFFFFFL );
		}
	}
	
	@Override
	public void write( int b ) throws IOException {
		this.checkOverflow( 1 );
		
		this.rawStream.write( b );
		this.bytesWritten += 1;
	}
	
	
	@Override
	public void write( byte[ ] b, int off, int len ) throws IOException {
		if( off < 0 || len < 0 || len > b.length - off ) {
			throw new IndexOutOfBoundsException( );
		}
		
		this.checkOverflow( len );
		this.rawStream.write( b, off, len );
		this.bytesWritten += len;
	}

	@Override
	public void close( ) throws IOException {
		this.rawStream.close( );
	}
	
	public long getBytesWritten( ) {
		return this.bytesWritten;
	}
	
}

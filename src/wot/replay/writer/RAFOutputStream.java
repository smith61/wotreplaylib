package wot.replay.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import wot.replay.IOUtils;

public class RAFOutputStream extends OutputStream {

	private final RandomAccessFile rafOut;
	
	private boolean closed;
	
	public RAFOutputStream( RandomAccessFile rafOut ) {
		this.rafOut = rafOut;
	}
	
	@Override
	public void write( int b ) throws IOException {
		this.checkClosed( );
		
		this.rafOut.write( b );
	}
	
	@Override
	public void write( byte[ ] b, int off, int len ) throws IOException {
		this.checkClosed( );
		
		this.rafOut.write( b, off, len );
	}

	@Override
	public void close( ) throws IOException {
		this.checkClosed( );
		this.closed = true;
	}

	private void checkClosed( ) throws IOException {
		if( this.closed ) {
			IOUtils.ioerror( "Stream closed." );
		}
	}
	
}

package wot.replay;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IOUtils {

	public static void ioerror( String fmt, Object... args ) throws IOException {
		throw new IOException( String.format( fmt, args ) );
	}
	
	public static int readInt( DataInput in ) throws IOException {
		byte[ ] buf = new byte[ 4 ];
		in.readFully( buf );
		int tmp = 0;
		for( int i = 0; i < 4; i++ ) {
			tmp |= ( ( buf[ i ] & 0xFF ) << ( i * 8 ) );
		}
		return tmp;
	}
	
	public static void writeInt( DataOutput out, int n ) throws IOException {
		byte[ ] buf = new byte[ 4 ];
		for( int i = 0; i < 4; i++ ) {
			buf[ i ] = ( byte ) ( ( n >> ( i * 8 ) ) & 0xFF );
		}
		out.write( buf );
	}
	
	public static void safeClose( Closeable closeable ) {
		try {
			if( closeable != null ) closeable.close( );
		}
		catch( IOException ioe ) { }
	}
	
}

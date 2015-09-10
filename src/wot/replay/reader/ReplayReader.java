package wot.replay.reader;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import wot.replay.IOUtils;
import wot.replay.ReplayConstants;

public class ReplayReader implements Closeable {

	private final DataInputStream replayStream;
	private int remBlocks;
	
	private InputStream curBlockStream;
	
	public ReplayReader( InputStream replayStream ) throws IOException {
		this.replayStream = new DataInputStream( replayStream );
		
		int magic = IOUtils.readInt( this.replayStream );
		if( magic != ReplayConstants.REPLAY_MAGIC ) {
			IOUtils.ioerror( "Invalid replay magic. Expected: 0x%08X, Got 0x%08X.", ReplayConstants.REPLAY_MAGIC, magic );
		}
		this.remBlocks = IOUtils.readInt( this.replayStream );
		if( this.remBlocks < 0 ) {
			IOUtils.ioerror( "Invalid replay block count: %d.", this.remBlocks );
		}
		this.curBlockStream = null;
	}
	
	public InputStream nextBlock( ) throws IOException {
		if( this.curBlockStream != null ) {
			this.curBlockStream.close( );
			this.curBlockStream = null;
		}
		if( this.remBlocks <= 0 ) return null;
		
		this.remBlocks -= 1;
		this.curBlockStream = new BlockedInputStream( this.replayStream, IOUtils.readInt( this.replayStream ) & 0xFFFFFFFFL );
		return this.curBlockStream;
	}
	
	public PacketInputStream getPacketStream( ) throws IOException {
		while( this.nextBlock( ) != null );
		
		@SuppressWarnings( "unused" )
		final int decompressedLength = IOUtils.readInt( this.replayStream );
		final int compressedLength   = IOUtils.readInt( this.replayStream );
		
		BlockedInputStream blockStream         = new BlockedInputStream( this.replayStream, compressedLength );
		EncryptionInputStream encryptionStream = new EncryptionInputStream( blockStream );
		InflaterInputStream inflaterStream     = new InflaterInputStream( encryptionStream );
		PacketInputStream packetStream         = new PacketInputStream( inflaterStream );
		
		return packetStream;
	}
	
	@Override
	public void close( ) throws IOException {
		this.replayStream.close( );
	}
	
}

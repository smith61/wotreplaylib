package wot.replay.writer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import wot.replay.IOUtils;
import wot.replay.ReplayConstants;


public class ReplayWriter implements Closeable {

	private final RandomAccessFile replayFile;
	
	private WriterState writerState;
	private int numBlocks;
	
	private Closeable curStream;
	
	public ReplayWriter( File file ) throws IOException {
		this.replayFile = new RandomAccessFile( file, "rw" );
		
		this.replayFile.setLength( 0 );
		IOUtils.writeInt( this.replayFile, ReplayConstants.REPLAY_MAGIC );
		IOUtils.writeInt( this.replayFile, 0 );
		this.numBlocks = 0;
		
		this.writerState = WriterState.DEFAULT;
	}
	
	public OutputStream nextBlock( ) throws IOException {
		switch( this.writerState ) {
			case BLOCK_STREAM:
				this.curStream.close( );
				this.curStream = null;
				this.writerState = WriterState.DEFAULT;
			case DEFAULT:
				long blockOffset = this.replayFile.getFilePointer( );
				IOUtils.writeInt( this.replayFile, 0 );
				
				OutputStream blockStream = new RecordedOutputStream( new RAFOutputStream( this.replayFile ), blockOffset );
				this.curStream = blockStream;
				this.numBlocks++;
				this.writerState = WriterState.BLOCK_STREAM;
				
				return blockStream;
			default:
				throw new IllegalStateException( this.writerState + " => " + WriterState.BLOCK_STREAM );
		}
	}
	
	public PacketOutputStream getPacketStream( ) throws IOException {
		switch( this.writerState ) {
			case BLOCK_STREAM:
				this.curStream.close( );
				this.curStream = null;
				this.writerState = WriterState.DEFAULT;
			case DEFAULT:
				long curPos = this.replayFile.getFilePointer( );
				this.replayFile.seek( 4 );
				IOUtils.writeInt( this.replayFile, this.numBlocks );
				this.replayFile.seek( curPos );
				
				IOUtils.writeInt( this.replayFile, 0 );
				IOUtils.writeInt( this.replayFile, 0 );
				
				RAFOutputStream rafStream               = new RAFOutputStream( this.replayFile );
				RecordedOutputStream compressedStream   = new RecordedOutputStream( rafStream, curPos + 4 );
				EncryptionOutputStream encryptionStream = new EncryptionOutputStream( compressedStream );
				DeflaterOutputStream deflaterStream     = new DeflaterOutputStream( encryptionStream, new Deflater( Deflater.BEST_COMPRESSION ) );
				RecordedOutputStream decompressedStream = new RecordedOutputStream( deflaterStream, curPos );
				
				PacketOutputStream packetStream = new PacketOutputStream( decompressedStream );
				this.curStream = packetStream;
				this.writerState = WriterState.PACKET_STREAM;
				
				return packetStream;
			
			default:
				throw new IllegalStateException( this.writerState + " => " + WriterState.PACKET_STREAM );
		}
	}
	
	@Override
	public void close( ) throws IOException {
		switch( this.writerState ) {
			case DEFAULT:
			case BLOCK_STREAM:
				this.getPacketStream( );
			case PACKET_STREAM:
				this.curStream.close( );
				this.curStream = null;
				this.replayFile.close( );
				this.writerState = WriterState.CLOSED;
			case CLOSED:
				break;
		}
	}
	
	
	private static enum WriterState {
		
		DEFAULT,
		BLOCK_STREAM,
		PACKET_STREAM,
		CLOSED;
		
	}
	
	private class RecordedOutputStream extends BlockedOutputStream {
		
		private final long recordOffset;
		
		private boolean isClosed;
		
		public RecordedOutputStream( OutputStream oStream, long recordOffset ) {
			super( oStream );
			this.recordOffset = recordOffset;
			
			this.isClosed = false;
		}
		
		@Override
		public void close( ) throws IOException {
			if( this.isClosed ) return;
			this.isClosed = true;
			
			super.close( );
			
			RandomAccessFile raf = ReplayWriter.this.replayFile;
			long curPos = raf.getFilePointer( );
			raf.seek( this.recordOffset );
			IOUtils.writeInt( raf, ( int ) this.getBytesWritten( ) );
			raf.seek( curPos );
		}
		
	}
	
}

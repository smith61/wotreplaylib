package wot.replay;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class ReplayConstants {

	public static final int REPLAY_MAGIC = 0x11343212;
	
	public static final Key ENCRYPTION_KEY = new SecretKeySpec(
			new byte[ ] {
				(byte) 0xDE, (byte) 0x72, (byte) 0xBE, (byte) 0xA0, 
				(byte) 0xDE, (byte) 0x04, (byte) 0xBE, (byte) 0xB1, 
				(byte) 0xDE, (byte) 0xFE, (byte) 0xBE, (byte) 0xEF, 
				(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF 
			},
			"Blowfish"
		);
	
	
	public static Cipher newCipher( int mode ) {
		try {
			Cipher cipher = Cipher.getInstance( "Blowfish/ECB/NoPadding" );
			cipher.init( mode, ReplayConstants.ENCRYPTION_KEY );
			return cipher;
		}
		catch( NoSuchAlgorithmException e ) {
			throw new RuntimeException( e );
		} 
		catch( NoSuchPaddingException e ) {
			throw new RuntimeException( e );
		} 
		catch( InvalidKeyException e ) {
			throw new RuntimeException( e );
		}
	}
	
	public static CipherInputStream wrapStream( InputStream is ) {
		return new CipherInputStream( is, ReplayConstants.newCipher( Cipher.DECRYPT_MODE ) );
	}
	
	public static CipherOutputStream wrapStream( OutputStream os ) {
		return new CipherOutputStream( os, ReplayConstants.newCipher( Cipher.ENCRYPT_MODE ) );
	}
	
	
	private ReplayConstants( ) { }
	
}

/*
 * SoundEffect.java
 * 
 * Version:
 *     $Id: SoundEffect.java,v 1.5 2006/11/06 16:39:04 exl2878 Exp $
 *     
 * Revisions:
 *     $Log: SoundEffect.java,v $
 *     Revision 1.5  2006/11/06 16:39:04  exl2878
 *     Added boolean playSounds to stop sounds if any sound-related exceptions
 *     are thrown
 *
 *     Revision 1.4  2006/10/08 17:40:06  exl2878
 *     Playing a sound no longer creates a new, unending thread
 *
 *     Revision 1.3  2006/10/07 19:54:57  exl2878
 *     Implemented static hitCorrectMole(), hitWrongMole(), and hitSign() methods
 *
 *     Revision 1.2  2006/09/24 16:50:16  exl2878
 *     Fixed bug where new SoundEffect objects would not always play audio files
 *
 *     Revision 1.1  2006/09/24 16:25:12  exl2878
 *     Initial revision
 *
 */

package GameUI;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

/**
 * Plays an audio file, either once or in a loop.  Each audio clip runs in its
 * own thread**, and multiple audio clips playing simultaneously will be 
 * composited together automatically.  A stop() method is also provided.
 * 
 * Note that the Java Sound Engine (and thus this class) only works with linear
 * PCM audio files.  It does not work with audio files encoded in ADPCM or any
 * other compression scheme, even if those files have a .wav extension.  To 
 * determine in Windows XP if a file is encoded with a compression scheme,
 * open the file's property window and select the summary tab at the top.  If
 * the audio format is listed as anything other than PCM, the file cannot be
 * played by this class.
 * 
 * **Threading has not yet been implemented in this class.
 *
 * @author Eric Lutley  exl2878@rit.edu
 */
public class SoundEffect {
	
	private static final SoundEffect HIT_CORRECT_MOLE = new SoundEffect( "sounds/bonk.wav" );
	private static final SoundEffect HIT_WRONG_MOLE = new SoundEffect( "sounds/boing.wav" );
	private static final SoundEffect HIT_SIGN = new SoundEffect( "sounds/wood_hit_1.wav" );
	
	private static boolean playSounds = true;
	
	private Clip clip;

	public SoundEffect( String fileName ) {
		AudioInputStream ain = null;
		try {
			ain = AudioSystem.getAudioInputStream( new File( fileName ) );
			DataLine.Info info = 
							new DataLine.Info( Clip.class, ain.getFormat() );
			clip = (Clip) AudioSystem.getLine( info );
			clip.open( ain );
		} catch( Exception e ) {
			System.err.println( "SoundEffect: " + e.getMessage() );
			playSounds = false;
		} finally {
			// This input stream must be closed.
			try {
				ain.close();
			} catch( IOException ioe ) {}
		}
		
		// Sometimes the audio files do not load from the beginning, so
		// this file is explicitly set to start at the beginning
		clip.setMicrosecondPosition( 0 );
	}
	
	/**
	 * Plays the audio clip in a continuous loop.
	 *
	 */
	public void loop() {
		clip.loop( Clip.LOOP_CONTINUOUSLY );
	}
	
	/**
	 * Plays the audio file once.
	 *
	 */
	public void play() {
		clip.setMicrosecondPosition( 0 );
		clip.start();
	}
	
	/**
	 * Stops the audio file if it is currently playing and resets its 
	 * position to the beginning.
	 *
	 */
	public void stop() {
		if ( isPlaying() ) {
			clip.stop();
			// Resets the sound effect so that it can be played again.
			clip.setMicrosecondPosition( 0 );
		}
	}
	
	/**
	 * Returns whether or not the audio clip is playing.
	 *
	 * @return	true if the audio clip is playing, false if it is not.
	 */
	public boolean isPlaying() {
		return clip.isActive();
	}
	
	/**
	 * Test method for the SoundEffect class.
	 *
	 * @param args	name/path of audio file to play
	 */
	public static void main( String args[] ) {
		if ( args.length != 1 ) {	
			System.err.println( "Usage: java SoundEffect <file-name>" );
			System.exit(0);
		}
		SoundEffect s = null;
		try {
			s = new SoundEffect( args[0] );
		} catch ( Exception e ) {
			System.err.println( "SoundEffect error: " + e.getMessage() );
			System.exit(1);
		}
		System.out.println( "Playing file " + args[0] );
		s.play();
		while ( s.isPlaying() );
		System.out.println( "Finished playing file " +  args[0]);
	}
	
	public static void hitCorrectMole() {
		if ( playSounds ) HIT_CORRECT_MOLE.play();
	}
	
	public static void hitWrongMole() {
		if ( playSounds ) HIT_WRONG_MOLE.play();
	}
	
	public static void hitSign() {
		if ( playSounds ) HIT_SIGN.play();
	}
}



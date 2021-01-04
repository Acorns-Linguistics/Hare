


import java.io.File;

import org.acorns.data.SoundData;
import org.acorns.audio.MP3Encoder;

public class MP3EncoderApplication 
{
	private static boolean DEBUG = true;
	
	/** Application to encode a single MP3 file from PCM data
	 * 
	 * @param args usage: Java MP3Encoder <source file> <destination file>
	 */
	public static final void main(final String[] args) 
	{
		if (args.length < 2)
		{
			System.err.println("usage: <inpath> <outpath>");
			return;
		}

		String inPath = args[0], outPath = args[1];

		if (inPath.equals(outPath)) 
		{
			System.err.println
				("Abort: Input file and Output file are the same.");
			return;
		}
		
		try 
		{
			SoundData sound = new SoundData();
			sound.readFile(new File(inPath));

			// Encode SoundData object to MP3 output file
			// DEBUG displays output
			new MP3Encoder(sound, outPath, DEBUG);
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace();  
		}
	}


}

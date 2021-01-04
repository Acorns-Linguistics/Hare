/*
 * Main.java
 *
 * Created on August 3, 2006, 8:35 AM
 *
 *
 *   @author  HarveyD
 *   @version 2.00 Beta
 *
 *   Copyright 2006-2010, all rights reserved
 */

import javax.swing.*;

import javax.help.*;
import java.awt.*;
import java.beans.*;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.awt.event.*;

import org.acorns.editor.*;
import org.acorns.audio.*;
import org.acorns.language.*;

/**
 *
 * (H)olistic (A)udio (R)ecorder and (E)ditor Application
 */
public class Hare
{
    private static SoundFrame rootFrame;
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args)  
   {  
  		rootFrame = new SoundFrame(args); 
	    new JavaAwtDesktop();
   }
   
   public static SoundFrame getRootFrame()
   { 
		   return rootFrame; 
   }
}
 
// The HARE main frame.     
class SoundFrame extends JFrame implements WindowListener
{  
	private final static long serialVersionUID = 1;
	public SoundPanels soundPanels;
   
   public SoundFrame(String[] args)
   {
       // Instantiate the application frame with the acorns icon.
       super("(H)olistic (A)udio (R)ecorder and (E)ditor (HARE)");

       
       ClassLoader loader = SoundPanels.class.getClassLoader();
       URL helpSetUrl = getClass().getResource
                                 ("/helpData/acorns.hs");
       HelpSet helpSet = null;
       try  { helpSet = new HelpSet(loader, helpSetUrl); }
       catch (HelpSetException hse) {}
       new LanguageText(helpSet, new String[]{"soundEditor"}, true);

       URL url = getClass().getResource("/data/hare32x32.png");
       if (url!=null)
       {
          Image newImage  = Toolkit.getDefaultToolkit().getImage(url);
		  newImage = newImage.getScaledInstance(30, 30, Image.SCALE_REPLICATE);
          setIconImage(newImage);		 
       }
       
       // Attach the window listener.
	   setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	   addWindowListener(this);
        
	   SoundDefaults.setSandboxKey("org.acorns.hare");
       String[] paths = SoundDefaults.readSoundDefaults();

		if (SoundDefaults.isSandboxed())
		{
			String libName = "SecurityScopedBookmarkLibrary";
			System.loadLibrary(libName);
			  
			boolean resetPaths = SoundDefaults.setBookmarkFolder();
		    if (resetPaths)
		    {
		    	  String data = SoundDefaults.getDataFolder();
		    	  paths[0] = paths[1] = data;
		    }
		}

       KeyboardFonts.readFonts();
      
       // Set the property change listener for loading/saving files.
       if (paths==null || paths.length==0) 
       {  paths = new String[2];
          paths[0] = paths[1] = "";
       }
       String loadPath = paths[0];
       String savePath = "";
       if (paths.length>=2) savePath = paths[1];
       PropertyChangeListener soundProperties
               = new SoundProperties(loadPath, savePath);
       Toolkit.getDefaultToolkit().addPropertyChangeListener
                ("Properties", soundProperties);   
       
       fileAssociations();

       // Create and add the sound panels.
       soundPanels = new SoundPanels(3, args);
       JScrollPane scroll = new JScrollPane(soundPanels);
       getContentPane().add(scroll);
      
       // Start the application.
       pack();
       setVisible(true);
       setLocationRelativeTo(null);

   }   // End of Constructor.
   
   public void openFile(File file)
   {
	   soundPanels.openFile(file);
   }

   /** Listen for the closing of the frame window.	*/
   public void windowClosing(WindowEvent event)
   {
       // Get property change listener maintaining file properties.
       PropertyChangeListener[] pcl =  
       Toolkit.getDefaultToolkit().getPropertyChangeListeners
                                                         ("Properties");
       
       SoundProperties properties = (SoundProperties)pcl[0];
       String pathName = properties.getPaths();
       String[] paths  = pathName.split(";");
       SoundDefaults.writeSoundDefaults(paths);
       soundPanels.stopSounds();
       
       Window  window = event.getWindow();
       window.dispose();
       System.exit(0);
	}
   
   public void shutDown()
   {
       PropertyChangeListener[] pcl =  
       Toolkit.getDefaultToolkit().getPropertyChangeListeners
	                                           ("Properties");
	    	       
       SoundProperties properties = (SoundProperties)pcl[0];
       String pathName = properties.getPaths();
       String[] paths  = pathName.split(";");
       SoundDefaults.writeSoundDefaults(paths);
       soundPanels.stopSounds();
       System.exit(0);
   }
   
   /** Check file associations */
   private static void fileAssociations()
   {
	   String os = System.getProperty("os.name").toLowerCase();
	   if (os.toLowerCase().indexOf("win")>=0)
       {
           String separator = System.getProperty("file.separator");
           String path =  System.getProperty("user.dir");
           String file = path + separator + "Hare.exe";
           File   exe  = new File(file);
           if (exe.exists())  
           { 
               try
               {
            	  String[] audioExtensions = SoundDefaults.getSoundsSupported();
            	  for (int i=0; i<audioExtensions.length; i++)
            	  {
            		  fileAssociation(exe, audioExtensions[i]);
            	  }
                  
               }  catch (Exception e) {}
           }   // End of if executable exists
       }       // End of if window operating system          
   }           // End of file associations    

   /**
    * 
    * @param application Path to Hare.exe
    * @param extension audio file extension
    * @throws IOException
    */
   private static void fileAssociation(File application, String extension) 
   {
	   try
	   {
           String command = "cmd /c assoc ." + extension + "=hare";
           Process p = Runtime.getRuntime().exec(command);
           p.waitFor();
            
           command = "cmd /c ftype hare=\"" + application + "\" \"%1\"";
           p = Runtime.getRuntime().exec(command);
           p.waitFor();
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
	   }
	   
   }

   //--------------------------------------------------------------
   // Unused window methods.
   //--------------------------------------------------------------
   public void windowDeactivated( WindowEvent event ) {}
   public void windowActivated(   WindowEvent event ) {}
   public void windowDeiconified( WindowEvent event ) {}
   public void windowIconified(   WindowEvent event ) {}
   public void windowClosed(      WindowEvent event ) {}
   public void windowOpened(      WindowEvent event ) {}
}  // End of SoundFrame class.


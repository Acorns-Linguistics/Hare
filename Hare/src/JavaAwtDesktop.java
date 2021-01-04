/**
 * JavaAwtDesktop.java
 * 		Interface between Java and Mac OS systems
 *
 *   @author  harveyD
 *   @version 3.00 Beta
 *
 *   Copyright 2007-2015, all rights reserved
 */

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.acorns.audio.SoundDefaults;
import org.acorns.editor.SoundProperties;
import org.acorns.language.LanguageText;

public class JavaAwtDesktop {

    private static String os = System.getProperty("os.name").toLowerCase();
	private ImageIcon icon = null;
	
    public JavaAwtDesktop() 
    {
  		if (os.indexOf("mac") >= 0) 
  		{
  			System.setProperty("apple.laf.useScreenMenuBar", "true");
  			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Hare");

  			Desktop desktop = Desktop.getDesktop();

  			desktop.setAboutHandler(e -> {
  				about();
  			});
  			
  	       desktop.setPreferencesHandler(e -> {
	           if (!SoundDefaults.isSandboxed())
	           {
	        		Frame root = JOptionPane.getRootFrame();
	        		JOptionPane.showMessageDialog
	        			(root, LanguageText.getMessage("commonHelpSets",  99));
	        		return;
	           }
	        	
	  	      if (SoundDefaults.resetBookmarkFolder())
		      {
		  	      String data = SoundDefaults.getDataFolder();
		  	      
		  	      PropertyChangeListener[] pcl =  
		  	    	       Toolkit.getDefaultToolkit().getPropertyChangeListeners
		  	    	                     ("Properties");		  	      
		  	      SoundProperties properties = (SoundProperties)pcl[0];
		  	      String paths = data + ";" + data;
		  	      properties.setPaths(paths);
		      }
        });

  			
  	        desktop.setQuitHandler((e,r) -> {
  	        	SoundFrame frame = Hare.getRootFrame();
  	        	frame.shutDown();
  	        });
  	        
  	        desktop.setOpenFileHandler(e -> {
  	            // Method to open an Hare audio file (Called from Mac OS application listener).
  	        	String path = "???";
  	        	String step = "";
  	        	try
  	            {  
  	            	List<File> files = e.getFiles();
  	            	for (File file: files)
  	            	{
  	            	   step  = "getting path";
  	            	   path = file.getCanonicalPath();
  	            	   step = "opening file " + file;
  	            	   
   	   	        	   SoundFrame frame = Hare.getRootFrame();
  	                   frame.openFile(file); 
  	            	}
  	            }
  	            catch (Exception exception) 
  	            { 
  	               JOptionPane.showMessageDialog
  	            	  (null, "Hare: " + step + " " + path 
  	            			                       + ":" + exception.toString());  
  	            }
  	 
  	        });

  		}
    }
    
    public void about()
    {
        // Create icon for the dialog window.
        URL url = getClass().getResource("/data/hare32x32.png");
        if (url!=null)
        {
           Image image  = Toolkit.getDefaultToolkit().getImage(url);
 		   Image newImage = image.getScaledInstance(30, 30, Image.SCALE_REPLICATE);
 		   icon = new ImageIcon(newImage);
        }

        // Create label to hold the text.
        String[] text =
        {     "Version 2.1",
              "",
              "Copyright \u00a9 2019, Dan Harvey, all rights reserved",
              "Contact: harveyd@sou.edu, http://cs.sou.edu/cs/~harveyd/acorns",
              "","",
              "This product is freeware, but its intention is to support tribal language revitalization efforts.",
              "We hope that the software is useful, but provide no guarantees of its suitability for any purpose.",
              "It is not to be sold for profit or be reverse engineered. Use of this software implies agreement",
              "to abide by these terms. Please contact the author with questions or comments.",
              "",
         };

        JLabel[] labels = new JLabel[text.length];
        for (int i=0; i<text.length; i++)	{labels[i] = new JLabel(text[i]);}

        String title = "About HARE ([H]olistic [A]udio [R]ecorder and [E]ditor)";
        JOptionPane.showMessageDialog
                (Hare.getRootFrame(), labels, title, JOptionPane.INFORMATION_MESSAGE, icon);
    }
}



package startScreen;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 *
 * @author franpeters
 */
public class runMapOptics {
     /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(startScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(startScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(startScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(startScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
         try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
            UIManager.put("ProgressBar.selectionBackground", Color.black);
            UIManager.put("ProgressBar.selectionForeground", Color.white);
            UIManager.put("ProgressBar.foreground", new Color(8, 32, 128));
            UIManager.put("JButton.setBackground",Color.CYAN);
            startScreen screen = new startScreen();
            screen.setVisible(true);
//            Make sure the json files for the josb and server objects to be saved between sessions still exist
            File jobsJson = new File("/Users/franpeters/Documents/MSc Thesis/MapOptics/jobs.json");
            if(!jobsJson.exists()){
              jobsJson.createNewFile();
            }
            File servJson = new File("/Users/franpeters/Documents/MSc Thesis/MapOptics/servers.json");
            if(!servJson.exists()){
              servJson.createNewFile();
            }
             
        } catch( UnsupportedLookAndFeelException ex ) {
            System.err.println( "Failed to initialize LaF" );
        } catch (IOException ex) {
            Logger.getLogger(runMapOptics.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
}

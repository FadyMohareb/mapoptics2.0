package ComparativeGenomics.ServerHandling;
import com.jcraft.jsch.SftpProgressMonitor;
import javax.swing.JProgressBar;

/**
 *
 * @author franpeters
 * A custom JProgressBar that will be populated when a file being uploaded to an external server has completed. 
 */
public class ProgressMonitor implements SftpProgressMonitor {
    JProgressBar progressBar;
    long count=0;
    long max=0;
    /**
     * 
     */
    public ProgressMonitor() {
        
    }
    /**
     * 
     * @param bar JProgressBar to add progress monitoring to
     */
    public ProgressMonitor(JProgressBar bar) {
        this.progressBar = bar;
    }
    public void setBar(JProgressBar bar){
    this.progressBar = bar;}
    /**
     * To set the start value to zero and the value of the JProgressBar to equal the count variable
     * @param op 
     * @param src file being transferred
     * @param dest destination of file
     * @param max 
     */
    @Override
    public void init(int op, String src, String dest, long max) 
    {

      this.max=max;
      count=0;

      progressBar.setMaximum((int)max);

      progressBar.setMinimum((int)0);

      progressBar.setValue((int)count);

      progressBar.setStringPainted(true);
    }
    /**
     * Set the value of the JProgressBar to equal the number of bytes of the file that have been transferred
     * @param bytes number of bytes of file that has been transferred
     * @return 
     */
    @Override
    public boolean count(long bytes)
    {
        for(int x=0; x < bytes; x++) {
            this.count+=1;
            progressBar.setValue((int)this.count);
        }
        return(true);
    }
    /**
     * Set the value of the JProgressBar to full once file transfer has completed
     */
    @Override
    public void end()
    {
        progressBar.setValue((int)this.max);
    }
}

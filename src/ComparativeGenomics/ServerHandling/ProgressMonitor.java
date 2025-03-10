package ComparativeGenomics.ServerHandling;

import com.jcraft.jsch.SftpProgressMonitor;
import javax.swing.JProgressBar;

/**
 * A custom <code>JProgressBar</code> that will be populated when a file being
 * uploaded to an external server has completed.
 *
 * @author franpeters
 */
public class ProgressMonitor implements SftpProgressMonitor {

    JProgressBar progressBar;
    long count = 0;
    long max = 0;

    /**
     * Constructor
     */
    public ProgressMonitor() {
    }

    /**
     * Constructor with progress bar
     *
     * @param bar <code>JProgressBar</code> which will display monitored
     * progress
     */
    public ProgressMonitor(JProgressBar bar) {
        this.progressBar = bar;
    }

    /**
     * Sets progress bar
     *
     * @param bar <code>JProgressBar</code> which will display monitored
     * progress
     */
    public void setBar(JProgressBar bar) {
        this.progressBar = bar;
    }

    /**
     * Sets the start value to zero and the value of the
     * <code>JProgressBar</code> to equal the count variable
     *
     * @param op unnecessary
     * @param src file being transferred
     * @param dest destination of file
     * @param max maximum value of the bar
     */
    @Override
    public void init(int op, String src, String dest, long max) {

        this.max = max;
        count = 0;

        progressBar.setMaximum((int) max);
        progressBar.setMinimum((int) 0);
        progressBar.setValue((int) count);
        progressBar.setStringPainted(true);
    }

    /**
     * Sets the value of the <code>JProgressBar</code> to equal the number of bytes of the
     * file that has been transferred
     *
     * @param bytes number of bytes of file that has been transferred
     * @return true when the file transfer is done
     */
    @Override
    public boolean count(long bytes) {
        for (int x = 0; x < bytes; x++) {
            this.count += 1;
            progressBar.setValue((int) this.count);
        }
        return (true);
    }

    /**
     * Sets the value of the JProgressBar to full once file transfer is complete
     */
    @Override
    public void end() {
        progressBar.setValue((int) this.max);
    }
}

package jssim;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.imageio.ImageIO;

import static java.lang.Math.pow;

// http://en.wikipedia.org/wiki/Structural_similarity
public class SsimCalculator 
{
    private final static float K1 = 0.01f;
    private final static float K2 = 0.03f;
    
    private final BufferedImage refImage;

    public SsimCalculator(File referenceFile) throws SsimException, IOException 
    {
        this.refImage = getBufferedImageForBytes(loadFile(referenceFile));
    }
    
    private BufferedImage getReferenceImage()
    {
        return refImage;
    }
    
    private WindowManager getWindowManager(byte[] compBytes) throws SsimException, IOException
    {
        final BufferedImage compImg = getBufferedImageForBytes(compBytes);
        
        if (getReferenceImage().getColorModel().getPixelSize() 
                != compImg.getColorModel().getPixelSize())
        {
            throw new SsimException("bits per pixel of images don't match");
        }
        
        return new WindowManager(getReferenceImage(), compImg);
    }
    
    public double compareTo(File comp) throws SsimException, IOException 
    {
        final byte[] compBytes = loadFile(comp);
        final WindowManager manager = getWindowManager(compBytes);
        
        final int[] size = getReferenceImage().getColorModel().getComponentSize();
        final long L = (long) pow(2, size[0]) - 1;
        final double c1 = pow((K1 * L), 2);
        final double c2 = pow((K2 * L), 2);
        
        int numWindows = 0;
        double mssim = 0.0f;
        
        final Iterator<Pair<Window>> iterator = manager.getWindowContainer().iterator();
        
        //calculate ssim for each window
        while (iterator.hasNext())
        {
            final Pair<Window> pair = iterator.next();
            
            final double[] yx = pair.getPrimary().getLumaValues();
            final double[] yy = pair.getSecondary().getLumaValues();

            final double mx = pair.getPrimary().getAverageLuma();
            final double my = pair.getSecondary().getAverageLuma();
            
            //calculate variance and covariance
            double sigxy, sigsqx, sigsqy;
            sigxy = sigsqx = sigsqy = 0f;
            for (int i = 0; i < yx.length; i++)
            {
                sigsqx += pow((yx[i] - mx), 2);
                sigsqy += pow((yy[i] - my), 2);
                
                sigxy += (yx[i] - mx) * (yy[i] - my);
            }
            
            final double numPixelsInWin = (double) yx.length - 1;
            sigsqx /= numPixelsInWin;
            sigsqy /= numPixelsInWin;
            sigxy /= numPixelsInWin;
   
            //perform ssim calculation on window
            final double numerator = (2 * mx * my + c1) * (2 * sigxy + c2);
            final double denominator = (pow(mx, 2) + pow(my, 2) + c1) * (sigsqx + sigsqy + c2);
            
            final double ssim = numerator/denominator;
            
            mssim += ssim;
            numWindows++;
        }
        
        
        return mssim / (double) numWindows;
    }

    private BufferedImage getBufferedImageForBytes(byte[] imageInBytes)
            throws IOException 
    {
        try (final InputStream is = new ByteArrayInputStream(imageInBytes))
        {
            return ImageIO.read(is);
        }
    }

    private byte[] loadFile(File fileToLoad) throws IOException 
    {
        return Files.readAllBytes(Paths.get(fileToLoad.getAbsolutePath()));
    }
}

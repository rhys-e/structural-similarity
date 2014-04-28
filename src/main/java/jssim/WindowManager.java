package jssim;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class WindowManager 
{
    private static final int WIN_SIZE = 8;

    private final WindowContainer windowContainer;

    public WindowManager(BufferedImage refImage,
            BufferedImage comparisonImage) throws SsimException 
    {

        if (refImage.getWidth() != comparisonImage.getWidth()
                || refImage.getHeight() != comparisonImage.getHeight()) 
        {
            throw new SsimException("Image dimmensions are not the same");
        }
        
        int width = getNearestMultipleOf(refImage.getWidth(), WIN_SIZE);
        int height = getNearestMultipleOf(refImage.getHeight(), WIN_SIZE);

        refImage = rescaleImage(refImage, width, height);
        comparisonImage = rescaleImage(comparisonImage, width, height);
        
        // all but 1 window should be 8x8 blocks (unless the image happens
        // to evenly divide by 8x8, in which case, all windows will be 8x8)
        final int numWinX = width / WIN_SIZE;
        final int numWinY = height / WIN_SIZE;

        final List<Window> refWindows = 
                getSsimWindowsForImage(refImage, numWinX, numWinY);
        
        final List<Window> compWindows = 
                getSsimWindowsForImage(comparisonImage, numWinX, numWinY);
        
        this.windowContainer = new WindowContainer(refWindows, compWindows);
    }
    
    private int getNearestMultipleOf(int dimension, int multiple)
    {
        return Math.round(dimension / multiple) * multiple;

    }
    
    private BufferedImage rescaleImage(BufferedImage image, int width, int height)
    {
        if (image.getWidth() == width && image.getHeight() == height)
        {
            return image;
        }
        
        BufferedImage newImage = new BufferedImage(width, height, image.getType());

        Graphics g = newImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        
        return newImage;
    }
    
    private List<Window> getSsimWindowsForImage(BufferedImage image,
            int numXWindows, int numYWindows)
    {
        final List<Window> windows = new ArrayList<Window>();
        
        for (int i = 0; i < numXWindows; i++) 
        {
            for (int j = 0; j < numYWindows; j++) 
            {
                windows.add(new Window(image, WIN_SIZE,
                        (i * WIN_SIZE), (j * WIN_SIZE)));
            }
        }
        
        return windows;
    }

    public WindowContainer getWindowContainer()
    {
        return windowContainer;
    }
    
    public static class WindowContainer implements Iterable<Pair<Window>>
    {
        
        private final Collection<Window> primary;
        private final Collection<Window> secondary;
        
        private WindowContainer(Collection<Window> primary, Collection<Window> secondary) 
                throws SsimException
        {
            if (primary.size() != secondary.size())
            {
                throw new SsimException("");
            }
            
            this.primary = primary;
            this.secondary = secondary;
        }
        
        @Override
        public Iterator<Pair<Window>> iterator()
        {
            return new PairIterator<Window>(primary, secondary);
        }
    }
}

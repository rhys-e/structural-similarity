package jssim;

import java.awt.image.BufferedImage;

class Window 
{
    // REC 601 coefficients for standard def digital formats
    // http://en.wikipedia.org/wiki/Luma_(video)
    private static final float RED_COEFFICIENT = 0.212655f;
    private static final float GREEN_COEFFICIENT = 0.715158f;
    private static final float BLUE_COEFFICIENT = 0.072187f;

    private final int windowSize;
    private final int x;
    private final int y;
    private final double[] lumaValues;
    private final double averageLuma;

    public Window(BufferedImage image, int size, int x, int y) 
    {
        this.windowSize = size;
        this.x = x;
        this.y = y;

        lumaValues = calculateLumaValuesForWindow(image);
        averageLuma = calculateAverageLuma(lumaValues);

    }

    private double calculateAverageLuma(double[] lumaValues) 
    {
        double sumLuma = 0.0f;
        for (int i = 0; i < lumaValues.length; i++) 
        {
            sumLuma += lumaValues[i];
        }

        return sumLuma / (double) lumaValues.length;
    }

    private double[] calculateLumaValuesForWindow(BufferedImage image) 
    {
        final double[] lumaValues = new double[getWindowSize()
                * getWindowSize()];

        int counter = 0;
        for (int i = getMinX(); i < getMaxX(); i++) 
        {
            for (int j = getMinY(); j < getMaxY(); j++) 
            {
                final int rgb = image.getRGB(i, j);

                final int red = (rgb >> 16) & 0xFF;
                final int green = (rgb >> 8) & 0xFF;
                final int blue = rgb & 0xFF;
                
                final double Y = ((double) red * RED_COEFFICIENT)
                        +  ((double) green * GREEN_COEFFICIENT)
                        + ((double) blue * BLUE_COEFFICIENT);

                lumaValues[counter] = Y;
                counter++;
            }
        }

        return lumaValues;
    }
    

    public double getAverageLuma() 
    {
        return averageLuma;
    }

    public double[] getLumaValues() 
    {
        return lumaValues;
    }

    public int getWindowSize() 
    {
        return windowSize;
    }

    public int getMinX() 
    {
        return x;
    }

    public int getMinY() 
    {
        return y;
    }

    public int getMaxX() 
    {
        return getMinX() + getWindowSize();
    }

    public int getMaxY() 
    {
        return getMinY() + getWindowSize();
    }
}

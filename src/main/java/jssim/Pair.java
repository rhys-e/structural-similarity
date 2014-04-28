package jssim;

class Pair<X> 
{
    private final X primary;
    private final X secondary;
    
    public Pair(X primary, X secondary)
    {
        this.primary = primary;
        this.secondary = secondary;
    }
    
    public X getPrimary()
    {
        return this.primary;
    }
    
    public X getSecondary()
    {
        return this.secondary;
    }
}


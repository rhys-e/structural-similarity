package jssim;

import java.util.Collection;
import java.util.Iterator;

class PairIterator<E> implements Iterator<Pair<E>>
{
    private final Iterator<E> primaryIterator;
    private final Iterator<E> secondaryIterator;
    
    public PairIterator(Collection<E> primary, Collection<E> secondary)
    {
        this.primaryIterator = primary.iterator();
        this.secondaryIterator = secondary.iterator();
    }
    
    private Iterator<E> getPrimaryIterator()
    {
        return primaryIterator;
    }
    
    private Iterator<E> getSecondaryIterator()
    {
        return secondaryIterator;
    }

    @Override
    public boolean hasNext()
    {
        return getPrimaryIterator().hasNext() && getSecondaryIterator().hasNext();
    }

    @Override
    public Pair<E> next()
    {
        final E x = getPrimaryIterator().next();
        final E y = getSecondaryIterator().next();
        
        return new Pair<E>(x, y);
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

}

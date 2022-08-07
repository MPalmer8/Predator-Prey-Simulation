
/**
 * A class representing shared characteristics of weather.
 *
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public abstract class Weather
{
    // The maximum steps that weather will last when weather is activated
    private int maxTime; 

    /**
     * Constructor for objects of class Weather
     */
    public Weather()
    {
        
    }
    
    /**
     * @return the Max time
     */
    protected int getMaxTime()
    {
        return maxTime; 
    }
    
    /**
     * Set the max time to an int value
     * @param int x
     */
    protected void setMaxTime(int x)
    {
        maxTime = x; 
    }
    
    /**
     * Decrement max time by 1
     */
    protected void decrementMaxTime()
    {
        maxTime--; 
    }
}

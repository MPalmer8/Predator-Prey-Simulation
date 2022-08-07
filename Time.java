
/**
 * Controls time in the simulation. 
 * It can either be day or night, this changes every 12 steps. 
 *
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public class Time
{
    // Stores a boolean value stating if it is night in the simulation
    private boolean isNight = true;
    
    // A Counter object responsible for controlling the time switch in the simulation
    private Counter counter = new Counter("time"); 

    /**
     * Constructor for objects of class Time
     */
    public Time()
    {
        
    }

    /**
     * Increment the time counter by 1 and run the determineTime method
     */
    public void incrementCounter()
    {
        counter.increment(); 
        determineTime(); 
    }
    
    /**
     * Determine if its day or night
     */
    private void determineTime()
    {
        if(counter.getCount() % 12 == 0)
        {
            isNight = !isNight; 
        }
    }
    
    /**
     * @return The boolean isNight field
     */
    public boolean getTime()
    {
        return isNight; 
    }
    
    /**
     * @return A string stating if its day or night depending on the boolean value of the isNight field
     */
    public String getTimeString()
    {
        if(isNight)
        {
            return "Night";
        }
        else
        {
            return "Day"; 
        }
    }
    
    /**
     * Reset the counter
     */
    public void resetTimeCounter()
    {
        counter.reset(); 
    }
}

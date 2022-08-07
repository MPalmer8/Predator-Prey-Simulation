import java.util.Random;

/**
 * Rain can enter a simulation at a random time and will last for a set number of steps.
 * When present, it causes plants to grow. 
 *
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public class Rain extends Weather
{
    // Determines if the rain is present
    private boolean rainPresent; 
    
    // The probability of rain entering the simulation.
    private static final double RAIN_PROBABILITY = 0.08; 
    
    // A random number generator
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Rain
     */
    public Rain()
    {
        
    }

    /**
     * @return true if rain is present in the simulation, false otherwise
     */
    public boolean getRainPresent()
    {
        return rainPresent; 
    }
    
    /**
     * Determine if rain will be present in the simulation
     */
    private void chanceOfRain()
    {
        double chance = rand.nextDouble(); 
        if(chance < RAIN_PROBABILITY)
        {
            rainPresent = true; 
            setMaxTime(rainTime()); 
        }   
    }
    
    /**
     * Generate amount of steps that rain will last for, maximum being 50
     */
    private int rainTime()
    {
        return rand.nextInt(51); 
    }

    /**
     * Controls rain in the simulation
     */
    public void determineRain()
    {
        if(!rainPresent)
        {
            chanceOfRain();
        }
        else
        {
            // Gets rid of rain in the simulation if maxTime is less than or equal to 0
            if(getMaxTime() <= 0)
            {
                rainPresent = false;
            }
            else
            {
                decrementMaxTime(); 
            }
        }
    }
}

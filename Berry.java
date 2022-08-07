
import java.util.List;
import java.util.Random;
/**
 * A simple model of a Berry.
 * Berries can grow, and they die when they get eaten.
 *
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */

public class Berry extends Plant
{
    // The growth probability of berries.
    private static final double GROWTH_RATE = 0.07;
    // The max amount of berries that can be grown per step by a berry object.
    private static final int MAX_GROWTH = 2;
    // A random double value that controls the chance of growing more berries.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Berry
     */
    public Berry(Field field, Location location)
    {
        super(field, location); 
    }
    
    /**
     * Make this berry act - that is: make it do
     * whatever it wants/needs to do.
     * @param newBerries A list to receive newly grown berries.
     */
    public void act(List<Plant> newBerries)
    {
        if(isPlantAlive()) 
        {
            grow(newBerries); 
        }
    }
    
    /**
     * Check whether or not this Berry is to grow more berries at this step.
     * New growths will be made into free adjacent locations.
     * @param newBerry A list to return newly grown berries.
     */
    private int multiply()
    {
        int newBerry = 0; 
        if(rand.nextDouble() <= GROWTH_RATE) 
        {
            newBerry = rand.nextInt(MAX_GROWTH) + 1;
        }
        return newBerry;
    }
    
    /**
     * Check whether or not new berries will be grown at this step.
     * New berries will be made into free adjacent locations.
     * @param newBerry A list to return newly grown berries.
     */
    public void grow(List<Plant> newBerry)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int spread = multiply(); 
        for(int b = 0; b < spread && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            Berry berry = new Berry(field, loc);
            newBerry.add(berry);
        }
    }
}

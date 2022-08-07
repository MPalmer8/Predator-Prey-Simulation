import java.util.List;
import java.util.Random;

/**
 * A simple model of a Carrot.
 * Carrots can grow, and they die when they get eaten. 
 *
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public class Carrot extends Plant
{
    // The growth probability of carrots.
    private static final double GROWTH_RATE = 0.03;
    // The max amount of carrots that can be grown per step by a carrot object
    private static final int MAX_GROWTH = 1;
    // A random double value that controls the chance of growing more carrots
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Carrot.
     */
    public Carrot(Field field, Location location)
    {
        super(field, location); 
    }
    
    /**
     * Make this carrot act - that is: make it do
     * whatever it wants/needs to do.
     * @param newCarrots A list to receive newly grown carrots.
     */
    public void act(List<Plant> newCarrots)
    {
        if(isPlantAlive()) 
        {
            grow(newCarrots); 
        }
    }
    
    /**
     * Check whether or not this carrot is to grow more carrots at this step.
     * New growths will be made into free adjacent locations.
     * @param newCarrot A list to return newly grown Carrots.
     */
    private int multiply()
    {
        int newCarrot = 0; 
        if(rand.nextDouble() <= GROWTH_RATE)
        {
            newCarrot = rand.nextInt(MAX_GROWTH) + 1;
        }
        return newCarrot;
    }
    
    /**
     * Check whether or not new carrots will be grown at this step.
     * New carrots will be made into free adjacent locations.
     * @param newCarrots A list to return newly grown carrots.
     */
    public void grow(List<Plant> newCarrots)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int spread = multiply(); 
        for(int b = 0; b < spread && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            Carrot carrot = new Carrot(field, loc);
            newCarrots.add(carrot);
        }
    }
}

import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a mouse.
 * Mice age, move, breed, die, and eat.
 * They can be infected by disease, they are active during the night. and can be male or female.
 * 
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public class Mouse extends Prey
{
    // Characteristics shared by all mice (class variables).

    // The age at which a mouse can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a mouse can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.675;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The chance of the mouse being female
    private static final double FEMALE_PROBABILITY = 0.5;
    // The food value of a single berry. In effect, this is the
    // number of steps a mouse can go before it has to eat again.
    private static final int BERRY_FOOD_VALUE = 16;
    
    // Individual characteristics (instance fields).
    
    // The mouse's food level, which is increased by eating berries.
    private int foodLevel;
    
    // The mouse's age.
    private int age;
    
    // True if the animal is a Female, False if it's a male
    private boolean isFemale;
    
    // Determines if the animal acts during the day or at night.
    private boolean activeAtNight = true;
    
    // Whether the animal is infected by disease or not.
    private boolean isInfected; 

    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        determineGender(); 
        age = 0;
        if(randomAge) 
        {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = BERRY_FOOD_VALUE;
    }
    
    /**
     * This is what the mouse does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newMouse A list to return newly born rabbits.
     */
    public void act(List<Animal> newMouse)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) 
        {
            passInfection();
            giveBirth(newMouse);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null)
            { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move
            if(newLocation != null) 
            {
                setLocation(newLocation);
            }
            else 
            {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the mouse's death.
     */
    private void incrementAge()
    {
        age++;
        if(isInfected) 
        {
            age++; 
        }
        
        if(age > MAX_AGE) 
        {
            setDead();
        }
    }
    
    /**
     * Check whether or not this mouse is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMouse A list to return newly born mice.
     */
    private void giveBirth(List<Animal> newMice)
    {
        // New mice are born into adjacent locations.
        // Get a list of adjacent free locations.
        if(searchAdjacentLocationsForMale() != null)
        {
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) 
            {
                Location loc = free.remove(0);
                Mouse young = new Mouse(false, field, loc);
                newMice.add(young);
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0; 
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) 
        {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A mouse can breed if it has reached the breeding age.
     * @return true if the mouse can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return isFemale && (age >= BREEDING_AGE);
    }
    
    /**
     * Determines if the mouse is going to be female or male
     */
    private void determineGender()
    {
        if(rand.nextDouble() <= FEMALE_PROBABILITY)
        {
            isFemale = true;
        }
    }
    
    /**
     * @return True if the mouse is female, false is the mouse is male
     */
    private boolean getGender()
    {
        return isFemale;
    }
    

    /**
     * Searches the Adjacent Locations for a male mouse
     * @return A Mouse object if a male mouse is found, null otherwise
     */
    
    private Mouse searchAdjacentLocationsForMale()
    {
        Field field = getField(); 
        List<Location> locations= field.adjacentLocations(getLocation());
        for (int i = 0; i < locations.size(); i++) 
        {
            Location z = locations.get(i);
            Object creature = field.getObjectAt(z); 
            if(creature instanceof Mouse)
            {
                Mouse mouse = (Mouse) creature;
                boolean gender = mouse.getGender();
                if(!gender)
                {
                    return mouse; 
                }
            }
        }
        return null; 
    }
    
    /**
     * @return Boolean true if the mouse is active at night, false otherwise
     */
    public boolean getActiveAtNight()
    {
        return activeAtNight; 
    }
    
    /**
     * @return Boolean true if the mouse is infected with disease, false otherwise
     */
    public boolean isInfected()
    {
        return isInfected; 
    }
    
    /**
     * Mouse becomes infected
     */
    public void setInfected()
    {
        isInfected = true; 
    }
    
    /**
     * Chance of spreading the disease to another Animal if infected. 
     */
    private void passInfection()
    {
        if(isInfected)
        {
            if(rand.nextDouble() <= getSpreadProbability())
            {
                Animal animalx = searchAdjacentLocationsForAnimal(); 
                if(animalx != null)
                {
                    animalx.setInfected(); 
                }
            }
        }
    }
    
    /**
     * Look for berry adjacent to the current location.
     * Only the first live berry is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) 
        {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Berry) 
            {
                Berry berry = (Berry) plant;
                if(berry.isPlantAlive()) 
                { 
                    berry.setPlantDead();
                    foodLevel = BERRY_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Make this mouse more hungry. This could result in the mouse's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) 
        {
            setDead();
        }
    }
}

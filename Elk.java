import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of an elk.
 * Elks age, move, breed, die, and eat.
 * They can be infected by disease, they are active during the day. and can be male or female.
 * 
 * @author David J. Barnes and Michael Kölling and Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public class Elk extends Prey
{
    // Characteristics shared by all elks (class variables).

    // The age at which an elk can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which an elk can live.
    private static final int MAX_AGE = 40;
    // The likelihood of an elk breeding.
    private static final double BREEDING_PROBABILITY = 0.6;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The chance of the elk being female
    private static final double FEMALE_PROBABILITY = 0.5;
    // The food value of a single carrot. In effect, this is the
    // number of steps a elk can go before it has to eat again.
    private static final int CARROT_FOOD_VALUE = 12;
    
    // Individual characteristics (instance fields).
    
    // The elk's food level, which is increased by eating carrots.
    private int foodLevel;
    
    // The elk's age.
    private int age;

    // True if the animal is a Female, False if it's a male
    private boolean isFemale; 

    // Determines if the animal acts during the day or at night.
    private boolean activeAtNight = false;

    // Whether the animal is infected by disease or not.
    private boolean isInfected; 

    /**
     * Create a new elk. An elk may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the elk will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Elk(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        determineGender(); 
        age = 0;
        if(randomAge) 
        {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = CARROT_FOOD_VALUE;
    }

    /**
     * This is what the elk does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newElks A list to return newly born elk.
     */
    public void act(List<Animal> newElks)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) 
        {
            passInfection();
            giveBirth(newElks);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) 
            { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * This could result in the elk's death.
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
     * Check whether or not this elk is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newElks A list to return newly born elks.
     */
    private void giveBirth(List<Animal> newElks)
    {
        // New elks are born into adjacent locations.
        // Get a list of adjacent free locations.
        if(searchAdjacentLocationsForMale() != null)
        {
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) 
            {
                Location loc = free.remove(0);
                Elk young = new Elk(false, field, loc);
                newElks.add(young);
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
     * An elk can breed if it has reached the breeding age.
     * @return true if the elk can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return isFemale && (age >= BREEDING_AGE);
    }

    /**
     * Determines if the elk is going to be female or male
     */
    private void determineGender()
    {
        if(rand.nextDouble() <= FEMALE_PROBABILITY)
        {
            isFemale = true;
        }
    }

    /**
     * @return True if the elk is female, false is the elk is male
     */
    private boolean getGender()
    {
        return isFemale;
    }

    /**
     * Searches the Adjacent Locations for a male elk
     * @return An Elk object if a male elk is found, null otherwise
     */

    private Elk searchAdjacentLocationsForMale()
    {
        Field field = getField(); 
        List<Location> locations= field.adjacentLocations(getLocation());
        for (int i = 0; i < locations.size(); i++) 
        {
            Location z = locations.get(i);
            Object creature = field.getObjectAt(z); 
            if(creature instanceof Elk)
            {
                Elk elk = (Elk) creature;
                boolean gender = elk.getGender();
                if(!gender)
                {
                    return elk;
                }
            }
        }
        return null; 
    }

    /**
     * @return Boolean true if the elk is active at night, false otherwise
     */
    public boolean getActiveAtNight()
    {
        return activeAtNight; 
    }

    /**
     * @return Boolean true if the elk is infected with disease, false otherwise
     */
    public boolean isInfected()
    {
        return isInfected; 
    }

    /**
     * Animal becomes infected
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
     * Look for carrots adjacent to the current location.
     * Only the first live carrot is eaten.
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
            if(plant instanceof Carrot) 
            {
                Carrot carrot = (Carrot) plant;
                if(carrot.isPlantAlive()) 
                { 
                    carrot.setPlantDead();
                    foodLevel = CARROT_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Make this elk more hungry. This could result in the elk's death.
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

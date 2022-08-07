import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Bear.
 * Bears age, move, breed, die, and hunt.
 * They can be infected by disease, they are active during the night. and can be male or female.
 * 
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public class Bear extends Predator
{
    // Characteristics shared by all bears (class variables).

    // The age at which a bear can start to breed.
    private static final int BREEDING_AGE = 12;
    // The age to which a bear can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a bear breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single elk. In effect, this is the
    // number of steps a bear can go before it has to eat again.
    private static final int ELK_FOOD_VALUE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The chance of the bear being female
    private static final double FEMALE_PROBABILITY = 0.51;

    // Individual characteristics (instance fields).
    // The bear's age.
    private int age;
    // The bear's food level, which is increased by eating elks.
    private int foodLevel;
    // True if the animal is a Female, False if it's a male
    private boolean isFemale; 

    // Determines if the animal acts during the day or at night.
    private boolean activeAtNight = true;
    
    // Whether the animal is infected by disease or not.
    private boolean isInfected; 

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        determineGender();
        if(randomAge) 
        {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(ELK_FOOD_VALUE);
        }
        else 
        {
            age = 0;
            foodLevel = ELK_FOOD_VALUE;
        } 
    }

    /**
     * This is what the bear does most of the time: it hunts for
     * elks. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newBears A list to return newly born bears.
     */
    public void act(List<Animal> newBears)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) 
        {
            giveBirth(newBears);
            passInfection();
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
     * Increase the age. This could result in the bear's death.
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
     * Make this bear more hungry. This could result in the bear's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) 
        {
            setDead();
        }
    }

    /**
     * Look for elks adjacent to the current location.
     * Only the first live elk is eaten.
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
            Object animal = field.getObjectAt(where);
            if(rand.nextDouble() > getBlindProbability())
            {
                if(animal instanceof Elk) 
                {
                    Elk elk = (Elk) animal;
                    if(elk.isAlive()) 
                    { 
                        elk.setDead();
                        foodLevel += ELK_FOOD_VALUE;
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this bear is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBears A list to return newly born bears.
     */
    private void giveBirth(List<Animal> newBears)
    {
        // New bears are born into adjacent locations.
        // Get a list of adjacent free locations.
        if(searchAdjacentLocationsForMale() != null)
        {
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) 
            {
                Location loc = free.remove(0);
                Bear young = new Bear(false, field, loc); 
                newBears.add(young);
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
     * A bear can breed if it has reached the breeding age and if it's female
     */
    private boolean canBreed()
    {
        return isFemale && (age >= BREEDING_AGE);
    }

    /**
     * Determine a bear's gender when a bear object is created
     */
    private void determineGender()
    {
        if(rand.nextDouble() <= FEMALE_PROBABILITY)
        {
            isFemale = true;
        }
    }

    /**
     * @return True if the bear is female, false is the bear is male
     */
    private boolean getGender()
    {
        return isFemale;
    }

    /**
     * Searches the Adjacent Locations for a male bear
     * @return A bear object if a male bear is found, null otherwise
     */

    private Bear searchAdjacentLocationsForMale()
    {
        Field field = getField(); 
        List<Location> locations= field.adjacentLocations(getLocation());
        for (int i = 0; i < locations.size(); i++) 
        {
            Location z = locations.get(i);
            Object creature = field.getObjectAt(z); 
            if(creature instanceof Bear)
            {
                Bear bear = (Bear) creature;
                boolean gender = bear.getGender();
                if(!gender)
                {
                    return bear; 
                }
            }
        }
        return null; 
    }

    /**
     * @return Boolean true if the bear is active at night, false otherwise
     */
    public boolean getActiveAtNight()
    {
        return activeAtNight; 
    }
    
    /**
     * @return Boolean true if the bear is infected with disease, false otherwise
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
}

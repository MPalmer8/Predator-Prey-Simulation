import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Wolf.
 * Wolves age, move, breed, die, and hunt.
 * They can be infected by disease, they are active during the night. and can be male or female.
 * 
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public class Wolf extends Predator
{
    // Characteristics shared by all wolves (class variables).

    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 12;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.45;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single mouse. In effect, this is the
    // number of steps a wolf can go before it has to eat again.
    private static final int MOUSE_FOOD_VALUE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The chance of the wolf being female
    private static final double FEMALE_PROBABILITY = 0.51;

    // Individual characteristics (instance fields).
    // The wolf's age.
    private int age;
    // The wolf's food level, which is increased by eating mice.
    private int foodLevel;
    // True if the animal is a Female, False if it's a male
    private boolean isFemale; 

    // Determines if the animal acts during the day or at night.
    private boolean activeAtNight = true;

    // Whether the animal is infected by disease or not.
    private boolean isInfected; 

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        determineGender();
        if(randomAge) 
        {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MOUSE_FOOD_VALUE);
        }
        else 
        {
            age = 0;
            foodLevel = MOUSE_FOOD_VALUE;
        } 
    }

    /**
     * This is what the wolf does most of the time: it hunts for
     * mice. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newWolves A list to return newly born wolves.
     */
    public void act(List<Animal> newWolves)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) 
        {
            giveBirth(newWolves); 
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
     * Increase the age. This could result in the wolf's death.
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
     * Make this wolf more hungry. This could result in the wolf's death.
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
     * Look for mice adjacent to the current location.
     * Only the first live mouse is eaten.
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
                if(animal instanceof Mouse) 
                {
                    Mouse mouse = (Mouse) animal;
                    if(mouse.isAlive()) 
                    { 
                        mouse.setDead();
                        foodLevel = MOUSE_FOOD_VALUE;
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWolves A list to return newly born wolves.
     */
    private void giveBirth(List<Animal> newWolves)
    {
        // New wolves are born into adjacent locations.
        // Get a list of adjacent free locations.
        if(searchAdjacentLocationsForMale() != null)
        {
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) 
            {
                Location loc = free.remove(0);
                Wolf young = new Wolf(false, field, loc);
                newWolves.add(young);
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
     * A wolf can breed if it has reached the breeding age and if it's female
     */
    private boolean canBreed()
    {
        return isFemale && (age >= BREEDING_AGE);
    }

    /**
     * Determine a wolf's gender when a fox object is created
     */
    private void determineGender()
    {
        if(rand.nextDouble() <= FEMALE_PROBABILITY)
        {
            isFemale = true;
        }
    }

    /**
     * @return True if the wolf is female, false is the wolf is male
     */
    private boolean getGender()
    {
        return isFemale;
    }

    /**
     * Searches the Adjacent Locations for a male wolf
     * @return A wolf object if a male wolf is found, null otherwise
     */

    private Wolf searchAdjacentLocationsForMale()
    {
        Field field = getField(); 
        List<Location> locations= field.adjacentLocations(getLocation());
        for (int i = 0; i < locations.size(); i++) 
        {
            Location z = locations.get(i);
            Object creature = field.getObjectAt(z); 
            if(creature instanceof Wolf)
            {
                Wolf wolf = (Wolf) creature;
                boolean gender = wolf.getGender();
                if(!gender)
                {
                    return wolf; 
                }
            }
        }
        return null; 
    }

    /**
     * @return Boolean true if the wolf is active at night, false otherwise
     */
    public boolean getActiveAtNight()
    {
        return activeAtNight; 
    }

    /**
     * @return Boolean true if the wolf is infected with disease, false otherwise
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

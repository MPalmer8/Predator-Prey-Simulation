import java.util.List;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling and Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    // The probability of disease spreading
    private static final double SPREAD_PROBABILITY = 0.22;

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);
    
    /**
     * Gets if the animal is active at night or not
     */
    abstract public boolean getActiveAtNight();
    
    /**
     * Gets if the animal is infected with disease
     */
    abstract public boolean isInfected();

    /**
     * Sets the animal as infected
     */
    abstract public void setInfected();

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive, false otherwise.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) 
        {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) 
        {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * @returns The probability of disease spreading
     */
    protected double getSpreadProbability()
    {
        return SPREAD_PROBABILITY; 
    }

    /**
     * Searches adjacent locations for an animal
     * @return An Animal object if found, null if otherwise. 
     */
    protected Animal searchAdjacentLocationsForAnimal()
    {
        Field field = getField(); 
        List<Location> locations= field.adjacentLocations(getLocation());
        if(locations != null)
        {
            for (int i = 0; i < locations.size(); i++) 
            {
                Location z = locations.get(i);
                Object creature = field.getObjectAt(z); 
                if(creature instanceof Animal){
                    Animal animal = (Animal) creature;  
                    return animal; 
                }

            }
            return null; 
        }   
        return null; 
    }
}

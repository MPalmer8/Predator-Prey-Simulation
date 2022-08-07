import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 *
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public abstract class Plant
{
    // Stores if the plant is currently alive
    private boolean isAlive;
    // The plant's field.
    private Field field;
    // The plant's position in the field.
    private Location location;

    /**
     * Create a new plant at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        isAlive = true;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlants A list to receive newly grown plants.
     */
    abstract public void act(List<Plant> newPlants);
    
    /**
     * @return The plant's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
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
    
    /**.
     * @return The plant's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    protected void setPlantDead()
    {
        isAlive = false;
        if(location != null) 
        {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * @return true if the plant is alive, false otherwise
     */
    protected boolean isPlantAlive() 
    {
        return isAlive;
    }
}

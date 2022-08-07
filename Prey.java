import java.util.List;

/**
 * A class representing shared characteristics of prey.
 *
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public abstract class Prey extends Animal
{
    /**
     * Create a new prey at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Prey(Field field, Location location)
    {
        super(field, location);
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
}

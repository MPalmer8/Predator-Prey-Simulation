import java.util.List;

/**
 * A class representing shared characteristics of predators.
 *
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public abstract class Predator extends Animal
{
    // The probability of predators not finding food
    private double BLIND_PROBABILITY = 0; 
    
    /**
     * Create a new predator at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Predator(Field field, Location location)
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
    
    /**
     * @return The blind probability (The probability of predators not being able to find food)
     */
    protected double getBlindProbability()
    {
        return BLIND_PROBABILITY;
    }
    
    /**
     * @param double x Blind probability is set to this value
     */
    public void setBlindProbability(double x)
    {
        BLIND_PROBABILITY = x; 
    }
}

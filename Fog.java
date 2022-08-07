import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * Fog can enter a simulation at a random time and will last for a set number of steps.
 * When present, it makes it harder for predators to hunt for food. 
 *
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public class Fog extends Weather
{
    // Determines if the fog is present
    private boolean fogPresent; 
    
    // The probability of fog entering the simulation. 
    private static final double FOG_PROBABILITY = 0.01;  
    
    // A random number generator
    private static final Random rand = Randomizer.getRandom();
    
    // The list of animals in the simulation
    private List<Animal> animalsList = new ArrayList();

    /**
     * Constructor for objects of class Fog
     */
    public Fog()
    {
        
    }

    /**
     * @return true if fog is present in the simulation, false otherwise
     */
    public boolean getFogPresent(){
        return fogPresent; 
    }
    
    /**
     * Determine if fog will be present in the simulation
     */
    private void chanceOfFog()
    {
        double chance = rand.nextDouble(); 
        if(chance < FOG_PROBABILITY)
        {
            fogPresent = true; 
            setMaxTime(fogTime()); 
        }   
    }
    
    /**
     * Generate amount of steps that fog will last for, maximum being 50
     */
    private int fogTime()
    {
        return rand.nextInt(51); 
    }
    
    /**
     * Control fog in the simulation
     */
    public void determineFog()
    {
        if(!fogPresent)
        {
            chanceOfFog();
        }
        else
        {
            // Set the blind probability of predators to 0.5
            setProbability(0.5);
            // Gets rid of fog in the simulation if maxTime is less than or equal to 0.
            if(getMaxTime() <= 0)
            {
                fogPresent = false;
                setProbability(0);
            }
            else
            {
                decrementMaxTime(); 
            }
        }
    }
    
    /**
     * Get the list of animals in the simulation
     */
    public void getAnimalsList(List<Animal> animals)
    {
        animalsList = animals; 
    }
    
    /**
     * Set the blind probability for all of the predators
     */
    public void setProbability(double x)
    {
        for(int i = 0; i < animalsList.size(); i++)
        {
            Animal animal = animalsList.get(i);
            if(animal instanceof Predator)
            {
                Predator predator = (Predator) animal; 
                predator.setBlindProbability(x); 
            }
        }
    }
}

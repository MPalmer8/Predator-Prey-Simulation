import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * Controls disease in the simulation.
 * Disease can randomly be present in the simulation at any time.
 * When introduced into the simulation, a random animal is infected. That animal can spread it to other animals.
 *
 * @author Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public class Disease
{
    // The probability of disease being introduced into the simulation.
    private static final double DISEASE_PROBABILITY = 0.05; 

    // Stores true if the disease is present in the simulation, false otherwise
    private boolean diseasePresent;

    // A random number generator
    private static final Random rand = Randomizer.getRandom();

    // The list of animals in the simulation
    private List<Animal> animalsList = new ArrayList(); 

    //private List<Animal> infectedAnimals = new ArrayList();
    private int infectedAmount; 

    /**
     * Constructor for objects of class Disease
     */
    public Disease()
    {

    }

    /**
     * @return A boolean that states if the disease is present in the simulation or not 
     */
    public boolean getDiseasePresent()
    {
        return diseasePresent; 
    }

    /**
     * Determine if disease will be present in the simulation
     */
    private void chanceOfDisease()
    {
        double chance = rand.nextDouble(); 
        if(chance < DISEASE_PROBABILITY)
        {
            diseasePresent = true; 
        }
    }

    /**
     * Sets the diseasePresent field to the parameter x. 
     * @param Boolean variable x
     */
    public void setDiseasePresent(boolean x)
    {
        diseasePresent = x;         
    }

    /**
     * Get the list of animals in the simulation
     */
    public void getAnimalsList(List<Animal> animals)
    {
        animalsList = animals; 
    }

    /**
     * Iterate through the animals arraylist to check if any are infected
     * @return true is there is an animal that is infected, false otherwise
     */
    public boolean checkDisease()
    {
        for(int i = 0; i < animalsList.size(); i++)
        {
            Animal animal = animalsList.get(i); 
            if(animal.isInfected())
            {
                return true;
            }
        }
        return false; 
    }

    /**
     * Determine the amount of animals that are currently infected
     */
    public void countInfected()
    {
        for(int i = 0; i < animalsList.size(); i++)
        {
            Animal animal = animalsList.get(i);
            if(animal.isInfected())
            {
                infectedAmount++; 
            }
        }
    }

    /**
     * Set the animals infected counter to 0.
     */
    public void clearInfected()
    {
        infectedAmount = 0; 
    }

    /**
     * @return An int of the amount of animals that are infected
     */
    public int getInfectedAmount()
    {
        return infectedAmount;
    }

    /**
     * @return A random animal from the animals array list 
     */
    private Animal getRandomAnimal()
    {
        Random randAnimal = new Random();
        if(animalsList.size() > 1)
        {
            int num = randAnimal.nextInt(animalsList.size() - 1); 
            Animal animal = animalsList.get(num);
            return animal;
        } 
        return null;
    }

    /**
     * Check if disease is not present in the simulation
     */
    public void diseaseAction()
    { 
        if(!getDiseasePresent())
        {
            chanceOfDisease();
            infectRandom(); 
        }
    }

    /**
     * Infects a random animal in the simulation when disease becomes present
     */
    private void infectRandom()
    {
        if(getDiseasePresent())
        {
            Animal animal = getRandomAnimal();
            if(animal != null)
            {
                animal.setInfected(); 
            }
        }
    }
}

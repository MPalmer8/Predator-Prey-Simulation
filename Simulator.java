import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing animals and plants.
 * 
 * @author David J. Barnes and Michael Kölling and Arda Ordu K21045233 and Matthew Palmer K21005255
 * @version 2022.03.01
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 270;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 180;
    // The probability that a bear will be created in any given grid position.
    private static final double BEAR_CREATION_PROBABILITY = 0.05;
    // The probability that a elk will be created in any given grid position.
    private static final double ELK_CREATION_PROBABILITY = 0.2; 
    // The probability that a mouse will be created in any given grid position.
    private static final double MOUSE_CREATION_PROBABILITY = 0.345; 
    // The probability that a wolf will be created in any given grid position.
    private static final double WOLF_CREATION_PROBABILITY = 0.05; 
    // The probability that a coyote will be created in any given grid position.
    private static final double COYOTE_CREATION_PROBABILITY = 0.05; 
    // The probability that a berry will be created in any given grid position.
    private static final double BERRY_CREATION_PROBABILITY = 0.2;
    // The probability that a carrot will be created in any given grid position.
    private static final double CARROT_CREATION_PROBABILITY = 0.0205;

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants; 
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Manages the time in the simulation.
    private Time time = new Time();
    // Controls disease in the simulation.
    private Disease disease = new Disease(); 
    // Controls fog in the simulation.
    private Fog fog = new Fog();
    // Controls rain in the simulation.
    private Rain rain = new Rain();
    

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) 
        {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Elk.class, Color.ORANGE);
        view.setColor(Bear.class, Color.BLUE);
        view.setColor(Mouse.class, Color.GRAY);
        view.setColor(Wolf.class, Color.YELLOW);
        view.setColor(Coyote.class, Color.BLACK);
        view.setColor(Carrot.class, Color.GREEN);
        view.setColor(Berry.class, Color.MAGENTA);

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation() 
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) 
        {
            simulateOneStep();
            //delay(120);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal.
     */
    public void simulateOneStep()
    {
        step++;
        disease.getAnimalsList(animals); 
        fog.getAnimalsList(animals); 
        time.incrementCounter();
        disease.diseaseAction();
        
        //Check if rain isn't present
        if(!rain.getRainPresent())
        {
            fog.determineFog();             
        }
        
        //Check if fog isn't present
        if(!fog.getFogPresent())
        {
            rain.determineRain();
        }

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        // Provide space for newly grown plant.
        List<Plant> newPlants = new ArrayList<>(); 
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) 
        {
            Animal animal = it.next();
            if(animal.getActiveAtNight() == time.getTime())
            {
                animal.act(newAnimals);
            }
            
            if(! animal.isAlive()) {
                it.remove();
            }
        }

        // Let all plants act.
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) 
        {
            Plant plant = it.next();
            
            if(rain.getRainPresent())
            {
                plant.act(newPlants);
            }
            
            if(! plant.isPlantAlive()) 
            {
                it.remove();
            }
        }
        
        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);
        
        // Add the newly grown plants to the main lists.
        plants.addAll(newPlants);
        
        // Reset the infected counter and count again.
        disease.clearInfected(); 
        disease.countInfected(); 
        
        // Update the field display, step counter, time display, infected counter, and weather display.
        view.showStatus(step, field);
        view.setInfo(time, disease, fog, rain);
        
        // Check if none of the animals are infected.
        if(!disease.checkDisease())
        {
            disease.setDiseasePresent(false);
        }
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        plants.clear();
        disease.clearInfected();
        populate();
        time.resetTimeCounter();
        disease.setDiseasePresent(false);

        // Show the starting state in the view.
        view.showStatus(step, field);
        view.setInfo(time, disease, fog, rain);
    }

    /**
     * Randomly populate the field with animals and plants.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) 
        {
            for(int col = 0; col < field.getWidth(); col++) 
            {
                if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) 
                {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location);
                    animals.add(bear);
                }
                else if(rand.nextDouble() <= ELK_CREATION_PROBABILITY) 
                {
                    Location location = new Location(row, col);
                    Elk elk = new Elk(true, field, location);
                    animals.add(elk);
                }
                else if(rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) 
                {
                    Location location = new Location(row, col);
                    Mouse mouse = new Mouse(true, field, location);
                    animals.add(mouse);
                }
                else if(rand.nextDouble() <= WOLF_CREATION_PROBABILITY) 
                {
                    Location location = new Location(row, col);
                    Wolf wolf = new Wolf(true, field, location);
                    animals.add(wolf);
                }
                else if(rand.nextDouble() <= COYOTE_CREATION_PROBABILITY) 
                {
                    Location location = new Location(row, col);
                    Coyote coyote = new Coyote(true, field, location);
                    animals.add(coyote);
                }
                else if(rand.nextDouble() <= CARROT_CREATION_PROBABILITY) 
                {
                    Location location = new Location(row, col);
                    Carrot carrot = new Carrot(field, location);
                    plants.add(carrot);
                }
                else if(rand.nextDouble() <= BERRY_CREATION_PROBABILITY) 
                {
                    Location location = new Location(row, col);
                    Berry berry = new Berry(field, location);
                    plants.add(berry);
                }
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try 
        {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}

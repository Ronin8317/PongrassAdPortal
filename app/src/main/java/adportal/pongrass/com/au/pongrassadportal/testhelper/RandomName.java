package adportal.pongrass.com.au.pongrassadportal.testhelper;

import java.util.Random;

/**
 * Created by user on 5/12/2016.
 */
public class RandomName extends RandomeGeneratorBase implements IRandomValueGenerator {
    public String FirstName[] = {"Oliver", "Olivia", "Williams", "Charlotte", "Jack", "Mia", "Jackson", "Ava", "Thomas", "Ameilia", "Thomas", "Sophie",
            "Lucas", "Emily", "James", "Sophia", "Alex", "Chloe", "Ethan", "Ruby"};
    public String LastName[] = {"Smith", "Jones", "Williams", "Brown", "Wilson", "Taylor", "Johnson", "White", "Martin", "Anderson", "Thompson", "Nguyen", "Turner", "Walker", "Harris", "Lee", "Ryan",
            "Robinson", "Kelly", "King"};

    public String _Name;

    public RandomName()
    {
        Random rand = new Random();

        int random_surname = rand.nextInt(20);
        int random_firstname = rand.nextInt(20);

        _Name =  FirstName[random_firstname] + " " + LastName[random_surname];
    }

    @Override
    public String GetRandomValue(String fieldname) {

       return _Name;

    }
}

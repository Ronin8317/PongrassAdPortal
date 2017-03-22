package adportal.pongrass.com.au.pongrassadportal.testhelper;

/**
 * Created by user on 5/12/2016.
 */
public class ConstantGenerator extends RandomeGeneratorBase implements IRandomValueGenerator {
    public String _value;

    public ConstantGenerator(String v) {
        _value = v;
    }


    @Override
    public String GetRandomValue(String fieldname) {
        return _value;

    }


}

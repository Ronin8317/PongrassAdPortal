package adportal.pongrass.com.au.pongrassadportal.data;

/**
 * Created by user on 27/03/2017.
 */

public class Address {

    protected String _LongAddressName;
    protected String _Unit;
    protected String _Number;
    protected String _StreetName;
    protected String _Suburb;
    protected String _CityName;
    protected String _PostCode;
    protected String _Country;

    protected long _Latitude;
    protected long _Longitude;

    protected Address _ParentAddress; // Parent address


    public static class AddressFactory implements FirebaseDataFactory
    {


        @Override
        public FirebaseData ReturnClass(String data) {
            // JSON Parse
            //JSONParser jp = new JSONParser();

            return null;
        }

        @Override
        public boolean shouldHandle(String path) {
            return false;
        }
    }

    public Address()
    {

    }

    public Address(String LongAddressName)
    {
        ParseAddress(LongAddressName);
    }

    protected void ParseAddress(String LongAddress)
    {
        // seperate it out into bits and pieces

    }
}

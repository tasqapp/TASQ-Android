package tasq.app;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

public class AddressParser {
    public Address parseAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        try {
            // Try to get an address from the address given.
            List<Address> addressList = geocoder.getFromLocationName(address, 3);
            if (!addressList.isEmpty()) {
                return addressList.get(0);
            }

        } catch (IOException e) {
            // No network connection
            // TODO: Add a Toast to warn the user that no Address was found???
            return null;
        }

        return null;
    }
}

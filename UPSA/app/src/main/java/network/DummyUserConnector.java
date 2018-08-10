package network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author OrehOnyah
 * 각 함수에 대한 설명은 NetworkUserInterface 참고
 */

public class DummyUserConnector implements NetworkUserInterface {
    public static final String DEVICEID = "deviceid";
    public static final String PLACEID = "placeid";
    @Override
    public int getPermission(String deviceId, String placeId) {
        /*
        placeId는 fyco2qyqweiuc923이어야 하며,
        id의 길이에 따라 퍼미션이 나옴(예 : asdf->퍼미션 4)
         */
        if(placeId.equals(PLACEID)){
            return deviceId.length();
        }
        return 1;
    }

    @Override
    public boolean getUser(String deviceId) {
        if(DEVICEID.equals(deviceId)){
            return true;
        }
        return false;
    }

    @Override
    public boolean newUser(String deviceId) {
        return true;
    }

    @Override
    public boolean deleteUser(String deviceId){
        return true;
    }
}

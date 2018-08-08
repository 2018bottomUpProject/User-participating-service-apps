package network;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author OrehOnyah
 * 각 함수에 대한 설명은 NetworkUserInterface 참고
 */

public class DummyUserConnector implements NetworkUserInterface {
    @Override
    public int getPermission(String id, String placeId) {
        /*
        placeId는 fyco2qyqweiuc923이어야 하며,
        id의 길이에 따라 퍼미션이 나옴(예 : asdf->퍼미션 4)
         */
        if(id.length()>5)
            return 1;
        if(placeId.equals("fyco2qyqweiuc923")){
            return id.length();
        }
        return 1;
    }

    @Override
    public JSONObject getUser(String deviceId) {
        return null;
    }

    @Override
    public JSONObject newUser(String deviceId) {
        return null;
    }
}

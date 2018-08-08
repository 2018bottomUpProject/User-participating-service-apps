package network;

import org.json.JSONObject;

/**
 * @author OrehOnyah
 */

public interface NetworkUserInterface {
    public int getPermission(String id, String placeId);//유저 권한 받아오기

    public JSONObject getUser(String deviceId);//유저 로그인
    public JSONObject newUser(String deviceId);//새로운 유저 생성
}

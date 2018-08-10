package network;

import org.json.JSONObject;

/**
 * @author OrehOnyah
 */

public interface NetworkUserInterface {
    public int getPermission(String deviceId, String placeId);//유저 권한 받아오기

    public boolean getUser(String deviceId);//유저 로그인
    public boolean newUser(String deviceId);//새로운 유저 생성
    public boolean deleteUser(String deviceId);
}

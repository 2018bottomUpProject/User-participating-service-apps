package network;

import java.util.Map;

public interface User {
    public Map<String, String> getPermission(String id);//유저 권한 받아오기

    public Map<String, String> getUser(String deviceId);//유저 로그인
    public Map<String, String> newUser(String deviceId);//새로운 유저 생성
}

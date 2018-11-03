package network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author OrehOnyah
 */

public interface NetworkPlaceInterface {
    public JSONObject getLocationFG(double lat, double lon, Map<String, Integer> wifi);//앱 전용, 위치 정보 받기. 위치가 존재하지 않으면 null 반환

    public JSONObject getLocationBG(double lat, double lon, Map<String, Integer> wifi);//서비스 전용, 위치 정보 받기. 위치가 존재하지 않으면 null 반환
    public boolean postLocationBG(String deviceId, String placeId);//서비스 전용, 기준 시간보다 오래 머물렀을 경우, DB에 존재하는 장소일 때
    public boolean postLocationBG(String deviceId, double lat, double lon, Map<String, Integer> wifi);//서비스 전용, 기준 시간보다 오래 머물렀을 경우, DB에 존재하지 않는 장소일 때

    public JSONObject getDocument(String placeId);// 문서 받아오기
    public boolean editDocument(String placeId, String userId);//문서 수정하기
    public boolean newDocument(String placeId, String user, String buildingName, String placeType, String placeName, JSONObject document);//새 문서 만들기
    public boolean delDocument(String placeId, String userId);//문서 삭제 요청하기

    public JSONArray getReview(String placeId, int page);//리뷰 받아오기
    public boolean editReview(String placeId, String userId, String ArticleId, String content);//리뷰 수정하기
    public boolean newReview(String placeId, String userId, String content);//새 리뷰 만들기
    public boolean delReview(String placeId, String userId, String ArticleId);//리뷰 삭제하기

    public JSONObject getLog(String articleId);//로그 받아오기(로그는 서버에서 자동 관리됨)

    public JSONObject getCategory(String category);//카테고리별 정보 받아오기

    /*
    JSONObject 사용법
    http://biig.tistory.com/52
     */

    /*
    JSONArray 사용법
    jarray.getJSONObject(인덱스번호)로
    List의 arr.get(인덱스번호)처럼 사용 가능함.
     */
}

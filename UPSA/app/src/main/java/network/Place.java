package network;

import java.util.List;
import java.util.Map;

public interface Place {
    public Map<String,String> getLocationFG();//앱 전용, 위치 정보 받기

    public Map<String,String> getLocationBG();//서비스 전용, 위치 정보 받기
    public boolean postLocationBG();//서비스 전용, 기준 시간보다 오래 머물렀을 경우

    public Map<String,String> getDocument(String placeId);// 문서 받아오기
    public boolean editDocument(String placeId, String userId);//문서 수정하기
    public boolean newDocument(String placeId, String userId);//새 문서 만들기
    public boolean delDocument(String placeId, String userId);//문서 삭제 요청하기

    public List<Map<String,String>> getReview(String placeId);//리뷰 받아오기
    public boolean editReview(String placeId, String userId, String ArticleId, String content);//리뷰 수정하기
    public boolean newReview(String placeId, String userId, String content);//새 리뷰 만들기
    public boolean delReview(String placeId, String userId, String ArticleId);//리뷰 삭제하기

    public List<Map<String,String>> getLog(String documentId);//로그 받아오기(로그는 서버에서 자동 관리됨)

    public Map<String,String> getCategory();//카테고리 받아오기
}

package network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author OrehOnyah
 * 각 함수에 대한 설명은 NetworkPlaceInterface 참고
 */

public class DummyPlaceConnector implements NetworkPlaceInterface {
    @Override
    public JSONObject getLocationFG(double lat, double lon, Map<String, Integer> wifi) {
        if(lat<35|| lat>36|| lon<127.0|| lon>128.0){
            return null;//해당 위치를 찾지 못함.
        }
        JSONObject output = null;
        try {
            output = new JSONObject("{\"lat\":\"35.233648\",\"lon\":\"127.648739\",\"WiFiDic\":{\"Wifiname1\":\"-84.33\",\"Wifiname2\":\"-34.33\"}," +
                    "\"PlaceType\":\"cafe\",\"PlaceName\":\"A+\",\"BuildingName\":\"홈플러스 유성점\", \"PlaceId\":\"fyco2qyqweiuc923\",}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    public JSONObject getLocationBG(double lat, double lon, Map<String, Integer> wifi) {
        JSONObject output = null;
        try {
            output = new JSONObject("{\"lat\":\"35.233648\",\"lon\":\"127.648739\",\"WiFiDic\":{\"Wifiname1\":\"-84.33\",\"Wifiname2\":\"-34.33\"}," +
                    "\"PlaceType\":\"cafe\",\"BuildingName\":\"홈플러스 유성점\", \"PlaceName\":\"A+\" \"PlaceId\":\"fyco2qyqweiuc923\",}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(lat<35|| lat>36|| lon<127.0|| lon>128.0){
            return null;//해당 위치를 찾지 못함.
        }
        return output;
    }

    @Override
    public boolean postLocationBG(String deviceId, String placeId) {
        return true;
    }

    @Override
    public boolean postLocationBG(String deviceId, double lat, double lon, Map<String, Integer> wifi) {
        return true;
    }

    @Override
    public JSONObject getDocument(String placeId) {
        if(placeId.equals("fyco2qyqweiuc923")) {
            try {
                return new JSONObject("{\"PlaceId\":\"fyco2qyqweiuc923\",\"phone\":\"010-xxxx-xxxx\",\"Article\":\"더미 문서\\n네이버는 사람과 사람, 오늘과 내일, 네트워크와 네트워크가 연결되는 더 큰 세상을 만들어왔습니다. 이제 국내뿐 아니라 전 세계 이용자들에게 새로운 경험과 가치를 전하는 새로운 기술을 통해 한 단계 진화된 연결을 만들어가고자 합니다.\\n\\n수많은 개인과 소상공인, 창작자들이 네이버와 함께 새로운 기회와 지속 가능한 성장을 함께 만들고 있습니다. 이미 많은 사람들이 네이버의 다양한 플랫폼과 서비스를 통해 손쉽게 창업하고 사업의 경쟁력을 높이며 성과를 보이고 있으며, 창작자들도 네이버 플랫폼을 통해 자신만의 콘텐츠를 국내외 이용자들과 공유하며 수익을 창출하고 창작활동의 영역을 넓혀가고 있습니다. 모두가 연결된 인터넷 세상에서 네이버는 전 세계 사람들이 사용하는 다양한 플랫폼과 서비스를 만들고 있습니다. LINE, SNOW, V LIVE, 네이버 웹툰 등은 이미 수많은 해외 이용자들의 사랑을 받으며 글로벌 시장에서 성장해나가고 있습니다.\\n\\n이제 네이버는 새로운 가치 창출과 미래 경쟁력 강화를 위해 기술 플랫폼으로 도약하고자 합니다. 이미 인공지능 기반의 R&D 성과물로 여러 가지 서비스와 플랫폼을 선보였습니다. 지속적인 도전을 위해 국내외 유망 기술 스타트업에 대한 투자 및 다양한 협업을 통해 기술 경쟁력을 키우고 우수 인재 발굴에도 적극 나설 것입니다.\",\"ArticleId\":\"asdfasdfasdf\"}");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        else{
            return null;
        }

    }

    @Override
    public boolean editDocument(String placeId, String userId) {
        return true;
    }

    @Override
    public boolean newDocument(String placeId, String userId) {
        return true;
    }

    @Override
    public boolean delDocument(String placeId, String userId) {
        return true;
    }

    @Override
    public JSONArray getReview(String placeId, int page) {
        if(placeId.equals("fyco2qyqweiuc923")) {
            try {

                JSONArray jarr;
                if(page == 1)
                    jarr = new JSONArray("[{\"PlaceId\":\"fyco2qyqweiuc923\",\"Article\":\"더미 리뷰1\\n네이버는 사람과 사람, 오늘과 내일, 네트워크와 네트워크가 연결되는 더 큰 세상을 만들어왔습니다. 이제 국내뿐 아니라 전 세계 이용자들에게 새로운 경험과 가치를 전하는 새로운 기술을 통해 한 단계 진화된 연결을 만들어가고자 합니다.\",\"ArticleId\":\"asdfasdfasdf\"},{\"PlaceId\":\"gsds\",\"Article\":\"더미 리뷰2\\n많은 개인과 소상공인, 창작자들이 네이버와 함께 새로운 기회와 지속 가능한 성장을 함께 만들고 있습니다. 이미 많은 사람들이 네이버의 다양한 플랫폼과 서비스를 통해 손쉽게 창업하고 사업의 경쟁력을 높이며 성과를 보이고 있으며, 창작자들도 네이버 플랫폼을 통해 자신만의 콘텐츠를 국내외 이용자들과 공유하며 수익을 창출하고 창작활동의 영역을 넓혀가고 있습니다. 모두가 연결된 인터넷 세상에서 네이버는 전 세계 사람들이 사용하는 다양한 플랫폼과 서비스를 만들고 있습니다. LINE, SNOW, V LIVE, 네이버 웹툰 등은 이미 수많은 해외 이용자들의 사랑을 받으며 글로벌 시장에서 성장해나가고 있습니다.\",\"ArticleId\":\"asdfasdfasdg\"}]");
                else
                    jarr = new JSONArray("[]");
                return jarr;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean editReview(String placeId, String userId, String ArticleId, String content) {
        return true;
    }

    @Override
    public boolean newReview(String placeId, String userId, String content) {
        return true;
    }

    @Override
    public boolean delReview(String placeId, String userId, String ArticleId) {
        return true;
    }

    @Override
    public JSONObject getLog(String articleId) {
        try {
            if(articleId.equals("asdfasdfasdf")) {
                return new JSONObject("{\"ArticleId\":\"asdfasdfasdf\",\"timestamp\":\"2018.07.31 16:09:22\",\"Deleted\":[{\"startline\":4,\"endline\":4,\"deletedText\":\"삭제된 내용 1\"},{\"startline\":10,\"endline\":11,\"deletedText\":\"삭제된 내용 2\\n두줄짜리임\"}],\"Added\":[{\"startline\":2,\"endline\":2,\"addedText\":\"새로 추가된 내용\"}]}");
            }
            if(articleId.equals("asdfasdfasdg")) {
                return new JSONObject("{\"ArticleId\":\"asdfasdfasdg\",\"timestamp\":\"2018.07.31 16:09:22\",\"Deleted\":[{\"startline\":4,\"endline\":4,\"deletedText\":\"삭제된 내용 1\"},{\"startline\":10,\"endline\":11,\"deletedText\":\"삭제된 내용 2\\n두줄짜리임\"}],\"Added\":[{\"startline\":2,\"endline\":2,\"addedText\":\"새로 추가된 내용\"}]}");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject getCategory(String category) {
        try {
            return new JSONObject("{\"Restaurant\":1200,\"Cafe\":120,\"Park\":240");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

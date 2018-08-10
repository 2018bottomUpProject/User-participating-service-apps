"use strict";
let express = require('express');
let router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {//PlaceId 받음
    console.log(req.query.PlaceId);
    res.render('API/PLACE/document', {
        data: JSON.stringify({
            PlaceId:req.query.PlaceId,
            Article:"더미 문서\n네이버는 사람과 사람, 오늘과 내일, 네트워크와 네트워크가 연결되는 더 큰 세상을 만들어왔습니다. 이제 국내뿐 아니라 전 세계 이용자들에게 새로운 경험과 가치를 전하는 새로운 기술을 통해 한 단계 진화된 연결을 만들어가고자 합니다.\n\n수많은 개인과 소상공인, 창작자들이 네이버와 함께 새로운 기회와 지속 가능한 성장을 함께 만들고 있습니다. 이미 많은 사람들이 네이버의 다양한 플랫폼과 서비스를 통해 손쉽게 창업하고 사업의 경쟁력을 높이며 성과를 보이고 있으며, 창작자들도 네이버 플랫폼을 통해 자신만의 콘텐츠를 국내외 이용자들과 공유하며 수익을 창출하고 창작활동의 영역을 넓혀가고 있습니다. 모두가 연결된 인터넷 세상에서 네이버는 전 세계 사람들이 사용하는 다양한 플랫폼과 서비스를 만들고 있습니다. LINE, SNOW, V LIVE, 네이버 웹툰 등은 이미 수많은 해외 이용자들의 사랑을 받으며 글로벌 시장에서 성장해나가고 있습니다.\n\n이제 네이버는 새로운 가치 창출과 미래 경쟁력 강화를 위해 기술 플랫폼으로 도약하고자 합니다. 이미 인공지능 기반의 R&D 성과물로 여러 가지 서비스와 플랫폼을 선보였습니다. 지속적인 도전을 위해 국내외 유망 기술 스타트업에 대한 투자 및 다양한 협업을 통해 기술 경쟁력을 키우고 우수 인재 발굴에도 적극 나설 것입니다.",
        ArticleId:"asdfasdfasdf"})
    });
});
router.post('/', function(req, res, next){
    res.render('API/PLACE/document', {
        data: "OK"
    });
});
module.exports = router;
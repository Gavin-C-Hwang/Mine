package com.controller.mine;

import com.com.entity.mine.Blog;

import java.util.HashMap;
import java.util.Iterator;

public class MyUtil {
    public static String htmlCode = "";
    public static String currnentUrl = "";
    public static int[] arInvalidImg = {
            11311
            ,11374 //쭉방 로고 http://cfile255.uf.daum.net/image/99A640345BF7CC572A0FB0
            ,11882
            ,12019 //이종격투기 쿠로세쇼이 http://t1.daumcdn.net/cafeattach/mEr9/ff142bad7399af70cc4e80f2ab662741c0137f06
            ,12120 //도탁스 화투로고 http://cfile294.uf.daum.net/image/1425B93C5133EC9F1BEA2D
            ,18684 //이종격투기 스마일100 http://t1.daumcdn.net/cafeattach/Uzlo/1f73ee3d35f7b6fddfd6bd2366bae796acb10cfc
            ,19624 //이종격투기 쿠로세쇼이 http://t1.daumcdn.net/cafeattach/mEr9/2e145d1682a2229fe546436aad9e3d1f4a973bc6
            ,22527  //이종격투기  http://cfile265.uf.daum.net/image/996BF54A5A9F2D802FB722
            ,22926 //쭉빵카페 메인로고 http://cfile256.uf.daum.net/image/996831455A49FABB319BDD
            ,26645
            ,27587
            ,27950
            ,30663 //이종격투기 쿠로세쇼이 http://t1.daumcdn.net/cafeattach/mEr9/d79d77f8da3ce420c82b8fdb2d0b15aa1c5ab714
            ,33471 //오븐전시장 http://cfile277.uf.daum.net/image/9966994D5BF3D0E81F895C
            ,36371 //여성시대 엽혹진 http://cfile258.uf.daum.net/image/214E124858B57D721854CE
            ,39256
            ,39258
            ,44629
            ,44631
            ,44665
            ,45180 //이종격투기 검성진산월 http://t1.daumcdn.net/cafeattach/Uzlo/afc76fcefd8f634b4ea3da998df03e67474231bc
            ,45217
            ,45219
            ,47295 //도탁스 게임중독 http://cfile268.uf.daum.net/image/25639847527D29AC165E60
            ,47876 //이종격투기 검성진산월 http://t1.daumcdn.net/cafeattach/Uzlo/280e1704509b86ad75fd2984c3f9f01ccd050687
            ,47897
            ,47899
            ,48182 //NBA 로고 http://cfile245.uf.daum.net/image/241DE94852B9AFAB27A61E
            ,50283
            ,53673 //이종격투기 이적 냉면 http://cfile292.uf.daum.net/image/99C992445C03F34B0851AE
            ,53693 //이종격투기 이적 냉면 http://cfile292.uf.daum.net/image/99C992445C03F34B0851AE
            ,58132 //이종격투기 스마일100 http://t1.daumcdn.net/cafeattach/Uzlo/957a18fd6bd6be0c6c91677372150830d9b09f5f
            ,54467
            ,55382
            ,59839 //이종격투기 명함클립 http://cfile248.uf.daum.net/image/994816345C03F34A256038
            ,63634
            ,70384 //소울드레서 눈목도리 http://cfile285.uf.daum.net/image/99E54F385C10A3B40A3B5D
            ,70418 //소울드레서 옷걸이 http://cfile253.uf.daum.net/image/995792365C01F67F1D0895
            ,71752
            ,71754
            ,73110
            ,73181 //이종격투기 검성진산월 http://cfile262.uf.daum.net/image/995869345ADD48051D58A6
            ,74869
            ,80230 //여성시대 꽃로고 http://cfile279.uf.daum.net/image/2176E04958F1FBE032CB10
            ,80232
            ,83161 //소울드레서 가을로고 http://cfile297.uf.daum.net/image/99527D3359AA53C308422A
            ,86278 //여성시대 출처작성요령 http://cfile247.uf.daum.net/image/2220A94858B57D74109448
            ,87184
            ,93351 //소울드레서 소주담 http://cfile248.uf.daum.net/image/255E8E455953C21120D1F5
            ,106835 //도탁스 반짝이 http://cfile242.uf.daum.net/image/99E91A505BB2E64F15D7B1
            ,126659 //여성시대 콧구멍로고 http://cfile274.uf.daum.net/image/241CFE4A58F1FA621CAAB5
            ,149266 //이종격투기 스마일100 http://t1.daumcdn.net/cafeattach/Uzlo/7f527d51170d81b4bbaade78699d5a239b90f990
            ,173211 //여성시대 비글로고 http://cfile288.uf.daum.net/image/25142A3A593A03450E8062
            ,187665 //쭉빵카페 잔디 http://cfile265.uf.daum.net/image/99D29F375A72ACCF150E7F
            ,202894
            ,230195 //이종격투기 스마일100 http://t1.daumcdn.net/cafeattach/Uzlo/8b28fcba8b5eaea71c5cccb87d27e3adfe6cd31c
            ,340591
            ,412865
            ,468563 //오븐전시장 꽃 http://cfile262.uf.daum.net/image/99597C4A5B05BBB818DB3C
            ,817417 //이종격투기 오다기리조 http://cfile290.uf.daum.net/image/998DD24D5BF6406B109B30
            ,877718
            ,1079564 //도탁스 가을gif http://cfile297.uf.daum.net/image/9960EC3359C14CDE21485E
            ,1390319
            ,1923104 //이종격투기 스마일100 http://t1.daumcdn.net/cafeattach/Uzlo/122eb050776ac5aaf4259ca111bcd0947b5da17c
            ,2535552
            ,3340542
            ,4674606
            ,4965681
            ,4965683
            ,5496183 //도탁스 호루슬 http://cfile242.uf.daum.net/image/99C5973359E2D3DA309C42
            ,6667687 //이종격투기 스마일100 http://t1.daumcdn.net/cafeattach/Uzlo/df3dcc5625577ddb7cf8864cd4587eb61fe8ae35
            ,7412875 //이종격투기 검성진산월 http://t1.daumcdn.net/cafeattach/Uzlo/fb3bd95240bd6057d7d5ae5482a9c64bf480edde
            ,8368598
            ,8658287
    };

}

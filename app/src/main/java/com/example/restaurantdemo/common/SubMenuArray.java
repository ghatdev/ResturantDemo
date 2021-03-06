package com.example.restaurantdemo.common;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.model.SubMenuModel;

public class SubMenuArray {
    public static final SubMenuModel[][] SUB_MENU = {
            {new SubMenuModel("햄버거", "이달의 행사메뉴", "\u20A92000~\u20A93000", R.drawable.menu1_1),
                    new SubMenuModel("사이드메뉴", "햄버거와 함께", "\u20A91000~\u20A92000", R.drawable.menu1_2),
                    new SubMenuModel("음료수", "정말 맛있는 음료수", "\u20A9900~\u20A91000", R.drawable.menu1_3),
                    new SubMenuModel("세트메뉴", "햄버가+음료수", "\u20A92000~\u20A910000", R.drawable.menu1_4)},
            {new SubMenuModel("가정식 한상차림", "이달의 행사메뉴", "\u20A910000~\u20A920000", R.drawable.menu2_1),
                    new SubMenuModel("한정식세트", "온가족이 즐길수 한정식", "\u20A96000~\u20A99000", R.drawable.menu2_2),
                    new SubMenuModel("식사류", "각종 찌게류", "\u20A912000~\u20A925000", R.drawable.menu2_3),
                    new SubMenuModel("고기류", "한우 고기 세트", "\u20A95000~\u20A97000", R.drawable.menu2_4)},
            {new SubMenuModel("면류", "이달의 행사메뉴", "\u20A97000~\u20A99000", R.drawable.menu3_1),
                    new SubMenuModel("밥류", "전통 중국식 식사", "\u20A99000~\u20A912000", R.drawable.menu3_2),
                    new SubMenuModel("요리", "다함께 즐기는 식사", "\u20A910000~\u20A925000", R.drawable.menu3_3)},
            {new SubMenuModel("튀김세트", "이달의 행사메뉴", "\u20A912000~\u20A925000", R.drawable.menu4_1),
                    new SubMenuModel("대만족 회세트", "회식용 대 인식 회모음", "\u20A960000~\u20A990000", R.drawable.menu4_2),
                    new SubMenuModel("모듬 스시", "실속파 스시메뉴", "\u20A915000~\u20A930000", R.drawable.menu4_3),
                    new SubMenuModel("샤브샤브", "추운날엔 나베와 샤브샤브", "\u20A917000~\u20A930000", R.drawable.menu4_4),
                    new SubMenuModel("식사류", "기운을 내주는 장어덮밥", "\u20A912000~\u20A915000", R.drawable.menu4_5)},
            {new SubMenuModel("아메리카노", "이달의 행사메뉴", "\u20A93000~\u20A93000", R.drawable.menu5_1),
                    new SubMenuModel("콜롬비아커피", "콜럼비아산 커피", "\u20A94000~\u20A94000", R.drawable.menu5_2),
                    new SubMenuModel("인도커피", "진한 모카향의 커피", "\u20A95000~\u20A95000", R.drawable.menu5_3),
                    new SubMenuModel("브라질커피", "기분전환을 위한 커피", "\u20A96000~\u20A96000", R.drawable.menu5_4),
                    new SubMenuModel("인도네이사아커피", "인도네이아산 커피", "\u20A93000\u20A95000", R.drawable.menu5_5),
                    new SubMenuModel("한국음료", "전통차", "\u20A96000~\u20A96000", R.drawable.menu5_6)},
            {new SubMenuModel("딸기케잌", "이달의 행사메뉴", "\u20A94500~\u20A96000", R.drawable.menu6_1),
                    new SubMenuModel("과일빙수", "정말 시원한 과일빙수", "\u20A98000~\u20A910000", R.drawable.menu6_2),
                    new SubMenuModel("스낵류", "무지개 색의 마카롱", "\u20A93000~\u20A95000", R.drawable.menu6_3),
                    new SubMenuModel("빵류", "살살녹는 치즈케잌", "\u20A95100~\u20A95400", R.drawable.menu6_4)},
    };

    public static final String[][] SUB_MENU_DESC = {
            {"전세계를 막론하고 대체로 일반적인 대다수의 햄버거는 패스트푸드점의 햄버거인데 이게 사람들의 인식으로 비추어 볼 때에 건강에 안 좋게 비추어진 것이다. 햄버거는 탄수화물 + 지방 + 단백질 + 나트륨 + 비타민(야채 조금 들어감)으로 되어있는데 실제로 패스트푸드는 균형적인 영양식임에도 불구하고 탄수화물과 지방이 유난히 많이 들어가 있어서 사람들이 정크푸드로 본 것이다.",
                    "세트에 600원을 추가하면 라지 세트로 교환할 수 있다. 이는 콜라와 감자튀김을 M 사이즈에서 L 사이즈로 바꾸는 것이지만, 용기 디자인이 디자인인지라 실질적으로 양이 많이 느는 것은 아니다. 점포와 시간에 따라 다르지만 맥딜리버리일 경우 감자튀김이 좀 눅눅하긴 해도 확실히 엄청 많이 들어있을 때도 있는 편. ",
                    "인간이 마실 수 있는 모든 액체의 총칭. 일반적으로 한국에서는 비알콜성 음료를 음료, 알콜성 음료를 '술'로 분리해서 생각하는 경우가 많지만, 서양에서는 음료를 분류할 때 아예 알콜성 / 비알콜성 음료로 구분, 합쳐서 '음료'라고 통칭한다. 특히 술은 순수 알코올 함유량이 1% 이상 함유된 마실 수 있는 음료를 뜻한다.",
                    "원래 메뉴판에 여러 메뉴가 있었으나 메뉴가 늘어나면서 점내 메뉴판에는 일부 메뉴는 생략하게 되었다. 여기서 숨겨진 메뉴란 홈페이지 메뉴판에도 없는 것을 뜻하며 더블치즈버거는 점포에 있는 책받침 형태로 된 메뉴판 및 키오스크에는 있다."},
            {"한국식 상차림의 기본인 밥, 국, 반찬과 김치, 장류에서 범위를 보다 확장해서 각 종류들을 늘려가며 상을 차린다. 보통 육류요리로는 구이, 조림, 전, 찜, 육회 등이 올라오고 국물요리로는 전골 내지는 찌개가 올라오며, 생선회나 조개류 같은 갖가지 해산물이나 생채 및 숙채 그리고 절인 반찬 및 젓갈 등이 올라온다.",
                    "한국 음식이 세계화, 패스트푸드화가 어려운 이유는 밥, 국, 반찬을 한꺼번에 먹는다는 점(=일품요리가 적다)이다. 대부분의 탕, 국, 찌개 뿐 아니라 불고기, 갈비찜, 잡채 등의 요리가 밥과 반찬과 함께 먹는 것을 전제로 간을 하고 조리하기에 초밥, 탕수육, 피쉬앤칩스처럼 한 접시만으로 간단한 차림을 내기가 매우 어렵다. 때문에 김치와 같은 반찬을 한식 대표 메뉴라며 오랫동안 밀어주었다. ",
                    "김치찌개의 역사는 사실상 김치의 역사와 궤를 나란히 한다. 너무 시어지고 염분도가 높아 생식이 힘들거나, 양을 불리기 위해 김치를 물에 넣어 끓여먹던 방식이 고기, 대파, 두부, 마늘 등이 추가되어 현재의 김치찌개가 되었다는 설이 있다.",
                    "'불에 구워먹는 고기'라는 뜻으로서의 불고기는 1930년대에서도 찾아볼 수 있는 말이다.[2] 이렇듯 일정하게 자연스럽게 쓰였던 것으로 추측된다. 현재는 불고기가 가리키는 말의 범위가 '프라이팬이나 뚝배기에 양념한 고기를 조리'해 먹는 쪽으로 축소되었는데 구워먹는 쪽은 고기구이/Korean BBQ라고 따로 불리우게 되었다."},
            {"야채와 돼지고기를 넣고 식용유와 함께 춘장을 볶아서 만든 양념을 국수에 비벼서 먹는 한국식 중화 요리. 지금은 꽤 알려진 사실이지만, 정작 중국엔 이런 요리가 없다. 중국 본토의 자장몐은 한국과는 달리 통으로 된 콩이 들어간다는 게 특징이다. 다른 모양의 중국 본토 자장몐. 생긴게 스테이크 들어간 국수 같다.",
                    "쌀을 주식으로 삼는 지역에서 쌀을 이용해 지어둔 밥의 보존성을 올리기 위해 기름 등을 이용해서 볶아낸 것이 기원이다.[1] 먹다 남은 찬밥을 처리하기 굉장히 좋은 요리법. 하지만 상하는 것에서 면역은 얻지만 대장균 수 자체는 급격히 늘어난다.",
                    "중국집에서 주문시에는 따로 요청하지 않는 이상 튀김 위에 소스가 부어져 나오는 것이 일반적인 형태이나, 가게에 따라서는 튀김과 소스를 볶아서 내놓는 곳도 있다. 표준국어대사전 및 두산백과사전 등에서는 탕수육은 소스를 부어 먹는 요리라고 정의하고 있으며, 중식조리기능사 실기시험에서는 튀김과 소스를 볶아서 제출하는 것이 정석이다."},
            {"고기나 생선, 채소 등의 식재료를 끓는 기름에 넣고 단시간에 익혀낸 음식으로 밀가루와 계란 또는 녹말 등으로 만든 튀김옷을 입혀서 튀기는 경우도 있고 빵가루를 겉에 입혀 튀기거나 그냥 재료 그대로 튀기는 경우도 있다.",
                    "날 것이라 맛을 잃고 상하기 쉽다는 점에서 되도록 신선도가 좋은 회가 고급으로 평가받기 때문에, 바닷가 사람들에게는 그다지 고급음식이 아니나 내륙에서는 고급요리로 여겨지고 있다. 이런 회의 고급화에는 초밥을 위시로 한 일본인들의 문화컨텐츠 전략이 통했다는 이야기도 있다. ",
                    "본래 일본말인 스시라는 말이 사용되었으나 1940년대 생선을 밥(배합초[1])위에 얹어서 먹는다고 하여 생선 초밥(줄여서 초밥 또는 회초밥)이라는 말이 대체용어로 사용되었고 이후 완전히 대체하였다. 그러다 2000년대 중반부에 들어서는 일본어 용어가 유입이 다시 들어오기도 하고, 초밥의 세계화로 스시가 이 요리의 국제 명칭(영어로 Sushi)이 되기도 하여 스시 역시 초밥과 함께 한국에서 혼용된다.",
                    "일본 요리의 하나로 얇게 저민 쇠고기[1]와 갖가지 채소를 끓는 육수에 즉석에서 데쳐서 양념장에 찍어 먹는 요리. 그냥 물에 데쳐먹는 게 절대 아니다. 육류를 사용하는 몇 안 되는 일본 요리이기도 하다. 국립국어원 외래어 표기법으로는 샤부샤부. 이름은 '찰랑찰랑', '살짜기' 라는 의미의 의태어에서 유래했다.",
                    "밥 위에 고기, 야채, 소스 등을 넣고 같이 섞어 먹는 요리의 일종이다. 쉽게 말해 '밥 위에 반찬을 얹어 먹는 요리'다고 표현할 수 있다. 한마디로 밥과 함께 먹는 것이라면 무엇이든 덮밥의 재료가 가능하다. 그냥 밥과 반찬의 관계로서 섭취하는 것과 다를 게 무엇일까 하는 말들도 있지만, 차이가 전혀 없음에도 특이하게 맛이 다르다는 게 매력이다."},
            {"일반적으로 커피 열매(커피체리)의 씨앗인 커피 콩, 혹은 그 씨앗을 볶은 뒤 갈아서 물에 우려내서 만드는 음료.",
                    "가장 기초가 되는 맛은 쓴맛 물론 다양한 세계식품기호에 맞게 각국의 커피맛이 어느정도 차이를 보이긴 하고 신맛을 포함해서 느껴지는 다른맛과 향도 커피를 평가하는데 있어 중요하게 여기지만 커피에게 쓴맛은 거의 정의나 다름없게 평가된다. ",
                    "커피나무의 열매, 열매 속의 씨앗, 그 씨앗을 박피·건조하여 만든 생두, 생두를 볶은 커피 원두, 원두를 분쇄한 커피가루, 가루에서 추출한 음료까지 광범위하게 '커피'라고 부르고 있다. 에티오피아에선 '분나'(ቡና, bunna)라고 부르며, 아랍어에서도 커피콩은 에티오피아어를 따라 분(بن, bunn)이라고 부른다. ",
                    "우유를 넣은 커피의 일종이다. 이탈리아어로 카페는 커피를, 라테는 우유를 뜻한다. 때문에 커피에 우유를 넣은 것.[1] 시중에서도 흔히 접할 수 있는 종류 중 하나이다.",
                    "카카오 열매의 씨앗인 카카오 빈을 가공한 것으로, 정확히는 카카오 매스를 압착하여 카카오 버터를 빼고 남은 부산물인 카카오 케이크를 분쇄한 것이다. 카카오 파우더는 물에 잘 섞이기 때문에 음료나 과자류의 제조에 쓰인다.",
                    "단일 재료가 아닌 여러가지 재료를 섞어 만든 쥬스. 엄밀히 말하자면 시중에 판매되는 거의 모든 쥬스는 사실상 믹스쥬스라고 해야 할 것이다. 하지만 일반적으로는 여러가지 과일이나 채소를 한꺼번에 갈아 만든 쥬스를 믹스쥬스라고 칭한다."},
            {"흔히 케이크라고 하면 높이가 높은 원형 혹은 상자형을 바탕으로 한 모양 케이크에 생크림이나 녹인 초콜릿 등으로 겉을 싸고 그 위에 각종 장식을 얹은 식품을 가리키는 경우가 많다. 흔히 생일을 기념하는 음식으로 나온다.",
                    "서양에서는 기원전 300년경 마케도니아 왕국의 알렉산더 대왕이 페르시아 제국을 점령할 때 만들어 먹었다는 설도 있는데, 병사들이 더위와 피로에 지쳐 쓰러지자 높은 산에 쌓인 눈을 그릇에 담아 꿀과 과일즙 등을 섞어 먹었다고 한다. 또 로마의 정치가이자 장군인 카이사르는 알프스에서 가져온 얼음과 눈으로 술과 우유를 차게 해서 마셨다고 한다.",
                    "오늘날 우리가 알고 있는 마카롱이 프랑스에서 만들어져서 프랑스가 마카롱 본산지로 알려져 있지만 실제 마카롱의 창안국은 이탈리아다. 사실 마카롱은 프랑스 일부 지방에서만 유명한 편. 16세기 중반 이탈리아 피렌체의 귀족 카트린 드 메디시스가 프랑스 국왕 앙리 2세에게 시집올 때 준비한 혼수품 중 포크, 향신료, 셔벗, 마카롱 등이 있었던 것.",
                    "치즈를 주 재료로 만들어진 커스터드 파이의 한 종류.[1] 후식 파이 중 가장 오래된 역사를 자랑한다. 기원전 8세기경 그리스를 비롯한 근동에서 만들어진 요리다. 로마 제국 때도 연회 음식으로 제공되었다. 겉은 갈색에 속은 노랗고 부드러운 현대적인 형태는 19세기 후반 미 동부 뉴욕과 필라델피아에서 정립되었다. 커피와는 최강의 궁합을 자랑하는 디저트로 손꼽힌다."}
    };

}

import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 100,         // 동시 사용자 100명
  duration: '10s'   // 10초 동안 테스트
};

export default function () {

  let url = "http://127.0.0.1:8080/public/search/v2?page=1&userId=2";

  let payload = JSON.stringify({
     "content" : "",
     "region": {
       "강원": 0,
       "경기": 0,
       "경남": 0,
       "경북": 0,
       "광주": 0,
       "대구": 0,
       "대전": 0,
       "부산": 0,
       "서울": 0,
       "인천": 0,
       "전남": 0,
       "전북": 0,
       "제주": 0,
       "충남": 0,
       "충북": 0
     },
     "type": {
       "당일형": 0,
       "휴식형": 0,
       "체험형": 0
     },
     "purpose": {
       "힐링": 0,
       "전통문화 체험": 0,
       "심신치유": 0,
       "자기계발": 0,
       "여행 일정": 0,
       "사찰순례": 0,
       "휴식": 0,
       "호기심": 0
     },
     "activity": {
       "발우공양": 0,
       "108배": 0,
       "스님과의 차담": 0,
       "등산": 0,
       "새벽 예불": 0,
       "사찰 탐방": 0,
       "염주 만들기": 0,
       "연등 만들기": 0,
       "다도": 0,
       "명상": 0,
       "산책": 0,
       "요가": 0,
       "기타": 0
     },
     "price": {
       "minPrice": 0,
       "maxPrice": 300000
     },
     "etc": {
       "절밥이 맛있는": 0,
       "TV에 나온": 0,
       "연예인이 다녀간": 0,
       "근처 관광지가 많은": 0,
       "속세와 멀어지고 싶은": 0,
       "단체 가능": 0,
       "동물 친구들과 함께": 0,
       "유튜브 운영 중인": 0
     }
  });

  let params = {
    headers: { "Content-Type": "application/json" }
  };

  let res = http.post(url, payload, params);

  // ✅ 응답 코드 및 성능 체크
  check(res, {
    '응답 코드가 200인가?': (r) => r.status === 200,
    '응답 시간이 500ms 이하인가?': (r) => r.timings.duration < 500,
  });

  sleep(1); // 1초 대기 후 다음 요청 실행
}

import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 10,         // 동시 사용자 10명
  duration: '600s'  // 10분 동안 테스트
};

export default function () {
  let url = 'http://127.0.0.1:8080/public/filter/count/v2'; // API 엔드포인트

  let payload = JSON.stringify({
       "content" : " 사",
       "region": {
         "강원": 0,
         "경기": 1,
         "경남": 1,
         "경북": 1,
         "광주": 0,
         "대구": 0,
         "대전": 0,
         "부산": 0,
         "서울": 1,
         "인천": 0,
         "전남": 0,
         "전북": 0,
         "제주": 0,
         "충남": 0,
         "충북": 0
       },
       "type": {
         "당일형": 1,
         "휴식형": 1,
         "체험형": 1
       },
       "purpose": {
         "힐링": 1,
         "전통문화 체험": 0,
         "심신치유": 1,
         "자기계발": 0,
         "여행 일정": 0,
         "사찰순례": 1,
         "휴식": 1,
         "호기심": 0
       },
       "activity": {
         "발우공양": 1,
         "108배": 1,
         "스님과의 차담": 1,
         "등산": 1,
         "새벽 예불": 0,
         "사찰 탐방": 0,
         "염주 만들기": 0,
         "연등 만들기": 0,
         "다도": 0,
         "명상": 0,
         "산책": 0,
         "요가": 0,
         "기타": 1
       },
       "price": {
         "minPrice": 0,
         "maxPrice": 200000
       },
       "etc": {
         "절밥이 맛있는": 0,
         "TV에 나온": 0,
         "연예인이 다녀간": 1,
         "근처 관광지가 많은": 1,
         "속세와 멀어지고 싶은": 1,
         "단체 가능": 0,
         "동물 친구들과 함께": 1,
         "유튜브 운영 중인": 1
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

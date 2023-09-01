-- 부맛 비번 - mrboo123!
-- 관리자 비번 - admin123!

INSERT INTO user_tb (email, password, provider, role, nick_name, profile_image_url,
                     about_me, notification_flag, badge_flag, status, created_at, updated_at)
VALUES ('boo@gmail.com', '$2y$12$LY3/pPv/tmfIRLiIccK51.SlwTnVQJqIwR40RAYafDRWqvq40e3XS', 'ME', 'USER', '부맛', NULL,
        NULL, 'Y', 'N', 'NORMAL', '2023-08-01', '2023-08-01'),
       ('boo2@gmail.com', '$2y$12$LY3/pPv/tmfIRLiIccK51.SlwTnVQJqIwR40RAYafDRWqvq40e3XS', 'ME', 'USER', '부맛2', NULL,
        NULL, 'Y', 'N', 'NORMAL', '2023-08-01', '2023-08-01'),
       ('boo3@gmail.com', '$2y$12$LY3/pPv/tmfIRLiIccK51.SlwTnVQJqIwR40RAYafDRWqvq40e3XS', 'ME', 'USER', '부맛3', NULL,
        NULL, 'Y', 'N', 'NORMAL', '2023-08-01', '2023-08-01'),
       ('boo4@gmail.com', '$2y$12$LY3/pPv/tmfIRLiIccK51.SlwTnVQJqIwR40RAYafDRWqvq40e3XS', 'ME', 'USER', '부맛4', NULL,
        NULL, 'Y', 'N', 'NORMAL', '2023-08-01', '2023-08-01'),
       ('boo5@gmail.com', '$2y$12$LY3/pPv/tmfIRLiIccK51.SlwTnVQJqIwR40RAYafDRWqvq40e3XS', 'ME', 'USER', '부맛5', NULL,
        NULL, 'Y', 'N', 'NORMAL', '2023-08-01', '2023-08-01'),
       ('admin@gmail.com', '$2a$12$G18OjisaPXaux8t7KH9bX.uECo8eiIx26hREWjpYG6dIxV6S18N4q', 'ME', 'ADMIN', '관리자', NULL,
        NULL, 'N', 'N', 'NORMAL', '2023-08-01', '2023-08-01');

INSERT INTO follow_tb (following_id, followed_id, created_at)
VALUES (1, 2, date('2023-08-02')),
       (2, 3, date('2023-08-02')),
       (1, 3, date('2023-08-02')),
       (1, 4, date('2023-08-02')),
       (1, 5, date('2023-08-02'));

INSERT INTO restaurant_tb (kakao_place_id, name, phone, category, link, mapX, mapY, address, road_address)
VALUES ('1849914946', '뻘다방', '032-889-8300', '음식점 > 카페 > 커피전문점', 'http://place.map.kakao.com/1849914946',
        '126.53125963775', '37.2341514661953', '인천 옹진군 영흥면 선재리 148-2', '인천 옹진군 영흥면 선재로 55'),
       ('186032184', '모이핀', '0507-1477-6003', '음식점 > 카페 > 커피전문점', 'http://place.map.kakao.com/186032184',
        '127.77908440127192', '34.687850799489055', '전남 여수시 돌산읍 평사리 1273-5', '전남 여수시 돌산읍 무술목길 50'),
       ('528293263', '웨이브온커피', '051-727-1660', '음식점 > 카페 > 커피전문점', 'http://place.map.kakao.com/528293263',
        '129.269784558837', '35.3222915727433', '부산 기장군 장안읍 월내리 553', '부산 기장군 장안읍 해맞이로 286'),
       ('21344157', '테라로사 커피공장 강릉본점', '033-648-2760', '음식점 > 카페 > 커피전문점', 'http://place.map.kakao.com/2134415',
        '128.8925581489692', '37.69650003203305', '강원특별자치도 강릉시 구정면 어단리 산 314', '강원특별자치도 강릉시 구정면 현천길 7'),
       ('87323834', '더스팟 패뷸러스', '02-779-1981', '음식점 > 카페 > 커피전문점', 'http://place.map.kakao.com/87323834',
        '126.982601390472', '37.5623324834706', '서울 중구 명동2가 105', '서울 중구 명동2길 22');

INSERT INTO feed_tb (restaurant_id, user_id, thumbnail_url, content, status, created_at, updated_at)
VALUES (1, 1, 'https://foodielog-bucket.s3.ap-northeast-2.amazonaws.com/55e8cfbc-5988-48ec-a843-a90869f9ee71.jpg',
        '부맛의 맛집을 소개합니다.', 'NORMAL', '2023-08-03', '2023-08-03'),
       (2, 2, 'https://foodielog-bucket.s3.ap-northeast-2.amazonaws.com/cf0939f2-6041-4153-b9de-5033c82746b3.jpg',
        '부맛2의 맛집을 소개합니다.', 'NORMAL', '2023-08-03', '2023-08-03'),
       (3, 3, 'https://foodielog-bucket.s3.ap-northeast-2.amazonaws.com/855d02df-f73c-4c66-92d6-b135a8e58246.jpg',
        '부맛3의 맛집을 소개합니다.', 'NORMAL', '2023-08-03', '2023-08-03'),
       (4, 4, 'https://foodielog-bucket.s3.ap-northeast-2.amazonaws.com/94d59641-dd4c-41d2-9e72-1d8616da7c6e.png',
        '부맛4의 맛집을 소개합니다.', 'NORMAL', '2023-08-03', '2023-08-03'),
       (5, 5, 'https://foodielog-bucket.s3.ap-northeast-2.amazonaws.com/b65a31fc-9942-4117-9b3c-c8cd674bb88e.jpeg',
        '부맛5의 맛집을 소개합니다.', 'NORMAL', '2023-08-03', '2023-08-03');

INSERT INTO restaurant_like_tb (restaurant_id, user_id, created_at)
VALUES (1, 1, '2023-08-01'),
       (2, 2, '2023-08-02'),
       (3, 3, '2023-08-03'),
       (4, 4, '2023-08-04'),
       (5, 5, '2023-08-04');

INSERT INTO media_tb (feed_id, image_url, created_at)
VALUES (1, 'https://foodielog-bucket.s3.ap-northeast-2.amazonaws.com/55e8cfbc-5988-48ec-a843-a90869f9ee71.jpg',
        '2023-08-03'),
       (2, 'https://foodielog-bucket.s3.ap-northeast-2.amazonaws.com/cf0939f2-6041-4153-b9de-5033c82746b3.jpg',
        '2023-08-03'),
       (3, 'https://foodielog-bucket.s3.ap-northeast-2.amazonaws.com/855d02df-f73c-4c66-92d6-b135a8e58246.jpg',
        '2023-08-03'),
       (4, 'https://foodielog-bucket.s3.ap-northeast-2.amazonaws.com/94d59641-dd4c-41d2-9e72-1d8616da7c6e.png',
        '2023-08-03'),
       (5, 'https://foodielog-bucket.s3.ap-northeast-2.amazonaws.com/b65a31fc-9942-4117-9b3c-c8cd674bb88e.jpeg',
        '2023-08-03');

INSERT INTO report_tb (reporter_id, reported_id, type, content_id, report_reason, status, created_at, updated_at)
VALUES (1, 2, 'FEED', 2, '광고', 'UNPROCESSED', '2023-08-10', '2023-08-10'),
       (1, 2, 'REPLY', 6, '광고', 'UNPROCESSED', '2023-08-10', '2023-08-10');

INSERT INTO reply_tb (user_id, feed_id, content, status, created_at, updated_at)
VALUES (1, 2, '너무 맛있어 보여요!', 'NORMAL', '2023-08-10', '2023-08-10'),
       (3, 2, '저도 가봐야 겠어요!', 'NORMAL', '2023-08-10', '2023-08-10'),
       (2, 1, '저희 집 근처네요!', 'NORMAL', '2023-08-10', '2023-08-10'),
       (5, 1, '카페가 너무 이뻐요!', 'NORMAL', '2023-08-10', '2023-08-10'),
       (4, 3, '빵이 맛있어 보여요!', 'NORMAL', '2023-08-10', '2023-08-10'),
       (2, 1, '@@ 오픈 이벤트 진행 중 입니다~ 프로필 상단 링크 확인 @@', 'NORMAL', '2023-08-10', '2023-08-10');

INSERT INTO feed_like_tb (feed_id, user_id, created_at)
VALUES (1, 2, '2023-08-12'),
       (2, 3, '2023-08-12'),
       (3, 4, '2023-08-12'),
       (4, 5, '2023-08-12'),
       (5, 1, '2023-08-12');

INSERT INTO notification_tb (user_id, type, content_id, check_flag, created_at)
VALUES (2, 'REPLY', 1, 'N', '2023-08-10'),
       (2, 'REPLY', 2, 'N', '2023-08-10'),
       (1, 'REPLY', 3, 'N', '2023-08-10'),
       (1, 'REPLY', 4, 'N', '2023-08-10'),
       (3, 'REPLY', 5, 'N', '2023-08-10'),
       (2, 'FOLLOW', 1, 'N', '2023-08-02'),
       (3, 'FOLLOW', 2, 'N', '2023-08-02'),
       (3, 'FOLLOW', 3, 'N', '2023-08-02'),
       (4, 'FOLLOW', 4, 'N', '2023-08-02'),
       (5, 'FOLLOW', 5, 'N', '2023-08-02'),
       (1, 'LIKE', 1, 'N', '2023-08-12'),
       (2, 'LIKE', 2, 'N', '2023-08-12'),
       (3, 'LIKE', 3, 'N', '2023-08-12'),
       (4, 'LIKE', 4, 'N', '2023-08-12'),
       (5, 'LIKE', 5, 'N', '2023-08-12');
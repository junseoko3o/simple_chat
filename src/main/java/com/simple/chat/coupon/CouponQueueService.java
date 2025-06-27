package com.simple.chat.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CouponQueueService {

    private static final String QUEUE_KEY = "coupon:queue";
    private static final String USER_SET_KEY = "coupon:users"; // 중복 체크용
    private static final int MAX_COUPONS = 100;

    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  // 대문자 + 숫자
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 대기열에 사용자 등록 (중복 허용 안함)
     * @param userEmail
     * @return true: 등록 성공, false: 이미 대기 중
     */
    public boolean enqueueUser(String userEmail) {
        Boolean already = redisTemplate.opsForSet().isMember(USER_SET_KEY, userEmail);
        if (Boolean.TRUE.equals(already)) {
            return false;  // 중복 대기 불가
        }

        // 현재 시간 기준 점수로 ZSET에 넣음
        long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(QUEUE_KEY, userEmail, now);
        redisTemplate.opsForSet().add(USER_SET_KEY, userEmail);
        return true;
    }

    /**
     * 대기열에서 사용자 제거
     * @param userEmail
     */
    public void dequeueUser(String userEmail) {
        redisTemplate.opsForZSet().remove(QUEUE_KEY, userEmail);
        redisTemplate.opsForSet().remove(USER_SET_KEY, userEmail);
    }

    /**
     * 대기열에서 가장 오래 기다린 사용자 한 명 꺼내서 쿠폰 발급 시도
     * @return 발급된 사용자 이메일 또는 null (대기열 비었거나 재고 소진)
     */
    public String issueCouponIfAvailable() {
        Long currentStock = getCouponStock();
        if (currentStock <= 0) {
            return null; // 재고 없음
        }

        Set<String> users = redisTemplate.opsForZSet().range(QUEUE_KEY, 0, 0); // 가장 오래된 1명
        if (users == null || users.isEmpty()) {
            return null; // 대기열 없음
        }
        String userEmail = users.iterator().next();

        // 쿠폰 재고 차감 (원자적 처리 위해 Lua스크립트 쓰면 더 좋음)
        redisTemplate.opsForZSet().remove(QUEUE_KEY, userEmail);
        redisTemplate.opsForSet().remove(USER_SET_KEY, userEmail);

        decreaseCouponStock();

        // 쿠폰 코드 생성 (랜덤)
        String couponCode = generateCouponCode(10).toUpperCase();

        // Kafka 이벤트 발송 (발급 성공)
        String eventMsg = String.format("{\"user\":\"%s\", \"coupon\":\"%s\", \"status\":\"ISSUED\"}", userEmail, couponCode);
        kafkaTemplate.send("coupon-events", userEmail, eventMsg);

        return userEmail;
    }

    /**
     * 쿠폰 재고 초기화 (예: 100개)
     */
    public void initCouponStock() {
        redisTemplate.opsForValue().set("coupon:stock", String.valueOf(MAX_COUPONS));
    }

    public Long getCouponStock() {
        String stock = redisTemplate.opsForValue().get("coupon:stock");
        return stock == null ? 0L : Long.parseLong(stock);
    }

    public void decreaseCouponStock() {
        redisTemplate.opsForValue().decrement("coupon:stock");
    }

    /**
     * 대기열 순서 조회
     */
    public Long getUserPosition(String userEmail) {
        Long rank = redisTemplate.opsForZSet().rank(QUEUE_KEY, userEmail);
        return rank == null ? -1 : rank + 1; // 순위는 1부터
    }

    public static String generateCouponCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHAR_POOL.length());  // CHAR_POOL에서 랜덤 인덱스를 선택
            sb.append(CHAR_POOL.charAt(index));  // 랜덤 문자 추가
        }
        return sb.toString();  // 생성된 랜덤 문자열 반환
    }

}

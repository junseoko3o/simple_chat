package com.simple.chat.controller;

import com.simple.chat.coupon.CouponQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponQueueService couponQueueService;

    // 대기열에 등록 요청
    @PostMapping("/join")
    public String joinQueue(@RequestParam String email) {
        boolean success = couponQueueService.enqueueUser(email);
        if (!success) {
            return "이미 대기 중입니다.";
        }
        return "대기열에 등록되었습니다. 현재 순서: " + couponQueueService.getUserPosition(email);
    }

    // 현재 대기순서 확인
    @GetMapping("/position")
    public String position(@RequestParam String email) {
        long pos = couponQueueService.getUserPosition(email);
        if (pos == -1) return "대기열에 없습니다.";
        return "현재 대기 순서: " + pos;
    }

    // 테스트용 쿠폰 재고 초기화
    @PostMapping("/init")
    public String init() {
        couponQueueService.initCouponStock();
        return "쿠폰 재고 100개 초기화 완료";
    }

    // 관리자용 : 발급 시도 (실제로는 백그라운드 작업으로 주기적 호출 권장)
    @PostMapping("/issue")
    public String issue() {
        String user = couponQueueService.issueCouponIfAvailable();
        if (user == null) return "발급 대상 없음 또는 재고 소진";
        return user + "님에게 쿠폰 발급 완료";
    }
}
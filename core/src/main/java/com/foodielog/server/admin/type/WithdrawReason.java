package com.foodielog.server.admin.type;

public enum WithdrawReason {
    ADVERTISEMENT("광고가 많아서"),
    INFREQUENTLY_USED("자주 이용하지 않아서"),
    USE_OTHER_SITES("비슷한 타 사이트를 이용하기 위해서"),
    UNSATISFACTORY_SUPPORT("고객 지원이 만족스럽지 못해서"),
    ETC("기타");

    private final String label;

    WithdrawReason(String label) {
        this.label = label;
    }
}

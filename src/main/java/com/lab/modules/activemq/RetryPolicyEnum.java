package com.lab.modules.activemq;
import java.util.Arrays;
import java.util.List;


public enum RetryPolicyEnum  {
    RAW_QUEUE("RAW.QUEUE", 1000, 3000, 5000, 10000, 15000),
    ORDER_QUEUE("ORDER.QUEUE", 1000, 3000, 5000, 15000),
    ORDER_TEXT_QUEUE("ORDER.TEXT.QUEUE", 1000, 3000, 5000),
    PRIORITY_ORDER_QUEUE("PRIORITY.ORDER.QUEUE", 1000, 3000, 5000),
    TEST_DLQ_QUEUE("TEST.DLQ.QUEUE", 0)
    ;

    private String queueName;
    private final List<Long> delays;

    RetryPolicyEnum(String queueName, long... delays) {
        this.queueName = queueName;
        this.delays = Arrays.stream(delays).boxed().toList();
    }

    public String getQueueName() {
        return queueName;
    }

    public List<Long> getDelays() {
        return delays;
    }

    // ===== STATIC LOOKUP =====
    public static RetryPolicyEnum fromQueue(String queueName) {
        for (RetryPolicyEnum policy : values()) {
            if (policy.queueName.equals(queueName)) {
                return policy;
            }
        }
        throw new IllegalArgumentException("No retry policy for queue: " + queueName);
    }

    public static long getDelay(String queueName, int retryCount) {
        RetryPolicyEnum policy = fromQueue(queueName);

        if (retryCount >= policy.delays.size()) {
            return -1;
        }

        return policy.delays.get(retryCount);
    }
}

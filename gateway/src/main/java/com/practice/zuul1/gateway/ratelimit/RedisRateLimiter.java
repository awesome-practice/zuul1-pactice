package com.practice.zuul1.gateway.ratelimit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.validation.constraints.Min;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author Luo Bao Ding
 * @since 2018/7/3
 */
public class RedisRateLimiter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisRateLimiter.class);
    private StringRedisTemplate redisTemplate;

    @Min(1)
    private int replenishRate;

    @Min(1)
    private int burstCapacity = 1;

    public RedisRateLimiter(StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = stringRedisTemplate;
    }

    public boolean isAllowed(String id) {

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";

        long now = Instant.now().getEpochSecond();
        long requestedTokens = 1;

        //  [[[[[[[[

        long fillTime = burstCapacity / replenishRate;
        long timeOut = ((long) (Math.floor(fillTime * 2)));


        long lastTokens = getRedisValue(tokenKey, burstCapacity);
        long lastRefreshed = getRedisValue(timestampKey, now);

        long elapsedTime = Math.max(0, now - lastRefreshed);
        long filledTokens = Math.min(burstCapacity, lastTokens + elapsedTime * replenishRate);
        boolean isAllowed = filledTokens >= requestedTokens;
        long tokensLeft;
        if (isAllowed) {
            tokensLeft = filledTokens - requestedTokens;
        } else {
            tokensLeft = filledTokens;
        }
        redisTemplate.opsForValue().set(tokenKey, tokensLeft + "", timeOut, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(timestampKey, now + "", timeOut, TimeUnit.SECONDS);

//       ]]]]]]]]]]
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("id=[" + id + "],isAllowed=[" + isAllowed + "],tokensLeft=[" + tokensLeft + "]");
        }
        return isAllowed;
    }

    private long getRedisValue(String key, long firstValue) {
        String s = redisTemplate.opsForValue().get(key);
        long val;
        if (s == null || "".equals(s)) {
            val = firstValue;
        } else {
            val = Long.parseLong(s);
        }
        return val;
    }

    //    ~~~~~~~~~~~~
    public int getReplenishRate() {
        return replenishRate;
    }

    public void setReplenishRate(int replenishRate) {
        this.replenishRate = replenishRate;
    }

    public int getBurstCapacity() {
        return burstCapacity;
    }

    public void setBurstCapacity(int burstCapacity) {
        this.burstCapacity = burstCapacity;
    }
}

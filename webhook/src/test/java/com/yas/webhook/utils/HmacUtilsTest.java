package com.yas.webhook.utils;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class HmacUtilsTest {

    @Test
    void hash_WhenValidData_ReturnsHashedString() throws NoSuchAlgorithmException, InvalidKeyException {
        String data = "test data";
        String key = "test key";
        String result = HmacUtils.hash(data, key);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
    }
}

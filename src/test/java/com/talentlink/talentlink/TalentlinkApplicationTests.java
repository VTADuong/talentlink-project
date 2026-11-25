package com.talentlink.talentlink;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TalentlinkApplicationTests {

    @Test
    @DisplayName("Khởi tạo cấu hình ứng dụng TalentLink")
    void contextLoads() {
        TalentlinkApplication application = new TalentlinkApplication();
        assertNotNull(application);
    }
}

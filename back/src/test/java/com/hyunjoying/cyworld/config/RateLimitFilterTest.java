//package com.hyunjoying.cyworld.config;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import com.hyunjoying.cyworld.common.service.FileUploadService;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@DisplayName("Rate Limit 테스트 (직접 구현 필터)")
//public class RateLimitFilterTest {
//
//    @TestConfiguration
//    static class RateLimitFilterTestConfig {
//        @Bean
//        public FileUploadService fileUploadService() {
//            return Mockito.mock(FileUploadService.class);
//        }
//    }
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private RateLimitingFilter rateLimitingFilter;
//
//    @BeforeEach
//    void clearCache() {
//        rateLimitingFilter.clear();
//    }
//
//    @Test
//    @DisplayName("20회 요청 후 21번째 요청 시 429 에러")
//    void testRateLimiting() throws Exception {
//        String endpoint = "/auth/check-availability";
//
//        for (int i = 0; i < 20; i++) {
//            mockMvc.perform(get(endpoint)
//                            .param("loginId", "testUser" + i))
//                    .andExpect(status().isOk());
//        }
//
//        mockMvc.perform(get(endpoint)
//                        .param("loginId", "testUser21"))
//                .andExpect(status().isTooManyRequests());
//    }
//}
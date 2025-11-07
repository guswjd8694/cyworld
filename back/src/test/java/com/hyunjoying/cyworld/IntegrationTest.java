//package com.hyunjoying.cyworld;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hyunjoying.cyworld.common.service.FileUploadService;
//import com.hyunjoying.cyworld.config.RateLimitingFilter;
//import org.junit.jupiter.api.BeforeEach;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//import static org.mockito.BDDMockito.given;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//public abstract class IntegrationTest {
//
//    @Autowired
//    protected MockMvc mockMvc;
//
//    @Autowired
//    protected ObjectMapper objectMapper;
//
//    @MockBean
//    private AuditorAware<Integer> auditorProvider;
//
//    @Autowired
//    private RateLimitingFilter rateLimitingFilter;
//
//    @TestConfiguration
//    static class IntegrationTestConfig {
//        @Bean
//        public FileUploadService fileUploadService() {
//            return Mockito.mock(FileUploadService.class);
//        }
//    }
//
//    @BeforeEach
//    protected void integrationTestSetup() {
//        rateLimitingFilter.clear();
//        given(auditorProvider.getCurrentAuditor()).willReturn(Optional.of(1));
//    }
//}
import axios from 'axios';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
});

// 요청 인터셉터: 모든 요청 헤더에 JWT 토큰 추가
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('jwt-token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// 응답 인터셉터: 401 에러 발생 시 로그아웃 이벤트 발생
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            console.log('Axios Interceptor: 401 Error detected! Firing auth-error event.');
            window.dispatchEvent(new Event('auth-error'));
        }
        return Promise.reject(error);
    }
);

export default apiClient;
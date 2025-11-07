import axios from 'axios';

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    }
});

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

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            window.dispatchEvent(new Event('auth-error'));
        } 
        else if (error.response && error.response.status === 429) {
            alert('요청 횟수가 너무 많습니다. 1분 후 다시 시도해 주세요.');
        }
        return Promise.reject(error);
    }
);

export default apiClient;


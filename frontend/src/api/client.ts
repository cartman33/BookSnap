import axios from "axios";

/**
 * 백엔드 서버의 기본 URL을 결정합니다.
 * 환경 변수 EXPO_PUBLIC_API_BASE_URL을 우선적으로 사용하며, 없을 경우 로컬 호스트를 기본값으로 사용합니다.
 */
function resolveBaseUrl() {
  const envUrl = process.env.EXPO_PUBLIC_API_BASE_URL;
  if (envUrl && envUrl.trim().length > 0) return envUrl.trim().replace(/\/+$/, "");

  // 로컬 개발 환경을 위한 폴백 설정
  // - Android 에뮬레이터: http://10.0.2.2:9090
  // - iOS 시뮬레이터 및 웹: http://localhost:9090
  return "http://localhost:9090";
}

/**
 * Axios 인스턴스 설정
 * 모든 API 요청은 이 인스턴스를 통해 수행됩니다.
 */
export const api = axios.create({
  baseURL: resolveBaseUrl(),
  timeout: 15_000, // 요청 제한 시간 15초
  headers: { "Content-Type": "application/json" },
});


import axios from "axios";

function resolveBaseUrl() {
  const envUrl = process.env.EXPO_PUBLIC_API_BASE_URL;
  if (envUrl && envUrl.trim().length > 0) return envUrl.trim().replace(/\/+$/, "");

  // Fallback for local dev. For real devices, set EXPO_PUBLIC_API_BASE_URL to your PC's LAN IP.
  // - Android emulator: http://10.0.2.2:9090
  // - iOS simulator: http://localhost:9090
  // - Web: http://localhost:9090
  return "http://localhost:9090";
}

export const api = axios.create({
  baseURL: resolveBaseUrl(),
  timeout: 15_000,
  headers: { "Content-Type": "application/json" },
});


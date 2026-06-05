import React, { useState, useMemo } from "react";
import {
  ActivityIndicator,
  Alert,
  Button,
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
  View,
  TouchableOpacity,
} from "react-native";

import { api } from "../api/client";

type TokenResponse = { accessToken: string; refreshToken: string };
type Mode = "login" | "signup";

/**
 * 로그인/회원가입 화면
 */
export default function LoginScreen({ onLoginSuccess }: { onLoginSuccess: () => void }) {
  const [mode, setMode] = useState<Mode>("login");
  const [loading, setLoading] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [errorText, setErrorText] = useState<string | null>(null);

  const submitLabel = useMemo(() => (mode === "login" ? "로그인" : "회원가입"), [mode]);

  const onSubmit = async () => {
    try {
      setLoading(true);
      setErrorText(null);

      const endpoint = mode === "login" ? "/api/auth/login" : "/api/auth/signup";
      const payload =
        mode === "login"
          ? { email, password }
          : { email, password, nickname: nickname || "사용자" };

      const { data } = await api.post<TokenResponse>(endpoint, payload);
      api.defaults.headers.common.Authorization = `Bearer ${data.accessToken}`;
      
      onLoginSuccess();
    } catch (e: any) {
      const msg = e?.response?.data?.message ?? e?.message ?? "오류가 발생했습니다.";
      setErrorText(String(msg));
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.container}>
        <Text style={styles.title}>BookSnap</Text>
        <Text style={styles.subTitle}>나만의 문장 수집 다이어리</Text>

        <View style={styles.toggleRow}>
          <TouchableOpacity 
            style={[styles.toggleBtn, mode === "login" && styles.activeToggle]}
            onPress={() => setMode("login")}
          >
            <Text style={[styles.toggleText, mode === "login" && styles.activeToggleText]}>로그인</Text>
          </TouchableOpacity>
          <TouchableOpacity 
            style={[styles.toggleBtn, mode === "signup" && styles.activeToggle]}
            onPress={() => setMode("signup")}
          >
            <Text style={[styles.toggleText, mode === "signup" && styles.activeToggleText]}>회원가입</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.form}>
          <TextInput
            style={styles.input}
            value={email}
            onChangeText={setEmail}
            autoCapitalize="none"
            keyboardType="email-address"
            placeholder="이메일"
          />
          <TextInput
            style={styles.input}
            value={password}
            onChangeText={setPassword}
            secureTextEntry
            placeholder="비밀번호"
          />
          {mode === "signup" && (
            <TextInput
              style={styles.input}
              value={nickname}
              onChangeText={setNickname}
              placeholder="닉네임"
            />
          )}
        </View>

        {errorText && <Text style={styles.error}>{errorText}</Text>}

        <TouchableOpacity 
          style={styles.submitBtn} 
          onPress={onSubmit}
          disabled={loading}
        >
          {loading ? <ActivityIndicator color="#FFF" /> : <Text style={styles.submitBtnText}>{submitLabel}</Text>}
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: "#FAF7F2" },
  container: { flex: 1, padding: 30, justifyContent: "center" },
  title: { fontSize: 36, fontWeight: "800", color: "#8E806A", textAlign: "center", marginBottom: 8 },
  subTitle: { fontSize: 16, color: "#666", textAlign: "center", marginBottom: 40 },
  toggleRow: { flexDirection: "row", marginBottom: 30, backgroundColor: "#E8E2D6", borderRadius: 12, padding: 4 },
  toggleBtn: { flex: 1, paddingVertical: 10, alignItems: "center", borderRadius: 8 },
  activeToggle: { backgroundColor: "#FFF" },
  toggleText: { color: "#8E806A", fontWeight: "600" },
  activeToggleText: { color: "#1F1F1F" },
  form: { gap: 12, marginBottom: 20 },
  input: { backgroundColor: "#FFF", borderWidth: 1, borderColor: "#DDD", borderRadius: 10, padding: 15, fontSize: 16 },
  error: { color: "#B00020", textAlign: "center", marginBottom: 10 },
  submitBtn: { backgroundColor: "#8E806A", borderRadius: 10, padding: 16, alignItems: "center" },
  submitBtnText: { color: "#FFF", fontSize: 18, fontWeight: "700" },
});

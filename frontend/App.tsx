import React, { useMemo, useState } from "react";
import {
  ActivityIndicator,
  Alert,
  Button,
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

import { api } from "./src/api/client";

type TokenResponse = { accessToken: string; refreshToken: string };
type Mode = "login" | "signup";

export default function App() {
  const [mode, setMode] = useState<Mode>("login");

  const [loading, setLoading] = useState(false);
  const [token, setToken] = useState<TokenResponse | null>(null);
  const [errorText, setErrorText] = useState<string | null>(null);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");

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
      setToken(data);
      api.defaults.headers.common.Authorization = `Bearer ${data.accessToken}`;
      Alert.alert("성공", mode === "login" ? "로그인되었습니다." : "회원가입되었습니다.");
    } catch (e: any) {
      const msg =
        e?.response?.data?.message ??
        e?.message ??
        "요청 처리 중 오류가 발생했습니다.";
      setErrorText(String(msg));
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.container}>
        <Text style={styles.title}>BookSnap</Text>
        <Text style={styles.subTitle}>회원가입 / 로그인</Text>

        <View style={styles.toggleRow}>
          <Button
            title="로그인"
            onPress={() => setMode("login")}
            disabled={mode === "login" || loading}
          />
          <View style={{ width: 12 }} />
          <Button
            title="회원가입"
            onPress={() => setMode("signup")}
            disabled={mode === "signup" || loading}
          />
        </View>

        <View style={styles.form}>
          <Text style={styles.label}>이메일</Text>
          <TextInput
            style={styles.input}
            value={email}
            onChangeText={setEmail}
            autoCapitalize="none"
            keyboardType="email-address"
            placeholder="you@example.com"
            editable={!loading}
          />

          <Text style={styles.label}>비밀번호</Text>
          <TextInput
            style={styles.input}
            value={password}
            onChangeText={setPassword}
            secureTextEntry
            placeholder="8자 이상"
            editable={!loading}
          />

          {mode === "signup" ? (
            <>
              <Text style={styles.label}>닉네임</Text>
              <TextInput
                style={styles.input}
                value={nickname}
                onChangeText={setNickname}
                placeholder="사용자"
                editable={!loading}
              />
            </>
          ) : null}
        </View>

        {loading ? (
          <View style={styles.loadingRow}>
            <ActivityIndicator />
            <Text style={styles.loadingText}>처리 중...</Text>
          </View>
        ) : (
          <Button title={submitLabel} onPress={onSubmit} />
        )}

        {errorText ? <Text style={styles.error}>{errorText}</Text> : null}

        {token ? (
          <View style={styles.result}>
            <Text style={styles.success}>인증 완료</Text>
            <Text style={styles.label}>accessToken</Text>
            <Text style={styles.mono} numberOfLines={2}>
              {token.accessToken}
            </Text>
            <Text style={[styles.label, { marginTop: 12 }]}>refreshToken</Text>
            <Text style={styles.mono} numberOfLines={2}>
              {token.refreshToken}
            </Text>
          </View>
        ) : null}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: "#FAF7F2" },
  container: {
    flex: 1,
    paddingHorizontal: 20,
    paddingTop: 24,
    gap: 16,
  },
  title: { fontSize: 28, fontWeight: "700", color: "#1F1F1F" },
  subTitle: { fontSize: 14, color: "#555" },
  toggleRow: { flexDirection: "row", justifyContent: "center" },
  form: { width: "100%" },
  input: {
    width: "100%",
    borderWidth: 1,
    borderColor: "#EEE",
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 10,
    backgroundColor: "#FFFFFF",
    marginBottom: 10,
  },
  label: { fontSize: 12, color: "#666", marginBottom: 6 },
  mono: {
    fontSize: 12,
    color: "#222",
  },
  loadingRow: { flexDirection: "row", alignItems: "center", gap: 10 },
  loadingText: { color: "#333" },
  error: { color: "#B00020" },
  result: {
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    padding: 14,
    borderWidth: 1,
    borderColor: "#EEE",
  },
  success: { color: "#0A7D2C", fontWeight: "700", marginBottom: 8 },
});


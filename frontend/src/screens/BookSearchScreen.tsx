import React, { useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  FlatList,
  Image,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
} from "react-native";
import { api } from "../api/client";
import { Search, Plus } from "lucide-react-native";

interface BookSearchResult {
  isbn: string;
  title: string;
  author: string;
  thumbnailUrl: string;
  publisher: string;
}

/**
 * 도서 검색 화면
 * 카카오 검색 API를 통해 책을 검색하고 서재에 추가합니다.
 */
export default function BookSearchScreen({ navigation }: any) {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<BookSearchResult[]>([]);
  const [loading, setLoading] = useState(false);

  const onSearch = async () => {
    if (!query.trim()) return;
    try {
      setLoading(true);
      const { data } = await api.get<BookSearchResult[]>(`/api/books/search?query=${query}`);
      setResults(data);
    } catch (e) {
      console.error("검색 실패:", e);
      Alert.alert("에러", "도서 검색에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const addToLibrary = async (book: BookSearchResult) => {
    try {
      await api.post("/api/books/library", book);
      Alert.alert("성공", "내 서재에 추가되었습니다.", [
        { text: "확인", onPress: () => navigation.goBack() },
      ]);
    } catch (e: any) {
      const msg = e?.response?.data?.message ?? "서재 추가 중 오류가 발생했습니다.";
      Alert.alert("알림", msg);
    }
  };

  const renderItem = ({ item }: { item: BookSearchResult }) => (
    <View style={styles.resultCard}>
      <Image source={{ uri: item.thumbnailUrl }} style={styles.thumbnail} />
      <View style={styles.info}>
        <Text style={styles.title} numberOfLines={2}>{item.title}</Text>
        <Text style={styles.author}>{item.author} | {item.publisher}</Text>
      </View>
      <TouchableOpacity 
        style={styles.addButton}
        onPress={() => addToLibrary(item)}
      >
        <Plus color="#8E806A" size={24} />
      </TouchableOpacity>
    </View>
  );

  return (
    <View style={styles.container}>
      {/* 검색 바 */}
      <View style={styles.searchBar}>
        <Search color="#999" size={20} style={styles.searchIcon} />
        <TextInput
          style={styles.input}
          placeholder="책 제목 또는 저자를 입력하세요"
          value={query}
          onChangeText={setQuery}
          onSubmitEditing={onSearch}
          returnKeyType="search"
        />
        <TouchableOpacity style={styles.searchBtn} onPress={onSearch}>
          <Text style={styles.searchBtnText}>검색</Text>
        </TouchableOpacity>
      </View>

      {loading ? (
        <ActivityIndicator style={styles.center} color="#8E806A" />
      ) : (
        <FlatList
          data={results}
          keyExtractor={(item) => item.isbn}
          renderItem={renderItem}
          contentContainerStyle={styles.list}
          ListEmptyComponent={
            query ? <Text style={styles.empty}>검색 결과가 없습니다.</Text> : null
          }
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#FAF7F2" },
  center: { flex: 1, justifyContent: "center", alignItems: "center" },
  searchBar: {
    flexDirection: "row",
    padding: 16,
    alignItems: "center",
    backgroundColor: "#FFF",
    borderBottomWidth: 1,
    borderBottomColor: "#EEE",
  },
  searchIcon: { marginRight: 8 },
  input: {
    flex: 1,
    height: 40,
    backgroundColor: "#F5F5F5",
    borderRadius: 20,
    paddingHorizontal: 16,
    fontSize: 14,
  },
  searchBtn: { marginLeft: 12 },
  searchBtnText: { color: "#8E806A", fontWeight: "700" },
  list: { padding: 16 },
  resultCard: {
    flexDirection: "row",
    backgroundColor: "#FFF",
    borderRadius: 12,
    padding: 12,
    marginBottom: 12,
    alignItems: "center",
    elevation: 2,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  thumbnail: { width: 60, height: 90, borderRadius: 4 },
  info: { flex: 1, marginLeft: 12, gap: 4 },
  title: { fontSize: 15, fontWeight: "600", color: "#1F1F1F" },
  author: { fontSize: 13, color: "#666" },
  addButton: { padding: 8 },
  empty: { textAlign: "center", marginTop: 40, color: "#999" },
});

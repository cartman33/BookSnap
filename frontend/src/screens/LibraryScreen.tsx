import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  Image,
  TouchableOpacity,
  ActivityIndicator,
  RefreshControl,
} from "react-native";
import { api } from "../api/client";
import { BookPlus } from "lucide-react-native";

interface MyBook {
  userBookId: number;
  isbn: string;
  title: string;
  author: string;
  thumbnailUrl: string;
  status: "READING" | "COMPLETED";
}

/**
 * 내 서재 화면
 * 사용자가 등록한 도서 목록을 보여줍니다.
 */
export default function LibraryScreen({ navigation }: any) {
  const [books, setBooks] = useState<MyBook[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchLibrary = async () => {
    try {
      const { data } = await api.get<MyBook[]>("/api/books/library");
      setBooks(data);
    } catch (e) {
      console.error("서재 로드 실패:", e);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchLibrary();
  }, []);

  const onRefresh = () => {
    setRefreshing(true);
    fetchLibrary();
  };

  const renderItem = ({ item }: { item: MyBook }) => (
    <TouchableOpacity style={styles.bookCard}>
      <Image source={{ uri: item.thumbnailUrl }} style={styles.thumbnail} />
      <View style={styles.bookInfo}>
        <Text style={styles.bookTitle} numberOfLines={2}>
          {item.title}
        </Text>
        <Text style={styles.bookAuthor}>{item.author}</Text>
        <View style={styles.statusBadge}>
          <Text style={styles.statusText}>
            {item.status === "READING" ? "읽는 중" : "완독"}
          </Text>
        </View>
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      {loading ? (
        <ActivityIndicator style={styles.center} color="#8E806A" />
      ) : (
        <FlatList
          data={books}
          keyExtractor={(item) => item.userBookId.toString()}
          renderItem={renderItem}
          numColumns={2}
          contentContainerStyle={styles.listContent}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
          }
          ListEmptyComponent={
            <View style={styles.emptyContainer}>
              <Text style={styles.emptyText}>서재가 비어 있습니다.</Text>
              <Text style={styles.emptySubText}>읽고 싶은 책을 검색해 추가해보세요!</Text>
            </View>
          }
        />
      )}

      {/* 도서 검색 화면으로 이동하는 플로팅 버튼 */}
      <TouchableOpacity
        style={styles.fab}
        onPress={() => navigation.navigate("BookSearch")}
      >
        <BookPlus color="#FFF" size={24} />
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#FAF7F2" },
  center: { flex: 1, justifyContent: "center", alignItems: "center" },
  listContent: { padding: 10 },
  bookCard: {
    flex: 0.5,
    backgroundColor: "#FFF",
    margin: 6,
    borderRadius: 12,
    padding: 10,
    // 그림자 효과 (iOS)
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    // 그림자 효과 (Android)
    elevation: 3,
  },
  thumbnail: {
    width: "100%",
    height: 180,
    borderRadius: 8,
    resizeMode: "cover",
    marginBottom: 8,
  },
  bookInfo: { gap: 4 },
  bookTitle: { fontSize: 14, fontWeight: "600", color: "#1F1F1F" },
  bookAuthor: { fontSize: 12, color: "#666" },
  statusBadge: {
    alignSelf: "flex-start",
    backgroundColor: "#E8E2D6",
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 4,
    marginTop: 4,
  },
  statusText: { fontSize: 10, color: "#8E806A", fontWeight: "700" },
  fab: {
    position: "absolute",
    right: 20,
    bottom: 20,
    backgroundColor: "#8E806A",
    width: 56,
    height: 56,
    borderRadius: 28,
    justifyContent: "center",
    alignItems: "center",
    elevation: 5,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 3 },
    shadowOpacity: 0.3,
    shadowRadius: 4,
  },
  emptyContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    marginTop: 100,
  },
  emptyText: { fontSize: 16, fontWeight: "600", color: "#666" },
  emptySubText: { fontSize: 14, color: "#999", marginTop: 8 },
});

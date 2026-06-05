import React, { useState } from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { Library, Calendar as CalendarIcon, User } from "lucide-react-native";
import { View, Text } from "react-native";

// 화면 컴포넌트 임포트
import LoginScreen from "./src/screens/LoginScreen";
import LibraryScreen from "./src/screens/LibraryScreen";
import BookSearchScreen from "./src/screens/BookSearchScreen";

const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

// 임시 화면 (추후 구현)
const CalendarPlaceholder = () => (
  <View style={{ flex: 1, justifyContent: "center", alignItems: "center", backgroundColor: "#FAF7F2" }}>
    <Text>캘린더 화면 (준비 중)</Text>
  </View>
);
const MyPagePlaceholder = () => (
  <View style={{ flex: 1, justifyContent: "center", alignItems: "center", backgroundColor: "#FAF7F2" }}>
    <Text>마이페이지 화면 (준비 중)</Text>
  </View>
);

/**
 * 메인 탭 네비게이션
 */
function MainTabs() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ color, size }) => {
          if (route.name === "LibraryTab") return <Library color={color} size={size} />;
          if (route.name === "CalendarTab") return <CalendarIcon color={color} size={size} />;
          if (route.name === "MyPageTab") return <User color={color} size={size} />;
        },
        tabBarActiveTintColor: "#8E806A",
        tabBarInactiveTintColor: "#999",
        tabBarStyle: { backgroundColor: "#FFF", borderTopColor: "#EEE" },
        headerStyle: { backgroundColor: "#FAF7F2", elevation: 0, shadowOpacity: 0 },
        headerTitleStyle: { fontWeight: "700", color: "#1F1F1F" },
      })}
    >
      <Tab.Screen 
        name="LibraryTab" 
        component={LibraryScreen} 
        options={{ title: "내 서재" }} 
      />
      <Tab.Screen 
        name="CalendarTab" 
        component={CalendarPlaceholder} 
        options={{ title: "캘린더" }} 
      />
      <Tab.Screen 
        name="MyPageTab" 
        component={MyPagePlaceholder} 
        options={{ title: "내 정보" }} 
      />
    </Tab.Navigator>
  );
}

/**
 * 앱 메인 엔트리 포인트
 */
export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  if (!isAuthenticated) {
    return <LoginScreen onLoginSuccess={() => setIsAuthenticated(true)} />;
  }

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerStyle: { backgroundColor: "#FAF7F2" } }}>
        <Stack.Screen 
          name="Main" 
          component={MainTabs} 
          options={{ headerShown: false }} 
        />
        <Stack.Screen 
          name="BookSearch" 
          component={BookSearchScreen} 
          options={{ title: "도서 검색" }} 
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

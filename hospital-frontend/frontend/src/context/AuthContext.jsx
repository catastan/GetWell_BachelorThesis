import React, { createContext, useState, useEffect } from "react";
import { fetchUserRole } from "../api/auth";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState({
    email: null,
    token: null,
    role: null,
  });

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    const email = localStorage.getItem("email");

    if (token && email) {
      fetchUserRole().then((role) => {
        setAuth({ token, email, role });
      });
    }
  }, []);

  const login = async (email, password, token) => {
    localStorage.setItem("accessToken", token);
    localStorage.setItem("email", email);
    const role = await fetchUserRole();
    setAuth({ email, token, role });
  };

  const logout = () => {
    localStorage.clear();
    setAuth({ email: null, token: null, role: null });
  };

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

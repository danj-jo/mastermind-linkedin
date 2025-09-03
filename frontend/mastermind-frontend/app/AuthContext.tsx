import { createContext, useContext, useState, useEffect } from "react";
import type { ReactNode } from "react";



interface AuthContextType {
    isLoggedIn: boolean;
    setIsLoggedIn: (v: boolean) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);


    useEffect(() => {
        fetch("http://localhost:8080/api/auth", { credentials: "include" })
            .then(res => res.ok ? res.json() : Promise.reject())
            .then(() => setIsLoggedIn(true))
            .catch(() => setIsLoggedIn(false));
    }, []);

    return (
        <AuthContext.Provider value={{ isLoggedIn, setIsLoggedIn }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth must be inside AuthProvider");
    return ctx;
};

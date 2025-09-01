<<<<<<< Updated upstream
import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
=======
import { Link, useNavigate } from "react-router-dom";
import {useAuth} from "~/AuthContext";
interface NavbarProps {
    user: string | null;
}
>>>>>>> Stashed changes

const Navbar: React.FC<NavbarProps> = ({ user }) => {
    const navigate = useNavigate();
<<<<<<< Updated upstream
    const [loading, setLoading] = useState(true);
    const [isLoggedIn, setIsLoggedIn] = useState<boolean | null>(null);
    const [username, setUsername] = useState<string>('');

    useEffect(() => {
        let isMounted = true;
        const checkAuth = async () => {
            try {
                const res = await fetch('http://localhost:8080/about', {
                    method: 'GET',
                    credentials: 'include',
                });
                if (!isMounted) return;
                if (res.ok) {
                    const data = await res.json().catch(() => null);
                    setIsLoggedIn(true);
                    if (data && (data.username || data.name)) {
                        setUsername(data.username || data.name);
                    }
                } else {
                    setIsLoggedIn(false);
                }
            } catch (e) {
                if (isMounted) setIsLoggedIn(false);
            } finally {
                if (isMounted) setLoading(false);
            }
        };
        checkAuth();
        return () => {
            isMounted = false;
        };
    }, []);

    const handleLogout = async () => {
        try {
            await fetch('http://localhost:8080/logout', {
                method: 'POST',
                credentials: 'include',
            });
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            setIsLoggedIn(false);
            setUsername('');
            navigate('/login');
=======
    const {isLoggedIn, setIsLoggedIn} = useAuth()

    const handleLogout = async () => {
        try {
            await fetch("http://localhost:8080/logout", {
                method: "POST",
                credentials: "include",
            });
            setIsLoggedIn(false)
            navigate("/login");
        } catch (error) {
            console.error("Logout error:", error);
            navigate("/login");
>>>>>>> Stashed changes
        }
    };

    return (
        <nav className="navbar">
            <div className="container navbar-container" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <Link to="/" className="navbar-brand">
                    Mastermind
                </Link>
<<<<<<< Updated upstream
                {/* Right side of navbar */}
                {loading ? null : (
                    isLoggedIn ? (
                        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
                            {username && (
                                <span style={{ color: 'var(--text-light)' }}>Hi, {username}</span>
                            )}
                            <Link to="/games/new" className="btn btn-outline">
                                New Game
                            </Link>
                            <Link to="/mygames" className="btn btn-outline">
                                My Games
                            </Link>
                            <Link to="/me" className="btn btn-outline">
                                Profile
                            </Link>
                            <button onClick={handleLogout} className="btn btn-outline">
                                Logout
                            </button>
                        </div>
                    ) : (
                        <div style={{ display: 'flex', gap: '12px' }}>
                            <Link to="/login" className="btn btn-outline">
                                Login
                            </Link>
                            <Link to="/register" className="btn btn-primary">
                                Register
                            </Link>
                        </div>
                    )
=======

                {isLoggedIn ? (
                    <div style={{ display: "flex", alignItems: "center", gap: "1rem" }}>
                        <div style={{ display: "flex" }}>
                            <Link className="nav-link" to="/homeScreen">Home</Link>
                            <Link className="nav-link" to="/mygames">Past Games</Link>
                            <Link className="nav-link" to="/me">Profile</Link>
                            <Link className="nav-link" to="/newgame">New Game</Link>

                        </div>

                        <button onClick={handleLogout} className="btn btn-outline">
                            Logout
                        </button>
                    </div>
                ) : (
                    <div style={{ display: "flex", gap: "1rem" }}>
                        <Link to="/login">Login</Link>
                        <Link to="/register">Register</Link>
                    </div>
>>>>>>> Stashed changes
                )}
            </div>
        </nav>

    );
};

export default Navbar;

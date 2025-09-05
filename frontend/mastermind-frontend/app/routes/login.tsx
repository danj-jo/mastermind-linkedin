"use client";
import React, { useState } from "react";
import {Link, useNavigate} from "react-router-dom";
import { useAuth } from "~/AuthContext";
const LoginForm: React.FC = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("")
    const [loading,setLoading] = useState(false);
    const navigate = useNavigate();
    const { setIsLoggedIn } = useAuth();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await fetch("http://localhost:8080/login", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({
                    username,
                    password
                }),
                credentials: "include"
            });
            const data = await response.json();
            if (response.ok) {
                setIsLoggedIn(true)
                navigate('/home');
            } else {
                setError(data.message);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);  // stop loading
        }
    };
    return (
        <>
            <div className="container">
                <div className="card" style={{ maxWidth: '400px', margin: '50px auto' }}>
                    <h2 style={{ textAlign: 'center', marginBottom: '24px', color: 'var(--primary-red)' }}>
                        Welcome back!
                    </h2>

                    <form onSubmit={handleSubmit}>

                        <div className="form-group">
                            <label htmlFor="username" className="form-label">Username</label>
                            <input
                                type="text"
                                id="username"
                                className="form-input"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="Enter your username"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password" className="form-label">Password</label>
                            <input
                                type="password"
                                id="password"
                                className="form-input"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Enter your password"
                                required
                            />
                        </div>
                        <p> {error && error}</p>

                        <button type="submit" disabled = {loading} className="btn btn-primary" style={{ width: '100%' }}>
                            Login
                        </button>
                    </form>
                    <div style={{ textAlign: 'center', marginTop: '20px' }}>
                        <p style={{ color: 'var(--text-light)' }}>
                            Don't have an account?{' '}
                            <Link to ="/register" style={{ color: 'var(--primary-red)' }}>
                                Register here
                            </Link>
                        </p>
                    </div>
                </div>
            </div>

        </>
    );
};

export default LoginForm;

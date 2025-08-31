"use client";

import React, { useState } from 'react';
import {Link, useNavigate} from "react-router-dom";

const RegistrationForm: React.FC = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const [error, setError] = useState("")
    const navigate = useNavigate();
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password, email}),
                credentials: "include",
            });
                navigate('/login');
            // Try to read body only if there is one
            let data: any = null;
            const text = await response.text(); // read as raw text
            if (text) {
                try {
                    data = JSON.parse(text); // parse only if not empty
                } catch (err) {
                    console.warn("Response was not JSON:", text);
                }
            }

            if (!response.ok) {
                console.error("Registration failed", data?.error || response.statusText);
                setError(data.error)
            }

            console.log("Registration successful.", data);
        } catch (err) {
            console.error("Registration failed ")
        }
    };

    return (
        <>

            <div className="container">
                <div className="card" style={{ maxWidth: '400px', margin: '50px auto' }}>
                    <h2 style={{ textAlign: 'center', marginBottom: '24px', color: 'var(--primary-red)' }}>
                        Sign Up for Mastermind!
                    </h2>

                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="email" className="form-label">Email Address</label>
                            <input
                                type="email"
                                id="email"
                                className="form-input"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="Enter your email address"
                                required
                            />
                        </div>

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
                        {error ??  <p> {error} </p> }
                        <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
                            Register
                        </button>
                    </form>
                </div>
            </div>
        </>
    );
};

export default RegistrationForm;

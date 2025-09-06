"use client";
import React, { useState, useEffect } from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {useAuth} from "~/AuthContext";

const Profile: React.FC = () => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [finishedGames, setFinishedGames] = useState([])
    const [unfinishedGames, setUnfinishedGames] = useState([])
    const [username,setUserName] = useState("")
    const [email,setEmail] = useState("");
    const navigate = useNavigate();
    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const response = await fetch("http://localhost:8080/me", {
                    method: "GET",
                    credentials: "include",
                });
                const pastGames = await fetch("http://localhost:8080/me/games", {
                    method: "GET",
                    credentials: "include",
                });

                const data = await pastGames.json()
                setFinishedGames(data.finished);
                setUnfinishedGames(data.unfinished);


                if (response.ok) {
                    const user = await response.json(); // ✅ read once
                    setUserName(user.username);
                    setEmail(user.email);

                } else {
                    console.error(`HTTP error! status: ${response.status}`);
                    setError("Failed to load profile data");
                }

            } catch (error) {
                console.error("Error fetching profile:", error);
                setError("Failed to load profile data");
            } finally {
                setLoading(false);
            }

        }
            fetchUserData();
    }, []);

    if (loading) {
        return (
            <div className="container">
                <div className="card" style={{ textAlign: 'center', margin: '50px auto', maxWidth: '400px' }}>
                    <h2 style={{ color: 'var(--primary-red)', marginBottom: '20px' }}>
                        Loading Profile...
                    </h2>
                </div>
            </div>
        );
    }





    return (
        <div className="container">
            <div className="card">
                <h2 style={{ textAlign: 'center', marginBottom: '32px', color: 'var(--primary-red)' }}>
                    Player Profile
                </h2>

                <div style={{ textAlign: 'center', marginBottom: '32px' }}>
                    <div style={{
                        width: '100px',
                        height: '100px',
                        borderRadius: '50%',
                        backgroundColor: 'var(--primary-red)',
                        margin: '0 auto 16px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '36px',
                        fontWeight: 'bold',
                        color: 'white'
                    }}>
                        {username?.charAt(0).toUpperCase() || 'U'}
                    </div>
                    <h3 style={{ color: 'var(--text-light)', marginBottom: '8px' }}>
                        {username}
                    </h3>
                    <p style={{ color: 'var(--border-color)' }}>
                        {email}
                    </p>
                </div>

                <div className="stats-grid" onClick={() => {navigate("/mygames")}}>
                    <div className="stat-card">
                        <div className="stat-number">{finishedGames.length + unfinishedGames.length}</div>
                        <div className="stat-label">Games Played</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-number"><p>{finishedGames.filter(item => item.result === "WIN").length}</p>
                        </div>
                        <div className="stat-label">Games Won</div>
                    </div>
                    <div className="stat-card">
                        <div className="stat-number">0</div>
                        <div className="stat-label"> Team Games</div>
                    </div>

                </div>

            </div>
        </div>
    );
};

export default Profile;

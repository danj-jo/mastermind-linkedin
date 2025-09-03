"use client";
import React, { useState, useEffect } from 'react';
import {Link, useNavigate} from 'react-router-dom';

const Profile: React.FC = () => {
    const [userData, setUserData] = useState<any>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [finishedGames, setFinishedGames] = useState([])
    const [totalGamesPlayed, setTotalGamesPlayed] = useState(0);
    const [username,setUserName] = useState("")
    const [email,setEmail] = useState("");
    const [gamesWon, setGamesWon] = useState(0);
    const navigate = useNavigate();
    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const response = await fetch("http://localhost:8080/me", {
                    method: "GET",
                    credentials: "include",
                });

                const nameRequest = await fetch("http://localhost:8080/about", {
                    method: "GET",
                    credentials: "include",
                });

                const name = await nameRequest.json();
                setUserName(name.username);
                setEmail(name.email)


                const data = await response.json();
                setUserData(data)
                setFinishedGames(data.finished)
                setTotalGamesPlayed(data.finished.length + data.unfinished.length)

            } catch (error) {
                console.error('Error fetching profile:', error);
                setError('Failed to load profile data');
            } finally {
                setLoading(false);
            }
        };

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

    if (error || !userData) {
        return (
            <div className="container">
                <div className="card" style={{ textAlign: 'center', margin: '50px auto', maxWidth: '400px' }}>
                    <h2 style={{ color: 'var(--primary-red)', marginBottom: '20px' }}>
                        Profile Error
                    </h2>
                    <p style={{ marginBottom: '20px' }}>
                        {error || 'Unable to load profile data. Please log in again.'}
                    </p>
                    <Link to="/login" className="btn btn-primary">
                        Login
                    </Link>
                </div>
            </div>
        );
    }

    const winRate = userData.stats?.gamesPlayed > 0
        ? Math.round((userData.stats.gamesWon / userData.stats.gamesPlayed) * 100)
        : 0;

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
                        <div className="stat-number">{totalGamesPlayed}</div>
                        <div className="stat-label">Games Played</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-number">2</div>
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

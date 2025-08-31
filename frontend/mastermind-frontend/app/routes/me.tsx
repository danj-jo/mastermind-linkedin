import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

const Profile: React.FC = () => {
    const [userData, setUserData] = useState<any>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const response = await fetch("http://localhost:8080/profile", {
                    credentials: "include"
                });

                if (!response.ok) {
                    setError('Failed to load profile data');
                    setLoading(false);
                    return;
                }

                const data = await response.json();
                setUserData(data);
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
                        {userData.username?.charAt(0).toUpperCase() || 'U'}
                    </div>
                    <h3 style={{ color: 'var(--text-light)', marginBottom: '8px' }}>
                        {userData.username || 'Unknown User'}
                    </h3>
                    <p style={{ color: 'var(--border-color)' }}>
                        {userData.email || 'No email available'}
                    </p>
                </div>

                <div className="stats-grid">
                    <div className="stat-card">
                        <div className="stat-number">{userData.stats?.gamesPlayed || 0}</div>
                        <div className="stat-label">Games Played</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-number">{userData.stats?.gamesWon || 0}</div>
                        <div className="stat-label">Games Won</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-number">{winRate}%</div>
                        <div className="stat-label">Win Rate</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-number">
                            {userData.stats?.averageAttempts > 0 ? userData.stats.averageAttempts.toFixed(1) : '0'}
                        </div>
                        <div className="stat-label">Avg Attempts</div>
                    </div>

                    <div className="stat-card">
                        <div className="stat-number">{userData.stats?.bestScore || 0}</div>
                        <div className="stat-label">Best Score</div>
                    </div>
                </div>

                <div style={{ textAlign: 'center', marginTop: '32px' }}>
                    <Link to="/games/new" className="btn btn-primary">
                        Start New Game
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default Profile;

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const NewGame: React.FC = () => {
    const [difficulty, setDifficulty] = useState('EASY');
    const navigate = useNavigate();
    let fields;
    switch(difficulty){
        case "easy":
            fields = 4;
            break
        case "medium":
            fields = 6;
            break
        case "hard":
            fields = 9;
            break
        default:
            fields = 4;

    }
    localStorage.setItem("fields",fields.toString())
    const handleStartGame = async () => {
        try {
            const response = await fetch("http://localhost:8080/games/new", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ "difficulty": difficulty }),
                credentials: "include"
            });

            if (response.ok) {
                navigate('/game');
            } else {
                alert('Failed to create game');
            }
        } catch (error) {
            console.error(error);
            alert('Failed to create game');
        }
    };

    return (
        <div className="container">
            <div className="card" style={{ maxWidth: '600px', margin: '50px auto' }}>
                <h2 style={{ textAlign: 'center', marginBottom: '32px', color: 'var(--primary-red)' }}>
                    Start New Game
                </h2>

                <div style={{ textAlign: 'center', marginBottom: '32px' }}>
                    <p style={{ color: 'var(--text-light)', fontSize: '18px', marginBottom: '16px' }}>
                        Choose your difficulty level and start playing Mastermind.
                    </p>
                </div>

                <div className="difficulty-selector">
                    <button
                        className={`difficulty-btn ${difficulty === 'easy' ? 'active' : ''}`}
                        onClick={() => setDifficulty('easy')}
                    >
                        Easy
                    </button>
                    <button
                        className={`difficulty-btn ${difficulty === 'medium' ? 'active' : ''}`}
                        onClick={() => setDifficulty('medium')}
                    >
                        Medium
                    </button>
                    <button
                        className={`difficulty-btn ${difficulty === 'hard' ? 'active' : ''}`}
                        onClick={() => setDifficulty('hard')}
                    >
                        Hard
                    </button>
                </div>

                <div style={{
                    backgroundColor: 'var(--background-dark)',
                    padding: '24px',
                    borderRadius: '8px',
                    marginBottom: '32px'
                }}>
                    <h3 style={{ color: 'var(--primary-yellow)', marginBottom: '16px' }}>
                        {difficulty.charAt(0).toUpperCase() + difficulty.slice(1)} Mode
                    </h3>
                    <p style={{ color: 'var(--text-light)' }}>
                        {difficulty === 'easy' && '4 numbers, 10 attempts'}
                        {difficulty === 'medium' && '6 numbers 10 attempts'}
                        {difficulty === 'hard' && '9 colors 10 attempts'}
                    </p>
                </div>

                <div style={{ textAlign: 'center' }}>
                    <button
                        onClick={handleStartGame}
                        className="btn btn-primary"
                        style={{ fontSize: '18px', padding: '16px 32px' }}
                    >
                        Start Game
                    </button>
                </div>
            </div>
        </div>
    );
};

export default NewGame;

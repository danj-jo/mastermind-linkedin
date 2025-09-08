"use client";
import React, {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import {useAuth} from "~/AuthContext";

const NewGame: React.FC = () => {
    const [difficulty, setDifficulty] = useState('easy');
    const [mode,setMode] = useState("");
    const navigate = useNavigate()
    const {isLoggedIn} = useAuth()
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
    useEffect(() => {
        localStorage.setItem("fields",fields.toString())
    },[])

    const multiplayerNavigate = () => {
        sessionStorage.setItem("difficulty", difficulty)
        console.log(difficulty)
        navigate("/lobby")
    }
    const handleStartGame = async () => {

        try {
            const response = await fetch("http://localhost:8080/singleplayer/games/new", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ "difficulty": difficulty.toUpperCase(), "mode": mode.toUpperCase()}),
                credentials: "include"
            });
            if (response.ok) {
                const data = await response.json();
                const gameId = data.gameId
                sessionStorage.setItem('currentGameId', gameId);  // Save game ID
                navigate(`/game/${gameId}`);
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
                <div style={{marginBottom: "20px"}}>
                    <h3 style={{fontKerning:"normal"}}> Welcome to mastermind! The goal of the game is to guess the correct number in 10
                        tries. Following your guess, you will be prompted with hints regarding how many attempts you have, and how many locations, but never the exact location of the number. Be mindful: there are duplicate numbers, but no duplicate guesses! </h3>
                </div>
                <div style={{ textAlign: 'center', marginTop: "10px",marginBottom: '10px' }}>
                    <p style={{ color: 'var(--text-light)', fontSize: '18px', marginBottom: '16px' }}>
                        Choose your Mode:
                    </p>
                </div>
                <div style={{display: "flex", alignSelf: "center"}}>
                    <div className="difficulty-selector">
                        <button
                            className={`difficulty-btn ${mode === 'singleplayer' ? 'active' : ''}`}
                            onClick={() => setMode('singleplayer')}
                        >
                            Single Player
                        </button>
                    </div>
                    <div className="difficulty-selector">
                        <button
                            className={`difficulty-btn ${mode === 'multiplayer' ? 'active' : ''}`}
                            onClick={() => setMode('multiplayer')}
                        >
                            Multi Player
                        </button>
                    </div>
                </div>

                <div style={{ textAlign: 'center', marginTop: "10px",marginBottom: '10px' }}>
                    <p style={{ color: 'var(--text-light)', fontSize: '18px', marginBottom: '16px' }}>
                        Choose your difficulty:
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

                    <p style={{ color: 'var(--text-light)' }}>
                        {difficulty === 'easy' && '4 numbers, 10 attempts'}
                        {difficulty === 'medium' && '6 numbers,10 attempts'}
                        {difficulty === 'hard' && '9 numbers, 10 attempts'}
                    </p>
                </div>

                <div style={{ textAlign: 'center' }}>
                    <button
                        onClick={() => {
                            if(mode == "singleplayer"){
                                handleStartGame()
                            }  if(mode == "multiplayer"){
                                multiplayerNavigate()
                            }
                        }}
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
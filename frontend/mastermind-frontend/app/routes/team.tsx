"use client";
import { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";

const Team = () => {
    const [guessInputBoxes, setGuessInputBoxes] = useState([]);
    const inputRefs = useRef([]);
    const [feedback, setFeedback] = useState("");
    const [result, setResult] = useState("");
    const clientRef = useRef(null);
    const [guesses, setGuesses] = useState([]);
    const [isYourTurn, setIsYourTurn] = useState(false);
    useEffect(() => {
        if (typeof window === "undefined") return;

        const gameIdRaw = sessionStorage.getItem("gameId");
        const gameId = gameIdRaw?.replace(/^"|"$/g, "");
        if (!gameId) return console.warn("No gameId in sessionStorage");

        const client = new Client({
            brokerURL: "ws://localhost:8080/ws",
            reconnectDelay: 5000,
            connectHeaders: { gameId },
        });

        client.onConnect = () => {
            client.subscribe("/topic/mp", (message) => {
                const response = JSON.parse(message.body);

                setFeedback(response.feedback);
                setGuesses(response.guesses);

            
                const myPlayerId = sessionStorage.getItem("playerId")?.replace(/^"|"$/g, "");
                setIsYourTurn(response.currentPlayerId === myPlayerId && !response.notYourTurn);
            });
        };


        client.activate();
        clientRef.current = client;

        const createBoard = async () => {
            const res = await fetch(`http://localhost:8080/multiplayer/${gameId}`, {
                method: "GET",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
            });
            const data = await res.json();
            const boxes = Array(data.numbersToGuess).fill("");
            setGuessInputBoxes(boxes);
        };

        createBoard();

        return () => client.deactivate();
    }, []);

    const handleSubmitGuess = () => {
        const guessString = guessInputBoxes.join("");
        clientRef.current.publish({
            destination: `/app/multiplayer/${sessionStorage.getItem("gameId")}/guess`,
            body: JSON.stringify({
                guess: guessString,
                gameId: sessionStorage.getItem("gameId"),
                playerId: sessionStorage.getItem("playerId"),
            }),
        });
        setGuessInputBoxes(Array(guessInputBoxes.length).fill(""));
    };

    const handleChange = (e, idx) => {
        const val = e.target.value.slice(0, 1);
        const newValues = [...guessInputBoxes];
        newValues[idx] = val;
        setGuessInputBoxes(newValues);
        if (val && idx < inputRefs.current.length - 1) {
            inputRefs.current[idx + 1].focus();
        }
    };

    const handleKeyDown = (e, idx) => {
        if (e.key === "Backspace" && !guessInputBoxes[idx] && idx >= 1) {
            inputRefs.current[idx - 1].focus();
        }
    };

    return (
        <div className="container">
            <div className="card">
                <h3 style={{ color: "var(--primary-yellow)", marginBottom: "16px" }}>
                    Choose {guessInputBoxes.length} numbers!
                </h3>

                <div style={{ marginBottom: "24px" }}>
                    {guessInputBoxes.map((val, idx) => (
                        <input
                            key={idx}
                            ref={(el) => (inputRefs.current[idx] = el)}
                            value={val}
                            onChange={(e) => handleChange(e, idx)}
                            onKeyDown={(e) => handleKeyDown(e, idx)}
                            maxLength={1}
                            style={{
                                width: "40px",
                                textAlign: "center",
                                marginRight: "10px",
                                fontSize: "18px",
                                border: "2px solid white",
                                borderRadius: "4px",
                                outline: "none",
                                marginBottom: "20px",
                            }}
                        />
                    ))}
                </div>

                {feedback && <p>{feedback}</p>}

                <button
                    onClick={handleSubmitGuess}
                    className={result === "done" ? "hide-button" : "btn btn-primary"}
                >
                    Submit Guess
                </button>

                <div
                    style={{
                        border: "2px solid white",
                        marginTop: "10px",
                        padding: "10px",
                    }}
                >
                    <h1>Guess Bank:</h1>
                    <ul>
                        {guesses.map((g, idx) => (
                            <li key={idx}>
                                {g.player}: {g.guess}
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default Team;

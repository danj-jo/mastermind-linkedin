"use client"
import React, {useEffect, useRef, useState} from 'react';
import {useParams} from "react-router";

const GameBoard: React.FC = () => {

    const [guess, setGuess] = useState("");
    const [values, setValues] = useState([]);
    const inputRefs = useRef([]);
    const [feedback, setFeedback] = useState("")
    const [result, setResult] = useState("")
    const gameId = useParams();
    const id = gameId.gameId

    useEffect(() => {
       const response = async () =>
        {

                let numsArray = []
                const response = await fetch(`http://localhost:8080/games/${id}`, {
                    method: "GET",
                    headers: { "Content-Type": "application/json" },
                    credentials: "include",
                });

                const data = await response.json();
                const fields = data.numbersToGuess;
                for(let i = 0; i < fields; i++){
                    numsArray.push("")
                }
                console.log(numsArray)
                setValues(numsArray)
        };

        response();
    },[])

    // @ts-ignore
    const handleChange = (e, idx) => {
        const val = e.target.value.slice(0, 1); // allow only 1 character
        const newValues = [...values];
        newValues[idx] = val;
        setValues(newValues);
        setGuess(newValues.toString().replace(/,/g, ""))

        // auto-focus next input if available
        if (val && idx < inputRefs.current.length - 1) {
            inputRefs.current[idx + 1].focus();
        }
    };

    const handleKeyDown = (e, idx) => {
        if (e.key === "Backspace" && !values[idx] && idx >= 1) {
            inputRefs.current[idx - 1].focus();
        }
    };

    const handleSubmitGuess = async () => {
        try {
            const response = await fetch("http://localhost:8080/games/update", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    "gameId": id,
                    "guess": guess
                }),
                credentials: "include"
            });
            const data = await response.json();
            console.log(guess)
            setFeedback(data.feedback);
            if(data.finished == "true"){
                setResult("done")
            }
            // Reset guess for next turn
        } catch (error) {
            console.error(error);
            alert(values.toString());
        }
    };




    // @ts-ignore
    return (

    <div className="container">
        <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                <h3 style={{ color: 'var(--primary-yellow)', marginBottom: '16px' }}>
                    Choose {values.length} numbers!
                </h3>
            </div>
            <div style={{ marginBottom: '24px' }}>


                <div>
                    {values.map((val, idx) => (
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
            </div>

        <div>
            {feedback.length > 1 && <p> {feedback}</p>}
        </div>
        <button style={{width: "25"}} onClick={handleSubmitGuess} className={result == "done" ? "hide-button" : "btn btn-primary"}>
            Submit Guess
        </button>
        </div>
    </div>
    )

};

export default GameBoard;

"use client"
import React, {useEffect, useRef, useState} from 'react';
const GameBoard: React.FC = () => {


    const [guess, setGuess] = useState("");
    const [guessInputBoxes, setGuessInputBoxes] = useState([]);
    const inputRefs = useRef([]);
    const [feedback, setFeedback] = useState("")
    const [result, setResult] = useState("")

    /**
     * Initializes the guess input boxes when the component first loads.
     *
     * - Reads the number of fields to create from localStorage ("fields").
     * - Converts that value into a number.
     * - Creates an array of empty strings with that length.
     * - Updates state so the UI shows the correct number of input boxes.
     *
     * Runs only once on mount because of the empty dependency array [].
     */

    useEffect(() => {
        let fields = localStorage.getItem("fields");
        let numOfFields = Number(fields);
        let valueArray = [];
        for(let i = 0; i < numOfFields; i++){
            valueArray.push("");
        }
        setGuessInputBoxes(valueArray)
    },[])

    /**
     * This method is used to set the
     * @param e - the value of the input box for guesses
     * @param idx - the index of the current guess
     */
    // @ts-ignore
    const handleChange = (e, idx) => {
        const val = e.target.value.slice(0, 1); // allow only 1 character
        const newValues = [...guessInputBoxes];
        newValues[idx] = val;
        setGuessInputBoxes(newValues);


        // auto-focus next input if available
        if (val && idx < inputRefs.current.length - 1) {
            inputRefs.current[idx + 1].focus();
        }
    };
/**
 * Handles key press events inside the guess input boxes.
 *
 * - If the user presses Backspace on an empty box,
 *   the cursor will move back to the previous box.
 * - Prevents trying to move back from the first box.
 */
    const handleKeyDown = (e, idx) => {
        if (e.key === "Backspace" && !guessInputBoxes[idx] && idx > 0) {
            inputRefs.current[idx - 1].focus();
        }
    };

    const handleSubmitGuess = async () => {
        try {
            const gameId = sessionStorage.getItem('currentGameId');
            if (!gameId) {
                alert('No active game found');
                return;
            }

            const guessString = guessInputBoxes.toString().replace(/,/g, "");
            setGuess(guessString)

            const response = await fetch(`http://localhost:8080/singleplayer/games/${gameId}/guess`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ "guess":guessString }),
                credentials: "include"
            });
                const data = await response.json();
                console.log('Guess result:', data.feedback)
                console.log(guess)
                setFeedback(data.feedback);
                if(data.finished == "true"){
                    setResult("done")
                }
        } catch (error) {
            console.error(error);
            alert(guessInputBoxes.toString());
        }
    };



    // @ts-ignore
    // @ts-ignore
    // @ts-ignore
    return (
        <div className="container">
            <div className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                    <h3 style={{ color: 'var(--primary-yellow)', marginBottom: '16px' }}>
                        Choose {guessInputBoxes.length} numbers!
                    </h3>
                </div>

                <div style={{ marginBottom: '24px' }}>


                    <div>
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
                </div>

                <div>

                </div>
                <p style={{color: "red"}}> {feedback && feedback} </p>
                <button style={{width: "25"}} onClick={handleSubmitGuess} className={result == "done" ? "hide-button" : "btn btn-primary"}>
                    Submit Guess
                </button>


            </div>
        </div>
    );
};

export default GameBoard;

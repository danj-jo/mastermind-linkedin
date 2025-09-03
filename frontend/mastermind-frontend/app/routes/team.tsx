"use client"
import {useEffect, useRef, useState} from "react";
import {useParams} from "react-router";
import { Client } from '@stomp/stompjs';
import {Stomp} from "@stomp/stompjs"
 const Team = () => {
     const stomp = Stomp;
     const [guess, setGuess] = useState("");
     const [values, setValues] = useState([]);
     const inputRefs = useRef([]);
     const [feedback, setFeedback] = useState("")
     const [isMyTurn, setIsMyTurn] = useState()
     const [result, setResult] = useState("")
     const gameIdRaw = sessionStorage.getItem("gameId");
     const gameId = gameIdRaw?.replace(/^"|"$/g, "");
     const clientRef = useRef(null);
     const [guesses,setGuesses]  = useState([])
     const[player,setPlayer] = useState()
     useEffect(() => {
         const client = new Client({
             brokerURL: 'ws://localhost:8080/ws',
             reconnectDelay: 5000
         });

         client.onConnect = () => {
             client.subscribe('/topic/mp', (message) => {
                 setIsMyTurn(true)
                 console.log(message)
                 const response = JSON.parse(message.body);
                 console.log(response.winningNumber)
                 setFeedback(response.feedback);
                 setGuesses(response.guesses)
                 setPlayer(response.player)
             });
         };

         client.activate();

         // @ts-ignore
         clientRef.current = client;
         const createBoard = async () =>
         {
             let numsArray = []
             const response = await fetch(`http://localhost:8080/multiplayer/${gameId}`, {
                 method: "GET",
                 headers: { "Content-Type": "application/json" },
                 credentials: "include",
             });

             const data = await response.json();
             const fields = data.numbersToGuess;
             for(let i = 0; i < fields; i++){
                 numsArray.push("")
             }
             // @ts-ignore
             setValues(numsArray)
         };

         createBoard();
         return () =>  client.deactivate();
     },[])


     const handleSubmitGuess = async () => {
         try {
             const newGuess = values.toString().replace(/,/g, "");
             setGuess(newGuess)
                 clientRef.current.publish({
                     destination: `/app/multiplayer/${gameId}/guess`,
                     body: JSON.stringify({"guess": newGuess})
                 });
             console.log(newGuess)
            setIsMyTurn(false)
         }
         catch (error) {
             console.log("Maybe not connected?")
         };
     }

             // Reset guess for next turn



     const handleChange = (e, idx) => {
         const val = e.target.value.slice(0, 1); // allow only 1 character
         const newValues = [...values];
         newValues[idx] = val;
         setValues(newValues);
         setGuess(newValues.toString().replace(/,/g, ""))
         if (val && idx < inputRefs.current.length - 1) {
             // @ts-ignore
             inputRefs.current[idx + 1].focus();
         }
     };

     const handleKeyDown = (e, idx) => {
         if (e.key === "Backspace" && !values[idx] && idx >= 1) {
             inputRefs.current[idx - 1].focus();
         }
     };
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
                    {feedback && <p> {feedback} </p>}
                </div>
                <button style={{width: "25"}} onClick={handleSubmitGuess} disabled={isMyTurn} className={result == "done" ? "hide-button" : "btn btn-primary"}>
                    Submit Guess
                </button>
                <div style={{border: "2px solid white", marginTop:"10px", padding: "10px"}}>
                    <h1> Guess Bank : </h1>
                    {guesses &&
                        <ul>
                            {guesses && guesses.map((g, index) => (
                                <li key={index}>{g}</li>
                            ))}
                        </ul>

                    }
                </div>



            </div>
        </div>
    );
}
export default Team;
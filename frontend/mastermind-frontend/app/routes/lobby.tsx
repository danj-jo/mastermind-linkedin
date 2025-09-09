import React, { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import {useAuth} from "~/AuthContext";
export default function Lobby() {
    const navigate = useNavigate()
    const {isLoggedIn} = useAuth()
    const [events, setEvents] = useState([]);
    const [isJoined, setIsJoined] = useState(false);
    const [eventSource, setEventSource] = useState(null);
    const[clicked,setIsClicked] = useState(false)
    const handleJoin = () => {
        const difficulty = sessionStorage.getItem("difficulty");
        const es = new EventSource(
            `http://localhost:8080/multiplayer/join?difficulty=${difficulty}`,
            { withCredentials: true }
        );
        setIsClicked(true);
        //  Listen for "matched" event
        es.addEventListener("matched", (event) => {
            setIsClicked(true)
            console.log(event.data)
            const res = JSON.parse(event.data);
            const gameId = res.gameId
            const playerId = res.playerId;
            sessionStorage.setItem("gameId", gameId.replace(/^"|"$/g, ""));
            sessionStorage.setItem("playerId", playerId.replace(/^"|"$/g, ""));

            setTimeout(() => {
                navigate("/team")
            }, 3000);

            setEvents((prev) => [...prev, `🎉 Player matched! redirecting to game`]);
        });


        es.addEventListener("ping", () => {
            setEvents((prev) => [...prev, `waiting for other players who want ${difficulty} game...`]);
        });
        es.addEventListener("heartbeat", () => {
            setEvents((prev) => [...prev, "💓 Heartbeat"]);
        });

        setEventSource(es);

    };

    return (
        <>
        <div className="container">
            <div className="card" style={{ maxWidth: '600px', margin: '50px auto' }}>
                <div style={{marginBottom: "20px"}}>
                    <h3 style={{fontKerning:"normal"}}> Welcome to the multiplayer lobby! The rules here the same as a standard game, but instead of 10 tries, you will have 5, along with your team mate. Click the button below to be added to the queue! </h3>
                </div>
                <div style={{ textAlign: 'center', marginTop: "10px",marginBottom: '10px' }}>

                </div>
                <div style={{display: "flex", alignSelf: "center"}}>
                    <button onClick={handleJoin}  className={clicked ? "hide-button" : 'btn btn-primary'}>
                        {isJoined ? "Joined!" : "Join Match"}
                    </button>
                    <ul style={{ marginTop: "1rem", paddingLeft: "1rem" }}>
                        {events.map((event, idx) => (
                            <li key={idx}>{event}</li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>

        </>
    );
}

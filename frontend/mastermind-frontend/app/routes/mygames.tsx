"use client";
import { useState, useEffect } from "react";
import { Table, Space, Tag } from "antd";
import {Link, useNavigate} from "react-router-dom";
import {useAuth} from "~/AuthContext";

const MyGames = () => {
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true); // start as loading
    const [finishedGames, setFinishedGames] = useState([])
    const [unfinishedGames, setUnfinishedGames] = useState([])
    const navigate = useNavigate()
    const {isLoggedIn} = useAuth()
    if(!isLoggedIn){
        navigate("/login")
    }
    const allGames = [];
    finishedGames.forEach(game => allGames.push(game));
    unfinishedGames.forEach(game => allGames.push(game));
    const columnsFinished = [{
        title: "id",
        dataIndex: "gameId",
        key: "gameId",
        render: id => {
            const game = allGames.find(i => i.id === id);

            return game?.result == "PENDING" ? (
                <Link  to={`/games/${id}`}>
                    resume
                </Link>
            ) : (
                <Link style={{color:"white"}} to={`/game/${id}`}>
                    {id}
                </Link>
            );
        },
    },
        {
            title: "Difficulty",
            dataIndex: "difficulty",
            key: "difficulty",
        },
        {
            title: "Result",
            dataIndex: "result",
            key: "result",
        },
        {
            title: "Winning Number",
            dataIndex: "winningNumber",
            key: "winningNumber",
        },
        {
            title: "Previous Guesses",
            dataIndex: "previousGuesses",
            key: "gameId",
        },

    ]



    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const response = await fetch("http://localhost:8080/me/games", {
                    method: "GET",
                    credentials: "include",
                });

                const data = await response.json();
                setFinishedGames(data.finished);
                setUnfinishedGames(data.unfinished);
            } catch (err) {
                setError("Failed to load profile data");
            } finally {
                setLoading(false);
            }
        };
        fetchUserData();
    }, []);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>{error}</p>;

    return (

        <>

            {allGames.length > 0 ? (
                <Table
                    dataSource={allGames}
                    columns={columnsFinished}
                    size="middle"
                    bordered
                    style={{ marginTop: "50px", border: `1px solid var(--border-color)` }}
                    onHeaderRow={() => ({
                        style: {
                            backgroundColor: '#1c2b3f', // background-light
                            color: '#e0f7fa',           // text-light
                            fontWeight: '600',
                            borderBottom: '1px solid #2a3b4c', // border-color
                        },
                    })}
                    onRow={() => ({
                        style: {
                            color: '#e0f7fa',
                            borderBottom: '1px solid #2a3b4c',
                            transition: 'background-color 0.3s ease',
                        },
                    })}
                />
            ) : (
                <p>Games are empty, go and play!</p>

            )}
        </>
    );
};

export default MyGames;

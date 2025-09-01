"use client";
import { useState, useEffect } from "react";
import { Table, Space, Tag } from "antd";
import {Link} from "react-router-dom";

const MyGames = () => {
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true); // start as loading
    const [finishedGames, setFinishedGames] = useState([])
    const [unfinishedGames, setUnfinishedGames] = useState([])

    const allGames = [];
    finishedGames.forEach(game => allGames.push(game));
    unfinishedGames.forEach(game => allGames.push(game));
    const columnsFinished = [{
        title: "id",
        dataIndex: "gameId",
        key: "gameId",
        render: id => {
            return allGames.filter(i => i.result == "PENDING") ? <Link to={`/games/${id}`}> Id </Link> : <p> id</p>
        }
    },
        {
            title: "Difficulty",
            dataIndex: "difficulty",
            key: "gameId",
        },
        {
            title: "Result",
            dataIndex: "result",
            key: "gameId",
        },
        {
            title: "Winning Number",
            dataIndex: "winningNumber",
            key: "gameId",
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
                const response = await fetch("http://localhost:8080/me", {
                    method: "GET",
                    credentials: "include",
                });

                const data = await response.json();
                setFinishedGames(data.finished);
                setUnfinishedGames(data.unfinished);
            } catch (err) {
                console.error("Error fetching profile:", err);
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
                <Table dataSource={allGames} columns={columnsFinished} pagination={false} />
            ) : (
                <p>Games are empty, go and play!</p>

            )}
        </>
    );
};

export default MyGames;

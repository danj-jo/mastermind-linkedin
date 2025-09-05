import { Link, useNavigate } from "react-router-dom";
import {useAuth} from "~/AuthContext";
interface NavbarProps {
    user: string | null;
}

const Navbar: React.FC<NavbarProps> = ({ user }) => {
    const navigate = useNavigate();
    const {isLoggedIn, setIsLoggedIn} = useAuth()

    const handleLogout = async () => {
        try {
            await fetch("http://localhost:8080/logout", {
                method: "POST",
                credentials: "include",
            });
            setIsLoggedIn(false)
            navigate("/login");
        } catch (error) {
            console.error("Logout error:", error);
            navigate("/login");
        }
    };

    return (
        <nav className="navbar">
            <div className="container navbar-container" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <div>
                <Link to={"/home"} className="navbar-brand">
                    Mastermind
                </Link>
                </div>
                <div>
                {isLoggedIn ? (
                    <div style={{ display: "flex", alignItems: "center", gap: "1rem" }}>
                        <div style={{ display: "flex" , alignSelf:"center"}}>
                            <Link className="nav-link" to="/home">Home</Link>
                            <Link className="nav-link" to="/mygames">Past Games</Link>
                            <Link className="nav-link" to="/me">Profile</Link>
                        </div>

                        <button onClick={handleLogout} className="btn btn-outline">
                            Logout
                        </button>
                    </div>
                ) : (
                    <div style={{ display: "flex", gap: "1rem" }}>
                        <Link to="/login">Login</Link>
                        <Link to="/register">Register</Link>
                    </div>
                )}
                </div>
            </div>
        </nav>

    );
};

export default Navbar;

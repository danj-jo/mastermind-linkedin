import {useNavigate} from "react-router-dom";
import LoginForm from "~/routes/login";
import Login from "~/routes/login";
import {useEffect} from "react";

const Logout =  () => {
    const navigate = useNavigate();
    useEffect(() => {
       fetch("http://localhost:8080/logout")
    })
    return <LoginForm/>
}
export default Logout
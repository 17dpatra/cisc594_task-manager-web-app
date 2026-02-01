import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';


function LoginRegister({ setIsAuthenticated }) {    
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('USER'); //default role is 'user'

    const navigate = useNavigate();

    const handleLoginOrRegister = async (e) => {
        e.preventDefault();

        const action = e.nativeEvent.submitter.value; //"login" or "register"

        const endpoint =
            action === "login"
            ? "/api/v1/auth/login"
            : "/api/v1/auth/register";
        
        //validation: incomplete info
        if (!username|| !password) {
            alert("Both fields are required and must be filled in.");
            return;
        }

        //POST request to backend
        try {
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({username, password, roles: [role] }),
            });
            const data = await response.json();
            console.log(data);
            
            if (!response.ok) {
                alert(`${data.error}: ${data.message}` || `${action} failed`);
                return;
            }
            else {
                localStorage.setItem("token", data.token);
            }

            if (action === "login") {
                alert("Login successful!");
                setIsAuthenticated(true); //setting this makes all the other endpoints accessible within the app
                navigate("/app");
            } else {
                alert("Registration successful!");
                setIsAuthenticated(true); //setting this makes all the other endpoints accessible within the app
                navigate("/app");
            }
        }
        catch (error) {
            console.error(`Error during ${action}:`, error);
            alert("Something went wrong.");
        }
    };


    return (
        <div>
            <h1>Welcome to the Task Manager</h1>
            <div style={{ marginTop: "20px" }}>
                <div className='login-or-register-form-container'>
                    <h2>Login/Register</h2>
                    <form onSubmit={handleLoginOrRegister}>
                        <div className='form-group'>
                            <label htmlFor='username'>Username:</label>
                            <input 
                            type='text' 
                            id='username' 
                            name='username' 
                            className='form-control'
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required />
                        </div>
                        <div className='form-group'>
                            <label htmlFor='password'>Password:</label>
                            <input 
                            type='password' 
                            id='password' 
                            name='password' 
                            className='form-control'
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required />
                        </div>
                        {/* checkbox if user wants to login/register as an Admin */}
                        <div className="form-group checkbox-group">
                        <label className="checkbox-label">
                            <input
                            type="checkbox"
                            checked={role === "GLOBAL_ADMIN"}
                            onChange={(e) =>
                                setRole(e.target.checked ? "GLOBAL_ADMIN" : "USER")
                            }
                            />
                            <span>Admin rights?</span>
                        </label>
                        </div>
                        <button type="submit" name="action" value="login">Login</button>
                        <button type="submit" name="action" value="register">Register</button>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default LoginRegister;
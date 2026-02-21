import React, { useState } from 'react';
import { fetchApi } from '../utils/api';
import './Login.css';

const Login = ({ onLogin }) => {
    const [isLogin, setIsLogin] = useState(true);
    const [fullname, setFullname] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        const endpoint = isLogin ? '/auth/login' : '/auth/register';
        const body = isLogin ? { username, password } : { fullname, username, password };

        try {
            const response = await fetchApi(endpoint, {
                method: 'POST',
                body: JSON.stringify(body),
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('token', data.token);
                onLogin();
            } else {
                setError(isLogin ? 'Invalid username or password' : 'Username already exists or invalid data');
            }
        } catch (err) {
            setError('Could not connect to the server');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-card glass-panel">
                <div className="login-header">
                    <h1>MAA BHAWANI</h1>
                    <p>BILLING SYSTEM</p>
                    <h2 className="auth-mode-title">{isLogin ? 'Sign In' : 'Create Account'}</h2>
                </div>

                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleSubmit} className="login-form">
                    {!isLogin && (
                        <div className="form-group">
                            <label htmlFor="fullname">Full Name</label>
                            <input
                                type="text"
                                id="fullname"
                                value={fullname}
                                onChange={(e) => setFullname(e.target.value)}
                                placeholder="Enter your full name"
                                required={!isLogin}
                            />
                        </div>
                    )}
                    <div className="form-group">
                        <label htmlFor="username">Username</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="Enter username"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <div className="password-input-wrapper">
                            <input
                                type={showPassword ? "text" : "password"}
                                id="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Enter password"
                                required
                            />
                            <button
                                type="button"
                                className="btn-toggle-password"
                                onClick={() => setShowPassword(!showPassword)}
                                title={showPassword ? "Hide password" : "Show password"}
                            >
                                {showPassword ? "👁️‍🗨️" : "👁️"}
                            </button>
                        </div>
                    </div>
                    <button type="submit" className="btn-primary" disabled={loading}>
                        {loading ? (isLogin ? 'Logging in...' : 'Registering...') : (isLogin ? 'Sign In' : 'Register')}
                    </button>

                    <div className="auth-toggle">
                        <p>
                            {isLogin ? "Don't have an account? " : "Already have an account? "}
                            <button
                                type="button"
                                className="btn-link"
                                onClick={() => {
                                    setIsLogin(!isLogin);
                                    setError('');
                                }}
                            >
                                {isLogin ? 'Register' : 'Sign In'}
                            </button>
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;

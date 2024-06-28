// src/App.js
import React from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import Home from './components/Home';
import ManageUsers from './components/ManageUsers';
import './styles.css';

function App() {
    return (
        <Router>
            <div>
                <header>
                    <h1>Admin Dashboard</h1>
                </header>
                <nav>
                    <Link to="/">Home</Link>
                    <Link to="/manage-users">Manage Users</Link>
                </nav>
                <div className="container">
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/manage-users" element={<ManageUsers />} />
                    </Routes>
                </div>
                <footer>
                    <p>© 2024 Your Company. All rights reserved.</p>
                </footer>
            </div>
        </Router>
    );
}

export default App;

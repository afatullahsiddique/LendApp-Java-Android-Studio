// src/components/Navbar.js
import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => {
    return (
        <nav className="navbar">
            <h1>Admin Dashboard</h1>
            <div className="links">
                <Link to="/">Home</Link>
                <Link to="/manage-users">Manage Users</Link>
            </div>
        </nav>
    );
};

export default Navbar;
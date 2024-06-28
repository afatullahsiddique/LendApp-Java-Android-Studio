// src/components/UserCard.js
import React from 'react';

function UserCard({ user }) {
    return (
        <div className="user-card">
            <img src={user.profilePicture} alt={user.fullName} />
            <h2>{user.fullName}</h2>
            <p>{user.email}</p>
            <p>{user.phoneNumber}</p>
        </div>
    );
}

export default UserCard;

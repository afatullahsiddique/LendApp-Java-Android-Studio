// src/pages/ManageUsers.js
import React, { useState, useEffect } from 'react';
import { getDatabase, ref, onValue, remove } from "firebase/database";

const ManageUsers = () => {
    const [users, setUsers] = useState([]);

    useEffect(() => {
        const db = getDatabase();
        const usersRef = ref(db, 'users');
        onValue(usersRef, (snapshot) => {
            const data = snapshot.val();
            const usersList = [];
            for (let id in data) {
                usersList.push({ id, ...data[id] });
            }
            setUsers(usersList);
        });
    }, []);

    const handleDelete = (id) => {
        const db = getDatabase();
        remove(ref(db, `users/${id}`));
    };

    return (
        <div>
            <h2>Manage Users</h2>
            <ul>
                {users.map(user => (
                    <li key={user.id}>
                        <p>{user.fullName}</p>
                        <p>{user.email}</p>
                        <button onClick={() => handleDelete(user.id)}>Delete</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default ManageUsers;

import React, { useState, useEffect, useContext } from "react";
import './styles/Calendar.css';
import { AuthContext } from '../context/AuthContext';

//TODO: call valid API for getting, adding, editing, deleting teams
//TODO: call valid API for getting all users

//dummy values for testing
//team.members = user IDs
const initialTeams =
[
    { id: "team-1", name: "Team 1", members: ["user-1", "user-2"]},
    { id: "team-2", name: "Team 2", members: ["user-1", "user-2", "user-3"] }
];

const allUsers = 
[
    { id: "user-1", name: "User 1" },
    { id: "user-2", name: "User 2" },
    { id: "user-3", name: "User 3" },
    { id: "user-4", name: "User 4" },
    { id: "user-5", name: "User 5" },
]


function AdminControls() {
    const { user } = useContext(AuthContext); //user's details
    const [openTeamId, setOpenTeamId] = useState(null);

    //toggles opening and closing accordions
    const toggleTeam = (teamId) => {
        setOpenTeamId(openTeamId === teamId ? null : teamId);
    };

    //add team, edit team
    const [displayAddEditForm, setDisplayAddEditForm] = useState(false);
    const [editingTeam, setEditingTeam] = useState(null);

    //create and edit team form fields
    const [teamName, setTeamName] = useState("");
    const [teamMembers, setTeamMembers] = useState([]);

    //get teams that user controls
    const [teams, setTeams] = useState(initialTeams); //should be useState([])

    //get all users (every single one)
    const [teamMemberOptions, setTeamMemberOptions] = useState(allUsers) //should be useState([])


    //fetch teams from backend on component mount
    const getTeams = async () => {
        try {
            const response = await fetch("/api/teams/username", {
                method: "GET",
                headers: {
                    'Content-Type': 'application/json'
                },
            });
            
            if (response.ok) {
                const data = await response.json();
                setTeams(data);
            } 
            else {
                console.log("Failed to get teams: ", response);
                alert(`${response.statusText}` || `Getting teams failed`);
                return;
            }
        }
        catch (error) {
            console.error("Error getting teams:", error);
        }
    };


    //fetch all users
    const getAllUsers = async () => {
        try {
            const response = await fetch("/api/users", {
                method: "GET",
                headers: {
                    'Content-Type': 'application/json'
                },
            });
            
            if (response.ok) {
                const data = await response.json();
                setTeamMemberOptions(data);
            } 
            else {
                console.log("Failed to get all users: ", response);
                alert(`${response.statusText}` || `Getting all users failed`);
                return;
            }
        }
        catch (error) {
            console.error("Error getting all users:", error);
        }
    };

    //get teams and users on component mount
    useEffect(() => {
        getTeams();
        getAllUsers();
    }, []);


    //reset form fields and state
    const resetForm = () => {
        setTeamName("");
        setTeamMembers([]);
        setDisplayAddEditForm(false);
        setEditingTeam(null)
    }


    //handle adding a new team or editing an existing one
    const handleSubmit = async (e) => {
        e.preventDefault();

        //validation - all are required
        if (!teamName || !teamMemberOptions) {
            alert("All fields are required.");
            return;
        }

        //determine if adding or editing
        const url = editingTeam ? `/api/editteam/${editingTeam.id}` : "/api/addteam";
        const method = editingTeam ? "PUT" : "POST";

        //request to backend to add or edit a task
        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ name: teamName, teamMembers: teamMembers }),
            });

            if (response.ok) {
                getTeams();   //get updated list of teams
                resetForm();    //reset all fields on form
                alert(editingTeam ? "Team updated!":"Team added successfully!");
            }
            else {
                const data = await response.json();
                alert("Failed to save team.");
                console.error("Failed to save team:", data.error);
            }
        } 
        catch (error) {
            console.error("Error:", error);
            alert("An error occurred while adding/updating a team.");
        }
    };


    //populate form with existing data for editing
    const handleEdit = (team) => {
        setEditingTeam(team);
        setTeamName(team.name);
        setTeamMembers(team.members); //team.members already contains IDs
        setDisplayAddEditForm(true);
    };


    //handle deleting a team
    const handleDelete = async (teamId) => {
        if (!window.confirm("Are you sure you want to delete this team?")) return;

        try {
            const response = await fetch(`/api/deleteteam/${teamId}`, {
                method: "DELETE",
            });

            if (response.ok) {
                getTeams();   //get updated list of teams
                alert("Team deleted successfully!");
            } 
            else {
                const data = await response.json();
                console.error("Failed to delete team:", data.error);
                alert("Failed to delete team.");
            }
        } 
        catch (error) {
            console.error("Error deleting team:", error);
            alert("An error occurred while deleting the team.");
        }
    };


    return (
        <div>
        <h2>Your Teams</h2>
        {/* Only display the create team form if displayAddEditForm=true
            displayAddEditForm is toggled onclick of the Add Team button
            Also allows for editing a team */}
            {!displayAddEditForm && (
                <button 
                className="btn btn-primary mb-3" 
                style={{marginBottom: 10}}
                onClick={() => setDisplayAddEditForm(true)}>
                    {"+ Add Team"}
                </button>
            )}

            {displayAddEditForm && (
                <div className="modal-overlay" onClick={() => resetForm()}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>{editingTeam ? "Edit Team" : "Add Team"}</h3>
                            <button className="modal-close" onClick={resetForm}>×</button>
                        </div>
                        <form className="mt-4" onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <label className="form-label">Team Name <span className="text-danger">*</span></label>
                                <input
                                type="text"
                                className="form-control"
                                value={teamName}
                                onChange={(e) => setTeamName(e.target.value)}
                                required
                                />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Team Members <span className="text-danger">*</span></label>
                                <select
                                className="form-control"
                                value={teamMembers}
                                onChange={(e) => setTeamMembers(Array.from(e.target.selectedOptions, option => option.value))}
                                multiple
                                required
                                >
                                    {teamMemberOptions && teamMemberOptions.map((user) => (
                                        <option 
                                        key={user.id} 
                                        value={user.id}
                                        >
                                            {user.name}
                                        </option>
                                    ))}
                                </select>
                                <small className="form-text text-muted">Hold Ctrl/Cmd to select multiple members</small>
                            </div>

                            <button type="submit" className="btn btn-success me-2">
                                {editingTeam ? "Update Team" : "Save Team"}
                            </button>
                            <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={resetForm}
                            >
                                Cancel
                            </button>
                        </form>
                    </div>
                </div>
            )}

            {/* Accordions of teams */}
            {teams.map((team) => {
                const isOpen = openTeamId === team.id;
                return (
                    <div
                    key={team.id}
                    style={{
                        marginBottom: "1rem",
                        borderRadius: "8px",
                        overflow: "hidden",
                        boxShadow: "0 2px 6px rgba(0,0,0,0.1)"
                    }}
                    >
                        {/* Accordion Header */}
                        <div
                        onClick={() => toggleTeam(team.id)}
                        style={{
                            background: "#007bff",
                            color: "#fff",
                            padding: "1rem",
                            fontWeight: "600",
                            cursor: "pointer",
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center"
                        }}
                        >
                            <span>{team.name}</span>
                            <div style={{ display: "flex", gap: "1rem", alignItems: "center" }}>
                                <button
                                onClick={(e) => {
                                    e.stopPropagation();
                                    handleEdit(team);
                                }}
                                style={{
                                    background: "none",
                                    border: "none",
                                    cursor: "pointer",
                                    color: "#fff",
                                    fontSize: "1rem",
                                    padding: "0"
                                }}
                                title="Edit team"
                                >
                                    ✎
                                </button>
                                <button
                                onClick={(e) => {
                                    e.stopPropagation();
                                    handleDelete(team.id);
                                }}
                                style={{
                                    background: "none",
                                    border: "none",
                                    cursor: "pointer",
                                    color: "#fff",
                                    fontSize: "1rem",
                                    padding: "0"
                                }}
                                title="Delete team"
                                >
                                    🗑
                                </button>
                                <span>{isOpen ? "▲" : "▼"}</span>
                            </div>
                        </div>
                        
                        {/* Accordion Body */}
                        {isOpen && (
                            <div
                            style={{
                            display: "flex",
                            flexDirection: "column",
                            gap: "0.5rem",
                            padding: "1rem",
                            background: "#f9f9f9"
                            }}
                            >
                                {team.members.length === 0 ? (
                                    <p style={{ color: "#888" }}>No members</p>
                                ) : (
                                    team.members.map((memberId) => {
                                        const user = teamMemberOptions.find((u) => u.id === memberId);
                                        return (
                                            <div
                                            key={memberId}
                                            style={{
                                                padding: "0.75rem 1rem",
                                                borderRadius: "6px",
                                                background: "#fff",
                                                boxShadow: "0 1px 3px rgba(0,0,0,0.1)"
                                            }}
                                            >
                                                {user ? user.name : "Unknown User"}
                                            </div>
                                        );
                                    })
                                )}
                            </div>
                        )}
                    </div>
                );
            })}
        </div>
    );
}

export default AdminControls;
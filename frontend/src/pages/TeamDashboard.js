import { useState, useEffect, useContext } from 'react';
import './styles/TeamDashboard.css';
import { AuthContext } from '../context/AuthContext';

const statusOrder = [
    "created", 
    "in-progress", 
    "validating",
    "completed"
];
const statusColors = {
    created: "#ea6671",
    "in-progress": "#f6ad55",
    validating: "#686ad3",
    completed: "#45cf4e"
};

function TeamDashboard() {
    const { user } = useContext(AuthContext); //user's details
    const [openStatus, setOpenStatus] = useState(null);
    const token = localStorage.getItem("token");

    //toggles opening and closing accordions
    const toggleStatus = (status) => {
        setOpenStatus(openStatus === status ? null : status);
    };

    //get tasks
    const [tasks, setTasks] = useState([]);

    //get possible assignees (can be anyone in user's team)
    const [assigneeOptions, setAssigneeOptions] = useState(null);
    
    //filtering
    const [filterBy, setFilterBy] = useState("");
    const [filterValue, setFilterValue] = useState("");

    //fetch tasks from backend on component mount
    const getTasks = async () => {
        try {
            const response = await fetch(`/api/v2/tasks/user/team/${user.id}`, {
                method: "GET",
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
            })
            
            if (response.ok) {
                const res = await response.json();
                console.log(res.data)
                setTasks(res.data);
            } 
            else {
                const data = await response.json();
                console.error("Failed to get tasks for team:", data);
                if (data.error == "User is not part of any team") {
                    alert(data.error + ". Please make sure you are a part of a team.");
                }
                else {
                    alert(data.error || data.message || data.data || "Getting tasks for team failed");
                }
            }
        }
        catch (error) {
            console.error("Error getting tasks for team:", error);
        }
    };


    //fetch assignees within user's team
    const getAssignees = async () => {
        try {
            const response = await fetch("/api/assignees", {
                method: "GET",
                headers: {
                    'Content-Type': 'application/json'
                },
            })
            
            if (response.ok) {
                const data = await response.json();
                setAssigneeOptions(data);
            } 
            else {
                console.log("Failed to get assignees for user: ", response)
                alert(`${response.statusText}` || `Getting assignees for the user failed`);
                return;
            }
        }
        catch (error) {
            console.error("Error getting assignees for user:", error);
        }
    };


    //get tasks and assignees on component mount
    useEffect(() => {
        getTasks();
        //getAssignees();
    }, []);

    
    //handle filtering tasks
    const getFilteredTasks = () => {
        if (!filterBy || !filterValue) {
            return tasks;
        }

        const filtered = {};
        
        Object.keys(tasks).forEach((status) => {
            filtered[status] = tasks[status].filter((task) => {
                const filterValueLower = filterValue.toLowerCase();
                
                switch(filterBy) {
                    case "name":
                        return task.title && task.title.toLowerCase().includes(filterValueLower);
                    case "priority":
                        return task.priority && task.priority.toLowerCase().includes(filterValueLower);
                    case "deadline":
                        return task.dueDate && task.dueDate.includes(filterValue);
                    case "assignee":
                        return task.assignedTo["username"] && task.assignedTo["username"].toLowerCase().includes(filterValueLower);
                    default:
                        return true;
                }
            });
        });
        
        return filtered;
    };

    return (
        <div style={{ maxWidth: "100%", overflow: "hidden" }}>
            <h2 className="mb-4" style={{ paddingBottom: "2rem" }}>Your Team's Tasks</h2>
            <p>Here you can see the tasks of every member in your team(s), who they are assigned to, and the deadline of each task.</p>
            <p style={{color:"red"}}>You must be a part of a team to see the tasks populated. Contact your team's admin to add you.</p>
            {/* Filter controls */}
            <div className="filter-controls">
                <label style={{ margin: 0 }}>Filter by:</label>
                <select
                value={filterBy}
                onChange={(e) => setFilterBy(e.target.value)}
                >
                    <option value="">-- No Filter --</option>
                    <option value="name">Task Name</option>
                    <option value="priority">Task Priority</option>
                    <option value="deadline">Task Deadline</option>
                    <option value="assignee">Task Assignee</option>
                </select>

                <input
                type="text"
                className="form-control"
                placeholder="Enter filter..."
                value={filterValue}
                onChange={(e) => setFilterValue(e.target.value)}
                />
            </div>

            
            {/* Accordions of tasks */}
            {statusOrder.map((status) => {
                const isOpen = openStatus === status;
                return (
                    <div
                    key={status}
                    style={{
                        marginBottom: "1rem",
                        borderRadius: "8px",
                        overflow: "hidden",
                        boxShadow: "0 2px 6px rgba(0,0,0,0.1)"
                    }}
                    >
                        {/* Accordion Header */}
                        <div
                        onClick={() => toggleStatus(status)}
                        style={{
                            background: statusColors[status],
                            color: "#fff",
                            padding: "1rem",
                            fontWeight: "600",
                            cursor: "pointer",
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center"
                        }}
                        >
                            <span>{status.charAt(0).toUpperCase() + status.slice(1)}</span>
                            <span>{isOpen ? "▲" : "▼"}</span>
                        </div>
                        
                        {/* Accordion Body */}
                        {isOpen && (
                            <div
                            style={{
                            display: "flex",
                            gap: "1rem",
                            flexWrap: "wrap",
                            padding: "1rem",
                            background: "#f9f9f9"
                            }}
                            >
                                {getFilteredTasks()[status]?.length === 0 ? (
                                    <p style={{ color: "#888" }}>No tasks</p>
                                    ) : 
                                    (
                                        getFilteredTasks()[status]?.map((task) => 
                                            (
                                                <div
                                                key={task.id}
                                                style={{
                                                    padding: "0.5rem 1rem",
                                                    borderRadius: "6px",
                                                    background: "#fff",
                                                    boxShadow: "0 1px 3px rgba(0,0,0,0.1)",
                                                    minWidth: "150px",
                                                    flex: "1 0 150px",
                                                    display: "flex",
                                                    justifyContent: "space-between",
                                                    alignItems: "center",
                                                    gap: "0.25rem",
                                                    flexDirection: "column",
                                                }}
                                                >
                                                    <div><b>Task: </b>{task.title}</div>
                                                    <div><b>Assignee: </b>{task.assignedTo["username"]}</div>
                                                    <div><b>Deadline: </b>{task.dueDate}</div>
                                                    <div><b>Priority: </b>{task.priority ? task.priority.charAt(0).toUpperCase() + task.priority.slice(1) : ""}</div>
                                                </div>
                                            )
                                        )
                                    )
                                }
                            </div>
                        )}
                    </div>
                );
            })}
        </div>
    );
}

export default TeamDashboard;
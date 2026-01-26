import { useState, useEffect } from 'react';
import './styles/TeamDashboard.css';

//TODO: call valid API for getting ALL tasks in user's team
//TODO: call valid API for getting all assignee options

//dummy values for testing
const initialTasks = {
    created: [
        { id: "task-1", name: "Set up repo" },
        { id: "task-2", name: "Design database" }
    ],
    "in-progress": [
        { id: "task-3", name: "Implement login form" }
    ],
    validating: [
        { id: "task-4", name: "Write unit tests" }
    ],
    completed: [
        { id: "task-5", name: "Deploy app" }
    ]
};

const statusOrder = ["created", "in-progress", "validating", "completed"];
const statusColors = {
    created: "#ea6671",
    "in-progress": "#f6ad55",
    validating: "#686ad3",
    completed: "#45cf4e"
};

function TeamDashboard() {
    const [currentDate] = useState(new Date());
    const [openStatus, setOpenStatus] = useState(null);

    //toggles opening and closing accordions
    const toggleStatus = (status) => {
        setOpenStatus(openStatus === status ? null : status);
    };

    //get tasks
    const [tasks, setTasks] = useState(initialTasks);// useState([]) - should be null in the beginning. Just set to initial tasks as a dummy for now

    //get possible assignees (can be anyone in user's team)
    const [assigneeOptions, setAssigneeOptions] = useState(null);
    
    //filtering
    const [filterBy, setFilterBy] = useState("");
    const [filterValue, setFilterValue] = useState("");

    //fetch tasks from backend on component mount
    const getTasks = async () => {
        try {
            const response = await fetch("/api/tasksforteam", {
                method: "GET",
                headers: {
                    'Content-Type': 'application/json'
                },
            })
            
            if (response.ok) {
                const data = await response.json();
                setTasks(data);
            } 
            else {
                console.log("Failed to get tasks: ", response)
                alert(`${response.statusText}` || `Getting tasks failed`);
                return;
            }
        }
        catch (error) {
            console.error("Error getting tasks:", error);
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
        getAssignees();
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
                        return task.name && task.name.toLowerCase().includes(filterValueLower);
                    case "priority":
                        return task.priority && task.priority.toLowerCase().includes(filterValueLower);
                    case "deadline":
                        return task.deadline && task.deadline.includes(filterValue);
                    case "assignee":
                        return task.assignee && task.assignee.toLowerCase().includes(filterValueLower);
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
            {/* Filter controls */}
            <div className="filter-controls">
                <label style={{ margin: 0 }}>Filter by:</label>
                <select
                value={filterBy}
                onChange={(e) => setFilterBy(e.target.value)}
                >
                    <option value="">-- No Filter --</option>
                    <option value="name">Name</option>
                    <option value="priority">Priority</option>
                    <option value="deadline">Deadline</option>
                    <option value="assignee">Assignee</option>
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
                                {getFilteredTasks()[status].length === 0 ? (
                                    <p style={{ color: "#888" }}>No tasks</p>
                                    ) : 
                                    (
                                        getFilteredTasks()[status].map((task) => 
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
                                                    gap: "0.5rem"
                                                }}
                                                >
                                                    <span>{task.name}</span>
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
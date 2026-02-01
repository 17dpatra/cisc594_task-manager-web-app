import { useState, useEffect, useContext } from 'react';
import './styles/UserDashboard.css';
import { AuthContext } from '../context/AuthContext';

//TODO: call valid API for getting, adding, editing, deleting user's tasks
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

const statusOrder = ["created", 
    //"in-progress", 
    //"validating", 
    "completed"];
const statusColors = {
    created: "#ea6671",
    //"in-progress": "#f6ad55",
    //validating: "#686ad3",
    completed: "#45cf4e"
};

function UserDashboard() {
    const { user } = useContext(AuthContext); //user's details
    const [currentDate] = useState(new Date());
    const token = localStorage.getItem("token");

    const [openStatus, setOpenStatus] = useState(null);

    //toggles opening and closing accordions
    const toggleStatus = (status) => {
        setOpenStatus(openStatus === status ? null : status);
    };

    //add task form, edit task boolean
    const [displayAddEditForm, setDisplayAddEditForm] = useState(false);
    const [editingTask, setEditingTask] = useState(null);

    //create and edit task form fields
    const [taskName, setTaskName] = useState("");
    const [taskDescription, setTaskDescription] = useState("");
    const [taskDeadline, setTaskDeadline] = useState("");
    const [taskAssignee, setTaskAssignee] = useState("");
    const [taskStatus, setTaskStatus] = useState("Created");
    const [taskPriority, setTaskPriority] = useState("Medium");

    //get tasks
    const [tasks, setTasks] = useState(initialTasks);// useState([]) - should be null in the beginning. Just set to initial tasks as a dummy for now

    //get possible assignees (can be anyone in user's team)
    const [assigneeOptions, setAssigneeOptions] = useState([]);
    
    //filtering
    const [filterBy, setFilterBy] = useState("");
    const [filterValue, setFilterValue] = useState("");

    //fetch tasks from backend on component mount
    const getTasks = async () => {
        try {
            const response = await fetch(`/api/v1/tasks/get_tasks/${user.id}`, {
                method: "GET",
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
            });
            
            if (response.ok) {
                const data = await response.json();
                setTasks(data);
            } 
            else {
                console.log("Failed to get tasks: ", response);
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
            });
            
            if (response.ok) {
                const data = await response.json();
                setAssigneeOptions(data);
            } 
            else {
                console.log("Failed to get assignees for user: ", response);
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


    //reset form fields and state
    const resetForm = () => {
        setTaskName("");
        setTaskDescription("");
        setTaskDeadline("");
        setTaskAssignee("");
        setTaskStatus("Created");
        setTaskPriority("Medium");
        setDisplayAddEditForm(false);
        setEditingTask(null)
    }

    //handle adding a new task or editing an existing one
    const handleSubmit = async (e) => {
        e.preventDefault();

        //validation - all are required
        if (!taskName || !taskDescription || !taskDeadline 
            || !taskAssignee || !taskStatus || !taskPriority) {
            alert("All fields are required.");
            return;
        }

        //validation - deadline must be after or including today's date
        if (new Date(taskDeadline) < currentDate) {
            alert("Task's deadline must be after or including today's date.");
            return;
        }

        //determine if adding or editing
        const url = editingTask ? `/api/v1/tasks/update_task/${editingTask.id}` : "/api/v1/tasks/addtask";
        const method = editingTask ? "PUT" : "POST";

        //request to backend to add or edit a task
        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ name: taskName, description: taskDescription, deadline: taskDeadline, assignee: taskAssignee, status: taskStatus, priority: taskPriority }),
            });

            if (response.ok) {
                getTasks();   //get updated list of tasks
                resetForm();    //reset all fields on form
                alert(editingTask ? "Task updated!":"Task added successfully!");
            }
            else {
                const data = await response.json();
                alert("Failed to save task.");
                console.error("Failed to save task:", data.error);
            }
        } 
        catch (error) {
            console.error("Error:", error);
            alert("An error occurred while adding/updating a task.");
        }
    };


    //populate form with existing data for editing
    const handleEdit = (task) => {
        setEditingTask(task);
        setTaskName(task.name);
        setTaskDescription(task.description);
        setTaskDeadline(task.deadline);
        setTaskAssignee(task.assignee);
        setTaskStatus(task.status);
        setTaskPriority(task.priority);
        setDisplayAddEditForm(true);
    };


    //handle deleting a task
    const handleDelete = async (taskId) => {
        if (!window.confirm("Are you sure you want to delete this task?")) return;

        try {
            const response = await fetch(`/api/v1/tasks/delete_task/${taskId}`, {
                method: "DELETE",
            });

            if (response.ok) {
                getTasks();   //get updated list of tasks
                alert("Task deleted successfully!");
            } 
            else {
                const data = await response.json();
                console.error("Failed to delete task:", data.error);
                alert("Failed to delete task.");
            }
        } 
        catch (error) {
            console.error("Error deleting task:", error);
            alert("An error occurred while deleting the task.");
        }
    };


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
                    default:
                        return true;
                }
            });
        });
        
        return filtered;
    };

    return (
        <div style={{ maxWidth: "100%", overflow: "hidden" }}>
            <h2 className="mb-4" style={{ paddingBottom: "2rem" }}>Your Tasks</h2>
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
                </select>

                <input
                type="text"
                className="form-control"
                placeholder="Enter filter..."
                value={filterValue}
                onChange={(e) => setFilterValue(e.target.value)}
                />
            </div>

            {/* Only display the create task form if displayAddEditForm=true
            displayAddEditForm is toggled onclick of the Add Task button
            Also allows for editing a task */}
            {!displayAddEditForm && (
                <button 
                className="btn btn-primary mb-3" 
                style={{marginBottom: 10}}
                onClick={() => setDisplayAddEditForm(true)}>
                    {"+ Add Task"}
                </button>
            )}

            {displayAddEditForm && (
                <div className="modal-overlay" onClick={() => resetForm()}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>{editingTask ? "Edit Task" : "Add Task"}</h3>
                            <button className="modal-close" onClick={resetForm}>×</button>
                        </div>
                        <form className="mt-4"  onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <label className="form-label">Name <span className="text-danger">*</span></label>
                                <input
                                type="text"
                                className="form-control"
                                value={taskName}
                                onChange={(e) => setTaskName(e.target.value)}
                                required
                                />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Short description <span className="text-danger">*</span></label>
                                <input
                                type="text"
                                className="form-control"
                                value={taskDescription}
                                onChange={(e) => setTaskDescription(e.target.value)}
                                required
                                />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Deadline <span className="text-danger">*</span></label>
                                <input
                                type="date"
                                className="form-control"
                                value={taskDeadline}
                                onChange={(e) => setTaskDeadline(e.target.value)}
                                required
                                />
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Assignee <span className="text-danger">*</span></label>
                                <select
                                className="form-control"
                                value={taskAssignee}
                                onChange={(e) => setTaskAssignee(e.target.value)}
                                required
                                >
                                    <option value="">-- Select an assignee --</option>
                                    {assigneeOptions && assigneeOptions.map((assignee) => (
                                        <option key={assignee.id} value={assignee.id}>
                                            {assignee.name}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="mb-3">
                                <label className="form-label">Status <span className="text-danger">*</span></label>
                                <select
                                value={taskStatus}
                                onChange={(e) => setTaskStatus(e.target.value)}
                                >
                                    <option value="low">Low</option>
                                    <option value="medium">Medium</option>
                                    <option value="high">High</option>
                                </select>
                            </div>

                            <button type="submit" className="btn btn-success me-2">
                                {editingTask ? "Update Task" : "Save Task"}
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
                                                    <div style={{ display: "flex", gap: "0.5rem" }}>
                                                        <button
                                                        onClick={() => handleEdit(task)}
                                                        style={{
                                                            background: "none",
                                                            border: "none",
                                                            cursor: "pointer",
                                                            color: "#007bff",
                                                            fontSize: "1rem",
                                                            padding: "0"
                                                        }}
                                                        title="Edit task"
                                                        >
                                                            ✎
                                                        </button>

                                                        <button
                                                        onClick={() => handleDelete(task.id)}
                                                        style={{
                                                            background: "none",
                                                            border: "none",
                                                            cursor: "pointer",
                                                            color: "#dc3545",
                                                            fontSize: "1rem",
                                                            padding: "0"
                                                        }}
                                                        title="Delete task"
                                                        >
                                                            🗑
                                                        </button>
                                                    </div>
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

export default UserDashboard;
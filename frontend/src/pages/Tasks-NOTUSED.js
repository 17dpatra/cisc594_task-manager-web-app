import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";

//TODO: display all tasks for the user
//TODO: allow adding, editing, deleting tasks

const initialTasks = {
    created: [
        { id: "task-1", title: "Set up repo" },
        { id: "task-2", title: "Design database" }
    ],
    "in-progress": [
        { id: "task-3", title: "Implement login form" }
    ],
    validating: [
        { id: "task-4", title: "Write unit tests" }
    ],
    completed: [
        { id: "task-5", title: "Deploy app" }
    ]
};

const statusOrder = ["created", "in-progress", "validating", "completed"];
const statusColors = {
    created: "#ea6671",
    "in-progress": "#f6ad55",
    validating: "#686ad3",
    completed: "#45cf4e"
};

function Tasks() {
    const [openStatus, setOpenStatus] = useState(null);

    //toggles opening and closing accordions
    const toggleStatus = (status) => {
        setOpenStatus(openStatus === status ? null : status);
    };

    //add task, edit task
    const [displayForm, setDisplayForm] = useState(false);
    const [editingTask, setEditingTask] = useState(null);

    //create and edit task form fields
    const [taskName, setTaskName] = useState("")
    const [taskDescription, setTaskDescription] = useState("")
    const [taskDeadline, setTaskDeadline] = useState("")
    const [taskAssignee, setTaskAssignee] = useState("")
    const [taskStatus, setTaskStatus] = useState("Created")
    const [taskPriority, setTaskPriority] = useState("Medium")

    //get tasks
    const [tasks, setTasks] = useState(initialTasks);// useState([]) - should be null in the beginning. Just set to initial tasks as a dummy for now

    //sorting
    
    return (
        <div style={{ padding: "2rem", margin: "0 auto" }}>
            <h1 style={{ textAlign: "center", marginBottom: "2rem" }}>Your Tasks</h1>
            
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
                            width: "100vw",
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
                                {tasks[status].length === 0 ? (
                                    <p style={{ color: "#888" }}>No tasks</p>
                                    ) : (
                                        tasks[status].map((task) => (
                                            <div
                                            key={task.id}
                                            style={{
                                                padding: "0.5rem 1rem",
                                                borderRadius: "6px",
                                                background: "#fff",
                                                boxShadow: "0 1px 3px rgba(0,0,0,0.1)",
                                                minWidth: "150px",
                                                flex: "1 0 150px"
                                            }}
                                            >
                                                {task.title}
                                            </div>
                                            ))
                                        )}
                            </div>
                        )}
                    </div>
                );
            })}
        </div>
    );
}

export default Tasks;
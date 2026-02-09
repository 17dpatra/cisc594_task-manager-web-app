import { useState, useEffect, useContext } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import './styles/Calendar.css';
import { AuthContext } from '../context/AuthContext';

const statusColors = {
    created: "#ea6671",
    "in_progress": "#f6ad55",
    validating: "#686ad3",
    completed: "#45cf4e"
};

function Calendar() {
    const { user } = useContext(AuthContext); //user's details
    const token = localStorage.getItem("token");
    const [tasks, setTasks] = useState([]);


    //get all tasks of user
    const getTasksOfUser = async () => {
        try {
            const response = await fetch(`/api/v2/tasks/get_tasks?userId=${user.id}`, {
                method: "GET",
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
            });

            if (response.ok) {
                const res = await response.json();
                //console.log(res)
                setTasks(res);
            } 
            else {
                const data = await response.json();
                console.error("Failed to get tasks for user:", data);
                alert(data.error || data.message || data.data || "Getting tasks for user failed");
            }
        } 
        catch (error) {
            console.error("Error getting tasks for user:", error);
        }
    };

    //get all tasks in user's team - this will only work if the user is assigned a team
    const getTasksOfTeam = async () => {
        try {
            const response = await fetch(`/api/v2/tasks/user/team/${user.id}`, {
                method: "GET",
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
            });

            if (response.ok) {
                const res = await response.json();
                //console.log(res.data)
                setTasks(res.data);
            } 
            else {
                const data = await response.json();
                console.error("Failed to get tasks for team:", data);
                if (data.error == "User is not part of any team") {
                    //this means that the user is not assigned a team.
                    //they should be able to view their own deadlines (if any) in this case.
                    await getTasksOfUser();
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

    //get tasks on component mount
    useEffect(() => {
        getTasksOfTeam();
    }, []);


    //flatten tasks into single array
    const flatTasks = (() => {
        if (!tasks) return [];

        //if it's an array, return as-is - this is for get_tasks endpoint
        if (Array.isArray(tasks)) return tasks;

        //if it's an object with status keys, flatten all arrays inside it - this is for get team tasks
        if (typeof tasks === "object") return Object.values(tasks).flat();

        return [];
    })();


    //convert each task into a FullCalendar event
    const events = flatTasks.map(task => (
        {
            id: task.id,
            title: task.title || task.name,       //handle both key names
            start: task.dueDate || task.deadline, //FullCalendar needs 'start'
            backgroundColor: statusColors[task.status?.toLowerCase()] || "#888", //fallback color
            borderColor: statusColors[task.status?.toLowerCase()] || "#888", //fallback color
            textColor: "#fff"
        }
    ));


    return (
        <div>
            <h2>Your Calendar</h2>
            <br />
            <p>Here you can view your own and your team members' deadlines.</p>
            <br />
            {/* Legend */}
            <div className="calendar-legend" style={{ display: 'flex', gap: '1rem', marginBottom: '1rem', flexWrap: 'wrap' }}>
            {Object.entries(statusColors).map(([status, color]) => (
                <div key={status} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span style={{
                        width: '20px',
                        height: '20px',
                        backgroundColor: color,
                        display: 'inline-block',
                        borderRadius: '4px',
                        border: '1px solid #ccc'
                    }}></span>
                    <span>{status.replace('_', ' ').replace(/\b\w/g, c => c.toUpperCase())}</span>
                </div>
            ))}
            </div>
            
            {/* FullCalendar display */}
            <FullCalendar
            plugins={[dayGridPlugin]}
            initialView="dayGridMonth"
            events={events}
            height="auto"
            eventDidMount={(data) => {
                //display task on the due date
                const todayDate = new Date();
                todayDate.setHours(0, 0, 0, 0);
                const dueDateOfEvent = new Date(data.event.start); //use 'start'

                if (dueDateOfEvent < todayDate) {
                    data.el.style.backgroundColor = '#e0e0e0';
                }

                //show tooltip with some task details
                const task = flatTasks.find(t => t.id.toString() === data.event.id.toString());
                if (task) {
                    data.el.setAttribute('title', 
                        `Title: ${task.title || task.name}\n` +
                        `Assignee: ${task.assignedTo?.username || 'N/A'}\n` +
                        `Status: ${task.status}\n` +
                        `Priority: ${task.priority ? task.priority.charAt(0).toUpperCase() + task.priority.slice(1) : ""}`
                    );
                }
            }}
            />
        </div>
    );
}

export default Calendar;
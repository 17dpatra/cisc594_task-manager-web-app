import {useState, useEffect} from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import './styles/Calendar.css';

//TODO: call valid API for getting ALL tasks in user's team

function Calendar() {
    const [tasks, setTasks] = useState([]);

    //get all tasks in user's team
    const getTasks = async () => {
        try {
            const response = await fetch("/api/tasks", {
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.ok) {
                const data = await response.json();
                setTasks(data);
                //console.log(data);
            } 
            else {
                console.error("Failed to get tasks:", response.statusText);
            }
        } 
        catch (error) {
            console.error("Error getting tasks:", error);
        }
    };

    //get tasks on component mount
    useEffect(() => {
        getTasks();
    }, []);

    //convert each task into a FullCalendar event
    const events = tasks.map((a) => ({
        id: a.id,
        title: a.name,
        deadline: a.deadline, //must be in YYYY-MM-DD format and FullCalendar needs it to be called 'start' for comparison
    }));


    return (
        <div>
            <h2>Your Calendar</h2>

            <FullCalendar
            plugins={[dayGridPlugin]}
            initialView="dayGridMonth"
            events={events}
            height="auto"
            eventDidMount={(data) => {
                const todayDate = new Date();
                todayDate.setHours(0, 0, 0, 0);
                const dueDateOfEvent = new Date(data.event.deadline);

                if (dueDateOfEvent < todayDate) {
                    data.el.style.backgroundColor = '#e0e0e0';    //change to grey if the due date has passed
                }
            }}
            />
        </div>
    );
}

export default Calendar;
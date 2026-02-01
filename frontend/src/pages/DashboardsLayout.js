import { useState, useContext, useEffect } from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import './styles/DashboardsLayout.css';
import { AuthContext } from '../context/AuthContext';

function DashboardsLayout() {
  const { setUser } = useContext(AuthContext);
  const { user } = useContext(AuthContext);
  const [displayAdmin, setAdminDisplay] = useState(null);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const location = useLocation();
  const token = localStorage.getItem("token");

  //check if on the root path (no child route selected)
  //controls when to view dashboard directions and when to hide
  const isRootPath = location.pathname === '/app';

  //collapse dropdown once it has been clicked
  const handleDropdownItemClick = () => {
    setDropdownOpen(false);
  };

  //GET request to get the user's ID and role
  const getUserIdAndRole = async () => {
    try {
        const response = await fetch("/api/v1/users/me", {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          credentials: 'include'
        });
        const data = await response.json();
        
        if (!response.ok) {
            alert(`${data.error}: ${data.message}` || `Getting UserID and Role failed`);
            return;
        }
        else {
          setUser(data)
          console.log(data)
          if (data.roles[0] == "GLOBAL_ADMIN") {
            setAdminDisplay(true)
          }
          else {
            setAdminDisplay(false);
          }
        }
    }
    catch (error) {
        console.error(`Error:`, error);
        alert("Something went wrong.");
    }
  };

  //get user id and role on component mount
  useEffect(() => {
      getUserIdAndRole();
  }, []);

  if (displayAdmin == null) {
    return <div>Loading data...</div>
  }

  return (
    <div className="dashboards-layout">
      <nav className="navbar">
        <div>
          <Link to="/app" className="nav-link">Home</Link>
        </div>
        <div className="nav-item dropdown">
          <button 
            className="nav-link dropdown-toggle"
            onClick={() => setDropdownOpen(!dropdownOpen)}
          >
            Dashboards
          </button>
          {dropdownOpen && (
            <div className="dropdown-menu">
              <Link to="/app/userdashboard" className="dropdown-item" onClick={handleDropdownItemClick}>User Dashboard</Link>
              <Link to="/app/teamdashboard" className="dropdown-item" onClick={handleDropdownItemClick}>Team Dashboard</Link>
            </div>
          )}
        </div>
        <div>
          <Link to="/app/calendar" className="nav-link">Calendar</Link>
        </div>
        <div>
          {displayAdmin && (
            <Link to="/app/admincontrols" className="nav-link">Admin Controls</Link>
          )}
        </div>
      </nav>
      <main className="content">
        {isRootPath && (
          <>
            <h2 className="mb-4">Welcome to your Task Manager</h2>
            <br/>
            <p>Click on the Dashboards tab to view:</p>
            <ul>
              <li>Your user dashboard with only tasks assigned to you.</li>
              <li>The team's dashboard where you can see all the tasks assigned to your team.</li>
            </ul>
            <p>Click on the Calendar tab to view when each task is due.</p>
            <p>Click on the Admin Controls tab to make changes to your team.</p>
          </>
        )}
        <Outlet />
      </main>
    </div>
  );
}

export default DashboardsLayout;
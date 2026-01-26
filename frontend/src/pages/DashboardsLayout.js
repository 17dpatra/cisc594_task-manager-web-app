import { useState, useContext } from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import './styles/DashboardsLayout.css';
import { AuthContext } from '../context/AuthContext';

function DashboardsLayout() {
  const { user } = useContext(AuthContext); //user's details
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const location = useLocation();

  //check if on the root path (no child route selected)
  //controls when to view dashboard directions and when to hide
  const isRootPath = location.pathname === '/app';

  //collapse dropdown once it has been clicked
  const handleDropdownItemClick = () => {
    setDropdownOpen(false);
  };

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
          <Link to="/app/admincontrols" className="nav-link">Admin Controls</Link>
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
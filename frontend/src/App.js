import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import "./App.css";
import { useState } from 'react';
import LoginRegister from './pages/LoginRegister';
import DashboardsLayout from './pages/DashboardsLayout';
import UserDashboard from './pages/UserDashboard';
import TeamDashboard from './pages/TeamDashboard';
import Calendar from './pages/Calendar';
import AdminControls from './pages/AdminControls';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false); //only allows other routes to be accessible after successful login

  return (
    <div className="App">
      <Router>
        <Routes>
          <Route path="/" element={<LoginRegister setIsAuthenticated={setIsAuthenticated}/>} />
          <Route 
            path="/app" 
            element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <DashboardsLayout />
                </ProtectedRoute>
            }
          >
            <Route path="userdashboard" element={<UserDashboard />} />
            <Route path="teamdashboard" element={<TeamDashboard />} />
            <Route path="calendar" element={<Calendar />} />
            <Route path="admincontrols" element={<AdminControls />} />
          </Route>
        </Routes>
      </Router>
    </div>
  );
}

export default App;
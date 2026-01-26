import { Navigate } from 'react-router-dom';

//sets isAuthenticated=true if login/registration is successful
//if isAuthenticated=true, all children endpoints are accessible
//otherwise, children endpoints are not accessible
function ProtectedRoute({ children, isAuthenticated }) {
  return isAuthenticated ? children : <Navigate to="/" replace />;
}

export default ProtectedRoute;
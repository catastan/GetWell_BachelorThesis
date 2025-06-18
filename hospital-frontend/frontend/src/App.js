import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Navbar from "./components/Navbar";
import AppointmentPatientPage from "./pages/AppointmentPatientPage";
import HomePage from "./pages/HomePage";
import RegisterPage from "./pages/RegisterPage";
import LoginPage from "./pages/LoginPage";
import ChangeDataPage from "./pages/ChangeDataPage";
import ChangePassword from "./pages/ChangePasswordPage";
import AppointmentActionsPage from "./pages/AppointmentDoctorPage";
import MedicationActionsPage from "./pages/MedicationActionsPage";
import DoctorManagementPage from "./pages/DoctorManagementPage";
function App() {
  return (
    <Router>
      <AuthProvider>
        <Navbar />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/doctor/app" element={<AppointmentActionsPage />} />
          <Route path="/patient/app" element={<AppointmentPatientPage />} />
          <Route path="/admin/doctors" element={<DoctorManagementPage />} />
          <Route path="/medication" element={<MedicationActionsPage />} />
          <Route path="/doctor/med" element={<MedicationActionsPage />} />
          <Route path="/changeData" element={<ChangeDataPage />} />
          <Route path="/changePassword" element={<ChangePassword />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;

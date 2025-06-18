import {
  Table,
  TableHead,
  TableBody,
  TableCell,
  TableRow,
  Paper,
  TableContainer,
  Typography,
  Pagination,
} from "@mui/material";
import { useState, useEffect } from "react";
import getAppointmentsForPatient from "../api/patientAppointments";
import wallpaper from "../assets/wallpaper.png";
import CONSTANTS from "../constants/Constants";

function AppointmentPatientPage() {
  useEffect(() => {
    document.body.style.margin = "0";
    document.body.style.overflow = "hidden";
  }, []);

  const [appointments, setAppointments] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const appointmentsPerPage = 8;

  const indexOfLastAppointment = currentPage * appointmentsPerPage;
  const indexOfFirstAppointment = indexOfLastAppointment - appointmentsPerPage;
  const currentAppointments = appointments.slice(
    indexOfFirstAppointment,
    indexOfLastAppointment
  );
  const totalPages = Math.ceil(appointments.length / appointmentsPerPage);

  useEffect(() => {
    getAppointmentsForPatient().then(([success, data]) => {
      if (success) {
        setAppointments(data);
      } else {
        console.error("Eroare la încărcarea programărilor.");
      }
    });
  }, []);

  return (
    <div
      style={{
        backgroundImage: `url(${wallpaper})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        minHeight: "100vh",
        padding: "40px",
        fontFamily: "'Segoe UI', sans-serif",
      }}
    >
      <div
        style={{
          width: "100%",
          maxWidth: "1000px",
          margin: "0 auto",
          display: "flex",
          flexDirection: "column",
          gap: "20px",
        }}
      >
        <Typography variant="h4" color="primary" sx={{ mb: 1 }}>
          Lista Programărilor
        </Typography>

        <TableContainer component={Paper} sx={{ borderRadius: 3 }}>
          <Table>
            <TableHead
              sx={(theme) => ({
                backgroundColor: theme.palette.primary.main,
              })}
            >
              <TableRow>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Data
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Durată (min)
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Doctor
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {currentAppointments.map((appointment, index) => (
                <TableRow key={index} sx={{ height: "60px" }}>
                  <TableCell>
                    {new Date(appointment.date).toLocaleString("ro-RO", {
                      day: "2-digit",
                      month: "2-digit",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                      hour12: false,
                    })}
                  </TableCell>
                  <TableCell>{appointment["duration(min)"]}</TableCell>
                  <TableCell>
                    {appointment.doctor_name || "Doctor necunoscut"}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        <div style={{ display: "flex", justifyContent: "flex-end" }}>
          <Pagination
            count={totalPages}
            page={currentPage}
            onChange={(event, value) => setCurrentPage(value)}
            color="primary"
            sx={(theme) => ({
              "& .MuiPaginationItem-root": {
                color: "white",
                backgroundColor: theme.palette.primary.dark,
                borderRadius: "8px",
                marginX: "2px",
              },
              "& .Mui-selected": {
                backgroundColor: `${theme.palette.primary.main} !important`,
                color: "white !important",
                fontWeight: "bold",
              },
            })}
          />
        </div>
      </div>
    </div>
  );
}

export default AppointmentPatientPage;

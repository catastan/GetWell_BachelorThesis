import { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import DeleteIcon from "@mui/icons-material/Delete";
import IconButton from "@mui/material/IconButton";
import wallpaper from "../assets/wallpaper.png";
import ManagePatients from "../components/ManagePatientDialog";
import CONSTANTS from "../constants/Constants";
import {
  Table,
  TableHead,
  TableBody,
  TableCell,
  TableRow,
  Paper,
  TableContainer,
  Typography,
  Button,
  TextField,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Pagination,
  Box,
} from "@mui/material";

function AppointmentActionsPage() {
  useEffect(() => {
    document.body.style.margin = "0";
    document.body.style.overflow = "hidden";
  }, []);

  const location = useLocation();
  const [appointments, setAppointments] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [appointmentToDelete, setAppointmentToDelete] = useState(null);
  const appointmentsPerPage = 8;

  const [showAddBox, setShowAddBox] = useState(false);
  const [newDate, setNewDate] = useState("");
  const [newTime, setNewTime] = useState("");
  const [newCnp, setNewCnp] = useState("");
  const [newDuration, setNewDuration] = useState(0);
  const [selectedPatientCnp, setSelectedPatientCnp] = useState(null);
  const [showManagePatients, setShowManagePatients] = useState(false);

  const filteredAppointments = appointments.filter((appointment) =>
    appointment.patient_cnp.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const indexOfLastAppointment = currentPage * appointmentsPerPage;
  const indexOfFirstAppointment = indexOfLastAppointment - appointmentsPerPage;
  const currentAppointments = filteredAppointments.slice(
    indexOfFirstAppointment,
    indexOfLastAppointment
  );
  const totalPages = Math.ceil(
    filteredAppointments.length / appointmentsPerPage
  );

  //   function getAllApointments() {
  //   return fetch(CONSTANTS.backendUrl + "/api/appointment/v1/getAppointments", {
  //     method: "GET",
  //     headers: {
  //       Authorization: "Bearer " + localStorage.getItem("accessToken"),
  //     },
  //   }).then(async (response) => {
  //     const message = await response.json();
  //     if (response.status === 200) {
  //       return [true, message];
  //     } else {
  //       alert(response.status + ":" + message);
  //       return [false, ""];
  //     }
  //   });
  // }

  async function getAllAppointments() {
    try {
      const response = await fetch(
        CONSTANTS.backendUrl + "/api/appointment/v1/getAppointments",
        {
          method: "GET",
          headers: {
            Authorization: "Bearer " + localStorage.getItem("accessToken"),
          },
        }
      );

      const message = await response.json();

      if (response.status === 200) {
        return [true, message];
      } else {
        alert(response.status + ": " + JSON.stringify(message));
        return [false, []];
      }
    } catch (error) {
      console.error("Fetch error:", error);
      return [false, []];
    }
  }

  // useEffect(() => {
  //   getAllApointments().then(([success, data]) => {
  //     if (success) setAppointments(data);
  //     else console.error("Eroare la încărcarea programărilor.");
  //   });
  // }, []);

  useEffect(() => {
    async function fetchAppointments() {
      const [success, data] = await getAllAppointments();
      if (success) {
        setAppointments(data);
      } else {
        console.error("Eroare la încărcarea programărilor.");
      }
    }

    fetchAppointments();
  }, []);
  const onDelete = (appointment) => {
    const date = new Date(appointment.date);
    const payload = {
      year: date.getFullYear(),
      month: date.getMonth() + 1,
      day: date.getDate(),
      hour: date.getHours(),
      minute: date.getMinutes(),
      id: appointment.app_id,
    };

    fetch(CONSTANTS.backendUrl + "/api/appointment/v1/deleteAppointment", {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + localStorage.getItem("accessToken"),
      },
      body: JSON.stringify(payload),
    }).then(async (response) => {
      const message = await response.text();
      if (response.status === 200) {
        const resp = await getAllAppointments();
        if (resp[0]) setAppointments(resp[1]);
      } else {
        alert("Eroare: " + message + " code: " + response.status);
      }
    });
  };

  const handleAddAppointment = () => {
    if (!showAddBox) {
      setShowAddBox(true);
      return;
    }

    if (!newDate || !newTime || !newCnp || newDuration === 0) {
      alert("Completează toate câmpurile!");
      return;
    }

    const localDateTime = `${newDate}T${newTime}`;

    fetch(CONSTANTS.backendUrl + "/api/appointment/v1/addAppointment", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + localStorage.getItem("accessToken"),
      },
      body: JSON.stringify({
        date: localDateTime,
        cnp: newCnp,
        duration: newDuration,
      }),
    }).then(async (response) => {
      const message = await response.text();
      if (response.status === 201) {
        const resp = await getAllAppointments();
        if (resp[0]) setAppointments(resp[1]);
        setShowAddBox(false);
        setNewDate("");
        setNewTime("");
        setNewCnp("");
        setNewDuration(0);
      } else {
        alert("Eroare: " + message + " code: " + response.status);
      }
    });
  };

  return (
    <div
      style={{
        backgroundImage: `url(${wallpaper})`,
        backgroundSize: "cover",
        minHeight: "100vh",
        padding: "40px",
        fontFamily: "'Segoe UI', sans-serif",
      }}
    >
      <Box
        sx={{
          maxWidth: "1000px",
          margin: "0 auto",
          display: "flex",
          flexDirection: "column",
          gap: 3,
        }}
      >
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            flexWrap: "wrap",
            gap: "10px",
          }}
        >
          <Typography
            variant="h4"
            color="primary"
            sx={{ flexGrow: 1, minWidth: "200px" }}
          >
            Lista Programărilor
          </Typography>

          <TextField
            variant="outlined"
            label="Caută după CNP"
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              setCurrentPage(1);
            }}
            sx={{
              backgroundColor: "white",
              borderRadius: 1,
              width: "250px",
              height: "40px",
              "& .MuiInputBase-root": {
                height: "40px",
              },
              "& .MuiInputLabel-root": {
                top: "-5px",
              },
            }}
          />

          <Button
            variant="contained"
            onClick={handleAddAppointment}
            sx={{ color: "white" }}
          >
            Adaugă Programare
          </Button>
        </Box>

        <TableContainer component={Paper} sx={{ borderRadius: 3 }}>
          <Table>
            <TableHead
              sx={(theme) => ({
                backgroundColor: theme.palette.primary.main,
              })}
            >
              <TableRow>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  ID
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Data
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Durată (min)
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  CNP Pacient
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Acțiuni
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {currentAppointments.map((appointment, index) => (
                <TableRow key={index}>
                  <TableCell>{appointment.app_id}</TableCell>
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
                    <Button
                      variant="outlined"
                      color="primary"
                      size="small"
                      onClick={() => {
                        setSelectedPatientCnp(appointment.patient_cnp);
                        setShowManagePatients(true);
                      }}
                    >
                      {appointment.patient_cnp}
                    </Button>
                  </TableCell>

                  <TableCell>
                    <IconButton
                      color="error"
                      size="small"
                      disableRipple
                      sx={{ padding: 0, margin: 0 }}
                      onClick={() => {
                        setAppointmentToDelete(appointment);
                        setShowDeleteModal(true);
                      }}
                    >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
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
        </Box>
      </Box>

      <Dialog open={showDeleteModal} onClose={() => setShowDeleteModal(false)}>
        <DialogTitle>Confirmare Ștergere</DialogTitle>
        <DialogContent>
          Ești sigur că vrei să ștergi această programare?
        </DialogContent>
        <DialogActions>
          <Button
            color="error"
            onClick={() => {
              onDelete(appointmentToDelete);
              setShowDeleteModal(false);
            }}
          >
            Confirmă
          </Button>
          <Button onClick={() => setShowDeleteModal(false)}>Anulează</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={showAddBox} onClose={() => setShowAddBox(false)}>
        <DialogTitle>Adaugă Programare</DialogTitle>
        <DialogContent
          sx={{ display: "flex", flexDirection: "column", gap: 1.2, mt: 1 }}
        >
          <TextField
            type="date"
            variant="outlined"
            fullWidth
            InputLabelProps={{ shrink: true }}
            value={newDate}
            onChange={(e) => setNewDate(e.target.value)}
          />

          <TextField
            label="Ora"
            type="time"
            InputLabelProps={{ shrink: true }}
            value={newTime}
            onChange={(e) => setNewTime(e.target.value)}
          />
          <TextField
            label="CNP Pacient"
            value={newCnp}
            onChange={(e) => setNewCnp(e.target.value)}
          />
          <TextField
            label="Durată (minute)"
            type="number"
            variant="outlined"
            fullWidth
            value={newDuration}
            onChange={(e) => setNewDuration(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button
            onClick={handleAddAppointment}
            variant="contained"
            sx={{ color: "white" }}
          >
            Salvează
          </Button>
          <Button onClick={() => setShowAddBox(false)}>Anulează</Button>
        </DialogActions>
      </Dialog>

      <ManagePatients
        open={showManagePatients}
        onClose={() => setShowManagePatients(false)}
        cnp={selectedPatientCnp}
      />
    </div>
  );
}

export default AppointmentActionsPage;

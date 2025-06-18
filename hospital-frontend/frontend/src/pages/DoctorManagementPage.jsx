import { useEffect, useState } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Pagination,
  IconButton,
} from "@mui/material";
import wallpaper from "../assets/wallpaper.png";
import DeleteIcon from "@mui/icons-material/Delete";
import CONSTANTS from "../constants/Constants";

function DoctorManagementPage() {
  const [doctors, setDoctors] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [formData, setFormData] = useState({
    email: "",
    username: "",
    password: "",
    firstName: "",
    lastName: "",
    phoneNumber: "",
  });

  const doctorsPerPage = 8;
  const handleDeleteDoctor = (doctorId) => {
    if (!window.confirm("Sigur vrei să ștergi acest doctor?")) return;

    fetch(CONSTANTS.backendUrl + `/api/auth/v1/doctors/${doctorId}`, {
      method: "DELETE",
      headers: {
        Authorization: "Bearer " + localStorage.getItem("accessToken"),
      },
    })
      .then(async (res) => {
        const msg = await res.text();
        if (res.ok) {
          fetchDoctors();
        } else {
          alert("Eroare la ștergere: " + msg);
        }
      })
      .catch((err) => alert("Eroare: " + err.message));
  };

  useEffect(() => {
    document.body.style.margin = "0";
    document.body.style.overflow = "hidden";
    fetchDoctors();
  }, []);

  const fetchDoctors = () => {
    fetch(CONSTANTS.backendUrl + "/api/auth/v1/doctors", {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("accessToken"),
      },
    })
      .then((res) => res.json())
      .then(setDoctors)
      .catch((err) => console.error("Eroare la încărcarea doctorilor:", err));
  };

  const filteredDoctors = doctors.filter((doc) =>
    (doc.username || "").toLowerCase().includes(searchTerm.toLowerCase())
  );

  const indexOfLast = currentPage * doctorsPerPage;
  const indexOfFirst = indexOfLast - doctorsPerPage;
  const currentDoctors = filteredDoctors.slice(indexOfFirst, indexOfLast);
  const totalPages = Math.ceil(filteredDoctors.length / doctorsPerPage);

  const handleAddDoctor = () => {
    const { email, username, password, firstName, lastName, phoneNumber } =
      formData;

    if (!email || !username || !password) {
      alert("Completează toate câmpurile obligatorii!");
      return;
    }

    fetch(CONSTANTS.backendUrl + "/api/auth/v1/doctor/signup", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + localStorage.getItem("accessToken"),
      },
      body: JSON.stringify(formData),
    })
      .then(async (res) => {
        const msg = await res.text();
        if (res.status === 201) {
          fetchDoctors();
          setShowAddDialog(false);
          setFormData({
            email: "",
            username: "",
            password: "",
            firstName: "",
            lastName: "",
            phoneNumber: "",
          });
        } else {
          alert("Eroare: " + msg);
        }
      })
      .catch((err) => alert("Eroare: " + err.message));
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
            Lista medicilor
          </Typography>

          <TextField
            variant="outlined"
            label="Caută după username"
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
              "& .MuiInputBase-root": { height: "40px" },
              "& .MuiInputLabel-root": { top: "-5px" },
            }}
          />

          <Button
            variant="contained"
            sx={{ color: "white" }}
            onClick={() => setShowAddDialog(true)}
          >
            Adaugă Medic
          </Button>
        </Box>

        <TableContainer component={Paper} sx={{ borderRadius: 3 }}>
          <Table>
            <TableHead
              sx={(theme) => ({ backgroundColor: theme.palette.primary.main })}
            >
              <TableRow>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Username
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Email
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Prenume
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Nume
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Telefon
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Acțiuni
                </TableCell>
                <TableCell></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {currentDoctors.map((doc, index) => (
                <TableRow key={index}>
                  <TableCell>{doc.username}</TableCell>
                  <TableCell>{doc.email}</TableCell>
                  <TableCell>{doc.firstName}</TableCell>
                  <TableCell>{doc.lastName}</TableCell>
                  <TableCell>{doc.phoneNumber}</TableCell>
                  <TableCell>
                    <IconButton
                      onClick={() => handleDeleteDoctor(doc.id)}
                      sx={{ color: "gray" }}
                      aria-label="Șterge"
                    >
                      <DeleteIcon />
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
            onChange={(e, val) => setCurrentPage(val)}
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

      <Dialog open={showAddDialog} onClose={() => setShowAddDialog(false)}>
        <DialogTitle>Adaugă medic</DialogTitle>
        <DialogContent
          sx={{ display: "flex", flexDirection: "column", gap: 1.2, mt: 1 }}
        >
          <TextField
            label="Email"
            value={formData.email}
            onChange={(e) =>
              setFormData({ ...formData, email: e.target.value })
            }
          />
          <TextField
            label="Username"
            value={formData.username}
            onChange={(e) =>
              setFormData({ ...formData, username: e.target.value })
            }
          />
          <TextField
            label="Parolă"
            type="password"
            value={formData.password}
            onChange={(e) =>
              setFormData({ ...formData, password: e.target.value })
            }
          />
          <TextField
            label="Prenume"
            value={formData.firstName}
            onChange={(e) =>
              setFormData({ ...formData, firstName: e.target.value })
            }
          />
          <TextField
            label="Nume"
            value={formData.lastName}
            onChange={(e) =>
              setFormData({ ...formData, lastName: e.target.value })
            }
          />
          <TextField
            label="Telefon"
            value={formData.phoneNumber}
            onChange={(e) =>
              setFormData({ ...formData, phoneNumber: e.target.value })
            }
          />
        </DialogContent>
        <DialogActions>
          <Button
            onClick={handleAddDoctor}
            variant="contained"
            sx={{ color: "white" }}
          >
            Salvează
          </Button>
          <Button onClick={() => setShowAddDialog(false)}>Anulează</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default DoctorManagementPage;

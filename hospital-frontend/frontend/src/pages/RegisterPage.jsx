import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  TextField,
  Button,
  Typography,
  Paper,
  useTheme,
} from "@mui/material";
import wallpaper from "../assets/wallpaper.png";

function RegisterPage() {
  const theme = useTheme();
  const navigate = useNavigate();

  useEffect(() => {
    document.body.style.margin = "0";
    document.body.style.overflow = "hidden";
  }, []);

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    password: "",
    phoneNumber: "",
    cnp: "",
  });

  const [errors, setErrors] = useState({});

  const handleRegister = () => {
    const newErrors = {};

    if (!/^\d{13}$/.test(form.cnp)) {
      newErrors.cnp = "CNP trebuie să conțină exact 13 cifre";
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      newErrors.email = "Email-ul nu este în format valid";
    }

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      fetch("http://localhost:8081/api/auth/v1/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(form),
      })
        .then(async (response) => {
          const message = await response.text();
          if (response.status === 201) {
            alert(message);
            setTimeout(() => {
              navigate("/");
            }, 2000);
          } else {
            alert(message);
          }
        })
        .catch((error) => {
          console.error("Error:", error);
        });
    }
  };

  return (
    <Box
      sx={{
        backgroundImage: `url(${wallpaper})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        height: "100vh",
        width: "100vw",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Paper
        elevation={6}
        sx={{
          padding: 4,
          borderRadius: 3,
          backgroundColor: "rgba(255, 255, 255, 0.95)",
          minWidth: 360,
          width: "90%",
          maxWidth: 480,
        }}
      >
        <Typography
          variant="h5"
          color="primary"
          fontWeight="bold"
          gutterBottom
          align="center"
        >
          Înregistrare utilizator
        </Typography>
        <Box display="flex" flexDirection="column" gap={2}>
          <TextField
            label="Prenume"
            variant="outlined"
            value={form.firstName}
            onChange={(e) => setForm({ ...form, firstName: e.target.value })}
            fullWidth
          />
          <TextField
            label="Nume"
            variant="outlined"
            value={form.lastName}
            onChange={(e) => setForm({ ...form, lastName: e.target.value })}
            fullWidth
          />
          <TextField
            label="Username"
            variant="outlined"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            fullWidth
          />
          <TextField
            label="Email"
            variant="outlined"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            error={Boolean(errors.email)}
            helperText={errors.email}
            fullWidth
          />
          <TextField
            label="Parolă"
            type="password"
            variant="outlined"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            fullWidth
          />
          <TextField
            label="Telefon"
            variant="outlined"
            value={form.phoneNumber}
            onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })}
            fullWidth
          />
          <TextField
            label="CNP"
            variant="outlined"
            value={form.cnp}
            onChange={(e) => setForm({ ...form, cnp: e.target.value })}
            error={Boolean(errors.cnp)}
            helperText={errors.cnp}
            fullWidth
          />
          <Button
            onClick={handleRegister}
            variant="contained"
            color="primary"
            fullWidth
            sx={{
              mt: 2,
              color: "#fff",
              fontWeight: "bold",
              borderRadius: "30px",
            }}
          >
            Înregistrează-te
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}

export default RegisterPage;

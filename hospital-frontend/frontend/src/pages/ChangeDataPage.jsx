import { useNavigate } from "react-router-dom";
import { useState } from "react";
import wallpaper from "../assets/wallpaper.png";
import { TextField, Typography, Button, Paper } from "@mui/material";
import { useEffect } from "react";
import CONSTANTS from "../constants/Constants";
function ChangeDataPage() {
  useEffect(() => {
    document.body.style.margin = "0";
    document.body.style.overflow = "hidden";
  }, []);

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    phoneNumber: "",
    username: "",
    email: "",
  });

  const [errors, setErrors] = useState({});
  const navigate = useNavigate();

  const handleChangeData = () => {
    const newErrors = {};
    if (form.email !== "" && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      newErrors.email = "Email-ul nu este în format valid";
    }

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      fetch(CONSTANTS.backendUrl + "/api/auth/v1/changeData", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("accessToken"),
        },
        body: JSON.stringify(form),
      })
        .then(async (response) => {
          const message = await response.text();
          if (response.status === 200) {
            if (form.email !== "") {
              localStorage.setItem("email", form.email);
              // await loginFetch(
              //   localStorage.getItem("email"),
              //   localStorage.getItem("password")
              // );
            }
            alert(message);
            navigate(-1);
          } else {
            alert(message);
          }
        })
        .catch((error) => console.error("Error:", error));
    }
  };

  return (
    <div
      style={{
        backgroundImage: `url(${wallpaper})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        fontFamily: "'Segoe UI', sans-serif",
      }}
    >
      <Paper
        elevation={3}
        sx={{
          padding: 4,
          borderRadius: 4,
          minWidth: 350,
          display: "flex",
          flexDirection: "column",
          gap: 2,
        }}
      >
        <Typography
          variant="h5"
          color="primary"
          sx={{ textAlign: "center", mb: 1 }}
        >
          Modifică Datele
        </Typography>

        <TextField
          label="Prenume"
          variant="outlined"
          fullWidth
          value={form.firstName}
          onChange={(e) => setForm({ ...form, firstName: e.target.value })}
        />
        <TextField
          label="Nume"
          variant="outlined"
          fullWidth
          value={form.lastName}
          onChange={(e) => setForm({ ...form, lastName: e.target.value })}
        />
        <TextField
          label="Username"
          variant="outlined"
          fullWidth
          value={form.username}
          onChange={(e) => setForm({ ...form, username: e.target.value })}
        />
        <TextField
          label="Email"
          variant="outlined"
          fullWidth
          error={!!errors.email}
          helperText={errors.email}
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
        />
        <TextField
          label="Telefon"
          variant="outlined"
          fullWidth
          value={form.phoneNumber}
          onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })}
        />

        <Button
          variant="contained"
          fullWidth
          sx={{ color: "white", mt: 1 }}
          onClick={handleChangeData}
        >
          Salvează Modificările
        </Button>
      </Paper>
    </div>
  );
}

export default ChangeDataPage;

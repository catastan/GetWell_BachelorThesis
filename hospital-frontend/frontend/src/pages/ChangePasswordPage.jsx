import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { loginFetch } from "../api/auth";
import wallpaper from "../assets/wallpaper.png";
import { TextField, Typography, Button, Paper } from "@mui/material";
import CONSTANTS from "../constants/Constants";

function ChangePassword() {
  useEffect(() => {
    document.body.style.margin = "0";
    document.body.style.overflow = "hidden";
  }, []);

  const [form, setForm] = useState({
    oldPassword: "",
    newPassword: "",
  });

  const [errors, setErrors] = useState({});
  const navigate = useNavigate();

  const handleChangePassword = () => {
    const newErrors = {};
    if (form.oldPassword === form.newPassword) {
      newErrors.password = "Parola nouă trebuie să fie diferită de cea veche.";
    }

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      fetch(CONSTANTS.backendUrl + "/api/auth/v1/changePass", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("accessToken"),
        },
        body: JSON.stringify(form),
      })
        .then(async (response) => {
          const message = await response.text();
          if (response.status < 200 || response.status > 299) {
            alert("Eroare: " + message);
          } else {
            alert("Parola a fost schimbată cu succes.");
            localStorage.setItem("password", form.newPassword);
            const resp_page = await loginFetch(
              localStorage.getItem("email"),
              localStorage.getItem("password")
            );
            if (resp_page[0] === true) {
              navigate(-1);
            }
          }
        })
        .catch((error) => {
          console.error("Error:", error);
        });
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
          Schimbare Parolă
        </Typography>

        <TextField
          label="Parolă veche"
          type="password"
          variant="outlined"
          fullWidth
          value={form.oldPassword}
          onChange={(e) => setForm({ ...form, oldPassword: e.target.value })}
        />
        <TextField
          label="Parolă nouă"
          type="password"
          variant="outlined"
          fullWidth
          error={!!errors.password}
          helperText={errors.password}
          value={form.newPassword}
          onChange={(e) => setForm({ ...form, newPassword: e.target.value })}
        />

        <Button
          variant="contained"
          fullWidth
          sx={{ color: "white", mt: 1 }}
          onClick={handleChangePassword}
        >
          Salvează Parola
        </Button>
      </Paper>
    </div>
  );
}

export default ChangePassword;

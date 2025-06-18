import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { loginFetch } from "../api/auth";
import wallpaper from "../assets/wallpaper.png";
import { TextField, Typography, Button, Paper } from "@mui/material";
import { useEffect } from "react";
function LoginPage() {
  useEffect(() => {
    document.body.style.margin = "0";
    document.body.style.overflow = "hidden";
  }, []);

  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const [errors, setErrors] = useState({});
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogin = async () => {
    const newErrors = {};
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      newErrors.email = "Email-ul nu este în format valid";
    }

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      const result = await loginFetch(form.email, form.password);
      if (result.success) {
        await login(form.email, form.password, result.token);
        navigate("/");
      } else {
        alert("Autentificare eșuată. Verifică emailul și parola.");
      }
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
          Autentificare
        </Typography>

        <TextField
          label="Email"
          type="email"
          variant="outlined"
          fullWidth
          error={!!errors.email}
          helperText={errors.email}
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
        />

        <TextField
          label="Parolă"
          type="password"
          variant="outlined"
          fullWidth
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
        />

        <Button
          variant="contained"
          fullWidth
          sx={{ color: "white", mt: 1 }}
          onClick={handleLogin}
        >
          Login
        </Button>
      </Paper>
    </div>
  );
}

export default LoginPage;

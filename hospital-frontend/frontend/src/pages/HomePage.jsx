import React, { useContext } from "react";
import { Box, Typography, Button } from "@mui/material";
import { Link as RouterLink } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import logo from "../assets/logo.png";
import wallpaper from "../assets/wallpaper.png";
import CONSTANTS from "../constants/Constants";
export default function HomePage() {
  const { auth } = useContext(AuthContext);

  const isLoggedIn = !!auth?.role;

  return (
    <Box
      sx={{
        minHeight: "calc(100vh - 80px)",
        backgroundImage: `url(${wallpaper})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundRepeat: "no-repeat",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        padding: 4,
        textAlign: "center",
      }}
    >
      <Box
        sx={{
          mb: 4,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: 2,
        }}
      >
        <img src={logo} alt="Logo GetWell" style={{ height: 100 }} />

        <Typography variant="h3" color="primary" fontWeight="bold">
          GetWell
        </Typography>

        <Typography variant="h6" color="custom.defaultText" maxWidth="600px">
          Sănătatea ta este prioritatea noastră!
        </Typography>
      </Box>

      {!isLoggedIn && (
        <Box
          sx={{
            display: "flex",
            gap: 2,
            flexWrap: "wrap",
            justifyContent: "center",
          }}
        >
          <Button
            variant="contained"
            color="primary"
            size="large"
            component={RouterLink}
            to="/login"
          >
            Intră în cont
          </Button>
          <Button
            variant="outlined"
            color="primary"
            size="large"
            component={RouterLink}
            to="/register"
          >
            Creează cont nou
          </Button>
        </Box>
      )}
    </Box>
  );
}

import React, { useContext, useState } from "react";
import { AuthContext } from "../context/AuthContext";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import {
  Box,
  Button,
  Typography,
  Menu,
  MenuItem,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import logo from "../assets/logo.png";
import Cookies from "js-cookie";

export default function Navbar() {
  const { auth, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const theme = useTheme();
  const [logoutDialogOpen, setLogoutDialogOpen] = useState(false);

  const [anchorEl, setAnchorEl] = useState(null);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [recipeDialogOpen, setRecipeDialogOpen] = useState(false);
  const [recipeList, setRecipeList] = useState([]);

  const openProfileMenu = (event) => setAnchorEl(event.currentTarget);
  const handleCloseProfileMenu = () => setAnchorEl(null);

  const handleChangeData = () => {
    handleCloseProfileMenu();
    navigate("/changeData");
  };

  const handleChangePassword = () => {
    handleCloseProfileMenu();
    navigate("/changePassword");
  };

  const handleDelete = () => {
    handleCloseProfileMenu();
    setOpenDeleteDialog(true);
  };

  const confirmDelete = () => {
    fetch("http://localhost:8081/api/auth/v1/delete", {
      method: "DELETE",
      headers: {
        Authorization: "Bearer " + localStorage.getItem("accessToken"),
      },
    }).then(async (response) => {
      if (response.status === 401) {
        alert("Token invalid. Încearcă să te autentifici din nou.");
      } else if (response.status === 404) {
        alert("Utilizatorul nu a fost găsit în baza de date.");
      } else if (response.status === 200) {
        localStorage.clear();
        Cookies.remove("accessToken");
        logout();
        navigate("/");
      }
      setOpenDeleteDialog(false);
    });
  };

  const cancelDelete = () => {
    setOpenDeleteDialog(false);
  };

  const handleLogoutClick = () => {
    setLogoutDialogOpen(true);
  };
  const confirmLogout = () => {
    setLogoutDialogOpen(false);
    logout();
    navigate("/");
  };

  const cancelLogout = () => {
    setLogoutDialogOpen(false);
  };

  const handleFetchMyRecipe = () => {
    const token = localStorage.getItem("accessToken");
    fetch("http://localhost:8081/api/recipe/v1/getMyRecipe", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Rețeta nu a fost găsită.");
        return res.json();
      })
      .then((data) => {
        const parsed = JSON.parse(data.content);
        setRecipeList(parsed);
        setRecipeDialogOpen(true);
      })
      .catch((err) => {
        console.error("Eroare la obținerea rețetei:", err);
        alert("Nu există rețetă disponibilă pentru acest pacient.");
      });
  };

  const getNavItemsByRole = (role) => {
    if (!role) {
      return [
        { name: "Login", path: "/login" },
        { name: "Register", path: "/register" },
      ];
    }

    switch (role) {
      case "ROLE_ADMIN":
        return [
          { name: "Farmacie", path: "/medication" },
          { name: "Medici", path: "/admin/doctors" },
          {
            profile: [
              { name: "Modifică datele", action: handleChangeData },
              { name: "Schimbă parola", action: handleChangePassword },
              { name: "Șterge cont", action: handleDelete },
            ],
          },
          { name: "Logout", path: "/" },
        ];
      case "ROLE_DOCTOR":
        return [
          { name: "Programări", path: "/doctor/app" },
          { name: "Farmacie", path: "/doctor/med" },
          {
            profile: [
              { name: "Modifică datele", action: handleChangeData },
              { name: "Schimbă parola", action: handleChangePassword },
              { name: "Șterge cont", action: handleDelete },
            ],
          },
          { name: "Logout", path: "/" },
        ];
      case "ROLE_PATIENT":
        return [
          { name: "Programări", path: "/patient/app" },
          { name: "Vizualizează Rețeta", action: handleFetchMyRecipe },
          {
            profile: [
              { name: "Modifică datele", action: handleChangeData },
              { name: "Schimbă parola", action: handleChangePassword },
              { name: "Șterge cont", action: handleDelete },
            ],
          },
          { name: "Logout", path: "/" },
        ];
      default:
        return [];
    }
  };

  const navItems = getNavItemsByRole(auth.role);

  return (
    <Box
      component="nav"
      sx={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        padding: "16px 32px",
        backgroundColor: "#fff",
        boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
        zIndex: 10,
        position: "relative",
      }}
    >
      <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
        <Button component={RouterLink} to="/" sx={{ padding: 0, minWidth: 0 }}>
          <img src={logo} alt="Logo GetWell" style={{ height: "40px" }} />
        </Button>
        <Typography variant="h6" color="primary" fontWeight="bold">
          GetWell
        </Typography>
      </Box>

      <Box sx={{ display: "flex", gap: 2 }}>
        {navItems.map((item, index) =>
          item.name === "Vizualizează Rețeta" ? (
            <Button
              key={item.name}
              variant="text"
              color="custom.defaultText"
              onClick={item.action}
            >
              {item.name}
            </Button>
          ) : item.name === "Logout" ? (
            <Button
              key={item.name}
              variant="text"
              color="custom.defaultText"
              onClick={handleLogoutClick}
            >
              Logout
            </Button>
          ) : item.profile ? (
            <Box key="profile">
              <Button
                variant="text"
                color="custom.defaultText"
                onClick={openProfileMenu}
              >
                Profil
              </Button>
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleCloseProfileMenu}
              >
                {item.profile.map((profileItem) => (
                  <MenuItem key={profileItem.name} onClick={profileItem.action}>
                    {profileItem.name}
                  </MenuItem>
                ))}
              </Menu>
            </Box>
          ) : (
            <Button
              key={item.name || index}
              variant="text"
              component={RouterLink}
              to={item.path}
              color="custom.defaultText"
            >
              {item.name}
            </Button>
          )
        )}
      </Box>

      <Dialog open={openDeleteDialog} onClose={cancelDelete}>
        <DialogTitle>Confirmare ștergere cont</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Ești sigur că vrei să ștergi acest cont? Această acțiune este
            ireversibilă.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelDelete} color="primary">
            Anulează
          </Button>
          <Button onClick={confirmDelete} color="error" variant="contained">
            Șterge contul
          </Button>
        </DialogActions>
      </Dialog>
      <Dialog
        open={recipeDialogOpen}
        onClose={() => setRecipeDialogOpen(false)}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>Rețetă Medicală</DialogTitle>
        <DialogContent dividers>
          {recipeList.length > 0 ? (
            recipeList.map((item, idx) => (
              <Typography key={idx}>
                - {item.medicationName}: {item.requiredQuantity} buc.
              </Typography>
            ))
          ) : (
            <Typography color="text.secondary">
              Nu există rețetă salvată.
            </Typography>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRecipeDialogOpen(false)} color="primary">
            Închide
          </Button>
        </DialogActions>
      </Dialog>
      <Dialog open={logoutDialogOpen} onClose={cancelLogout}>
        <DialogTitle>Confirmare deconectare</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Ești sigur că vrei să te deconectezi?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelLogout} color="primary">
            Anulează
          </Button>
          <Button
            onClick={confirmLogout}
            color="primary"
            variant="contained"
            sx={{ color: "white" }}
          >
            Deconectează-te
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

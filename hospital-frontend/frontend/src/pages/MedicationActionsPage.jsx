import { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Paper,
  TextField,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Pagination,
} from "@mui/material";
import { FiSearch } from "react-icons/fi";
import wallpaper from "../assets/wallpaper.png";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import DeleteIcon from "@mui/icons-material/Delete";

import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText,
} from "@mui/material";
import { useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import Checkbox from "@mui/material/Checkbox";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import CONSTANTS from "../constants/Constants";
function MedicationActionsPage() {
  useEffect(() => {
    document.body.style.margin = "0";
    document.body.style.overflow = "hidden";
  }, []);

  const [form, setForm] = useState({ medicationName: "", category: "" });
  const [medications, setMedications] = useState([]);
  const [showMed, setShowMed] = useState(false);
  const [catMed, setCatMed] = useState([]);
  const [quantities, setQuantities] = useState({});
  const [medsCurrentPage, setMedsCurrentPage] = useState(1);
  const medsPerPage = 5;
  const indexOfLastMed = medsCurrentPage * medsPerPage;
  const indexOfFirstMed = indexOfLastMed - medsPerPage;
  const [searchTerm, setSearchTerm] = useState("");
  const [showAddForm, setShowAddForm] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);
  const { auth } = useContext(AuthContext);
  const isAdmin = auth?.role === "ROLE_ADMIN";
  const isDoctor = auth?.role === "ROLE_DOCTOR";
  const [selectedMedications, setSelectedMedications] = useState([]);
  const [showRecipeDialog, setShowRecipeDialog] = useState(false);
  const [cnpInput, setCnpInput] = useState("");
  const [recipeQuantities, setRecipeQuantities] = useState({});
  const [newMed, setNewMed] = useState({
    name: "",
    category: "",
    startStock: "",
  });

  const filteredMeds = medications.filter((med) =>
    med.name.toLowerCase().includes(searchTerm.toLowerCase())
  );
  const handleRemoveMedication = (id) => {
    setSelectedMedications((prev) => prev.filter((m) => m.id !== id));
    setRecipeQuantities((prev) => {
      const updated = { ...prev };
      delete updated[id];
      return updated;
    });
  };
  const handleClearSelection = () => {
    setSelectedMedications([]);
    setRecipeQuantities({});
    setShowRecipeDialog(false);
  };

  const handleSelectMedication = (med) => {
    const alreadySelected = selectedMedications.find((m) => m.id === med.id);
    if (alreadySelected) {
      setSelectedMedications((prev) => prev.filter((m) => m.id !== med.id));
      setRecipeQuantities((prev) => {
        const updated = { ...prev };
        delete updated[med.id];
        return updated;
      });
    } else {
      setSelectedMedications((prev) => [...prev, med]);
      setRecipeQuantities((prev) => ({
        ...prev,
        [med.id]: 0,
      }));
    }
  };

  const currentMedications = filteredMeds.slice(
    indexOfFirstMed,
    indexOfLastMed
  );
  const medsTotalPages = Math.ceil(filteredMeds.length / medsPerPage);

  useEffect(() => {
    fetch(CONSTANTS.backendUrl + "/api/medication/v1/listAllCategories", {
      headers: {
        Authorization: "Bearer " + localStorage.getItem("accessToken"),
      },
    })
      .then((res) => res.json())
      .then((data) => setCatMed(data))
      .catch(() => setCatMed([]));
  }, []);

  const handleCategoryClick = (category) => {
    setForm({ ...form, category });
    fetch(
      CONSTANTS.backendUrl +
        `/api/medication/v1/getMedicationsByCategory?category=${category}`,
      {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("accessToken"),
        },
      }
    )
      .then((res) => res.json())
      .then((data) => {
        setMedications(data);
        setShowMed(true);
      });
  };

  const refetchMedications = () => {
    if (form.medicationName) {
      handleSearchByName();
    } else if (form.category) {
      handleCategoryClick(form.category);
    }
  };

  const handleSearchByName = () => {
    const name = form.medicationName.trim();
    if (!name) return;
    fetch(
      CONSTANTS.backendUrl +
        `/api/medication/v1/getMedication?medicationName=${name}`,
      {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("accessToken"),
        },
      }
    )
      .then((res) => res.json())
      .then((data) => {
        if (data.length > 0) {
          setMedications(data);
          setShowMed(true);
          setMedsCurrentPage(1);
        } else {
          alert("Niciun medicament găsit cu acest nume.");
        }
      });
  };
  const handleDeleteMedication = (medicationId) => {
    if (!window.confirm("Ești sigur că vrei să ștergi acest medicament?"))
      return;

    fetch(
      CONSTANTS.backendUrl +
        `/api/medication/v1/deleteMedication/${medicationId}`,
      {
        method: "DELETE",
        headers: {
          Authorization: "Bearer " + localStorage.getItem("accessToken"),
        },
      }
    )
      .then((res) => {
        if (res.ok) {
          alert("Medicament șters cu succes.");
          refetchMedications();
        } else {
          alert("Eroare la ștergerea medicamentului.");
        }
      })
      .catch(() => alert("Eroare de rețea."));
  };

  const handleAddMedication = () => {
    if (!newMed.name || !newMed.category || !newMed.startStock) {
      alert("Completează toate câmpurile.");
      return;
    }
    fetch(CONSTANTS.backendUrl + "/api/medication/v1/addMedication", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + localStorage.getItem("accessToken"),
      },
      body: JSON.stringify({
        name: newMed.name,
        category: newMed.category,
        startStock: parseInt(newMed.startStock, 10),
      }),
    })
      .then((response) => response.text())
      .then((message) => {
        alert("Medicament adăugat cu succes!");
        setNewMed({ name: "", category: "", startStock: "" });
        setShowAddForm(false);
        refetchMedications();
      });
  };

  const handleUpdateStock = (med) => {
    const parsedQuantity = parseInt(quantities[med.id], 10);
    if (!parsedQuantity || parsedQuantity <= 0) {
      alert("Te rog introdu o cantitate validă.");
      return;
    }
    const body = {
      medicationId: med.id,
      medicationName: med.name,
      stock: parsedQuantity,
    };

    fetch(CONSTANTS.backendUrl + "/api/medication/v1/updateStockMedication", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + localStorage.getItem("accessToken"),
      },
      body: JSON.stringify(body),
    })
      .then((res) => res.text())
      .then((message) => {
        alert("Stock updatat cu succes.");
        setQuantities({ ...quantities, [med.id]: "" });
        refetchMedications();
      });
  };

  return (
    <div
      style={{
        backgroundImage: `url(${wallpaper})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        minHeight: "100vh",
        paddingTop: "80px",
        display: "flex",
        justifyContent: "center",
        alignItems: "flex-start",
        fontFamily: "'Segoe UI', sans-serif",
      }}
    >
      {showMed ? (
        <Box
          sx={{
            position: "relative",
            minWidth: 700,
            backgroundColor: "#fff",
            p: 3,
            borderRadius: 3,
            boxShadow: 3,
          }}
        >
          <Box display="flex" justifyContent="flex-end" mb={1}>
            <IconButton
              aria-label="Închide"
              onClick={() => setShowMed(false)}
              sx={{
                color: "grey.700",
                border: "1px solid",
                borderColor: "grey.400",
                borderRadius: "8px",
                padding: "4px",
                backgroundColor: "white",
                "&:hover": {
                  backgroundColor: "grey.100",
                },
              }}
            >
              <CloseIcon fontSize="small" />
            </IconButton>
          </Box>

          <Box
            display="flex"
            justifyContent="space-between"
            alignItems="flex-start"
            mb={2}
          >
            <Box>
              <Typography
                variant="h6"
                color="primary"
                sx={{ whiteSpace: "nowrap", mb: 1 }}
              >
                Rezultate Medicamente
              </Typography>
              {isDoctor && selectedMedications.length > 0 && (
                <Button
                  variant="contained"
                  color="primary"
                  onClick={() => setShowRecipeDialog(true)}
                  sx={{ color: "white" }}
                >
                  Vizualizează rețeta
                </Button>
              )}
            </Box>

            <Box
              display="flex"
              flexDirection="column"
              alignItems="flex-end"
              gap={1}
            >
              <Box display="flex" alignItems="center" gap={2}>
                <TextField
                  placeholder="Caută după nume..."
                  value={searchTerm}
                  onChange={(e) => {
                    setSearchTerm(e.target.value);
                    setMedsCurrentPage(1);
                  }}
                  size="small"
                />
                {isAdmin && (
                  <Button
                    variant="contained"
                    onClick={() => {
                      setNewMed({
                        name: "",
                        category: form.category,
                        startStock: "",
                      });
                      setOpenDialog(true);
                    }}
                    sx={{ color: "white", whiteSpace: "nowrap" }}
                  >
                    Adaugă
                  </Button>
                )}
              </Box>
            </Box>
          </Box>

          <TableContainer
            component={Paper}
            sx={{
              maxWidth: "900px",
              margin: "0 auto",
              borderRadius: 3,
              overflowX: "auto",
            }}
          >
            <Table sx={{ tableLayout: "fixed", minWidth: 800 }}>
              <TableHead sx={{ backgroundColor: "primary.main" }}>
                <TableRow>
                  <TableCell
                    sx={{ color: "white", fontWeight: "bold", width: "30%" }}
                  >
                    Nume
                  </TableCell>
                  <TableCell
                    sx={{ color: "white", fontWeight: "bold", width: "25%" }}
                  >
                    Categorie
                  </TableCell>
                  <TableCell
                    sx={{ color: "white", fontWeight: "bold", width: "15%" }}
                  >
                    Stoc
                  </TableCell>
                  {isDoctor && (
                    <TableCell
                      sx={{ color: "white", fontWeight: "bold", width: "15%" }}
                    >
                      Selectează
                    </TableCell>
                  )}
                  {isAdmin && (
                    <>
                      <TableCell
                        sx={{
                          color: "white",
                          fontWeight: "bold",
                          width: "15%",
                        }}
                      >
                        Adaugă cantitate
                      </TableCell>
                      <TableCell
                        sx={{
                          color: "white",
                          fontWeight: "bold",
                          width: "15%",
                        }}
                      >
                        Acțiuni
                      </TableCell>
                    </>
                  )}
                </TableRow>
              </TableHead>

              <TableBody>
                {currentMedications.map((med) => (
                  <TableRow key={med.id}>
                    <TableCell>{med.name}</TableCell>
                    <TableCell>{med.category}</TableCell>
                    <TableCell>{med.stock}</TableCell>
                    {isDoctor && (
                      <>
                        <TableCell>
                          <Checkbox
                            color="primary"
                            checked={selectedMedications.some(
                              (m) => m.id === med.id
                            )}
                            onChange={() => handleSelectMedication(med)}
                          />
                        </TableCell>
                      </>
                    )}
                    {isAdmin && (
                      <>
                        <TableCell>
                          <Box display="flex" alignItems="center" gap={1}>
                            <TextField
                              type="number"
                              size="small"
                              value={quantities[med.id] || ""}
                              onChange={(e) =>
                                setQuantities({
                                  ...quantities,
                                  [med.id]: e.target.value,
                                })
                              }
                              sx={{ width: 80 }}
                            />
                            <IconButton
                              color="primary"
                              onClick={() => handleUpdateStock(med)}
                              sx={{
                                border: "1px solid",
                                borderColor: "primary.main",
                                borderRadius: "8px",
                              }}
                            >
                              <Typography fontWeight="bold" fontSize="1.2rem">
                                +
                              </Typography>
                            </IconButton>
                          </Box>
                        </TableCell>
                        <TableCell>
                          <Button
                            variant="outlined"
                            color="error"
                            onClick={() => handleDeleteMedication(med.id)}
                            size="small"
                          >
                            Șterge
                          </Button>
                        </TableCell>
                      </>
                    )}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
            <DialogTitle>Adaugă un nou medicament</DialogTitle>
            <DialogContent>
              <Box mt={1} display="flex" flexDirection="column" gap={2}>
                <TextField
                  label="Nume"
                  value={newMed.name}
                  onChange={(e) =>
                    setNewMed({ ...newMed, name: e.target.value })
                  }
                />
                <TextField
                  label="Categorie"
                  value={newMed.category}
                  onChange={(e) =>
                    setNewMed({ ...newMed, category: e.target.value })
                  }
                />
                <TextField
                  label="Stoc inițial"
                  type="number"
                  inputProps={{ min: 0 }}
                  value={newMed.startStock}
                  onChange={(e) =>
                    setNewMed({ ...newMed, startStock: e.target.value })
                  }
                />
              </Box>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenDialog(false)}>Anulează</Button>
              <Button
                variant="contained"
                onClick={() => {
                  handleAddMedication();
                  setOpenDialog(false);
                }}
                sx={{ color: "white" }}
              >
                Salvează
              </Button>
            </DialogActions>
          </Dialog>

          <Box mt={2} display="flex" justifyContent="flex-end">
            <Pagination
              count={medsTotalPages}
              page={medsCurrentPage}
              onChange={(_, val) => setMedsCurrentPage(val)}
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
      ) : (
        <Box
          sx={{
            backgroundColor: "white",
            p: 3,
            borderRadius: 3,
            boxShadow: 3,
            minWidth: 400,
          }}
        >
          <Box
            display="flex"
            justifyContent="space-between"
            alignItems="center"
            mb={2}
          >
            <Typography variant="h6" color="primary">
              Categorii Medicamente
            </Typography>
          </Box>
          {showAddForm && (
            <Box mb={2} display="flex" flexDirection="column" gap={2}>
              <TextField
                label="Nume"
                value={newMed.name}
                onChange={(e) => setNewMed({ ...newMed, name: e.target.value })}
              />
              <TextField
                label="Categorie"
                value={newMed.category}
                onChange={(e) =>
                  setNewMed({ ...newMed, category: e.target.value })
                }
              />
              <TextField
                label="Stoc inițial"
                type="number"
                value={newMed.startStock}
                onChange={(e) =>
                  setNewMed({ ...newMed, startStock: e.target.value })
                }
              />
              <Button
                variant="contained"
                onClick={handleAddMedication}
                sx={{ color: "white" }}
              >
                Salvează Medicament
              </Button>
            </Box>
          )}
          <Box
            sx={{
              maxHeight: "300px",
              overflowY: "auto",
              mb: 2,
              pr: 1,
            }}
          >
            {isDoctor && selectedMedications.length > 0 && (
              <Box display="flex" justifyContent="center" mt={2}>
                <Button
                  variant="contained"
                  color="primary"
                  onClick={() => setShowRecipeDialog(true)}
                  sx={{ color: "white" }}
                >
                  Vizualizează și creează rețetă
                </Button>
              </Box>
            )}
            {catMed.map((cat) => (
              <Paper
                key={cat}
                elevation={2}
                sx={{
                  p: 2,
                  my: 1,
                  cursor: "pointer",
                  ":hover": { backgroundColor: "#e0f7fa" },
                }}
                onClick={() => handleCategoryClick(cat)}
              >
                {cat}
              </Paper>
            ))}
          </Box>

          <Typography textAlign="center" mt={2} color="primary">
            sau
          </Typography>
          <Box mt={2} display="flex">
            <TextField
              placeholder="Caută medicament..."
              fullWidth
              value={form.medicationName}
              onChange={(e) =>
                setForm({ ...form, medicationName: e.target.value })
              }
              onKeyDown={(e) => e.key === "Enter" && handleSearchByName()}
            />
            <Button
              onClick={handleSearchByName}
              variant="contained"
              sx={{ ml: 1, minWidth: 48, px: 2 }}
            >
              <FiSearch color="white" />
            </Button>
          </Box>
        </Box>
      )}
      <Dialog
        open={showRecipeDialog}
        onClose={() => setShowRecipeDialog(false)}
        fullWidth
        maxWidth="md"
      >
        <DialogTitle>Rețetă pacient</DialogTitle>
        <DialogContent>
          <Box
            display="flex"
            justifyContent="space-between"
            alignItems="center"
            mb={1}
          >
            <Typography gutterBottom variant="subtitle1" fontWeight="bold">
              Medicamente selectate:
            </Typography>
            {selectedMedications.length > 0 && (
              <Button
                variant="outlined"
                color="error"
                size="small"
                onClick={handleClearSelection}
              >
                Șterge tot
              </Button>
            )}
          </Box>

          <Box
            sx={{
              maxHeight: 300,
              overflowY: "auto",
              borderRadius: 1,
              border: "1px solid #ddd",
              px: 1,
            }}
          >
            <List dense>
              {selectedMedications.map((med) => (
                <ListItem
                  key={med.id}
                  secondaryAction={
                    <IconButton
                      edge="end"
                      onClick={() => handleRemoveMedication(med.id)}
                    >
                      <DeleteIcon sx={{ color: "grey.600" }} />
                    </IconButton>
                  }
                >
                  <ListItemText primary={`${med.name} (${med.category})`} />
                  <TextField
                    label="Cantitate"
                    type="number"
                    size="small"
                    inputProps={{ min: 1 }}
                    value={recipeQuantities[med.id]}
                    onChange={(e) =>
                      setRecipeQuantities({
                        ...recipeQuantities,
                        [med.id]: e.target.value,
                      })
                    }
                    sx={{ width: 100, ml: 2 }}
                  />
                </ListItem>
              ))}
            </List>
          </Box>

          <TextField
            label="CNP pacient"
            fullWidth
            margin="dense"
            value={cnpInput}
            onChange={(e) => setCnpInput(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowRecipeDialog(false)}>Anulează</Button>
          <Button
            variant="contained"
            color="primary"
            sx={{ color: "white" }}
            onClick={() => {
              const payload = selectedMedications.map((m) => ({
                medicationId: m.id,
                medicationName: m.name,
                requiredQuantity: parseInt(recipeQuantities[m.id], 10) || 1,
              }));

              fetch(
                CONSTANTS.backendUrl +
                  `/api/recipe/v1/createRecipe/${cnpInput}`,
                {
                  method: "PUT",
                  headers: {
                    "Content-Type": "application/json",
                    Authorization:
                      "Bearer " + localStorage.getItem("accessToken"),
                  },
                  body: JSON.stringify(payload),
                }
              )
                .then(async (res) => {
                  const message = await res.text();
                  if (res.ok) {
                    alert("Rețetă creată cu succes.");
                    setShowRecipeDialog(false);
                    setSelectedMedications([]);
                    setCnpInput("");
                  } else {
                    alert("Eroare: " + message);
                  }
                  refetchMedications();
                  handleClearSelection();
                })
                .catch(() => alert("Eroare la trimiterea cererii."));
            }}
          >
            Creează rețetă
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
export default MedicationActionsPage;

import React, { useState, useEffect } from "react";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import DownloadIcon from "@mui/icons-material/Download";
import CONSTANTS from "../constants/Constants";

import {
  Dialog,
  DialogTitle,
  DialogContent,
  Tabs,
  Tab,
  Box,
  Typography,
  TextField,
  Button,
  CircularProgress,
  InputLabel,
} from "@mui/material";

function ManagePatients({ open, onClose, cnp }) {
  useEffect(() => {
    document.body.style.margin = "0";
    document.body.style.overflow = "hidden";
  }, []);

  const [tabIndex, setTabIndex] = useState(0);
  const [diagnoses, setDiagnoses] = useState([]);
  const [diagnosisText, setDiagnosisText] = useState("");
  const [diagnosisDate, setDiagnosisDate] = useState("");
  const [stateOfHealth, setStateOfHealth] = useState("");
  const [medicalFiles, setMedicalFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [recipeList, setRecipeList] = useState([]);

  const token = localStorage.getItem("accessToken");

  const payload = {
    username: "",
    email: "",
    cnp: cnp,
  };
  const handleFetchRecipe = () => {
    fetch(CONSTANTS.backendUrl + `/api/recipe/v1/getReteta/${cnp}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then(async (res) => {
        if (!res.ok) {
          if (res.status === 404) {
            setRecipeList([]);
            return;
          }

          const msg = await res.text();
          throw new Error(msg || "Eroare necunoscută");
        }

        return res.json();
      })
      .then((data) => {
        if (data?.content) {
          const parsed = JSON.parse(data.content);
          setRecipeList(parsed);
        } else {
          setRecipeList([]);
        }
      })
      .catch((err) => {
        console.error("Eroare în fetch rețetă:", err);
      });
  };

  useEffect(() => {
    if (!open || !cnp) return;

    setLoading(true);

    fetch(CONSTANTS.backendUrl + "/api/patientManagement/v1/getDiagnoses", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    })
      .then((res) => res.json())
      .then((data) => setDiagnoses(data))
      .catch((err) =>
        console.error("Eroare la încărcarea diagnosticului", err)
      );

    fetch(CONSTANTS.backendUrl + "/api/patientManagement/v1/getStateOfHealth", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    })
      .then((res) => res.text())
      .then((data) => setStateOfHealth(data))
      .catch((err) => console.error("Eroare la stare de sănătate", err))
      .finally(() => setLoading(false));
    handleFetchRecipe();
  }, [open, cnp]);

  const handleTabChange = (e, newValue) => {
    setTabIndex(newValue);
    if (newValue === 3) handleFetchRecipe();
  };

  const handleAddDiagnosis = () => {
    if (!diagnosisText || !diagnosisDate) return;
    fetch(CONSTANTS.backendUrl + "/api/patientManagement/v1/addDiagnosis", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        ...payload,
        diagnosis: diagnosisText,
        diagnosisDate,
      }),
    })
      .then((res) => res.text())
      .then(() => {
        setDiagnoses([
          ...diagnoses,
          { diagnosis: diagnosisText, date: diagnosisDate },
        ]);

        setDiagnosisText("");
        setDiagnosisDate("");
      });
  };

  const handleUpdateState = () => {
    fetch(
      CONSTANTS.backendUrl + "/api/patientManagement/v1/updateStateOfHealth",
      {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ ...payload, stateOfHealth }),
      }
    ).then(() => alert("Stare actualizată."));
  };
  const handleDownloadFile = () => {
    fetch(CONSTANTS.backendUrl + "/api/patientManagement/v1/getMedicalInfo", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    })
      .then(async (res) => {
        if (!res.ok) {
          const msg = await res.text();
          alert(msg || "Eroare la descărcarea fișei medicale.");
          return;
        }

        return res.blob().then((blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement("a");
          a.href = url;
          a.download = `fisa_medicala_${cnp}.zip`;
          a.click();
          a.remove();
        });
      })
      .catch((err) => {
        console.error("Eroare descărcare:", err);
        alert("Pacientul nu are o fișă medicală.");
      });
  };

  const handleUploadFiles = () => {
    if (!medicalFiles || medicalFiles.length === 0) {
      alert("Niciun fișier selectat.");
      return;
    }

    const formData = new FormData();
    formData.append("cnp", payload.cnp);
    formData.append("username", payload.username);
    formData.append("email", payload.email);
    for (let f of medicalFiles) formData.append("files", f);

    fetch(CONSTANTS.backendUrl + "/api/patientManagement/v1/addMedicalInfo", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: formData,
    }).then(() => alert("Fișiere încărcate."));
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        Gestionare pacient: {cnp}
        <IconButton onClick={onClose} edge="end">
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent>
        <Tabs value={tabIndex} onChange={handleTabChange} sx={{ mb: 2 }}>
          <Tab label="Diagnostic" />
          <Tab label="Fișă Medicală" />
          <Tab label="Stare de Sănătate" />
          <Tab label="Rețetă medicală" />
        </Tabs>

        {tabIndex === 0 && (
          <Box display="flex" flexDirection="column" gap={2}>
            <TextField
              label="Diagnostic"
              value={diagnosisText}
              onChange={(e) => setDiagnosisText(e.target.value)}
            />
            <TextField
              type="date"
              value={diagnosisDate}
              onChange={(e) => setDiagnosisDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
            />
            <Button
              variant="contained"
              onClick={handleAddDiagnosis}
              sx={{ color: "white" }}
            >
              Salvează Diagnostic
            </Button>

            <Box>
              <Typography variant="h6">Istoric diagnostic:</Typography>
              {[...diagnoses]
                .sort((a, b) => new Date(b.date) - new Date(a.date))
                .map((d, i) => {
                  const formattedDate = new Intl.DateTimeFormat("ro-RO", {
                    day: "2-digit",
                    month: "2-digit",
                    year: "numeric",
                  }).format(new Date(d.date));

                  return (
                    <Typography key={i}>
                      - {d.diagnosis} ({formattedDate})
                    </Typography>
                  );
                })}
            </Box>
          </Box>
        )}

        {tabIndex === 1 && (
          <Box display="flex" flexDirection="column" gap={2}>
            <InputLabel>Fișă medicală</InputLabel>

            <input
              type="file"
              multiple
              onChange={(e) => setMedicalFiles(e.target.files)}
            />
            <Button variant="outlined" onClick={handleUploadFiles}>
              Încarcă Fișiere
            </Button>

            <Button
              variant="contained"
              startIcon={<DownloadIcon />}
              onClick={handleDownloadFile}
              sx={{ color: "white" }}
            >
              Descarcă Fișa Medicală
            </Button>
          </Box>
        )}

        {tabIndex === 2 && (
          <Box display="flex" flexDirection="column" gap={2}>
            <TextField
              label="Stare de sănătate"
              value={stateOfHealth}
              onChange={(e) => setStateOfHealth(e.target.value)}
              multiline
              minRows={3}
            />
            <Button
              variant="contained"
              onClick={handleUpdateState}
              sx={{ color: "white" }}
            >
              Actualizează Starea
            </Button>
          </Box>
        )}
        {tabIndex === 3 && (
          <Box display="flex" flexDirection="column" gap={2}>
            {recipeList.length > 0 ? (
              <Box>
                <Typography variant="h6" mb={1}>
                  Rețetă pacient:
                </Typography>
                {recipeList.map((item, index) => (
                  <Typography key={index}>
                    - {item.medicationName}: {item.requiredQuantity} buc.
                  </Typography>
                ))}
              </Box>
            ) : (
              <Typography color="text.secondary">
                Pacientul nu are rețetă salvată.
              </Typography>
            )}
          </Box>
        )}
      </DialogContent>
    </Dialog>
  );
}

export default ManagePatients;

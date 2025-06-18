import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: {
      main: "#23b89a",
    },
    secondary: {
      main: "#fafcfb",
    },
    error: {
      main: "#f44336",
    },
    background: {
      default: "#f5f5f5",
    },
    custom: {
      darkBlue: "#003366",
      lightGray: "#e0e0e0",
      successGreen: "#4caf50",
      defaultText: "#333333",
    },
  },
  typography: {
    fontFamily: "Roboto, sans-serif",
    h6: {
      fontWeight: 700,
    },
  },
});

export default theme;

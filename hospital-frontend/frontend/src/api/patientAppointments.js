import CONSTANTS from "../constants/Constants";

function getAppointmentsForPatient() {
  console.log(CONSTANTS.backendUrl + "/api/appointment/v1/patient");
  return fetch(CONSTANTS.backendUrl + "/api/appointment/v1/patient", {
    method: "GET",
    headers: {
      Authorization: "Bearer " + localStorage.getItem("accessToken"),
    },
  })
    .then(async (response) => {
      const message = await response.json();
      if (response.status === 200) {
        return [true, message];
      } else {
        alert(response.status + ": " + message);
        return [false, ""];
      }
    })
    .catch((error) => {
      console.error("Eroare " + error);
      return [false, []];
    });
}

export default getAppointmentsForPatient;

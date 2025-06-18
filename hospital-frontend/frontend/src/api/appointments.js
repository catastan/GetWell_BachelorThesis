import CONSTANTS from './Constants';

function getAllApointments() {
  return fetch(CONSTANTS.backendUrl + "/api/appointment/v1/getAppointments", {
    method: "GET",
    headers: {
      Authorization: "Bearer " + localStorage.getItem("accessToken"),
    },
  }).then(async (response) => {
    const message = await response.json();
    if (response.status === 200) {
      return [true, message];
    } else {
      alert(response.status + ":" + message);
      return [false, ""];
    }
  });
}

export default getAllApointments;

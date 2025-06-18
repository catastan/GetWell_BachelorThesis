export async function loginFetch(email, password) {
  const response = await fetch("http://localhost:8081/api/auth/v1/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  const message = await response.json();

  if (response.status === 200) {
    return { success: true, token: message.accessToken, role: message.role };
  } else {
    return { success: false };
  }
}

export async function fetchUserRole() {
  const token = localStorage.getItem("accessToken");
  const response = await fetch("http://localhost:8081/api/auth/v1/user-role", {
    headers: {
      Authorization: "Bearer " + token,
    },
  });

  if (response.ok) {
    const data = await response.json();
    return data.role;
  } else {
    return null;
  }
}

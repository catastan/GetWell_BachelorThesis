import os
import requests
import json
import time

LOGIN_URL = 'http://localhost:8081/api/auth/v1/login'
ADD_MEDICAMENT_URL = 'http://localhost:8081/api/medication/v1/addMedication'
MEDICAMENTS_DIR = 'medicaments'
LOG_FILE = 'errors.log'

EMAIL = 'admin@hospital.com'
PASSWORD = 'hospital'

login_payload = {
    'email': EMAIL,
    'password': PASSWORD
}

try:
    response = requests.post(LOGIN_URL, json=login_payload)
    response.raise_for_status()
    access_token = response.json().get('accessToken')

    if not access_token:
        raise ValueError('Tokenul nu a fost gasit')

    headers = {
        'Authorization': f'Bearer {access_token}',
        'Content-Type': 'application/json'
    }

    for filename in os.listdir(MEDICAMENTS_DIR):
        filepath = os.path.join(MEDICAMENTS_DIR, filename)

        if not os.path.isfile(filepath) or not filename.endswith(".txt"):
            continue

        category = os.path.splitext(filename)[0]

        with open(filepath, 'r', encoding='utf-8') as file:
            for line in file:
                line = line.strip().replace('\u200b', '')
                if not line:
                    continue

                names = [name.strip() for name in line.split("/") if name.strip()]

                for name in names:
                    payload = {
                        'name': name,
                        'category': category,
                        'startStock': 30
                    }

                    try:
                        add_response = requests.post(ADD_MEDICAMENT_URL, json=payload, headers=headers)
                        status = add_response.status_code
                        text = add_response.text

                        if status in [200, 201]:
                            print(f"[✔] Adaugat: {name} (categoria: {category})")
                        elif status == 400 and "could not be created" in text:
                            print(f"[↩] Ignorat: {name} – deja exista")
                        else:
                            print(f"[✖] Eroare la {name}: {status} - {text}")
                            with open(LOG_FILE, 'a', encoding='utf-8') as log:
                                log.write(f"[{category}] {name} -> {status}: {text}\n")

                    except requests.RequestException as e:
                        print(f"[✖] Eroare retea la {name}: {e}")
                        with open(LOG_FILE, 'a', encoding='utf-8') as log:
                            log.write(f"[{category}] {name} -> NETWORK ERROR: {e}\n")

                    time.sleep(0.1)

    print("\n✔️ Script finalizat. Verifica 'errors.log' pentru erori.")

except requests.RequestException as e:
    print(f"[!] Eroare la login: {e}")
except Exception as ex:
    print(f"[!] Eroare: {ex}")

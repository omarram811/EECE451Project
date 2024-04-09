import socket
import json
import traceback

# Function to send data to the server
def send_data(data):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect(('0.0.0.0', 8080))
        s.sendall(data.encode())
        response = s.recv(1024)
        print('Response from server:', response.decode())

# Function to request statistics from the server
def request_statistics(start_date, end_date):
    request_data = {
        "operator": "Operator1",
        "signal_power": "10",
        "snr": 20,
        "network_type": "Type1",
        "frequency_band": "Band1",
        "cell_id": "Cell1",
        "date_1": start_date,
        "date_2": end_date
    }
    send_data(json.dumps(request_data))

send_data(json.dumps({
    "operator": "Operator1",
    "signal_power": "10",
    "snr": 20,
    "network_type": "Type1",
    "frequency_band": "Band1",
    "cell_id": "Cell1",
    "date_1": None,
    "date_2": None
}))

#request_statistics("2024-01-01", "2024-01-15")


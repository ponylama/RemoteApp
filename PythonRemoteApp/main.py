# Requires Python 3.7

import requests

ANDROID_IP = "10.100.102.4"
PORT = 8080


def send_get_request(route: str):
    """
    Sends an HTTP GET request to the specified route on the Android device.

    Args:
        route (str): The API route to send the GET request to (e.g., "", "getprop").
    """
    url = f"http://{ANDROID_IP}:{PORT}/{route}"
    try:
        response = requests.get(url)
        print(f"[GET] /{route} → {response.status_code}: {response.text}")
    except requests.RequestException as e:
        print(f"[GET] /{route} → Failed: {e}")


def send_post_request(route: str):
    """
    Sends an HTTP POST request to the specified route on the Android device.

    Args:
        route (str): The API route to send the POST request to (e.g., "takephoto", "opencamera").
    """
    url = f"http://{ANDROID_IP}:{PORT}/{route}"
    try:
        response = requests.post(url)
        print(f"[POST] /{route} → {response.status_code}: {response.text}")
    except requests.RequestException as e:
        print(f"[POST] /{route} → Failed: {e}")


def main():
    """
    Main function that sends predefined GET and POST requests
    to the Android server for testing purposes.
    """
    get_routes = ["", "getprop"]
    post_routes = ["takephoto", "opencamera"]

    print("=== Sending GET requests ===")
    for route in get_routes:
        send_get_request(route)

    print("\n=== Sending POST requests ===")
    for route in post_routes:
        send_post_request(route)


if __name__ == "__main__":
    main()

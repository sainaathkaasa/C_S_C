import asyncio
import json
import random
import websockets

async def send_data():
    uri = "ws://localhost:9091/client/ws"  # Connecting to Java client on this endpoint
    try:
        async with websockets.connect(uri) as websocket:
            data = {
                "sensor_id": "your_sensor_id_here",
                "uses_template": "EnvironmentSensing",
                "timestamp": "2024-12-10T11:30:00.000Z",  # Use the current UTC timestamp
                "temperature": round(random.uniform(-30.0, 0), 2),  # Random temperature between -30 and 0 degrees
                "moisture": round(random.uniform(30.0, 100.0), 2),  # Random moisture between 30 and 100
                "pressure": round(random.uniform(0.5, 1.5), 2),  # Random pressure between 950 and 1050 hPa
                "particles_ppm": round(random.uniform(0.0, 15.0), 2)
            }
            # Replace with your generated data
            await websocket.send(json.dumps(data))
            print(f"Data sent: {data}")
    except ConnectionRefusedError:
        print("Connection refused. Please ensure the WebSocket server is running and the URL is correct.")
    except websockets.exceptions.InvalidStatusCode as e:
        print(f"Invalid status code: {e.status_code}")

asyncio.run(send_data())

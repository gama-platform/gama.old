import websockets
import asyncio

HOST = "localhost"  # The server's hostname or IP address
PORT = 3001  # The port used by the server


async def main():
    async for websocket in websockets.connect("ws://" + HOST + ":" + str(PORT)):
        try:
            while True:
                await websocket.send("I'm the websocket client".encode("utf-8"))
                message = await websocket.recv()
                print("client received: " + message)
        except websockets.ConnectionClosed as ex:
            print(ex)
            exit(0)

if __name__ == "__main__":
    asyncio.run(main())

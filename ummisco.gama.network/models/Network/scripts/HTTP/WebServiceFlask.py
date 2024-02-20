# -*- coding: utf-8 -*-

# This code is a very simple script that can be launched in a Flask HTTP server.
# https://flask.palletsprojects.com/en/2.2.x/quickstart/
# 
# Instructions: 
#  * Just run the file, the server URL and port will be displayed

from flask import Flask
from flask import request

app = Flask(__name__)

@app.route('/test', methods=['GET', 'POST'])
def index():    
    print("request ",request)
    print("request.method ", request.method)
    print("request.data ", request.data)
    print("request.headers ", request.headers)
    print("request.json ", request.json)

    return "true"

if __name__ == '__main__':
    app.run(debug=True)
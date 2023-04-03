# -*- coding: utf-8 -*-

# This code is a very simple script that can be launched in a Flask HTTP server.
# https://flask.palletsprojects.com/en/2.2.x/quickstart/
# 
# Instructions: 
#  * In a Terminal, locate in the folder containing this file
#  * Type the following bash instructions (the last one will launch the server)
#
# export FLASK_ENV=development
# export FLASK_APP=WebServiceFlask.py
# flask run

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

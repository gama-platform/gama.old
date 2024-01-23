
// var ABSOLUTE_PATH_TO_GAMA = 'C:\\git\\';
// var modelPath = ABSOLUTE_PATH_TO_GAMA + 'gama/msi.gama.models/models/Tutorials/Road Traffic/models/Model 05.gaml'; 
// var modelPath =  '/var/www/github/gama/msi.gama.models/models/Tutorials/Road Traffic/models/Model 05.gaml';

// var modelPath = '/Users/hqn88/git/gama/msi.gama.models/models/Tutorials/Road Traffic/models/Model 05.gaml';
// var experimentName = 'road_traffic';
var modelPath = '/Users/hqn88/git/gama/ummisco.gama.serialize/models/Serialization/models/Backward Experiment Formats.gaml';
var experimentName = 'prey_predator';// 'Binary back and forth';
// const experiment = new GAMA("ws://51.255.46.42:6001/", modelPath, experimentName);
var experiment = null;
function connect() {
    const host = document.getElementById("host").value;
    const port = document.getElementById("socket").value;
    experiment = new GAMA("ws://" + host + ":" + port + "/", modelPath, experimentName);
    experiment.logger = log;
    experiment.connect(on_connected);
}
function load() {
    if (experiment == null) return;
    // console.log(document.getElementById("model").value);
    experiment.modelPath=document.getElementById("model").value;
    experiment.experimentName=document.getElementById("exp_name").value;

    // experiment.setParameters([
    //     { "name": "Number of people agents", "value": 111, "type": "int" },
    //     { "name": "Value of destruction when a people agent takes a road", "value": 0.2, "type": "float" }
    // ]);
    // experiment.setEndCondition("cycle>=15");
    experiment.launch(onReceiveMsg);
}
function play() {
    if (experiment == null) return;
    experiment.play(onReceiveMsg);

}

function step() {
    if (experiment == null) return;
    experiment.step(1, onReceiveMsg);

}
function stepback() {
    if (experiment == null) return;
    experiment.stepback(1, onReceiveMsg);

}
function eval() {
    if (experiment == null) return;

    // experiment.evalExpr("create people number:100;", onReceiveMsg);
    // experiment.evalExpr("length(people)", onReceiveMsg);
    experiment.evalExpr("cycle", onReceiveMsg);

}
function reload() {
    if (experiment == null) return;

    experiment.setParameters([
        { "name": "Number of people agents", "value": "333", "type": "int" },
        { "name": "Value of destruction when a people agent takes a road", "value": "0.2", "type": "float" }
    ]);
    experiment.setEndCondition("cycle>=10000");
    experiment.reload();
}
function on_connected() {
    onReceiveMsg("connected");

    // experiment.evalExpr("cycle", onReceiveMsg);


    // experiment.play();
    // experiment.evalExpr("length(people)", onReceiveMsg);
    // experiment.evalExpr("cycle", onReceiveMsg);

}

function onReceiveMsg(e) {
    log(e);
}

function log(e) {
    const element = document.getElementById("output");
    element.innerHTML += (e);
    element.innerHTML += ("</br>");
    element.innerHTML += ("------------------------------");
    element.innerHTML += ("</br>");
}
class GAMA {
    host = "";
    modelPath = 'gama/msi.gama.models/models/Tutorials/Road Traffic/models/Model 05.gaml';
    experimentName = 'road_traffic';


    socket_id = 0;
    exp_id = 0;
    wSocket;
    state = "";
    queue = [];
    req = "";
    result = "";
    executor;
    executor_speed = 1;
    endCondition = "";
    param = [];
    logger;
    constructor(address, md, exp) {
        this.host = address;
        this.modelPath = md;
        this.experimentName = exp;
    }
    connect(opened_callback, closed_callback) {

        this.wSocket = new WebSocket(this.host);

        this.wSocket.onclose = function (event) {
            clearInterval(this.executor);
            if (closed_callback) closed_callback();
        };
        this.wSocket.onerror = function (event) {
            console.log("Error: " + event.message);
        }


        this.wSocket.onmessage = function (e) {
            // console.log(event); 
            var result = JSON.parse(e.data).content;
            if (result) _this.socket_id = result;
        };
        var _this = this;
        this.wSocket.onopen = function (event) {
            if (opened_callback) opened_callback();
            _this.initExecutor();
        };


        this.wSocket.addEventListener('open', () => {
            this.wSocket.onmessage = (event) => {
                this.executor = setInterval(() => {
                    if (this.queue.length > 0 && this.req === "") {
                        // console.log(this.queue);
                        this.req = this.queue.shift();
                        this.req.exp_id = this.exp_id;
                        this.req.socket_id = this.socket_id;
                        // console.log(this.req);
                        this.wSocket.send(JSON.stringify(this.req));
                        // console.log("request " + JSON.stringify(this.req));
                        if (this.logger) { this.logger("<div>request</div> " + JSON.stringify(this.req)); }
                        var myself = this;
                        this.wSocket.onmessage = function (event) {
                            // console.log(event.data);
                            if (typeof event.data != "object") {
                                if (myself.req.callback) {
                                    myself.req.callback(event.data,
                                        myself.endRequest());
                                } else {
                                    myself.endRequest();
                                }
                            }
                        };
                    }

                }, this.executor_speed);
            };
        });
    }
    initExecutor() {

        this.executor = setInterval(() => {
            if (this.queue.length > 0 && this.req === "") {
                this.req = this.queue.shift();
                this.req.exp_id = this.exp_id;
                this.req.socket_id = this.socket_id;
                // console.log(this.req);
                this.wSocket.send(JSON.stringify(this.req)); // console.log("request " + JSON.stringify(this.req));

                if (this.logger) {
                    this.logger("<div>request</div>" + JSON.stringify(this.req));
                }

                var myself = this;

                this.wSocket.onmessage = function (event) {
                    // console.log(event);

                    // if (this.logger) {
                    //     this.logger("response " + (event.data));
                    // }
                    if (myself.req !== "") {
                        // console.log(myself.req);
                        if (event.data instanceof Blob) { } else {
                            if (myself.req.callback) {
                                myself.req.callback(event.data);
                            }
                            myself.endRequest();
                        }
                    }
                };
            }
        }, this.executor_speed);
    }
    requestCommand(cmd) {
        if (this.req === "" || this.queue.length == 0) {
            this.queue.push(cmd);
        }
    }
    endRequest() {
        // console.log("end response of "+ this.req.type);
        this.req = "";
    }

    evalExpr(q, c, es) {
        // var cmd = {
        //     "type": "expression",
        //     "socket_id": this.socket_id,
        //     "exp_id": this.exp_id,
        //     "escaped": es ? es : false,
        //     "expr": q,
        //     "callback": c
        // };  
        var cmd = {
            // "atimestamp": Math.floor(Math.random() * Date.now()).toString(16),
            "type": "expression",
            "model": this.modelPath,
            "experiment": this.experimentName,
            "socket_id": this.socket_id,
            "exp_id": this.exp_id,
            "console": false,
            "status": false,
            "dialog": false,
            "runtime": false,
            "escaped": es ? es : false,
            "sync": false,
            "expr": q,
            "callback": c
        };
        this.requestCommand(cmd);
    }
    execute(q, c) {
        // var cmd = {
        //     "type": q,
        //     "model": this.modelPath,
        //     "experiment": this.experimentName,
        //     "socket_id": this.socket_id,
        //     "exp_id": this.exp_id,
        //     "console": false,
        //     "status": false,
        //     "dialog": false,
        //     "auto-export": false,
        //     "parameters": this.param,
        //     "until": this.endCondition,
        //     "sync": true,
        //     "callback": c
        // };

        var cmd = {
            // "atimestamp": Math.floor(Math.random() * Date.now()).toString(16),
            "type": q,
            "model": this.modelPath,
            "experiment": this.experimentName,
            "socket_id": this.socket_id,
            "exp_id": this.exp_id,
            "console": false,
            "status": false,
            "dialog": false,
            "runtime": false,
            "auto-export": false,
            "parameters": this.param,
            "sync": false,
            "callback": c
        };
        this.requestCommand(cmd);
    }

    setParameters(p) {
        this.param = p;
    }
    setEndCondition(ec) {
        this.endCondition = ec;
    }

    async launch(c) {

        this.queue.length = 0;
        var myself = this;
        this.status = "load";
        this.execute(this.status, function (e) {

            var result = JSON.parse(e);
            console.log(result);
            // if(result.type==="CommandExecutedSuccessfully"){
            if (result.type === "CommandExecutedSuccessfully" && result.content) myself.exp_id = result.content;
            if (c) {
                c(result);
            }
            // }
        });
    }
    play(c) {
        clearInterval(this.output_executor);
        // this.queue.length = 0;
        this.state = "play";
        this.execute(this.state, c);
    }

    pause(c) {
        //     // this.queue.length = 0;
        //     this.state = "pause";
        //     this.execute(this.state, c);

        this.queue.length = 0;
        clearInterval(this.output_executor);
        this.status = "pause";
        this.execute(this.status, () => {
            if (c) c();
        });
    }

    step(nb, c) {
        // this.queue.length = 0;
        this.state = "step";
        // this.execute(this.state, c);
        var cmd = {
            // "atimestamp": Math.floor(Math.random() * Date.now()).toString(16),
            "type": "step",
            "model": this.modelPath,
            "experiment": this.experimentName,
            "socket_id": this.socket_id,
            "exp_id": this.exp_id,
            "console": false,
            "status": false,
            "dialog": false,
            "runtime": false,
            "nb_step": nb,
            "sync": false,
            "callback": c
        };
        this.requestCommand(cmd);
    }
    stepback(nb, c) {
        // this.queue.length = 0;
        this.state = "stepBack";
        // this.execute(this.state, c);
        var cmd = {
            // "atimestamp": Math.floor(Math.random() * Date.now()).toString(16),
            "type": "stepBack",
            "model": this.modelPath,
            "experiment": this.experimentName,
            "socket_id": this.socket_id,
            "exp_id": this.exp_id,
            "console": false,
            "status": false,
            "dialog": false,
            "runtime": false,
            "nb_step": nb,
            "sync": false,
            "callback": c
        };
        this.requestCommand(cmd);
    }


    reload(c) {
        // this.queue.length = 0;
        this.state = "reload";
        this.execute(this.state, c);
        // if (c) c();
    }

} 
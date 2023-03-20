# GAMA Headless

## Getting started

You can start GAMA in headless mode using the custom script file. 

Several options are available, you can run the help to command to have a better view of what is available as follow : 

```bash
bash ./gama-headless.sh -help
```

### GUI experiment

You can run a GUI experimenta as follow : 

```bash
bash ./gama-headless.sh [options] [xmlInputFile] [outputDirectory]
```

You can try for example this sample file : 

```bash 
bash ./gama-headless.sh ./samples/roadTraffic.xml ./output-folder
```

### Batch experiment

You can also run a batch experimenta as follow : 

```bash
bash ./gama-headless.sh [options] -batch [experimentName] [modelFile.gaml]
```

You can try for example this sample file : 

```bash 
bash ./gama-headless.sh -batch Optimization ./samples/predatorPrey/predatorPrey.gaml
```

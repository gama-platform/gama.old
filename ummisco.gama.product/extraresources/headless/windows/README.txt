# GAMA Headless

## Getting started

You can start GAMA in headless mode using the custom script file. 

Several options are available, you can run the help to command to have a better view of what is available as follow : 

```bash
.\gama-headless.bat -help
```

### GUI experiment

You can run a GUI experimenta as follow : 

```bash
.\gama-headless.bat [options] [xmlInputFile] [outputDirectory]
```

You can try for example this sample file : 

```bash 
.\gama-headless.bat ./samples/roadTraffic.xml ./output-folder
```

### Batch experiment

You can also run a batch experimenta as follow : 

```bash
.\gama-headless.bat [options] -batch [experimentName] [modelFile.gaml]
```

You can try for example this sample file : 

```bash 
.\gama-headless.bat -batch Optimization ./samples/predatorPrey/predatorPrey.gaml
```

# Voronoi Diagrams

Creates a Voronoi Diagram from a program definition using the LOC metric

## Quick Usage


Download any grammar defined in ANTLR4 and run the following command,
```
java -cp antlr-4.9-complete.jar org.antlr.v4.Tool -Dlanguage=Python3 -visitor ../clone_detection/grammars/XXX/XXX.g4
```

To run the program, create an environment
```
conda create -n clones python=3.8
conda activate clones

pip install antlr4-python3-runtime==4.7.2

```

Then run the parser Using the command 

```
python -m analysis --f examples/example.kt 
```
will return the eCST for the program

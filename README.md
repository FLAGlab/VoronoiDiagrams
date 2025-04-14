# VAR Check

VAR Check offers software quality analysis for Reinforcement Learning (RL) programs. VAR Check provides an abstraction of RL programs and a first-hand view of code smells. Our visualization is based on Voronoi diagrams structured basd on the results of the software quality metrics.
Currently VAR Check includes four metrics, but others can be added modularly

- Large Class (LC)
- Long Method (LM)
- Multiply Nested Components (MNC)
- Long Parameter List (LPL)

## Usage

### 1. Metrics Extraction

Metrics are extracted using a Python script (`PyAnalyzer.py`) that analyzes the source code for each of the required metrics

``` python PyAnalyzer.py /path/to/programs Metric1 Metric2 ```

### 2. Diagram Generation

The Voronoi diagrams are generated from the extracted metrics, based on the Voronoi Tree Maps tool. To generate the diagrams run the `PyAnalyzer.java` class part of the java project in the repository.

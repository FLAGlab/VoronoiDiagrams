import os

def analyze_python_file(file_path, strategy1, strategy2):
    csv_file_path = "results/code_metrics_" + os.path.splitext(os.path.basename(file_path))[0] + ".csv"
    print(os.path.splitext(os.path.basename(file_path))[0])
    with open(file_path, 'r') as python_file, open(csv_file_path, 'w+') as csv_file:
        group = 0
        for line in python_file:
            ng1, flag1, res1 = strategy1.analyze_line(line, group, lambda x : x+1)
            ng2, flag2, res2 = strategy2.analyze_line(line, group)
            group = max(ng1, ng2)
            if flag1:
                csv_file.write(res1)
            if flag2:
                csv_file.write(res2)
        print("Code analysis for", os.path.basename(file_path), "completed. Results saved in", csv_file_path)


def indent_counter(line):
    spaces = 0
    for c in line:
        if c == ' ':
            spaces += 1
        else:
            break
    return spaces / 4

class Strategy():
    def __init__(self):
        self.indent_level = 0

    def analyze(self, _line, _group, _group_inc):
        pass

class Analysis():
    def __init__(self, strategy:Strategy) -> None:
        self.strategy = strategy
        
    def strategy(self) -> Strategy:
        return self.strategy
        
    def set_strategy(self, strategy:Strategy) -> None:
        self.strategy = strategy
        
    def analyze_line(self, line:str, group:int) -> list[int,bool,str]:
        return self.strategy.analyze(line, group)

class LargeClass(Strategy):
    def __init__(self):
        self.class_name = ""
        self.class_count = 0
        self.indent_level = 0

    def analyze(self, line:str, group:int, group_inc = lambda x : x) -> list[int, bool,str]:
        indentation = indent_counter(line)
        if line.startswith("class"):
            self.indent_level = indentation
            self.class_name = line.strip()[6:-1]
            self.class_count += 1
            return group_inc(group), False, ""
        elif self.class_name != "" and (indentation - self.indent_level) > 0:
            self.class_count += 1
            return group, False, ""
        elif self.class_name != "" and (indentation - self.indent_level) == 0:
            result = f"LC,class,{self.class_name},{group},{self.class_count}\n"
            self.class_count = 0
            self.indent_level = 0
            self.class_name = ""
            return group, True, result
        else:
            return group, False, ""

class LongMethod(Strategy):
    def __init__(self):
        self.method_count = 0
        self.def_name = ""
        self.indent_level = 0
        
    def analyze(self, line:str, group:int, group_inc = lambda x : x) -> list[int, bool,str]:
        def_index = line.find("def")
        indentation = indent_counter(line)
        if  def_index != -1:
            self.def_name = line[def_index+4:line.index("(")]
            self.indent_level = indentation
            self.method_count += 1
            return group_inc(group), False, ""
        elif self.def_name != "" and indentation - self.indent_level <= 0:
            result = f"LM,method,{self.def_name},{group},{self.method_count}\n"
            self.def_count = 0
            self.indent_level = 0
            return group, True, result
        elif self.def_name != "":
            self.method_count += 1 
            return group, False, ""
        return group,False,""
            
class LongParameterList(Strategy):
    def __init__(self):
        self.def_name = ""
        self.parameters_count = 0

    def analyze(self, line:str, group:int, group_inc = lambda x : x) -> list[int,bool,str]:
        def_index = line.find("def")
        indentation = indent_counter(line)
        if def_index != -1: 
            self.indent_level = indentation
            l_index = line.index("(") + 1
            self.parameters_count = 0
            self.def_name = line[def_index+4:l_index-1]
        if self.def_name != "":
            r_index = line.find("):")
            if r_index == -1:
                self.parameters_count += 1
            else:
                if l_index == r_index:
                    self.parameters_count = 0
                else:
                    self.parameters_count += len(line[l_index:r_index].split(","))
            result = group_inc(group), True, f"LPL,method,{self.def_name},{group},{self.parameters_count}\n"
            self.def_name = ""
            self.indent_level = 0
            return result
        return group, False, ""        

class MultiplyNestedComponent(Strategy):
    def __init__(self):
        self.in_function = False
        self.line_num = 0
        self.def_name = ""

    def analyze(self, line:str, group:int, group_inc = lambda x : x):
        def_index = line.find("def")
        if def_index != -1:
            self.in_function = True
            self.def_name = line[def_index+4:line.find("(")]
            self.indent_level = indent_counter(line)
            self.line_num = 0
            return group, False, ""
        elif indent_counter(line) - self.indent_level > 3:
            return group_inc(group), True, f"MNC,method,{self.def_name}-{self.line_num},{group},{indent_counter(line) - self.indent_level}\n"
        else:
            return group, False, ""


#PyAnalyzer.py /Users/NicolasCardozo_1/Documents/workspace-data/QCorp/cart_pole/data2 LC LPL
def main():
    import sys
    analyses = {"LC":"Large class", "LM":"Long Method", "LPL": "Long parameter List", "MNC":"Multiple Nested Components"}
    if len(sys.argv) < 4:
        print("Usage: python PyAnalyzer.py <folder_path> <analysis_1> <analysis_2>")
        print(len(sys.argv))
        sys.exit(1)
        
    #DEBUG MODE: ON    
    folder_path = sys.argv[2] #Debug values, add 1
    analysis_1 = sys.argv[3]
    analysis_2 = sys.argv[4]
    
    if (analysis_1 or analysis_2) not in analyses.keys():
        print("The currently available analyses are:")
        for k,v in analyses.items():
            print(f"\t {k} - {v}")
        sys.exit(1)
    
    if not os.path.isdir(folder_path):
        print("Error: The provided path is not a directory.")
        sys.exit(1)
        
    python_files = [f for f in os.listdir(folder_path) if f.lower().endswith(".py")]
    
    if not python_files:
        print("No Python files found in the specified directory.")
        sys.exit(1)
        
    for python_file in python_files:
        match (analysis_1):
            case "LC": strategy1 = Analysis(LargeClass())
            case "LM": strategy1 = Analysis(LongMethod())
            case "LPL": strategy1 = Analysis(LongParameterList())
            case "MNC": strategy1 = Analysis(MultiplyNestedComponent())
        match (analysis_2):
            case "LC": strategy2 = Analysis(LargeClass())
            case "LM": strategy2 = Analysis(LongMethod())
            case "LPL": strategy2 = Analysis(LongParameterList())
            case "MNC": strategy2 = Analysis(MultiplyNestedComponent())
        analyze_python_file(os.path.join(folder_path, python_file), strategy1, strategy2)

if __name__ == "__main__":
    main()


        
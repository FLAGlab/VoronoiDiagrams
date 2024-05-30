import os

def analyze_python_file(file_path):
    csv_file_path = "results/code_metrics_" + os.path.splitext(os.path.basename(file_path))[0] + ".csv"
    
    with open(file_path, 'r') as python_file, open(csv_file_path, 'w') as csv_file:
        class_count = 0
        method_count = 0
        group = 0
        class_name = ""
        def_name = ""
        
        for line in python_file:
            line = line.strip()
            if line.startswith("class "):
                if class_name != "":
                    csv_file.write(f"class,{class_name},{group},{class_count}\n")
                if def_name != "":
                    csv_file.write(f"method,{def_name},{group},{method_count}\n")
                group += 1
                class_count = 0
                method_count = 0
                class_count += 1
                class_name = line[6:]
            elif "def " in line:
                if def_name != "":
                    csv_file.write(f"method,{def_name},{group},{method_count}\n")
                method_count = 0
                method_count += 1
                class_count += 1
                def_name = line[4:line.index("(")]
            else:
                method_count += 1
                class_count += 1
                
        if class_name != "":
            csv_file.write(f"class,{class_name},{group},{class_count}\n")
        if def_name != "":
            csv_file.write(f"method,{def_name},{group},{method_count}\n")
        
        print("Code analysis for", os.path.basename(file_path), "completed. Results saved in", csv_file_path)

def main():
    import sys
    
    if len(sys.argv) != 2:
        print("Usage: python PyAnalyzer.py <folder_path>")
        sys.exit(1)
        
    folder_path = sys.argv[1]
    
    if not os.path.isdir(folder_path):
        print("Error: The provided path is not a directory.")
        sys.exit(1)
        
    python_files = [f for f in os.listdir(folder_path) if f.lower().endswith(".py")]
    
    if not python_files:
        print("No Python files found in the specified directory.")
        sys.exit(1)
        
    for python_file in python_files:
        analyze_python_file(os.path.join(folder_path, python_file))

if __name__ == "__main__":
    main()

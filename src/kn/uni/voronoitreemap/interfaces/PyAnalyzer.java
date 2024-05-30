/**
 * 
 */
package kn.uni.voronoitreemap.interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author lrodr
 *
 */
public class PyAnalyzer {

	/**
	 * 
	 */
	public PyAnalyzer() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java PythonCodeCounter <folder_path>");
            System.exit(1);
        }

        String folderPath = args[0];
        File folder = new File(folderPath);

        if (!folder.isDirectory()) {
            System.out.println("Error: The provided path is not a directory.");
            System.exit(1);
        }

        File[] pythonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".py"));

        if (pythonFiles == null || pythonFiles.length == 0) {
            System.out.println("No Python files found in the specified directory.");
            System.exit(1);
        }

        for (File pythonFile : pythonFiles) {
            analyzePythonFile(pythonFile);
        }
    }

    private static void analyzePythonFile(File pythonFile) {
        String csvFilePath = "results/code_metrics_" + getFileNameWithoutExtension(pythonFile.getName()) + ".csv";

        try (BufferedReader br = new BufferedReader(new FileReader(pythonFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath))) {

            int classCount = 0;
            int methodCount = 0;
            int group = 0;
            String line;
            String className = "";
            String defName = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("class ")) {
                    
                    if (!"".equals(className)){
                        bw.write("class," + className + "," + group + "," + classCount + "\n");
                    }
                    if (!"".equals(defName)){
                        bw.write("method," + defName + "," + group + "," + methodCount + "\n");
                    }
                    group++;
                    classCount = 0;
                    methodCount = 0;
                    classCount++;
                    className = line.substring(6);
                } else if (line.contains("def ")) {
                    if (!"".equals(defName)){
                        bw.write("method," + defName + "," + group + "," + methodCount + "\n");
                    }
                    methodCount = 0;
                    methodCount++;
                    classCount++;
                    defName = line.substring(4, line.indexOf("("));
                }
                else{
                    methodCount++;
                    classCount++;
                }
            }
            if (!"".equals(className)){
                bw.write("class," + className + "," + group + "," + classCount + "\n");
            }
            if (!"".equals(defName)){
                bw.write("method," + defName + "," + group + "," + methodCount + "\n");
            }

            System.out.println("Code analysis for " + pythonFile.getName() + " completed. Results saved in " + csvFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
    }
}


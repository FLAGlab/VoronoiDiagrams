package kn.uni.voronoitreemap.interfaces;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import kn.uni.voronoitreemap.IO.PDFStatusObject;
import kn.uni.voronoitreemap.IO.PNGStatusObject;
import kn.uni.voronoitreemap.interfaces.data.TreeData;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.treemap.VoronoiTreemap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Sample1 {

	public static void main(String[] args) {
		// create a convex root polygon
	    // create a convex root polygon
	    PolygonSimple rootPolygon = new PolygonSimple();
	    int width = 300;
	    int height = 500;
	    int numPoints = 10;
	    for (int j = 0; j < numPoints; j++) {
	      double angle = 2.0 * Math.PI * (j * 1.0 / numPoints);
	      double rotate = 2.0 * Math.PI / numPoints / 2;
	      double y = Math.sin(angle + rotate) * height + height;
	      double x = Math.cos(angle + rotate) * width + width;
	      rootPolygon.add(x, y);
	    }

//	    // create hierarchical structure
//	    TreeData data = new TreeData();

//	    data.addLink("TaxiWorld:", "code_metrics_s23_taxi");
//	    data.addLink("__init__", "TaxiWorld:");
//	    data.addLink("set_passenger_features", "TaxiWorld:");
//	    data.addLink("get_current_state", "TaxiWorld:");
//	    data.addLink("set_current_state", "TaxiWorld:");
//	    data.addLink("get_possible_actions", "TaxiWorld:");
//	    data.addLink("do_action", "TaxiWorld:");
//	    data.addLink("calculate_travel_reward", "TaxiWorld:");
//	    data.addLink("reset_state", "TaxiWorld:");
//	    data.addLink("is_terminal", "TaxiWorld:");
//	    data.addLink("QLearningAgent:", "code_metrics_s23_taxi");
//	    data.addLink("__init__", "QLearningAgent:");
//	    data.addLink("define_env", "QLearningAgent:");
//	    data.addLink("run_episode", "QLearningAgent:");
//	    data.addLink("run_Qlearning_algorithm", "QLearningAgent:");
//	    data.addLink("compute_qvalue", "QLearningAgent:");
//	    data.addLink("compute_action_from_current_state", "QLearningAgent:");
//	    data.addLink("get_action", "QLearningAgent:");
//	    data.addLink("get_qvalue", "QLearningAgent:");
//	    data.addLink("plot_policy", "QLearningAgent:");
//	    data.addLink("generate_Q_table", "QLearningAgent:");
//	    data.setRoot("code_metrics_s23_taxi");
//	    data.setRoot("code_metrics_s3_gridworld");
	    
	    for (int i = 1; i < 32; i++) {
            try {
                String name = "code_metrics" + "_pendulum_s" + i;
                String name2 = "pendulum_s" + i;
                // Replace 'your_file.csv' with the actual file path
                TreeData data = new TreeData();
        	    String filePath = "results_pendulum\\"+name+".csv";
//        	    String name = getFileNameWithoutExtension(filePath);
        	    ArrayList<String> names = new ArrayList<String>();
        	    ArrayList<Integer> weights = new ArrayList<Integer>();
        	    
        	    
        	    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    String parent = "";
                    

                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        
                        	if (parts[0].equals("class")) {
	                            parent = parts[1].trim();
	                            data.addLink(parent,name);
	                            System.out.println("Parent: "+parent+" - name: "+name+" - weight: "+parts[3]);
	                            names.add(parent);
	                            weights.add(Integer.parseInt(parts[3]));
	//                            data.setWeight(parent, Integer.parseInt(parts[3]));
                        		}
	                        else {
	                        	if (parent.equals("")) {
	                        		data.addLink(parts[1].trim(),name);
		                            System.out.println("Method: "+parts[1].trim()+" - name: "+name+" - weight: "+parts[3]);
		                            names.add(parts[1].trim());
		                            weights.add(Integer.parseInt(parts[3]));
	                        	}
	                        	else {
		                            data.addLink(parts[1].trim(),parent);
		                            System.out.println("Method: "+parts[1].trim()+" - parent: "+parent+" - weight: "+parts[3]);
		                            names.add(parts[1].trim());
		                            weights.add(Integer.parseInt(parts[3]));
		//                            data.setWeight(parts[1].trim(),Integer.parseInt(parts[3]));
	                        	}
	                        }
                    }
                    System.out.println(names.size());
                    System.out.println(weights.size());
                    data.setRoot(name);
                    
                    VoronoiTreemap treemap = new VoronoiTreemap();
            	    // VoronoiCore.setDebugMode(); //shows iteration process
            	    treemap.setRootPolygon(rootPolygon);
            	    System.out.println("TOTAL AREA");
            	    System.out.println(name);
            	    System.out.println(treemap.getRootPolygon().getArea());
            	    treemap.setTreeData(data);
            	    treemap.setCancelOnMaxIteration(true);
            	    treemap.setNumberMaxIterations(1500);
            	    treemap.setCancelOnThreshold(true);
            	    treemap.setErrorAreaThreshold(0.01);
            	    // treemap.setUniformWeights(true);
            	    treemap.setNumberThreads(1);
            	    System.out.println("------------");
            	    //add result handler
            	    treemap.setStatusObject(new PNGStatusObject("images_pendulum\\"+name2, treemap,names, weights));
//            	    treemap.setStatusObject(new PDFStatusObject("images_gridworld\\"+name, treemap));
            	    treemap.computeLocked();
                    System.out.println("-------------------");

                    
                } catch (IOException e) {
                    System.err.println("An error occurred: " + e.getMessage());
                }
        	    
        	    
//        	     data.setWeight("def_main", 4);// increase cell size (leafs only)


        	    
            } catch (Exception e) {
                System.err.println("error" + i);
                e.printStackTrace();
            }
            System.out.println("-------------------------------------------------");
        }
	    
//	    TreeData data = new TreeData();
//	    String filePath = "code_metrics_s2_gridworld.csv";
//	    String name = getFileNameWithoutExtension(filePath);
//	    
//	    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            String parent = "";
//            
//
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split(",");
//                
//                if (parts[0].equals("class")) {
//                    parent = parts[1].trim();
//                    data.addLink(parent,name);
//                    data.setWeight(parent, Integer.parseInt(parts[3].trim()));
//                } else {
//                    data.addLink(parts[1].trim(),parent);
//                    data.setWeight(parent,Integer.parseInt(parts[3].trim()));
//                }
//            }
//
//            data.setRoot(name);
//        } catch (IOException e) {
//            System.err.println("An error occurred: " + e.getMessage());
//        }
//	    
//	    
////	     data.setWeight("def_main", 4);// increase cell size (leafs only)
//
//
//	    VoronoiTreemap treemap = new VoronoiTreemap();
//	    // VoronoiCore.setDebugMode(); //shows iteration process
//	    treemap.setRootPolygon(rootPolygon);
//	    System.out.println("TOTAL AREA");
//	    System.out.println(treemap.getRootPolygon().getArea());
//	    treemap.setTreeData(data);
//	    treemap.setCancelOnMaxIteration(true);
//	    treemap.setNumberMaxIterations(1500);
//	    treemap.setCancelOnThreshold(true);
//	    treemap.setErrorAreaThreshold(0.01);
//	    // treemap.setUniformWeights(true);
//	    treemap.setNumberThreads(1);
//	    System.out.println("------------");
//	    //add result handler
//	    treemap.setStatusObject(new PNGStatusObject("results\\"+name, treemap));
//	    treemap.setStatusObject(new PDFStatusObject("results\\"+name, treemap));
//	    treemap.computeLocked();

	}
	
//	public static void readAndPrintCsv(String name, String filePath) {
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            String parent = "";
//
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split(",");
//
//                if (parts[0].equals("class")) {
//                    parent = parts[1].trim();
//                    data.addLink(parent,name);
//                    data.setWeight(parent ,parts[3].trim());
//                } else {
//                    data.addLink(parts[1].trim(),parent);
//                    data.setWeight(parent,parts[3].trim());
//                }
//            }
//
//            data.setRoot(name);
//        } catch (IOException e) {
//            System.err.println("An error occurred: " + e.getMessage());
//        }
//    }
	
	private static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
    }

}

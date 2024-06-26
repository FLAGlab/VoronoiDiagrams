/*******************************************************************************
 * Copyright (c) 2013 Arlind Nocaj, University of Konstanz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * For distributors of proprietary software, other licensing is possible on request: arlind.nocaj@gmail.com
 * 
 * This work is based on the publication below, please cite on usage, e.g.,  when publishing an article.
 * Arlind Nocaj, Ulrik Brandes, "Computing Voronoi Treemaps: Faster, Simpler, and Resolution-independent", Computer Graphics Forum, vol. 31, no. 3, June 2012, pp. 855-864
 ******************************************************************************/
package kn.uni.voronoitreemap.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;

import kn.uni.voronoitreemap.gui.Colors;
import kn.uni.voronoitreemap.helper.InterpolColor;
import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.treemap.VoroNode;
import kn.uni.voronoitreemap.treemap.VoronoiTreemap;

/**
 * Renderer which should draw the polygons of a Voronoi Treemap into a
 * Graphics2D. Mainly used for debugging and testing.
 * 
 * @author Arlind Nocaj
 * 
 */

public class VoroRenderer {
	private boolean renderText=true;

	boolean drawNames = true;
	Graphics2D g;

	protected VoronoiTreemap treemap;

	private JLayeredPane layeredPane;

	private BufferedImage bufferImage;

	public VoroRenderer() {
		init();
	}

	private void init() {
		layeredPane = new JLayeredPane();
	}

	public void setTreemap(VoronoiTreemap treemap) {
		this.treemap = treemap;
	}

	public VoronoiTreemap getTreemap() {
		return treemap;
	}

	public void setGraphics2D(Graphics2D graphics) {
		this.g = graphics;
	}

	public void renderTreemap(String filename, ArrayList<String> names, ArrayList<Integer> weights) {
		PolygonSimple rootPolygon = treemap.getRootPolygon();
		Rectangle rootRect = rootPolygon.getBounds();

		if (g == null) {
			int border=5;
			bufferImage = new BufferedImage(rootRect.width+border, rootRect.height+border,
					BufferedImage.TYPE_INT_ARGB);
			g = bufferImage.createGraphics();
			g.translate(border, border);
		}
		double translateX = -rootRect.getMinX();
		double translateY = -rootRect.getMinY();
		g.translate(translateX, translateY);

		// JFrame frame = new JFrame();
		// frame.setVisible(true);
		// frame.setSize(1500, 1000);
		// frame.getContentPane().add(layeredPane);
		// layeredPane.setVisible(true);
		// Ask the test to render into the SVG Graphics2D implementation.
		int maxHeight = 0;
		LinkedList<VoroNode> nodeList = new LinkedList<VoroNode>();
		LinkedList<VoroNode> nodeListReverse = new LinkedList<VoroNode>();
		for (VoroNode child : (VoronoiTreemap) treemap) {
			// JPolygon2 jp = new JPolygon2(child.getNodeID(), new
			// Integer(child.getNodeID()).toString());
			// layeredPane.add(jp, -child.getHeight());
			//
			// jp.setVisible(true);
			if (child.getPolygon() != null) {
				nodeList.add(child);
				nodeListReverse.addFirst(child);
				if (child.getHeight() > maxHeight) {
					maxHeight = child.getHeight();
				}
				
//				System.out.println(child.name);
//				System.out.println(child.getPolygon().getArea());
//				System.out.println(child.getWeight());
//				System.out.println("---------");
			}
			
		}

//		System.out.println("Elements:" + nodeList.size());
 

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		//
		int hue=342;
//		hue=180;
		Color colGreen = new Color(175,204,163,170);
//		InterpolColor colGreen=new InterpolColor(0, maxHeight+1, 0.0, hue/360.0, 1.0, 0.6,hue/360.0, 0.40);
		InterpolColor colRed=new InterpolColor(0, maxHeight+1, hue/360.0, 0.0, 1.0, hue/360.0, 0.6, 0.40);
		// draw polygon border
		InterpolColor grayGetDarker = new InterpolColor(0, maxHeight, 0, 0, 0.5, 0,
				0, 1.0);

//		InterpolColor grayGetBrighter = new InterpolColor(1, maxHeight, 0, 0, 1.0, 0,
//				0, 0.9); //white border
		InterpolColor grayGetBrighter = new InterpolColor(1, maxHeight, 0, 0, 0.4, 0,
				0, 0.6);
		
		Random rand=new Random(1);
		

//		g.setColor(new Color(90, 180, 172)); // orange
//		g.setColor(Colors.getColors().get(0));
//		g.setColor(Color.white);
		g.setColor(Color.black);
		g.setColor(colRed.getColorLinear(2));
		g.fill(rootPolygon);
		layeredPane.setSize(5000, 5000);
		layeredPane.setVisible(false);
		layeredPane.paintAll(g);
		
		
		
		int showLayouer=100;
		// fill polygon
		for (VoroNode child : nodeList) {
			PolygonSimple poly = child.getPolygon();
			
			int height=child.getHeight();
			if(height>showLayouer) continue;
			int level=Math.min(child.getHeight(), Colors.getColors().size() - 1);
			Color fillColor = Colors.getColors().get(level);
//			ACAAAA
//			fillColor=colRed.getColorLinear(height, 50);
			int index = names.indexOf(child.name);
//			System.out.println(weights.size());
//			System.out.println(index);
			System.out.println();
			System.out.println("========================================");
			System.out.println(child.name);
			System.out.println(child.getChildren());
			System.out.println("Parent: "+child.getParent().name);
//			System.out.println(child.name);
			if(weights.get(index)>=38) {
				System.out.println(child.name);
//				System.out.println(child.getWeight());
				System.out.println("color");
				System.out.println(colRed.getColorLinear(height, 50));
				System.out.println(child.getPolygon().getArea());
				System.out.println(child.getPolygon().length);
				System.out.println("----------------------------");
				fillColor=colRed.getColorLinear(height, 50);
			}
			else {
				fillColor=colGreen;
				System.out.println(child.name);
//				System.out.println(child.getWeight());
				System.out.println("color");
				System.out.println(colGreen);
				System.out.println("area");
				System.out.println(child.getPolygon().getArea());
				System.out.println(child.getPolygon().length);
				System.out.println("----------------------------");
				System.out.println("----------------------------");
			}
			
			
			g.setColor(fillColor);
			RadialGradientPaint p = getRadialGradient(poly,fillColor);
			Paint oldP = g.getPaint();
			g.setPaint(p);
			g.fillPolygon(poly.getXpointsClosed(), poly.getYpointsClosed(),
					poly.length + 1);		
			g.setPaint(oldP);
			
			g.setColor(Color.DARK_GRAY);
//			Color textCol = grayGetDarker.getColorLinear(height, 180);
			Color textCol = Color.black;
//			textCol=new Color(255,255,255,200);
			g.setColor(textCol);
			drawName(child, g);
			
		}

	//draw border in reverse order
		for (VoroNode child : nodeListReverse) {
//			if(child.getHeight()>showLayouer) continue;
			PolygonSimple poly = child.getPolygon();
//			Color col = grayScale.getColorLinear(child.getHeight());
			Color col = grayGetBrighter.getColorLinear(child.getHeight(),170);
			double width = 5* (1.0 / (child.getHeight()*child.getHeight()*1.1));
			g.setStroke(new BasicStroke((int) width));
			g.setColor(col);
					
			g.drawPolygon(poly.getXpointsClosed(), poly.getYpointsClosed(),
					poly.length + 1);
		}
		
		// fill polygon
//		for (VoroNode child : nodeList) {
//			PolygonSimple poly = child.getPolygon();
//			int height=child.getHeight();
//		
//			Point2D centroid = poly.getCentroid();
//			
//			getRadialGradient(poly, centroid);
//			g.setColor(fillColor);					
//		
//			g.fillPolygon(poly.getXpointsClosed(), poly.getYpointsClosed(),
//					poly.length + 1);						
//			
//		}

		// draw text
//		for (VoroNode child : nodeList) {
////			if(child.getHeight()<=1) continue;
//			if(child.getHeight()>2) continue;
////			if(child.getHeight()==3 && rand.nextDouble()<0.50) continue;
//						
//			 g.setColor(grayGetDarker.getColorLinear(child.getHeight(),150));
//			drawName(child,g);
//	
//		}

		
if(filename!=null){
		try {
			ImageIO.write(bufferImage, "png", new File(filename + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}

	}

	private  RadialGradientPaint getRadialGradient(PolygonSimple poly, Color color) {
		Point2D centroid = poly.getCentroid();
		java.awt.geom.Point2D.Float center = new java.awt.geom.Point2D.Float((float)centroid.x,(float)centroid.y);
		
		float radius = (float) ((poly.getBounds().getWidth()/2.0+poly.getBounds().getHeight()/2.0)/2.0);
		java.awt.geom.Point2D.Float focus = new java.awt.geom.Point2D.Float((float)centroid.x+30,(float)centroid.y+30);			
//		 float[] dist = {0.0f, 0.3f, 1.0f};
//		 Color[] colors = {Color.white, Color.gray,color};
		 float[] dist = {0.0f, 0.8f};
//		 color=color.brighter();
		 Color colorA = new Color(color.getRed(),color.getGreen(),color.getBlue(),5);
//		 colorA=new Color(255, 255, 255,90);
//		 colorA=color.brighter().brighter().brighter();
		 Color[] colors = {colorA,color};
//		 Color[] colors = {Color.white, color};
//		 RadialGradientPaint p =
//		     new RadialGradientPaint(center, radius, focus,
//		                             dist, colors,
//		                             CycleMethod.NO_CYCLE);
		 RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
		 return p;
	}

	
	
	private void drawName(VoroNode child, Graphics2D g) {
		if(!renderText) return;
		
//		if(child.getHeight()>3) return;
//		if(child.getHeight()>2) continue;
////		if(child.getHeight()==3 && rand.nextDouble()<0.50) continue;
		
		double percent=child.getPolygon().getArea()/treemap.getRootPolygon().getArea();
		
//		if(percent<0.015) return;
		
//		if(child.getParent().getChildren().size()==1) return;
		
		// draw name
		PolygonSimple poly = child.getPolygon();
		if(poly==null) return;
		
		Point2D center = poly.getCentroid();
		String name = child.getName();
		int maxChar=11;
		if(name.length()>maxChar)
			name=name.substring(0, maxChar)+"..";
		
		Font res = scaleFont(name, poly, g, g.getFont());
//		System.out.println("res");
//		System.out.println(res);
		if(res==null) return;
		g.setFont(res);
		FontMetrics fm = g.getFontMetrics(res);
		Rectangle2D bounds = fm.getStringBounds(name, g);
		double posX=(center.x-bounds.getWidth()/2.0);
		double posY=(center.y+bounds.getHeight()/4.0);
		g.drawString(name, (int) posX, (int) ((int)posY));
		
//		g.drawRect((int)(center.x-bounds.getWidth()/2.0), (int)(center.y-bounds.getHeight()/2.0), (int)bounds.getWidth(),(int)bounds.getHeight());
//		
//		g.setColor(Color.black);
//		g.drawRect((int)center.x,(int)center.y , 10, 10);
	}


	public Font scaleFont(String text, Rectangle rect, Graphics2D g, Font pFont) {
		float nextTry = 100.0f;
		Font font = pFont;

		while (true) {
			font = g.getFont().deriveFont(nextTry);
			FontMetrics fm = g.getFontMetrics(font);
			int width = fm.stringWidth(text);
			if (width <= rect.width)
				return font;
			nextTry *= .9;
		}
		// return font;
	}

	public Font scaleFont(String text, PolygonSimple poly, Graphics2D g,
			Font pFont) {
		float nextTry = 200.0f;
		Font font = pFont;
		Point2D center = poly.getCentroid();
		int count=0;
		while (true && count++<100) {
			font = g.getFont().deriveFont(nextTry);
			FontMetrics fm = g.getFontMetrics(font);
			Rectangle2D bounds = fm.getStringBounds(text, g);
			// int width=fm.stringWidth(text);
			double cx = center.x - bounds.getWidth() * 0.5;
			double cy = center.y -bounds.getHeight() * 0.5;
			Rectangle2D.Double rect = new Rectangle2D.Double(cx, cy,
					bounds.getWidth(), bounds.getHeight());
			if (poly.contains(rect))
				// if(width <= rect.width)
				return font;
			
			nextTry *= .9;
		}
		return font;
	}

	public boolean isRenderText() {
		return renderText;
	}

	public void setRenderText(boolean renderText) {
		this.renderText = renderText;
	}

}

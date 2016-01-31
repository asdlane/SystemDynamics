package de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.*;

public class SharedNodeVertexView extends VertexView{
	private static final long serialVersionUID = 1L;
	private String nodeTypeLocal;
	private static SharedNodeVertexRenderer renderer;

	/**
	 * Constructor.
	 * 
	 * @param cell constant node vertex
	 * @param nodeType 
	 */
	public SharedNodeVertexView(Object cell, String nodeType) {
		super(cell);
		nodeTypeLocal = nodeType;
		renderer = SharedNodeVertexRenderer.getInstance(nodeType);
	}

	/**
	 * Gets the perimeter point.
	 * 
	 * @param edge edge view (is ignored)
	 * @param source (is ignored)
	 * @param p other point
	 * @return perimeter point
	 */
	@Override
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		if(nodeTypeLocal == "Constant") {
			Rectangle2D r = getBounds();

			double x = r.getX() + r.getHeight() / 4;
			double y = r.getY();
			double radius = (r.getHeight() + 1) / 2;

			// x0,y0 - center of circle
			double x0 = x + radius;
			double y0 = y + radius;

			// x1, y1 - point
			double x1 = p.getX();
			double y1 = p.getY();

			// calculate straight line equation through point and circle center
			// y = d * x + h
			double dx = x1 - x0;
			double dy = y1 - y0;

			if (dx == 0) {
				return new Point((int) x0, (int) (y0 + radius * dy / Math.abs(dy)));
			}

			double d = dy / dx;
			double h = y0 - d * x0;

			// calculate intersection
			double a = radius * radius * d * d + radius * radius;
			double b = -2 * x0 * a;
			double c = radius * radius * d * d * x0 * x0 + radius * radius * x0 * x0 - Math.pow(radius, 4);

			double det = Math.sqrt(b * b - 4 * a * c);

			// two solutions (perimeter points)
			double xout1 = (-b + det) / (2 * a);
			double xout2 = (-b - det) / (2 * a);
			double yout1 = d * xout1 + h;
			double yout2 = d * xout2 + h;

			// correct solution
			double xout, yout;

			if (x1 > x0) {
				if (xout1 > x0) {
					xout = xout1;
					yout = yout1;
				} else {
					xout = xout2;
					yout = yout2;
				}
			} else {
				// x1 < x0
				if (xout1 < x0) {
					xout = xout1;
					yout = yout1;
				} else {
					xout = xout2;
					yout = yout2;
				}
			}

			return getAttributes().createPoint(xout, yout);
		}
		else if(nodeTypeLocal=="Auxiliary") {
			   Rectangle2D r = getBounds();

			      double x = r.getX();
			      double y = r.getY();
			      double radius = (r.getWidth() + 1) / 2;

			      // x0,y0 - center of circle
			      double x0 = x + radius;
			      double y0 = y + radius;
			      
			      // x1, y1 - point
			      double x1 = p.getX();
			      double y1 = p.getY();

			      // calculate straight line equation through point and circle center
			      // y = d * x + h
			      double dx = x1 - x0;
			      double dy = y1 - y0;

			      if (dx == 0) {
			         return new Point((int) x0, (int) (y0 + radius * dy / Math.abs(dy)));
			      }

			      double d = dy / dx;
			      double h = y0 - d * x0;

			      // calculate intersection
			      double a = radius * radius * d * d + radius * radius;
			      double b = -2 * x0 * a;
			      double c = radius * radius * d * d * x0 * x0 + radius * radius * x0 * x0 - Math.pow(radius, 4);

			      double det = Math.sqrt(b * b - 4 * a * c);

			      // two solutions (perimeter points)
			      double xout1 = (-b + det) / (2 * a);
			      double xout2 = (-b - det) / (2 * a);
			      double yout1 = d * xout1 + h;
			      double yout2 = d * xout2 + h;
			      
			      // correct solution
			      double xout, yout;

			      if (x1 > x0) {
			         if (xout1 > x0) {
			            xout = xout1;
			            yout = yout1;
			         } else {
			            xout = xout2;
			            yout = yout2;
			         }
			      } else {
			         // x1 < x0
			         if (xout1 < x0) {
			            xout = xout1;
			            yout = yout1;
			         } else {
			            xout = xout2;
			            yout = yout2;
			         }
			      }

			      return getAttributes().createPoint(xout, yout);
		}
		else{
				return getAttributes().createPoint(0,0);
		}
	}

	/**
	 * Gets the vertex renderer.
	 * 
	 * @return vertex renderer
	 */
	@Override
	public CellViewRenderer getRenderer() {
		return renderer;
	}
}

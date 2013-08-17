/*
 * @(#)LineAnim.java	1.13 98/09/13
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package demos.Lines;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.TextLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import AnimatingContext;
import DemoSurface;
import DemoPanel;


/**
 * Lines & Paths animation illustrating BasicStroke attributes.
 */
public class LineAnim extends DemoSurface implements AnimatingContext {

    private static int caps[] = { BasicStroke.CAP_BUTT, 
                BasicStroke.CAP_SQUARE, BasicStroke.CAP_ROUND};
    private static int joins[] = { BasicStroke.JOIN_MITER, 
                BasicStroke.JOIN_BEVEL, BasicStroke.JOIN_ROUND};
    private static Color colors[] = {Color.gray, Color.pink, Color.lightGray};
    private static BasicStroke bs1 = new BasicStroke(1.0f);
    private static final int CLOCKWISE = 0;
    private static final int COUNTERCW = 1;

    private Line2D lines[] = new Line2D[3];
    private int rAmt[] = new int[lines.length];
    private int direction[] = new int[lines.length];
    private int speed[] = new int[lines.length];
    private BasicStroke strokes[] = new BasicStroke[lines.length];
    private GeneralPath path;
    private Point2D[] pts;
    private float size;
    private Ellipse2D ellipse = new Ellipse2D.Double();


    public LineAnim() {
        setBackground(Color.white);
    }

    public void reset(int w, int h) {
        size = (w > h) ? h/6f : w/6f;
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new Line2D.Float(0,0,size,0);
            strokes[i] = new BasicStroke(size/3, caps[i], joins[i]);
            rAmt[i] = i * 360/lines.length;
            direction[i] = i%2;
            speed[i] = i + 1;
        }

        path = new GeneralPath();
        path.moveTo(size, -size/2);
        path.lineTo(size+size/2, 0);
        path.lineTo(size, +size/2);

        ellipse.setFrame(w/2-size*2-4.5f,h/2-size*2-4.5f,size*4,size*4);
        PathIterator pi = ellipse.getPathIterator(null, 0.9);
        Point2D[] points = new Point2D[100];
        int num_pts = 0;
        while ( !pi.isDone() ) {
            float[] pt = new float[6];
            switch ( pi.currentSegment(pt) ) {
                case FlatteningPathIterator.SEG_MOVETO:
                case FlatteningPathIterator.SEG_LINETO:
                    points[num_pts] = new Point2D.Float(pt[0], pt[1]);
                    num_pts++;
            }
            pi.next();
        }
        pts = new Point2D[num_pts];
        System.arraycopy(points, 0, pts, 0, num_pts);
    }


    public void step(int w, int h) {
        for (int i = 0; i < lines.length; i++) {
            if (direction[i] == CLOCKWISE) {
                rAmt[i] += speed[i];
                if (rAmt[i] == 360) {
                    rAmt[i] = 0;
                }
            } else {
                rAmt[i] -= speed[i];
                if (rAmt[i] == 0) {
                    rAmt[i] = 360;
                }
            }
        }
    }


    public void drawDemo(int w, int h, Graphics2D g2) {

        ellipse.setFrame(w/2-size,h/2-size,size*2,size*2);
        g2.setColor(Color.black);
        g2.draw(ellipse);

        for (int i = 0; i < lines.length; i++) {
            AffineTransform at = AffineTransform.getTranslateInstance(w/2,h/2);
            at.rotate(Math.toRadians(rAmt[i]));
            g2.setStroke(strokes[i]);
            g2.setColor(colors[i]);
            g2.draw(at.createTransformedShape(lines[i]));
            g2.draw(at.createTransformedShape(path));

            int j = (int) ((double) rAmt[i]/360 * pts.length);
            j = (j == pts.length) ? pts.length-1 : j;
            ellipse.setFrame(pts[j].getX(), pts[j].getY(), 9, 9);
            g2.fill(ellipse);
        }

        g2.setStroke(bs1);
        g2.setColor(Color.black);
        for (int i = 0; i < pts.length; i++) {
            ellipse.setFrame(pts[i].getX(), pts[i].getY(), 9, 9);
            g2.draw(ellipse);
        }
    }


    public static void main(String argv[]) {
        final DemoPanel dp = new DemoPanel(new LineAnim());
        Frame f = new Frame("Java2D Demo - LineAnim");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { 
                dp.surface.start(); 
            }
            public void windowIconified(WindowEvent e) { 
                dp.surface.stop(); 
            }
        });
        f.add("Center", dp);
        f.pack();
        f.setSize(new Dimension(400,300));
        f.show();
        dp.surface.start();
    }
}
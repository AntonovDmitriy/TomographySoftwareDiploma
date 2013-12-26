/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.viewSwing;

import com.antonov.tomographysoftwarediploma.impl.imageprocessing.ImageTransformator;
import com.antonov.tomographysoftwarediploma.impl.imageprocessing.Utils;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JViewport;

/**
 *
 * @author Antonov
 */
public class ImageViewerPane extends javax.swing.JFrame {

    private Point point;
    private BufferedImage initialImage;

    public ImageViewerPane() {
        initComponents();
    }

    public ImageViewerPane(BufferedImage image) {
        initComponents();
        this.image.setIcon(new ImageIcon(image));
        this.initialImage = image;
        initListeners();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imagePanel = new javax.swing.JScrollPane();
        image = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(500, 500));

        imagePanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        image.setToolTipText("");
        image.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        image.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        image.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                imageMouseWheelMoved(evt);
            }
        });
        imagePanel.setViewportView(image);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void imageMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_imageMouseWheelMoved
        // TODO add your handling code here:

        image.getIcon();

    }//GEN-LAST:event_imageMouseWheelMoved


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel image;
    private javax.swing.JScrollPane imagePanel;
    // End of variables declaration//GEN-END:variables

    private void initListeners() {

        image.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                Icon icon = image.getIcon();
                int notches = e.getWheelRotation();
                Point p = e.getPoint();
                if (notches < 0) {
                    image.setIcon(new ImageIcon(ImageTransformator.aspectZoom(initialImage, 1.1, icon.getIconWidth(),
                            icon.getIconHeight(), initialImage.getType())));
                    imagePanel.getViewport().setViewPosition(p);
                } else {
                    image.setIcon(new ImageIcon(ImageTransformator.aspectZoom(initialImage, 0.9, icon.getIconWidth(),
                            icon.getIconHeight(), initialImage.getType())));
                    imagePanel.getViewport().setViewPosition(p);
                }
            }
        });

        image.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                point = e.getPoint();
            }

        });
        image.addMouseMotionListener(new Dragger());

    }

    class Dragger extends MouseAdapter implements MouseMotionListener {

        public Point startPt;

        public void mouseMoved(MouseEvent me) {

        }

        public void mouseDragged(MouseEvent me) {

            JViewport viewPort = imagePanel.getViewport();
            Point scrollPosition = viewPort.getViewPosition();

            int dx = me.getX() - point.x;
            int dy = me.getY() - point.y;

            scrollPosition.x += dx;
            scrollPosition.y += dy;

            viewPort.setViewPosition(scrollPosition);
            point = me.getPoint();
        }

        public void mousePressed(MouseEvent me) {
            startPt = me.getPoint();
        }
    }
}

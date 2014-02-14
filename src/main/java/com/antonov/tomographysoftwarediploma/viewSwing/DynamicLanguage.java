/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.viewSwing;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DynamicLanguage {

    public static Locale[] getCommonLocales() {
        Collection<Locale> locales = new TreeSet(
                new Comparator() {

                    @Override
                    public int compare(Object o1, Object o2) {
                        return ((Locale) o1).getDisplayName((Locale) o1).compareTo(
                                ((Locale) o2).getDisplayName((Locale) o2));
                    }

                });
        for (Field field : Locale.class.getFields()) {
            if (Locale.class.isAssignableFrom(field.getType())) {
                try {
                    locales.add((Locale) field.get(null));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        locales.add(Locale.getDefault());
        return locales.toArray(new Locale[locales.size()]);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new JFileChooser());
                frame.setJMenuBar(new JMenuBar());
                JMenu menu = new JMenu("Locales");
                frame.getJMenuBar().add(menu);
                ButtonGroup group = new ButtonGroup();
                for (final Locale locale : getCommonLocales()) {
                    JCheckBoxMenuItem item = new JCheckBoxMenuItem(
                            new AbstractAction(locale.getDisplayName(locale)) {

                                private static final long serialVersionUID = 1L;

                                {
                                    this.putValue(Action.SELECTED_KEY, locale
                                            .equals(Locale.getDefault()));
                                }

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    setLocale(locale, frame);
                                }
                            });
                    group.add(item);
                    menu.add(item);
                }
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public static void setLocale(Locale locale, final Window... windows) {
        Locale.setDefault(locale);
        System.setProperty("user.language", locale.getLanguage());
        System.setProperty("user.country", locale.getCountry());
        System.setProperty("user.variant", locale.getVariant());
        try {
            /*
             * Force setting of a new instance of the current LAF.
             */
            UIManager.setLookAndFeel(UIManager.getLookAndFeel().getClass()
                    .getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (Window window : windows) {
                    SwingUtilities.updateComponentTreeUI(window);
                }
            }
        });
    }
}

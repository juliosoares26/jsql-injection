/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.action;

import java.awt.AWTKeyStroke;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.menubar.Menubar;
import com.jsql.view.swing.text.JPopupLabel;

/**
 * Keyword shortcut definition. <br>
 * - ctrl TAB: switch to next tab, <br>
 * - ctrl shift TAB: switch to previous tab, <br>
 * - ctrl W: delete tab
 */
public final class ActionHandler {
    /**
     * Utility class without constructor.
     */
    private ActionHandler() {
        //not called
    }
    
    /**
     * Select all textfield content when focused.
     */
    public static void addTextFieldShortcutSelectAll() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
            "permanentFocusOwner", 
            new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent e) {
                    if (
                        e.getNewValue() instanceof JTextField ||
                        e.getNewValue() instanceof JPopupLabel
                    ) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JTextField textField = (JTextField) e.getNewValue();
                                textField.selectAll();
                            }
                        });
                    }
                }
            }
        );
    }
    
    /**
     * Add action to a single tabbedpane (ctrl-tab, ctrl-shift-tab).
     */
    public static void addShortcut(JTabbedPane tabbedPane) {
        KeyStroke ctrlTab = KeyStroke.getKeyStroke("ctrl TAB");
        KeyStroke ctrlShiftTab = KeyStroke.getKeyStroke("ctrl shift TAB");

        // Remove ctrl-tab from normal focus traversal
        Set<AWTKeyStroke> forwardKeys = new HashSet<>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardKeys.remove(ctrlTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        // Remove ctrl-shift-tab from normal focus traversal
        Set<AWTKeyStroke> backwardKeys = new HashSet<>(tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backwardKeys.remove(ctrlShiftTab);
        tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

        // Add keys to the tab's input map
        InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(ctrlTab, "navigateNext");
        inputMap.put(ctrlShiftTab, "navigatePrevious");
    }
    
    /**
     * Add action to global root (ctrl-tab, ctrl-shift-tab, ctrl-W).
     */
    @SuppressWarnings("serial")
    public static void addShortcut(JRootPane rootPane, final JTabbedPane valuesTabbedPane) {
        Action closeTab = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (valuesTabbedPane.getTabCount() > 0) {
                    valuesTabbedPane.removeTabAt(valuesTabbedPane.getSelectedIndex());
                }
            }
        };
        
        Action nextTab = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (valuesTabbedPane.getTabCount() > 0) {
                    int selectedIndex = valuesTabbedPane.getSelectedIndex();
                    if (selectedIndex + 1 < valuesTabbedPane.getTabCount()) {
                        valuesTabbedPane.setSelectedIndex(selectedIndex + 1);
                    } else {
                        valuesTabbedPane.setSelectedIndex(0);
                    }
                }
            }
        };
        
        Action previousTab = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (valuesTabbedPane.getTabCount() > 0) {
                    int selectedIndex = valuesTabbedPane.getSelectedIndex();
                    if (selectedIndex - 1 > -1) {
                        valuesTabbedPane.setSelectedIndex(selectedIndex - 1);
                    } else {
                        valuesTabbedPane.setSelectedIndex(valuesTabbedPane.getTabCount() - 1);
                    }
                }
            }
        };
        
        Set<AWTKeyStroke> forwardKeys = new HashSet<>(rootPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardKeys.remove(KeyStroke.getKeyStroke("ctrl TAB"));
        rootPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
        
        Set<AWTKeyStroke> forwardKeys2 = new HashSet<>(rootPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        forwardKeys2.remove(KeyStroke.getKeyStroke("ctrl shift TAB"));
        rootPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, forwardKeys2);
        
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ctrl W"), "actionString-closeTab");
        actionMap.put("actionString-closeTab", closeTab);
        
        inputMap.put(KeyStroke.getKeyStroke("ctrl TAB"), "actionString-nextTab");
        actionMap.put("actionString-nextTab", nextTab);

        inputMap.put(KeyStroke.getKeyStroke("ctrl shift TAB"), "actionString-previousTab");
        actionMap.put("actionString-previousTab", previousTab);
        
        // TODO : replace directly by "for (int j = 0; j < GUIMediator.left().getTabCount(); j++)"
        int[] i = new int[MediatorGui.tabManagers().getTabCount()];
        for (int j = 0; j < MediatorGui.tabManagers().getTabCount(); j++) {
            i[j] = j + 1;
        }
        for (final int j: i) {
            inputMap.put(KeyStroke.getKeyStroke("ctrl " + j), "actionString-selectTab" + j);
            inputMap.put(KeyStroke.getKeyStroke("ctrl NUMPAD" + j), "actionString-selectTab" + j);
            actionMap.put("actionString-selectTab" + j, new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    MediatorGui.tabManagers().setSelectedIndex(j - 1);
                }
            });
        }
        
        inputMap.put(KeyStroke.getKeyStroke("ctrl S"), "actionString-saveTab");
        actionMap.put("actionString-saveTab", new ActionSaveTab());
    }

    /**
     * Create Alt shortcut to display menubar ; remove menubar when focus is set to a component.
     * @param menubar The menubar to display
     */
    public static void addShortcut(final Menubar menubar) {
        final boolean[] wasAltDPressed = {false};
        final boolean[] wasAltPressed = {false};
        
        /* Hide Menubar when focusing any component */
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
            "permanentFocusOwner", 
            new PropertyChangeListener() {
                public void propertyChange(final PropertyChangeEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (!MediatorGui.panelAddress().isExpanded) {
                                menubar.setVisible(false);
                            }
                        }
                    });
                }
            }
        );
        
        /* Show/Hide the Menubar with Alt key */
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.isAltDown() && e.getKeyCode() == (KeyEvent.VK_ALT & KeyEvent.VK_D)) {
                    MediatorGui.panelAddress().urlTextField.requestFocusInWindow();
                    MediatorGui.panelAddress().urlTextField.selectAll();
                    wasAltDPressed[0] = true;
                    return true;
                } else if (
                    e.getKeyCode() == KeyEvent.VK_ALT && 
                    e.getModifiers() == (InputEvent.ALT_MASK & KeyEvent.KEY_RELEASED)
                ) {
                    if (!wasAltDPressed[0] && !wasAltPressed[0]) {
                        if (!MediatorGui.panelAddress().isExpanded) {
                            menubar.setVisible(!menubar.isVisible());
                        }
                    } else {
                        wasAltDPressed[0] = false;
                        wasAltPressed[0] = false;
                    }
                    return true;
                } else if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_ALT) {
                    if (!MediatorGui.panelAddress().isExpanded && menubar.isVisible()) {
                        menubar.setVisible(!menubar.isVisible());
                        wasAltPressed[0] = true;
                    }
                    return true;
                }
                return false;
            }
        });
    }
}
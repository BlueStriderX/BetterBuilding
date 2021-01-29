package net.thederpgamer.betterbuilding.gui.advancedbuildmode.symmetry;

import net.thederpgamer.betterbuilding.BetterBuilding;
import net.thederpgamer.betterbuilding.gui.Advanced2dButtonPane;
import org.schema.game.client.view.gui.advanced.AdvancedGUIElement;
import org.schema.game.client.view.gui.advanced.tools.*;
import org.schema.game.client.view.gui.advancedbuildmode.AdvancedBuildModeSymmetry;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDockableDirtyInterface;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import java.util.ArrayList;

/**
 * NewAdvancedBuildModeSymmetry.java
 * Improved version of AdvancedBuildModeSymmetry that supports multiple planes of the same axis
 * ==================================================
 * Created 01/27/2021
 * @author TheDerpGamer
 */
public class NewAdvancedBuildModeSymmetry extends AdvancedBuildModeSymmetry {

    private ArrayList<Advanced2dButtonPane> buttonPanes;

    private ArrayList<SymmetryPlane> xyPlanes = new ArrayList<>();
    private ArrayList<SymmetryPlane> xzPlanes = new ArrayList<>();
    private ArrayList<SymmetryPlane> yzPlanes = new ArrayList<>();

    private boolean mirrorCubic = true;
    private boolean mirrorNonCubic = true;

    public NewAdvancedBuildModeSymmetry(AdvancedGUIElement guiElement) {
        super(guiElement);
    }

    private int getRows() {
        return Math.max(Math.max(xyPlanes.size(), xzPlanes.size()), yzPlanes.size());
    }

    @Override
    public void build(final GUIContentPane contentPane, GUIDockableDirtyInterface dockableInterface) {
        contentPane.setTextBoxHeightLast(80);

        addButton(contentPane.getContent(0), 0, 0, new ButtonResult() {
            @Override
            public GUIHorizontalArea.HButtonColor getColor() {
                return GUIHorizontalArea.HButtonColor.YELLOW;
            }

            @Override
            public ButtonCallback initCallback() {
                return new ButtonCallback() {
                    @Override
                    public void pressedLeftMouse() {
                        if(getRows() < BetterBuilding.getInstance().maxSymmetryPlanes) addRow(getRows(), contentPane);
                    }

                    @Override
                    public void pressedRightMouse() { }
                };
            }

            @Override
            public String getName() {
                return "ADD";
            }
        });
        addButton(contentPane.getContent(0), 1, 0, new ButtonResult() {
            @Override
            public GUIHorizontalArea.HButtonColor getColor() {
                return GUIHorizontalArea.HButtonColor.ORANGE;
            }

            @Override
            public ButtonCallback initCallback() {
                return new ButtonCallback() {
                    @Override
                    public void pressedLeftMouse() {
                        if(getRows() > 1) removeRow(contentPane);
                    }

                    @Override
                    public void pressedRightMouse() { }
                };
            }

            @Override
            public String getName() {
                return "REMOVE";
            }
        });

        addCheckbox(contentPane.getContent(0), 0, 1, new CheckboxResult() {

            @Override
            public CheckboxCallback initCallback() {
                return null;
            }

            @Override
            public String getName() {
                return Lng.str("Mirror cubic blocks");
            }

            @Override
            public boolean getDefault() {
                return mirrorCubic;
            }

            @Override
            public boolean getCurrentValue() {
                return mirrorCubic;
            }

            @Override
            public void setCurrentValue(boolean b) {
                setMirrorCubic(b);
            }
            @Override
            public String getToolTipText() {
                return Lng.str("Rotates cubic blocks on the other side of the plane\nto mirror the blocks you place");
            }
        });
        addCheckbox(contentPane.getContent(0), 0, 2, new CheckboxResult() {

            @Override
            public CheckboxCallback initCallback() {
                return null;
            }

            @Override
            public String getName() {
                return Lng.str("Mirror non-cubic blocks");
            }

            @Override
            public boolean getDefault() {
                return mirrorNonCubic;
            }

            @Override
            public boolean getCurrentValue() {
                return mirrorNonCubic;
            }

            @Override
            public void setCurrentValue(boolean b) {
                setMirrorNonCubic(b);
            }
            @Override
            public String getToolTipText() {
                return Lng.str("Rotates non-cubic blocks on the other side of the plane\nto mirror the blocks you place");
            }
        });
        buttonPanes = new ArrayList<>();
        addRow(0, contentPane);
    }

    private ArrayList<SymmetryPlane> getAllPlanes() {
        ArrayList<SymmetryPlane> allPlanes = new ArrayList<>();
        allPlanes.addAll(xyPlanes);
        allPlanes.addAll(xzPlanes);
        allPlanes.addAll(yzPlanes);
        return allPlanes;
    }

    private void setMirrorCubic(boolean bool) {
        this.mirrorCubic = bool;
        for(SymmetryPlane plane : getAllPlanes()) {
            plane.setMirrorCubeShapes(mirrorCubic);
        }
    }

    private void setMirrorNonCubic(boolean bool) {
        this.mirrorNonCubic = bool;
        for(SymmetryPlane plane : getAllPlanes()) {
            plane.setMirrorNonCubicShapes(mirrorNonCubic);
        }
    }

    private void addRow(final int index, GUIContentPane contentPane) {
        Advanced2dButtonPane buttonPane = new Advanced2dButtonPane(contentPane.addNewTextBox(52), 3, 2);

        buttonPane.addButton(0, 0, new NewSymmetryResult(SymmetryMode.XY));
        buttonPane.addButton(0, 1, new ButtonResult() {
            @Override
            public GUIHorizontalArea.HButtonColor getColor() {
                return GUIHorizontalArea.HButtonColor.BLUE;
            }

            @Override
            public ButtonCallback initCallback() {
                return new ButtonCallback() {
                    @Override
                    public void pressedLeftMouse() {
                        int value = xyPlanes.get(index).getExtraDist();
                        xyPlanes.get(index).setExtraDist(value == 0 ? 1 : 0);
                    }

                    @Override
                    public void pressedRightMouse() {
                    }

                };

            }

            @Override
            public String getName() {
                return Lng.str("XY [" + (index + 1) + "] ODD");
            }

            @Override
            public boolean isHighlighted() {
                return index < xyPlanes.size() && xyPlanes.get(index).getExtraDist() == 1;
            }
        });

        buttonPane.addButton(1, 0, new NewSymmetryResult(SymmetryMode.XZ));
        buttonPane.addButton(1, 1, new ButtonResult() {
            @Override
            public GUIHorizontalArea.HButtonColor getColor() {
                return GUIHorizontalArea.HButtonColor.GREEN;
            }

            @Override
            public ButtonCallback initCallback() {
                return new ButtonCallback() {
                    @Override
                    public void pressedLeftMouse() {
                        int value = xzPlanes.get(index).getExtraDist();
                        xzPlanes.get(index).setExtraDist(value == 0 ? 1 : 0);
                    }

                    @Override
                    public void pressedRightMouse() {
                    }

                };

            }

            @Override
            public String getName() {
                return Lng.str("XZ [" + (index + 1) + "] ODD");
            }

            @Override
            public boolean isHighlighted() {
                return index < xzPlanes.size() && xzPlanes.get(index).getExtraDist() == 1;
            }
        });

        buttonPane.addButton(2, 0, new NewSymmetryResult(SymmetryMode.YZ));
        buttonPane.addButton(2, 1, new ButtonResult() {
            @Override
            public GUIHorizontalArea.HButtonColor getColor() {
                return GUIHorizontalArea.HButtonColor.RED;
            }

            @Override
            public ButtonCallback initCallback() {
                return new ButtonCallback() {
                    @Override
                    public void pressedLeftMouse() {
                        int value = yzPlanes.get(index).getExtraDist();
                        yzPlanes.get(index).setExtraDist(value == 0 ? 1 : 0);
                    }

                    @Override
                    public void pressedRightMouse() {
                    }

                };
            }

            @Override
            public String getName() {
                return Lng.str("YZ [" + (index + 1) + "] ODD");
            }

            @Override
            public boolean isHighlighted() {
                return index < yzPlanes.size() && yzPlanes.get(index).getExtraDist() == 1;
            }
        });

        buttonPane.onInit();
        buttonPanes.add(buttonPane);
    }

    private void removeRow(GUIContentPane contentPane) {
        if(xyPlanes.size() == getRows()) xyPlanes.remove(getRows() - 1);
        if(xzPlanes.size() == getRows()) xzPlanes.remove(getRows() - 1);
        if(yzPlanes.size() == getRows()) yzPlanes.remove(getRows() - 1);

        Advanced2dButtonPane buttonPane = buttonPanes.get(buttonPanes.size() - 1);
        contentPane.getTextboxes().remove(contentPane.getTextboxes().top());
        buttonPane.remove();
        buttonPanes.remove(buttonPane);
    }

    @Override
    public GUIAdvButton addButton(GUIElement element, int x, int y, ButtonResult result) {
        GUIAdvButton button = new GUIAdvButton(getState(), element, result);
        addElement(button, element, x, y);
        return button;
    }

    private class NewSymmetryResult extends ButtonResult {

        private SymmetryMode symmetryMode;
        private SymmetryPlane symmetryPlane;

        public NewSymmetryResult(SymmetryMode symmetryMode) {
            this.symmetryMode = symmetryMode;
            this.symmetryPlane = new SymmetryPlane(symmetryMode);
            switch (symmetryMode) {
                case XY:
                    xyPlanes.add(symmetryPlane);
                    break;
                case XZ:
                    xzPlanes.add(symmetryPlane);
                    break;
                case YZ:
                    yzPlanes.add(symmetryPlane);
                    break;
            }
        }

        @Override
        public GUIHorizontalArea.HButtonColor getColor() {
            switch (symmetryMode) {
                case XY:
                    return GUIHorizontalArea.HButtonColor.BLUE;
                case XZ:
                    return GUIHorizontalArea.HButtonColor.GREEN;
                case YZ:
                    return GUIHorizontalArea.HButtonColor.RED;
            }

            throw new RuntimeException("Mode fail: " + symmetryMode.name());
        }

        @Override
        public ButtonCallback initCallback() {
            return new ButtonCallback() {

                @Override
                public void pressedRightMouse() {
                    symmetryPlane.setPlaceMode(false);
                }

                @Override
                public void pressedLeftMouse() {
                    if (!symmetryPlane.inPlaceMode()) {
                        if(symmetryPlane.isEnabled()) {
                            symmetryPlane.setEnabled(false);
                        } else {
                            symmetryPlane.setPlaceMode(true);
                        }
                    } else {
                        symmetryPlane.setPlaceMode(false);
                    }

                }
            };
        }

        @Override
        public String getName() {
            if (symmetryPlane.inPlaceMode()) {
                return Lng.str("*click on block*");
            } else {
                switch(symmetryMode) {
                    case XY:
                        if (symmetryPlane.isEnabled()) {
                            return Lng.str("Unset XY");
                        } else {
                            return Lng.str("XY");
                        }
                    case XZ:
                        if (symmetryPlane.isEnabled()) {
                            return Lng.str("Unset XZ");
                        } else {
                            return Lng.str("XZ");
                        }
                    case YZ:
                        if (symmetryPlane.isEnabled()) {
                            return Lng.str("Unset YZ");
                        } else {
                            return Lng.str("YZ");
                        }
                }
                throw new RuntimeException("Mode fail: " + symmetryMode.name());
            }
        }

        @Override
        public boolean isHighlighted() {
            return symmetryPlane.isEnabled();
        }
    }
}
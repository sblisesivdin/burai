/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.jmol;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.BorderPane;

import org.jmol.api.JmolViewer;

import burai.atoms.model.Cell;
import burai.atoms.viewer.AtomsViewerBase;

public class AtomsJmol extends AtomsViewerBase<BorderPane> {

    private JmolBase jmolBase;
    private SwingNode jmolNode;

    public AtomsJmol(Cell cell, double size) {
        this(cell, size, size);
    }

    public AtomsJmol(Cell cell, double width, double height) {
        super(cell, width, height);

        this.jmolBase = null;
        this.jmolNode = null;

        this.createJmolBase();
        this.createJmolNode();
        this.sceneRoot.setCenter(this.jmolNode);
    }

    private void createJmolBase() {
        this.jmolBase = new JmolBase();

        JmolViewer jmolViewer = this.jmolBase.getJmolViewer();
        if (jmolViewer != null) {
            jmolViewer.script("axes 2");
        }
    }

    private void createJmolNode() {
        this.jmolNode = new SwingNode();
        this.jmolNode.setContent(this.jmolBase);
    }

    @Override
    protected BorderPane newSceneRoot() {
        BorderPane pane = new BorderPane();
        pane.setStyle("-fx-background-color: transparent");
        return pane;
    }

    @Override
    protected void onSceneResized() {
        Platform.runLater(() -> {
            this.jmolBase.repaint();
        });
    }

    public void stopJmol() {
        this.jmolBase.stopJmolViewer();
    }
}

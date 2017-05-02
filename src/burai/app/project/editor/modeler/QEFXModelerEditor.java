/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.modeler;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import burai.app.QEFXAppComponent;
import burai.app.project.QEFXProjectController;
import burai.com.keys.PriorKeyEvent;

public class QEFXModelerEditor extends QEFXAppComponent<QEFXModelerEditorController> {

    public QEFXModelerEditor(QEFXProjectController projectController) throws IOException {
        super("QEFXModelerEditor.fxml", new QEFXModelerEditorController(projectController));

        if (this.node != null) {
            this.setupKeys(this.node);
        }
    }

    private void setupKeys(Node node) {
        if (node == null) {
            return;
        }

        node.setOnKeyPressed(event -> {
            if (event == null) {
                return;
            }

            if (PriorKeyEvent.isPriorKeyEvent(event)) {
                return;
            }

            if (event.isControlDown() && KeyCode.Z.equals(event.getCode())) {
                // Ctrl+Z
                /*
                 * TODO
                 */
            }
        });
    }
}

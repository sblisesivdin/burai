/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.input.scf;

import java.io.IOException;

import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorComponent;
import burai.input.QEInput;

public class QEFXHubbard extends QEFXEditorComponent<QEFXHubbardController> {

    public QEFXHubbard(QEFXMainController mainController, QEInput input) throws IOException {
        super("QEFXHubbard.fxml", new QEFXHubbardController(mainController, input));
    }

    @Override
    public void notifyEditorOpened() {
        // TODO 自動生成されたメソッド・スタブ
    }

}

/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body.contextmenu;

import burai.app.explorer.body.QEFXExplorerBody;
import burai.app.explorer.body.menuitem.QEFXCopyFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXDeleteFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXMakeDirectoryMenuItem;
import burai.app.explorer.body.menuitem.QEFXOpenFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXOpenTabMenuItem;
import burai.app.explorer.body.menuitem.QEFXPasteFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXRenameFileMenuItem;
import burai.app.icon.QEFXProjectIcon;
import burai.project.Project;

public class QEFXProjectContextMenu extends QEFXContextMenu<QEFXProjectIcon> {

    public QEFXProjectContextMenu(QEFXProjectIcon icon, QEFXExplorerBody body) {
        super(icon, body);
    }

    @Override
    protected void createMenuItems() {

        Project project = this.icon.getContent();
        String filePath = project == null ? null : project.getRootFilePath();
        if (filePath != null && (!filePath.isEmpty())) {
            this.getItems().add(new QEFXOpenFileMenuItem(filePath));
        }

        this.getItems().add(new QEFXOpenTabMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXRenameFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXCopyFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXPasteFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXDeleteFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXMakeDirectoryMenuItem(this.icon, this.body));
    }
}

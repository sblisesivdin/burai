/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import burai.atoms.model.Cell;

public class XSFReader extends AtomsReader {

    private boolean animation;

    public XSFReader(String filePath, boolean animation) throws FileNotFoundException {
        super(filePath);
        this.animation = animation;
    }

    public XSFReader(File file, boolean animation) throws FileNotFoundException {
        super(file);
        this.animation = animation;
    }

    @Override
    public Cell readCell() throws IOException {
        if (this.reader == null) {
            return null;
        }

        // TODO

        return null;
    }
}

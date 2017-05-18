/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.modeler;

import java.util.ArrayList;
import java.util.List;

import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.com.math.Matrix3D;

public class SlabModelBuilder {

    private static final double DET_THR = 1.0e-8;

    private Cell cell;

    private int miller1;
    private int miller2;
    private int miller3;

    private int intercept1;
    private int intercept2;
    private int intercept3;
    private int numIntercept;
    private boolean hasIntercept1;
    private boolean hasIntercept2;
    private boolean hasIntercept3;

    private int[] vector1;
    private int[] vector2;
    private int[] vector3;

    private int aMax;
    private int aMin;
    private int bMax;
    private int bMin;
    private int cMax;
    private int cMin;

    private String[] names;
    private double[][] coords;

    private List<String> nameList;
    private List<double[]> coordList;

    protected SlabModelBuilder(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
    }

    protected boolean build(int h, int k, int l) {
        if (!this.setupMillers(h, k, l)) {
            return false;
        }

        if (!this.setupAtoms()) {
            return false;
        }

        if (!this.packAtoms()) {
            return false;
        }

        // TODO

        return true;
    }

    private boolean setupAtoms() {
        Atom[] atoms = this.cell.listAtoms(true);
        if (atoms == null || atoms.length < 1) {
            return false;
        }

        this.names = new String[atoms.length];
        this.coords = new double[atoms.length][];

        for (int i = 0; i < atoms.length; i++) {
            Atom atom = atoms[i];
            if (atom == null) {
                return false;
            }
            this.names[i] = atom.getName();
            this.coords[i] = this.cell.convertToLatticePosition(atom.getX(), atom.getY(), atom.getZ());
        }

        return true;
    }

    private boolean packAtoms() {
        if (this.names.length != this.coords.length) {
            return false;
        }

        double[][] lattice = new double[3][];
        lattice[0] = new double[] { (double) this.vector1[0], (double) this.vector1[1], (double) this.vector1[2] };
        lattice[1] = new double[] { (double) this.vector2[0], (double) this.vector2[1], (double) this.vector2[2] };
        lattice[2] = new double[] { (double) this.vector3[0], (double) this.vector3[1], (double) this.vector3[2] };

        double detLatt = Matrix3D.determinant(lattice);
        if (Math.abs(detLatt) < DET_THR) {
            return false;
        }

        double[][] invLatt = Matrix3D.inverse(lattice);
        if (invLatt == null || invLatt.length < 3) {
            return false;
        }
        if (invLatt[0] == null || invLatt[0].length < 3) {
            return false;
        }
        if (invLatt[1] == null || invLatt[1].length < 3) {
            return false;
        }
        if (invLatt[2] == null || invLatt[2].length < 3) {
            return false;
        }

        int natom = this.names.length;
        this.nameList = new ArrayList<String>();
        this.coordList = new ArrayList<double[]>();

        for (int ia = this.aMin; ia <= this.aMax; ia++) {
            double ta = (double) ia;
            for (int ib = this.bMin; ib <= this.bMax; ib++) {
                double tb = (double) ib;
                for (int ic = this.cMin; ic <= this.cMax; ic++) {
                    double tc = (double) ic;

                    String name = null;
                    double[] coord = new double[3];
                    for (int iatom = 0; iatom < natom; iatom++) {
                        name = this.names[iatom];
                        coord[0] = this.coords[iatom][0] + ta;
                        coord[1] = this.coords[iatom][1] + tb;
                        coord[2] = this.coords[iatom][2] + tc;
                        double f1 = coord[0] * invLatt[0][0] + coord[1] * invLatt[1][0] + coord[2] * invLatt[2][0];
                        double f2 = coord[0] * invLatt[0][1] + coord[1] * invLatt[1][1] + coord[2] * invLatt[2][1];
                        double f3 = coord[0] * invLatt[0][2] + coord[1] * invLatt[1][2] + coord[2] * invLatt[2][2];
                        if (0.0 <= f1 && f1 < 1.0 && 0.0 <= f2 && f2 < 1.0 && 0.0 <= f3 && f3 < 1.0) {
                            this.nameList.add(name);
                            this.coordList.add(new double[] { f1, f2, f3 });
                        }
                    }
                }
            }
        }

        if (this.nameList.isEmpty() || this.coordList.isEmpty()) {
            return false;
        }

        return true;
    }

    private boolean setupMillers(int h, int k, int l) {
        if (h == 0 && k == 0 && l == 0) {
            return false;
        }

        this.miller1 = h;
        this.miller2 = k;
        this.miller3 = l;

        try {
            this.setupIntercepts();
        } catch (RuntimeException e) {
            return false;
        }

        this.setupVectors();

        this.setupBox();

        return true;
    }

    private void setupIntercepts() throws RuntimeException {
        int scaleMin = 0;
        int scaleMax = 0;
        this.numIntercept = 0;

        if (this.miller1 != 0) {
            scaleMin = Math.max(scaleMin, Math.abs(this.miller1));
            scaleMax *= Math.abs(this.miller1);
            this.numIntercept++;
            this.hasIntercept1 = true;
        } else {
            this.hasIntercept1 = false;
        }

        if (this.miller2 != 0) {
            scaleMin = Math.max(scaleMin, Math.abs(this.miller2));
            scaleMax *= Math.abs(this.miller2);
            this.numIntercept++;
            this.hasIntercept2 = true;
        } else {
            this.hasIntercept2 = false;
        }

        if (this.miller3 != 0) {
            scaleMin = Math.max(scaleMin, Math.abs(this.miller3));
            scaleMax *= Math.abs(this.miller3);
            this.numIntercept++;
            this.hasIntercept3 = true;
        } else {
            this.hasIntercept3 = false;
        }

        if (scaleMin < 1) {
            throw new RuntimeException("scaleMin is not positive.");
        }

        if (scaleMax < scaleMin) {
            throw new RuntimeException("scaleMax < scaleMin.");
        }

        if (this.numIntercept < 1) {
            throw new RuntimeException("there are no intercepts.");
        }

        int scale = 0;
        for (int i = scaleMin; i <= scaleMax; i++) {
            if (this.hasIntercept1 && (i % this.miller1) != 0) {
                continue;
            }
            if (this.hasIntercept2 && (i % this.miller2) != 0) {
                continue;
            }
            if (this.hasIntercept3 && (i % this.miller3) != 0) {
                continue;
            }

            scale = i;
            break;
        }

        if (scale < 1) {
            throw new RuntimeException("cannot detect scale.");
        }

        this.intercept1 = scale / this.miller1;
        this.intercept2 = scale / this.miller2;
        this.intercept3 = scale / this.miller3;
    }

    private void setupVectors() {
        this.vector1 = new int[] { 0, 0, 0 };
        this.vector2 = new int[] { 0, 0, 0 };
        this.vector3 = new int[] { 0, 0, 0 };

        if (this.numIntercept <= 1) {
            this.setupVectors1();
        } else if (this.numIntercept <= 2) {
            this.setupVectors2();
        } else {
            this.setupVectors3();
        }
    }

    private void setupVectors1() {
        if (this.hasIntercept1) {
            if (this.intercept1 > 0) {
                this.vector1[1] = 1;
                this.vector2[2] = 1;
                this.vector3[0] = 1;
            } else {
                this.vector1[2] = 1;
                this.vector2[1] = 1;
                this.vector3[0] = -1;
            }

        } else if (this.hasIntercept2) {
            if (this.intercept2 > 0) {
                this.vector1[2] = 1;
                this.vector2[0] = 1;
                this.vector3[1] = 1;
            } else {
                this.vector1[0] = 1;
                this.vector2[2] = 1;
                this.vector3[1] = -1;
            }

        } else if (this.hasIntercept3) {
            if (this.intercept3 > 0) {
                this.vector1[0] = 1;
                this.vector2[1] = 1;
                this.vector3[2] = 1;
            } else {
                this.vector1[1] = 1;
                this.vector2[0] = 1;
                this.vector3[2] = -1;
            }
        }
    }

    private void setupVectors2() {
        if (!this.hasIntercept3) { // cat in A-B plane
            int sign1 = Integer.signum(this.intercept1);
            int sign2 = Integer.signum(this.intercept2);
            this.vector1[2] = sign1 * sign2;
            this.vector2[0] = +this.intercept1;
            this.vector2[1] = -this.intercept2;
            this.vector3[0] = sign1;
            this.vector3[1] = sign2;

        } else if (!this.hasIntercept2) { // cat in A-C plane
            int sign1 = Integer.signum(this.intercept1);
            int sign3 = Integer.signum(this.intercept3);
            this.vector1[1] = sign1 * sign3;
            this.vector2[0] = -this.intercept1;
            this.vector2[2] = +this.intercept3;
            this.vector3[0] = sign1;
            this.vector3[2] = sign3;

        } else if (!this.hasIntercept1) { // cat in B-C plane
            int sign2 = Integer.signum(this.intercept2);
            int sign3 = Integer.signum(this.intercept3);
            this.vector1[0] = sign2 * sign3;
            this.vector2[1] = +this.intercept2;
            this.vector2[2] = -this.intercept3;
            this.vector3[1] = sign2;
            this.vector3[2] = sign3;
        }
    }

    private void setupVectors3() {
        int sign1 = Integer.signum(this.intercept1);
        int sign2 = Integer.signum(this.intercept2);
        int sign3 = Integer.signum(this.intercept3);
        this.vector1[1] = +this.intercept2;
        this.vector1[2] = -this.intercept3;
        this.vector2[0] = -this.intercept1;
        this.vector2[2] = +this.intercept3;
        this.vector3[0] = sign1;
        this.vector3[1] = sign2;
        this.vector3[2] = sign3;
    }

    private void setupBox() {
        this.aMax = 0;
        this.aMin = 0;

        if (this.vector1[0] > 0) {
            aMax += this.vector1[0];
        } else {
            aMin += this.vector1[0];
        }

        if (this.vector2[0] > 0) {
            aMax += this.vector2[0];
        } else {
            aMin += this.vector2[0];
        }

        if (this.vector3[0] > 0) {
            aMax += this.vector3[0];
        } else {
            aMin += this.vector3[0];
        }

        this.bMax = 0;
        this.bMin = 0;

        if (this.vector1[1] > 0) {
            bMax += this.vector1[1];
        } else {
            bMin += this.vector1[1];
        }

        if (this.vector2[1] > 0) {
            bMax += this.vector2[1];
        } else {
            bMin += this.vector2[1];
        }

        if (this.vector3[1] > 0) {
            bMax += this.vector3[1];
        } else {
            bMin += this.vector3[1];
        }

        this.cMax = 0;
        this.cMin = 0;

        if (this.vector1[2] > 0) {
            cMax += this.vector1[2];
        } else {
            cMin += this.vector1[2];
        }

        if (this.vector2[2] > 0) {
            cMax += this.vector2[2];
        } else {
            cMin += this.vector2[2];
        }

        if (this.vector3[2] > 0) {
            cMax += this.vector3[2];
        } else {
            cMin += this.vector3[2];
        }
    }
}

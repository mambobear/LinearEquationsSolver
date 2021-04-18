package solver;

import java.util.ArrayList;
import java.util.Arrays;

public class Matrix {
    private final Row[] matrix;
    private final int nRows;
    private final int nColumns;

    Matrix(Complex[][] array) {
        this.nRows = array.length;
        this.nColumns = array[0].length;
        matrix = new Row[this.nRows];
        for (int row = 0; row < nRows; row++) {
            matrix[row] = new Row(array[row]);
        }
    }

    public Solution solve() {
        int currentRow = 0;
        int currentCol = 0;
        ArrayList<Coordinate> pivots = new ArrayList<>();
        while (currentCol < this.nColumns - 1 && currentRow < this.nRows) {
            int pivot = findPivotFor(currentRow, currentCol);
            if (pivot != -1)  {
                matrix[pivot].multiplyBy((new Complex(1.0, 0)).divideBy(getElementAt(pivot, currentCol)));
                clearElementBelowPivot(pivot, currentCol);
                if (pivot != currentRow) this.switchRows(currentRow, pivot);
                pivots.add(new Coordinate(currentRow, currentCol));
                currentRow++;
            }
            currentCol++;
        }

        int rank = pivots.size();

        // clear elements above pivots
        clearElementAbovePivots(pivots);

        Complex[] lastCol = new Complex[nRows];
        for (int r = 0; r < nRows; r++) {
            lastCol[r] = matrix[r].getElementAt(nColumns - 1);
        }

        // matrix is square
        if (nRows == nColumns - 1) {
            // full rank
            if (rank == nRows) {
                return new Solution(lastCol, 1);
                // rank smaller that matrix size
            } else {
                if (isColInColumnSpace(lastCol, rank)) return new Solution(null, Integer.MAX_VALUE);
                else return new Solution(null, 0);
            }
        }

        if (nRows < nColumns - 1) {
            if (rank == nRows) {
                return new Solution(null, Integer.MAX_VALUE);
            } else {
                // rank < nRows
                if (isColInColumnSpace(lastCol, rank)) return new Solution(null, Integer.MAX_VALUE);
                else return new Solution(null, 0);
            }
        }

        if (nRows > nColumns - 1) {
            boolean isInColSpace = isColInColumnSpace(lastCol, rank);
            if (!isInColSpace) return new Solution(null, 0);

            // last col is in column space
            if (rank == nColumns - 1) {
                return new Solution(Arrays.copyOf(lastCol, rank), 1);
            } else {
                return new Solution(null, Integer.MAX_VALUE);
            }
        }
        return null;
    }

    private Complex getElementAt(int pivot, int currentCol) {
        return this.matrix[pivot].getElementAt(currentCol);
    }

    private boolean isColInColumnSpace(Complex[] lastCol, int rank) {

        for (int i = rank; i < lastCol.length; i++) {
            if (!lastCol[i].equal(0)) {
                return false;
            }
        }
        return true;
    }

    private void clearElementAbovePivots(ArrayList<Coordinate> pivots) {
        while (!pivots.isEmpty()) {
            Coordinate pivot = pivots.remove(pivots.size() - 1);
            int pRow = pivot.getRow();
            int pCol = pivot.getCol();
            for (int r = pRow - 1; r >= 0; r--) {
                Complex element = matrix[r].getElementAt(pCol);
                if (!element.equal(0)) addRowMultiple(pRow, r, element.multiply(-1));
            }
        }
    }

    // element at (pivot, currentCol) is assumed to be 1
    private void clearElementBelowPivot(int pivot, int currentCol) {
        for (int r = pivot + 1; r < nRows; r++) {
            Complex element = matrix[r].getElementAt(currentCol);
            if (!element.equal(0)) addRowMultiple(pivot, r, element.multiply(-1));
        }
    }

    private int findPivotFor(int row, int col) {
        for (int r = row; r < matrix.length; r++)  {
            if (!matrix[r].getElementAt(col).equal(0.0)) return r;
        }
        return -1;
    }

    private void switchRows(int i, int j) {
        Row temp = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = temp;
    }

    private void addRowMultiple(int fromRow, int toRow, Complex factor) {
        Row from = matrix[fromRow].copyRow();
        from.multiplyBy(factor);
        matrix[toRow].addRow(from);
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        for (Row r : matrix) {
            strb.append(r.toString()).append("\n");
        }
        return strb.toString();
    }
}

class Row {

    final Complex[] row;
    Row(Complex[] row) {
        this.row = Arrays.copyOf(row, row.length);
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        strb.append("| ");
        for (int i = 0; i < row.length; i++) {
            strb.append(String.format("%s", row[i]));
            if (i < row.length - 1) strb.append("\t");
        }
        strb.append(" |");
        return strb.toString();
    }

    public void multiplyBy(Complex d) {
        for (int i = 0; i < row.length; i++) {
            row[i] = row[i].multiply(d);
        }
    }

    public void addRow(Row row) {
        for (int i = 0; i < this.row.length; i++) {
            this.row[i] = this.row[i].add(row.elementAt(i));
        }
    }

    private Complex elementAt(int i) {
        if (i < 0 || i >= row.length) throw new IllegalArgumentException("invalid row index");
        return row[i];
    }

    public Complex getElementAt(int col) {
        return row[col];
    }

    public Row copyRow() {
        return new Row(Arrays.copyOf(this.row, this.row.length));
    }
}

class Coordinate {
    int row;
    int col;
    Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}

class Solution {
    private final Complex[] solution;
    private final int numberOfSolutions;

    Solution(Complex[] solution, int numberOfSolutions) {
        this.solution = solution;
        this.numberOfSolutions = numberOfSolutions;
    }

    public Complex[] getSolution() {
        return solution;
    }

    public int getNumberOfSolutions() {
        return numberOfSolutions;
    }
}

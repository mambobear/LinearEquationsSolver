package solver;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));
        String inFileName = args[argList.indexOf("-in") + 1];
        String outFileName = args[argList.indexOf("-out") + 1];

        Complex[][] array = readArrayFromFile(inFileName);
        if (array == null) {
            writeSolutionToFile(new Solution(null, 0), outFileName);
            //System.out.println("Could not read data from file");
            return;
        }
        Matrix matrix = new Matrix(array);
        System.out.println("Matrix:");
        System.out.println(matrix);
        Solution solution = matrix.solve();

        writeSolutionToFile(solution, outFileName);
    }

    private static Complex[][] readArrayFromFile(String inFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inFileName))) {

            String[] dimStr = reader.readLine().split("\\s+");
            int nCols = Integer.parseInt(dimStr[0]);
            int nRows = Integer.parseInt(dimStr[1]);

            if (nRows == 0 || nCols == 0) return null;

            Complex[][] array = new Complex[nRows][nCols+1];
            for (int row = 0; row < nRows; row++) {
                String[] rowStr = reader.readLine().split("\\s+");
                for (int col = 0; col <= nCols; col++) {
                    array[row][col] = parseComplex(rowStr[col]);
                    // System.out.println(array[row][col]);
                }
            }
            return array;
        } catch(FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Complex parseComplex(String str) {
        if (str.charAt(str.length() - 1) != 'i') {
            return new Complex(Double.parseDouble(str), 0);
        }
        if ("i".equals(str)) return new Complex(0, 1);
        if ("-i".equals(str)) return new Complex(0, -1);

        // (±a)?±bi where b != 1, (±a)?±i
        // extract leading sign if any
        int ch = -1;
        if (str.charAt(0) == '+' || str.charAt(0) == '-') {
            ch = str.charAt(0);
            str = str.substring(1);
        }

        // split on the inner (if any) ±
        boolean imaginaryNegative = str.contains("-");
        String[] parts = str.split("[+-]");
        if (ch != -1) parts[0] = (char)ch + parts[0];

        double real;
        double imaginary;
        if (parts.length == 1) {
            // only imaginary part preset
            imaginary = imaginaryValueOf(parts[0]);
            return new Complex(0, imaginary);
        }
        if (parts.length == 2) {
            // composite
            real = Double.parseDouble(parts[0]);
            imaginary = imaginaryValueOf(parts[1]);
            if (imaginaryNegative) imaginary *= -1;
            return new Complex(real, imaginary);
        }

        return null;
    }

    private static double imaginaryValueOf(String part) {
        assert part.charAt(part.length() - 1) == 'i';
        if ("i".equals(part)) return 1;
        else if ("-i".equals(part)) return  -1;
        else return Double.parseDouble(part.substring(0,part.length() - 1));
    }

    private static void writeSolutionToFile(Solution solution, String outFileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName))) {
            if (solution.getSolution() == null) {
                if (solution.getNumberOfSolutions() == 0) {
                    writer.write("No solutions");
                } else {
                    writer.write("Infinitely many solutions");
                }
            } else {
                for (Complex z : solution.getSolution()) {
                    writer.write(String.format("%s\n", z));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
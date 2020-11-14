package edu.spbu.matrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Разряженная матрица
 */
public class SparseMatrix implements Matrix {
    int rows;
    int cols;
    // Массив ненулевых значений матрицы
    ArrayList<Integer> A = new ArrayList<>();
    // Массив индексов столбцов: хранит номера столбцов, соответствующих элементов из массива значений.
    ArrayList<Integer> LJ = new ArrayList<>();
    // Массив индексации строк: хранит местоположение (индекс в массиве А) первого  ненулевого  элемента
    // в  каждой  строке. (Количество ненулевых элементов до i-той строчки)
    ArrayList<Integer> LI = new ArrayList<>();
    /**
     * загружает матрицу из файла
     *
     * @param fileName
     */
    public SparseMatrix(String fileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader((fileName)));
        int cols = reader.readLine().split(" ").length;
        int rows = 1;
        while (reader.readLine() != null) {
            rows++;
        }
        reader.close();

        this.LI.add(0);

        Scanner scanner = new Scanner(new File(fileName));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (scanner.hasNextInt()) {
                    int x = scanner.nextInt();
                    if (x != 0) {
                        this.A.add(x);
                        this.LJ.add(j);
                    }
                }
            }
            this.LI.add(this.A.size());
        }
        scanner.close();

        this.rows = rows;
        this.cols = cols;
    }

    public SparseMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.LI.add(0);
    }


    public void transposeMatrix() {
        // массив векторов содержащих номера столбцов ненулевых значений для каждой строки
        ArrayList<Integer>[] rowIndexVectors = new ArrayList[cols];
        for (int i = 0; i < cols; i++)
            rowIndexVectors[i] = new ArrayList<>();
        
        // сами значения сопоставленные rowIndexVectors
        ArrayList<Integer>[] valueVectors = new ArrayList[cols];
        for (int i = 0; i < cols; i++)
            valueVectors[i] = new ArrayList<>();
        
        // l - номер первой непустой строки
        int l = 0;
        for (int i = 0; LI.get(i).equals(LI.get(i + 1)); i++, l++);
        
        // k идет по массиву A всех ненулевых элемнтов.
        // i номер строки в матрице, номер столбца в транспонированной матрице.
        for (int i = l, k = 0; k < A.size(); k++) {
            rowIndexVectors[LJ.get(k)].add(i);
            valueVectors[LJ.get(k)].add(A.get(k));
            // проверка последней итерации выходящей за массив
            if (k != A.size() - 1) {
                // если осуществляется переход на следующую строчку
                if (LJ.get(k) >= LJ.get(k + 1)) {
                    // если строчка непустая
                    if (!LI.get(i + 2).equals(LI.get(i + 1))) {
                        i++;
                    } else {
                        i += 2;
                    }
                }
            }
        }

        A.clear();
        LJ.clear();
        LI.clear();
        LI.add(0);
        
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rowIndexVectors[i].size(); j++) {
                A.add(valueVectors[i].get(j));
                LJ.add(rowIndexVectors[i].get(j));
            }
            LI.add(LI.get(i) + rowIndexVectors[i].size());
        }

        int t = rows;
        rows = cols;
        cols = t;
    }

    /**
     * однопоточное умнджение матриц
     * должно поддерживаться для всех 4-х вариантов
     *
     * @param o
     * @return res
     */
    @Override
    public Matrix mul(Matrix o) {

        if (o instanceof SparseMatrix) {
            SparseMatrix res = new SparseMatrix(rows, ((SparseMatrix) o).cols);
            SparseMatrix so = (SparseMatrix) o;
            so.transposeMatrix();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < so.rows; j++) {
                    int sum = 0;
                    // индексы идущие в массивах индексов столбцов для каждой строчки
                    int r1 = 0, r2 = 0;
                    while (r1 < LI.get(i + 1) - LI.get(i) && r2 < so.LI.get(j + 1) - so.LI.get(j)) {
                        if (LJ.get(LI.get(i) + r1) == so.LJ.get(so.LI.get(j) + r2)) {
                            sum += A.get(LI.get(i) + r1) * so.A.get(so.LI.get(j) + r2);
                            r1++;
                            r2++;
                        } else if (LJ.get(LI.get(i) + r1) > so.LJ.get(so.LI.get(j) + r2)) {
                            r2++;
                        } else {
                            r1++;
                        }
                    }
                    if (sum != 0) {
                        res.A.add(sum);
                        res.LJ.add(j);
                    }
                }
                res.LI.add(res.A.size());
            }
            return res;
        }
        else {
            SparseMatrix res = new SparseMatrix(rows, ((DenseMatrix)o).cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < ((DenseMatrix) o).cols; j++) {
                    int sum = 0;
                    for (int s = 0; s < LI.get(i + 1) - LI.get(i); s++) {
                        sum += A.get(LI.get(i) + s) * ((DenseMatrix) o).m[LJ.get(LI.get(i) + s)][j];
                    }
                    if (sum != 0) {
                        res.A.add(sum);
                        res.LJ.add(j);
                    }
                }
                res.LI.add(res.A.size());
            }
            return res;
        }
    }

    /**
     * многопоточное умножение матриц
     *
     * @param o
     * @return
     */
    @Override
    public Matrix dmul(Matrix o) {
        return null;
    }

    /**
     * спавнивает с обоими вариантами
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof SparseMatrix) {
            for (int i = 0; i < A.size(); i++) {
                if (!A.get(i).equals(((SparseMatrix)o).A.get(i)) || !LJ.get(i).equals(((SparseMatrix)o).LJ.get(i))) {
                    return false;
                }
            }
            for (int i = 1; i < rows; i++) {
                if (!LI.get(i).equals(((SparseMatrix) o).LI.get(i))) {
                    return false;
                }
            }
        }
        else {
            for (int i = 0, k = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (((DenseMatrix)o).m[i][j] != 0) {
                        if (((DenseMatrix)o).m[i][j] != A.get(k)) {
                            return false;
                        }
                        k++;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String matrixToString() {
        String str = new String();
        for (int i = 0; i < rows; i++) {
            if (!LI.get(i).equals(LI.get(i + 1))) {
                for (int j = 0; j < LJ.get(LI.get(i)); j++) {
                    for (int t = 0; t < 8; t++) str += " ";
                    str += "0 ";
                }
                for (int s = 0; s < LI.get(i + 1) - LI.get(i) - 1; s++) {
                    for (int k = LJ.get(LI.get(i) + s); k < LJ.get(LI.get(i) + s + 1); k++) {
                        if (k == LJ.get(LI.get(i) + s)) {
                            for (int t = 0; t < 9 - String.valueOf(A.get(LI.get(i) + s)).length(); t++) str += " ";
                            str += A.get(LI.get(i) + s) + " ";
                        }
                        else {
                            for (int t = 0; t < 8; t++) str += " ";
                            str += "0 ";
                        }
                    }
                }
                for (int t = 0; t < 9 - String.valueOf(A.get(LI.get(i + 1) - 1)).length(); t++) str += " ";
                str += A.get(LI.get(i + 1) - 1) + " ";
                for (int j = 0; j < rows - 1 - LJ.get(LI.get(i + 1) - 1); j++) {
                    for (int t = 0; t < 8; t++) str += " ";
                    str += "0 ";
                }
            }
            else {
                for (int j = 0; j < cols; j++) {
                    for (int t = 0; t < 8; t++) str += " ";
                    str += "0 ";
                }
            }
            str += "\n";
        }
        return str;
    }
}

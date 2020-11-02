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
    int size;
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
        this.size = reader.readLine().split(" ").length;
        reader.close();

        this.LI.add(0);

        Scanner scanner = new Scanner(new File(fileName));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
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
    }

    public SparseMatrix(int size) {
        this.size = size;
        this.LI.add(0);
    }


    public void transposeMatrix() {
        // массив векторов содержащих номера столбцов ненулевых значений для каждой строки
        ArrayList<Integer>[] rowIndexVectors = new ArrayList[size];
        for (int i = 0; i < size; i++)
            rowIndexVectors[i] = new ArrayList<>();
        
        // сами значения сопоставленные rowIndexVectors
        ArrayList<Integer>[] valueVectors = new ArrayList[size];
        for (int i = 0; i < size; i++)
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
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < rowIndexVectors[i].size(); j++) {
                A.add(valueVectors[i].get(j));
                LJ.add(rowIndexVectors[i].get(j));
            }
            LI.add(LI.get(i) + rowIndexVectors[i].size());
        }
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
//        SparseMatrix res = new SparseMatrix(size);
//        if (o instanceof SparseMatrix) {
//            SparseMatrix oT = (SparseMatrix) ((SparseMatrix) o).transposeMatrix();
//            for (int k = 0, i = 0; i < size; i++)
//                for (int j = 0; j < size; j++) {
//                    int sum = 0;
//                    int r1 = 0, r2 = 0;
//                    res.LI[i] = k;
//                    while (r1 < LI[i + 1] || r2 < oT.LI[j + 1]) {
//                        if (LJ[LI[i] + r1] == oT.LJ[oT.LI[j] + r2]) {
//                            sum += A[LI[i] + r1] * oT.A[oT.LI[j] + r2];
//                            r1++;
//                            r2++;
//                        } else if (LJ[LI[i] + r1] > oT.LJ[oT.LI[j] + r2]) {
//                            r2++;
//                        } else {
//                            r1++;
//                        }
//                    }
//                    if (sum != 0) {
//                        res.A[k] = sum;
//                        res.LJ[k] = j;
//                        res.LI[i + 1]++;
//                        k++;
//                    }
//                }
//        }
//        else {
//            for (int k = 0, i = 0; i < size; i++)
//                for (int j = 0; j < size; j++) {
//                    int sum = 0;
//                    res.LI[i] = k;
//                    for (int s = 0; s < LI[i+1]; s++) {
//                        sum += A[LJ[LI[j]+s]] * ((DenseMatrix)o).m[LJ[LI[j]+s]][j];
//                    }
//                    if (sum != 0) {
//                        res.A[k] = sum;
//                        res.LJ[k] = j;
//                        res.LI[i + 1]++;
//                        k++;
//                    }
//                }
//        }
//        return res;
        return null;
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
            for (int i = 1; i < size; i++) {
                if (!LI.get(i).equals(((SparseMatrix) o).LI.get(i))) {
                    return false;
                }
            }
        }
        else {
            for (int i = 0, k = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
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
        for (int i = 0; i < size; i++) {
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
                for (int j = 0; j < size - 1 - LJ.get(LI.get(i + 1) - 1); j++) {
                    for (int t = 0; t < 8; t++) str += " ";
                    str += "0 ";
                }
            }
            else {
                for (int j = 0; j < size; j++) {
                    for (int t = 0; t < 8; t++) str += " ";
                    str += "0 ";
                }
            }
            str += "\n";
        }
        return str;
    }
}

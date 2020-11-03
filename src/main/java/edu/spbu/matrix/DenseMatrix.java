package edu.spbu.matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix
{
  int size;
  int[][] m;
  /**
   * загружает матрицу из файла
   * @param fileName
   */
  public DenseMatrix(String fileName) throws Exception {
    BufferedReader reader = new BufferedReader(new FileReader((fileName)));
    int size = reader.readLine().split(" ").length;
    reader.close();

    int[][] m = new int[size][size];
    Scanner scanner = new Scanner(new FileReader(fileName));

    for (int i = 0; i < size; i++)
      for (int j = 0; j < size; j++) {
        if (scanner.hasNext()) {
          if (scanner.hasNextInt()) {
            m[i][j] = scanner.nextInt();
          }
        }
      }
    scanner.close();

    this.size = size;
    this.m = m;
  }

  public DenseMatrix(int size){
    this.size = size;
    this.m = new int[size][size];
  }

  public SparseMatrix DenseToSparse() {
    SparseMatrix SM = new SparseMatrix(size);

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (m[i][j] != 0) {
          SM.A.add(m[i][j]);
          SM.LJ.add(j);
        }
      }
      SM.LI.add(SM.A.size());
    }
    return SM;
  }

  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return res
   */
  @Override public Matrix mul(Matrix o)
  {
    /*
    * Умножение плотной матрицы на плотную
    * DM x DM
    * */
    if (o instanceof DenseMatrix) {
      DenseMatrix res = new DenseMatrix(size);
      for (int i = 0; i < size; i++)
        for (int j = 0; j < size; j++) {
          int sum = 0;
          for (int k = 0; k < size; k++) {
            sum += m[i][k] * ((DenseMatrix) o).m[k][j];
            res.m[i][j] = sum;
          }
        }
      return res;
    }
    /*
    * Умножение плотной матрицы на разряженную
    * DM x SM
    * */
    else {
      SparseMatrix res = new SparseMatrix(size);

      ((SparseMatrix)o).transposeMatrix();

      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          int sum = 0;
          // s идет по всем ненулевым элементам в транспонированной матрице в j-той строчке
          for (int s = 0; s < ((SparseMatrix)o).LI.get(j + 1) - ((SparseMatrix)o).LI.get(j); s++) {
            sum += m[i][((SparseMatrix)o).LJ.get(((SparseMatrix)o).LI.get(j) + s)] * ((SparseMatrix)o).A.get(((SparseMatrix)o).LI.get(j) + s);
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
  @Override public Matrix dmul(Matrix o)
  {
    return null;
  }

  /**
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
  @Override public boolean equals(Object o) {

    if (o instanceof DenseMatrix) {
      for (int i = 0; i < size; i++)
        for (int j = 0; j < size; j++) {
          if (m[i][j] != ((DenseMatrix)o).m[i][j]){
            return false;
          }
        }
    }
    else {
      for (int i = 0, k = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          if (m[i][j] != 0) {
            if (m[i][j] != ((SparseMatrix)o).A.get(k)) {
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
    String s= new String();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        for (int k = 0; k < 6 - String.valueOf(m[i][j]).length(); k++) {
          s += " ";
        }
        s += String.valueOf(m[i][j]);
        s += " ";
        //if (m[i][j] >= 0) s += " ";
      }
      s += "\n";
    }
    return s;
  }

}

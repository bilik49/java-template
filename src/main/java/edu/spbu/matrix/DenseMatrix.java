package edu.spbu.matrix;

import edu.spbu.MatrixGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix
{
  int size = MatrixGenerator.SIZE;
  int m[][] = new int[size][size];
  /**
   * загружает матрицу из файла
   * @param fileName
   */
  public DenseMatrix(String fileName) {
    Scanner scanner = null;
    try {
      scanner = new Scanner(new File(fileName));
    } catch (FileNotFoundException e) {
      System.out.println("Error loading matrix file: " + e);
    }
    for (int i = 0; i < size; i++)
      for (int j = 0; j < size; j++) {
        this.m[i][j] = scanner.nextInt();
      }
  }

  public DenseMatrix(){

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
    if (o instanceof DenseMatrix) {
      DenseMatrix res = new DenseMatrix();
      for (int i = 0; i < size; i++)
        for (int j = 0; j < size; j++)
          for (int k = 0; k < size; k++) {
            res.m[i][j] = m[i][k] + ((DenseMatrix) o).m[k][j];
          }
      return res;
    }
    else {
      SparseMatrix res = new SparseMatrix();
      SparseMatrix oT = (SparseMatrix) ((SparseMatrix)o).transponeMatrix();
      for (int k = 0, i = 0; i < size; i++)
        for (int j = 0; j < size; j++) {
          int sum = 0;
          res.LI[i] = k;
          for (int s = 0; s < oT.LI[j+1]; s++) {
            sum += m[i][oT.LJ[oT.LI[j]+s]] * oT.A[oT.LJ[oT.LI[j]+s]];
          }
          if (sum != 0) {
            res.A[k] = sum;
            res.LJ[k] = j;
            res.LI[i + 1]++;
            k++;
          }
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

    for (int i = 0; i < size; i++)
      for (int j = 0; j < size; j++) {
        if (this.m[i][j] != ((DenseMatrix)o).m[i][j]){
          return false;
        }
      }
    return true;
  }

}

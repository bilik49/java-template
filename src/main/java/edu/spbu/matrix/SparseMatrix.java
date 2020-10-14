package edu.spbu.matrix;

import edu.spbu.MatrixGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Разряженная матрица
 */
public class SparseMatrix implements Matrix
{
  int size = MatrixGenerator.SIZE;
  // Массив ненулевых значений матрицы
  int[] A;
  // Массив индексов столбцов: хранит номера столбцов, соответствующих элементов из массива значений.
  int[] LJ;
  // Массив индексации строк: хранит местоположение (индекс в массиве А) первого  ненулевого  элемента  в  каждой  строке.
  int[] LI = new int[size+1];

  /**
   * загружает матрицу из файла
   * @param fileName
   */
  public SparseMatrix(String fileName) {
    int k = 0;
    Scanner scanner = null;
    try {
      scanner = new Scanner(new File(fileName));
    } catch (FileNotFoundException e) {
      System.out.println("Error loading matrix file: " + e);
    }
    for (int i = 0; i < size; i++) {
      LI[i+1] = k;
      for (int j = 0; j < size; j++) {
        if (scanner.nextInt() != 0) {
          A[k] = scanner.nextInt();
          LJ[k] = j;
          LI[i+1]++;
          k++;
        }
      }
    }
  }

  public SparseMatrix(){

  }


  public Matrix transponeMatrix(){
    SparseMatrix res = new SparseMatrix();
    int[][] rowIndexVectors = new int[size][];
    int[][] valueVectors = new int[size][];
    int[] indexInVectors = new int[size];

    for (int i = 0, k = 0; k < A.length; k++) {
      rowIndexVectors[LJ[k]][indexInVectors[LJ[k]]] = i;
      valueVectors[LJ[k]][indexInVectors[LJ[k]]] = A[i];
      if (LJ[k] >= LJ[k+1]){
        if (LI[i+1] != LI[i]) {
          i++;
        }
        else {
          i += 2;
        }
      }
      indexInVectors[LJ[k]]++;
    }

    // k - номер элемента
    for (int i = 0, k = 0; i < size; i++) {
      for(int j = 0; j < indexInVectors[i]; j++){
        res.A[k] = valueVectors[i][j];
        res.LJ[k] = rowIndexVectors[i][j];
        k++;
      }
      res.LI[k+1] = LI[k] + indexInVectors[i];
    }

    return res;
  }
  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */
  @Override public Matrix mul(Matrix o)
  {
    SparseMatrix res = new SparseMatrix();
    for (int i = 0; i < size; i++)
      for (int j = 0; j < size; j++) {

      }
    return null;
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
    return false;
  }
}

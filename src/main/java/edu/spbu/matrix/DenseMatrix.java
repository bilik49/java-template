package edu.spbu.matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.lang.Thread;


class MultiMulDD implements Runnable
{
  DenseMatrix A, B;
  volatile DenseMatrix res;
  int startI, startJ;
  int lastI, lastJ;


  public MultiMulDD(DenseMatrix A, DenseMatrix B, DenseMatrix res, int startI, int startJ, int lastI, int lastJ) {
    this.A = A;
    this.B = B;
    this.res = res;
    this.startI = startI;
    this.startJ = startJ;
    this.lastI = lastI;
    this.lastJ = lastJ;
  }

  @Override
  public void run()
  {
    for (int i = startI; i <= lastI; i++) {
      for (int j = startJ; j <= lastJ; j++) {
        int sum = 0;
        for (int k = 0; k < A.cols; k++) {
          sum += A.m[i][k] * B.m[k][j];
        }
        res.m[i][j] = sum;
      }
    }
  }
}
/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix
{
  int rows;
  int cols;
  int[][] m;

  /**
   * загружает матрицу из файла
   * @param fileName
   */
  public DenseMatrix(String fileName) throws Exception {
    BufferedReader reader = new BufferedReader(new FileReader((fileName)));
    int cols = reader.readLine().split(" ").length;
    int rows = 1;
    while (reader.readLine() != null) {
      rows++;
    }
    reader.close();

    int[][] m = new int[rows][cols];
    Scanner scanner = new Scanner(new FileReader(fileName));

    for (int i = 0; i < rows; i++)
      for (int j = 0; j < cols; j++) {
        if (scanner.hasNext()) {
          if (scanner.hasNextInt()) {
            m[i][j] = scanner.nextInt();
          }
        }
      }
    scanner.close();

    this.rows = rows;
    this.cols = cols;
    this.m = m;
  }

  public DenseMatrix(int rows, int cols){
    this.rows = rows;
    this.cols = cols;
    this.m = new int[rows][cols];
  }

  public SparseMatrix DenseToSparse() {
    SparseMatrix SM = new SparseMatrix(rows, cols);

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
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
      DenseMatrix res = new DenseMatrix(rows, ((DenseMatrix) o).cols);
      for (int i = 0; i < rows; i++)
        for (int j = 0; j < ((DenseMatrix) o).cols; j++) {
          int sum = 0;
          for (int k = 0; k < cols; k++) {
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
      SparseMatrix res = new SparseMatrix(rows, ((SparseMatrix) o).cols);
      SparseMatrix so = (SparseMatrix)o;
      so.transposeMatrix();

      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < so.rows; j++) {
          int sum = 0;
          // s идет по всем ненулевым элементам в транспонированной матрице в j-той строчке
          for (int s = 0; s < so.LI.get(j + 1) - so.LI.get(j); s++) {
            sum += m[i][so.LJ.get(so.LI.get(j) + s)] * so.A.get(so.LI.get(j) + s);
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
  public Matrix dmul(Matrix o)
  {
    DenseMatrix B = (DenseMatrix)o;
    int rowsR1 = this.rows % 2 == 0 ? this.rows/2 : this.rows/2 + 1;
    int colsR1 = B.cols % 2 == 0 ? B.cols/2 : B.cols/2 + 1;

    DenseMatrix res = new DenseMatrix(this.rows, B.cols);

    Thread[] threads = { new Thread(new MultiMulDD(this, B, res, 0,0, this.rows/2, B.cols/2)),
          new Thread(new MultiMulDD(this, B, res, 0, colsR1, this.rows/2, B.cols - 1)),
          new Thread(new MultiMulDD(this, B, res, rowsR1, 0, this.rows - 1, B.cols/2)),
          new Thread(new MultiMulDD(this, B, res, rowsR1, colsR1, this.rows - 1, B.cols - 1))
    };


    for (Thread thread: threads) {
      thread.start();
    }

    try {
      for (int i = 0; i < threads.length; i++) {
        threads[i].join();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return res;
  }

  /**
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
  @Override public boolean equals(Object o) {

    if (o instanceof DenseMatrix) {
      for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++) {
          if (m[i][j] != ((DenseMatrix)o).m[i][j]){
            return false;
          }
        }
    }
    else {
      for (int i = 0, k = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
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
    String s= "";
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
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

package edu.spbu.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MatrixTest
{
  /**
   * ожидается 4 таких теста
   */
  /*@Test
  public void mulDD() {
    Matrix m1 = new DenseMatrix("dm1.txt");
    Matrix m2 = new DenseMatrix("dm2.txt");
    Matrix expected = new DenseMatrix("resultDM1DM2.txt");
    assertEquals(expected, m1.mul(m2));
  }*/

  @Test
  public void testConstructorDenseMatrix() throws Exception {
    DenseMatrix expected = new DenseMatrix(5, 5);
    expected.m = new int[][] {{32, -16, 88, -62, -65},
                              {-30, -36, -61, -41, 8},
                              {65, 76, -55, 94, 10},
                              {-15, 66, -47, -10, 77},
                              {53, 55, -20, -5, 65} };
    Matrix actual = new DenseMatrix("dm1.txt");
    assertEquals(expected,actual);
  }

  @Test
  public void testEqualsDD() throws Exception {
    DenseMatrix m1 = new DenseMatrix("dm1.txt");
    DenseMatrix m2 = new DenseMatrix("dm1.txt");
    boolean actual1 = m1.equals(m2);

    m1 = new DenseMatrix("dm1.txt");
    m2 = new DenseMatrix("dm2.txt");
    boolean actual2 = m1.equals(m2);

    assertTrue(actual1 && !actual2);
  }

  @Test
  public void testDmulDmultiThreads() throws Exception {
//    DenseMatrix m1 = new DenseMatrix("dm1.txt");
//    DenseMatrix m2 = new DenseMatrix("dm2.txt");
//    DenseMatrix expected = new DenseMatrix("resultDM1DM2.txt");
//
//    DenseMatrix actual = (DenseMatrix) m1.dmul(m2);

//    new MatrixGenerator(3,1,"bigDM1", 2000).generate();
//    new MatrixGenerator(4,1,"bigDM2", 2000).generate();

    DenseMatrix m1 = new DenseMatrix("bigDM1");
    DenseMatrix m2 = new DenseMatrix("bigDM2");

    long start = System.currentTimeMillis();
    DenseMatrix expected = (DenseMatrix) m1.mul(m2);
    System.out.println("Single thread time: " +(System.currentTimeMillis() - start));

    start = System.currentTimeMillis();
    DenseMatrix actual = (DenseMatrix) m1.dmul(m2);
    System.out.println("Multithreading time: " +(System.currentTimeMillis() - start));
    assertEquals(expected, actual);
  }

  @Test
  public void testDmulD() throws Exception {
    Matrix m1 = new DenseMatrix("dm1.txt");
    Matrix m2 = new DenseMatrix("dm2.txt");
    Matrix expected = new DenseMatrix("resultDM1DM2.txt");

    Matrix actual = m1.mul(m2);

    assertEquals(expected, actual);
  }

  @Test
  public void testDmulS() throws Exception {
//    new MatrixGenerator(1, 1, "DM.txt", 5).generate();
//    new MatrixGenerator(2, 10, "SM.txt", 5).generate();
    Matrix m1 = new DenseMatrix("DM.txt");
    Matrix m2 = new DenseMatrix("SM.txt");
    Matrix expected = ((DenseMatrix)m1.mul(m2)).DenseToSparse();

//    System.out.println(expected.matrixToString());
//    System.out.println();
//    for (int i = 0; i <((SparseMatrix)expected).A.size(); i++) {
//      System.out.print(((SparseMatrix)expected).A.get(i) + " ");
//    }
//    System.out.println();
//    for (int i = 0; i <((SparseMatrix)expected).A.size(); i++) {
//      System.out.print(((SparseMatrix)expected).LJ.get(i) + " ");
//    }
//    System.out.println();
//    for (int i = 0; i <((SparseMatrix)expected).size + 1; i++) {
//      System.out.print(((SparseMatrix)expected).LI.get(i) + " ");
//    }
//    System.out.println();

    m2 = new SparseMatrix("SM.txt");
    Matrix actual = m1.mul(m2);

//    System.out.println(actual.matrixToString());
//    System.out.println();
//    for (int i = 0; i <((SparseMatrix)actual).A.size(); i++) {
//      System.out.print(((SparseMatrix)actual).A.get(i) + " ");
//    }
//    System.out.println();
//    for (int i = 0; i <((SparseMatrix)actual).A.size(); i++) {
//      System.out.print(((SparseMatrix)actual).LJ.get(i) + " ");
//    }
//    System.out.println();
//    for (int i = 0; i <((SparseMatrix)actual).size + 1; i++) {
//      System.out.print(((SparseMatrix)actual).LI.get(i) + " ");
//    }
    assertEquals(expected, actual);
  }

  @Test
  public void testSmulS() throws Exception {
    Matrix m1 = new DenseMatrix("SM.txt");
    Matrix m2 = new DenseMatrix("SM.txt");

    Matrix expected = m1.mul(m2);
    expected = ((DenseMatrix)expected).DenseToSparse();

//    System.out.println(expected.matrixToString());
//    System.out.println();
//    for (int i = 0; i <((SparseMatrix)expected).A.size(); i++) {
//      System.out.print(((SparseMatrix)expected).A.get(i) + " ");
//    }
//    System.out.println();
//    for (int i = 0; i <((SparseMatrix)expected).A.size(); i++) {
//      System.out.print(((SparseMatrix)expected).LJ.get(i) + " ");
//    }
//    System.out.println();
//    for (int i = 0; i <((SparseMatrix)expected).size + 1; i++) {
//      System.out.print(((SparseMatrix)expected).LI.get(i) + " ");
//    }
//    System.out.println();

    m1 = new SparseMatrix("SM.txt");
    m2 = new SparseMatrix("SM.txt");

    Matrix actual = m1.mul(m2);

    assertEquals(expected, actual);
  }

  @Test
  public void testSmulD() throws Exception {
    Matrix m1 = new DenseMatrix("SM.txt");
    Matrix m2 = new DenseMatrix("DM.txt");

    Matrix expected = m1.mul(m2);
    expected = ((DenseMatrix)expected).DenseToSparse();

    m1 = new SparseMatrix("SM.txt");

    Matrix actual = m1.mul(m2);

    assertEquals(expected, actual);
  }
}

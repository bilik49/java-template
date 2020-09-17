package edu.spbu.sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void sort(int array[]) {
    mergeSort(array, 0, array.length - 1);
  }

  public static void sort(List<Integer> list) {
    mergeSortList(list,0, list.size()-1);
  }


  public static void mergeSort(int[] array, int left, int right) {
    if (right <= left) return;
    int mid = (left + right) / 2;
    mergeSort(array, left, mid);
    mergeSort(array, mid + 1, right);
    merge(array, left, mid, right);
  }

  static void merge(int[] array, int left, int mid, int right) {
    // вычисляем длину
    int lengthLeft = mid - left + 1;
    int lengthRight = right - mid;

    // создаем временные подмассивы
    int leftArray[] = new int[lengthLeft];
    int rightArray[] = new int[lengthRight];

    // копируем отсортированные массивы во временные
    for (int i = 0; i < lengthLeft; i++)
      leftArray[i] = array[left + i];
    for (int i = 0; i < lengthRight; i++)
      rightArray[i] = array[mid + i + 1];

    // итераторы содержат текущий индекс временного подмассива
    int leftIndex = 0;
    int rightIndex = 0;

    // копируем из leftArray и rightArray обратно в массив
    for (int i = left; i < right + 1; i++) {
      // если остаются нескопированные элементы в R и L, копируем минимальный
      if (leftIndex < lengthLeft && rightIndex < lengthRight) {
        if (leftArray[leftIndex] < rightArray[rightIndex]) {
          array[i] = leftArray[leftIndex];
          leftIndex++;
        } else {
          array[i] = rightArray[rightIndex];
          rightIndex++;
        }
      }
      // если все элементы были скопированы из rightArray, скопировать остальные из leftArray
      else if (leftIndex < lengthLeft) {
        array[i] = leftArray[leftIndex];
        leftIndex++;
      }
      // если все элементы были скопированы из leftArray, скопировать остальные из rightArray
      else if (rightIndex < lengthRight) {
        array[i] = rightArray[rightIndex];
        rightIndex++;
      }
    }
  }

  public static void mergeSortList(List<Integer> list, int left, int right) {
    if (right <= left) return;
    int mid = (left + right) / 2;
    mergeSortList(list, left, mid);
    mergeSortList(list, mid + 1, right);
    mergeList(list, left, mid, right);
  }

  static void mergeList(List<Integer> list, int left, int mid, int right) {
    // вычисляем длину
    int lengthLeft = mid - left + 1;
    int lengthRight = right - mid;

    // создаем временные подмассивы
    List<Integer> leftList = new ArrayList<Integer>(lengthLeft);
    List<Integer> rightList = new ArrayList<Integer>(lengthRight);

    // копируем отсортированные массивы во временные
    for (int i = 0; i < lengthLeft; i++)
      leftList.add(list.get(left + i));
    for (int i = 0; i < lengthRight; i++)
      rightList.add(list.get(mid + i + 1));

    // итераторы содержат текущий индекс временного подмассива
    int leftIndex = 0;
    int rightIndex = 0;

    // копируем из leftArray и rightArray обратно в массив
    for (int i = left; i < right + 1; i++) {
      // если остаются нескопированные элементы в R и L, копируем минимальный
      if (leftIndex < lengthLeft && rightIndex < lengthRight) {
        if (leftList.get(leftIndex) < rightList.get(rightIndex)) {
          list.set(i, leftList.get(leftIndex));
          leftIndex++;
        } else {
          list.set(i, rightList.get(rightIndex));
          rightIndex++;
        }
      }
      // если все элементы были скопированы из rightArray, скопировать остальные из leftArray
      else if (leftIndex < lengthLeft) {
        list.set(i, leftList.get(leftIndex));
        leftIndex++;
      }
      // если все элементы были скопированы из leftArray, скопировать остальные из rightArray
      else if (rightIndex < lengthRight) {
        list.set(i, rightList.get(rightIndex));
        rightIndex++;
      }
    }
  }
}